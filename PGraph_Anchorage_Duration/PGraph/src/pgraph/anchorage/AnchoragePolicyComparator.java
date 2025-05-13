package pgraph.anchorage;

import pgraph.Path;
import pgraph.anchorage.distributions.ArrivalGenerator;
import pgraph.anchorage.distributions.UniformArrivalGenerator;
import pgraph.anchorage.policy.AnchorPolicy;
import pgraph.anchorage.policy.HybridAnchoragePolicy;
import pgraph.anchorage.policy.MHDAnchoragePolicy;
import pgraph.gui.GraphViewer;
import pgraph.util.Pen;
import pgraph.util.Polygon2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:00
 * To change this template use File | Settings | File Templates.
 */
public class AnchoragePolicyComparator {

    GraphViewer gv= null;
    private static final long REFRESH_INTERVAL = 1000;
    AnchorAreaList anchorAreaList;
    ArrivalGenerator arrivalGenerator;
    java.util.List<AnchorPolicy> anchorPolicyList;



    private boolean finished= false;


    public AnchoragePolicyComparator(AnchorAreaList anchorAreaList, ArrivalGenerator arrivalGenerator, java.util.List<AnchorPolicy> anchorPolicyList) {
        this.anchorAreaList = anchorAreaList;
        this.arrivalGenerator = arrivalGenerator;
        this.anchorPolicyList = anchorPolicyList;
    }




    public void performSilentSimulation() throws InterruptedException {
        System.out.println("Simulation started.");

        finished = false;

        while (!finished)
        {
            ArrivalInterface arrival = arrivalGenerator.generate();
            finished = !_performAnchorageSilent(arrival);
        }

        System.out.println("ALL ANCHORAGE AREAS ARE FULL");
        System.out.println("Simulation ended..");

    }






    public void  showAnchorageanimation() throws InterruptedException {
        gv = GraphViewer.showContent(anchorAreaList);
        //gv.setIgnoreRepaint(true);

        waitandRefresh(0*REFRESH_INTERVAL);

        while (!finished)
        {
            ArrivalInterface arrival = arrivalGenerator.generate();

            _performArrival(arrival);

            waitandRefresh(REFRESH_INTERVAL);

            finished = !_performAnchorage(arrival);

            waitandRefresh(REFRESH_INTERVAL);

        }

        System.out.println("ANCHORAGE AREAS ARE FULL");
        Thread.sleep(REFRESH_INTERVAL);
        gv.repaint();
    }

    private boolean _performAnchorageSilent(ArrivalInterface arrival) throws InterruptedException {

        int size = anchorPolicyList.size();
        boolean anchorageHappened = false;
        for (int i =0 ; i<size ;i++)
        {
            AnchorArea anchorArea = anchorAreaList.getAreaList().get(i);
            AnchorPolicy anchorPolicy = anchorPolicyList.get(i);

            if (anchorArea.isAreaFull())
                continue;


            Anchorage anchorage = anchorPolicy.createAnchorage(anchorArea, arrival);

            if (anchorage == null)
            {
                anchorArea.setAreaFull(true);
                continue;
            }
            anchorageHappened = true;
            anchorArea.addExistingAnchorage(anchorage);


        }

        return anchorageHappened;
    }





    private boolean _performAnchorage(ArrivalInterface arrival) throws InterruptedException {

        int size = anchorPolicyList.size();
        boolean anchorageHappened = false;
        for (int i =0 ; i<size ;i++)
        {
            AnchorArea anchorArea = anchorAreaList.getAreaList().get(i);
            AnchorPolicy anchorPolicy = anchorPolicyList.get(i);

            if (anchorArea.isAreaFull())
                continue;

            Anchorage anchorage = anchorPolicy.createAnchorage(anchorArea, arrival);

            if (anchorage == null)
            {
                anchorArea.setAreaFull(true);
                continue;
            }

            anchorageHappened = true;
            anchorage.setPen(new Pen(Color.red, Pen.PenStyle.PS_Pointed));
            anchorArea.setCurrentAnchoragePath(new Path(anchorPolicy.getLastAnchoragePath(),new Pen(Color.blue, Pen.PenStyle.PS_Pointed)));
            anchorArea.addExistingAnchorage(anchorage);

            anchorArea.repositionArrivals(anchorage.getArea().center().getAsDouble());
        }

        if (!anchorageHappened)
            return false;
        waitandRefresh(REFRESH_INTERVAL);
        waitandRefresh(REFRESH_INTERVAL);

        for (AnchorArea a: anchorAreaList.getAreaList())
        {
            if (!a.existingAnchorages.isEmpty())
                a.existingAnchorages.get(a.existingAnchorages.size()-1).setPen(Pen.DefaultPen);
            a.clearArrivals();
            a.setCurrentAnchoragePath(null);
        }

        return anchorageHappened;
    }

    private void _performArrival(ArrivalInterface arrival)
    {
        for (AnchorArea a: anchorAreaList.getAreaList())
        {
            if (a.isAreaFull())
                continue;
            a.newArrival(arrival);
        }
    }

    private void waitandRefresh(long refreshInterval) throws InterruptedException {
        Thread.sleep(refreshInterval);
        if (gv != null)
            gv.repaint();
    }

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException
    {
        test1();
    }

