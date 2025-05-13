package experiments.drawings;

import org.jgrapht.GraphPath;
import pgraph.Path;
import pgraph.alg.ExactPSA;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.gui.GraphViewer;
import pgraph.tag.TangentArcDirectedGraph;
import pgraph.util.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.NumberFormat;






/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 13.07.2013
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */
public class TAG_Drawings {


    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {

        // performExperimentTAGvsGRID("maps/drawings1.txt",2,10);
       // performExperimentTAGvsGRID("maps/random3.txt",5,100);
       // performExperimentRandom(4,new Point2D.Double(50,100),new Point2D.Double(50,1));
       // performExperiment2("maps/drawings1.txt",5,new Point2D.Double(50,100),new Point2D.Double(50,1),10);

        performExperimentRandom(4,new Point2D.Double(50,100),new Point2D.Double(50,1));

    }


    private static void performExperiment1(String map, int K, Point2D.Double start, Point2D.Double end, int obstacleCount) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        double initialAlpha = 0.1;

        System.out.println(">> EXPERIMENT-1 START  map: " + map + "  K: "+ K);

        IdManager.reset();

        TangentArcDirectedGraph tag1 = GraphFactory.createTAG(map, obstacleCount, start, end);

        TangentArcDirectedGraph tag2 = GraphFactory.createTAG(map,obstacleCount,start,end);

        tag1.setObstacleCost(initialAlpha);


        tag2.setObstacleCost(initialAlpha);



        ExactPSA escnh = new ExactPSA(initialAlpha,0.5);



        escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromLowerBound;

        escnh.perform(tag1,tag1.start,tag1.end,K);

        GraphPath<BaseVertex,BaseEdge> pathLB = escnh.bisectionPath;
        long rpcLB = escnh.returnPathCount;
        long elapsedTimeLB = escnh.elapsedTime;

        tag1.setObstacleCost(1);
        escnh.returnPolicy =   ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;

        GraphPath<BaseVertex,BaseEdge> pathESCNH = escnh.perform(tag1,tag1.start,tag1.end,K);

        GraphPath<BaseVertex,BaseEdge> pathUB = escnh.bisectionPath;
        long elapsedTimeUB = escnh.elapsedTime;


        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        String outputLB = map + " K: "+ K  + " Cost: " + nf.format(pathLB.getWeight()) +       " RP_LowerBound RPC: " + rpcLB+" Elapsed Time : "+ elapsedTimeLB;
        String outputUB = map + " K: "+ K  + " Cost: " + nf.format(pathUB.getWeight()) +       " RP_UpperBound RPC: " + escnh.returnPathCount+" Elapsed Time : "+ elapsedTimeUB;




