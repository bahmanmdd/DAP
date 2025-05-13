package experiments.ExactPSA;

import pgraph.grid.GridDirectedGraph;
import pgraph.util.GraphFactory;
import pgraph.util.TestUtil;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 01.01.2014
 * Time: 10:35
 * To change this template use File | Settings | File Templates.
 */
public class PSA_GRID_Tester {


    public static boolean validateParams(String[] args)
    {
        if (args.length!=13)
        {
            System.out.println("WRONG PARAMETERS!");
            System.out.println("USAGE:");
            System.out.println();
            System.out.println("java -j PGraph.jar [inputFile] [neutroCost] [neutroCount] [epsilon] [outputFile] [obstacleCount] [xSize] [ySize] [unitEdgeLen] [startX] [startY] [endX] [endY]");
            return false;
        }
        return true;
    }


    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException
    {
        if (!validateParams(args))
            return;

        String inputFile = args[0];
        double neutroCost = Double.parseDouble(args[1]);
        int    neutroCount= Integer.parseInt(args[2]);
        double epsilon  = Double.parseDouble(args[3]);
        String outputFile= args[4];

        int obstacleCount=Integer.parseInt(args[5]);
        int xSize=Integer.parseInt(args[6]);;
        int ySize=Integer.parseInt(args[7]);;
        int unitEdgeLen=Integer.parseInt(args[8]);;

        int startX=Integer.parseInt(args[9]);;
        int startY=Integer.parseInt(args[10]);;
        int endX=Integer.parseInt(args[11]);;
        int endY=Integer.parseInt(args[12]);;

        performTest(inputFile,neutroCost,neutroCount,epsilon,outputFile,obstacleCount,xSize,ySize,unitEdgeLen,startX,startY,endX,endY);

    }


    private static void performTest(String inputFile,double neutroCost,int neutroCount,double epsilon,String outputFile,int obstacleCount,int xSize,int ySize,int unitEdgeLen,int startX,int startY,int endX,int endY) throws IOException, InstantiationException, IllegalAccessException, InterruptedException, ClassNotFoundException {
        System.out.println("TEST START >> "+ inputFile + " Alpha: "+neutroCost+ " K: "+neutroCount+ " Epsilon: "+ epsilon);



        GridDirectedGraph gg = GraphFactory.createGrid(inputFile, obstacleCount, startX, startY, endX, endY, 1, xSize, ySize, unitEdgeLen);



        TestUtil.performExperiment(inputFile,outputFile,gg,neutroCost,neutroCount,obstacleCount,epsilon,TestUtil.A_PSA);

        System.out.println("TEST ENDED >> "+ inputFile + " Alpha: "+neutroCost+ " K: "+neutroCount+ " Epsilon: "+ epsilon);

        return;
    }
}
