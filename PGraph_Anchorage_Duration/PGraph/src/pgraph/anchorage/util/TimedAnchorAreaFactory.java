package pgraph.anchorage.util;

import pgraph.anchorage.AnchorArea;
import pgraph.anchorage.DepthZone;
import pgraph.anchorage.TimedAnchorArea;
import pgraph.util.Pen;
import pgraph.util.Polygon2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 08.02.2014
 * Time: 00:28
 * To change this template use File | Settings | File Templates.
 */
public class TimedAnchorAreaFactory {

    public static List<TimedAnchorArea> createExperimentAreaSet()
    {
        List<TimedAnchorArea> areaList = new ArrayList<TimedAnchorArea>();
        areaList.add(createTypeA());
        areaList.add(createTypeB());
        areaList.add(createTypeC());
        areaList.add(createTypeD());
        return areaList;
    }

    public static TimedAnchorArea createDemoArea() // RECTANGLE 1000-1000
    {
        TimedAnchorArea aa = new TimedAnchorArea();
        aa.setArea(new Polygon2D(new double[]{0, 0, 4000, 4000}, new double[]{0, 4000, 4000, 0}, 4));
        aa.setEntrySide(new Line2D.Double(0,4000,3000,4000));
        aa.setFirstEntryPoint(new Point2D.Double(2000,4000));
        aa.setMaximumDepth(10);
        aa.setScale(1);
        return aa;
    }

    public static TimedAnchorArea createTypeA() // RECTANGLE 5000-3000
    {
        TimedAnchorArea aa = new TimedAnchorArea();
        aa.setArea(new Polygon2D(new double[]{0, 0, 8000, 8000}, new double[]{0, 8000, 8000, 0}, 4));
        aa.setEntrySide(new Line2D.Double(0,8000,8000,8000));
        aa.setFirstEntryPoint(new Point2D.Double(4000,8000));
        aa.setMaximumDepth(35);
        aa.setScale(1);
        return aa;
    }


    public static TimedAnchorArea createTypeB() // IMKIZKENAR YAMUK 7000-4000
    {
        TimedAnchorArea aa = new TimedAnchorArea();
        aa.setArea( new Polygon2D(new double[]{500, 0, 8000, 7500}, new double[]{0, 8000, 8000, 0}, 4));
        aa.setEntrySide(new Line2D.Double(0,8000,8000,8000));
        aa.setFirstEntryPoint(new Point2D.Double(4000,8000));
        aa.setMaximumDepth(35);
        aa.setScale(1);
        return aa;
    }



    public static TimedAnchorArea createTypeC() // TERS IMKIZKENAR YAMUK 7000-4000
    {
        TimedAnchorArea aa = new TimedAnchorArea();
        aa.setArea( new Polygon2D(new double[]{0, 1000, 7000, 8000}, new double[]{0, 8000, 8000, 0}, 4));
        aa.setEntrySide(new Line2D.Double(1000,8000,7000,8000));
        aa.setFirstEntryPoint(new Point2D.Double(4000,8000));
        aa.setMaximumDepth(35);
        aa.setScale(1);
        return aa;
    }

    public static TimedAnchorArea createTypeD() // Kum saati  6000-8000
    {
        TimedAnchorArea aa = new TimedAnchorArea();
        aa.setArea( new Polygon2D(new double[]{0, 1000, 0, 6000,5000,6000}, new double[]{0, 4000, 8000, 8000,4000,0}, 6));
        aa.setEntrySide(new Line2D.Double(0,8000,6000,8000));
        aa.setFirstEntryPoint(new Point2D.Double(3000,8000));
        aa.setMaximumDepth(35);
        aa.setScale(1);
        return aa;
    }

