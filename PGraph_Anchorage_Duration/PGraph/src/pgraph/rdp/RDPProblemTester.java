package pgraph.rdp;

import pgraph.DiskObstacleShape;
import pgraph.ObstacleShape;
import pgraph.grid.GridDirectedGraph;
import pgraph.gui.GraphViewer;
import pgraph.util.IdManager;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 27.01.2013
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
public class RDPProblemTester {

    static GridDirectedGraph generateFromFile() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        GridDirectedGraph lg= null;
        try {
            lg = new GridDirectedGraph("LatticeGraphDebug2.lg",5);
        } catch (NumberFormatException | InstantiationException
                | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lg;
    }

    static void test2() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        GridDirectedGraph g = generateFromFile();
        List<RDPObstacleInterface> roList = new ArrayList<RDPObstacleInterface>();
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(200,400),100),0.005,true,0.1));
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(400,400),100),0.002,false,0.1));
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(600,400),100),0.008,true,0.1));


        ///
        ///
        g.setIntersectionHandler(new DTBasedRDPIntersectionHandler(1));
        g.addObstacles(roList);
        RDPProblem p = new RDPProblem(2,50,g);
        p.setPathFinder(new DijkstraPathFinder());

        //double aw = p.calculateActualWeight();
        double ew = p.calculateExpectedWeight();


        g.pathList.addAll(p.allPathList);

        GraphViewer gv = new GraphViewer(g);



        gv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gv.setSize(1000, 1000);
        gv.setVisible(true);



        //System.out.println("Expected Weight: " + ew );
        System.out.println("Actual   Weight: " +ew );

    }

    static void test1() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        //BaseDirectedGraph g = performTCGridExperiment();
        /*GridDirectedGraph g = generateFromFile();
        List<RDPObstacleInterface> roList = new ArrayList<RDPObstacleInterface>();
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(200,400),100),0.05,true,0.1));
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(400,400),100),0.02,true,0.1));
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(600,400),100),0.08,true,0.1));*/

        //String fileName = "COBRA_100x100_divideBy10_add54";
        String fileName = "inst1";
        int numberOfObstacles = 140;//39;
        int d, x, y, sX, sY, tX, tY;
        d=1;
        x=101;
        y=101;
        sX=50;//54;
        sY=100;//80;
        tX=50;//54;
        tY=1;//10;
        double el = 1.0;
        double offsetX = 0.0;
        double offsetY = 0.0;
        Point2D.Double offset = new Point2D.Double(offsetX,offsetY);

        GridDirectedGraph g = new GridDirectedGraph(d, x, y, el, offset, sX, sY, tX, tY);
        List<RDPObstacleInterface> roList = new ArrayList<RDPObstacleInterface>();

        BufferedReader br = new BufferedReader(new FileReader(fileName+".csv"));
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

        ///
        ///
        double disambiguationcost= 10;
        int K =50;
        g.setIntersectionHandler(new DTBasedRDPIntersectionHandler(disambiguationcost));
        g.addObstacles(roList);
        RDPProblem p = new RDPProblem(K,disambiguationcost,g);
        p.setPathFinder(new DijkstraPathFinder());

        double aw = p.calculateActualWeight();
        //double ew = p.calculateExpectedWeight();


        g.pathList.addAll(p.actualPathList);

        GraphViewer gv = new GraphViewer(g);



        gv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gv.setSize(1000, 1000);
        gv.setVisible(true);



        //System.out.println("Expected Weight: " + ew );
        System.out.println("Actual   Weight: " + aw );




    }
    static void test3() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        //BaseDirectedGraph g = performTCGridExperiment();
        /*GridDirectedGraph g = generateFromFile();
        List<RDPObstacleInterface> roList = new ArrayList<RDPObstacleInterface>();
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(200,400),100),0.05,true,0.1));
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(400,400),100),0.02,true,0.1));
        roList.add(new RDPObstacle(IdManager.getObstacleId(),1,new DiskObstacleShape(new Point2D.Double(600,400),100),0.08,true,0.1));*/

        //String fileName = "COBRA_100x100_divideBy10_add54";
        String fileName = "COBRA_100x100_divideBy10_add54";
        int numberOfObstacles = 39;
        int d, x, y, sX, sY, tX, tY;
        d=1;
        x=100;
        y=100;
        sX=54;
        sY=80;
        tX=54;
        tY=10;//10;
        double el = 1.0;
        double offsetX = 0.0;
        double offsetY = 0.0;
        Point2D.Double offset = new Point2D.Double(offsetX,offsetY);

        GridDirectedGraph g = new GridDirectedGraph(d, x, y, el, offset, sX, sY, tX, tY);
        List<RDPObstacleInterface> roList = new ArrayList<RDPObstacleInterface>();

        BufferedReader br = new BufferedReader(new FileReader(fileName+".csv"));
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
            RDPObstacleInterface o = new RDPObstacle(IdManager.getObstacleId(),1,os,Double.parseDouble(str.split(" ")[4]),actualExistence,1);
            roList.add(o);
        }	//for
        br.close();

        ///
        ///
        double disambiguationCost= 10;
        int K = 50;
        g.setIntersectionHandler(new DTBasedRDPIntersectionHandler(disambiguationCost));
        g.addObstacles(roList);
        RDPProblem p = new RDPProblem(K,disambiguationCost,g);
        p.setPathFinder(new DijkstraPathFinder());

        //double ew = p.calculateExpectedWeight();
        double ew = p.calculateActualWeight();



        g.pathList.addAll(p.actualPathList);

        GraphViewer gv = new GraphViewer(g);



        gv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gv.setSize(1000, 1000);
        gv.setVisible(true);



        System.out.println("Expected Weight: " + ew );
        //System.out.println("Actual   Weight: " + aw );




    }


    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        test1();
    }
}
