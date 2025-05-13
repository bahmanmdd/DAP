package pgraph.anchorage;

import pgraph.anchorage.distributions.*;
import pgraph.anchorage.policy.HybridTAPV2;
import pgraph.anchorage.policy.TimedAnchorPolicy;
import pgraph.gui.GraphViewer;
import pgraph.util.Pen;
import pgraph.util.Polygon2D;
import pgraph.util.RandUtil;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TimedAnchorageManager {
    // Scenario selection: "Ahirkapi", "Busy", "Idle", or "Average"
    private static final String SCENARIO = "Ahirkapi"; // Alternatives: "Busy", "Idle", "Average"

    GraphViewer gv = null;
    private static final long REFRESH_INTERVAL = 1;
    TimedAnchorArea anchorArea;
    TimedArrivalGenerator arrivalGenerator;
    TimedAnchorPolicy anchorPolicy;
    long simulationClock;

    public final boolean ANIMATION = false;

    java.util.List<TimedArrivalInterface> arrivalList = new ArrayList<>();
    private double riskFactor = 0;

    private static final double ANCHORAGE_LENGTH;

    static {
        if (SCENARIO.equals("Ahirkapi")) {
            ANCHORAGE_LENGTH = 2500;
        } else {
            ANCHORAGE_LENGTH = 4000;
        }
    }

    public static double getAnchorageLength() {
        return ANCHORAGE_LENGTH;
    }

    public TimedAnchorageManager(TimedAnchorArea anchorArea, TimedArrivalGenerator arrivalGenerator, TimedAnchorPolicy anchorPolicy) {
        this.anchorArea = anchorArea;
        this.arrivalGenerator = arrivalGenerator;
        this.anchorPolicy = anchorPolicy;
    }

    public void showAnchorageanimation() throws InterruptedException, IOException {
        List<Anchorage> lastExistingAnchorages = null;

        System.out.println("Running...");

        if (ANIMATION) {
            gv = GraphViewer.showContent(anchorArea);
        }

        _updateClock(simulationClock);

        check: while (!anchorArea.simulationFinished()) {
            if (anchorArea.getRunCount() == 1 && simulationClock == 0) {
                // Skip initial population for Idle scenario
                if (!SCENARIO.equals("Idle")) {
                    rFill: while (true) {
                        TimedArrivalInterface nextArrival = arrivalGenerator.generate(simulationClock);
                        Anchorage anchorage = anchorPolicy.createRandomAnchorage(anchorArea, nextArrival);
                        if (anchorage == null) {
                            break rFill;
                        }
                        anchorage.setArrivalTime(0);
                        anchorage.setDepartureTime(nextArrival.getDepartureTime());
                        anchorArea.addExistingAnchorage(anchorage);
                    }

                    // For Ahirkapi and Average, we remove 70% of initially placed ships
                    if (SCENARIO.equals("Ahirkapi") || SCENARIO.equals("Average")) {
                        RandUtil rng = new RandUtil(LognormalTAG.seed);
                        int s = anchorArea.getExistingAnchorages().size();
                        for (int q = 0; q < (s * 0.7); q++) {
                            anchorArea.getExistingAnchorages().remove(rng.nextInt(s));
                            s--;
                        }
                    }
                }

                lastExistingAnchorages = anchorArea.getExistingAnchorages();
            }

            if (!anchorArea.bsteadyState) {
                if (anchorArea.steadyState()) {
                    anchorArea.steadyStateStatisticsReset();
                    anchorArea.bsteadyState = true;
                }
            }

            if (anchorArea.simulationTimeUp()) {
                anchorArea.updateFinalResults();
                if (anchorArea.getRunCount() >= anchorArea.getNoOfRuns()) {
                    if (anchorArea.spsa) {
                        anchorArea.writeObjectiveFunctionList();
                    }
                    break check;
                }
                anchorArea.simulationTimeUpReset();
                arrivalGenerator.reset();

                if (anchorArea.STATISTICS) {
                    anchorArea.prepareReportFile();
                }
                resetSimulationClock();

                if (anchorArea.getRunCount() % 100 == 0) {
                    System.out.println("Replication no:" + anchorArea.getRunCount());
                }

                // Handle subsequent replications based on the scenario
                if (SCENARIO.equals("Idle")) {
                    // Idle starts empty in all replications - do nothing
                } else if (SCENARIO.equals("Busy")) {
                    // Busy scenario: keep the same ships, just update departure times
                    anchorArea.setExistingAnchorages(lastExistingAnchorages);
                    RandUtil rng = new RandUtil(LognormalTAG.seed);
                    for (Anchorage a : anchorArea.getExistingAnchorages()) {
                        a.setArrivalTime(0);
                        a.setDepartureTime((long) (60 * LognormalTAG.DEP_ARR_RATIO * (Math.exp(rng.nextGaussian(LognormalTAG.Mean_DEP_TIME, LognormalTAG.S2_DEP_TIME)))));
                    }
                } else {
                    // Ahirkapi and Average scenarios: repopulate and then remove some
                    anchorArea.setExistingAnchorages(lastExistingAnchorages);
                    RandUtil rng = new RandUtil(LognormalTAG.seed);
                    for (Anchorage a : anchorArea.getExistingAnchorages()) {
                        a.setArrivalTime(0);
                        a.setDepartureTime((long) (60 * LognormalTAG.DEP_ARR_RATIO * (Math.exp(rng.nextGaussian(LognormalTAG.Mean_DEP_TIME, LognormalTAG.S2_DEP_TIME)))));
                    }

                    // Additional repopulation for Ahirkapi and Average
                    rFill: while (true) {
                        TimedArrivalInterface nextArrival = arrivalGenerator.generate(simulationClock);
                        Anchorage anchorage = anchorPolicy.createRandomAnchorage(anchorArea, nextArrival);
                        if (anchorage == null) {
                            break rFill;
                        }
                        anchorage.setArrivalTime(0);
                        anchorage.setDepartureTime(nextArrival.getDepartureTime());
                        anchorArea.addExistingAnchorage(anchorage);
                    }

                    rng.setSeed(LognormalTAG.seed);
                    int s = anchorArea.getExistingAnchorages().size();
                    int ss = anchorArea.getExistingAnchorages().size();
                    for (int q = 0; q < (s * 0.6); q++) {
                        anchorArea.getExistingAnchorages().remove(rng.nextInt(ss));
                        ss--;
                    }
                }
            }

            TimedArrivalInterface nextArrival = arrivalGenerator.generate(simulationClock);
            Anchorage departure = anchorArea.getNextDeparture();

            while (departure != null && departure.getDepartureTime() < nextArrival.getArrivalTime()) {
                _updateClock(departure.departureTime);
                if (ANIMATION) {
                    _performDepartureAnimated(departure);
                } else {
                    _performDepartureSilent(departure);
                }
                departure = anchorArea.getNextDeparture();
            }

            _updateClock(nextArrival.getArrivalTime());
            Anchorage anchorage = anchorPolicy.createAnchorage(anchorArea, nextArrival);

            if (anchorage == null) {
                anchorArea.setStatusText("TIME: " + simulationClock + "  NEW ARRIVAL REJECTED ", simulationClock);
                anchorArea.newArrival(nextArrival);
                waitandRefresh(REFRESH_INTERVAL);
                _updateClock(simulationClock);
                continue;
            }

            anchorage.setArrivalTime(nextArrival.getArrivalTime());
            anchorage.setDepartureTime(nextArrival.getDepartureTime());
            if (ANIMATION) {
                anchorage.setPen(new Pen(Color.red, Pen.PenStyle.PS_Pointed));
            }

            Point2D entryPoint = anchorArea.calculateEntryPoint(anchorage.area.center().getAsDouble());

            if (ANIMATION) {
                anchorArea.setStatusText("TIME: " + simulationClock + "  NEW ARRIVAL ACCEPTED ", simulationClock);
            }
            anchorArea.newArrival(nextArrival);

            if (ANIMATION) {
                waitandRefresh(REFRESH_INTERVAL);
            }

            anchorArea.repositionArrivals(entryPoint);

            if (ANIMATION) {
                waitandRefresh(REFRESH_INTERVAL);
            }

            anchorArea.addExistingAnchorage(anchorage);

            if (ANIMATION) {
                waitandRefresh(REFRESH_INTERVAL);
                anchorage.setPen(Pen.DefaultPen);
            }

            anchorArea.clearArrivals();
            anchorArea.setCurrentAnchoragePath(null);
        }

        System.out.println("\n\tSIMULATION IS OOOOOOOOOOOOOOOOOOOOOVER!");
        if (ANIMATION) {
            Thread.sleep(REFRESH_INTERVAL);
            gv.repaint();
        }
    }

    public void resetSimulationClock() {
        simulationClock = -1;
    }

    public long getSimulationClock() {
        return simulationClock;
    }

    private void _updateClock(long clock) {
        simulationClock = clock;
        anchorArea.setStatusText("TIME: " + simulationClock, simulationClock);
    }

    private void _performDepartureAnimated(Anchorage departure) throws InterruptedException {
        departure.setPen(new Pen(Color.red, Pen.PenStyle.PS_Pointed));
        anchorArea.setStatusText("TIME: " + simulationClock + "  DEPARTURE EVENT", simulationClock);
        waitandRefresh(REFRESH_INTERVAL);
        anchorArea.removeDepartingAnchorage(departure);
        waitandRefresh(REFRESH_INTERVAL);
        _updateClock(simulationClock);
    }

    private void _performDepartureSilent(Anchorage departure) {
        anchorArea.removeDepartingAnchorage(departure);
    }

    private void waitandRefresh(long refreshInterval) throws InterruptedException {
        Thread.sleep(refreshInterval);
        if (gv != null)
            gv.repaint();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        test1();
    }

    private static void test1() throws InterruptedException, IOException {
        TimedAnchorArea aa = new TimedAnchorArea();

        // Common parameters for all scenarios
        aa.setMaximumDepth(3.5);
        aa.setShowStatistics(true);
        aa.setStatsPlace(TimedAnchorArea.STATS_PLACE.SP_BOTTOM);
        aa.setRiskFactor(0);
        aa.setScale(1);

        // Configure LognormalTAG DEP_ARR_RATIO based on scenario
        if (SCENARIO.equals("Ahirkapi")) {
            // Set Ahirkapi-specific DEP_ARR_RATIO
            LognormalTAG.DEP_ARR_RATIO = 1.0;

            // Ahirkapi scenario - trapezoid area
            aa.setArea(new Polygon2D(new double[]{0, 4000, 4000, 0}, new double[]{0, 1500, 4000, 2500}, 4));
            aa.setEntrySide(new Line2D.Double(0, 2500, 4000, 4000));
            aa.setFirstEntryPoint(new Point2D.Double(2000, 3250));

            if (aa.STATISTICS) {
                aa.prepareReportFile();
            }
            aa.prepareFinalReportFile();

            if (aa.spsa) {
                aa.createQvectorSPSA();
            } else {
                aa.myTheta();
            }

            TimedAnchorageManager am = new TimedAnchorageManager(aa,
                    new LognormalTAG(new BetaArrivalGenerator()),
                    new HybridTAPV2(aa.getFirstEntryPoint().distance(2000, 750)));

            am.showAnchorageanimation();
        }
        else if (SCENARIO.equals("Busy")) {
            // Set Busy-specific DEP_ARR_RATIO
            LognormalTAG.DEP_ARR_RATIO = 2.2;

            // Busy scenario - rectangular area with standard initial population
            aa.setArea(new Polygon2D(new double[]{0, 2500, 2500, 0}, new double[]{0, 0, 4000, 4000}, 4));
            aa.setEntrySide(new Line2D.Double(0, 4000, 2500, 4000));
            aa.setFirstEntryPoint(new Point2D.Double(1250, 4000));

            if (aa.STATISTICS) {
                aa.prepareReportFile();
            }
            aa.prepareFinalReportFile();

            if (aa.spsa) {
                aa.createQvectorSPSA();
            } else {
                aa.myTheta();
            }

            TimedAnchorageManager am = new TimedAnchorageManager(aa,
                    new LognormalTAG(new BetaArrivalGenerator()),
                    new HybridTAPV2(aa.getFirstEntryPoint().distance(1250, 0)));

            am.showAnchorageanimation();
        }
        else if (SCENARIO.equals("Idle")) {
            // Set Idle-specific DEP_ARR_RATIO
            LognormalTAG.DEP_ARR_RATIO = 0.5;

            // Idle scenario - no initial population
            aa.setArea(new Polygon2D(new double[]{0, 2500, 2500, 0}, new double[]{0, 0, 4000, 4000}, 4));
            aa.setEntrySide(new Line2D.Double(0, 4000, 2500, 4000));
            aa.setFirstEntryPoint(new Point2D.Double(1250, 4000));

            if (aa.STATISTICS) {
                aa.prepareReportFile();
            }
            aa.prepareFinalReportFile();

            if (aa.spsa) {
                aa.createQvectorSPSA();
            } else {
                aa.myTheta();
            }

            TimedAnchorageManager am = new TimedAnchorageManager(aa,
                    new LognormalTAG(new BetaArrivalGenerator()),
                    new HybridTAPV2(aa.getFirstEntryPoint().distance(1250, 0)));

            am.showAnchorageanimation();
        }
        else if (SCENARIO.equals("Average")) {
            // Set Average-specific DEP_ARR_RATIO
            LognormalTAG.DEP_ARR_RATIO = 1.0;

            // Average scenario - same population strategy as Ahirkapi
            aa.setArea(new Polygon2D(new double[]{0, 2500, 2500, 0}, new double[]{0, 0, 4000, 4000}, 4));
            aa.setEntrySide(new Line2D.Double(0, 4000, 2500, 4000));
            aa.setFirstEntryPoint(new Point2D.Double(1250, 4000));

            if (aa.STATISTICS) {
                aa.prepareReportFile();
            }
            aa.prepareFinalReportFile();

            if (aa.spsa) {
                aa.createQvectorSPSA();
            } else {
                aa.myTheta();
            }

            TimedAnchorageManager am = new TimedAnchorageManager(aa,
                    new LognormalTAG(new BetaArrivalGenerator()),
                    new HybridTAPV2(aa.getFirstEntryPoint().distance(1250, 0)));

            am.showAnchorageanimation();
        }
    }
}