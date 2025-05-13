package pgraph.anya.experiments;

import pgraph.anya.AnyaGrid;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Dindar on 22.8.2014.
 */
public class MovingAIScenarioPerformer {

    private static final double MAX_LENGTH = 200000;
    double maxLength =0;

    ExperimentRunnerInterface expRunner = null;
    ExperimentLoaderInterface expLoader = null;

    public MovingAIScenarioPerformer(double maxLength, ExperimentRunnerInterface expRunner, ExperimentLoaderInterface expLoader) {
        this.maxLength = maxLength;
        this.expRunner = expRunner;
        this.expLoader = expLoader;
    }

    public MovingAIScenarioPerformer(double maxLength) {
        this.maxLength = maxLength;
        expRunner = new AnyaExperimentRunner((AnyaMapLoaderInterface)new AnyaMapLoader(),false);
        expLoader = new AnyaExperimentLoader();
    }

/*
    public void performScenarios(String scenarioFolder, String folderName, boolean debugTrace) throws IOException {
        File folder = new File(scenarioFolder);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile())
            {
                System.out.println("Scenario File " + listOfFiles[i].getName());
                String scenarioName = listOfFiles[i].getName().replaceAll(".scen", "");
                performScenario(scenarioName,folderName,debugTrace);
            } else if (listOfFiles[i].isDirectory()) {
                continue;
            }
        }
    }*/

    public void performScenarios(String scenarioFolder, String outputFolder) throws IOException {
        File folder = new File(scenarioFolder);
        File[] listOfFiles = folder.listFiles();



        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile())
            {
                File of = new File(outputFolder);
                if (!of.exists())
                {
                   of.mkdirs();
                }
                System.out.println("Scenario File " + listOfFiles[i].getName());
                String scenarioName = listOfFiles[i].getName().replaceAll(".scen", "");
                String scenarioFilePath = scenarioFolder +"/"+listOfFiles[i].getName();
                String outputFilePath= outputFolder + "/"+ scenarioName + ".out";

                performScenario(scenarioFilePath,outputFilePath);
            } else if (listOfFiles[i].isDirectory()) {
                String subScenarioFolder = listOfFiles[i].getAbsolutePath();
                String subOutputFolder = outputFolder+"/" + listOfFiles[i].getName();
                performScenarios(subScenarioFolder,subOutputFolder);
                continue;
            }
        }
    }

    public void performScenario(String scenarioFilePath,String outputFilePath) throws IOException {

        System.gc();
        List<ExperimentInterface> experiments =  expLoader.loadExperiments(scenarioFilePath);

        expRunner.printHeader(outputFilePath);

        for (ExperimentInterface exp:experiments)
        {
            if (exp.getUpperBound()<maxLength) {

                expRunner.setOutputFile(outputFilePath);
                expRunner.runExperiment(exp);

            }
        }
    }




    private static void jarJobAstar() throws IOException {
        MapLoaderInterface mapLoader = new AStarMapLoader(new AnyaMapLoader());
        ExperimentRunnerInterface expRunner = new AStarExperimentRunner((AStarMapLoaderInterface)mapLoader,false);
        ExperimentLoaderInterface expLoader = new AStarExperimentLoader();

        MovingAIScenarioPerformer performer = new MovingAIScenarioPerformer( MAX_LENGTH,expRunner,expLoader);
        performer.performScenarios("scenarios","output_astar");
    }

    private static void jarJobAstarPS() throws IOException {
        MapLoaderInterface mapLoader = new AStarMapLoader(new AnyaMapLoader());
        ExperimentRunnerInterface expRunner = new AStarExperimentRunner((AStarMapLoaderInterface)mapLoader,true);
        ExperimentLoaderInterface expLoader = new AStarExperimentLoader();

        MovingAIScenarioPerformer performer = new MovingAIScenarioPerformer( MAX_LENGTH,expRunner,expLoader);
        performer.performScenarios("scenarios","output_astar_ps");
    }

    private static void job2() throws IOException {
        MovingAIScenarioPerformer performer = new MovingAIScenarioPerformer(MAX_LENGTH);
        performer.performScenarios("anya_experiments/scenarios/dao","anya_experiments/output/dao");
        performer.performScenarios("anya_experiments/scenarios/sc1","anya_experiments/output/sc1");
    }

    private static void job3() throws IOException {
        MapLoaderInterface mapLoader = new AStarMapLoader(new AnyaMapLoader());
        ExperimentRunnerInterface expRunner = new AStarExperimentRunner((AStarMapLoaderInterface)mapLoader);
        ExperimentLoaderInterface expLoader = new AStarExperimentLoader();

        MovingAIScenarioPerformer performer = new MovingAIScenarioPerformer( MAX_LENGTH,expRunner,expLoader);
        //MovingAIScenarioPerformer performer = new MovingAIScenarioPerformer( MAX_LENGTH);
        performer.performScenarios("anya_experiments/scenarios/bg2","anya_experiments/output/bg2");
    }

    private static void jarJob() throws IOException {
        jarJob("scenarios","output");
    }

    private static void jarJob(String fpScenario, String fpOutput) throws IOException {
        MovingAIScenarioPerformer performer = new MovingAIScenarioPerformer(MAX_LENGTH);

        performer.performScenarios(fpScenario,fpOutput);
    }



    public static void main(String[] args) throws IOException {

        if (args.length ==1) {
            if (args[0].equals("-ideaRun"))
                job3();
            if (args[0].equals("-ASTAR"))
                jarJobAstar();
            if (args[0].equals("-ASTAR_PS"))
                jarJobAstarPS();
        }
        else if (args.length ==0)
            jarJob();
        else if (args.length==2)
            jarJob(args[0],args[1]);

        else
            return;


    }

}
