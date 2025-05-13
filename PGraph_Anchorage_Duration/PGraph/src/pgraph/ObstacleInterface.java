package pgraph;

import pgraph.util.Pen;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:20
 * To change this template use File | Settings | File Templates.
 */
public interface ObstacleInterface extends Drawable , Comparable<ObstacleInterface>
{
    public long getId();
    public void setId(long id) ;

    public double getObstacleWeight();
    public boolean isPassable();

    public ObstacleShape getObstacleShape();

    public void setWeight(double w);
    public void setPassable(boolean  p);

    public Pen getPen();
    public String toString();
    public void fromString(String st);

    public boolean intersectsLine(Point2D.Double p1,Point2D.Double p2);
    public int lineIntersectionCount(LineEdge le) throws InstantiationException;

}
