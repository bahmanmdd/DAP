package pgraph.anchorage;

import pgraph.Path;
import pgraph.anchorage.distributions.AhkpArrivalGenerator;
import pgraph.anchorage.distributions.AhkpGammaArrivalGenerator;
import pgraph.anchorage.distributions.ArrivalGenerator;
import pgraph.anchorage.policy.AnchorPolicy;
import pgraph.anchorage.policy.HybridAnchoragePolicyV2;
import pgraph.anchorage.util.AnchorAreaFactory;
import pgraph.gui.GraphViewer;
import pgraph.util.Pen;
import pgraph.util.Polygon2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:00
 * To change this template use File | Settings | File Templates.
 */
public class AnchorageManager {

    GraphViewer gv= null;
    private static final long REFRESH_INTERVAL = 1000;
    AnchorArea anchorArea;
    ArrivalGenerator arrivalGenerator;
    AnchorPolicy anchorPolicy;

    private boolean finished= false;



    private double riskFactor=0;


    public AnchorageManager(AnchorArea anchorArea, ArrivalGenerator arrivalGenerator, AnchorPolicy anchorPolicy) {
        this.anchorArea = anchorArea;
        this.arrivalGenerator = arrivalGenerator;
        this.anchorPolicy = anchorPolicy;
    }

    public void  showAnchorageanimation() throws InterruptedException {
        gv = GraphViewer.showContent(anchorArea);
        //gv.setIgnoreRepaint(true);
        waitandRefresh(50*REFRESH_INTERVAL);

        while (!finished)
        {
            ArrivalInterface arrival = arrivalGenerator.generate();
            Anchorage anchorage = anchorPolicy.createAnchorage(anchorArea,arrival);


            if (anchorage == null)
            {
                anchorArea.newArrival(arrival);
                waitandRefresh(REFRESH_INTERVAL);
                finished = true;
                continue;
            }
            anchorage.setPen(new Pen(Color.red, Pen.PenStyle.PS_Pointed));

            Point2D entryPoint = anchorArea.calculateEntryPoint(anchorage.area.center().getAsDouble());


            anchorArea.newArrival(arrival);

            waitandRefresh(REFRESH_INTERVAL);
            waitandRefresh(REFRESH_INTERVAL);

            anchorArea.repositionArrivals(entryPoint);

            waitandRefresh(REFRESH_INTERVAL);

            anchorArea.setCurrentAnchoragePath(new Path(anchorPolicy.getLastAnchoragePath(),new Pen(Color.blue, Pen.PenStyle.PS_Pointed)));
            anchorArea.addExistingAnchorage(anchorage);


            waitandRefresh(REFRESH_INTERVAL);
            waitandRefresh(REFRESH_INTERVAL);

            anchorage.setPen(Pen.DefaultPen);
            anchorArea.clearArrivals();
            anchorArea.setCurrentAnchoragePath(null);

            //waitandRefresh(REFRESH_INTERVAL);

        }

        System.out.println("ANCHORAGE AREA IS FULL");
        Thread.sleep(REFRESH_INTERVAL);
        gv.repaint();
    }

    private void waitandRefresh(long refreshInterval) throws InterruptedException {
        Thread.sleep(refreshInterval);
        if (gv != null)
            gv.repaint();
    }

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {

        testNonUniform();
        //test3();

    }

    private static void test1() throws InterruptedException {

        DepthZone dz1 = new DepthZone(new Polygon2D(new double[]{50, 30, 350, 350}, new double[]{0, 160, 120, 0}, 4),1.5,"15m");
        dz1.setPen(new Pen(new Color(0,0,255,90)));
        DepthZone dz2 = new DepthZone(new Polygon2D(new double[]{350, 350, 670, 650}, new double[]{0, 120, 160,0}, 4),2.5,"25m");
        dz2.setPen(new Pen(new Color(0,0,255,45)));
        DepthZone dz3 = new DepthZone(new Polygon2D(new double[]{30, 20,400, 680, 670,350}, new double[]{160, 240,300, 240, 160,120}, 6),3,"30m");
        dz3.setPen(new Pen(new Color(0,0,255,15)));

        AnchorArea aa = new AnchorArea();
        aa.setArea(new Polygon2D(new double[]{50, 0, 700, 650}, new double[]{0, 400, 400, 0}, 4));
        aa.setEntrySide(new Line2D.Double(0,400,700,400));
        aa.setFirstEntryPoint(new Point2D.Double(350, 400));
        aa.setMaximumDepth(3.5);

        //aa.addDepthZone(dz1);
        //aa.addDepthZone(dz2);
        //aa.addDepthZone(dz3);

        aa.setShowStatistics(false);
        aa.setStatsPlace(AnchorArea.STATS_PLACE.SP_RIGHT);

        aa.setRiskFactor(0);
        aa.setScale(0.1);
      //  AnchorageManager am = new AnchorageManager(aa,new UniformArrivalGenerator(), new AMHD_AS_AnchoragePolicy(aa.getEntryPoint().distance(0,0),0.1));
      //  AnchorageManager am = new AnchorageManager(aa,new UniformArrivalGenerator(), new AMHDAnchoragePolicy());

      //  AnchorageManager am = new AnchorageManager(aa,new UniformArrivalGenerator(), new MHDAnchoragePolicy());

      //  AnchorageManager am = new AnchorageManager(aa,new UniformArrivalGenerator(), new HybridAnchoragePolicyV2(aa.getEntryPoint().distance(0,0)));

      //  AnchorageManager am = new AnchorageManager(aa,new AhkpArrivalGenerator(0.1), new WallPack_MHDAnchoragePolicy());
        AnchorageManager am = new AnchorageManager(aa,new AhkpArrivalGenerator(0.1), new HybridAnchoragePolicyV2(aa.getFirstEntryPoint().distance(0,0)));
      //  AnchorageManager am = new AnchorageManager(aa,new AhkpArrivalGenerator(0.1), new MHDAnchoragePolicy());

        am.showAnchorageanimation();






    }

    private static void test3() throws InterruptedException {


        AnchorArea aa = AnchorAreaFactory.createTypeC();


        aa.setShowStatistics(false);
        aa.setStatsPlace(AnchorArea.STATS_PLACE.SP_RIGHT);
        aa.setRiskFactor(0);


        //AnchorageManager am = new AnchorageManager(aa,new AhkpArrivalGenerator(aa.getScale()), new HybridAnchoragePolicyV2(aa.getFirstEntryPoint().distance(0,0)));

        AnchorageManager am = new AnchorageManager(aa,new AhkpGammaArrivalGenerator(aa.getScale()), new HybridAnchoragePolicyV2(aa.getFirstEntryPoint().distance(0,0)));

        am.showAnchorageanimation();

    }

    private static void testNonUniform() throws InterruptedException {


       // AnchorArea aa = AnchorAreaFactory.createNonUniform1();

        AnchorArea aa = AnchorAreaFactory.createNonUniform_AK3();

        aa.setShowStatistics(false);
        aa.setStatsPlace(AnchorArea.STATS_PLACE.SP_RIGHT);
        aa.setRiskFactor(0);


        //AnchorageManager am = new AnchorageManager(aa,new AhkpArrivalGenerator(aa.getScale()), new HybridAnchoragePolicyV2(aa.getFirstEntryPoint().distance(0,0)));

        AnchorageManager am = new AnchorageManager(aa,new AhkpArrivalGenerator(aa.getScale()), new HybridAnchoragePolicyV2(aa.getFirstEntryPoint().distance(0,0)));

        am.showAnchorageanimation();

    }
}
