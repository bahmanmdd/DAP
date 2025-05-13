package pgraph.CAOStar;

import pgraph.ObstacleShape;
import pgraph.grid.GridDirectedGraph;
import pgraph.intersectionhandler.BAOInformationStateIntersectionHandler;
import pgraph.intersectionhandler.DTBasedRDPIntersectionHandler;
import pgraph.rdp.DijkstraPathFinder;
import pgraph.rdp.RDPObstacle;
import pgraph.rdp.RDPObstacleInterface;
import pgraph.rdp.RDPProblem;
import pgraph.util.IdManager;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ADMIN
 * Date: 2/10/13
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */

public class CAOProblemTester {

    public static double run() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        int d, x, y, sX, sY, tX, tY;

        // the DTA path length will be multiplied by this factor if the DPS Heuristic is used:
        double DPSHeuristicDTAFactor = 1.1; // Note: 1.1 seems to be a good trade-off.

        int numberOfObstacles = 39;

        double disambiguationCost = 0.0;
        int numberOfDisagPoints   = -1;   // set to -1 for all possible grid disambiguation points

        int disambiguationCount   = 1;

        //String fileName = "COBRA";
        String fileName = "Inst1";

/*
        //COBRA:
        d=1;
        x=101;
        y=101;
        sX=54;
        sY=80;
        tX=54;
        tY=10;
*/

        //COBRA-LIKE:
        d=1;
        x=101;
        y=101;
        sX=50;
        sY=100;
        tX=50;
        tY=1;


        /*
        String fileName = "Test_2Disk";
        minOfZRandDTA     = 26.35; //26.35 for 2 & 38.62 for 3 disks
        numberOfDisagPoints = -1;
        int numberOfObstacles     = 2;
        int disambiguationCount   = 2;
        double disambiguationCost = 0.4;
        int d, x, y, sX, sY, tX, tY;
        d=1;
        x=101;
        y=101;
        sX=2;
        sY=6;
        tX=26; //26 for 2 & 38 for 3 disks
        tY=6;
        */

        double el = 1.0;
        double offsetX = 0.0;
        double offsetY = 0.0;
        Point2D.Double offset = new Point2D.Double(offsetX,offsetY);

        GridDirectedGraph g = new GridDirectedGraph(d, x, y, el, offset, sX, sY, tX, tY);
        List<RDPObstacleInterface> roList = new ArrayList<>();

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
        }	// end for()
        br.close();

        g.setIntersectionHandler(new BAOInformationStateIntersectionHandler());
        g.addObstacles(roList);
        RDPProblem p = new RDPProblem(roList,disambiguationCount,numberOfDisagPoints,disambiguationCost,g);
        p.setPathFinder(new DijkstraPathFinder());

        HashMap<RDPObstacleInterface, Character> ambInfState = new HashMap<>(roList.size());
        for (RDPObstacleInterface obstacle : roList) {
            ambInfState.put(obstacle, 'a');
        }

        if (numberOfDisagPoints == -1) DPSHeuristicDTAFactor = 1.0;

        p.setDPSHeuristicDTAFactor(DPSHeuristicDTAFactor);

        double startNodeDistToDestinationZR  = p.zeroRiskLengthForUpperBound(p.getInitialGraph().start,p.getInitialGraph().end,ambInfState);
        double startNodeDistToDestinationDTA = p.calculateExpectedWeight(p.getInitialGraph().start, p.getInitialGraph().end, ambInfState, new DTBasedRDPIntersectionHandler(p.getDisambiguationCost()));

        System.out.println("\nNow Running: K = " + disambiguationCount + ", c = " + disambiguationCost
                + ", M = " + numberOfDisagPoints + ", F = " + DPSHeuristicDTAFactor + "\n");

        CAOStar CAOStar = new CAOStar();
        double  returnValue = p.runBAOStar(CAOStar);

        System.out.println("\nRESULTS FOR: K = " + disambiguationCount + ", c = " + disambiguationCost
                + ", M = " + numberOfDisagPoints + ", F = " + DPSHeuristicDTAFactor);
        System.out.println("Expanded: " + CAOStar.getNumberOfNodeExpansions()
                + " & Cached: " + CAOStar.getNumberOfNodesCached()
                + " & Revisited: " + CAOStar.getNumberOfRevisitedStates()
                + " & Pruned: " + CAOStar.getNumberOfPrunedChildren());

        NumberFormat formatter = new DecimalFormat("#0.000");
        System.out.println("ZRDistance    = " + formatter.format(startNodeDistToDestinationZR));
        System.out.println("DTDistance    = " + formatter.format(startNodeDistToDestinationDTA));

        if (startNodeDistToDestinationDTA < returnValue) {
            System.out.println("WARNING: DTDistance < Optimal Value");
        }



        return returnValue;
    }

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        NumberFormat formatter2 = new DecimalFormat("#0.00");
        NumberFormat formatter3 = new DecimalFormat("#0.000");

        long start = System.currentTimeMillis();
        System.out.println("Optimal Value = " + formatter3.format(run()));
        long end = System.currentTimeMillis();

        System.out.print("Exec. Time    = " + formatter2.format((end - start) / 1000d) + " seconds\n");
    }
}
