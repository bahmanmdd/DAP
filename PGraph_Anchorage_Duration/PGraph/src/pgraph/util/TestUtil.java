package pgraph.util;

import org.jgrapht.GraphPath;
import pgraph.Path;
import pgraph.alg.ExactPSA;
import pgraph.alg.KSPNA;
import pgraph.alg.PSA;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridPosition;
import pgraph.gui.GraphViewer;
import pgraph.ranga.RangaInputPreparer;
import pgraph.ranga.RangaOutputParser;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 04.05.2013
 * Time: 09:51
 * To change this template use File | Settings | File Templates.
 */
public class TestUtil {


    public static final byte A_RANGA        = 1;
    public static final byte A_UPPERBOUND   = 2;
    public static final byte A_LOWERBOUND   = 4;
    public static final byte A_KSP          = 8;
    public static final byte A_PSA          = 16;
    private static boolean workSilent = true;

    public static void writeOutput(String outputFile, String line) throws IOException {

        BufferedWriter br  = new BufferedWriter(new FileWriter(outputFile,true));
        br.write(line);
        br.newLine();
        br.close();
    }

    public static void cleanUpEnvironment()
    {
        return;
    }


    public static void performRangaExperiment(String outputFile,String map,BaseDirectedGraph g,BaseVertex start,BaseVertex end,double neutroCost, int K) throws IOException, InterruptedException, InstantiationException {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        RangaInputPreparer.createEdgetData("EdgeData.txt", g);
        RangaInputPreparer.createPathVars("PathVars.txt", g, start, end,K);

        byte[] bytes = new byte[300];
        byte[] errorBytes = new byte[300];

        long rangaElapsedTime = System.currentTimeMillis();

        Process p = new ProcessBuilder("rangaInputs/WCSPP.exe").start();
        p.waitFor();

        rangaElapsedTime = System.currentTimeMillis()- rangaElapsedTime;

        p.getInputStream().read(bytes);
        p.getErrorStream().read(errorBytes);
        String s= new String(bytes);
        String se= new String(errorBytes);
        p.destroy();


        GraphPath<BaseVertex,BaseEdge> pathRanga = RangaOutputParser.getOptimumPath(g, "optpath.txt");

        String outputRanga = map + " NC: "+neutroCost+" IDC: NA  K: "+ K  + " Cost: " + nf.format(pathRanga.getWeight()) + " RP_RANGA      RPC: NA" +" Elapsed Time : "+ rangaElapsedTime;

        g.pathList.add(new Path(pathRanga,new Pen(Color.black,Pen.PenStyle.PS_Normal)));
        TestUtil.writeOutput(outputFile,outputRanga);
    }

    public static void performKSPExperiment(String outputFile,String map,BaseDirectedGraph g,BaseVertex start,BaseVertex end,double neutroCost, int K) throws IOException, InterruptedException, InstantiationException {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        KSPNA kspna = new KSPNA();
        GraphPath<BaseVertex,BaseEdge> pathKSPNA = kspna.perform(g,start,end,K);
        String outputKSPNA = map + " NC: "+neutroCost+ " IDC: NA  K: "+ K  + " Cost: " + nf.format(pathKSPNA.getWeight()) + " RP_KSP        RPC: " + kspna.returnPathCount+" Elapsed Time : "+ kspna.elapsedTime;

        TestUtil.writeOutput(outputFile,outputKSPNA);

        g.pathList.add(new Path(pathKSPNA,new Pen(Color.green,Pen.PenStyle.PS_Normal)));
    }

    public static void performLowerBoundExperiment(String outputFile,String map,BaseDirectedGraph g,BaseVertex start,BaseVertex end, double neutroCost ,int K) throws IOException, InterruptedException, InstantiationException {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        g.setObstacleCost(neutroCost);

        ExactPSA escnh = new ExactPSA(neutroCost,0.5);
        escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromLowerBound;

        GraphPath<BaseVertex,BaseEdge> pathLB = escnh.perform(g,start,end,K);
        long rpcLB = escnh.returnPathCount;
        long elapsedTimeLB = escnh.elapsedTime;
        int neutroCountLB= escnh.neutroCount;

        String outputLB = map + " NC: "+neutroCost+ " IDC: "+ neutroCountLB+ " K: "+ K  + " Cost: " + nf.format(pathLB.getWeight()) +       " RP_LowerBound RPC: " + rpcLB+" Elapsed Time : "+ elapsedTimeLB;

        TestUtil.writeOutput(outputFile,outputLB);

        g.pathList.add(new Path(pathLB,new Pen(Color.red,Pen.PenStyle.PS_Normal)));

    }

