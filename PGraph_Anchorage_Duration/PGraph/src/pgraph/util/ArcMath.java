package pgraph.util;

import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 1/4/13
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArcMath {

    public static Point2D.Double getCircleCenter(Point2D.Double a, Point2D.Double b, Point2D.Double c) {
        double ax = a.getX();
        double ay = a.getY();
        double bx = b.getX();
        double by = b.getY();
        double cx = c.getX();
        double cy = c.getY();

        double A = bx - ax;
        double B = by - ay;
        double C = cx - ax;
        double D = cy - ay;

        double E = A * (ax + bx) + B * (ay + by);
        double F = C * (ax + cx) + D * (ay + cy);

        double G = 2 * (A * (cy - by) - B * (cx - bx));
        if (G == 0.0)
            return null; // a, b, c must be collinear

        double px = (D * E - B * F) / G;
        double py = (A * F - C * E) / G;
        return new Point2D.Double(px, py);
    }

    public static double makeAnglePositive(double angleDegrees) {
        double ret = angleDegrees;
        if (angleDegrees < 0) {
            ret = 360 + angleDegrees;
        }
        return ret;
    }

    public static double getNearestAnglePhase(double limitDegrees, double sourceDegrees, int dir) {
        double value = sourceDegrees;
        if (dir > 0) {
            while (value < limitDegrees) {
                value += 360.0;
            }
        } else if (dir < 0) {
            while (value > limitDegrees) {
                value -= 360.0;
            }
        }
        return value;
    }

    public static Arc2D makeArc(Point2D.Double s, Point2D.Double e,Point2D.Double c)
    {

        double radius = c.distance(s);

        Arc2D.Double arc = new Arc2D.Double(c.getX() - radius, c.getY() - radius, radius * 2, radius * 2, 0, 360,
                Arc2D.OPEN) ;

        arc.setAngles(new Point2D.Double(s.getX(),s.getY()), new Point2D.Double(e.getX(),e.getY()));

        if (arc.extent>180)
            arc.setAngleExtent(-1*(360-arc.extent));

        return arc;
    }

    public static double turnAngle(double x1,double y1, double x2,double y2,double x3,double y3 )
    {
        double ang = 0;
        double l1x = x2 - x1;
        double l1y = (y2 - y1)*-1; // Coordinate system adjustment
        double l2x = x3 - x2;
        double l2y = (y3 - y2)*(-1); // Coordinate system adjustment
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

    public static boolean sameDirection(double a1,double a2)
    {
        return (a1>0 && a2>0)||(a1<0&&a2<0);
    }

    public static double getAngle(Point2D.Double p, Point2D.Double c )
    {
        double radius = c.distance(p);

        Arc2D.Double arc = new Arc2D.Double(c.getX() - radius, c.getY() - radius, radius * 2, radius * 2, 0, 360,
                Arc2D.OPEN) ;

        arc.setAngles(new Point2D.Double(p.getX(),p.getY()), new Point2D.Double(p.getX(),p.getY()));

        return arc.start;
    }



    public static double getAngle(Point2D p, Point2D c )
    {
        double radius = p.distance(c);

        Arc2D.Double arc = new Arc2D.Double(c.getX() - radius, c.getY() - radius, radius * 2, radius * 2, 0, 360,
                Arc2D.OPEN) ;

        arc.setAngles( p, p);

        return arc.start;
    }



    public static double[][] getTangents(double x1, double y1, double r1, double x2, double y2, double r2) {
        double d_sq = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
        if (d_sq <= (r1-r2)*(r1-r2)) return new double[0][4];

        double d = Math.sqrt(d_sq);
        double vx = (x2 - x1) / d;
        double vy = (y2 - y1) / d;

        double[][] res = new double[4][4];
        int i = 0;

        // Let A, B be the centers, and C, D be points at which the tangent
        // touches first and second circle, and n be the normal vector to it.
        //
        // We have the system:
        //   n * n = 1          (n is a unit vector)
        //   C = A + r1 * n
        //   D = B +/- r2 * n
        //   n * CD = 0         (common orthogonality)
        //
        // n * CD = n * (AB +/- r2*n - r1*n) = AB*n - (r1 -/+ r2) = 0,  <=>
        // AB * n = (r1 -/+ r2), <=>
        // v * n = (r1 -/+ r2) / d,  where v = AB/|AB| = AB/d
        // This is a linear equation in unknown vector n.

        for (int sign1 = +1; sign1 >= -1; sign1 -= 2) {
            double c = (r1 - sign1 * r2) / d;

            // Now we're just intersecting a line with a circle: v*n=c, n*n=1

            if (c*c > 1.0) continue;
            double h = Math.sqrt(Math.max(0.0, 1.0 - c*c));

            for (int sign2 = +1; sign2 >= -1; sign2 -= 2) {
                double nx = vx * c - sign2 * h * vy;
                double ny = vy * c + sign2 * h * vx;

                double[] a = res[i++];
                a[0] = x1 + r1 * nx;
                a[1] = y1 + r1 * ny;
                a[2] = x2 + sign1 * r2 * nx;
                a[3] = y2 + sign1 * r2 * ny;
            }
        }

        if (r1==0 || r2 == 0)
            i = i/2;

        if (r1==0 && r2 == 0)
            i = i/2;

        return Arrays.copyOf(res, i);
    }


    public static Point2D.Double[][] getTangents(Point2D.Double c1, double r1, Point2D.Double c2, double r2)
    {
        double d_sq = (c1.getX() - c2.getX()) * (c1.getX() - c2.getX()) + (c1.getY() - c2.getY()) * (c1.getY() - c2.getY());
        if (d_sq <= (r1-r2)*(r1-r2)) return new Point2D.Double[0][2];

        double d = Math.sqrt(d_sq);
        double vx = (c2.getX() - c1.getX()) / d;
        double vy = (c2.getY() - c1.getY()) / d;

        Point2D.Double[][] res = new Point2D.Double[4][2];
        int i = 0;

        // Let A, B be the centers, and C, D be points at which the tangent
        // touches first and second circle, and n be the normal vector to it.
        //
        // We have the system:
        //   n * n = 1          (n is a unit vector)
        //   C = A + r1 * n
        //   D = B +/- r2 * n
        //   n * CD = 0         (common orthogonality)
        //
        // n * CD = n * (AB +/- r2*n - r1*n) = AB*n - (r1 -/+ r2) = 0,  <=>
        // AB * n = (r1 -/+ r2), <=>
        // v * n = (r1 -/+ r2) / d,  where v = AB/|AB| = AB/d
        // This is a linear equation in unknown vector n.

        for (int sign1 = +1; sign1 >= -1; sign1 -= 2) {
            double c = (r1 - sign1 * r2) / d;

            // Now we're just intersecting a line with a circle: v*n=c, n*n=1

            if (c*c > 1.0) continue;
            double h = Math.sqrt(Math.max(0.0, 1.0 - c*c));

            for (int sign2 = +1; sign2 >= -1; sign2 -= 2) {
                double nx = vx * c - sign2 * h * vy;
                double ny = vy * c + sign2 * h * vx;

                Point2D.Double[] a = res[i++];
                a[0] = new Point2D.Double(c1.getX()+r1*nx,c1.getY()+r1*ny);
                a[1] = new Point2D.Double(c2.getX()+sign1*r2*nx,c2.getY()+sign1*r2*ny);
            }
        }

        if (r1==0 || r2 == 0)
            i = i/2;

        if (r1==0 && r2 == 0)
            i = i/2;

        return Arrays.copyOf(res, i);
    }

    public static final double INTERSECT_THRESHOLD=0.0001;
    public static final double MIN_DISTANCE = 0.0001;
    public static int arcDiskIntersectionCount(Arc2D.Double arc, Ellipse2D.Double diskShape)
    {
        Point2D ap1 = arc.getStartPoint();
        Point2D ap2 = arc.getEndPoint();
        Point2D ac  = new Point2D.Double(arc.getCenterX(),arc.getCenterY());
        double rarc = arc.getHeight()/2;

        Point2D dc  = new Point2D.Double(diskShape.getCenterX(),diskShape.getCenterY());
        double  rdisk =  ((diskShape.getHeight()/2)-INTERSECT_THRESHOLD);


        Point2D.Double pac = new Point2D.Double(arc.getCenterX(),arc.getCenterY());
        Point2D.Double pdc = new Point2D.Double(diskShape.getCenterX(),diskShape.getCenterY());

        if ( MathUtil.equalPoints( pdc,pac))
        {
            return 0;
        }

        double dist =dc.distance(ac);
        if ( dist<=MIN_DISTANCE)
            return 0 ;
        if(dist>=rdisk+rarc )
            return 0;

        Line2D.Double line = new Line2D.Double(ap1,ap2);
        double d = line.ptSegDist(dc);



        if (d>=rdisk)
        {
            double angle = ArcMath.getAngle(dc,ac);

            if (arc.containsAngle(angle))
                return 2;
            else return 0;
        }
        else
        {
            int f =0;
            if (ap1.distance(dc)>=rdisk)
                f+= 1;
            if (ap2.distance(dc)>=rdisk)
                f+= 1;

            return f;
        }
    }

    public static int secondTurnConstraint(double distanceToTurn, double turnAngle, double minTurnRadius) {


        return 0;  //To change body of created methods use File | Settings | File Templates.
    }


    public enum TurnDirection{ CW, CCW };
    public static double maxTurnAngle(double tangentLength, double minTurnRadius)
    {
        double a =Math.toDegrees(Math.atan2(minTurnRadius,tangentLength));
        return 180-2*a;
    }

    public static double minTurnDistance(double turnAngle, double minTurnRadius)
    {
        double a = (180-turnAngle)/2;

        double  turnDistance= minTurnRadius/Math.tan(Math.toRadians(a));
        return turnDistance;
    }


    public static double minTurnDistanceFromDegree(int d, double  minTurnRadius)
    {
        double angle =  180 - Math.toDegrees(Math.atan2(1,d));
        return ArcMath.minTurnDistance(angle,minTurnRadius);
    }


    public static void main(String[] args)
    {
        double mintd = minTurnDistance(Math.toDegrees(Math.atan2(1,2)),400);
        double mtd0 = minTurnDistance(45,10000) ;
        double mtd10 = minTurnDistance(5,20) ; ;
        double mtd11 = minTurnDistance(1,20) ; ;

        double ang1 = maxTurnAngle(1,20);
        double ang2 = maxTurnAngle(5,20);
        double ang3 = maxTurnAngle(15,20);
        double ang4 = maxTurnAngle(25,20);

        double mtd = minTurnDistance(5,20);
        double mtd2 = minTurnDistance(15,20);
        double mtd3 = minTurnDistance(45,20);
        double mtd4 = minTurnDistance(85,20);
        double mtd5 = minTurnDistance(175,20);
    }

}