    public static TimedAnchorArea createNonUniform1() // Non Uniform Area
    {
        DepthZone dz1 = new DepthZone(new Polygon2D(new double[]{50, 30, 350, 350}, new double[]{0, 160, 120, 0}, 4),1,"10m");
        dz1.setPen(new Pen(new Color(0, 0, 255, 90)));
        DepthZone dz2 = new DepthZone(new Polygon2D(new double[]{350, 350, 670, 650}, new double[]{0, 120, 160,0}, 4),2,"20m");
        dz2.setPen(new Pen(new Color(0,0,255,45)));
        DepthZone dz3 = new DepthZone(new Polygon2D(new double[]{30, 20,400, 680, 670,350}, new double[]{160, 240,300, 240, 160,120}, 6),3,"30m");
        dz3.setPen(new Pen(new Color(0,0,255,15)));
        DepthZone dz4 = new DepthZone(new Polygon2D(new double[]{ 20,400, 680, 700,0}, new double[]{240,300, 240, 400,400}, 5),3.5,"35m");
        dz4.setPen(new Pen(new Color(0,0,255,0)));

        TimedAnchorArea aa = new TimedAnchorArea();
        aa.setArea(new Polygon2D(new double[]{50, 0, 700, 650}, new double[]{0, 400, 400, 0}, 4));
        aa.setEntrySide(new Line2D.Double(0,400,700,400));
        aa.setFirstEntryPoint(new Point2D.Double(350, 400));
        aa.setMaximumDepth(3.5);

        aa.addDepthZone(dz1);
        aa.addDepthZone(dz2);
        aa.addDepthZone(dz3);
        aa.addDepthZone(dz4);

        aa.setScale(0.1);
        return aa;
    }

    public static TimedAnchorArea createNonUniform2() // Non Uniform Area
    {
        DepthZone dz1 = new DepthZone(new Polygon2D(new double[]{0, 0, 7000, 7000}, new double[]{0, 1200, 1200, 0}, 4),10,"10m");
        dz1.setPen(new Pen(new Color(0, 0, 255, 90)));
        DepthZone dz2 = new DepthZone(new Polygon2D(new double[]{0, 0, 7000, 7000}, new double[]{1200, 2500, 2500,1200}, 4),20,"20m");
        dz2.setPen(new Pen(new Color(0,0,255,45)));
        DepthZone dz3 = new DepthZone(new Polygon2D(new double[]{0, 0, 7000, 7000}, new double[]{2500, 4500, 4500,2500}, 4),35,"30m");
        dz3.setPen(new Pen(new Color(0,0,255,15)));
        DepthZone dz4 = new DepthZone(new Polygon2D(new double[]{0, 0, 7000, 7000}, new double[]{4500,7000,7000,4500}, 4),50,"35m");
        dz4.setPen(new Pen(new Color(0,0,255,0)));

        TimedAnchorArea aa = new TimedAnchorArea();
        aa.setArea(new Polygon2D(new double[]{0, 0, 7000, 7000}, new double[]{0, 7000, 7000, 0}, 4));
        aa.setEntrySide(new Line2D.Double(0,7000,7000,7000));
        aa.setFirstEntryPoint(new Point2D.Double(3500, 7000));
        aa.setMaximumDepth(35);

        aa.addDepthZone(dz1);
        aa.addDepthZone(dz2);
        aa.addDepthZone(dz3);
        aa.addDepthZone(dz4);

        aa.setScale(1);
        return aa;
    }


    public static TimedAnchorArea createNonUniform_AK1() // Non Uniform Area
    {
        DepthZone dz1 = new DepthZone(new Polygon2D(new double[]{0, 5000, 5000, 180}, new double[]{0, 0, 720, 720}, 4),10,"10m");
        dz1.setPen(new Pen(new Color(0, 0, 255, 90)));
        DepthZone dz2 = new DepthZone(new Polygon2D(new double[]{180, 5000, 5000, 400}, new double[]{720, 720, 1600,1600}, 4),20,"20m");
        dz2.setPen(new Pen(new Color(0,0,255,45)));
        DepthZone dz3 = new DepthZone(new Polygon2D(new double[]{2800, 5000, 5000, 1700}, new double[]{1600, 1600, 5600,3200}, 4),25,"25m");
        dz3.setPen(new Pen(new Color(0,0,255,15)));
        DepthZone dz4 = new DepthZone(new Polygon2D(new double[]{400, 2800, 1700, 600}, new double[]{1600,1600,3200,2400}, 4),30,"30m");
        dz4.setPen(new Pen(new Color(0,0,255,0)));

        TimedAnchorArea aa = new TimedAnchorArea();
        aa.setArea(new Polygon2D(new double[]{0, 5000, 5000, 600}, new double[]{0, 0, 5600, 2400}, 4));
        aa.setEntrySide(new Line2D.Double(600,2400,5000,5600));
        aa.setFirstEntryPoint(new Point2D.Double(1700, 3200));
        aa.setMaximumDepth(30);

        aa.addDepthZone(dz1);
        aa.addDepthZone(dz2);
        aa.addDepthZone(dz3);
        aa.addDepthZone(dz4);

        aa.setScale(1);
        return aa;
    }