    public static void performUpperBoundExperiment(String outputFile,String map,BaseDirectedGraph g,BaseVertex start,BaseVertex end, double neutroCost ,int K) throws IOException, InterruptedException, InstantiationException {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        g.setObstacleCost(neutroCost);

        ExactPSA escnh = new ExactPSA(neutroCost,0.5);
        escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;

        GraphPath<BaseVertex,BaseEdge> pathUB = escnh.perform(g,start,end,K);
        long rpcUB = escnh.returnPathCount;
        long elapsedTimeUB = escnh.elapsedTime;
        int neutroCountUB= escnh.neutroCount;

        String outputUB = map+ " NC: "+neutroCost + " IDC: "+ neutroCountUB+ " K: "+ K  + " Cost: " + nf.format(pathUB.getWeight()) +       " RP_UpperBound RPC: " + rpcUB+" Elapsed Time : "+ elapsedTimeUB;

        TestUtil.writeOutput(outputFile,outputUB);

        g.pathList.add(new Path(pathUB,new Pen(Color.red,Pen.PenStyle.PS_Normal)));

    }

    public static void performPSAExperiment(String outputFile,String map,BaseDirectedGraph g,BaseVertex start,BaseVertex end, double neutroCost ,int K) throws IOException, InterruptedException, InstantiationException {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        g.setObstacleCost(neutroCost);

        PSA psa = new PSA(neutroCost);


        long startTime = System.currentTimeMillis();

        GraphPath<BaseVertex,BaseEdge> path = psa.perform(g,start,end,K);



        double elapsedTime = System.currentTimeMillis()- startTime;

        int solutionIDC= psa.solutionIDC;

        String outputUB = map+ " NeutroCost: "+neutroCost + " IDC: "+ solutionIDC+ " K: "+ K  + " Alpha: " +psa.finalAlpha + " Cost: " + nf.format(path.getWeight()) + " DijkstraCallCount: "+ psa.dijkstraCallCount +" Elapsed Time : "+ elapsedTime;

        TestUtil.writeOutput(outputFile,outputUB);

        g.pathList.add(new Path(path,new Pen(Color.red,Pen.PenStyle.PS_Normal)));

    }


    public static void performPSAExperiment(String outputFile,String map,BaseDirectedGraph g,BaseVertex start,BaseVertex end, double neutroCost ,int K,double epsilon) throws IOException, InterruptedException, InstantiationException {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(5);

        g.setObstacleCost(neutroCost);

        PSA psa = new PSA(neutroCost,epsilon);


        long startTime = System.currentTimeMillis();

        GraphPath<BaseVertex,BaseEdge> path = psa.perform(g,start,end,K);

        double elapsedTime = System.currentTimeMillis()- startTime;

        int solutionIDC= psa.solutionIDC;

        String output = map+ " NeutroCost: "+StringUtil.padLeft(nf.format(neutroCost),7) + " IDC: "+ solutionIDC+ " K: "+ K  + " Alpha: " +StringUtil.padLeft(nf.format(psa.finalAlpha),7) + " Cost: " + StringUtil.padLeft(nf.format(path.getWeight()),7) + " DijkstraCallCount: "+ StringUtil.padLeft(psa.dijkstraCallCount+"",4) +" Elapsed Time : "+ elapsedTime + " EPSILON: " + epsilon;

        TestUtil.writeOutput(outputFile,output);

        g.pathList.add(new Path(path,new Pen(Color.red,Pen.PenStyle.PS_Normal)));

    }


    public static void performExperiment(String map,String outputFile, int xSize,int ySize,int unitEdgeLength,double[] neutroCostSet,int[] K_Set, GridPosition start, GridPosition end, int obstacleCount,int  algorithm) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        double neutroCost= 0;
        int K = 0;

