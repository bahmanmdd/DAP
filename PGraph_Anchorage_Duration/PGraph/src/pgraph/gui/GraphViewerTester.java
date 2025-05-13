package pgraph.gui;

import org.jgrapht.GraphPath;
import org.jgrapht.traverse.MHDHeuristic;
import pgraph.*;
import pgraph.alg.ExactPSA;
import pgraph.alg.PSA;
import pgraph.alg.dijkstra.DijkstraWithTC;
import pgraph.alg.dijkstra.DijkstraWithTCWithHeuristic;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.tcgrid.TCGridDirectedGraph;
import pgraph.grid.tcgrid.TCGridDirectedGraph2;

import pgraph.tag.TagVertex;
import pgraph.tag.TangentArcDirectedGraph;
import pgraph.util.IdManager;
import pgraph.util.Pen;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 03.02.2013
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
public class GraphViewerTester {


    // TAG CONSTANTS
    static final String TAG_DEMO_GRAPH = "maps/randomTest.txt";
    static final int    TAG_DEMO_GRAPH_OBSTACLE_COUNT = 3;
    static final String TAG_EXPERIMENT_GRAPH = "maps/random1001.txt";
    static final int    TAG_EXPERIMENT_GRAPH_OBSTACLE_COUNT = 100;

    // GRID CONSTANTS
    static final String GRID_DEMO_GRAPH = "GridDemoGraph.lg";
    static final int    GRID_DEMO_GRAPH_DEGREE = 3;
    static final String GRID_EXPERIMENT_GRAPH = "GridExperimentGraph.lg";
    static final int    GRID_EXPERIMENT_GRAPH_DEGREE = 10;

    //BZ CONSTANTS
    static final String GRID_BZ_GRAPH = "ship120x80withBZ.txt";
    static final int    GRID_BZ_GRAPH_DEGREE = 1;

    // TC GRID CONSTANTS
    static final String TCGRID_DEMO_GRAPH = "TCGridDemoGraph2.lg";
    static final int    TCGRID_DEMO_GRAPH_DEGREE = 2;
    static final double TCGRID_DEMO_TURNRADIUS = 300;
    //static final String TCGRID_EXPERIMENT_GRAPH = "TCGridExperimentGraph.lg";
    static final String TCGRID_EXPERIMENT_GRAPH = "ship50x30withoutBZ.txt";
    static final int    TCGRID_EXPERIMENT_GRAPH_DEGREE = 2;
    static final double TCGRID_EXPERIMENT_TURNRADIUS = 12;


    // RDP GRID CONSTANTS
    static final String RDP_GRID_DEMO_GRAPH = "RDPGridDemoGraph.lg";


    // POLYGON DEMOS
    //static final String POLYGON_DEMO_GRAPH = "LatticeGraph2.lg";
    static final String POLYGON_DEMO_GRAPH = "ship120x80.txt";
    static final int    POLYGON_DEMO_GRAPH_DEGREE = 4;


    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {

      //test_Polygon_Demo();
     //   test_DijkstraTC_Experiment();
     //  test_TAG_Demo();
     //   test_TAG_Experiment();
//
    //    test_Grid_Demo();
    // test_Grid_Experiment();

       //   test_Grid_BZ();
//
        test_TCGrid_DEMO();

    //   test_TCGrid_Experiment();
   //  test_TCGrid_Experiment2();

      //  test_DijkstraTC_Experiment();
//
//        test_RDP_Demo();
       // test_RDP_Experiment();
    }



    private static void test_TAG_Demo() throws IOException, InstantiationException {
        System.out.println("TAG Demo test is started..");

        TangentArcDirectedGraph tag = _create_TAG(TAG_DEMO_GRAPH, TAG_DEMO_GRAPH_OBSTACLE_COUNT);
        showGraph(tag, GraphViewer.GraphType.GT_BASIC);
    }
    private static void test_TAG_Experiment() throws IOException, InstantiationException {
        System.out.println("TAG Experiement test is started..");

        BaseDirectedGraph tag = _create_TAG(TAG_EXPERIMENT_GRAPH, TAG_EXPERIMENT_GRAPH_OBSTACLE_COUNT);
        showGraph(tag,GraphViewer.GraphType.GT_BASIC);
    }
    private static void test_Grid_Demo() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("Grid Demo test is started..");

