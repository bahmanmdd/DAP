package pgraph;

import pgraph.util.IdManager;
import pgraph.util.MathUtil;
import pgraph.util.Polygon2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public class PolygonObstacleShape  implements ObstacleShape{
    Polygon2D polygon= null;


    public PolygonObstacleShape() {
    }

    public Shape shape() {
        return polygon;
    }
    @Override
    public Shape shape(int scale) {
        return polygon;
    }

    @Override
    public String toString() {
        String st = new String("pgraph.PolygonObstacleShape ");
        st += ""+polygon.npoints+" ";
        for (int i= 0 ;i<polygon.npoints;i++)
        {
            st += polygon.xpoints[i]+" " +polygon.ypoints[i] +" ";
        }
        return st;
    }

    @Override
    public void fromString(String st)
    {
        String[] stList = st.split(" ");
        int n = Integer.parseInt(stList[1]);
        double[] xpoints= new double[n];
        double[] ypoints= new double[n];
        for (int i=0;i<n;i++)
        {
            xpoints[i] = Double.parseDouble(stList[2*(i+1)]);
            ypoints[i] = Double.parseDouble(stList[2*(i+1)+1]);
        }
        polygon = new Polygon2D(xpoints,ypoints,n);
    }

    @Override
    public boolean intersectsLine(Point2D.Double p1, Point2D.Double p2) {

        Line2D.Double l = new Line2D.Double(p1.getX(),p1.getY(),p2.getX(),p2.getY());
        boolean  b = false;
        try {
            b = !MathUtil.getIntersections(polygon, l).isEmpty();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }

        if (b == true)
            b =b;

        return b;
    }


    public int lineIntersectionCount(LineEdge le)
    {
        int ic = 0;


        if (le.getLength()==0)
            return 0;

        Line2D.Double l = (Line2D.Double)le.shape();
        try {
            ic = MathUtil.getIntersections(polygon,l ).size();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return 0;
        }
        return ic;
    }

}
