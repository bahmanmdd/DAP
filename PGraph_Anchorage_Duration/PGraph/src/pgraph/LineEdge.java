package pgraph;

import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.tag.TagEdgeInterface;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 19.01.2013
 * Time: 10:13
 * To change this template use File | Settings | File Templates.
 */
public class LineEdge extends BaseEdge  implements TagEdgeInterface {

    public LineEdge(long id, BaseVertex s, BaseVertex e)
    {
        super(id,s,e,s.pos.distance(e.pos));
    }

    @Override
    public Shape shape() {
        return new Line2D.Double(start.pos.getX(),start.pos.getY(),end.pos.getX(),end.pos.getY());
    }
    @Override
    public Shape shape(int scale) {
        return new Line2D.Double(start.pos.getX()*scale,start.pos.getY()*scale,end.pos.getX()*scale,end.pos.getY()*scale);
    }

    @Override
    public boolean intersects(Shape s) {
        Area a1 = new Area(s);
        Area a2 = new Area(shape());

        a1.intersect(a2);
        boolean  b = a1.isEmpty();
        return !b;
    }

    @Override
    public int intersectionCount(Shape s) throws InstantiationException {
        if (!(s instanceof Ellipse2D.Double))
            throw new InstantiationException();
        Ellipse2D.Double diskShape = (Ellipse2D.Double)s;

        Point2D.Double p1 = start.pos;
        Point2D.Double p2 = end.pos;

        Line2D l = new Line2D.Double(p1.getX(),p1.getY(),p2.getX(),p2.getY());

        double d = l.ptSegDist(diskShape.getCenterX(),diskShape.getCenterY());

        double   r = ((diskShape.getHeight()/2)-INTERSECT_THRESHOLD);

        if (d>=r)
            return 0;

        Point2D.Double c = new Point2D.Double(diskShape.getCenterX(),diskShape.getCenterY());
        int f=0;
        if(p1.distance(c)>=r)
            f+=1;
        if(p2.distance(c)>=r)
            f+=1;
        return f;
    }

    public double getLength()
    {
        return start.pos.distance(end.pos) ;
    }

    public static final double INTERSECT_THRESHOLD=0.01;
    @Override
    public double diskIntersection(Ellipse2D.Double diskShape) {
        Point2D.Double p1 = start.pos;
        Point2D.Double p2 = end.pos;

        Line2D l = new Line2D.Double(p1.getX(),p1.getY(),p2.getX(),p2.getY());

        double d = l.ptSegDist(diskShape.getCenterX(),diskShape.getCenterY());

        double   r = ((diskShape.getHeight()/2)-INTERSECT_THRESHOLD);

        if (d>=r)
            return 0;

        Point2D.Double c = new Point2D.Double(diskShape.getCenterX(),diskShape.getCenterY());
        double f=0;
        if(p1.distance(c)>=r)
            f+=0.5;
        if(p2.distance(c)>=r)
            f+=0.5;
        return f;
    }
    public String toString()
    {
        return "LE-"+id+"("+weight+")";
    }


}
