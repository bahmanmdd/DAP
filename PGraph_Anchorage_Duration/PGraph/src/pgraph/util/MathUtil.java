package pgraph.util;

import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: oz
 * Date: 22.12.2012
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class MathUtil {

    public static double turnAngle(int x1,int y1, int x2,int y2,int x3,int y3 )
    {
        double ang = 0;
        int l1x = x2 - x1;
        int l1y = (y2 - y1)*-1; // Coordinate system adjustment
        int l2x = x3 - x2;
        int l2y = (y3 - y2)*(-1); // Coordinate system adjustment
        double ang1 = Math.toDegrees(Math.atan2(l1y,l1x));
        double ang2 = Math.toDegrees(Math.atan2(l2y,l2x));

        if (ang1<0)
            ang1= ang1+360;

        if (ang2<0)
            ang2= ang2+360;

        if (ang1>ang2)
        {
            ang = ang1-ang2;
            if (ang>180)
                ang = ang-360;
        }else
        {
            ang = ang2-ang1;
            if (ang<180)
                ang = -1*ang;
            else ang = 360-ang;
        }

        return ang;
    }

    public static double calculateEuclidian(int x1, int y1, int x2, int y2, double unitEdgeLen)
    {
        return (Math.sqrt(((x1-x2) * (x1-x2)) + (y1-y2)*(y1-y2))*unitEdgeLen);
    }

    public static boolean insideCone(int x1,int y1, int x2,int y2,int x3,int y3,double cwAngle,double ccwAngle )
    {
        double angle = turnAngle(x1,y1,x2,y2,x3,y3);

        return ((angle<cwAngle )&& (angle >-1*ccwAngle));
    }

    public static final double MIN_DIFFERENCE = 0.00001;
    public static boolean equalDoubles(double d1,double d2)
    {
        return Math.abs(d1-d2)<MIN_DIFFERENCE;
    }


    public static Set<Point2D> getIntersections(final Polygon2D poly, final Line2D.Double line) throws Exception {

        final PathIterator polyIt = poly.getPathIterator(null); //Getting an iterator along the polygon path
        final double[] coords = new double[6]; //Double array with length 6 needed by iterator
        final double[] firstCoords = new double[2]; //First point (needed for closing polygon path)
        final double[] lastCoords = new double[2]; //Previously visited point
        final Set<Point2D> intersections = new HashSet<Point2D>(); //List to hold found intersections
        polyIt.currentSegment(firstCoords); //Getting the first coordinate pair
        lastCoords[0] = firstCoords[0]; //Priming the previous coordinate pair
        lastCoords[1] = firstCoords[1];
        polyIt.next();
        while(!polyIt.isDone()) {
            final int type = polyIt.currentSegment(coords);
            switch(type) {
                case PathIterator.SEG_LINETO : {
                    final Line2D.Double currentLine = new Line2D.Double(lastCoords[0], lastCoords[1], coords[0], coords[1]);
                    if(currentLine.intersectsLine(line))
                        intersections.add(getIntersection(currentLine, line));
                    lastCoords[0] = coords[0];
                    lastCoords[1] = coords[1];
                    break;
                }
                case PathIterator.SEG_CLOSE : {
                    final Line2D.Double currentLine = new Line2D.Double(coords[0], coords[1], firstCoords[0], firstCoords[1]);
                    if(currentLine.intersectsLine(line))
                        intersections.add(getIntersection(currentLine, line));
                    break;
                }
                default : {
                    throw new Exception("Unsupported PathIterator segment type.");
                }
            }
            polyIt.next();
        }
        return intersections;

    }

    public static Point2D getIntersection(final Line2D.Double line1, final Line2D.Double line2) {

        final double x1,y1, x2,y2, x3,y3, x4,y4;
        x1 = line1.x1; y1 = line1.y1; x2 = line1.x2; y2 = line1.y2;
        x3 = line2.x1; y3 = line2.y1; x4 = line2.x2; y4 = line2.y2;
        final double x = (
                (x2 - x1)*(x3*y4 - x4*y3) - (x4 - x3)*(x1*y2 - x2*y1)
        ) /
                (
                        (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4)
                );
        final double y = (
                (y3 - y4)*(x1*y2 - x2*y1) - (y1 - y2)*(x3*y4 - x4*y3)
        ) /
                (
                        (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4)
                );

        return new Point2D.Double(x, y);

    }


    public static void main(String[] args)
	{
		
		double a = MathUtil.turnAngle(0, 0, -1, 0, 4, 1);
	
		a= a+1;
		
	}

    public static  double MIN_POINT_DIFFERENCE= 0.0001;
    public static boolean equalPoints(Point2D.Double p1, Point2D.Double p2) {
        return ((Math.abs(p1.getX()-p2.getX())<MIN_POINT_DIFFERENCE)&&(Math.abs(p1.getY()-p2.getY())<MIN_DIFFERENCE));
    }
}