    private static void test1() throws InterruptedException {

        AnchorArea aa1 = new AnchorArea();
        aa1.setArea(new Polygon2D(new double[]{0, 0, 100, 100}, new double[]{0, 100, 100, 0}, 4));
        aa1.setEntrySide(new Line2D.Double(0,100,100,100));
        aa1.setFirstEntryPoint(new Point2D.Double(50,99));

        AnchorArea aa2 = new AnchorArea();
        aa2.setArea(new Polygon2D(new double[]{150, 150, 250, 250}, new double[]{0, 100, 100, 0}, 4));
        aa2.setEntrySide(new Line2D.Double(150,100,250,100));
        aa2.setFirstEntryPoint(new Point2D.Double(200,99));

        AnchorArea aa3 = new AnchorArea();
        aa3.setArea(new Polygon2D(new double[]{300, 300, 400, 400}, new double[]{0, 100, 100, 0}, 4));
        aa3.setEntrySide(new Line2D.Double(300,100,400,100));
        aa3.setFirstEntryPoint(new Point2D.Double(350,99));



        java.util.List<AnchorArea> aaList = new ArrayList<AnchorArea>();
        aaList.add(aa1); aaList.add(aa2); aaList.add(aa3);
        AnchorAreaList aal = new AnchorAreaList(aaList);

        aa1.setScale(0.01);  aa2.setScale(0.01); aa3.setScale(0.01);
        aa1.setMaximumDepth(0.35);  aa2.setMaximumDepth(0.35); aa3.setMaximumDepth(0.35);

        java.util.List<AnchorPolicy> apList = new ArrayList<AnchorPolicy>();
        apList.add(new MHDAnchoragePolicy());
        apList.add(new HybridAnchoragePolicy(5,20,1 ,0,aa2.getFirstEntryPoint().distance(150,0) ));
        apList.add(new HybridAnchoragePolicy(1,0.2,100 ,0,aa3.getFirstEntryPoint().distance(300,0) ));
        //apList.add(new HybridAnchoragePolicy(1,0,0 ,0,aa2.getEntryPoint().distance(150,0) ));

       // apList.add(new AMHD_AS_AnchoragePolicy(aa2.getEntryPoint().distance(0,0),0.1));


      //  AnchorageManager am = new AnchorageManager(aa,new UniformArrivalGenerator(), new AMHD_AS_AnchoragePolicy(aa.getEntryPoint().distance(0,0),0.1));
      //  AnchorageManager am = new AnchorageManager(aa,new UniformArrivalGenerator(), new AMHDAnchoragePolicy());

      //  AnchorageManager am = new AnchorageManager(aa,new UniformArrivalGenerator(), new MHDAnchoragePolicy());



        AnchoragePolicyComparator apc = new AnchoragePolicyComparator(aal,new UniformArrivalGenerator(100,500,aa1.getScale()), apList);

        apc.showAnchorageanimation();

    }

    private static void test2() throws InterruptedException {
        AnchorArea aa1 = new AnchorArea();
        aa1.setArea(new Polygon2D(new double[]{0, 0, 100, 100}, new double[]{0, 100, 100, 0}, 4));
        aa1.setEntrySide(new Line2D.Double(0,100,100,100));
        aa1.setFirstEntryPoint(new Point2D.Double(50,99));
        aa1.setOutputFile("anchorstats_MHD.txt");


        AnchorArea aa2 = new AnchorArea();
        aa2.setArea(new Polygon2D(new double[]{150, 150, 250, 250}, new double[]{0, 100, 100, 0}, 4));
        aa2.setEntrySide(new Line2D.Double(150,100,250,100));
        aa2.setFirstEntryPoint(new Point2D.Double(200,99));
        aa2.setOutputFile("anchorstats_HYB1.txt");

        AnchorArea aa3 = new AnchorArea();
        aa3.setArea(new Polygon2D(new double[]{300, 300, 400, 400}, new double[]{0, 100, 100, 0}, 4));
        aa3.setEntrySide(new Line2D.Double(300,100,400,100));
        aa3.setFirstEntryPoint(new Point2D.Double(350,99));
        aa3.setOutputFile("anchorstats_HYB2.txt");


        java.util.List<AnchorArea> aaList = new ArrayList<AnchorArea>();
        aaList.add(aa1); aaList.add(aa2); aaList.add(aa3);
        AnchorAreaList aal = new AnchorAreaList(aaList);

        java.util.List<AnchorPolicy> apList = new ArrayList<AnchorPolicy>();
        apList.add(new MHDAnchoragePolicy());
        apList.add(new HybridAnchoragePolicy(5,20,1 ,0,aa2.getFirstEntryPoint().distance(150,0) ));
        apList.add(new HybridAnchoragePolicy(1,0.2,100 ,0,aa3.getFirstEntryPoint().distance(300,0) ));
        //apList.add(new HybridAnchoragePolicy(1,0,0 ,0,aa2.getEntryPoint().distance(150,0) ));

        // apList.add(new AMHD_AS_AnchoragePolicy(aa2.getEntryPoint().distance(0,0),0.1));


        //  AnchorageManager am = new AnchorageManager(aa,new UniformArrivalGenerator(), new AMHD_AS_AnchoragePolicy(aa.getEntryPoint().distance(0,0),0.1));
        //  AnchorageManager am = new AnchorageManager(aa,new UniformArrivalGenerator(), new AMHDAnchoragePolicy());

        //  AnchorageManager am = new AnchorageManager(aa,new UniformArrivalGenerator(), new MHDAnchoragePolicy());



        AnchoragePolicyComparator apc = new AnchoragePolicyComparator(aal,new UniformArrivalGenerator(aa1.getScale()), apList);

        apc.performSilentSimulation();

    }


}