        tag1.pathList.add(new Path(pathESCNH,new Pen(Color.red,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathLB,new Pen(Color.yellow,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathUB,new Pen(Color.blue,Pen.PenStyle.PS_Normal)));



        GraphViewer.showGraph(tag1, GraphViewer.GraphType.GT_BASIC, 0, 100, 0, 100,false);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-1 END  map: " + map + "  K: "+ K);

    }

    private static void performExperiment2(String map, int K, Point2D.Double start, Point2D.Double end, int obstacleCount) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        double initialAlpha = 1.9;

        System.out.println(">> EXPERIMENT-1 START  map: " + map + "  K: "+ K);

        IdManager.reset();

        TangentArcDirectedGraph tag1 = GraphFactory.createTAG(map, obstacleCount, start, end);

        TangentArcDirectedGraph tag2 = GraphFactory.createTAG(map,obstacleCount,start,end);



        ExactPSA escnh = new ExactPSA(initialAlpha,0.5);



        escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;

        GraphPath<BaseVertex,BaseEdge> pathESCNH = escnh.perform(tag1,tag1.start,tag1.end,K);

        GraphPath<BaseVertex,BaseEdge> pathBisection = escnh.bisectionPath;

        GraphPath<BaseVertex,BaseEdge> pathCand1 = escnh.firstCDPath;

        GraphPath<BaseVertex,BaseEdge> pathCand2 = escnh.secondCDPath == null  ? pathCand1 : escnh.secondCDPath;



        long elapsedTimeUB = escnh.elapsedTime;


        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        String outputB = map +     " Bisection K: "+ K  + " Cost: " + nf.format(pathBisection.getWeight());
        String outputC1 = map +    " Cand-1    K: "+ K  + " Cost: " + nf.format(pathCand1.getWeight());
        String outputC2 = map +    " Cand-2    K: "+ K  + " Cost: " + nf.format(pathCand2.getWeight());
        String outputESCNH = map + " Optimum   K: "+ K  + " Cost: " + nf.format(pathESCNH.getWeight()) + " RPC: "+ escnh.returnPathCount;


        System.out.println(outputB);
        System.out.println(outputC1);
        System.out.println(outputC2);
        System.out.println(outputESCNH);

        tag1.pathList.add(new Path(pathESCNH,new Pen(Color.red,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathBisection,new Pen(Color.yellow,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathCand1,new Pen(Color.blue,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathCand2,new Pen(Color.green,Pen.PenStyle.PS_Normal)));


        GraphViewer.showGraph(tag1, GraphViewer.GraphType.GT_BASIC, 0, 100, 0, 100,false);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-1 END  map: " + map + "  K: "+ K);

    }



    private static void performExperimentRandom( int K, Point2D.Double start, Point2D.Double end) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        double initialAlpha = 1;

        int obstacleCount = 10;

        System.out.println(">> EXPERIMENT-RANDOM START  K: "+ K);

        ExactPSA escnh= null;

        GraphPath<BaseVertex,BaseEdge> pathESCNH =null;
        GraphPath<BaseVertex,BaseEdge> pathBisection = null;
        GraphPath<BaseVertex,BaseEdge> pathCand1 = null;
        GraphPath<BaseVertex,BaseEdge> pathCand2 = null;

        TangentArcDirectedGraph tag1 = null;

        while (true)
        {

            IdManager.reset();

            tag1 = GraphFactory.createRandomTAG(100,100,10,0.1, obstacleCount, start, end);

            escnh = new ExactPSA(initialAlpha,0.5);

            escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;

            pathESCNH = escnh.perform(tag1,tag1.start,tag1.end,K);

            pathBisection = escnh.bisectionPath;

            pathCand1 = escnh.firstCDPath;

            pathCand2 = escnh.secondCDPath == null  ? pathCand1 : escnh.secondCDPath;


           // if (escnh.bisectionPath.getEdgeList().size() != pathESCNH.getEdgeList().size())
           //     break;

            if (escnh.secondCDPath != null && (GraphUtil.recalculatePathWeight(tag1, pathCand2)-pathESCNH.getWeight()>0.0001))
                break;

            long elapsedTimeUB = escnh.elapsedTime;


        }




        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);


        String outputB      = " Bisection K: "+ K  + " Cost: " + nf.format(GraphUtil.recalculatePathWeight(tag1, pathBisection));
        String outputC1     = " Cand-1    K: "+ K  + " Cost: " + nf.format(GraphUtil.recalculatePathWeight(tag1, pathCand1));
        String outputC2     = " Cand-2    K: "+ K  + " Cost: " + nf.format(GraphUtil.recalculatePathWeight(tag1, pathCand2));
        String outputESCNH  = " Optimum   K: "+ K  + " Cost: " + nf.format(pathESCNH.getWeight()) + " RPC: "+ escnh.returnPathCount;

        System.out.println("Current Alpha: "+ escnh.currentAlpha);
        System.out.println(outputB);
        System.out.println(outputC1);
        System.out.println(outputC2);
        System.out.println(outputESCNH);

        tag1.pathList.add(new Path(pathESCNH,new Pen(Color.red,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathBisection,new Pen(Color.blue,Pen.PenStyle.PS_Pointed)));
        //tag1.pathList.add(new Path(pathCand1,new Pen(Color.blue,Pen.PenStyle.PS_Dashed)));
        tag1.pathList.add(new Path(pathCand2,new Pen(Color.green,Pen.PenStyle.PS_Dashed)));


        GraphViewer.showGraph(tag1, GraphViewer.GraphType.GT_BASIC, 0, 100, 0, 100,false);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-RANDOM END    K: "+ K);

    }


    private static void performExperimentRandom2( Point2D.Double start, Point2D.Double end) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        double initialAlpha = 0.5;

        int obstacleCount = 20;

        System.out.println(">> EXPERIMENT-RANDOM START  " );

        ExactPSA escnh= null;

        GraphPath<BaseVertex,BaseEdge> pathESCNH0 =null;
        GraphPath<BaseVertex,BaseEdge> pathESCNH1 = null;
        GraphPath<BaseVertex,BaseEdge> pathESCNH2 = null;
        GraphPath<BaseVertex,BaseEdge> pathESCNH3 = null;

        TangentArcDirectedGraph tag1 = null;

        while (true)
        {

            IdManager.reset();

            tag1 = GraphFactory.createRandomTAG(100,100,10,0.1, obstacleCount, start, end);

            escnh = new ExactPSA(initialAlpha,0.5);

            escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;

           try {
               pathESCNH0 = escnh.perform(tag1,tag1.start,tag1.end,0);
               pathESCNH1 = escnh.perform(tag1,tag1.start,tag1.end,1);
               pathESCNH2 = escnh.perform(tag1,tag1.start,tag1.end,2);
               pathESCNH3 = escnh.perform(tag1,tag1.start,tag1.end,3);
           }
           catch (Exception e)
           {

           }





            if ( Math.abs( pathESCNH0.getWeight()-pathESCNH1.getWeight())>0.01 &&
                 Math.abs( pathESCNH0.getWeight()-pathESCNH2.getWeight())>0.01 &&
                 Math.abs( pathESCNH2.getWeight()-pathESCNH1.getWeight())>0.01 &&
                 Math.abs( pathESCNH3.getWeight()-pathESCNH1.getWeight())>0.01
                    )
                break;

            long elapsedTimeUB = escnh.elapsedTime;


        }




        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        String outputB      = " K: 0 Cost: " + nf.format( pathESCNH0.getWeight());
        String outputC1     = " K: 1 Cost: " + nf.format(pathESCNH1.getWeight());
        String outputC2     = " K: 2 Cost: " + nf.format(pathESCNH2.getWeight());
        String outputESCNH  = " K: 3 Cost: " + nf.format(pathESCNH3.getWeight());


        System.out.println(outputB);
        System.out.println(outputC1);
        System.out.println(outputC2);
        System.out.println(outputESCNH);

        tag1.pathList.add(new Path(pathESCNH0,new Pen(Color.red,2.5f,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathESCNH1,new Pen(Color.blue,Pen.PenStyle.PS_Pointed)));
        tag1.pathList.add(new Path(pathESCNH2,new Pen(Color.black,Pen.PenStyle.PS_Dashed)));
        tag1.pathList.add(new Path(pathESCNH3,new Pen(Color.green,1f,Pen.PenStyle.PS_Normal)));


        GraphViewer.showGraph(tag1, GraphViewer.GraphType.GT_BASIC, 0, 100, 0, 100,false);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-RANDOM 2 END  "  );

    }


    private static void performExperimentTAGvsGRID(String map, int K, int obstacleCount) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, InterruptedException {

        double initialAlpha = 1;

        System.out.println(">> EXPERIMENT-1 START  map: " + map + "  K: "+ K);

        IdManager.reset();

        TangentArcDirectedGraph tag1 = GraphFactory.createTAG(map, obstacleCount, new Point2D.Double(50,100), new Point2D.Double(50,0));

        //GridDirectedGraph grid1 = GraphFactory.createGrid(map, obstacleCount, 50,100,50,0,1,101,101,1);
        GridDirectedGraph grid2 = GraphFactory.createGrid(map, obstacleCount, 25,50,25,0,1,51,51,2);
        GridDirectedGraph grid5 = GraphFactory.createGrid(map, obstacleCount, 10,20,10,0,1,21,21,5);
        GridDirectedGraph grid10 = GraphFactory.createGrid(map, obstacleCount, 5,10,5,0,1,11,11,10);

        ExactPSA escnh = new ExactPSA(initialAlpha,0.5);



        escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;

        GraphPath<BaseVertex,BaseEdge> pathTAG = escnh.perform(tag1,tag1.start,tag1.end,K);



        escnh = new ExactPSA(initialAlpha,0.5);
        escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;
        GraphPath<BaseVertex,BaseEdge> pathGrid2 = escnh.perform(grid2,grid2.start,grid2.end,K);

        escnh = new ExactPSA(initialAlpha,0.5);
        escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;
        GraphPath<BaseVertex,BaseEdge> pathGrid5 = escnh.perform(grid5,grid5.start,grid5.end,K);

        escnh = new ExactPSA(initialAlpha,0.5);
        escnh.returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromUpperBound;
        GraphPath<BaseVertex,BaseEdge> pathGrid10 = escnh.perform(grid10,grid10.start,grid10.end,K);


        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(4);

        String outputT   = map +    " TAG  K: "+ K  + " Cost: " + nf.format(pathTAG.getWeight());
        //String outputG1  = map +    " Grid-1    K: "+ K  + " Cost: " + nf.format(pathGrid1.getWeight());
        String outputG2  = map +    " Grid-2    K: "+ K  + " Cost: " + nf.format(pathGrid2.getWeight());
        String outputG5  = map +    " Grid-5    K: "+ K  + " Cost: " + nf.format(pathGrid5.getWeight());
        String outputG10 = map +    " Grid-10   K: "+ K  + " Cost: " + nf.format(pathGrid10.getWeight());


        System.out.println(outputT);
        //System.out.println(outputG1);
        System.out.println(outputG2);
        System.out.println(outputG5);
        System.out.println(outputG10);

        tag1.pathList.add(new Path(pathTAG,new Pen(Color.red,2.5f,Pen.PenStyle.PS_Normal)));
        //tag1.pathList.add(new Path(pathGrid1,new Pen(Color.yellow,Pen.PenStyle.PS_Normal)));
        tag1.pathList.add(new Path(pathGrid2,new Pen(Color.blue,Pen.PenStyle.PS_Pointed)));
        tag1.pathList.add(new Path(pathGrid5,new Pen(Color.black,Pen.PenStyle.PS_Dashed)));
        tag1.pathList.add(new Path(pathGrid10,new Pen(Color.green,1f,Pen.PenStyle.PS_Normal)));

        GraphViewer.showGraph(tag1, GraphViewer.GraphType.GT_BASIC, 0, 100, 0, 100,false);


        TestUtil.cleanUpEnvironment();

        System.out.println(">> EXPERIMENT-1 END  map: " + map + "  K: "+ K);

    }



}
