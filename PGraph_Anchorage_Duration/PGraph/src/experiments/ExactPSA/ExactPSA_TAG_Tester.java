package experiments.ExactPSA;

import org.jgrapht.GraphPath;
import pgraph.Path;
import pgraph.alg.ExactPSA;
import pgraph.alg.KSPNA;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridPosition;
import pgraph.gui.GraphViewer;
import pgraph.ranga.RangaInputPreparer;
import pgraph.ranga.RangaOutputParser;
import pgraph.tag.TangentArcDirectedGraph;
import pgraph.util.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.NumberFormat;

/**
 * Created with IntelliJ IDEA.
 * User: oz
 * Date: 21.04.2013
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public class ExactPSA_TAG_Tester {

    static boolean workSilent = true;

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {

        workSilent = true;

      //  testDEBUG();

        test4();

      //  performExperiment1("maps/random1.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);


    //    performExperiment1("maps/random4.txt","outputTAG_deneme.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
     //   performExperiment1("maps/random4.txt","outputTAG_deneme.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
      //  performExperiment1("maps/random4.txt","outputTAG_deneme.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
       // performExperiment1("maps/random4.txt","outputTAG_deneme.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
 //        testBatch_6();
//        testBatch_1();
 //       testBatch_2();
 //       testBatch_3();
 //       testBatch_4();
  //      testBatch_5();
      //  testBatch_Cobra();
        //testVertexOptimizations();


     }

    public static void testDEBUG() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException, IOException {
        workSilent = false;

        String outputFile_10 = "outputTAG_DEBUG.txt" ;
        String mapFile = "maps/random4.txt";
        performExperiment3(mapFile,outputFile_10,1,5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);

    }


    public static void testBatch_1() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        performExperiment1("maps/random4.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);

        performExperiment1("maps/random1.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);

        performExperiment1("maps/random6.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
        performExperiment1("maps/random7.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
        performExperiment1("maps/random8.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
        performExperiment1("maps/random9.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
        performExperiment1("maps/random10.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
        performExperiment1("maps/random12.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
        performExperiment1("maps/random19.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
        performExperiment1("maps/random22.txt","outputTAG.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);





    }


    public static void testVertexOptimizations() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        MathUtil.MIN_POINT_DIFFERENCE =1;
        performExperimentVertexOpt("maps/random4.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.1;
        performExperimentVertexOpt("maps/random4.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.01;
        performExperimentVertexOpt("maps/random4.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.001;
        performExperimentVertexOpt("maps/random4.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);


        MathUtil.MIN_POINT_DIFFERENCE =1;
        performExperimentVertexOpt("maps/random1.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.1;
        performExperimentVertexOpt("maps/random1.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.01;
        performExperimentVertexOpt("maps/random1.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.001;
        performExperimentVertexOpt("maps/random1.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);

        MathUtil.MIN_POINT_DIFFERENCE =1;
        performExperimentVertexOpt("maps/random6.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.1;
        performExperimentVertexOpt("maps/random6.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.01;
        performExperimentVertexOpt("maps/random6.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.001;
        performExperimentVertexOpt("maps/random6.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);


        MathUtil.MIN_POINT_DIFFERENCE =1;
        performExperimentVertexOpt("maps/random7.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.1;
        performExperimentVertexOpt("maps/random7.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.01;
        performExperimentVertexOpt("maps/random7.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.001;
        performExperimentVertexOpt("maps/random7.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);

        MathUtil.MIN_POINT_DIFFERENCE =1;
        performExperimentVertexOpt("maps/random8.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.1;
        performExperimentVertexOpt("maps/random8.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.01;
        performExperimentVertexOpt("maps/random8.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.001;
        performExperimentVertexOpt("maps/random8.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);

        MathUtil.MIN_POINT_DIFFERENCE =1;
        performExperimentVertexOpt("maps/random9.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.1;
        performExperimentVertexOpt("maps/random9.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.01;
        performExperimentVertexOpt("maps/random9.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.001;
        performExperimentVertexOpt("maps/random9.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);

        MathUtil.MIN_POINT_DIFFERENCE =1;
        performExperimentVertexOpt("maps/random10.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.1;
        performExperimentVertexOpt("maps/random10.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.01;
        performExperimentVertexOpt("maps/random10.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);
        MathUtil.MIN_POINT_DIFFERENCE =0.001;
        performExperimentVertexOpt("maps/random10.txt", "outputTAG_VertexOpt.txt", 5, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100);

    }



    public static void testBatch_2() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        String outputFile = "outputTAG_testBatch_1_100.txt" ;
        for (int mapIndex=1; mapIndex<101; mapIndex++)
        {
            String mapFile = "maps/random"+mapIndex+".txt";
            for (int kIndex =1; kIndex<10; kIndex++)
            {
                performExperiment3(mapFile,outputFile,1,kIndex,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
            }
        }
    }


    public static void testBatch_1_100_With_Cost() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        String outputFile_1 = "outputTAG_testBatch_1_100_NC_1.txt" ;
        String outputFile_2 = "outputTAG_testBatch_1_100_NC_2.txt" ;
        String outputFile_3 = "outputTAG_testBatch_1_100_NC_3.txt" ;
        String outputFile_4 = "outputTAG_testBatch_1_100_NC_4.txt" ;
        String outputFile_5 = "outputTAG_testBatch_1_100_NC_5.txt" ;
        String outputFile_10 = "outputTAG_testBatch_1_100_NC_10.txt" ;
        String outputFile_20 = "outputTAG_testBatch_1_100_NC_20.txt" ;

        for (int mapIndex=1; mapIndex<101; mapIndex++)
        {
            String mapFile = "maps/random"+mapIndex+".txt";

            performExperiment3(mapFile,outputFile_1,1,5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
            performExperiment3(mapFile,outputFile_2,2,5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
            performExperiment3(mapFile,outputFile_3,3,5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
            performExperiment3(mapFile,outputFile_4,4,5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
            performExperiment3(mapFile,outputFile_5,5,5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
            performExperiment3(mapFile,outputFile_10,10,5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
            performExperiment3(mapFile,outputFile_20,20,5,new Point2D.Double(50,100),new Point2D.Double(50,1),100);
        }
    }

    public static void testBatch_6() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        String outputFile = "outputTAG_testBatch_100_200.txt" ;
        for (int mapIndex=101; mapIndex<201; mapIndex++)
        {
            String mapFile = "maps/random"+mapIndex+".txt";
            for (int kIndex =1; kIndex<10; kIndex++)
            {
                performExperiment3(mapFile,outputFile,1,kIndex,new Point2D.Double(50,100),new Point2D.Double(50,1),1000);
            }
        }
    }


    public static void testBatch_3() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        String outputFile = "outputTAG_testBatch_500_600.txt" ;
        for (int mapIndex=501; mapIndex<600; mapIndex++)
        {
            String mapFile = "maps/random"+mapIndex+".txt";
            for (int kIndex =1; kIndex<10; kIndex++)
            {
                performExperiment3(mapFile,outputFile,1,kIndex,new Point2D.Double(50,100),new Point2D.Double(50,1),200);
            }
        }
    }

    public static void testBatch_4() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        String outputFile = "outputTAG_testBatch_600_700.txt" ;
        for (int mapIndex=601; mapIndex<700; mapIndex++)
        {
            String mapFile = "maps/random"+mapIndex+".txt";
            for (int kIndex =1; kIndex<10; kIndex++)
            {
                performExperiment3(mapFile,outputFile,1,kIndex,new Point2D.Double(50,100),new Point2D.Double(50,1),300);
            }
        }
    }

    public static void testBatch_5() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        String outputFile = "outputTAG_testBatch_700_800.txt" ;
        for (int mapIndex=701; mapIndex<800; mapIndex++)
        {
            String mapFile = "maps/random"+mapIndex+".txt";
            for (int kIndex =1; kIndex<10; kIndex++)
            {
                performExperiment3(mapFile,outputFile,1,kIndex,new Point2D.Double(50,100),new Point2D.Double(50,1),400);
            }
        }
    }


    public static void testBatch_Cobra() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        String outputFile_10 = "outputTAG_testBatch_COBRA_NC10.txt" ;
        String outputFile_20 = "outputTAG_testBatch_COBRA_NC20.txt" ;
        String outputFile_50 = "outputTAG_testBatch_COBRA_NC50.txt" ;
        String outputFile_100 = "outputTAG_testBatch_COBRA_NC100.txt" ;

        String mapFile = "maps/cobraor.txt";
        for (int kIndex =0; kIndex<4; kIndex++)
        {
            performExperiment3(mapFile,outputFile_10,10,kIndex,new Point2D.Double(530,760),new Point2D.Double(530,60),39);
            performExperiment3(mapFile,outputFile_20,20,kIndex,new Point2D.Double(530,760),new Point2D.Double(530,60),39);
            performExperiment3(mapFile,outputFile_50,50,kIndex,new Point2D.Double(530,760),new Point2D.Double(530,60),39);
            performExperiment3(mapFile,outputFile_100,100,kIndex,new Point2D.Double(530,760),new Point2D.Double(530,60),39);
        }

    }


    private static void performExperiment1(String map,String outputFile, int K, Point2D.Double start, Point2D.Double end, int obstacleCount) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        System.out.println(">> EXPERIMENT-1 START  map: " + map + "  K: "+ K);

        IdManager.reset();

        TangentArcDirectedGraph tag1 = GraphFactory.createTAG(map,obstacleCount,start,end);

        TangentArcDirectedGraph tag2 = GraphFactory.createTAG(map,obstacleCount,start,end);

        tag1.setObstacleCost(1);

        tag1.setObstacleCost(1);
        tag2.setObstacleCost(1);

        RangaInputPreparer.createEdgetData("EdgeData.txt", tag1);
        RangaInputPreparer.createPathVars("PathVars.txt", tag1, tag1.start, tag1.end,K);

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


        GraphPath<BaseVertex,BaseEdge> pathRanga = RangaOutputParser.getOptimumPath(tag1, "optpath.txt");

        ExactPSA epsa = new ExactPSA(1,0.5);

        KSPNA kspna = new KSPNA();



        epsa.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromLowerBound;

        GraphPath<BaseVertex,BaseEdge> pathLB = epsa.perform(tag1,tag1.start,tag1.end,K);
        long rpcLB = epsa.returnPathCount;
        long elapsedTimeLB = epsa.elapsedTime;
        int neutroCountLB= epsa.neutroCount;

        tag1.setObstacleCost(1);
        epsa.returnPolicy =   ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;

        GraphPath<BaseVertex,BaseEdge> pathUB = epsa.perform(tag1,tag1.start,tag1.end,K);
        long elapsedTimeUB = epsa.elapsedTime;

        GraphPath<BaseVertex,BaseEdge> pathKSPNA = kspna.perform(tag2,tag2.start,tag2.end,K);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        String outputLB = map + " IDC: "+ neutroCountLB+ " K: "+ K  + " Cost: " + nf.format(pathLB.getWeight()) +       " RP_LowerBound RPC: " + rpcLB+" Elapsed Time : "+ elapsedTimeLB;
        String outputUB = map + " IDC: "+ epsa.neutroCount+" K: "+ K  + " Cost: " + nf.format(pathUB.getWeight()) +       " RP_UpperBound RPC: " + epsa.returnPathCount+" Elapsed Time : "+ elapsedTimeUB;

        String outputKSPNA = map + " IDC: NA  K: "+ K  + " Cost: " + nf.format(pathKSPNA.getWeight()) + " RP_KSP        RPC: " + kspna.returnPathCount+" Elapsed Time : "+ kspna.elapsedTime;
        String outputRanga = map + " IDC: NA  K: "+ K  + " Cost: " + nf.format(pathRanga.getWeight()) + " RP_RANGA      RPC: NA" +" Elapsed Time : "+ rangaElapsedTime;



        TestUtil.writeOutput(outputFile,outputLB);
        TestUtil.writeOutput(outputFile,outputUB);
        TestUtil.writeOutput(outputFile,outputKSPNA);
        TestUtil.writeOutput(outputFile,outputRanga);


        tag1.pathList.add(new Path(pathLB,new Pen(Color.red,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathUB,new Pen(Color.blue,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathKSPNA,new Pen(Color.green,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathRanga,new Pen(Color.black,Pen.PenStyle.PS_Normal)));

        if (!workSilent)
            GraphViewer.showGraph(tag1, GraphViewer.GraphType.GT_BASIC,0,100,0,100);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-1 END  map: " + map + "  K: "+ K);

    }

    private static void performExperiment3(String map,String outputFile,double neutrocost, int K, Point2D.Double start, Point2D.Double end,int obstacleCount) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        IdManager.reset();

        System.out.println(">> EXPERIMENT-3 START  map: " + map + "  K: "+ K + " NeutroCost: "+ neutrocost);

        TangentArcDirectedGraph tag1 = GraphFactory.createTAG(map,obstacleCount,start,end);

        tag1.setObstacleCost(neutrocost);


        RangaInputPreparer.createEdgetData("EdgeData.txt", tag1);
        RangaInputPreparer.createPathVars("PathVars.txt", tag1, tag1.start, tag1.end,K);

        byte[] bytes = new byte[300];
        byte[] errorBytes = new byte[300];

        long rangaElapsedTime = System.currentTimeMillis();

        Process p = new ProcessBuilder("rangaInputs/WCSPP.exe").start();
        p.waitFor();

        rangaElapsedTime = System.currentTimeMillis()-rangaElapsedTime;

        p.getInputStream().read(bytes);
        p.getErrorStream().read(errorBytes);
        String s= new String(bytes);
        String se= new String(errorBytes);
        p.destroy();


        GraphPath<BaseVertex,BaseEdge> pathRanga = RangaOutputParser.getOptimumPath(tag1, "optpath.txt");

        ExactPSA epsa = new ExactPSA(neutrocost,0.5);


        epsa.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromLowerBound;

        GraphPath<BaseVertex,BaseEdge> pathLB = epsa.perform(tag1,tag1.start,tag1.end,K);
        long rpcLB = epsa.returnPathCount;
        long elapsedTimeLB = epsa.elapsedTime;
        int neutroCountLB = epsa.neutroCount;

        tag1.setObstacleCost(neutrocost);
        epsa.returnPolicy =   ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;

        GraphPath<BaseVertex,BaseEdge> pathUB = epsa.perform(tag1,tag1.start,tag1.end,K);
        long elapsedTimeUB = epsa.elapsedTime;

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        String outputLB = map + " IDC: "+neutroCountLB +" K: "+ K + " Cost: " + nf.format(pathLB.getWeight()) +       " RP_LowerBound RPC: " + rpcLB+" Elapsed Time : "+ elapsedTimeLB;
        String outputUB = map + " IDC: "+epsa.neutroCount+ " K: "+ K  + " Cost: " + nf.format(pathUB.getWeight()) +       " RP_UpperBound RPC: " + epsa.returnPathCount+" Elapsed Time : "+ elapsedTimeUB;

        String outputRanga = map +" IDC: NA"   +" K: "+ K  + " Cost: " + nf.format(pathRanga.getWeight()) + " RP_RANGA      RPC: NA"+" Elapsed Time : "+rangaElapsedTime;



        TestUtil.writeOutput(outputFile,outputLB);
        TestUtil.writeOutput(outputFile,outputUB);
        TestUtil.writeOutput(outputFile, outputRanga);


        tag1.pathList.add(new Path(pathLB,new Pen(Color.red,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathUB,new Pen(Color.blue,Pen.PenStyle.PS_Normal)));

        tag1.pathList.add(new Path(pathRanga,new Pen(Color.green,Pen.PenStyle.PS_Normal)));

        if (!workSilent)
            GraphViewer.showGraph(tag1, GraphViewer.GraphType.GT_BASIC,0,100,0,100);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-3 END  map: " + map + "  K: "+ K);

    }


    private static void performExperimentVertexOpt(String map,String outputFile, int K, Point2D.Double start, Point2D.Double end,int obstacleCount) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        IdManager.reset();

        System.out.println(">> EXPERIMENT-3 START  map: " + map + "  K: "+ K);

        TangentArcDirectedGraph tag1 = GraphFactory.createTAG(map,obstacleCount,start,end);

        tag1.setObstacleCost(1);


        ExactPSA epsa = new ExactPSA(1,0.5);


        epsa.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromLowerBound;

        GraphPath<BaseVertex,BaseEdge> pathLB = epsa.perform(tag1,tag1.start,tag1.end,K);
        long rpcLB = epsa.returnPathCount;
        long elapsedTimeLB = epsa.elapsedTime;


        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        String outputLB = map +" POINT_THREHOLD: "+ MathUtil.MIN_POINT_DIFFERENCE + "IDC: " + epsa.neutroCount +  " K: "+ K + " Cost: " + nf.format(pathLB.getWeight()) +       " RP_LowerBound RPC: " + rpcLB+" Elapsed Time : "+ elapsedTimeLB;
        //String outputRanga = map + " K: "+ K  + " Cost: " + nf.format(pathRanga.getWeight()) + " RP_RANGA      RPC: NA"+" Elapsed Time : "+rangaElapsedTime;



        TestUtil.writeOutput(outputFile,outputLB);
        //TestUtil.writeOutput(outputFile, outputRanga);


        tag1.pathList.add(new Path(pathLB,new Pen(Color.red,Pen.PenStyle.PS_Normal)));


        //tag1.pathList.add(new Path(pathRanga,new Pen(Color.green,Pen.PenStyle.PS_Normal)));

        if (!workSilent)
            GraphViewer.showGraph(tag1, GraphViewer.GraphType.GT_BASIC,0,100,0,100);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-VERTEXOPT END  map: " + map + "  K: "+ K);

    }


    private static void performRangaExperiment(String outputFile,String map,BaseDirectedGraph g,BaseVertex start,BaseVertex end,double neutroCost, int K) throws IOException, InterruptedException, InstantiationException {

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

    private static void performKSPExperiment(String outputFile,String map,BaseDirectedGraph g,BaseVertex start,BaseVertex end,double neutroCost, int K) throws IOException, InterruptedException, InstantiationException {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        KSPNA kspna = new KSPNA();
        GraphPath<BaseVertex,BaseEdge> pathKSPNA = kspna.perform(g,start,end,K);
        String outputKSPNA = map + " NC: "+neutroCost+ " IDC: NA  K: "+ K  + " Cost: " + nf.format(pathKSPNA.getWeight()) + " RP_KSP        RPC: " + kspna.returnPathCount+" Elapsed Time : "+ kspna.elapsedTime;

        TestUtil.writeOutput(outputFile,outputKSPNA);

        g.pathList.add(new Path(pathKSPNA,new Pen(Color.green,Pen.PenStyle.PS_Normal)));
    }

    private static void performLowerBoundExperiment(String outputFile,String map,BaseDirectedGraph g,BaseVertex start,BaseVertex end, double neutroCost ,int K) throws IOException, InterruptedException, InstantiationException {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        g.setObstacleCost(neutroCost);

        ExactPSA escnh = new ExactPSA(neutroCost,0.5);
        escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromLowerBound;

        GraphPath<BaseVertex,BaseEdge> pathLB = escnh.perform(g,start,end,K);
        GraphPath<BaseVertex,BaseEdge> psaPath = escnh.bisectionPath;
        long rpcLB = escnh.returnPathCount;
        long elapsedTimeLB = escnh.elapsedTime;
        int neutroCountLB= escnh.neutroCount;

        String outputLB = map + " NC: "+neutroCost+ " IDC: "+ neutroCountLB+ " K: "+ K  + " Cost: " + nf.format(pathLB.getWeight()) +  " PSA_Cost: "+  nf.format(psaPath.getWeight()) +   " RP_LowerBound RPC: " + rpcLB+" Elapsed Time : "+ elapsedTimeLB;

        TestUtil.writeOutput(outputFile,outputLB);

        g.pathList.add(new Path(pathLB,new Pen(Color.red,Pen.PenStyle.PS_Normal)));

    }

    private static void performUpperBoundExperiment(String outputFile,String map,BaseDirectedGraph g,BaseVertex start,BaseVertex end, double neutroCost ,int K) throws IOException, InterruptedException, InstantiationException {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        g.setObstacleCost(neutroCost);

        ExactPSA escnh = new ExactPSA(neutroCost,0.5);
        escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;

        GraphPath<BaseVertex,BaseEdge> pathUB = escnh.perform(g,start,end,K);
        GraphPath<BaseVertex,BaseEdge> psaPath = escnh.bisectionPath;
        long rpcUB = escnh.returnPathCount;
        long elapsedTimeUB = escnh.elapsedTime;
        int neutroCountUB= escnh.neutroCount;

        String outputUB = map+ " NC: "+neutroCost + " IDC: "+ neutroCountUB+ " K: "+ K  + " Cost: " + nf.format(pathUB.getWeight()) +   " PSA_Cost: "+  nf.format(psaPath.getWeight()) +      " RP_UpperBound RPC: " + rpcUB+" Time : "+ elapsedTimeUB + " PSA_Time : " + escnh.elapsedTimePSA;

        TestUtil.writeOutput(outputFile,outputUB);

        g.pathList.add(new Path(pathUB,new Pen(Color.red,Pen.PenStyle.PS_Normal)));

    }

    private static void performExperiment(String map,String outputFile,TangentArcDirectedGraph tag, double neutroCost,int K, Point2D.Double start, Point2D.Double end, int obstacleCount,byte algorithm) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        System.out.println(">> EXPERIMENT-1 START  map: " + map + "  K: "+ K);


        IdManager.reset();


        if  ((algorithm & TestUtil.A_RANGA) != 0)
        {
            performRangaExperiment(outputFile,map,tag,tag.start,tag.end,neutroCost,K);
            tag.setObstacleCost(neutroCost);
        }


        if  ((algorithm & TestUtil.A_KSP) != 0)
        {
            performKSPExperiment(outputFile,map,tag,tag.start,tag.end,neutroCost,K);
            tag.setObstacleCost(neutroCost);
        }


        if  ((algorithm & TestUtil.A_LOWERBOUND) != 0)
        {
            performLowerBoundExperiment(outputFile,map,tag,tag.start,tag.end,neutroCost,K);
            tag.setObstacleCost(neutroCost);
        }

        if  ((algorithm & TestUtil.A_UPPERBOUND) != 0)
        {
            performUpperBoundExperiment(outputFile,map,tag,tag.start,tag.end,neutroCost,K);
        }


        if (!workSilent)
            GraphViewer.showGraph(tag, GraphViewer.GraphType.GT_BASIC,0,100,0,100);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-1 END  map: " + map + "  K: "+ K);

    }

    private static void performExperiment(String map,String outputFile, double[] neutroCostSet,int[] K_Set, Point2D.Double start, Point2D.Double end, int obstacleCount,int  algorithm) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        double neutroCost= 0;
        int K = 0;

        TangentArcDirectedGraph tag = GraphFactory.createTAG(map,obstacleCount,start,end);

        for (int nc = 0;nc<neutroCostSet.length;nc++)
        {
            for (int k = 0;k<K_Set.length;k++)
            {
                neutroCost = neutroCostSet[nc];
                K = K_Set[k];

                tag.setObstacleCost(neutroCost);

                performExperiment(map,outputFile,tag,neutroCost,K,start,end,obstacleCount,(byte)algorithm);

                tag.pathList.clear();

            }
        }
    }

    private static void test1() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException, IOException {
        String outputFile = "outputTAG_test1.txt" ;
        for (int mapIndex=501; mapIndex<511; mapIndex++)
        {
            String mapFile = "maps/random"+mapIndex+".txt";

            performExperiment(mapFile, outputFile, new double[]{0.1, 0.5,1,2,5}, new int[]{1, 2,3,4,5,6}, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 200, TestUtil.A_RANGA|TestUtil.A_UPPERBOUND);

        }
    }
    private static void test4() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException, IOException {
        String outputFile1 = "outputTAG_test4_1.txt" ;
        String outputFile2 = "outputTAG_test4_2.txt" ;
        for (int mapIndex=1; mapIndex<51; mapIndex++)
        {
            String mapFile = "maps/random"+mapIndex+".txt";
           // String mapFile = "maps/random9.txt";
            //performExperiment(mapFile, outputFile, new double[]{0.5}, new int[]{1, 2,3,4,5,6,7,8,9}, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100, TestUtil.A_UPPERBOUND);
            //performExperiment(mapFile, outputFile1, new double[]{1}, new int[]{2}, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100, TestUtil.A_UPPERBOUND);
            performExperiment(mapFile, outputFile1, new double[]{1}, new int[]{2}, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100, TestUtil.A_UPPERBOUND);
            performExperiment(mapFile, outputFile2, new double[]{1}, new int[]{2}, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100, TestUtil.A_UPPERBOUND);
            //performExperiment(mapFile, outputFile2, new double[]{1}, new int[]{2}, new Point2D.Double(50, 100), new Point2D.Double(50, 1), 100, TestUtil.A_UPPERBOUND);
        }
    }
}
