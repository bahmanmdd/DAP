package pgraph;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 27.01.2013
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
public interface ObstacleShape {
    public String toString();
    public void fromString(String st);

    public boolean intersectsLine(Point2D.Double p1,Point2D.Double p2);


    public int lineIntersectionCount(LineEdge le) throws InstantiationException;

    public Shape shape();
    public Shape shape(int scale);

}
