package pgraph.util;

import math.geom2d.conic.Circle2D;
import pgraph.anchorage.AnchorArea;
import pgraph.gui.GraphViewer;


import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 30.10.2013
 * Time: 10:04
 * To change this template use File | Settings | File Templates.
 */
public class GeomUtil {


    public static java.util.List<Point2D> getCircleLineIntersectionPoint(Point2D pointA,
                                                             Point2D pointB, Point2D center, double radius)
    {
        double baX = pointB.getX() - pointA.getX();
        double baY = pointB.getY() - pointA.getY();
        double caX = center.getX() - pointA.getX();
        double caY = center.getY() - pointA.getY();

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point2D p1 = new Point2D.Double(pointA.getX() - baX * abScalingFactor1, pointA.getY()
                - baY * abScalingFactor1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        Point2D p2 = new Point2D.Double(pointA.getX() - baX * abScalingFactor2, pointA.getY()
                - baY * abScalingFactor2);
        return Arrays.asList(p1, p2);
    }

    public static Circle2D getTangentCircleAdjusted(Circle2D d1  , Line2D l2, double r,Circle2D oldCircle )
    {
        java.util.List<Circle2D> cl = getTangentCircles(d1,l2,r);
        Circle2D adjusted = getClosestCircle(cl,oldCircle.center().getAsDouble());
        return adjusted;
    }


    public static java.util.List<Circle2D> getTangentCircles(Circle2D d1 , Line2D l, double r )
    {
        double rt_2 = (r + d1.radius())*(r + d1.radius());

        Point2D p1 = l.getP1();
        Point2D p2 = l.getP2();

        double x1 = p1.getX(); double y1 = p1.getY();  double x2 = p2.getX(); double y2 = p2.getY();

        Point2D pointA,pointB,pointA2,pointB2;

        if (x1 != x2)
        {
            double m = (y1-y2)/(x1-x2);
            double n = y1 - ( ((y1-y2)/(x1-x2))*x1  );

            double n2 = n- ( r / Math.cos(Math.atan(m)) );
            double n2_2 = n+ ( r / Math.cos(Math.atan(m)) );

            pointA= new Point2D.Double(-10000, -10000*m + n2);
            pointB= new Point2D.Double(10000, 10000*m + n2);

            pointA2= new Point2D.Double(-10000, -10000*m + n2_2);
            pointB2= new Point2D.Double(10000, 10000*m + n2_2);

        }
        else
        {
            pointA= new Point2D.Double( x1+r, -10000);
            pointB= new Point2D.Double( x1+r, 10000 );

            pointA2= new Point2D.Double( x1-r, -10000);
            pointB2= new Point2D.Double( x1-r, 10000);
        }

        //System.out.println(d1);   System.out.println("Line : "+ l.getP1() + " - " + l.getP2()  );


        java.util.List<Point2D> plist = new ArrayList<Point2D>();
        plist.addAll(getCircleLineIntersectionPoint(pointA,pointB,d1.center().getAsDouble(),d1.radius()+r));
        plist.addAll(getCircleLineIntersectionPoint(pointA2,pointB2,d1.center().getAsDouble(),d1.radius()+r));

        java.util.List<Circle2D> cl = new ArrayList<>();
        for (Point2D p:plist)
        {
            cl.add(new Circle2D(p.getX(),p.getY(),r));
        }

        return cl;



    }

    public static Circle2D getClosestCircle(java.util.List<Circle2D> cList, Point2D p)
    {
        double d = Double.MAX_VALUE;
        Circle2D closest = null;
        for (Circle2D c : cList)
        {
            if (d > c.center().distance(p.getX(),p.getY()) )
            {
                closest = c;
                d = c.center().distance(p.getX(),p.getY());
            }
        }
        return closest;
    }
    public static Circle2D getTangentCircleAdjusted(Line2D l1 , Line2D l2, double r,Circle2D oldCircle )
    {
        java.util.List<Circle2D> cl = getTangentCircles(l1,l2,r);
        Circle2D adjusted = getClosestCircle(cl,oldCircle.center().getAsDouble());
        return adjusted;
    }


    public static final double COLLISION_TRESHOLD = 0.001;
    public static java.util.List<Circle2D> getTangentCircles(Line2D l1 , Line2D l2, double r )
    {

        java.util.List<Circle2D> cl = new ArrayList<>();

        if ( ( l1.ptSegDist(l2.getP1())<COLLISION_TRESHOLD && l1.ptSegDist(l2.getP2())<COLLISION_TRESHOLD ) || ( l2.ptSegDist(l1.getP1())<COLLISION_TRESHOLD && l2.ptSegDist(l1.getP2())<COLLISION_TRESHOLD  )   )
        {
            // Same lines. No circle
            return cl;
        }

        Point2D p1 = l1.getP1();
        Point2D p2 = l1.getP2();

        double x1 = p1.getX(); double y1 = p1.getY();  double x2 = p2.getX(); double y2 = p2.getY();

        double a1 = y2-y1;
        double b1 = x1-x2;
        double c1 = (x2-x1)*y1 + (y1-y2)*x1;
        double d1 = r*Math.sqrt(a1*a1 + b1*b1);

        p1 = l2.getP1();
        p2 = l2.getP2();

        x1 = p1.getX(); y1 = p1.getY();  x2 = p2.getX(); y2 = p2.getY();

        double a2 = y2-y1;
        double b2 = x1-x2;
        double c2 = (x2-x1)*y1 + (y1-y2)*x1;
        double d2 = r*Math.sqrt(a2*a2 + b2*b2);

        double x= 0,y=0;

        double TANGENT_THRESHOLD=0.01;

        if (a1 != 0 &&  a1*b2 != b1*a2 )
        {
            y = (d1*a2 - d2*a1 + c2*a1 - c1*a2)/(b1*a2-b2*a1);
            x = (d1-c1-b1*y)/a1;
            if ( ( l1.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) && ( l2.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) )
            {
                cl.add(new Circle2D(x,y,r));
            }


            y = (d1*a2 + d2*a1 + c2*a1 - c1*a2)/(b1*a2-b2*a1);
            x = (d1-c1-b1*y)/a1;
            if ( ( l1.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) && ( l2.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) )
            {
                cl.add(new Circle2D(x,y,r));
            }

            y = (-d1*a2 + d2*a1 + c2*a1 - c1*a2)/(b1*a2-b2*a1);
            x = (-d1-c1-b1*y)/a1;
            if ( ( l1.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) && ( l2.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) )  )
            {
                cl.add(new Circle2D(x,y,r));
            }

            y = (-d1*a2 - d2*a1 - c2*a1 - c1*a2)/(b1*a2-b2*a1);
            x = (-d1-c1-b1*y)/a1;
            if ( ( l1.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) && ( l2.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) )  )
            {
                cl.add(new Circle2D(x,y,r));
            }
        }
        else if (a1 == 0 && b1 !=0 && a2 !=0 )
        {
            y = (d1 - c1)/b1;
            x = (d2-b2*y-c2)/a2;
            if ( ( l1.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) && ( l2.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) )
            {
                cl.add(new Circle2D(x,y,r));
            }

            y = (-d1 - c1)/b1;
            x = (d2-b2*y-c2)/a2;
            if ( ( l1.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) && ( l2.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) )
            {
                cl.add(new Circle2D(x,y,r));
            }

            y = (d1 - c1)/b1;
            x = (-d2-b2*y-c2)/a2;
            if ( ( l1.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) && ( l2.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) )
            {
                cl.add(new Circle2D(x,y,r));
            }

            y = (-d1 - c1)/b1;
            x = (-d2-b2*y-c2)/a2;
            if ( ( l1.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) && ( l2.ptSegDist(x,y)< (r + TANGENT_THRESHOLD ) ) )
            {
                cl.add(new Circle2D(x,y,r));
            }
        }

