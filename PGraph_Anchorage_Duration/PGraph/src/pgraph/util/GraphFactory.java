package pgraph.util;

import pgraph.FileBasedTagDiskGenerator;
import pgraph.ObstacleInterface;
import pgraph.RandomTagDiskGenerator;
import pgraph.grid.GridDirectedGraph;
import pgraph.tag.TagVertex;
import pgraph.tag.TangentArcDirectedGraph;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 17.03.2013
 * Time: 09:53
 * To change this template use File | Settings | File Templates.
 */
public class GraphFactory {

    public static  TangentArcDirectedGraph createTAG(String graphFile, int  obstacleCount, Point2D.Double s , Point2D.Double e) throws IOException, InstantiationException {
        FileBasedTagDiskGenerator fdg = new FileBasedTagDiskGenerator(graphFile,obstacleCount);
        List<? extends ObstacleInterface> dzList = fdg.generate();
        TreeSet<ObstacleInterface> disks = new TreeSet<ObstacleInterface>();
        disks.addAll(dzList);

        TangentArcDirectedGraph tag = new TangentArcDirectedGraph(disks,new TagVertex(s),new TagVertex(e),new Point2D.Double(10,10));
        return tag;
    }


    public static  TangentArcDirectedGraph createRandomTAG(double maxX, double maxY ,double radius ,double cost ,int  obstacleCount, Point2D.Double s , Point2D.Double e) throws IOException, InstantiationException {
        RandomTagDiskGenerator rdg = new RandomTagDiskGenerator(obstacleCount,new Point2D.Double(radius,radius),new Point2D.Double(maxX-2*radius,maxY-2*radius),radius,cost);
        List<? extends ObstacleInterface> dzList = rdg.generate();
        TreeSet<ObstacleInterface> disks = new TreeSet<ObstacleInterface>();
        disks.addAll(dzList);

        TangentArcDirectedGraph tag = new TangentArcDirectedGraph(disks,new TagVertex(s),new TagVertex(e),new Point2D.Double(10,10));
        return tag;
    }


    //GridDirectedGraph(int d, int x, int y, double el, Point2D.Double offset, int sX, int sY, int tX, int tY)
    public static GridDirectedGraph createGrid(String graphFile, int  obstacleCount, int sX,int sY , int  eX,int eY,int degree, int xSize,int ySize,int unitEdgeLength) throws IOException, InstantiationException {
        FileBasedTagDiskGenerator fdg = new FileBasedTagDiskGenerator(graphFile,obstacleCount);
        List<? extends ObstacleInterface> dzList = fdg.generate();

        long startTime = System.currentTimeMillis();

        GridDirectedGraph g = new GridDirectedGraph(degree,xSize,ySize,unitEdgeLength,new Point2D.Double(0,0),sX,sY,eX,eY);

        double elapsedTime = System.currentTimeMillis()- startTime;

        System.out.println( "Graph build time: "+ elapsedTime);

        g.addObstacles(dzList);

        return g;
    }
}
