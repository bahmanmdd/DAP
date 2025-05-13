package experiments.ExactPSA;

import org.jgrapht.GraphPath;
import pgraph.Path;
import pgraph.alg.ExactPSA;
import pgraph.alg.KSPNA;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridPosition;
import pgraph.gui.GraphViewer;
import pgraph.ranga.RangaInputPreparer;
import pgraph.ranga.RangaOutputParser;
import pgraph.util.GraphFactory;
import pgraph.util.IdManager;
import pgraph.util.Pen;
import pgraph.util.TestUtil;

import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;

/**
 * Created with IntelliJ IDEA.
 * User: oz
 * Date: 21.04.2013
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public class ExactPSA_GRID_Tester {

    static boolean workSilent = false;

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {

        workSilent = true;

      //  performExperiment3("maps/random4.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);

        test5();

        //testBatch_1();
        //testBatch_2();
     }


    public static void testBatch_3() throws ClassNotFoundException, InstantiationException, IllegalAccessException, InterruptedException, IOException {
        performExperiment1("maps/random6.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);
    }

    public static void testBatch_1() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        performExperiment1("maps/random22.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);

        performExperiment1("maps/random1.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);
        performExperiment1("maps/random4.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);
        performExperiment1("maps/random6.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);
        performExperiment1("maps/random7.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);
        performExperiment1("maps/random8.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);
        performExperiment1("maps/random9.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);
        performExperiment1("maps/random10.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);
        performExperiment1("maps/random12.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);
        performExperiment1("maps/random19.txt","output.txt",10,5,new GridPosition(5,10),new GridPosition(5,0),11,11,100);


        /**
         * 20*20
         */

        performExperiment3("maps/random1.txt","output.txt",5,5,new GridPosition(10,20),new GridPosition(10,0),21,21,100);
        performExperiment3("maps/random4.txt","output.txt",5,5,new GridPosition(10,20),new GridPosition(10,0),21,21,100);
        performExperiment3("maps/random6.txt","output.txt",5,5,new GridPosition(10,20),new GridPosition(10,0),21,21,100);
        performExperiment3("maps/random7.txt","output.txt",5,5,new GridPosition(10,20),new GridPosition(10,0),21,21,100);
        performExperiment3("maps/random8.txt","output.txt",5,5,new GridPosition(10,20),new GridPosition(10,0),21,21,100);
        performExperiment3("maps/random9.txt","output.txt",5,5,new GridPosition(10,20),new GridPosition(10,0),21,21,100);
        performExperiment3("maps/random10.txt","output.txt",5,5,new GridPosition(10,20),new GridPosition(10,0),21,21,100);
        performExperiment3("maps/random12.txt","output.txt",5,5,new GridPosition(10,20),new GridPosition(10,0),21,21,100);
        performExperiment3("maps/random19.txt","output.txt",5,5,new GridPosition(10,20),new GridPosition(10,0),21,21,100);
        performExperiment3("maps/random22.txt","output.txt",5,5,new GridPosition(10,20),new GridPosition(10,0),21,21,100);

        /**
         * 50*50
         */

        performExperiment3("maps/random1.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);
        performExperiment3("maps/random4.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);
        performExperiment3("maps/random6.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);
        performExperiment3("maps/random7.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);
        performExperiment3("maps/random8.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);
        performExperiment3("maps/random9.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);
        performExperiment3("maps/random10.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);
        performExperiment3("maps/random12.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);
        performExperiment3("maps/random19.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);
        performExperiment3("maps/random22.txt","output.txt",2,5,new GridPosition(25,50),new GridPosition(25,0),51,51,100);

        // performExperiment1("maps/random1.txt","output.txt",1,5,new GridPosition(50,100),new GridPosition(50,0),101,101,100);


        /**/

    }


    public static void testBatch_2() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        String outputFile = "output_testBatch2.txt" ;
        for (int mapIndex=1; mapIndex<101; mapIndex++)
        {
            String mapFile = "maps/random"+mapIndex+".txt";
            for (int kIndex =1; kIndex<10; kIndex++)
            {
                performExperiment3(mapFile,outputFile,10,kIndex,new GridPosition(5,10),new GridPosition(5,0),11,11,100);
            }
        }
    }


    public static GridDirectedGraph _create_Grid(String graphFile,int connectivityDegree) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        GridDirectedGraph gg= null;
        try {
            gg = new GridDirectedGraph(graphFile,connectivityDegree);
            System.out.println("Grid has been created. Performing PSA algorithm..");
            return gg;

        } catch (NumberFormatException | InstantiationException
                | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }



        /*
        PSA sgnh = new PSA(0.5,0.1);

        GraphPath<BaseVertex,BaseEdge> path1 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),0);
        GraphPath<BaseVertex,BaseEdge> path2 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),1);
        GraphPath<BaseVertex,BaseEdge> path3 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),3);
        GraphPath<BaseVertex,BaseEdge> path4 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),5);

        gg.pathList.add(new Path(path1,new Pen(Color.black,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new Path(path2,new Pen(Color.red,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new Path(path3,new Pen(Color.blue,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new Path(path4,new Pen(Color.green,Pen.PenStyle.PS_Normal)));
*/

    }




   private static void performExperiment2(String map,String outputFile, int unitEdgeLength,int K, GridPosition start, GridPosition end, int xSize, int ySize,int obstacleCount) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException
   {
       IdManager.reset();
       GridDirectedGraph gg1 = GraphFactory.createGrid(map, obstacleCount, start.getX(),start.getY(), end.getX(), end.getY(), 1, xSize, ySize,unitEdgeLength);


       gg1.setObstacleCost(1);


       RangaInputPreparer.createEdgetData("EdgeData.txt", gg1);
       RangaInputPreparer.createPathVars("PathVars.txt", gg1, gg1.start, gg1.end,K);

       byte[] bytes = new byte[300];
       byte[] errorBytes = new byte[300];
       // Process p = Runtime.getRuntime().exec("rangaInputs/WCSPP.exe");
       Process p = new ProcessBuilder("rangaInputs/WCSPP.exe").start();
       p.waitFor();
       p.getInputStream().read(bytes);
       p.getErrorStream().read(errorBytes);
       String s= new String(bytes);
       String se= new String(errorBytes);
       p.destroy();
       return;
       //
   }

    private static void performExperiment1(String map,String outputFile, int unitEdgeLength,int K, GridPosition start, GridPosition end, int xSize, int ySize,int obstacleCount) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        IdManager.reset();


        System.out.println(">> EXPERIMENT-1 START  map: " + map + " "+xSize+"X"+ ySize + " K: "+ K);

        GridDirectedGraph gg1 = GraphFactory.createGrid(map, obstacleCount, start.getX(),start.getY(), end.getX(), end.getY(), 1, xSize, ySize,unitEdgeLength);
        GridDirectedGraph gg2 = GraphFactory.createGrid(map, obstacleCount, start.getX(),start.getY(), end.getX(), end.getY(), 1, xSize, ySize,unitEdgeLength);

        gg1.setObstacleCost(1);
        gg2.setObstacleCost(1);

        RangaInputPreparer.createEdgetData("EdgeData.txt", gg1);
        RangaInputPreparer.createPathVars("PathVars.txt", gg1, gg1.start, gg1.end,K);

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


        GraphPath<BaseVertex,BaseEdge> pathRanga = RangaOutputParser.getOptimumPath(gg1, "optpath.txt");

        ExactPSA epsa = new ExactPSA(1,0.5);

        KSPNA kspna = new KSPNA();



        epsa.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromLowerBound;

        GraphPath<BaseVertex,BaseEdge> pathLB = epsa.perform(gg1,gg1.getStart(),gg1.getEnd(),K);
        long rpcLB = epsa.returnPathCount;
        long elapsedTimeLB = epsa.elapsedTime;

        gg1.setObstacleCost(1);
        epsa.returnPolicy =   ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;


        GraphPath<BaseVertex,BaseEdge> pathUB = epsa.perform(gg1,gg1.getStart(),gg1.getEnd(),K);

        long elapsedTimeUB = epsa.elapsedTime;

        GraphPath<BaseVertex,BaseEdge> pathKSPNA = kspna.perform(gg2,gg2.getStart(),gg2.getEnd(),K);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        String outputLB = map + " " + (xSize-1) + "x" + (xSize-1)+ " PathFound : "+epsa.pathFound+ " K: "+ K  + " Cost: " + nf.format(pathLB.getWeight()) +       " RP_LowerBound RPC: " + rpcLB+ " ElapsedTime: "+ elapsedTimeLB;
        String outputUB = map + " " + (xSize-1) + "x" + (xSize-1)+ " PathFound : "+epsa.pathFound+ " K: "+ K  + " Cost: " + nf.format(pathUB.getWeight()) +       " RP_UpperBound RPC: " + epsa.returnPathCount+ " ElapsedTime: "+ elapsedTimeUB;

        String outputKSPNA = map + " " + (xSize-1) + "x" + (xSize-1)+ " K: "+ K  + " Cost: " + nf.format(pathKSPNA.getWeight()) + " RP_KSP        RPC: " + kspna.returnPathCount+ " ElapsedTime: "+ kspna.elapsedTime;
        String outputRanga = map + " " + (xSize-1) + "x" + (xSize-1)+ " K: "+ K  + " Cost: " + nf.format(pathRanga.getWeight()) + " RP_RANGA      RPC: NA" +" Elapsed Time : "+ rangaElapsedTime;



        TestUtil.writeOutput(outputFile,outputLB);
        TestUtil.writeOutput(outputFile,outputUB);
        TestUtil.writeOutput(outputFile,outputKSPNA);
        TestUtil.writeOutput(outputFile,outputRanga);


        gg1.pathList.add(new Path(pathLB,new Pen(Color.red,Pen.PenStyle.PS_Normal)));

        if (!workSilent)
            GraphViewer.showGraph(gg1, GraphViewer.GraphType.GT_BASIC,0,100,0,100);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-1 END  map: " + map + " "+xSize+"X"+ ySize + " K: "+ K);

    }

    private static void performExperiment3(String map,String outputFile, int unitEdgeLength,int K, GridPosition start, GridPosition end, int xSize, int ySize,int obstacleCount) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        IdManager.reset();

        System.out.println(">> EXPERIMENT-3 START  map: " + map + " "+xSize+"X"+ ySize + " K: "+ K);

        GridDirectedGraph gg1 = GraphFactory.createGrid(map, obstacleCount, start.getX(),start.getY(), end.getX(), end.getY(), 1, xSize, ySize,unitEdgeLength);
        GridDirectedGraph gg2 = GraphFactory.createGrid(map, obstacleCount, start.getX(),start.getY(), end.getX(), end.getY(), 1, xSize, ySize,unitEdgeLength);

        gg1.setObstacleCost(1);
        gg2.setObstacleCost(1);

        RangaInputPreparer.createEdgetData("EdgeData.txt", gg1);
        RangaInputPreparer.createPathVars("PathVars.txt", gg1, gg1.start, gg1.end,K);

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


        GraphPath<BaseVertex,BaseEdge> pathRanga = RangaOutputParser.getOptimumPath(gg1, "optpath.txt");

        ExactPSA epsa = new ExactPSA(1,0.5);


        epsa.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromLowerBound;

        GraphPath<BaseVertex,BaseEdge> pathLB = epsa.perform(gg1,gg1.getStart(),gg1.getEnd(),K);
        long rpcLB = epsa.returnPathCount;
        long elapsedTimeLB = epsa.elapsedTime;

        gg1.setObstacleCost(1);
        epsa.returnPolicy =   ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;

        GraphPath<BaseVertex,BaseEdge> pathUB = epsa.perform(gg1,gg1.getStart(),gg1.getEnd(),K);

        long elapsedTimeUB = epsa.elapsedTime;



        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        String outputLB = map + " " + (xSize-1) + "x" + (xSize-1)+ " PathFound : "+epsa.pathFound+" K: "+ K + " Cost: " + nf.format(pathLB.getWeight()) +       " RP_LowerBound RPC: " + rpcLB+ " ElapsedTime: "+ elapsedTimeLB;
        String outputUB = map + " " + (xSize-1) + "x" + (xSize-1)+  " PathFound : "+epsa.pathFound+" K: "+ K  + " Cost: " + nf.format(pathUB.getWeight()) +       " RP_UpperBound RPC: " + epsa.returnPathCount+ " ElapsedTime: "+elapsedTimeUB;

        String outputRanga = map + " " + (xSize-1) + "x" + (xSize-1)+ " K: "+ K  + " Cost: " + nf.format(pathRanga.getWeight()) + " RP_RANGA      RPC: NA"+" Elapsed Time : "+ rangaElapsedTime ;



        TestUtil.writeOutput(outputFile,outputLB);
        TestUtil.writeOutput(outputFile,outputUB);
        TestUtil.writeOutput(outputFile,outputRanga);


        gg1.pathList.add(new Path(pathLB,new Pen(Color.red,Pen.PenStyle.PS_Normal)));
        gg1.pathList.add(new Path(pathUB,new Pen(Color.blue,Pen.PenStyle.PS_Normal)));

        gg1.pathList.add(new Path(pathRanga,new Pen(Color.green,Pen.PenStyle.PS_Normal)));

        if (!workSilent)
            GraphViewer.showGraph(gg1, GraphViewer.GraphType.GT_BASIC,0,100,0,100);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-3 END  map: " + map + " "+xSize+"X"+ ySize + " K: "+ K);

    }






    public static void test5() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, InterruptedException {

        String outputFile = "output_GRID_test5.txt" ;
        for (int mapIndex=1; mapIndex<101; mapIndex++)
        {
            String mapFile = "maps/random"+mapIndex+".txt";

            TestUtil.performExperiment(mapFile,outputFile,11,11,10,new double[]{0.5,1,2},new int[]{3,4,5},new GridPosition(5,10),new GridPosition(5,0),100,TestUtil.A_RANGA|TestUtil.A_UPPERBOUND|TestUtil.A_LOWERBOUND|TestUtil.A_KSP);

        }
    }

}
