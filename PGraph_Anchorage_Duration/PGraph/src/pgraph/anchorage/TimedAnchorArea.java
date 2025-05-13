package pgraph.anchorage;

import pgraph.Path;
import pgraph.util.*;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import math.geom2d.conic.Circle2D;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraWithHeuristicShortestPath;
import org.jgrapht.traverse.MHDHeuristic;
import pgraph.CircleListObstacleGenerator;
import pgraph.ObstacleGenerator;
import pgraph.ObstacleInterface;
import pgraph.anchorage.util.ShipDynamics;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridVertex;
import pgraph.gui.BaseGraphPanel;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.geom.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class TimedAnchorArea implements BaseGraphPanel.Renderable
{
    String statusText = null;

    public final boolean spsa = true;

    public final int WORMUP_DAYS = 0;
    public final int RUN_DAYS = 1;
    public static final int RUN_PER_ITERATION = 2;
    public final int NO_OF_ITERATION   = 3;
    public final int NO_OF_RUNS = (spsa) ? RUN_PER_ITERATION * NO_OF_ITERATION * 3 :   RUN_PER_ITERATION ;


    public final double W1 = 5;         // AIL & DIL
    public final double W2 = 0;         // Dynamic Area Utilization
    public final double W3 = 1;         // AVG Total Distance Travelled

    public final double lambda = 0.000;

    public final double W  = 0;

    public final boolean STATISTICS = false;

    //public double[] qVector = new double[5];
    public List<double[]> qVectorList = new ArrayList<>(NO_OF_RUNS+RUN_PER_ITERATION);
    public List<double[]> thetaList = new ArrayList<>(NO_OF_ITERATION);
    public List<double[]> thetaZeroList = new ArrayList<>(NO_OF_RUNS);
    public List<int[]> deltaList = new ArrayList<>(NO_OF_RUNS);

    private int k = 1;

    public double[] oFList  = new double[NO_OF_RUNS];       // DO NOT REMOVE!!
    public double[] finalFList = new double[NO_OF_RUNS];    // DO NOT REMOVE!!

    public double[] akList = new double[NO_OF_RUNS];
    public double[] ckList = new double[NO_OF_RUNS];

    public double totalDistanceTraveled = 0.0;
    public double avgDistanceTraveled = 0.0;

    private static final double DEFAULT_MAX_DEPTH = 350; // maximum depth for Ahirkapi
    private static final int MAX_ADJUSTMENT_COUNT = 10;

    private int maxIntersection =-1;
    private double maximumDepth= DEFAULT_MAX_DEPTH;

    private double riskFactor=0;
    private boolean uniformAssumption=false;

    private static final long MAX_REJECT_COUNT= 999999999;
    private int rejectCount=0;
    private int consecutive_rejectCount=0;
    private int arrivalCount=0;
    private int departureCount =0;
    private long simulationClock=0;
    public String fileTime;
    public String finalFileTime;

    public boolean bsteadyState=false;
    public boolean bsimulationTimeUp=false;

    public int runCount=1;
    private double ststAvgAvgArrIntersectionLength=0;
    private double ststAvgAvgDepIntersectionLength=0;
    // private double ststAvgEffAreaUtil=0;
    private double ststAvgAreaUtil=0;
    private double ststAvgDinAreaUtil=0;

    private String type =  (spsa) ? "SPSA": "OPT";



    public int getRUN_DAYS(){return RUN_DAYS;}
    public int getNoOfRuns(){return NO_OF_RUNS;}
    public int getRunCount(){return runCount;}

    public List<double[]> getqVectorList(){return qVectorList;}
    public double[] getCurrentQvector(){
        return qVectorList.get(runCount-1);
    }

    double anchorageLength = TimedAnchorageManager.getAnchorageLength();

    public void prepareReportFile() throws IOException {
        fileTime = new SimpleDateFormat("_yyyyMMdd_hhmmss").format(new Date());
        String current = System.getProperty("user.dir");

        // Create results directory if it doesn't exist
        File resultsDir = new File(current + File.separator + "results");
        if (!resultsDir.exists()) {
            resultsDir.mkdirs();
        }

        // Create the file using File.separator instead of hardcoded "\\"
        File file = new File(resultsDir, "RunNO_" + runCount + "_CreatTime" + fileTime + ".csv");

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Simulation_Time, "+ "Anchorage_Count,"+ "Safety,"+ "Distance,"+ "Utilization," +
//            "Area_Utilization, "+ "Eff_Area_Utilization, "+ "Total_AIL,"+ "Avg_AIL,"+ "Total_DIL,"+ "Avg_DIL " +
                "\n");
        bw.close();
    }


    public String summaryFileTime;

    public void prepareFinalReportFile() throws IOException {
        finalFileTime = new SimpleDateFormat("_yyyyMMdd_hhmmss").format(new Date());
        summaryFileTime = finalFileTime; // Add this line to initialize summaryFileTime
        String current = System.getProperty("user.dir");

        // Create results directory if it doesn't exist
        File resultsDir = new File(current + File.separator + "results");
        if (!resultsDir.exists()) {
            resultsDir.mkdirs();
        }

        // Create the file using File.separator
        File file = new File(resultsDir, type+"_runs_"+W1+"-"+W2+"-"+W3+ "_"+"L"+lambda+finalFileTime+".csv");

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Run_NO, "+ "Avg_AIL, "+ "Avg_DIL, "+ "Avg_AilDil, "+ "Avg_Dist_Traveled, "+ "Dyn_Area_Utilization, "+ "Area_Utilization, "+ "Objective_F, " + "Theta_Vector, " + " \n");
        bw.close();
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText, long simulationClock) {
        this.simulationClock = simulationClock;
        this.statusText = statusText;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    private double scale= 1;

    public Point2D getFirstEntryPoint() {
        return firstEntryPoint;
    }

    public void setFirstEntryPoint(Point2D firstEntryPoint) {
        this.firstEntryPoint = firstEntryPoint;
    }

    private Point2D firstEntryPoint;

    public double getRiskFactor() {
        return riskFactor;
    }

    public void setRiskFactor(double riskFactor) {
        this.riskFactor = riskFactor;
    }


    public double getMaximumDepth() {
        return maximumDepth;
    }

    public void setMaximumDepth(double maximumDepth) {
        this.maximumDepth = maximumDepth;
    }

    public Anchorage getNextDeparture() {

        long departureTime = Long.MAX_VALUE;
        Anchorage departure = null;

        for (Anchorage a: existingAnchorages)
        {
            if (departureTime>a.getDepartureTime())
            {
                departure = a;
                departureTime = a.departureTime;
            }
        }
        return departure;
    }

    public void removeDepartingAnchorage(Anchorage departure)
    {
        //updateDepartureIntersectionCount(departure);
        departureCount++;
        calculateDepIntersectionLength(departure);
        existingAnchorages.remove(departure);
        areaUtilization  = calculateAreaUtilization() ;
        //effAreaUtilization = calculateEffectiveAreaUtilization();
        //totalEffAreaUtilization += effAreaUtilization;
        totalAreaUtilization += areaUtilization;
        ststAvgAvgDepIntersectionLength += AvgDepIntersectionLength;
        // ststAvgEffAreaUtil += effAreaUtilization;
        ststAvgAreaUtil += areaUtilization;
        ststAvgDinAreaUtil -= departure.calculateArea()* (departure.getDepartureTime() - departure.getArrivalTime()) ;
        if (STATISTICS){updateResults();}
    }


    public enum STATS_PLACE { SP_RIGHT, SP_BOTTOM };

    public static final double COLLISION_TRESHOLD = 0.001;
    private static final double DZ_INCLUSION_THRESHOLD = 1;

    private static final int MODEL_GRAPH_DEGREE = 1;
    private int MODEL_GRAPH_UNIT_EDGE_LEN=20;


    STATS_PLACE statsPlace = STATS_PLACE.SP_BOTTOM;

    Polygon2D area;
    List<Anchorage> existingAnchorages= new ArrayList<Anchorage>();
    List<Anchorage> candidateAnchorages = new ArrayList<Anchorage>();

    List<Anchorage> arrivals = new ArrayList<Anchorage>();


    List<DepthZone> depthZones= new ArrayList<DepthZone>();

    Line2D entrySide;

    public double totalArrivalIntersectionLength=0;
    public double AvgArrIntersectionLength= arrivalCount   ==0           ?  0 :  totalArrivalIntersectionLength/arrivalCount;
    public double totalDepartureIntersectionLength=0;
    public double AvgDepIntersectionLength= departureCount ==0           ?  0 :  totalDepartureIntersectionLength/departureCount;

    private double minDistanceToEntry = -1;
    private double areaUtilization=0;
    //private double effAreaUtilization=0;
    private double totalArea = 0;

    private GridDirectedGraph modelGraph = null;
    private double totalAnchoragePathLength = 0;
    private int totalAnchorageIntersection=0;
    private double totalDepartureIntersection=0;
    private double totalMinDistanceToEntry= 0;
    private double totalAreaUtilization=0;
    //private double totalEffAreaUtilization=0;




    private String outputFile=null;

    private int departureIntersectionMeasurePeriod = 1;

    public int getMaxIntersection() {
        return maxIntersection;
    }

    public void setMaxIntersection(int maxIntersection) {
        this.maxIntersection = maxIntersection;
    }


    public STATS_PLACE getStatsPlace() {
        return statsPlace;
    }

    public void setStatsPlace(STATS_PLACE statsPlace) {
        this.statsPlace = statsPlace;
    }

    public boolean isShowStatistics() {
        return showStatistics;
    }

    public void setShowStatistics(boolean showStatistics) {
        this.showStatistics = showStatistics;
    }

    private boolean showStatistics=false;


    public void addDepthZone(DepthZone dz)
    {
        depthZones.add(dz);
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public boolean isAreaFull() {
        return areaFull;
    }

    public void setAreaFull(boolean areaFull) {
        this.areaFull = areaFull;
    }

    private boolean areaFull = false;

    private void _createModelGraph( Rectangle2D boundingRect ) {

        double minX = boundingRect.getMinX();
        double maxX = boundingRect.getMaxX();
        double minY = boundingRect.getMinY();
        double maxY = boundingRect.getMaxY();

        double unitEdgeLength = MODEL_GRAPH_UNIT_EDGE_LEN*scale;

        modelGraph=  new GridDirectedGraph(MODEL_GRAPH_DEGREE,(int)(maxX-minX)/(int)unitEdgeLength+1,(int)(maxY-minY)/MODEL_GRAPH_UNIT_EDGE_LEN+1,MODEL_GRAPH_UNIT_EDGE_LEN,new Point2D.Double(minX,minY),0,0,0,0 );

    }


    public boolean isCollectStatistics() {
        return collectStatistics;
    }

    public void setCollectStatistics(boolean collectStatistics) {
        this.collectStatistics = collectStatistics;
    }

    private boolean collectStatistics = true;


    public Path getCurrentAnchoragePath() {
        return currentAnchoragePath;
    }

    public void setCurrentAnchoragePath(Path currentAnchoragePath) {
        this.currentAnchoragePath = currentAnchoragePath;
    }

    Path currentAnchoragePath = null;

    public void newArrival(ArrivalInterface arrival)
    {
        double innerRadius = arrival.getLength();
        double outerRadius = ShipDynamics.getOuterRadius(arrival.getLength(), maximumDepth, scale);
        double radius = ShipDynamics.getOuterRadius(arrival.getLength(),maximumDepth,scale,riskFactor);
        arrivals.add(new Anchorage(new Circle2D(firstEntryPoint.getX(),firstEntryPoint.getY(),radius),outerRadius,innerRadius, Pen.DefaultPen));
    }
    public void repositionArrivals(Point2D entryPoint)
    {
        for (Anchorage a: arrivals)
        {
            a.setArea( new Circle2D(entryPoint.getX(),entryPoint.getY(),a.getArea().radius()));
        }
    }

    public void clearArrivals()
    {
        arrivals.clear();
    }



    public Line2D getEntrySide() {
        return entrySide;
    }

    public void setEntrySide(Line2D entrySide) {
        this.entrySide = entrySide;
    }



    public List<Anchorage> getCandidateAnchorages() {
        return candidateAnchorages;
    }

    public void setCandidateAnchorages(List<Anchorage> candidateAnchorages) {
        this.candidateAnchorages = candidateAnchorages;
    }


    public Polygon2D getArea() {
        return area;
    }

    public void setArea(Polygon2D area) {
        this.area = area;
        if (collectStatistics)
        {
            _createModelGraph(area.getBounds2D());
            totalArea = area.calculateArea();
        }
    }

    public List<Anchorage> getExistingAnchorages() {
        return existingAnchorages;
    }

    public List<Circle2D> getExistingAnchoragesAsCircles()
    {
        List<Circle2D> cl = new ArrayList<Circle2D>();
        for (Anchorage a: existingAnchorages)
        {
            cl.add(a.getArea());
        }
        return cl;
    }

    public void setExistingAnchorages(List<Anchorage> existingAnchorages) {
        this.existingAnchorages = existingAnchorages;
    }

    public void setExistingAnchoragesFromCircles(List<Circle2D> existingAnchorages) {
        this.existingAnchorages.clear();

        for (Circle2D c: existingAnchorages)
        {
            this.existingAnchorages.add(new Anchorage(c));
        }
    }

    @Override
    public void draw(Graphics2D g, BaseGraphPanel.ViewTransform transform)
    {
        Color orjColor = g.getColor();
        //       Stroke orjStroke = g.getStroke();
        AffineTransform orjTransform =  g.getTransform();

        if (area != null)
        {
            if (areaFull)
                g.setColor(Color.red);
            g.draw(transform.createTransformedShape(area));
            g.setColor(orjColor);
        }

        List<Anchorage> aList = existingAnchorages;

        for (Anchorage a: aList)
        {
            a.draw(g,transform);
        }

        aList = candidateAnchorages;
        for (Anchorage a: aList)
        {
            a.draw(g, transform);
        }

        aList= arrivals;
        for (Anchorage a: aList)
        {
            a.draw(g, transform);
        }

        if (currentAnchoragePath != null)
            currentAnchoragePath.draw(g,transform);


        for (DepthZone dz:depthZones)
            dz.draw(g,transform);


        g.setColor(Color.white);

        g.draw(transform.createTransformedShape(entrySide));


        drawStatistics(g, transform);

        g.setColor(orjColor);
        g.setTransform(orjTransform);



    }


    private void drawStatistics(Graphics2D g, BaseGraphPanel.ViewTransform transform) {

        float bX,bY;

        Rectangle2D bounds = area.getBounds2D();
        if (statsPlace== STATS_PLACE.SP_BOTTOM)
        {
            bX = (float) area.getBounds2D().getMinX();
            bY = (float) area.getBounds2D().getMinY();
        }
        else
        {
            bX = (float) area.getBounds2D().getMaxX()+200;
            bY = (float) ( 0- area.getBounds2D().getMaxY());
        }

        AffineTransform t = null;
        try {
            t = transform.createInverse().createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        g.setColor(Color.black);
        g.setFont(new Font("Rockwell",Font.BOLD,80));

        if (t != null)
        {
            t.scale(1,-1);
            g.setTransform(t);
        }


        if (statusText != null && !statusText.isEmpty())
        {
            g.drawString(statusText, bX, bY + 200 );
            bY = bY + 10;
        }
        if (!collectStatistics || !showStatistics)
            return;

        g.setFont(new Font("Arial",0,80));

        NumberFormat nf = NumberFormat.getInstance();

        nf.setMaximumFractionDigits(4);
        nf.setMinimumFractionDigits(4);


        g.drawString("Anchorage_Count     : " + existingAnchorages.size(), bX,bY+300);
        g.drawString("Area_Utilization    : " + nf.format(areaUtilization),bX,bY+400);
        g.drawString("Total_AIL  : " + nf.format(totalArrivalIntersectionLength),bX,bY+500);
        g.drawString("Avg_AIL  : " + nf.format(AvgArrIntersectionLength),bX,bY+600);
        g.drawString("Total_DIL  : " + nf.format(totalDepartureIntersectionLength),bX,bY+700);
        g.drawString("Avg_DIL  : " + nf.format(AvgDepIntersectionLength),bX,bY+800);
        g.drawString("Replication No:  : " + runCount,bX,bY+900);


    }

    @Override
    public Rectangle2D boundingRect() {
        return area.getBounds2D();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }


    private void updateResults(){

        if (simulationClock!=0 && steadyState()){

            double safety = (arrivalCount==0 || departureCount==0) ? 0: ((ststAvgAvgArrIntersectionLength/arrivalCount)+(ststAvgAvgDepIntersectionLength/departureCount))/2;
            try {
                String current = System.getProperty("user.dir");

                // Create results directory if it doesn't exist
                File resultsDir = new File(current + File.separator + "results");
                if (!resultsDir.exists()) {
                    resultsDir.mkdirs();
                }

                // Create the file using File.separator
                File file = new File(resultsDir, "RunNO_" + runCount + "_CreatTime" + fileTime + ".csv");

                // Important fix: Use the full file path, not just the filename
                FileWriter fileWritter = new FileWriter(file, true);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);


                bufferWritter.write(
                        simulationClock +","+
                                getExistingAnchorages().size() +","+
                                safety  + ","+
                                avgDistanceTraveled  + ","+
                                ststAvgDinAreaUtil/(totalArea* RUN_DAYS*1440) +","+
                                "\n");
                bufferWritter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void alarmSteadystate(){

        try {
            String current = System.getProperty("user.dir");

            // Create results directory if it doesn't exist
            File resultsDir = new File(current + File.separator + "results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }

            // Create the file using File.separator
            File file = new File(resultsDir, "RunNO_" + runCount + "_CreatTime" + fileTime + ".csv");

            // Important fix: Use the full file path, not just the filename
            FileWriter fileWritter = new FileWriter(file, true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(
                    "\n" +",,,Steady State Started,,,"+ "\n\n");
            bufferWritter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static final byte  AT_SIDE_SIDE     = 1;
    public static final byte  AT_SIDE_CIRCLE   = 2;
    public static final byte  AT_CIRCLE_CIRCLE = 4;
    public static final byte  AT_SIDE_DZSIDE   = 8;
    public static final byte  AT_CIRCLE_DZSIDE = 16;
    public static final byte  AT_DZSIDE_DZSIDE = 32;


    public List<Anchorage> calculateCandidateAnchorages(double length, int anchorageType)
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();
        uniformAssumption = false;

        double innerRadius = length;
        double outerRadius = ShipDynamics.getOuterRadius(length,maximumDepth,scale);
        double radius = ShipDynamics.getOuterRadius(length,maximumDepth,scale,riskFactor);

        if ( ( anchorageType & AT_SIDE_SIDE ) != 0)
            caList.addAll(getSideAndSideAnchorages(radius, length));

        if ( ( anchorageType & AT_SIDE_CIRCLE ) != 0)
            caList.addAll(getSideAndCircleAnchorages(radius, length));

        if ( ( anchorageType & AT_CIRCLE_CIRCLE ) != 0)
            caList.addAll(getCircleAndCircleAnchorages(radius, length));

        if ( ( anchorageType & AT_SIDE_DZSIDE ) != 0)
            caList.addAll(getSideAndDZSideAnchorages(radius, length));

        if ( ( anchorageType & AT_CIRCLE_DZSIDE ) != 0)
            caList.addAll(getCircleAndDZSideAnchorages(radius, length));

        if ( ( anchorageType & AT_DZSIDE_DZSIDE ) != 0)
            caList.addAll(getDZSideAndDZSideAnchorages(radius, length));


        return caList;
    }

    public List<Anchorage> calculateCandidateAnchorages_UniformAssumption(double length, int anchorageType,double maxDepth)    // Before  Non-uniformity updates
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();
        List<Point2D> cpList = new ArrayList<Point2D>();
        //uniformAssumption = true;

        double innerRadius = length;
        double outerRadius = ShipDynamics.getOuterRadius(length,maxDepth,scale);
        double radius = ShipDynamics.getOuterRadius(length,maxDepth,scale,riskFactor);

        if ( ( anchorageType & AT_SIDE_SIDE ) != 0)
            cpList.addAll(getSideAndSideCornerPoints(radius,length));

        if ( ( anchorageType & AT_SIDE_CIRCLE ) != 0)
            cpList.addAll(getSideAndCircleCornerPoints(radius,length));

        if ( ( anchorageType & AT_CIRCLE_CIRCLE ) != 0)
            cpList.addAll(getCircleAndCircleCornerPoints(radius,length));

        if ( ( anchorageType & AT_SIDE_DZSIDE ) != 0)
            cpList.addAll(getSideAndDZSideCornerPoints(radius,length));

        if ( ( anchorageType & AT_CIRCLE_DZSIDE ) != 0)
            cpList.addAll(getCircleAndDZSideCornerPoints(radius,length));

        if ( ( anchorageType & AT_DZSIDE_DZSIDE ) != 0)
            cpList.addAll(getDZSideAndDZSideCornerPoints(radius,length));

        for (Point2D p : cpList)
        {
            caList.add(new Anchorage(new Circle2D(p.getX(),p.getY(),radius),outerRadius,innerRadius));
        }

        return caList;
    }

    public List<Anchorage> getAllCandidateAnchorages_UniformAssumption(double length,double maxdepth)
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();

        if (ShipDynamics.getSafeLength(maxdepth) >= length)
        {
            if (existingAnchorages.size() <= 1)
            {
                caList.addAll(calculateCandidateAnchorages_UniformAssumption(length, AT_SIDE_SIDE,maxdepth));
                if( caList.isEmpty())
                    caList.addAll(calculateCandidateAnchorages_UniformAssumption(length, AT_SIDE_CIRCLE,maxdepth));
                if ( caList.isEmpty())
                    caList.addAll(calculateCandidateAnchorages_UniformAssumption(length,  AT_CIRCLE_CIRCLE | AT_SIDE_DZSIDE,maxdepth));

            }  else
            {
                caList.addAll(calculateCandidateAnchorages_UniformAssumption(length, AT_SIDE_SIDE | AT_SIDE_CIRCLE | AT_CIRCLE_CIRCLE | AT_SIDE_DZSIDE,maxdepth));
            }
        }

        if (caList.isEmpty())
        {
            rejectCount++;
            consecutive_rejectCount++;
            if (consecutive_rejectCount<MAX_REJECT_COUNT)
                caList.add(new Anchorage(null));
            return caList;
        }
        consecutive_rejectCount=0;
        return caList;
    }

    public List<Anchorage> getAllCandidateAnchorages(double length)
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();

        if (existingAnchorages.size() <= 1)
        {
            caList.addAll(calculateCandidateAnchorages(length, AT_SIDE_SIDE));
            if( caList.isEmpty())
                caList.addAll(calculateCandidateAnchorages(length, AT_SIDE_CIRCLE));
            if ( caList.isEmpty())
                caList.addAll(calculateCandidateAnchorages(length,  AT_CIRCLE_CIRCLE | AT_SIDE_DZSIDE));

        }  else
        {
            caList.addAll(calculateCandidateAnchorages(length, AT_SIDE_SIDE | AT_SIDE_CIRCLE | AT_CIRCLE_CIRCLE | AT_SIDE_DZSIDE));
        }

        return caList;
    }

    public List<Anchorage> getOtherCandidateAnchorages_UniformAssumption(double length,int priority,double maxDepth)
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();


        switch (priority)
        {
            case 1: caList.addAll(calculateCandidateAnchorages_UniformAssumption(length, AT_CIRCLE_DZSIDE,maxDepth));break;
            case 2: caList.addAll(calculateCandidateAnchorages_UniformAssumption(length, AT_DZSIDE_DZSIDE,maxDepth));break;
        }

        return caList;
    }

    public List<Anchorage> getOtherCandidateAnchorages(double length,int priority)
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();


        switch (priority)
        {
            case 1: caList.addAll(calculateCandidateAnchorages(length, AT_CIRCLE_DZSIDE));break;
            case 2: caList.addAll(calculateCandidateAnchorages(length, AT_DZSIDE_DZSIDE));break;
        }

        return caList;
    }

    private Collection<? extends Point2D> getCircleAndCircleCornerPoints( double radius , double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        for (int i = 0; i< existingAnchorages.size()-1;i++ )
        {
            Circle2D ci = existingAnchorages.get(i).getArea();
            for (int j = i+1; j < existingAnchorages.size();j++)
            {
                Circle2D cj = existingAnchorages.get(j).getArea();
                cpList.addAll(getCircleAndCircleCornerPoints(ci,cj,radius,length));
            }
        }

        return cpList;
    }

    private Collection<? extends Anchorage> getCircleAndCircleAnchorages( double radius , double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        for (int i = 0; i< existingAnchorages.size()-1;i++ )
        {
            Circle2D ci = existingAnchorages.get(i).getArea();
            for (int j = i+1; j < existingAnchorages.size();j++)
            {
                Circle2D cj = existingAnchorages.get(j).getArea();
                aList.addAll(getCircleAndCircleAnchorages(ci, cj, radius, length));
            }
        }

        return aList;
    }

    private Collection<? extends Anchorage> getCircleAndCircleAnchorages(Circle2D c1, Circle2D c2, double radius,double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(c1,c2,radius) ;
        List<Double> calculatedDepths = new ArrayList<>();

        for (Circle2D ci:cList)
        {
            boolean adjusted = false;

            double maxDepth = 0;
            double depth = maximumDepth;
            int adjustmentCount =0;
            double previousDepth = maximumDepth;
            calculatedDepths.clear();

            while (!adjusted &&  (prevalidityCheck(ci, length)))
            {
                DepthZone deepest =  deepestZone(ci);
                depth = (deepest == null) ? maximumDepth: deepest.getDepth();

                if (depth == previousDepth)    // Converged!!!
                    adjusted = true;
                else if (calculatedDepths.contains(depth)) // Previously calculated depth
                {
                    depth = maxDepth;
                    adjusted = true;
                }
                else                            // Adjustment required
                {
                    double newRadius = ShipDynamics.getOuterRadius(length,depth,scale,riskFactor);
                    ci = GeomUtil.getTangentCircleAdjusted(c1,c2,newRadius,ci);
                    calculatedDepths.add(depth);
                    previousDepth = depth;
                    if (depth>maxDepth)
                        maxDepth = depth;
                    adjustmentCount++;
                }
                if (adjustmentCount>MAX_ADJUSTMENT_COUNT)
                {
                    throw new RuntimeException("Too many adjustment..");
                }
            }
            if (adjusted&& validAnchorage(ci,length))
            {
                double innerRadius = length;
                double outerRadius = ShipDynamics.getOuterRadius(length,depth,scale);
                Anchorage a = new Anchorage(ci,outerRadius,innerRadius,c1,c2);
                aList.add(a);
            }
        }

        return aList;
    }

    private Collection<? extends Point2D> getCircleAndCircleCornerPoints(Circle2D c1, Circle2D c2, double radius, double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(new Circle2D(c1.center(), c1.radius()), new Circle2D(c2.center(), c2.radius()), radius) ;

        for (Circle2D ci:cList)
        {
            if (validAnchorage(ci, length))
                cpList.add(ci.center().getAsDouble());
        }

        return cpList;
    }

    private Collection<? extends Point2D> getSideAndCircleCornerPoints( double radius,double length) {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<? extends Line2D> sides = getAreaSides();

        for (Line2D l : sides )
        {
            for (Anchorage a: existingAnchorages)
            {
                cpList.addAll(getSideAndCircleCornerPoints(l,a.getArea(),radius,length));
            }
        }


        return cpList;

    }

    private Collection<? extends Anchorage> getSideAndCircleAnchorages( double radius,double length) {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<? extends Line2D> sides = getAreaSides();

        for (Line2D l : sides )
        {
            for (Anchorage a: existingAnchorages)
            {
                aList.addAll(getSideAndCircleAnchorages(l, a.getArea(), radius, length));
            }
        }


        return aList;

    }

    private Collection<? extends Anchorage> getSideAndCircleAnchorages(Line2D l, Circle2D c, double radius,double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(c,l,radius) ;
        List<Double> calculatedDepths = new ArrayList<>();

        for (Circle2D ci:cList)
        {
            boolean adjusted = false;

            double maxDepth = 0;
            double depth = maximumDepth;
            int adjustmentCount =0;
            double previousDepth = maximumDepth;
            calculatedDepths.clear();

            while (!adjusted &&  (prevalidityCheck(ci, length)))
            {
                DepthZone deepest =  deepestZone(ci);
                depth = (deepest == null) ? maximumDepth: deepest.getDepth();

                if (depth == previousDepth)    // Converged!!!
                    adjusted = true;
                else if (calculatedDepths.contains(depth)) // Previously calculated depth
                {
                    depth = maxDepth;
                    adjusted = true;
                }
                else                            // Adjustment required
                {
                    double newRadius = ShipDynamics.getOuterRadius(length,depth,scale,riskFactor);
                    ci = GeomUtil.getTangentCircleAdjusted(c,l,newRadius,ci);
                    calculatedDepths.add(depth);
                    previousDepth = depth;
                    if (depth>maxDepth)
                        maxDepth = depth;
                    adjustmentCount++;
                }
                if (adjustmentCount>MAX_ADJUSTMENT_COUNT)
                {
                    throw new RuntimeException("Too many adjustment..");
                }
            }
            if (adjusted && validAnchorage(ci,length))
            {
                double innerRadius = length;
                double outerRadius = ShipDynamics.getOuterRadius(length,depth,scale);
                Anchorage a = new Anchorage(ci,outerRadius,innerRadius,c,l);
                aList.add(a);
            }
        }

        return aList;
    }

    private Collection<? extends Point2D> getSideAndCircleCornerPoints(Line2D l, Circle2D ce, double radius,double length) {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(ce, l, radius) ;

        for (Circle2D ci:cList)
        {
            if (validAnchorage(ci,length))
                cpList.add(ci.center().getAsDouble());
        }

        return cpList;
    }

    private Collection<? extends Point2D> getSideAndSideCornerPoints(double radius, double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<? extends Line2D> sides = getAreaSides();
        int size = sides.size();
        for (int i=0; i<size-1;i++ )
        {
            Line2D l1 = sides.get(i);

            for (int j=i+1; j<size;j++)
            {
                Line2D l2 = sides.get(j);
                cpList.addAll(getSideAndSideCornerPoints(l1, l2, radius,length));
            }
        }


        return cpList;

    }

    private Collection<? extends Anchorage> getSideAndSideAnchorages(double radius, double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<? extends Line2D> sides = getAreaSides();
        int size = sides.size();
        for (int i=0; i<size-1;i++ )
        {
            Line2D l1 = sides.get(i);

            for (int j=i+1; j<size;j++)
            {
                Line2D l2 = sides.get(j);
                aList.addAll(getSideAndSideAnchorages(l1, l2, radius, length));
            }
        }


        return aList;

    }

    private Collection<? extends Point2D> getSideAndDZSideCornerPoints(double radius,double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<? extends Line2D> sides = getAreaSides();

        List<Line2D> dzSides = getDZSides();
        for (Line2D side:sides)
        {
            for (Line2D dzSide:dzSides)
            {
                cpList.addAll(getSideAndSideCornerPoints(side, dzSide, radius,length));
            }
        }

        return cpList;

    }

    private Collection<? extends Anchorage> getSideAndDZSideAnchorages(double radius,double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<? extends Line2D> sides = getAreaSides();

        List<Line2D> dzSides = getDZSides();
        for (Line2D side:sides)
        {
            for (Line2D dzSide:dzSides)
            {
                aList.addAll(getSideAndSideAnchorages(side, dzSide, radius, length));
            }
        }

        return aList;

    }

    private Collection<? extends Anchorage> getCircleAndDZSideAnchorages(double radius,double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<Line2D> dzSides = getDZSides();

        for (Line2D l : dzSides )
        {
            for (Anchorage a: existingAnchorages)
            {
                aList.addAll(getSideAndCircleAnchorages(l, a.getArea(), radius, length));
            }
        }

        return aList;
    }

    private Collection<? extends Point2D> getCircleAndDZSideCornerPoints(double radius,double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Line2D> dzSides = getDZSides();

        for (Line2D l : dzSides )
        {
            for (Anchorage a: existingAnchorages)
            {
                cpList.addAll(getSideAndCircleCornerPoints(l,a.getArea(),radius,length));
            }
        }

        return cpList;
    }

    private Collection<? extends Anchorage> getDZSideAndDZSideAnchorages(double radius, double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<? extends Line2D> dzSides = getDZSides();
        int size = dzSides.size();
        for (int i=0; i<size-1;i++ )
        {
            Line2D l1 = dzSides.get(i);

            for (int j=i+1; j<size;j++)
            {
                Line2D l2 = dzSides.get(j);
                aList.addAll(getSideAndSideAnchorages(l1, l2, radius, length));
            }
        }

        return aList;
    }

    private Collection<? extends Point2D> getDZSideAndDZSideCornerPoints(double radius, double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<? extends Line2D> dzSides = getDZSides();
        int size = dzSides.size();
        for (int i=0; i<size-1;i++ )
        {
            Line2D l1 = dzSides.get(i);

            for (int j=i+1; j<size;j++)
            {
                Line2D l2 = dzSides.get(j);
                cpList.addAll(getSideAndSideCornerPoints(l1, l2, radius,length));
            }
        }

        return cpList;
    }

    public List<? extends Line2D> getAreaSides()
    {
        List<? extends Line2D> sides = area.getSides();

        if (entrySide == null)
            return sides;

        Line2D openLine = null;
        for ( Line2D l:sides)
        {
            if ( ( l.getP1().distance(entrySide.getP1())<COLLISION_TRESHOLD && l.getP2().distance(entrySide.getP2())<COLLISION_TRESHOLD ) ||
                    ( l.getP1().distance(entrySide.getP2())<COLLISION_TRESHOLD && l.getP2().distance(entrySide.getP1())<COLLISION_TRESHOLD ) )
            {
                openLine = l;
            }
        }

        sides.remove(openLine);
        return sides;
    }

    public List<Line2D> getDZSides()
    {
        List< Line2D> sides = new ArrayList< Line2D>();
        for (DepthZone dz: depthZones)
        {
            List<? extends Line2D> dzSides = dz.getArea().getSides();
            for(Line2D l:dzSides)
            {
                if ( entrySide.ptSegDist(l.getP1())<COLLISION_TRESHOLD && entrySide.ptSegDist(l.getP2())<COLLISION_TRESHOLD )
                    continue;

                sides.add(l);
            }
        }

        return sides;
    }


    private Collection<? extends Anchorage> getSideAndSideAnchorages(Line2D l1, Line2D l2, double radius,double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(l1,l2,radius) ;
        List<Double> calculatedDepths = new ArrayList<>();

        for (Circle2D ci:cList)
        {
            boolean adjusted = false;

            double maxDepth = 0;
            double depth = maximumDepth;
            int adjustmentCount =0;
            double previousDepth = maximumDepth;
            calculatedDepths.clear();

            while (!adjusted &&  (prevalidityCheck(ci, length)))
            {
                DepthZone deepest =  deepestZone(ci);
                depth = (deepest == null) ? maximumDepth: deepest.getDepth();

                if (depth == previousDepth)    // Converged!!!
                    adjusted = true;
                else if (calculatedDepths.contains(depth)) // Previously calculated depth
                {
                    depth = maxDepth;
                    adjusted = true;
                }
                else                            // Adjustment required
                {
                    double newRadius = ShipDynamics.getOuterRadius(length,depth,scale,riskFactor);
                    ci = GeomUtil.getTangentCircleAdjusted(l1,l2,newRadius,ci);
                    calculatedDepths.add(depth);
                    previousDepth = depth;
                    if (depth>maxDepth)
                        maxDepth = depth;
                    adjustmentCount++;
                }
                if (adjustmentCount>MAX_ADJUSTMENT_COUNT)
                {
                    throw new RuntimeException("Too many adjustment..");
                }
            }
            if (adjusted && validAnchorage(ci,length))
            {
                double innerRadius = length;
                double outerRadius = ShipDynamics.getOuterRadius(length,depth,scale);
                Anchorage a = new Anchorage(ci,outerRadius,innerRadius,l1,l2);
                aList.add(a);
            }
        }

        return aList;
    }

    private DepthZone deepestZone(Circle2D ci) {
        double maxDepth = -1;
        DepthZone deepest = null;
        for (DepthZone dz:depthZones)
        {
            if ( (dz.getArea().intersects(ci.parallel(DZ_INCLUSION_THRESHOLD).asAwtShape().getBounds2D())) &&
                    maxDepth< dz.getDepth()  )
            {
                deepest = dz;
                maxDepth = dz.getDepth();
            }
        }

        return deepest;  //To change body of created methods use File | Settings | File Templates.
    }

    private Collection<? extends Point2D> getSideAndSideCornerPoints(Line2D l1, Line2D l2, double radius,double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(l1,l2,radius) ;

        for (Circle2D ci:cList)
        {
            if (validAnchorage(ci, length))
                cpList.add(ci.center().getAsDouble());
        }

        return cpList;
    }

    private boolean prevalidityCheck(Circle2D ci, double length) {

        List<? extends Line2D> sides = area.getSides();


        if (ci==null)  // STG WRONG
            return false;

        //BOUNDARY CONDITION
        if (!area.contains(ci.center().getAsDouble()))
            return false;

        //PRE OVERLAPPING CONDITION
        for (Anchorage a: existingAnchorages)
        {
            if (ci.center().distance(a.getArea().center())< ( a.getArea().radius() ) )
            {
                return false;
            }
        }

        return true ;

    }

    private boolean validAnchorage(Circle2D ci, double length) {

        List<? extends Line2D> sides = area.getSides();


        if (ci==null)  // STG WRONG
            return false;

        //BOUNDARY CONDITION
        if (!area.contains(ci.center().getAsDouble()))
            return false;

        //OVERLAPPING CONDITION
        for (Anchorage a: existingAnchorages)
        {
            if (ci.center().distance(a.getArea().center())< (ci.radius() + a.getArea().radius() - COLLISION_TRESHOLD ) )
            {
                return false;
            }
        }

        // BOUNDARY CONDITION
        for (Line2D l : area.getSides())
        {
            if (l.ptSegDist(ci.center().getAsDouble())< (ci.radius()-COLLISION_TRESHOLD) )
            {
                return false;
            }

        }


        // DEPTH CONDITION CHECK
        if (!uniformAssumption)
        {
            for (DepthZone dz:depthZones)
            {
                Circle2D cip = ci.parallel(-1*DZ_INCLUSION_THRESHOLD);
                if (    dz.getArea().intersectsCircle(cip)  &&
                        (length>dz.getMaximumLength() ) )
                {
                    //System.out.println("Depth Zone Condition Fail: Length: " + length + " MaxLength: " + dz.getMaximumLength());
                    return false;
                }
            }
        }

        //Anchor Path Intersection Check ( IF ENABLED )
        if (maxIntersection >=0)
        {
            GraphPath<BaseVertex,BaseEdge> anchoragePath = getAnchoragePath(ci,true);
            int intersectionCount = getTotalIntersectionCount(anchoragePath);

            if (maxIntersection <intersectionCount)
            {
                return false;
            }
        }

        return true ;

    }

    public DepthZone depthZone(Point2D p)
    {
        for (DepthZone dz:depthZones)
        {
            if (dz.getArea().contains(p))
            {
                return dz;
            }
        }
        return null;
    }

    public DepthZone depthZone(Anchorage c)
    {
        return depthZone(c.getArea().center().getAsDouble());
    }

    public Point2D calculateEntryPoint(Point2D center)
    {
        return GeomUtil.getClosestPointOnSegment(entrySide,center);
    }

    public double getHoleDegree(Circle2D c)
    {
        double d1 = Double.MAX_VALUE-2 , d2 = Double.MAX_VALUE-1 , d3 = Double.MAX_VALUE; ;

        for (Anchorage a: existingAnchorages)
        {
            Circle2D ci = a.getArea();
            double d = c.center().distance(ci.center())-ci.radius();
            if (d<d1)
            {
                d3 = d2; d2=d1; d1=d;
            }
            else if (d<d2)
            {
                d3 = d2; d2=d;
            }
            if (d<d1)
            {
                d3 = d;
            }
        }

        for (Line2D l: area.getSides())
        {
            double d = l.ptLineDist(c.center().getAsDouble());
            if (d<d1)
            {
                d3 = d2; d2=d1; d1=d;
            }
            else if (d<d2)
            {
                d3 = d2; d2=d;
            }
            if (d<d1)
            {
                d3 = d;
            }
        }

        double hd = 1- (d3-c.radius())/c.radius();

        return hd;
    }

    public double getMHD(List<Circle2D> candidates)
    {
        double mhd = Double.MIN_VALUE;

        for (Circle2D c:candidates)
        {
            double hd = getHoleDegree(c);
            if (mhd < hd)
            {
                mhd = hd;
            }
        }
        return mhd;
    }

    public Anchorage getMHDCircle(List<Anchorage> candidates)
    {
        double mhd = -1*Double.MAX_VALUE;
        Anchorage mhdAnchorage = null;

        for (Anchorage a:candidates)
        {
            Circle2D c = a.getArea() ;
            double hd = getHoleDegree(c);
            if (mhd < hd)
            {
                mhd = hd;
                mhdAnchorage = a;
            }
        }
        return mhdAnchorage;
    }


    private double getEffectiveAnchorageArea()
    {
        Rectangle2D br = getEffectiveAnchorageBoundingRect();
        return (br.getHeight()*br.getWidth());
    }

    private Rectangle2D getEffectiveAnchorageBoundingRect() {
        double maxX=-1*Double.MAX_VALUE;
        double maxY=-1*Double.MAX_VALUE;
        double minX=Double.MAX_VALUE;
        double minY=Double.MAX_VALUE;
        for (Anchorage a: existingAnchorages)
        {
            if (a.getArea().boundingBox().getMaxX()>maxX)
                maxX = a.getArea().boundingBox().getMaxX();
            if (a.getArea().boundingBox().getMaxY()>maxY)
                maxY = a.getArea().boundingBox().getMaxY();

            if (a.getArea().boundingBox().getMinX()<minX)
                minX = a.getArea().boundingBox().getMinX();
            if (a.getArea().boundingBox().getMinY()<minY)
                minY = a.getArea().boundingBox().getMinY();

        }

        return new Rectangle2D.Double(minX,minY,maxX-minX,maxY-minY);
    }


    public double calculateAreaUtilization()
    {
        double totalAnchorArea = 0;
        for (Anchorage a: existingAnchorages)
        {
            totalAnchorArea += a.calculateArea();
        }


        return totalAnchorArea/totalArea;
    }

    public double calculateEffectiveAreaUtilization()
    {
        double effectiveAnchorageArea =  getEffectiveAnchorageArea();
        double eau =(effectiveAnchorageArea==0) ? 0: areaUtilization*totalArea / effectiveAnchorageArea ;
        return eau;
    }

    HashMap<Circle2D,GraphPath<BaseVertex,BaseEdge> > pathHistory = new HashMap<Circle2D,GraphPath<BaseVertex,BaseEdge> >();

    private GraphPath<BaseVertex,BaseEdge> checkHistory(Circle2D c)
    {
        if (pathHistory.containsKey(c))
        {
            return pathHistory.get(c);
        }
        else return null;
    }

    long dijkstraCallCount =0;

    private GraphPath<BaseVertex,BaseEdge>  getAnchoragePathWithoutObstacles(Circle2D c)
    {
        GraphPath<BaseVertex,BaseEdge> path = checkHistory(c);
        if ( path!= null )
            return path;

        if (modelGraph == null)
        {
            _createModelGraph(area.getBounds2D());
        }

        TimerUtil.start("DIJKSTRA");

        Point2D entryPoint = calculateEntryPoint(c.center().getAsDouble());
        GridVertex s = modelGraph.getClosestVertex(entryPoint);
        GridVertex t = modelGraph.getClosestVertex(c.center().getAsDouble());
        DijkstraWithHeuristicShortestPath<BaseVertex,BaseEdge> spalg = new DijkstraWithHeuristicShortestPath<BaseVertex, BaseEdge>(modelGraph,s,t,new MHDHeuristic<BaseVertex>(t));
        dijkstraCallCount++;

        TimerUtil.stop("DIJKSTRA");

        path  = spalg.getPath();
        pathHistory.put(c,path);
        return path;
    }

    public GraphPath<BaseVertex,BaseEdge>  getAnchoragePath(Circle2D c,boolean passThroughAnchorages /*todo*/)
    {
        if (passThroughAnchorages)
            return getAnchoragePathWithoutObstacles(c);
        else
            throw new NotImplementedException(); /*todo*/
    }

    public int getTotalIntersectionCount(GraphPath<BaseVertex,BaseEdge> path)
    {
        TimerUtil.start("INTERSECT");

        ObstacleGenerator og = new CircleListObstacleGenerator(getExistingAnchoragesAsCircles(),10000,0);

        List<ObstacleInterface> oList = null;
        int intersectionCount=0;
        double intersectionLength=0;
        try {
            oList = (List<ObstacleInterface>) og.generate();
            intersectionCount = Path.getTotalIntersectionCount(path,oList);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        TimerUtil.stop("INTERSECT");

        return intersectionCount;
    }

    public void addExistingAnchorage(Anchorage anchorage) {

        double disToEnt = ((0.6)*(anchorage.getArea().center().x()) + anchorageLength) - anchorage.getArea().center().y()  ;

        ++arrivalCount;
        totalDistanceTraveled += 2* disToEnt;
        avgDistanceTraveled =  totalDistanceTraveled/arrivalCount ;
        existingAnchorages.add(anchorage);
        calculateArrIntersectionLength(anchorage);


        if  (collectStatistics)
        {
            areaUtilization  = calculateAreaUtilization() ;
            totalAreaUtilization += areaUtilization;
        }

        ststAvgAvgArrIntersectionLength += AvgArrIntersectionLength;
        ststAvgAreaUtil += areaUtilization;
        ststAvgDinAreaUtil += anchorage.calculateArea()* (anchorage.getDepartureTime() - anchorage.getArrivalTime());


        if (STATISTICS){ updateResults();}
    }

    public void calculateArrIntersectionLength(Anchorage a)
    {

        for(Circle2D ea: getExistingAnchoragesAsCircles()){
            if((Math.abs(a.getArea().center().x() - ea.center().x()) < ea.radius()) && (a.getArea().center().y() < ea.center().y())) {
                double IntersectionLength = 2 * Math.sqrt(Math.pow(ea.radius(), 2) - Math.pow((ea.center().x() - a.getArea().center().x()), 2));
                totalArrivalIntersectionLength +=  IntersectionLength;
                AvgArrIntersectionLength= arrivalCount   ==0           ?  0 :  totalArrivalIntersectionLength/arrivalCount;
            }else continue;
        }
    }

    public void calculateDepIntersectionLength(Anchorage d)
    {

        for(Circle2D ea: getExistingAnchoragesAsCircles()){
            if ((Math.abs(d.getArea().center().x() - ea.center().x()) < ea.radius()) && (d.getArea().center().y() < ea.center().y())) {
                double IntersectionLength = 2 * Math.sqrt(Math.pow(ea.radius(), 2) - Math.pow((ea.center().x() - d.getArea().center().x()), 2));
                totalDepartureIntersectionLength +=  IntersectionLength;
                AvgDepIntersectionLength= departureCount ==0           ?  0 :  totalDepartureIntersectionLength/departureCount;
            }else continue;
        }
    }

    private void updateDepartureIntersectionCount(Anchorage departure) {

        GraphPath<BaseVertex,BaseEdge> anchoragePath =  getAnchoragePath(departure.getArea(), true);
        int intersect = getTotalIntersectionCount(anchoragePath);

        totalDepartureIntersection += intersect;

        departureCount++;
    }

    public void steadyStateStatisticsReset(){

        bsteadyState = true;

        candidateAnchorages.clear();
        totalArrivalIntersectionLength=0;
        AvgArrIntersectionLength=0;
        totalDepartureIntersectionLength=0;
        AvgDepIntersectionLength=0;
        totalAreaUtilization=0;
        areaUtilization = 0;

        departureCount =0;
        arrivalCount =0 ;

        avgDistanceTraveled = 0;
        totalDistanceTraveled = 0;

        totalAnchoragePathLength = 0;
        totalAnchorageIntersection = 0;
        totalDepartureIntersection=0;
        totalMinDistanceToEntry = 0;

        rejectCount = 0;
        consecutive_rejectCount=0;

        minDistanceToEntry = -1;
        dijkstraCallCount =0;
        pathHistory.clear();
        areaFull = false;

        ststAvgAvgArrIntersectionLength=0;
        ststAvgAvgDepIntersectionLength=0;
        ststAvgAreaUtil=0;
    }

    public void simulationTimeUpReset() throws IOException {

        simulationClock=0;
        runCount++;

        existingAnchorages.clear();
        candidateAnchorages.clear();
        totalArrivalIntersectionLength=0;
        AvgArrIntersectionLength=0;
        totalDepartureIntersectionLength=0;
        AvgDepIntersectionLength=0;
        totalAreaUtilization=0;
        areaUtilization=0;

        departureCount=0;
        arrivalCount=0 ;

        avgDistanceTraveled = 0;
        totalDistanceTraveled = 0;

        totalAnchoragePathLength = 0;
        totalAnchorageIntersection = 0;
        totalDepartureIntersection=0;
        totalMinDistanceToEntry = 0;

        rejectCount = 0;
        consecutive_rejectCount=0;

        minDistanceToEntry = -1;
        dijkstraCallCount =0;
        pathHistory.clear();

        areaFull = false;

        bsteadyState = false;
        bsimulationTimeUp = false;

        ststAvgAvgArrIntersectionLength=0;
        ststAvgAvgDepIntersectionLength=0;
        ststAvgAreaUtil=0;
        ststAvgDinAreaUtil=0;

        if (spsa){
            createQvectorSPSA();
        }else {
            myTheta();
        }
    }

    public void updateFinalResults(){
        try {
            String current = System.getProperty("user.dir");

            // Create results directory if it doesn't exist
            File resultsDir = new File(current + File.separator + "results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }

            File file = new File(resultsDir, type+"_runs_"+W1+"-"+W2+"-"+W3+ "_"+"L"+lambda+finalFileTime+".csv");

            // Use the full file path
            FileWriter fileWritter = new FileWriter(file, true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(4);
            nf.setMinimumFractionDigits(4);

            oFList[runCount-1]= ((W1* ((ststAvgAvgArrIntersectionLength/arrivalCount+ststAvgAvgDepIntersectionLength/departureCount)/(2* anchorageLength))+ W2* (1-ststAvgDinAreaUtil/(totalArea* RUN_DAYS*1440))+ W3* (avgDistanceTraveled/(2* anchorageLength))) / (W1+W2+W3)) + lambda * ( Math.abs(getCurrentQvector()[0])+Math.abs(getCurrentQvector()[1])+Math.abs(getCurrentQvector()[2])+Math.abs(getCurrentQvector()[3])+Math.abs(getCurrentQvector()[4])+Math.abs(getCurrentQvector()[5])+Math.abs(getCurrentQvector()[6]));

            bufferWritter.write(
                    runCount +","+
                            ststAvgAvgArrIntersectionLength/arrivalCount  + ","+
                            ststAvgAvgDepIntersectionLength/departureCount  + ","+
                            ((ststAvgAvgArrIntersectionLength/arrivalCount)+(ststAvgAvgDepIntersectionLength/departureCount))/2  + ","+
                            avgDistanceTraveled  + ","+
                            ststAvgDinAreaUtil/(totalArea* RUN_DAYS*1440) +","+
                            ststAvgAreaUtil/(arrivalCount+departureCount) +","+
                            oFList[runCount-1]+","+
                            "("+nf.format(getCurrentQvector()[0])+"_"+ nf.format(getCurrentQvector()[1])+"_"+ nf.format(getCurrentQvector()[2])+"_"+ nf.format(getCurrentQvector()[3])+"_"+ nf.format(getCurrentQvector()[4])+"_"+ nf.format(getCurrentQvector()[5])+"_"+ nf.format(getCurrentQvector()[6])+")"
                            +"\n");

            bufferWritter.close();

            ststAvgAvgArrIntersectionLength=0;
            ststAvgAvgDepIntersectionLength=0;
            totalDistanceTraveled=0;
            avgDistanceTraveled=0;
            ststAvgAreaUtil=0;
            ststAvgDinAreaUtil=0;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean steadyState(){
        if(simulationClock >= 1440*WORMUP_DAYS){
            return true;

        }else{return false;}
    }
    public boolean simulationTimeUp(){

        if (simulationClock >= 1440*(RUN_DAYS + WORMUP_DAYS)) {

            return true;

        }else {return false;}
    }

    public boolean simulationFinished(){
        if (runCount == NO_OF_RUNS+1){
            return true;

        }else {return false;}
    }


    public void createQvectorSPSA() {

        int p = 7;
        int n = NO_OF_RUNS;

        double alpha = 0.602;
        double gamma = 0.101;
        double a = 0.017 * 100 * 2 ;                     // Theta & Plus & Minus Adjustment
        double c = 1.9 * 0.1;                         // Theta Plus & Minus Adjustment
        double A = 1 / (2 * 10 * 100 * 10);       // Theta Vector Adjustment

        double[] thetaPlus = new double[p];
        double[] thetaMinus = new double[p];
        //double[] theta = new double[p];
        double[] gHat = new double[p];
        double yp = 0;
        double ym = 0;

        if (runCount == 1) {

            RandUtil rng = new RandUtil((int) System.currentTimeMillis());
            double[] qVector = new double[p];
            qVector[0]= -1;
            qVector[1]= -1;
            qVector[2]= 1;

            qVector[3]= -1;
            qVector[4]= -1;
            qVector[5]= 1;
            qVector[6]= -1;

            double[] theta = qVector;

            double ak = a / Math.pow(( 1), alpha);
            double ck = c / Math.pow((1), gamma);
            for (int i = runCount - 1; i < runCount - 1 + (2 * RUN_PER_ITERATION); i++) {
                akList[i] = ak;
                ckList[i] = ck;
            }

            RandUtil rn = new RandUtil((int) System.currentTimeMillis());
            int[] delta = new int[p];
            double[] temp = new double[p];

            for (int i = 0; i < p; i++) {
                delta[i] = (int) (2 * Math.round(rn.nextDouble()) - 1);
                thetaPlus[i] = theta[i] + ck * delta[i];
                thetaMinus[i] = theta[i] - ck * delta[i];
                temp[i] = (thetaPlus[i] + thetaMinus[i])/2;
            }
            thetaList.add(temp);
            for (int i = runCount - 1; i < runCount - 1 + (RUN_PER_ITERATION); i++) {
                qVectorList.add(i, thetaPlus);
            }
            for (int i = runCount - 1 + RUN_PER_ITERATION; i < runCount - 1 + (2 * RUN_PER_ITERATION); i++) {
                qVectorList.add(i, thetaMinus);
            }

            for (int i = runCount - 1; i < runCount - 1 + (2 * RUN_PER_ITERATION); i++) {
                deltaList.add(i, delta);
                thetaZeroList.add(i, theta);
            }

            k++;

        } else {
            if (runCount <= ((NO_OF_RUNS * 2) / 3)) {
                if (runCount % (2 * RUN_PER_ITERATION) == 1) {

                    double[] theta = thetaZeroList.get(runCount - 1 - (2 * RUN_PER_ITERATION));
                    for (int i = runCount - 1 - (2 * RUN_PER_ITERATION); i < runCount - 1 - (RUN_PER_ITERATION); i++) {
                        yp += oFList[i];
                    }
                    yp = yp / RUN_PER_ITERATION;
                    for (int i = runCount - 1 - (RUN_PER_ITERATION); i < runCount - 1; i++) {
                        ym += oFList[i];
                    }
                    ym = ym / RUN_PER_ITERATION;
                    for (int j = 0; j < p; j++) {
                        gHat[j] = Math.signum((yp - ym) / (2 * ckList[runCount - 1 - (2 * RUN_PER_ITERATION)] * deltaList.get(runCount - 1 - (2 * RUN_PER_ITERATION))[j]));
                    }

                    for (int j = 0; j < p; j++) {
                        theta[j] = theta[j] - akList[runCount - 1 - (2 * RUN_PER_ITERATION)] * gHat[j] ;
                    }

                    double ak = a / Math.pow(( 1 ), alpha);
                    double ck = c / Math.pow(( 1), gamma);
                    for (int i = runCount - 1; i < runCount - 1 + (2 * RUN_PER_ITERATION); i++) {
                        akList[i] = ak;
                        ckList[i] = ck;
                    }

                    RandUtil rn = new RandUtil((int) System.currentTimeMillis());
                    int[] delta = new int[p];
                    double[] temp2 = new double[p];
                    for (int i = 0; i < p; i++) {
                        delta[i] = (int) (2 * Math.round(rn.nextDouble()) - 1);
                        thetaPlus[i] = theta[i] + ck * delta[i];
                        thetaMinus[i] = theta[i] - ck * delta[i];
                        temp2[i] = (thetaPlus[i] + thetaMinus[i])/2;
                    }
                    thetaList.add(temp2);
                    for (int i = runCount - 1; i < runCount - 1 + (RUN_PER_ITERATION); i++) {
                        qVectorList.add(i, thetaPlus);
                    }
                    for (int i = runCount - 1 + RUN_PER_ITERATION; i < runCount - 1 + (2 * RUN_PER_ITERATION); i++) {
                        qVectorList.add(i, thetaMinus);
                    }

                    for (int i = runCount - 1; i < runCount - 1 + (2 * RUN_PER_ITERATION); i++) {
                        deltaList.add(i, delta);
                        thetaZeroList.add(i, theta);
                    }
                    k++;
                }
            }
        }
        if (runCount == (((NO_OF_RUNS * 2 )/ 3)) + 1) {

            for (double[] s : thetaList){
                for (int x=0;x<RUN_PER_ITERATION;x++){
                    qVectorList.add(s);
                }
            }
        }
    }

    public void writeObjectiveFunctionList(){
        int k=0;
        for (int i = 0; i < NO_OF_ITERATION; i++) {
            double fTemp = 0;
            for (int j = 0; j < RUN_PER_ITERATION; j++) {
                fTemp += oFList[(NO_OF_RUNS * 2 / 3) + (i * RUN_PER_ITERATION) + j];
            }
            finalFList[k] = fTemp/RUN_PER_ITERATION;
            k++;
        }
        try {
            String current = System.getProperty("user.dir");

            // Create results directory if it doesn't exist
            File resultsDir = new File(current + File.separator + "results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }

            File file = new File(resultsDir, type+"_summary_"+W1+"-"+W2+"-"+W3+ "_"+"L"+lambda+summaryFileTime+".csv");

            FileWriter fileWritter = new FileWriter(file);  // Removed the true parameter to not append
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(4);
            nf.setMinimumFractionDigits(4);

            bufferWritter.write("Iteration" + "," + "Objective_Function" + "," + "Net_OF" + "," + "Theta" + "\n");

            for (int i=0;i<NO_OF_ITERATION;i++){
                double coeficientContributs = 0;
                for (int j=0; j<thetaList.get(i).length; j++){
                    coeficientContributs += Math.abs(thetaList.get(i)[j]);
                }
                bufferWritter.write( (i+1) + "," + finalFList[i] + "," + (finalFList[i]- (coeficientContributs * lambda))  + "," + "(" + nf.format(thetaList.get(i)[0])  + "_" + nf.format(thetaList.get(i)[1])  + "_" + nf.format(thetaList.get(i)[2]) + "_" + nf.format(thetaList.get(i)[3]) + "_" + nf.format(thetaList.get(i)[4]) + "_" + nf.format(thetaList.get(i)[5]) + "_" + nf.format(thetaList.get(i)[6]) + ")"  + "\n");
            }

            bufferWritter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void myTheta(){
        double[] theta = new double[] {  -1.8500,1.5500,-3.9300,1.8900,-2.1900,-2.9100,-5.5900
        };
        qVectorList.add(theta);
    }
}