        BaseDirectedGraph tag = _create_Grid(GRID_DEMO_GRAPH, GRID_DEMO_GRAPH_DEGREE);
        showGraph(tag,GraphViewer.GraphType.GT_BASIC);
    }
    private static void test_Grid_Experiment() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("Grid Demo test is started..");

        BaseDirectedGraph tag = _create_Grid(GRID_EXPERIMENT_GRAPH, GRID_EXPERIMENT_GRAPH_DEGREE);
        showGraph(tag,GraphViewer.GraphType.GT_BASIC);
    }

    private static void test_Grid_BZ() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("Grid Demo test is started..");

        BaseDirectedGraph tag = _create_Grid_BZ(GRID_BZ_GRAPH, GRID_BZ_GRAPH_DEGREE);
        showGraph(tag,GraphViewer.GraphType.GT_BASIC);
    }


    private static void test_TCGrid_DEMO() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("Turn-Constrained Grid Demo test is started..");

        BaseDirectedGraph tag = _create_TCGrid(TCGRID_DEMO_GRAPH, TCGRID_DEMO_GRAPH_DEGREE, TCGRID_DEMO_TURNRADIUS);
        showGraph(tag,GraphViewer.GraphType.GT_BASIC);
    }

    private static void test_TCGrid_Experiment() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("Turn-Constrained Grid Demo test is started..");

        BaseDirectedGraph tag = _create_TCGrid(TCGRID_EXPERIMENT_GRAPH, TCGRID_EXPERIMENT_GRAPH_DEGREE, TCGRID_EXPERIMENT_TURNRADIUS);
        showGraph(tag,GraphViewer.GraphType.GT_BASIC);
    }

    private static void test_TCGrid_Experiment2() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("Turn-Constrained Grid Demo test is started..");

        BaseDirectedGraph tag = _create_TCGrid2(TCGRID_EXPERIMENT_GRAPH, TCGRID_EXPERIMENT_GRAPH_DEGREE, TCGRID_EXPERIMENT_TURNRADIUS);
        showGraph(tag,GraphViewer.GraphType.GT_BASIC);
    }


    private static void test_DijkstraTC_Experiment() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("Turn-Constrained Grid Demo test is started..");

        BaseDirectedGraph tag = _create_DijkstraTC(TCGRID_EXPERIMENT_GRAPH, TCGRID_EXPERIMENT_GRAPH_DEGREE, TCGRID_EXPERIMENT_TURNRADIUS);
        showGraph(tag,GraphViewer.GraphType.GT_BASIC);
    }


