package pgraph.specialzone;

import pgraph.LineEdge;
import pgraph.ObstacleShape;
import pgraph.base.BaseEdge;
import pgraph.util.ArcMath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:22
 * To change this template use File | Settings | File Templates.
 */
public class DiskZoneShape implements ZoneShape{

    Point2D.Double center;
    double radius;



    public DiskZoneShape() {
    }

    public DiskZoneShape(Point2D.Double center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Point2D.Double getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "pgraph.specialzone.DiskZoneShape "+ center.getX()+" "+center.getY()+" " + radius ;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
      public void fromString(String st) {
        String[] stList = st.split(" ");
        center = new Point2D.Double(Double.parseDouble(stList[1]),Double.parseDouble(stList[2])) ;
        radius = Double.parseDouble(stList[3]);
        return ;  //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public void fromString(String st, double d) {
        String[] stList = st.split(" ");
        center = new Point2D.Double(Double.parseDouble(stList[1]),Double.parseDouble(stList[2])) ;
        radius = Double.parseDouble(stList[3]);
        return ;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Shape shape() {
        return new Ellipse2D.Double((center.getX()-radius),(center.getY()-radius),2*radius,2*radius);
     }
    @Override
    public Shape shape(int scale) {
        return new Ellipse2D.Double((center.getX()-radius)*scale,(center.getY()-radius)*scale,2*radius*scale,2*radius*scale);
     }

    @Override
    public boolean isCovering(BaseEdge e) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
