package pgraph.CAOStar;

import org.jgrapht.util.MathUtil;
import pgraph.LineEdge;
import pgraph.anchorage.util.RandUtil;
import pgraph.base.BaseEdge;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridVertex;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 01.07.2014
 * Time: 09:38
 * To change this template use File | Settings | File Templates.
 */
public class CAOStarObstacleGenerator {

    private static final double     OBSTACLE_RADIUS = 0.1;
    private static final double     OBSTACLE_DISTRIBUTION = 0.5;
    private static final double     OBSTACLE_POSITION= 0.25;

    private static final double TRUE_ALPHA = 6;
    private static final double TRUE_BETA = 2;
    private static final double FALSE_ALPHA = 2;
    private static final double FALSE_BETA = 6;

    public static  void genearate(String outputFile, int xMax, int yMax,double stockProb, boolean directedEdges) throws IOException {

        RandUtil rng = new RandUtil((int)System.currentTimeMillis());

        GridDirectedGraph g = new GridDirectedGraph(1,xMax,yMax,1,new Point2D.Double(0,0),0,0,0,0);

        BufferedWriter br  = new BufferedWriter(new FileWriter(outputFile));

        for (BaseEdge e: g.edgeSet)
        {
            if (e.start.pos.getX()>7 && e.start.pos.getY()>7 ) {
                boolean aha = true;
            }

            if (boundryEdge(e,g))
                continue;;

            if ( !directedEdges)
            {
                if ( e.start.pos.getX()> e.end.pos.getX())
                    continue;
                else if (e.start.pos.getX()== e.end.pos.getX() && e.start.pos.getY()>e.end.pos.getY())
                    continue;
            }

            if (rng.nextBoolean(stockProb)) // Stockastic Edge
            {
                LineEdge le = (LineEdge)e;

                Point2D.Double c = getPoint(le,OBSTACLE_POSITION);

                boolean realObstacle = rng.nextBoolean(OBSTACLE_DISTRIBUTION);

                double rho = getRHO(rng,realObstacle);

                br.write( c.getX()+","+c.getY()+","+OBSTACLE_RADIUS + "," + rho + ","+(realObstacle ? "1":"0") ); br.newLine();

            }
        }

        br.close();
    }

    private static double getRHO(RandUtil rng,boolean realObstacle) {

        if (realObstacle)
            return rng.nextBeta(TRUE_ALPHA,TRUE_BETA);
        else
            return rng.nextBeta(FALSE_ALPHA,FALSE_BETA);
    }

    public static Point2D.Double getPoint(LineEdge le, double ratio)
    {
        double sx = le.start.pos.getX();
        double sy = le.start.pos.getY();

        double tx = le.end.pos.getX();
        double ty = le.end.pos.getY();

        int signX =  (sx<tx) ? +1:-1;
        int signY =  (sy<ty) ? +1:-1;

        double px = (sx<tx) ? (sx + (tx-sx)*ratio):(sx - (sx-tx)*ratio) ;
        double py = (sy<ty) ? (sy + (ty-sy)*ratio):(sy - (sy-ty)*ratio) ;
        return new Point2D.Double(px,py);

    }


    private static boolean boundryEdge(BaseEdge e,GridDirectedGraph g) {
        int sx= ((GridVertex)e.start).gridPos.getX();
        int sy= ((GridVertex)e.start).gridPos.getY();

        int tx= ((GridVertex)e.end).gridPos.getX();
        int ty= ((GridVertex)e.end).gridPos.getY();

        if (sx==0 && tx == 0)
            return true;

        if (sy==0 && ty == 0)
            return true;

        if (sx==(g.xSize-1) && tx == (g.xSize-1) )
            return true;

        if (sy==(g.ySize-1) && ty == (g.ySize-1) )
            return true;

        return false;
    }

    public static void main (String[] args) throws IOException {
        genearate("caostarSample.txt",3,3,1,false);

    }

}