    public static TimedAnchorArea createNonUniform_AK2() // Non Uniform Area
    {
        DepthZone dz1 = new DepthZone(new Polygon2D(new double[]{0, 6000, 6300, 300}, new double[]{0, 0, 1500, 1500}, 4),15,"15m");
        dz1.setPen(new Pen(new Color(0, 0, 255, 90)));
        DepthZone dz2 = new DepthZone(new Polygon2D(new double[]{4300, 6300, 6700, 4550}, new double[]{1500, 1500, 3500,2500}, 4),20,"20m");
        dz2.setPen(new Pen(new Color(0,0,255,45)));
        DepthZone dz3 = new DepthZone(new Polygon2D(new double[]{300, 4300, 4550, 6700,7000,5000,600}, new double[]{1500, 1500, 2500,3500,5000,5000,3000}, 7),25,"25m");
        dz3.setPen(new Pen(new Color(0,0,255,15)));
        DepthZone dz4 = new DepthZone(new Polygon2D(new double[]{600, 5000, 1000}, new double[]{3000,5000,5000}, 3),35,"35m");
        dz4.setPen(new Pen(new Color(0,0,255,0)));

        TimedAnchorArea aa = new TimedAnchorArea();
        aa.setArea(new Polygon2D(new double[]{0, 6000, 7000, 1000}, new double[]{0, 0, 5000, 5000}, 4));
        aa.setEntrySide(new Line2D.Double(1000,5000,7000,5000));
        aa.setFirstEntryPoint(new Point2D.Double(4000, 5000));
        aa.setMaximumDepth(35);

        aa.addDepthZone(dz1);
        aa.addDepthZone(dz2);
        aa.addDepthZone(dz3);
        aa.addDepthZone(dz4);

        aa.setScale(1);
        return aa;
    }

    public static TimedAnchorArea createNonUniform_AK3() // Non Uniform Area
    {
        DepthZone dz1 = new DepthZone(new Polygon2D(new double[]{0, 6000, 6300, 300}, new double[]{0, 0, 1500, 1500}, 4),10,"10m");
        dz1.setPen(new Pen(new Color(0, 0, 255, 120)));
        DepthZone dz2 = new DepthZone(new Polygon2D(new double[]{3000, 6300, 5000}, new double[]{1500, 1500, 2500}, 3),15,"15m");
        dz2.setPen(new Pen(new Color(0,0,255,100)));
        DepthZone dz3 = new DepthZone(new Polygon2D(new double[]{300, 3000, 600}, new double[]{1500, 1500, 3000}, 3),20,"20m");
        dz3.setPen(new Pen(new Color(0,0,255,80)));
        DepthZone dz4 = new DepthZone(new Polygon2D(new double[]{600, 3000, 5000,2000,1000}, new double[]{3000,1500,2500,5000,5000}, 5),25,"25m");
        dz4.setPen(new Pen(new Color(0,0,255,60)));
        DepthZone dz5 = new DepthZone(new Polygon2D(new double[]{5000, 6300, 6500,4000}, new double[]{2500,1500,2500,5000}, 4),30,"30m");
        dz5.setPen(new Pen(new Color(0,0,255,40)));
        DepthZone dz6 = new DepthZone(new Polygon2D(new double[]{5000, 4000, 2000}, new double[]{2500,5000,5000}, 3),40,"40m");
        dz6.setPen(new Pen(new Color(0,0,255,20)));
        DepthZone dz7 = new DepthZone(new Polygon2D(new double[]{6500, 6700,5000, 4000}, new double[]{2500,3500,5000,5000}, 4),50,"50m");
        dz7.setPen(new Pen(new Color(0,0,255,10)));
        DepthZone dz8 = new DepthZone(new Polygon2D(new double[]{6700, 7000, 7000}, new double[]{3500,5000,5000}, 3),70,"70m");
        dz8.setPen(new Pen(new Color(0,0,255,0)));


        TimedAnchorArea aa = new TimedAnchorArea();
        aa.setArea(new Polygon2D(new double[]{0, 6000, 7000, 1000}, new double[]{0, 0, 5000, 5000}, 4));
        aa.setEntrySide(new Line2D.Double(1000,5000,7000,5000));
        aa.setFirstEntryPoint(new Point2D.Double(4000, 5000));
        aa.setMaximumDepth(70);

        aa.addDepthZone(dz1);
        aa.addDepthZone(dz2);
        aa.addDepthZone(dz3);
        aa.addDepthZone(dz4);
        aa.addDepthZone(dz5);
        aa.addDepthZone(dz6);
        aa.addDepthZone(dz7);
        aa.addDepthZone(dz8);

        aa.setScale(1);
        return aa;
    }
}
