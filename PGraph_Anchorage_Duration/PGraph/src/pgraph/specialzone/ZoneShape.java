package pgraph.specialzone;

import pgraph.LineEdge;
import pgraph.base.BaseEdge;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 27.01.2013
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
public interface ZoneShape {
    public String toString();
    public void fromString(String st);
    public void fromString(String st, double d);

    public Shape shape();
    public Shape shape(int scale);


    public boolean isCovering(BaseEdge e);

}
