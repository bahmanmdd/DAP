package pgraph.anchorage.util;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 29.01.2014
 * Time: 10:26
 * To change this template use File | Settings | File Templates.
 */
public class ShipDynamics {

    private static final double DRAFT_RATIO = 10.5;

    public static double getDraft(double length)
    {
/*      Draft (also known as draught): Depth of the ship from water line to ship’s bottom (geminin su altında kalan kısmının
        derinliği)
        Length/Draft = 10.5 for tankers, 11.75 for bulk carriers and containers.
        Reference: Practical Ship Design, D.G.M Watson, 1991.*/

        return length/DRAFT_RATIO;
    }

/*  UKC = Under Keel Clearance: safety distance from the ship’s bottom to the sea bottom.
    UKCF = UKC Factor as a function of the ship’s draft.
    UKCF = 1.5 at anchorages.*/

    public static final double UKCF = 1.5;

    public static double getSafeDepth(double length)
    {
/*        D(Length) = Draft * UKCF = Length / (10.5 or 11.75) * 1.5*/

        return getDraft(length) * UKCF;
    }

    public static double getSafeLength(double depth)
    {
/*        D(Length) = Draft * UKCF = Length / (10.5 or 11.75) * 1.5*/

        return (depth/UKCF)*DRAFT_RATIO;
    }

    public static double getInnerRadius(double length)
    {
/*        Ship’s danger circle radius = r1 = Length.
          This is the inner circle that other ships must avoid. The ship’s center of mass is the center of this danger circle. Thus,
          we are recommending a distance of Length/2 for safety*/

        return length;
    }

    public static double getShackle(double depth )
    {
/*        Recommended anchorage shackle length: 25 * root(D). Example: D = 25  shackle length is 125 meters.
          Reference: The theory and practice of seamanship, Graham Danton, 1996.*/

          return Math.sqrt(depth)* 25;
    }


    public static double getOuterRadius(double length , double depth, double scale)
    {
/*        Swing circle: this is the outer circle for no-risk anchorage.

            Swing circle radius = r2 = b + Length

          b: consider the a-b-c right triangle where c is the hypotenuse which is the shackle length; 25 * root(D). Here, a is the
          water depth. Therefore b is the over-the-water distance from the anchorage center to the maximum swing distance. We
          are adding Length to this quantity b to determine the swing circle radius.
          Observe that r2 is a function of Depth and Length.
          Also observe that r2 – r1 = b.*/


        depth = depth/scale;
        length = length/scale;

        double shackle = getShackle(depth);

        double r = length + Math.sqrt(shackle*shackle - depth*depth);

        return r*scale;
    }


    public static double getOuterRadius(double length , double depth, double scale ,double riskFactor)
    {
/*      risk:  0  means outer radius does not change.
        risk:  1  means outer radius = inner radius*/

        double outer = getOuterRadius(length,depth,scale);
        double inner = getInnerRadius(length);

        return outer - (outer-inner)*riskFactor;

    }


/*      L: 30.0     Depth(L):  4.28     Inner(L):  30.0     Outer(L):  81.57
        L: 100.0    Depth(L): 14.28     Inner(L): 100.0     Outer(L): 193.40
        L: 150.0    Depth(L): 21.42     Inner(L): 150.0     Outer(L): 263.72
        L: 200.0    Depth(L): 28.57     Inner(L): 200.0     Outer(L): 330.54
        L: 250.0    Depth(L): 35.71     Inner(L): 250.0     Outer(L): 395.07*/

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {
        test1();

    }

    private static void test1() {

        final int slices[] = {3,10,15,20,25,30,35};

        for (int i = 0; i<slices.length;i++)
        {
            double l = slices[i];
            double d = getSafeDepth(l);
            System.out.println("L: " +l+ "  Depth(L): "+ d+ " Inner(L): "+ getInnerRadius(l) +"   Outer(L): "+ getOuterRadius(l,d,0.1)  );

        }
    }

    private static void test2() {

        final int depthList[] = {10,20,30,40,50,60,70,80,90,100};

        double l = 50;
        for (int i = 0; i<depthList.length;i++)
        {

            double d = depthList[i];
            System.out.println("L: " +l+ "  Depth(L): "+ d +"   Outer(L): "+ getOuterRadius(l,d,1)  );

        }
    }
}
