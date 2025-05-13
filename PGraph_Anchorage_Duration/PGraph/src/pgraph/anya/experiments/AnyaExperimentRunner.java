package pgraph.anya.experiments;

import pgraph.util.TimerUtil;
import pgraph.anya.AnyaGrid;
import pgraph.gui.GraphViewer;
import pgraph.util.MicroBenchmark;
import pgraph.util.StringUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Dindar on 22.8.2014.
 */
public class AnyaExperimentRunner implements ExperimentRunnerInterface {

    private static final int EXPERIMENT_COUNT = 10;
    AnyaMapLoaderInterface mapLoader = null;
    private long MAX_DEPTH= Long.MAX_VALUE;
    private boolean showResult = false;

    String outputFile;
    String debugTraceFile = null;
    long debugTraceInterval = 1;
    private boolean visibleIntervals=false;



    public AnyaExperimentRunner(AnyaMapLoaderInterface mapLoader, long MAX_DEPTH, boolean showResult) {
        this.mapLoader = mapLoader;
        this.MAX_DEPTH = MAX_DEPTH;
        this.showResult = showResult;
    }

    public AnyaExperimentRunner(AnyaMapLoaderInterface mapLoader,boolean showResult) {
        this.mapLoader = mapLoader;
        this.showResult = showResult;
    }

    public AnyaExperimentRunner(AnyaMapLoaderInterface mapLoader) {
        this.mapLoader = mapLoader;
    }

    public void setMapLoader(AnyaMapLoaderInterface mapLoader) {
        this.mapLoader = mapLoader;
    }

    public void setMAX_DEPTH(long MAX_DEPTH) {
        this.MAX_DEPTH = MAX_DEPTH;
    }

    public void setShowResult(boolean showResult) {
        this.showResult = showResult;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }


    public void setDebugTraceFile(String debugTraceFile) {
        this.debugTraceFile = debugTraceFile;
    }

    public void setDebugTraceInterval(long debugTraceInterval) {
        this.debugTraceInterval = debugTraceInterval;
    }

    public void runExperiment(ExperimentInterface exp) throws IOException {
        if (mapLoader== null)
        {
            System.out.println("No Map Loader found");
            return;
        }


        AnyaExperimentInterface aexp = (AnyaExperimentInterface)exp;
        String title = exp.getTitle() ==null ? "":exp.getTitle();
        System.out.println(title+" Experiment started: "+ exp.getMapFile() + " ("+exp.getStartX()+","+exp.getStartY() +")->("+exp.getEndX()+","+exp.getEndY()+")");



        AnyaGrid ag = mapLoader.loadMap(exp.getMapFile());


        if (debugTraceFile!= null) {
            ag.setDebugTraceInterval(debugTraceInterval);
            ag.setDebugTraceFile(debugTraceFile,exp.getMapFile()+ " ("+exp.getStartX()+","+exp.getStartY() +")->("+exp.getEndX()+","+exp.getEndY()+")");
            /*debug*/ag.setShowIntervals(visibleIntervals);
        }

        if (ag.xSize != exp.getXSize()+1 || ag.ySize != exp.getYSize()+1)
        {
            System.out.println("Map size does not match with the scenario");
            return;
        }

        ag.setOptimizationType(aexp.getOptimizationType());
        ag.setStart(ag.getVertex(exp.getStartX(),exp.getStartY()));
        ag.setEnd(ag.getVertex(exp.getEndX(), exp.getEndY()));

        MicroBenchmark mb = new MicroBenchmark(ag);
        mb.benchmark(EXPERIMENT_COUNT);

        long duration = mb.getAvgTime();

        printResultStandard(exp,ag, outputFile,duration);

        if (showResult)
            GraphViewer.showContent(ag);
    }

    @Override
    public void printHeader(String outputFile) throws IOException {
        FileWriter fstream = new FileWriter(outputFile,true);
        BufferedWriter out = new BufferedWriter(fstream);

        String headerLine  =    "Time" + ";"+
                                "Duration" + ";"+
                                "StepCount"+";" +
                                "NodeCount"+ ";"+
                                "MapFile" +";"+
                                "Start" +";"+
                                "End" +";"+
                                "UpperBound"  +";"+
                                "Solution"+";"+
                                "Status"+";";

        out.write(headerLine );

        out.newLine();
        out.close();
    }

    private void printResultStandard(ExperimentInterface exp, AnyaGrid ag, String outputFile,long duration) throws IOException
    {
        FileWriter fstream = new FileWriter(outputFile,true);
        BufferedWriter out = new BufferedWriter(fstream);

        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime =  sdf.format(cal.getTime()) ;

        DecimalFormat nf = StringUtil.getMyDecimalFormatter();

        double solution = ag.getPathLength();



        String outputLine =  currentTime + ";"+
                duration + ";"+
                ag.getStepCounter()+";" +
                ag.generatedIntervalCount+";" +
                exp.getMapFile() +";"+
                "("+ exp.getStartX()+","+ exp.getStartY() + ")" +";"+
                "(" + exp.getEndX() + "," + exp.getEndY() + ")" +";"+
                nf.format(exp.getUpperBound())  +";"+
                nf.format(solution)+";";

        if (solution-exp.getUpperBound()>0.1)
            outputLine+= "FAILED";
        else outputLine+= "SUCCESS";

        out.write(outputLine );

        out.newLine();
        out.close();

    }

