package pgraph;

import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.tag.TagEdgeInterface;
import pgraph.util.ArcMath;

import java.awt.*;
import java.awt.geom.*;

public class ArcEdge extends BaseEdge implements TagEdgeInterface
{
    Point2D.Double center;
    double radius;


    public ArcEdge(long id,BaseVertex argSource, BaseVertex argTarget, Point2D.Double c, double r)
    {
        super(id,argSource,argTarget,0);
        center =c ;
        radius = r;
        double   angle = Math.toRadians(Math.abs(((Arc2D.Double)shape()).extent));
        weight = r*angle;
        if (weight==0) {
            String aha = null;
        }
        r = r;
    }

    public ArcEdge(long id,BaseVertex argSource, BaseVertex argTarget, double argWeight)
    {
        super(id,argSource,argTarget,argWeight);
    }



    public Shape shape()
    {
        return ArcMath.makeArc(start.pos, end.pos, center);
    }
    @Override
    public Shape shape(int scale)
    {
        return ArcMath.makeArc(start.pos, end.pos, center);
    }

    @Override
    public double getLength() {
        double   angle = Math.toRadians(Math.abs(((Arc2D.Double)shape()).extent));
        return radius*angle;
    }

    @Override
    public boolean intersects(Shape s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int intersectionCount(Shape s) throws InstantiationException
    {
        if (!(s instanceof Ellipse2D.Double))
            throw new InstantiationException();

        Ellipse2D.Double diskShape = (Ellipse2D.Double)s;

        int  ic =  ArcMath.arcDiskIntersectionCount((Arc2D.Double) shape(), diskShape);

        return ic;
    }

    public boolean intersectsDiskSlow( Ellipse2D.Double diskShape)
    {
        if (diskShape.getCenterX() == center.getX() && diskShape.getCenterY() == center.getY() )
            return false;

        Area a1 = new Area(shape());
        Area a2 = new Area(diskShape);

        a1.intersect(a2);

        boolean b = a1.isEmpty();

        return !b;
    }
    public static final double INTERSECT_THRESHOLD=0.0001;
    public double diskIntersection( Ellipse2D.Double diskShape)
    {
        double   f =  (0.5 * ArcMath.arcDiskIntersectionCount((Arc2D.Double) shape(), diskShape));

        return f;
    }

    public String toString()
    {
        return "AE-"+id+"("+weight+")";
    }

    public static void main(String[] args)
    {
        Point2D.Double s = new Point2D.Double(140,70), e =new Point2D.Double(60,70),  c = new Point2D.Double(100,100);
        Arc2D.Double arc = (Arc2D.Double) ArcMath.makeArc(s, e, c);
        int ic = ArcMath.arcDiskIntersectionCount(arc,new Ellipse2D.Double(145,50,100,100));

        Line2D.Double line =new  Line2D.Double(100,120,100,150);
        Ellipse2D.Double d = new Ellipse2D.Double(50,50,100,100);
        double dist = line.ptSegDist(new Point2D.Double(100, 100));
        dist = dist;
    }

}
