package pgraph.anya.experiments;

import pgraph.util.TimerUtil;
import pgraph.anya.astar.AStarGrid;
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
public class AStarExperimentRunner implements ExperimentRunnerInterface {

    private static final int EXPERIMENT_COUNT = 10;
    AStarMapLoaderInterface mapLoader = null;

    boolean postSmoothingEnabled= false;

    String outputFile;

    public AStarExperimentRunner(AStarMapLoaderInterface mapLoader, boolean postSmoothingEnabled) {
        this.mapLoader = mapLoader;
        this.postSmoothingEnabled = postSmoothingEnabled;
    }

    public AStarExperimentRunner(AStarMapLoaderInterface mapLoader) {
        this.mapLoader = mapLoader;
    }

    public void setMapLoader(AStarMapLoaderInterface mapLoader) {
        this.mapLoader = mapLoader;
    }


    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    @Override
    public void printHeader(String outputFile) throws IOException {
        FileWriter fstream = new FileWriter(outputFile,true);
        BufferedWriter out = new BufferedWriter(fstream);

        String headerLine  =    "Time" + ";"+
                                "Duration" + ";"+
                                "Expanded"+";" +
                                "Generated"+ ";"+
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

    private void printResultStandard(ExperimentInterface exp, AStarGrid asg, String outputFile,long duration) throws IOException
    {
        FileWriter fstream = new FileWriter(outputFile,true);
        BufferedWriter out = new BufferedWriter(fstream);

        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime =  sdf.format(cal.getTime()) ;

        DecimalFormat nf = StringUtil.getMyDecimalFormatter();


        double solution = asg.solutionLength;

        long expanded = asg.spAlg.iter.expandedNodes;
        long generated = asg.spAlg.iter.generatedNodes;

        String outputLine =  currentTime + ";"+
                duration + ";"+
                expanded + ";"+
                generated + ";"+
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


    public void runExperiment(ExperimentInterface exp) throws IOException {
        if (mapLoader== null)
        {
            System.out.println("No Map Loader found");
            return;
        }


        String title = exp.getTitle() ==null ? "":exp.getTitle();
        System.out.println(title+" Experiment started: "+ exp.getMapFile() + " ("+exp.getStartX()+","+exp.getStartY() +")->("+exp.getEndX()+","+exp.getEndY()+")");



        AStarGrid asg = mapLoader.loadMap(exp.getMapFile());
        asg.setPostSmoothingEnabled(postSmoothingEnabled);


        if (asg.xSize != exp.getXSize()+1 || asg.ySize != exp.getYSize()+1)
        {
            System.out.println("Map size does not match with the scenario");
            return;
        }

        asg.setStart(exp.getStartX(), exp.getStartY());
        asg.setEnd(exp.getEndX(), exp.getEndY());

        MicroBenchmark mb = new MicroBenchmark(asg);
        mb.benchmark(EXPERIMENT_COUNT);
        long duration = mb.getAvgTime();

        printResultStandard(exp, asg, outputFile, duration);


    }

    private void printResult(ExperimentInterface exp, AStarGrid asg, String outputFile,long duration) throws IOException
    {
        FileWriter fstream = new FileWriter(outputFile,true);
        BufferedWriter out = new BufferedWriter(fstream);

        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime =  sdf.format(cal.getTime()) ;

        NumberFormat nf = NumberFormat.getInstance();

        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(3);

        double solution = asg.solutionLength;

        String outputLine =  "[ "+currentTime + " ] "+
                            StringUtil.padLeft(duration+"msec ",13) +
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

        List<ExperimentInterface> experiments =  el.loadExperiments("anya_experiments/scenarios/bg2/AR0011SR.map.scen");

        //TimerUtil.setEnabled(true);


        for (ExperimentInterface exp:experiments)
        {
            if (exp.getUpperBound()>0) {

                AStarMapLoaderInterface mapLoader= new AStarMapLoader(new AnyaMapLoader());


                exp.setTitle("O3");
                for (int i =0; i<3 ;i++) {

                    AStarExperimentRunner er = new AStarExperimentRunner(mapLoader);
                    er.setOutputFile("anya_experiments/output/test_opt.out");



                    er.runExperiment(exp);
                }

                //break; // Testing

            }
        }

        TimerUtil.printTotal();
    }


    private static void test3() throws IOException {
        ExperimentLoaderInterface el = new AnyaExperimentLoader();

        List<ExperimentInterface> experiments =  el.loadExperiments("anya_experiments/scenarios/sc1/test/test.map.scen");

        TimerUtil.setEnabled(true);


        for (ExperimentInterface exp:experiments) {
            if (exp.getUpperBound() > 0)
            {
                AStarExperimentRunner er = new AStarExperimentRunner(new AStarMapLoader(new AnyaMapLoader()));
                er.setOutputFile("astar_experiments/output/test_opt.out");

                er.runExperiment(exp);

                TimerUtil.printTotal();

                break;


            }
        }
    }



    public static void main(String[] args) throws IOException {

        test1();

    }

}