    private void printResult(ExperimentInterface exp, AnyaGrid ag, String outputFile,long duration) throws IOException
    {
    /* System.out.println("Solution: "+ ag.getPathLength());
        System.out.println("Step Count: "+ ag.getStepCounter());
        System.out.println("Generated Interval Count: "+ ag.generatedIntervalCount);
        System.out.println("Ignored Interval Count: "+ ag.ignoredIntervalCount);
        System.out.println("Total Time: "+ duration);*/

        FileWriter fstream = new FileWriter(outputFile,true);
        BufferedWriter out = new BufferedWriter(fstream);

        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime =  sdf.format(cal.getTime()) ;

        NumberFormat nf = NumberFormat.getInstance();

        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(3);

        double solution = ag.getPathLength();

        String outputLine =  "[ "+currentTime + " ] "+
                            StringUtil.padLeft(duration+"msec ",13) +
                            StringUtil.padLeft( ag.getStepCounter()+"steps  ",13) +
                            //StringUtil.padRight(exp.getTitle(),5) +
                            exp.getMapFile() +
                            StringUtil.padLeft(" ("+ exp.getStartX()+","+ exp.getStartY() + ")",12) +" -> "+
                            StringUtil.padRight("(" + exp.getEndX() + "," + exp.getEndY() + ")", 11) +"  UB: "+
                            StringUtil.padLeft(nf.format(exp.getUpperBound()),8)  +"   S: "+
                            StringUtil.padLeft(nf.format(solution),8);

        if (solution-exp.getUpperBound()>0.001)
            outputLine+= " FAILED";
        out.write(outputLine );

        out.newLine();
        out.close();

    }



    private static void test1() throws IOException {
        ExperimentLoaderInterface el = new AnyaExperimentLoader();

        List<ExperimentInterface> experiments =  el.loadExperiments("anya_experiments/scenarios/sc1/test/opt_test.map.scen");

        //TimerUtil.setEnabled(true);


        for (ExperimentInterface exp:experiments)
        {
            if (exp.getUpperBound()>0) {

                AnyaMapLoader mapLoader= new AnyaMapLoader();


                exp.setTitle("O3");
                for (int i =0; i<3 ;i++) {

                    AnyaExperimentRunner er = new AnyaExperimentRunner(mapLoader, false);
                    er.setOutputFile("anya_experiments/output/test_opt.out");
                    er.setDebugTraceFile("anya_experiments/output/test_opt.trace");
                    er.setDebugTraceInterval(10000);


                    er.runExperiment(exp);
                }

                //break; // Testing

            }
        }

        TimerUtil.printTotal();
    }


    private static void test3() throws IOException {
        ExperimentLoaderInterface el = new AnyaExperimentLoader();

        List<ExperimentInterface> experiments =  el.loadExperiments("anya_experiments/scenarios/dao/test/temp.map.scen");

        TimerUtil.setEnabled(true);


        for (ExperimentInterface exp:experiments) {
            if (exp.getUpperBound() > 0)
            {
                AnyaExperimentRunner er = new AnyaExperimentRunner(new AnyaMapLoader(), 12300,true);
                er.setOutputFile("anya_experiments/output/test_opt.out");
                er.setDebugTraceFile("anya_experiments/output/test_opt.trace");
                er.setDebugTraceInterval(100000);

                er.visibleIntervals = true;


                AnyaGrid ag = er.mapLoader.loadMap(exp.getMapFile());
                ag.setStart(ag.getVertex(exp.getStartX(),exp.getStartY()));
                ag.setEnd(ag.getVertex(exp.getEndX(), exp.getEndY()));

                ag.search_best_first(20);

                GraphViewer.showContent(ag);

                TimerUtil.printTotal();

                break;


            }
        }
    }

    private static void test2() throws IOException
    {
        //AnyaExperimentInterface exp = new AnyaExperiment("maps/sc1/AcrosstheCape.map",110,100,31,87,102,25);
        //AnyaExperimentRunner er = new AnyaExperimentRunner(new AnyaSubMapLoader(30,140,300,400), 20000,true);

        ExperimentInterface exp = new AnyaExperiment("maps/dao/den510d.map",60,120,1,115,51,19);
        AnyaExperimentRunner er = new AnyaExperimentRunner(new AnyaSubMapLoader(100,160,100,220), 500,true);

        //er.visibleIntervals = true;

        er.setOutputFile("anya_experiments/output/test3.out");
        er.setDebugTraceFile("anya_experiments/output/test3.trace");

        er.runExperiment(exp);
    }

    public static void main(String[] args) throws IOException {

        test3();

    }

}