        return cl;

    }

    public static java.util.List<Circle2D> getTangentCirclesOld(Line2D l1 , Line2D l2, double r )
    {
        Point2D p1 = l1.getP1();
        Point2D p2 = l1.getP2();

        double x1 = p1.getX(); double y1 = p1.getY();  double x2 = p2.getX(); double y2 = p2.getY();

        double m1 = (y1-y2)/(x1-x2);
        double n1 = y1 - ( ((y1-y2)/(x1-x2))*x1  );

        p1 = l2.getP1();
        p2 = l2.getP2();

        x1 = p1.getX(); y1 = p1.getY();  x2 = p2.getX(); y2 = p2.getY();

        double m2 = (y1-y2)/(x1-x2);
        double n2 = y1 - ( ((y1-y2)/(x1-x2))*x1  );

        double x = ( r *(Math.sqrt(m2*m2+1) - Math.sqrt(m1*m1+1)) + n2 - n1)/(m1-m2);
        double y = r*Math.sqrt(m1*m1+1) + m1*x + n1;






        java.util.List<Circle2D> cl = new ArrayList<>();

        cl.add(new Circle2D(x,y,r));

        return cl;



    }

    public static Circle2D getTangentCircleAdjusted(Circle2D d1 , Circle2D d2, double r,Circle2D oldCircle )
    {
        java.util.List<Circle2D> cl = getTangentCircles(d1,d2,r);
        Circle2D adjusted = getClosestCircle(cl,oldCircle.center().getAsDouble());
        return adjusted;
    }


    public static java.util.List<Circle2D> getTangentCircles(Circle2D d1 , Circle2D d2, double r )
    {
        Point2D c1 = d1.center().getAsDouble();
        Point2D c2 = d2.center().getAsDouble();

        double a = Math.sqrt((c1.getX()-c2.getX())*(c1.getX()-c2.getX()) + (c1.getY()-c2.getY())*(c1.getY()-c2.getY())  ) ;
        double b = d1.radius() + r;
        double c = d2.radius() + r;

        double cosC = (b*b + a*a - c*c)/(2*b*a);
        double C = Math.acos(cosC);

        double Ch = Math.atan2((c2.getY()-c1.getY()),(c2.getX()-c1.getX()));

        double Ctotal = C+ Ch;

        double x3 = c1.getX()+ (b*Math.cos(Ctotal));
        double y3 = c1.getY()+ (b*Math.sin(Ctotal));

        Circle2D d = new Circle2D(x3,y3,r);

        java.util.List<Circle2D> cl = new ArrayList<>();
        cl.add(d);


        Ctotal = 2*Math.PI -  C+ Ch;

        x3 = c1.getX()+ (b*Math.cos(Ctotal));
        y3 = c1.getY()+ (b*Math.sin(Ctotal));

        d = new Circle2D(x3,y3,r);
        cl.add(d);

        return cl;
    }


    public static Point2D getClosestPointOnSegment(Line2D l,Point2D p)
    {
        return getClosestPointOnSegment(l.getX1(),l.getY1(),l.getX2(),l.getY2(),p.getX(),p.getY());
    }

    public static Point2D getClosestPointOnSegment(double sx1, double sy1, double sx2, double sy2, double px, double py)
    {
        double xDelta = sx2 - sx1;
        double yDelta = sy2 - sy1;

        if ((xDelta == 0) && (yDelta == 0))
        {
            throw new IllegalArgumentException("Segment start equals segment end");
        }

        double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final Point2D closestPoint;
        if (u < 0)
        {
            closestPoint = new Point2D.Double(sx1, sy1);
        }
        else if (u > 1)
        {
            closestPoint = new Point2D.Double(sx2, sy2);
        }
        else
        {
            closestPoint = new Point2D.Double(sx1 + u * xDelta, sy1 + u * yDelta);
        }

        return closestPoint;
    }


    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        test5();

    }

    private static void test4() {
        AnchorArea aa = new AnchorArea();
        aa.setArea(new Polygon2D(new double[]{0, 0, 100, 100}, new double[]{0, 100, 100, 0}, 4));

        aa.setExistingAnchorages(aa.getAllCandidateAnchorages(10));

        aa.setCandidateAnchorages(aa.getAllCandidateAnchorages(5));

        GraphViewer.showContent(aa,0,100,0,100);
    }

    private static void test5() {
        AnchorArea aa = new AnchorArea();
        aa.setArea(new Polygon2D(new double[]{0, 0, 100, 100}, new double[]{0, 100, 100, 0}, 4));

        aa.setEntrySide(new Line2D.Double(0,100,100,100));

        aa.setExistingAnchorages(aa.getAllCandidateAnchorages(10));








        GraphViewer.showContent(aa,0,100,0,100);
    }

    private static void test1() {

        Circle2D d1 = new Circle2D(10,70,30);
        Circle2D d2 = new Circle2D(50,0,5);


        java.util.List<Circle2D> cl = GeomUtil.getTangentCircles(d2, d1, 40.0);
        cl.add(d1);   cl.add(d2);

        AnchorArea cc = new AnchorArea();
        cc.setExistingAnchoragesFromCircles(cl);

        GraphViewer.showContent(cc,0,100,0,100);
    }


    private static void test2() {

        Circle2D d1 = new Circle2D(90,90,10);
        Line2D l = new Line2D.Double(100,100,100,0);


        java.util.List<Circle2D> cl = GeomUtil.getTangentCircles(d1, l , 10.0);
        cl.add(d1);

        AnchorArea cc = new AnchorArea();
        cc.setExistingAnchoragesFromCircles(cl);
        cc.setArea(new Polygon2D(new double[]{0, 0, 100, 100}, new double[]{0, 100, 100, 0}, 4));

        GraphViewer.showContent(cc,0,100,0,100);
    }

    private static void test3() {

        Line2D l2 = new Line2D.Double(0,0,0,100);
        Line2D l1 = new Line2D.Double(0,0,100,0);


        java.util.List<Circle2D> cl = GeomUtil.getTangentCircles(l1, l2 , 10.0);


        AnchorArea cc = new AnchorArea();
        cc.setExistingAnchoragesFromCircles(cl);
        cc.setArea(new Polygon2D(new double[]{0, 0, 100, 100}, new double[]{0, 100, 100, 0}, 4));

        GraphViewer.showContent(cc,0,100,0,100);
    }


}