/*

    private static void test_RDP_Demo() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("RDP Grid Demo test is started..");

        BaseDirectedGraph g = _create_RDPGrid_Demo();
        showGraph(g,GraphViewer.GraphType.GT_BASIC);
    }

    private static void test_RDP_Experiment() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("RDP Grid Demo test is started..");

        BaseDirectedGraph g = _create_RDPGrid_Experiment();
        showGraph(g,GraphViewer.GraphType.GT_GRID);
    }

*/

    private static void test_Polygon_Demo() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        System.out.println("Grid Demo test is started..");

        BaseDirectedGraph tag = _create_Grid(POLYGON_DEMO_GRAPH, POLYGON_DEMO_GRAPH_DEGREE);
        showGraph(tag,GraphViewer.GraphType.GT_BASIC);
    }

    private static void showGraph(BaseDirectedGraph g,GraphViewer.GraphType type) {
        GraphViewer gv = new GraphViewer(g, type);
        if (gv.mainPanel instanceof BaseGraphPanel)
            ((BaseGraphPanel)gv.mainPanel).setViewRegion(0,120,0,120);
        gv.setVisible(true);
        gv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


/*

    static BaseDirectedGraph _create_RDPGrid_Demo() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        GridDirectedGraph g= null;
        try {
            g = new GridDirectedGraph(RDP_GRID_DEMO_GRAPH,5);
        } catch (NumberFormatException | InstantiationException
                | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<RDPObstacleInterface> roList = new ArrayList<RDPObstacleInterface>();
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(200,400),100),0.05,true,0.1));
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(400,400),100),0.02,true,0.1));
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(600,400),100),0.08,true,0.1));


        int disambiguationCost = 1, disambiguationCount =2;
        g.setIntersectionHandler(new DTBasedRDPIntersectionHandler(disambiguationCost));
        g.addObstacles(roList);
        RDPProblem p = new RDPProblem(disambiguationCount,disambiguationCost,g);
        p.setPathFinder(new DijkstraPathFinder());

        System.out.println("Grid has been created. Performing RDP-DT algorithm..");


        double ew = p.calculateExpectedWeight();
        double aw = p.calculateActualWeight();

        g.pathList.addAll(p.allPathList);
        return g;
    }

    static BaseDirectedGraph _create_RDPGrid_Experiment() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        //String RDP_GRID_EXPERIMENT_GRAPH = "diskinfo_V70_C100_M40.csv";
        String RDP_GRID_EXPERIMENT_GRAPH = "COBRA_100x100_divideBy10_add54.csv";

        int numberOfObstacles = 140;//39;
        int d, x, y, sX, sY, tX, tY;
        d=1;
        x=100;
        y=100;
        sX=49;//54;
        sY=99;//80;
        tX=49;//54;
        tY=0;//10;
        double el = 1.0;
        double offsetX = 0.0;
        double offsetY = 0.0;
        Point2D.Double offset = new Point2D.Double(offsetX,offsetY);

        GridDirectedGraph g = new GridDirectedGraph(d, x, y, el, offset, sX, sY, tX, tY);
        List<RDPObstacleInterface> roList = new ArrayList<RDPObstacleInterface>();

        BufferedReader br = new BufferedReader(new FileReader(RDP_GRID_EXPERIMENT_GRAPH));
        for (int i = 0; i < numberOfObstacles; i++) {
            boolean actualExistence;
            String str = br.readLine();
            str = str.replace(',', ' ');
            ObstacleShape os = (ObstacleShape) Class.forName("pgraph.DiskObstacleShape").newInstance();
            str = "pgraph.DiskObstacleShape " + str;
            os.fromString(str);
            //ObstacleInterface o = new Obstacle(IdManager.getObstacleId(),DEFAULT_OBSTACLE_WEIGHT,os);
            if(Integer.parseInt(str.split(" ")[5])==1) {
                actualExistence = true;
            } else {
                actualExistence = false;
            }
            RDPObstacleInterface o = new RDPObstacle(IdManager.getObstacleId(),1,os,Double.parseDouble(str.split(" ")[4]),actualExistence,0.1);
            roList.add(o);
        }	//for
        br.close();

        double disambiguationCost=0;

        g.setIntersectionHandler(new DTBasedRDPIntersectionHandler(disambiguationCost));
        g.addObstacles(roList);

        System.out.println("Grid has been created. Performing RDP-DT algorithm..");


        RDPProblem p = new RDPProblem(1,disambiguationCost,g);
        p.setPathFinder(new DijkstraPathFinder());

        //double aw = p.calculateActualWeight();
        double ew = p.calculateExpectedWeight();


        g.pathList.addAll(p.allPathList);
        return g;
    }

*/
    private static BaseDirectedGraph _create_DijkstraTC(String graphFile, int connectivityDegree, double turnRadius) throws InstantiationException {

      GridDirectedGraph gg= null;

        try {
            gg = new GridDirectedGraph(graphFile,connectivityDegree);
        } catch (NumberFormatException | InstantiationException
                | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assert gg != null;
        gg.setObstacleCost(20000);


        PSA sgnh = new PSA(6);


        DijkstraWithTC dsp1 =new  DijkstraWithTC(gg,turnRadius, 45);
        DijkstraWithTCWithHeuristic dsp2 = new DijkstraWithTCWithHeuristic(gg,turnRadius,45,new MHDHeuristic<BaseVertex>(gg.getEnd()));

        long startTime = System.currentTimeMillis();
        GraphPath<BaseVertex,BaseEdge> path1 = dsp1.getShortestPath();
        long elapsedTime1 = System.currentTimeMillis()-startTime;
        startTime = System.currentTimeMillis();
        GraphPath<BaseVertex,BaseEdge> path2 = dsp2.getShortestPath();
        long elapsedTime2 = System.currentTimeMillis()-startTime;

        System.out.println("Cost DjH1: "+path1.getWeight()+ " ElapsedTime: "+ elapsedTime1);
        System.out.println("Cost DjH2: "+path2.getWeight()+ " ElapsedTime: "+ elapsedTime2);

        gg.pathList.add(new SmoothPath(path1,new Pen(Color.red,1,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new SmoothPath(path2,new Pen(Color.blue,1,Pen.PenStyle.PS_Normal)));
        return gg;
    }

    public static TCGridDirectedGraph2 _create_TCGrid2(String graphFile,int connectivityDegree,double turnRadius ) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        TCGridDirectedGraph2 tcg= null;
        GridDirectedGraph gg= null;

        try {
            tcg = new TCGridDirectedGraph2(graphFile,connectivityDegree,2000.0,turnRadius, 0);
            gg = new GridDirectedGraph(graphFile,connectivityDegree);
        } catch (NumberFormatException | InstantiationException
                | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        gg.setObstacleCost(20000);
        tcg.setObstacleCost(20000);

        System.out.println("TC-Grid has been created. Performing PSA algorithm..");

       PSA sgnh = new PSA(6);

        GraphPath<BaseVertex,BaseEdge> path1 = sgnh.perform(tcg,tcg.getStart(),tcg.getEnd(),0);

        DijkstraWithTC dsp =new  DijkstraWithTC(gg,turnRadius);
        GraphPath<BaseVertex,BaseEdge> path2 = dsp.getShortestPath();


        System.out.println("Cost TC : "+path1.getWeight());
        System.out.println("Cost DjH: "+path2.getWeight());


        tcg.pathList.add(new Path(path1,new Pen(Color.red,1,Pen.PenStyle.PS_Normal)));
        tcg.pathList.add(new Path(path2,new Pen(Color.blue,1,Pen.PenStyle.PS_Normal)));


        return tcg;
    }

    public static TCGridDirectedGraph _create_TCGrid(String graphFile,int connectivityDegree,double turnRadius ) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        TCGridDirectedGraph tcg= null;
        GridDirectedGraph gg= null;

        try {
            tcg = new TCGridDirectedGraph(graphFile,connectivityDegree,2000.0,turnRadius, 0);
            gg = new GridDirectedGraph(graphFile,connectivityDegree);
        } catch (NumberFormatException | InstantiationException
                | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        gg.setObstacleCost(20000);

        System.out.println("TC-Grid has been created. Performing PSA algorithm..");


        PSA sgnh = new PSA(6);

        GraphPath<BaseVertex,BaseEdge> path1 = sgnh.perform(tcg,tcg.getStart(),tcg.getEnd(),0);

        DijkstraWithTC dsp =new  DijkstraWithTC(gg,turnRadius);
        GraphPath<BaseVertex,BaseEdge> path2 = dsp.getShortestPath();


        System.out.println("Cost TC : "+path1.getWeight());
        System.out.println("Cost DjH: "+path2.getWeight());



        tcg.pathList.add(new Path(path1,new Pen(Color.black,2,Pen.PenStyle.PS_Normal)));


       // tcg.pathList.add(new SmoothPath(path2,new Pen(Color.blue,1,Pen.PenStyle.PS_Normal)));


        return tcg;
    }


    public static GridDirectedGraph _create_Grid(String graphFile,int connectivityDegree) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        GridDirectedGraph gg= null;
        try {
            gg = new GridDirectedGraph(graphFile,connectivityDegree);
        } catch (NumberFormatException | InstantiationException
                | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Grid has been created. Performing PSA algorithm..");

        gg.setObstacleCost(10000);

        PSA sgnh = new PSA(0.5,0.1);

        GraphPath<BaseVertex,BaseEdge> path1 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),0);
        GraphPath<BaseVertex,BaseEdge> path2 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),1);
        GraphPath<BaseVertex,BaseEdge> path3 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),3);
        GraphPath<BaseVertex,BaseEdge> path4 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),5);



        gg.pathList.add(new SmoothPath(path1,new Pen(Color.black,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new SmoothPath(path2,new Pen(Color.red,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new SmoothPath(path3,new Pen(Color.blue,Pen.PenStyle.PS_Normal)));


        /*
        DijkstraWithTC spAlg = new DijkstraWithTC((GridDirectedGraph)gg,30);
        GraphPath<BaseVertex,BaseEdge> path1 = spAlg.getShortestPath();

        DijkstraWithTC spAlg1 = new DijkstraWithTC((GridDirectedGraph)gg,20);
        GraphPath<BaseVertex,BaseEdge> path2 = spAlg1.getShortestPath();

        DijkstraWithTC spAlg2 = new DijkstraWithTC((GridDirectedGraph)gg,10);
        GraphPath<BaseVertex,BaseEdge> path3 = spAlg2.getShortestPath();

        gg.pathList.add(new Path(path1,new Pen(Color.black,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new Path(path2,new Pen(Color.red,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new Path(path3,new Pen(Color.blue,Pen.PenStyle.PS_Normal)));

        System.out.println("Path1:   MTR: 1000 Cost: "+ path1.getWeight());
        System.out.println("Path2:   MTR: 50 Cost: "+ path2.getWeight());
        System.out.println("Path3:   MTR: 10 Cost: "+ path3.getWeight());
    */

      //  gg.specialZones.add(new BasicSpecialZone(IdManager.getZoneId(),null,new DiskZoneShape(new Point2D.Double(80,80),100),new Pen(Color.green),"Wind Zone",60,80));
      //  gg.specialZones.add(new BasicSpecialZone(IdManager.getZoneId(),null,new DiskZoneShape(new Point2D.Double(390,390),200),new Pen(Color.red),"Ocean Current",360,390));

        return gg;
    }

    public static GridDirectedGraph _create_Grid_BZ(String graphFile,int connectivityDegree) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        GridDirectedGraph gg= null;
        try {
            gg = new GridDirectedGraph(graphFile,connectivityDegree);
        } catch (NumberFormatException | InstantiationException
                | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Grid has been created. Performing PSA algorithm..");

        return gg;
    }

    private static TangentArcDirectedGraph _create_TAG(String graphFile,int obstacleCount) throws IOException, InstantiationException {
        FileBasedTagDiskGenerator fdg = new FileBasedTagDiskGenerator(graphFile,obstacleCount);
        List<? extends ObstacleInterface> dzList = fdg.generate();
        TreeSet<ObstacleInterface> disks = new TreeSet<ObstacleInterface>();
        disks.addAll(dzList);

        TangentArcDirectedGraph tag = new TangentArcDirectedGraph(disks,new TagVertex(new Point2D.Double(500, 0)),new TagVertex(new Point2D.Double(500,1000)),new Point2D.Double(10,10));

        System.out.println("TAG has been created. Performing Exact_SCNH algorithm..");

        ExactPSA escnh = new ExactPSA(0.5,1);

        GraphPath<BaseVertex,BaseEdge> path1 = escnh.perform(tag,tag.start,tag.end,1);
        GraphPath<BaseVertex,BaseEdge> path2 = escnh.perform(tag,tag.start,tag.end,2);
        GraphPath<BaseVertex,BaseEdge> path3 = escnh.perform(tag,tag.start,tag.end,3);
        GraphPath<BaseVertex,BaseEdge> path4 = escnh.perform(tag,tag.start,tag.end,0);

        tag.pathList.add(new Path(path1,new Pen(Color.red,Pen.PenStyle.PS_Dashed)));
        tag.pathList.add(new Path(path2,new Pen(Color.green,Pen.PenStyle.PS_Normal)));
        tag.pathList.add(new Path(path3,new Pen(Color.blue,Pen.PenStyle.PS_Dashed)));
        tag.pathList.add(new Path(path4,new Pen(Color.black,Pen.PenStyle.PS_Normal)));

        return tag;
    }
}
