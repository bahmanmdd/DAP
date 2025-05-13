package pgraph.specialzone;

import pgraph.LineEdge;
import pgraph.ObstacleShape;
import pgraph.base.BaseEdge;
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
public class PolygonZoneShape implements ZoneShape{
    Polygon2D polygon= null;


    public PolygonZoneShape() {
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
        String st = new String("pgraph.specialzone.PolygonZoneShape ");
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
    public void fromString(String st, double BZdistance)
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
          //  BufferZone bz = new BufferZone(BZdistance,StepAngle);
         //   polygon = bz.createBufferZone(polygon);

    }

    @Override
    public boolean isCovering(BaseEdge e) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