        GridDirectedGraph gg = GraphFactory.createGrid(map, obstacleCount, start.getX(),start.getY(), end.getX(), end.getY(), 1, xSize, ySize,unitEdgeLength);

        map = map + " "+xSize+"X"+ ySize;

        for (int nc = 0;nc<neutroCostSet.length;nc++)
        {
            for (int k = 0;k<K_Set.length;k++)
            {
                neutroCost = neutroCostSet[nc];
                K = K_Set[k];

                gg.setObstacleCost(neutroCost);

                performExperiment(map,outputFile,gg,neutroCost,K,obstacleCount,(byte)algorithm);

                gg.pathList.clear();

            }
        }
    }


    public static void performExperiment(String map,String outputFile,GridDirectedGraph gg, double neutroCost,int K,  int obstacleCount,byte algorithm) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        System.out.println(">> EXPERIMENT-1 START  map: " + map + "  K: "+ K);


        IdManager.reset();


        if  ((algorithm & TestUtil.A_RANGA) != 0)
        {
            TestUtil.performRangaExperiment(outputFile, map, gg, gg.start, gg.end, neutroCost, K);
            gg.setObstacleCost(neutroCost);
        }

        if  ((algorithm & TestUtil.A_PSA) != 0)
        {
            TestUtil.performPSAExperiment(outputFile, map, gg, gg.start, gg.end, neutroCost, K);
            gg.setObstacleCost(neutroCost);
        }

        if  ((algorithm & TestUtil.A_KSP) != 0)
        {
            TestUtil.performKSPExperiment(outputFile, map, gg, gg.start, gg.end, neutroCost, K);
            gg.setObstacleCost(neutroCost);
        }


        if  ((algorithm & TestUtil.A_LOWERBOUND) != 0)
        {
            TestUtil.performLowerBoundExperiment(outputFile, map, gg, gg.start, gg.end, neutroCost, K);
            gg.setObstacleCost(neutroCost);
        }

        if  ((algorithm & TestUtil.A_UPPERBOUND) != 0)
        {
            TestUtil.performUpperBoundExperiment(outputFile, map, gg, gg.start, gg.end, neutroCost, K);
        }


        if (!workSilent)
            GraphViewer.showGraph(gg, GraphViewer.GraphType.GT_BASIC, 0, 100, 0, 100);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-1 END  map: " + map + "  K: "+ K);

    }


    public static void performExperiment(String map,String outputFile,GridDirectedGraph gg, double neutroCost,int K,  int obstacleCount,double epsilon,byte algorithm) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        System.out.println(">> EXPERIMENT-1 START  map: " + map + "  K: "+ K);


        IdManager.reset();


        if  ((algorithm & TestUtil.A_RANGA) != 0)
        {
            TestUtil.performRangaExperiment(outputFile, map, gg, gg.start, gg.end, neutroCost, K);
            gg.setObstacleCost(neutroCost);
        }

        if  ((algorithm & TestUtil.A_PSA) != 0)
        {
            TestUtil.performPSAExperiment(outputFile, map, gg, gg.start, gg.end, neutroCost, K,epsilon);
            gg.setObstacleCost(neutroCost);
        }

        if  ((algorithm & TestUtil.A_KSP) != 0)
        {
            TestUtil.performKSPExperiment(outputFile, map, gg, gg.start, gg.end, neutroCost, K);
            gg.setObstacleCost(neutroCost);
        }


        if  ((algorithm & TestUtil.A_LOWERBOUND) != 0)
        {
            TestUtil.performLowerBoundExperiment(outputFile, map, gg, gg.start, gg.end, neutroCost, K);
            gg.setObstacleCost(neutroCost);
        }

        if  ((algorithm & TestUtil.A_UPPERBOUND) != 0)
        {
            TestUtil.performUpperBoundExperiment(outputFile, map, gg, gg.start, gg.end, neutroCost, K);
        }


        if (!workSilent)
            GraphViewer.showGraph(gg, GraphViewer.GraphType.GT_BASIC, 0, 100, 0, 100);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-1 END  map: " + map + "  K: "+ K);

    }



}
