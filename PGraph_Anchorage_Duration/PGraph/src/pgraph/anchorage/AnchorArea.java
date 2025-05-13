package pgraph.anchorage;

import math.geom2d.conic.Circle2D;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraWithHeuristicShortestPath;
import org.jgrapht.traverse.MHDHeuristic;
import pgraph.CircleListObstacleGenerator;
import pgraph.ObstacleGenerator;
import pgraph.ObstacleInterface;
import pgraph.Path;
import pgraph.util.GeomUtil;
import pgraph.anchorage.util.ShipDynamics;
import pgraph.util.TimerUtil;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridVertex;
import pgraph.gui.BaseGraphPanel;
import pgraph.util.Pen;
import pgraph.util.Polygon2D;
import pgraph.util.StringUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.geom.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 30.10.2013
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class AnchorArea implements BaseGraphPanel.Renderable
{
    String statusText = null;

    private static final double DEFAULT_MAX_DEPTH = 35; // maximum depth for Ahirkapi
    private static final int MAX_ADJUSTMENT_COUNT = 1000;

    private int maxIntersection =-1;
    private double maximumDepth= DEFAULT_MAX_DEPTH;

    private double riskFactor=0;
    private boolean uniformAssumption=false;

    private static final int MAX_REJECT_COUNT= 1000;
    private int rejectCount=0;
    private int consecutive_rejectCount=0;

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    private double scale= 1;

    public Point2D getFirstEntryPoint() {
        return firstEntryPoint;
    }

    public void setFirstEntryPoint(Point2D firstEntryPoint) {
        this.firstEntryPoint = firstEntryPoint;
    }

    private Point2D firstEntryPoint;

    public double getRiskFactor() {
        return riskFactor;
    }

    public void setRiskFactor(double riskFactor) {
        this.riskFactor = riskFactor;
    }


    public double getMaximumDepth() {
        return maximumDepth;
    }

    public void setMaximumDepth(double maximumDepth) {
        this.maximumDepth = maximumDepth;
    }

    public Anchorage getNextDeparture() {

        long departureTime = Long.MAX_VALUE;
        Anchorage departure = null;

        for (Anchorage a: existingAnchorages)
        {
            if (departureTime>a.getDepartureTime())
            {
                departure = a;
                departureTime = a.departureTime;
            }
        }
        return departure;
    }

    public void removeDepartingAnchorage(Anchorage departure) {
        existingAnchorages.remove(departure);
    }


    public enum STATS_PLACE { SP_RIGHT, SP_BOTTOM };

    public static final double COLLISION_TRESHOLD = 0.001;
    private static final double DZ_INCLUSION_THRESHOLD = 1;

    private static final int MODEL_GRAPH_DEGREE = 1;
    private int MODEL_GRAPH_UNIT_EDGE_LEN=20;
    private double sAvgMindistToEntry=0;
    private double sAvgIntersection=0;
    private double sAvgDepartureIntersection=0;
    private double sAvgAnchorPathLength = 0;
    private int experimentCount=0;
    private double sAreaUtilization=0;
    private double sAvgAnchorCount = 0;
    private double sAvgRejectCount = 0;
    private double sAvgEffAreaUtilization=0;



    STATS_PLACE statsPlace = STATS_PLACE.SP_BOTTOM;

    Polygon2D area;
    List<Anchorage> existingAnchorages= new ArrayList<Anchorage>();
    List<Anchorage> candidateAnchorages = new ArrayList<Anchorage>();

    List<Anchorage> arrivals = new ArrayList<Anchorage>();


    List<DepthZone> depthZones= new ArrayList<DepthZone>();

    Line2D entrySide;

    private double minDistanceToEntry = -1;
    private double areaUtilization=0;
    private double effAreaUtilization=0;
    private double totalArea = 0;

    private GridDirectedGraph modelGraph = null;
    private double totalAnchoragePathLength = 0;
    private int totalAnchorageIntersection=0;
    private double totalDepartureIntersection=0;
    private double totalMinDistanceToEntry= 0;
    private double totalEffAreaUtilization=0;

    private String outputFile=null;
    private String outputSummaryFile=null;

    private int departureIntersectionMeasurePeriod = 1;
    private int departureIntersectionMeasureCount=0;


    public int getMaxIntersection() {
        return maxIntersection;
    }

    public void setMaxIntersection(int maxIntersection) {
        this.maxIntersection = maxIntersection;
    }


    public STATS_PLACE getStatsPlace() {
        return statsPlace;
    }

    public void setStatsPlace(STATS_PLACE statsPlace) {
        this.statsPlace = statsPlace;
    }

    public boolean isShowStatistics() {
        return showStatistics;
    }

    public void setShowStatistics(boolean showStatistics) {
        this.showStatistics = showStatistics;
    }

    private boolean showStatistics=false;


    public void addDepthZone(DepthZone dz)
    {
        depthZones.add(dz);
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputSummaryFile() {
        return outputSummaryFile;
    }

    public void setOutputSummaryFile(String outputSummaryFile) {
        this.outputSummaryFile = outputSummaryFile;
    }

    public boolean isAreaFull() {
        return areaFull;
    }

    public void setAreaFull(boolean areaFull) {
        this.areaFull = areaFull;
    }

    private boolean areaFull = false;

    private void _createModelGraph( Rectangle2D boundingRect ) {

        double minX = boundingRect.getMinX();
        double maxX = boundingRect.getMaxX();
        double minY = boundingRect.getMinY();
        double maxY = boundingRect.getMaxY();

        double unitEdgeLength = MODEL_GRAPH_UNIT_EDGE_LEN*scale;

        modelGraph=  new GridDirectedGraph(MODEL_GRAPH_DEGREE,(int)(maxX-minX)/(int)unitEdgeLength+1,(int)(maxY-minY)/MODEL_GRAPH_UNIT_EDGE_LEN+1,MODEL_GRAPH_UNIT_EDGE_LEN,new Point2D.Double(minX,minY),0,0,0,0 );

    }


    public boolean isCollectStatistics() {
        return collectStatistics;
    }

    public void setCollectStatistics(boolean collectStatistics) {
        this.collectStatistics = collectStatistics;
    }

    private boolean collectStatistics = true;


    public Path getCurrentAnchoragePath() {
        return currentAnchoragePath;
    }

    public void setCurrentAnchoragePath(Path currentAnchoragePath) {
        this.currentAnchoragePath = currentAnchoragePath;
    }

    Path currentAnchoragePath = null;

    public void newArrival(ArrivalInterface arrival)
    {
        double innerRadius = arrival.getLength();
        double outerRadius = ShipDynamics.getOuterRadius(arrival.getLength(), maximumDepth, scale);
        double radius = ShipDynamics.getOuterRadius(arrival.getLength(),maximumDepth,scale,riskFactor);
        arrivals.add(new Anchorage(new Circle2D(firstEntryPoint.getX(),firstEntryPoint.getY(),radius),outerRadius,innerRadius, Pen.DefaultPen));
    }
    public void repositionArrivals(Point2D entryPoint)
    {
        for (Anchorage a: arrivals)
        {
            a.setArea( new Circle2D(entryPoint.getX(),entryPoint.getY(),a.getArea().radius()));
        }
    }

    public void clearArrivals()
    {
        arrivals.clear();
    }



    public Line2D getEntrySide() {
        return entrySide;
    }

    public void setEntrySide(Line2D entrySide) {
        this.entrySide = entrySide;
    }



    public List<Anchorage> getCandidateAnchorages() {
        return candidateAnchorages;
    }

    public void setCandidateAnchorages(List<Anchorage> candidateAnchorages) {
        this.candidateAnchorages = candidateAnchorages;
    }




    public Polygon2D getArea() {
        return area;
    }

    public void setArea(Polygon2D area) {
        this.area = area;
        if (collectStatistics)
        {
            _createModelGraph(area.getBounds2D());
            totalArea = area.calculateArea();
        }
    }

    public List<Anchorage> getExistingAnchorages() {
        return existingAnchorages;
    }

    public List<Circle2D> getExistingAnchoragesAsCircles()
    {
        List<Circle2D> cl = new ArrayList<Circle2D>();
        for (Anchorage a: existingAnchorages)
        {
            cl.add(a.getArea());
        }
        return cl;
    }

    public void setExistingAnchorages(List<Anchorage> existingAnchorages) {
        this.existingAnchorages = existingAnchorages;
    }

    public void setExistingAnchoragesFromCircles(List<Circle2D> existingAnchorages) {
        this.existingAnchorages.clear();

        for (Circle2D c: existingAnchorages)
        {
            this.existingAnchorages.add(new Anchorage(c));
        }
    }

    @Override
    public void draw(Graphics2D g, BaseGraphPanel.ViewTransform transform)
    {
        Color orjColor = g.getColor();
        Stroke orjStroke = g.getStroke();
        AffineTransform orjTransform =  g.getTransform();

        if (area != null)
        {
            if (areaFull)
                g.setColor(Color.red);
            g.draw(transform.createTransformedShape(area));
            g.setColor(orjColor);
        }

        List<Anchorage> aList = existingAnchorages;

        for (Anchorage a: aList)
        {
            a.draw(g,transform);
        }

        aList = candidateAnchorages;
        for (Anchorage a: aList)
        {
            a.draw(g, transform);
        }

        aList= arrivals;
        for (Anchorage a: aList)
        {
            a.draw(g, transform);
        }

        if (currentAnchoragePath != null)
            currentAnchoragePath.draw(g,transform);


        for (DepthZone dz:depthZones)
            dz.draw(g,transform);


        g.setColor(Color.white);

        g.draw(transform.createTransformedShape(entrySide));


        drawStatistics(g, transform);


        g.setStroke(orjStroke);
        g.setColor(orjColor);
        g.setTransform(orjTransform);



    }

    public void printSummaryStatistics(String configHeader) throws IOException {

        if (!collectStatistics || outputSummaryFile == null)
            return;

        FileWriter fw = new FileWriter(outputSummaryFile,true);
        BufferedWriter bo = new BufferedWriter(fw);

        NumberFormat nf = NumberFormat.getInstance();

        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(3);




        bo.write(   " [  "+ StringUtil.padLeft(configHeader,15) + " ]  >> " +
                "Risk Factor: " +  StringUtil.padLeft(nf.format(riskFactor),4) + "   "+
                "Avg Anchorage Count: "       + StringUtil.padLeft(nf.format(sAvgAnchorCount),5) + "   "+
                "Avg Reject Count: "       + StringUtil.padLeft(nf.format(sAvgRejectCount),5) + "   "+
                "Avg Area Utilization: "       + StringUtil.padLeft(nf.format(sAreaUtilization),5) + "   "+
                "Avg Eff. Area Utilization: "       + StringUtil.padLeft(nf.format(sAvgEffAreaUtilization),5) + "   "+
                "Avg Min Dist To Entry: "  + StringUtil.padLeft(nf.format(sAvgMindistToEntry),8) + "  "+
                "Avg_AnchPathLen: " + StringUtil.padLeft(nf.format(sAvgAnchorPathLength),8) + "    "+
                "Avg_DepartureIntersect: " + StringUtil.padLeft(nf.format(sAvgDepartureIntersection),6) + "    "+
                "Avg_Intersect: "   + StringUtil.padLeft(nf.format(sAvgIntersection),6) + "\n");


        bo.close();
        fw.close();
    }

    private void printStatistics() throws IOException {

        if (!collectStatistics || outputFile == null)
            return;

        double avgAnchorPathLength  =existingAnchorages.isEmpty() ?  0 :  totalAnchoragePathLength/existingAnchorages.size() ;
        double avgIntersection      =existingAnchorages.isEmpty() ?  0 :  (double)totalAnchorageIntersection/(double)existingAnchorages.size() ;
        double avgEffAreaUtilization      =existingAnchorages.isEmpty() ?  0 :  (double)totalEffAreaUtilization/(double)existingAnchorages.size() ;

        FileWriter fw = new FileWriter(outputFile,true);
        BufferedWriter bo = new BufferedWriter(fw);

        NumberFormat nf = NumberFormat.getInstance();

        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(3);



        bo.write(   "Area_Util: "       + StringUtil.padLeft(nf.format(areaUtilization),5) + "   "+
                "Eff_Area_Util: "   + StringUtil.padLeft(nf.format(effAreaUtilization),5) + "   "+
                "Avg_Eff_Area_Util: "   + StringUtil.padLeft(nf.format(avgEffAreaUtilization),5) + "   "+
                "Min_Dist2Entry: "  + StringUtil.padLeft(nf.format(minDistanceToEntry),8) + "  "+
                "Avg_AnchPathLen: " + StringUtil.padLeft(nf.format(avgAnchorPathLength),8) + "    "+
                "Avg_Intersect: "   + StringUtil.padLeft(nf.format(avgIntersection),6) + "\n");


        bo.close();
        fw.close();
    }


    private void drawStatistics(Graphics2D g, BaseGraphPanel.ViewTransform transform) {

        float bX,bY;

        Rectangle2D bounds = area.getBounds2D();
        if (statsPlace==STATS_PLACE.SP_BOTTOM)
        {
            bX = (float) area.getBounds2D().getMinX();
            bY = (float) area.getBounds2D().getMinY();
        }
        else
        {
            bX = (float) area.getBounds2D().getMaxX()+20;
            bY = (float) ( 0- area.getBounds2D().getMaxY());
        }

        AffineTransform t = null;
        try {
            t = transform.createInverse().createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        g.setColor(Color.black);
        g.setFont(new Font("Arial",0,8));

        if (t != null)
        {
            t.scale(1,-1);
            g.setTransform(t);
        }
        else
        {
            boolean aha = true;
        }

        if (statusText != null && !statusText.isEmpty())
        {
            g.drawString(statusText, bX, bY + 30);
            bY = bY + 10;
        }
        if (!collectStatistics || !showStatistics)
            return;



        double avgAnchorPathLength      =existingAnchorages.isEmpty() ?  0 :  totalAnchoragePathLength/existingAnchorages.size() ;
        double avgIntersection          =existingAnchorages.isEmpty() ?  0 :  (double)totalAnchorageIntersection/(double)existingAnchorages.size() ;
        double avgMinDistanceToEntry    =existingAnchorages.isEmpty() ?  0 :  (double)totalMinDistanceToEntry/(double)existingAnchorages.size() ;
        double avgEffAreaUtilization    =existingAnchorages.isEmpty() ?  0 :  (double)totalEffAreaUtilization/(double)existingAnchorages.size() ;

        NumberFormat nf = NumberFormat.getInstance();

        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(3);


        g.drawString("Anchorage Count     : " + existingAnchorages.size(), bX,bY+30);
        g.drawString("Area Utilization    : " + nf.format(areaUtilization),bX,bY+40);
        g.drawString("Ef_Area Utilization : " + nf.format(effAreaUtilization), bX,bY+50);
        g.drawString("Avg_Ef_Area Utilization : " + nf.format(avgEffAreaUtilization), bX,bY+60);
        g.drawString("Min Dist. To Entry  : " + nf.format(minDistanceToEntry), bX,bY+70);
        g.drawString("Avg. MinDistToEntry : " + nf.format(avgMinDistanceToEntry), bX,bY+80);
        g.drawString("Avg AnchorPath Len  : " + nf.format(avgAnchorPathLength), bX,bY+90);
        g.drawString("Total Intersection  : " + totalAnchorageIntersection,bX,bY+100);
        g.drawString("Avg Intersection    : " + nf.format(avgIntersection),bX,bY+110);
    }

    @Override
    public Rectangle2D boundingRect() {
        return area.getBounds2D();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }


    public static final byte  AT_SIDE_SIDE     = 1;
    public static final byte  AT_SIDE_CIRCLE   = 2;
    public static final byte  AT_CIRCLE_CIRCLE = 4;
    public static final byte  AT_SIDE_DZSIDE   = 8;
    public static final byte  AT_CIRCLE_DZSIDE = 16;
    public static final byte  AT_DZSIDE_DZSIDE = 32;


    public List<Anchorage> calculateCandidateAnchorages(double length, int anchorageType)
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();
        uniformAssumption = false;

        double innerRadius = length;
        double outerRadius = ShipDynamics.getOuterRadius(length,maximumDepth,scale);
        double radius = ShipDynamics.getOuterRadius(length,maximumDepth,scale,riskFactor);

        if ( ( anchorageType & AT_SIDE_SIDE ) != 0)
            caList.addAll(getSideAndSideAnchorages(radius, length));

        if ( ( anchorageType & AT_SIDE_CIRCLE ) != 0)
            caList.addAll(getSideAndCircleAnchorages(radius, length));

        if ( ( anchorageType & AT_CIRCLE_CIRCLE ) != 0)
            caList.addAll(getCircleAndCircleAnchorages(radius, length));

        if ( ( anchorageType & AT_SIDE_DZSIDE ) != 0)
            caList.addAll(getSideAndDZSideAnchorages(radius, length));

        if ( ( anchorageType & AT_CIRCLE_DZSIDE ) != 0)
            caList.addAll(getCircleAndDZSideAnchorages(radius, length));

        if ( ( anchorageType & AT_DZSIDE_DZSIDE ) != 0)
            caList.addAll(getDZSideAndDZSideAnchorages(radius, length));


        return caList;
    }

    public List<Anchorage> calculateCandidateAnchorages_UniformAssumption(double length, int anchorageType,double maxDepth)    // Before  Non-uniformity updates
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();
        List<Point2D> cpList = new ArrayList<Point2D>();
        //uniformAssumption = true;

        double innerRadius = length;
        double outerRadius = ShipDynamics.getOuterRadius(length,maxDepth,scale);
        double radius = ShipDynamics.getOuterRadius(length,maxDepth,scale,riskFactor);

        if ( ( anchorageType & AT_SIDE_SIDE ) != 0)
            cpList.addAll(getSideAndSideCornerPoints(radius,length));

        if ( ( anchorageType & AT_SIDE_CIRCLE ) != 0)
            cpList.addAll(getSideAndCircleCornerPoints(radius,length));

        if ( ( anchorageType & AT_CIRCLE_CIRCLE ) != 0)
            cpList.addAll(getCircleAndCircleCornerPoints(radius,length));

        if ( ( anchorageType & AT_SIDE_DZSIDE ) != 0)
            cpList.addAll(getSideAndDZSideCornerPoints(radius,length));

        if ( ( anchorageType & AT_CIRCLE_DZSIDE ) != 0)
            cpList.addAll(getCircleAndDZSideCornerPoints(radius,length));

        if ( ( anchorageType & AT_DZSIDE_DZSIDE ) != 0)
            cpList.addAll(getDZSideAndDZSideCornerPoints(radius,length));

        for (Point2D p : cpList)
        {
            caList.add(new Anchorage(new Circle2D(p.getX(),p.getY(),radius),outerRadius,innerRadius));
        }

        return caList;
    }

    public List<Anchorage> getAllCandidateAnchorages_UniformAssumption(double length,double maxdepth)
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();

        if (ShipDynamics.getSafeLength(maxdepth) >= length)
        {
            if (existingAnchorages.size() <= 1)
            {
                caList.addAll(calculateCandidateAnchorages_UniformAssumption(length, AT_SIDE_SIDE,maxdepth));
                if( caList.isEmpty())
                    caList.addAll(calculateCandidateAnchorages_UniformAssumption(length, AT_SIDE_CIRCLE,maxdepth));
                if ( caList.isEmpty())
                    caList.addAll(calculateCandidateAnchorages_UniformAssumption(length,  AT_CIRCLE_CIRCLE | AT_SIDE_DZSIDE,maxdepth));

            }  else
            {
                caList.addAll(calculateCandidateAnchorages_UniformAssumption(length, AT_SIDE_SIDE | AT_SIDE_CIRCLE | AT_CIRCLE_CIRCLE | AT_SIDE_DZSIDE,maxdepth));
            }
        }

        if (caList.isEmpty())
        {
            rejectCount++;
            consecutive_rejectCount++;
            if (consecutive_rejectCount<MAX_REJECT_COUNT)
                caList.add(new Anchorage(null));
            return caList;
        }
        consecutive_rejectCount=0;
        return caList;
    }

    public List<Anchorage> getAllCandidateAnchorages(double length)
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();

        if (existingAnchorages.size() <= 1)
        {
            caList.addAll(calculateCandidateAnchorages(length, AT_SIDE_SIDE));
            if( caList.isEmpty())
                caList.addAll(calculateCandidateAnchorages(length, AT_SIDE_CIRCLE));
            if ( caList.isEmpty())
                caList.addAll(calculateCandidateAnchorages(length,  AT_CIRCLE_CIRCLE | AT_SIDE_DZSIDE));

        }  else
        {
            caList.addAll(calculateCandidateAnchorages(length, AT_SIDE_SIDE | AT_SIDE_CIRCLE | AT_CIRCLE_CIRCLE | AT_SIDE_DZSIDE));
        }


        if (caList.isEmpty())
        {
            rejectCount++;
            consecutive_rejectCount++;
            if (consecutive_rejectCount<MAX_REJECT_COUNT)
                caList.add(new Anchorage(null));
            return caList;
        }
        consecutive_rejectCount=0;
        return caList;
    }

    public List<Anchorage> getOtherCandidateAnchorages_UniformAssumption(double length,int priority,double maxDepth)
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();


        switch (priority)
        {
            case 1: caList.addAll(calculateCandidateAnchorages_UniformAssumption(length, AT_CIRCLE_DZSIDE,maxDepth));break;
            case 2: caList.addAll(calculateCandidateAnchorages_UniformAssumption(length, AT_DZSIDE_DZSIDE,maxDepth));break;
        }

        return caList;
    }

    public List<Anchorage> getOtherCandidateAnchorages(double length,int priority)
    {
        List<Anchorage> caList = new ArrayList<Anchorage>();


        switch (priority)
        {
            case 1: caList.addAll(calculateCandidateAnchorages(length, AT_CIRCLE_DZSIDE));break;
            case 2: caList.addAll(calculateCandidateAnchorages(length, AT_DZSIDE_DZSIDE));break;
        }

        return caList;
    }

    private Collection<? extends Point2D> getCircleAndCircleCornerPoints( double radius , double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        for (int i = 0; i< existingAnchorages.size()-1;i++ )
        {
            Circle2D ci = existingAnchorages.get(i).getArea();
            for (int j = i+1; j < existingAnchorages.size();j++)
            {
                Circle2D cj = existingAnchorages.get(j).getArea();
                cpList.addAll(getCircleAndCircleCornerPoints(ci,cj,radius,length));
            }
        }

        return cpList;
    }

    private Collection<? extends Anchorage> getCircleAndCircleAnchorages( double radius , double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        for (int i = 0; i< existingAnchorages.size()-1;i++ )
        {
            Circle2D ci = existingAnchorages.get(i).getArea();
            for (int j = i+1; j < existingAnchorages.size();j++)
            {
                Circle2D cj = existingAnchorages.get(j).getArea();
                aList.addAll(getCircleAndCircleAnchorages(ci, cj, radius, length));
            }
        }

        return aList;
    }

    private Collection<? extends Anchorage> getCircleAndCircleAnchorages(Circle2D c1, Circle2D c2, double radius,double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(c1,c2,radius) ;
        List<Double> calculatedDepths = new ArrayList<>();

        for (Circle2D ci:cList)
        {
            boolean adjusted = false;

            double maxDepth = 0;
            double depth = maximumDepth;
            int adjustmentCount =0;
            double previousDepth = maximumDepth;
            calculatedDepths.clear();

            while (!adjusted &&  (prevalidityCheck(ci, length)))
            {
                DepthZone deepest =  deepestZone(ci);
                depth = (deepest == null) ? maximumDepth: deepest.getDepth();

                if (depth == previousDepth)    // Converged!!!
                    adjusted = true;
                else if (calculatedDepths.contains(depth)) // Previously calculated depth
                {
                    depth = maxDepth;
                    adjusted = true;
                }
                else                            // Adjustment required
                {
                    double newRadius = ShipDynamics.getOuterRadius(length,depth,scale,riskFactor);
                    ci = GeomUtil.getTangentCircleAdjusted(c1,c2,newRadius,ci);
                    calculatedDepths.add(depth);
                    previousDepth = depth;
                    if (depth>maxDepth)
                        maxDepth = depth;
                    adjustmentCount++;
                }
                if (adjustmentCount>MAX_ADJUSTMENT_COUNT)
                {
                    throw new RuntimeException("Too many adjustment..");
                }
            }
            if (adjusted&& validAnchorage(ci,length))
            {
                double innerRadius = length;
                double outerRadius = ShipDynamics.getOuterRadius(length,depth,scale);
                Anchorage a = new Anchorage(ci,outerRadius,innerRadius,c1,c2);
                aList.add(a);
            }
        }

        return aList;
    }

    private Collection<? extends Point2D> getCircleAndCircleCornerPoints(Circle2D c1, Circle2D c2, double radius, double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(new Circle2D(c1.center(), c1.radius()), new Circle2D(c2.center(), c2.radius()), radius) ;

        for (Circle2D ci:cList)
        {
            if (validAnchorage(ci, length))
                cpList.add(ci.center().getAsDouble());
        }

        return cpList;
    }

    private Collection<? extends Point2D> getSideAndCircleCornerPoints( double radius,double length) {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<? extends Line2D> sides = getAreaSides();

        for (Line2D l : sides )
        {
            for (Anchorage a: existingAnchorages)
            {
                cpList.addAll(getSideAndCircleCornerPoints(l,a.getArea(),radius,length));
            }
        }


        return cpList;

    }

    private Collection<? extends Anchorage> getSideAndCircleAnchorages( double radius,double length) {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<? extends Line2D> sides = getAreaSides();

        for (Line2D l : sides )
        {
            for (Anchorage a: existingAnchorages)
            {
                aList.addAll(getSideAndCircleAnchorages(l, a.getArea(), radius, length));
            }
        }


        return aList;

    }

    private Collection<? extends Anchorage> getSideAndCircleAnchorages(Line2D l, Circle2D c, double radius,double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(c,l,radius) ;
        List<Double> calculatedDepths = new ArrayList<>();

        for (Circle2D ci:cList)
        {
            boolean adjusted = false;

            double maxDepth = 0;
            double depth = maximumDepth;
            int adjustmentCount =0;
            double previousDepth = maximumDepth;
            calculatedDepths.clear();

            while (!adjusted &&  (prevalidityCheck(ci, length)))
            {
                DepthZone deepest =  deepestZone(ci);
                depth = (deepest == null) ? maximumDepth: deepest.getDepth();

                if (depth == previousDepth)    // Converged!!!
                    adjusted = true;
                else if (calculatedDepths.contains(depth)) // Previously calculated depth
                {
                    depth = maxDepth;
                    adjusted = true;
                }
                else                            // Adjustment required
                {
                    double newRadius = ShipDynamics.getOuterRadius(length,depth,scale,riskFactor);
                    ci = GeomUtil.getTangentCircleAdjusted(c,l,newRadius,ci);
                    calculatedDepths.add(depth);
                    previousDepth = depth;
                    if (depth>maxDepth)
                        maxDepth = depth;
                    adjustmentCount++;
                }
                if (adjustmentCount>MAX_ADJUSTMENT_COUNT)
                {
                    throw new RuntimeException("Too many adjustment..");
                }
            }
            if (adjusted && validAnchorage(ci,length))
            {
                double innerRadius = length;
                double outerRadius = ShipDynamics.getOuterRadius(length,depth,scale);
                Anchorage a = new Anchorage(ci,outerRadius,innerRadius,c,l);
                aList.add(a);
            }
        }

        return aList;
    }

    private Collection<? extends Point2D> getSideAndCircleCornerPoints(Line2D l, Circle2D ce, double radius,double length) {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(ce, l, radius) ;

        for (Circle2D ci:cList)
        {
            if (validAnchorage(ci,length))
                cpList.add(ci.center().getAsDouble());
        }

        return cpList;
    }

    private Collection<? extends Point2D> getSideAndSideCornerPoints(double radius, double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<? extends Line2D> sides = getAreaSides();
        int size = sides.size();
        for (int i=0; i<size-1;i++ )
        {
            Line2D l1 = sides.get(i);

            for (int j=i+1; j<size;j++)
            {
                Line2D l2 = sides.get(j);
                cpList.addAll(getSideAndSideCornerPoints(l1, l2, radius,length));
            }
        }


        return cpList;

    }

    private Collection<? extends Anchorage> getSideAndSideAnchorages(double radius, double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<? extends Line2D> sides = getAreaSides();
        int size = sides.size();
        for (int i=0; i<size-1;i++ )
        {
            Line2D l1 = sides.get(i);

            for (int j=i+1; j<size;j++)
            {
                Line2D l2 = sides.get(j);
                aList.addAll(getSideAndSideAnchorages(l1, l2, radius, length));
            }
        }


        return aList;

    }

    private Collection<? extends Point2D> getSideAndDZSideCornerPoints(double radius,double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<? extends Line2D> sides = getAreaSides();

        List<Line2D> dzSides = getDZSides();
        for (Line2D side:sides)
        {
            for (Line2D dzSide:dzSides)
            {
                cpList.addAll(getSideAndSideCornerPoints(side, dzSide, radius,length));
            }
        }

        return cpList;

    }

    private Collection<? extends Anchorage> getSideAndDZSideAnchorages(double radius,double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<? extends Line2D> sides = getAreaSides();

        List<Line2D> dzSides = getDZSides();
        for (Line2D side:sides)
        {
            for (Line2D dzSide:dzSides)
            {
                aList.addAll(getSideAndSideAnchorages(side, dzSide, radius, length));
            }
        }

        return aList;

    }

    private Collection<? extends Anchorage> getCircleAndDZSideAnchorages(double radius,double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<Line2D> dzSides = getDZSides();

        for (Line2D l : dzSides )
        {
            for (Anchorage a: existingAnchorages)
            {
                aList.addAll(getSideAndCircleAnchorages(l, a.getArea(), radius, length));
            }
        }

        return aList;
    }

    private Collection<? extends Point2D> getCircleAndDZSideCornerPoints(double radius,double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Line2D> dzSides = getDZSides();

        for (Line2D l : dzSides )
        {
            for (Anchorage a: existingAnchorages)
            {
                cpList.addAll(getSideAndCircleCornerPoints(l,a.getArea(),radius,length));
            }
        }

        return cpList;
    }

    private Collection<? extends Anchorage> getDZSideAndDZSideAnchorages(double radius, double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<? extends Line2D> dzSides = getDZSides();
        int size = dzSides.size();
        for (int i=0; i<size-1;i++ )
        {
            Line2D l1 = dzSides.get(i);

            for (int j=i+1; j<size;j++)
            {
                Line2D l2 = dzSides.get(j);
                aList.addAll(getSideAndSideAnchorages(l1, l2, radius, length));
            }
        }

        return aList;
    }

    private Collection<? extends Point2D> getDZSideAndDZSideCornerPoints(double radius, double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<? extends Line2D> dzSides = getDZSides();
        int size = dzSides.size();
        for (int i=0; i<size-1;i++ )
        {
            Line2D l1 = dzSides.get(i);

            for (int j=i+1; j<size;j++)
            {
                Line2D l2 = dzSides.get(j);
                cpList.addAll(getSideAndSideCornerPoints(l1, l2, radius,length));
            }
        }

        return cpList;
    }

    public List<? extends Line2D> getAreaSides()
    {
        List<? extends Line2D> sides = area.getSides();

        if (entrySide == null)
            return sides;

        Line2D openLine = null;
        for ( Line2D l:sides)
        {
            if ( ( l.getP1().distance(entrySide.getP1())<COLLISION_TRESHOLD && l.getP2().distance(entrySide.getP2())<COLLISION_TRESHOLD ) ||
                    ( l.getP1().distance(entrySide.getP2())<COLLISION_TRESHOLD && l.getP2().distance(entrySide.getP1())<COLLISION_TRESHOLD ) )
            {
                openLine = l;
            }
        }

        sides.remove(openLine);
        return sides;
    }

    public List<Line2D> getDZSides()
    {
        List< Line2D> sides = new ArrayList< Line2D>();
        for (DepthZone dz: depthZones)
        {
            List<? extends Line2D> dzSides = dz.getArea().getSides();
            for(Line2D l:dzSides)
            {
                if ( entrySide.ptSegDist(l.getP1())<COLLISION_TRESHOLD && entrySide.ptSegDist(l.getP2())<COLLISION_TRESHOLD )
                    continue;

                sides.add(l);
            }
        }

        return sides;
    }


    private Collection<? extends Anchorage> getSideAndSideAnchorages(Line2D l1, Line2D l2, double radius,double length)
    {
        List<Anchorage> aList = new ArrayList<Anchorage>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(l1,l2,radius) ;
        List<Double> calculatedDepths = new ArrayList<>();

        for (Circle2D ci:cList)
        {
            boolean adjusted = false;

            double maxDepth = 0;
            double depth = maximumDepth;
            int adjustmentCount =0;
            double previousDepth = maximumDepth;
            calculatedDepths.clear();

            while (!adjusted &&  (prevalidityCheck(ci, length)))
            {
                DepthZone deepest =  deepestZone(ci);
                depth = (deepest == null) ? maximumDepth: deepest.getDepth();

                if (depth == previousDepth)    // Converged!!!
                    adjusted = true;
                else if (calculatedDepths.contains(depth)) // Previously calculated depth
                {
                    depth = maxDepth;
                    adjusted = true;
                }
                else                            // Adjustment required
                {
                    double newRadius = ShipDynamics.getOuterRadius(length,depth,scale,riskFactor);
                    ci = GeomUtil.getTangentCircleAdjusted(l1,l2,newRadius,ci);
                    calculatedDepths.add(depth);
                    previousDepth = depth;
                    if (depth>maxDepth)
                        maxDepth = depth;
                    adjustmentCount++;
                }
                if (adjustmentCount>MAX_ADJUSTMENT_COUNT)
                {
                    throw new RuntimeException("Too many adjustment..");
                }
            }
            if (adjusted && validAnchorage(ci,length))
            {
                double innerRadius = length;
                double outerRadius = ShipDynamics.getOuterRadius(length,depth,scale);
                Anchorage a = new Anchorage(ci,outerRadius,innerRadius,l1,l2);
                aList.add(a);
            }
        }

        return aList;
    }

    private DepthZone deepestZone(Circle2D ci) {
        double maxDepth = -1;
        DepthZone deepest = null;
        for (DepthZone dz:depthZones)
        {
            if ( (dz.getArea().intersects(ci.parallel(DZ_INCLUSION_THRESHOLD).asAwtShape().getBounds2D())) &&
                  maxDepth< dz.getDepth()  )
            {
                deepest = dz;
                maxDepth = dz.getDepth();
            }
        }

        return deepest;  //To change body of created methods use File | Settings | File Templates.
    }

    private Collection<? extends Point2D> getSideAndSideCornerPoints(Line2D l1, Line2D l2, double radius,double length)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(l1,l2,radius) ;

        for (Circle2D ci:cList)
        {
            if (validAnchorage(ci, length))
                cpList.add(ci.center().getAsDouble());
        }

        return cpList;
    }

    private boolean prevalidityCheck(Circle2D ci, double length) {

        List<? extends Line2D> sides = area.getSides();


        if (ci==null)  // STG WRONG
            return false;

        //BOUNDARY CONDITION
        if (!area.contains(ci.center().getAsDouble()))
            return false;

        //PRE OVERLAPPING CONDITION
        for (Anchorage a: existingAnchorages)
        {
            if (ci.center().distance(a.getArea().center())< ( a.getArea().radius() ) )
            {
                return false;
            }
        }

        return true ;

    }

    private boolean validAnchorage(Circle2D ci, double length) {

        List<? extends Line2D> sides = area.getSides();


        if (ci==null)  // STG WRONG
            return false;

        //BOUNDARY CONDITION
        if (!area.contains(ci.center().getAsDouble()))
            return false;

        //OVERLAPPING CONDITION
        for (Anchorage a: existingAnchorages)
        {
            if (ci.center().distance(a.getArea().center())< (ci.radius() + a.getArea().radius() - COLLISION_TRESHOLD ) )
            {
                return false;
            }
        }

        // BOUNDARY CONDITION
        for (Line2D l : area.getSides())
        {
            if (l.ptSegDist(ci.center().getAsDouble())< (ci.radius()-COLLISION_TRESHOLD) )
            {
                return false;
            }

        }


        // DEPTH CONDITION CHECK
        if (!uniformAssumption)
        {
            for (DepthZone dz:depthZones)
            {
                Circle2D cip = ci.parallel(-1*DZ_INCLUSION_THRESHOLD);
                if (    dz.getArea().intersectsCircle(cip)  &&
                        (length>dz.getMaximumLength() ) )
                {
                    //System.out.println("Depth Zone Condition Fail: Length: " + length + " MaxLength: " + dz.getMaximumLength());
                    return false;
                }
            }
        }

        //Anchor Path Intersection Check ( IF ENABLED )
        if (maxIntersection >=0)
        {
            GraphPath<BaseVertex,BaseEdge> anchoragePath = getAnchoragePath(ci,true);
            int intersectionCount = getTotalIntersectionCount(anchoragePath);

            if (maxIntersection <intersectionCount)
            {
                return false;
            }
        }

        return true ;

    }

    public DepthZone depthZone(Point2D p)
    {
        for (DepthZone dz:depthZones)
        {
            if (dz.getArea().contains(p))
            {
                return dz;
            }
        }
        return null;
    }

    public DepthZone depthZone(Anchorage c)
    {
        return depthZone(c.getArea().center().getAsDouble());
    }

    public Point2D calculateEntryPoint(Point2D center)
    {
        return GeomUtil.getClosestPointOnSegment(entrySide,center);
    }

    public double getHoleDegree(Circle2D c)
    {
        double d1 = Double.MAX_VALUE-2 , d2 = Double.MAX_VALUE-1 , d3 = Double.MAX_VALUE; ;

        for (Anchorage a: existingAnchorages)
        {
            Circle2D ci = a.getArea();
            double d = c.center().distance(ci.center())-ci.radius();
            if (d<d1)
            {
                d3 = d2; d2=d1; d1=d;
            }
            else if (d<d2)
            {
                d3 = d2; d2=d;
            }
            if (d<d1)
            {
                d3 = d;
            }
        }

        for (Line2D l: area.getSides())
        {
            double d = l.ptLineDist(c.center().getAsDouble());
            if (d<d1)
            {
                d3 = d2; d2=d1; d1=d;
            }
            else if (d<d2)
            {
                d3 = d2; d2=d;
            }
            if (d<d1)
            {
                d3 = d;
            }
        }

        double hd = 1- d3/c.radius();

        return hd;
    }

    public double getMHD(List<Circle2D> candidates)
    {
        double mhd = Double.MIN_VALUE;

        for (Circle2D c:candidates)
        {
            double hd = getHoleDegree(c);
            if (mhd < hd)
            {
                mhd = hd;
            }
        }
        return mhd;
    }

    public Anchorage getMHDCircle(List<Anchorage> candidates)
    {
        double mhd = -1*Double.MAX_VALUE;
        Anchorage mhdAnchorage = null;

        for (Anchorage a:candidates)
        {
            Circle2D c = a.getArea() ;
            double hd = getHoleDegree(c);
            if (mhd < hd)
            {
                mhd = hd;
                mhdAnchorage = a;
            }
        }
        return mhdAnchorage;
    }


    private double getEffectiveAnchorageArea()
    {
        Rectangle2D br = getEffectiveAnchorageBoundingRect();
        return (br.getHeight()*br.getWidth());
    }

    private Rectangle2D getEffectiveAnchorageBoundingRect() {
        double maxX=-1*Double.MAX_VALUE;
        double maxY=-1*Double.MAX_VALUE;
        double minX=Double.MAX_VALUE;
        double minY=Double.MAX_VALUE;
        for (Anchorage a: existingAnchorages)
        {
            if (a.getArea().boundingBox().getMaxX()>maxX)
                maxX = a.getArea().boundingBox().getMaxX();
            if (a.getArea().boundingBox().getMaxY()>maxY)
                maxY = a.getArea().boundingBox().getMaxY();

            if (a.getArea().boundingBox().getMinX()<minX)
                minX = a.getArea().boundingBox().getMinX();
            if (a.getArea().boundingBox().getMinY()<minY)
                minY = a.getArea().boundingBox().getMinY();

        }

        return new Rectangle2D.Double(minX,minY,maxX-minX,maxY-minY);
    }


    public double calculateEffectiveAreaUtilization()
    {
        double effectiveAnchorageArea =  getEffectiveAnchorageArea();
        double eau =(effectiveAnchorageArea==0) ? 0: areaUtilization*totalArea / effectiveAnchorageArea ;
        return eau;
    }

    HashMap<Circle2D,GraphPath<BaseVertex,BaseEdge> > pathHistory = new HashMap<Circle2D,GraphPath<BaseVertex,BaseEdge> >();

    private GraphPath<BaseVertex,BaseEdge> checkHistory(Circle2D c)
    {
        if (pathHistory.containsKey(c))
        {
            return pathHistory.get(c);
        }
        else return null;
    }

    long dijkstraCallCount =0;

    private GraphPath<BaseVertex,BaseEdge>  getAnchoragePathWithoutObstacles(Circle2D c)
    {
        GraphPath<BaseVertex,BaseEdge> path = checkHistory(c);
        if ( path!= null )
            return path;

        if (modelGraph == null)
        {
            _createModelGraph(area.getBounds2D());
        }

        TimerUtil.start("DIJKSTRA");

        Point2D entryPoint = calculateEntryPoint(c.center().getAsDouble());
        GridVertex s = modelGraph.getClosestVertex(entryPoint);
        GridVertex t = modelGraph.getClosestVertex(c.center().getAsDouble());
        DijkstraWithHeuristicShortestPath<BaseVertex,BaseEdge> spalg = new DijkstraWithHeuristicShortestPath<BaseVertex, BaseEdge>(modelGraph,s,t,new MHDHeuristic<BaseVertex>(t));
        dijkstraCallCount++;

        TimerUtil.stop("DIJKSTRA");

        path  = spalg.getPath();
        pathHistory.put(c,path);
        return path;
    }

    public GraphPath<BaseVertex,BaseEdge>  getAnchoragePath(Circle2D c,boolean passThroughAnchorages /*todo*/)
    {
        if (passThroughAnchorages)
            return getAnchoragePathWithoutObstacles(c);
        else
            throw new NotImplementedException(); /*todo*/
    }

    public int getTotalIntersectionCount(GraphPath<BaseVertex,BaseEdge> path)
    {
        TimerUtil.start("INTERSECT");

        ObstacleGenerator og = new CircleListObstacleGenerator(getExistingAnchoragesAsCircles(),10000,0);

        List<ObstacleInterface> oList = null;
        int intersectionCount=0;
        try {
            oList = (List<ObstacleInterface>) og.generate();
            intersectionCount = Path.getTotalIntersectionCount(path,oList);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        TimerUtil.stop("INTERSECT");

        return intersectionCount;
    }

    public void addExistingAnchorage(Anchorage anchorage) {

        if  (collectStatistics)
        {
            Point2D entryPoint = calculateEntryPoint(anchorage.area.center().getAsDouble());
            double dist2Entry = anchorage.getArea().center().distance(entryPoint.getX(),entryPoint.getY());
            if ( minDistanceToEntry > dist2Entry || minDistanceToEntry == -1  )
                minDistanceToEntry = dist2Entry;

            totalMinDistanceToEntry +=  minDistanceToEntry;

            GraphPath<BaseVertex,BaseEdge> anchoragePath = getAnchoragePath(anchorage.getArea(),true);

            int intersectionCount = getTotalIntersectionCount(anchoragePath);

            if (intersectionCount>0)
            {
                boolean aha = true;
            }

            totalAnchoragePathLength +=  anchoragePath.getWeight();
            totalAnchorageIntersection += intersectionCount;

        }

        existingAnchorages.add(anchorage);

        if  (collectStatistics)
        {
            areaUtilization  = areaUtilization + anchorage.calculateArea()/totalArea ;
            effAreaUtilization = calculateEffectiveAreaUtilization();
            totalEffAreaUtilization += effAreaUtilization;

            updateDepartureIntersectionCount();

            try {
                printStatistics();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    private void updateDepartureIntersectionCount() {
        //if (existingAnchorages.size()%departureIntersectionMeasurePeriod!=0)
         //   return;

        int totalIntersect =0;
        for (Anchorage a: existingAnchorages)
        {

            GraphPath<BaseVertex,BaseEdge> anchoragePath =  getAnchoragePath(a.getArea(),true);
            int intersect = getTotalIntersectionCount(anchoragePath);
            totalIntersect += (intersect-1); // Ignore the self intersection
        }
        totalDepartureIntersection += (double)totalIntersect/(double)existingAnchorages.size();

        departureIntersectionMeasureCount++;
    }



    public void updateSummaryStatistics()
    {

        double avgAnchorPathLength  =existingAnchorages.isEmpty() ?  0 :  totalAnchoragePathLength/existingAnchorages.size() ;
        double avgIntersection      =existingAnchorages.isEmpty() ?  0 :  (double)totalAnchorageIntersection/(double)existingAnchorages.size() ;
        double avgMinDistToEntry    =existingAnchorages.isEmpty() ?  0 :  totalMinDistanceToEntry/existingAnchorages.size() ;
        double avgEffAreaUtilization  =existingAnchorages.isEmpty() ?  0 :  totalEffAreaUtilization/existingAnchorages.size() ;

        double avgDepartureIntersection = (departureIntersectionMeasureCount == 0) ? 0: (double)totalDepartureIntersection/(double)departureIntersectionMeasureCount;


        sAvgRejectCount =  (double)(sAvgRejectCount*experimentCount + rejectCount)/(double)(experimentCount+1);
        sAvgAnchorCount =  (double)(sAvgAnchorCount*experimentCount + existingAnchorages.size())/(double)(experimentCount+1);
        sAreaUtilization  = (sAreaUtilization*experimentCount + areaUtilization)/(experimentCount+1);
        sAvgAnchorPathLength  = (sAvgAnchorPathLength*experimentCount + avgAnchorPathLength)/(experimentCount+1);
        sAvgIntersection  = (sAvgIntersection*experimentCount + avgIntersection)/(experimentCount+1);
        sAvgDepartureIntersection  = (sAvgDepartureIntersection*experimentCount + avgDepartureIntersection)/(experimentCount+1);
        sAvgMindistToEntry  = (sAvgMindistToEntry*experimentCount + avgMinDistToEntry)/(experimentCount+1);
        sAvgEffAreaUtilization  = (sAvgEffAreaUtilization*experimentCount + avgEffAreaUtilization)/(experimentCount+1);

        experimentCount++;
    }

    public void reset()
    {
        existingAnchorages.clear();
        candidateAnchorages.clear();
        totalAnchoragePathLength = 0;
        totalAnchorageIntersection = 0;
        totalDepartureIntersection=0;
        totalMinDistanceToEntry = 0;
        totalEffAreaUtilization=0;

        rejectCount = 0;
        consecutive_rejectCount=0;

        departureIntersectionMeasureCount =0;

        minDistanceToEntry = -1;
        areaUtilization = 0;
        effAreaUtilization=0;

        dijkstraCallCount =0;

        pathHistory.clear();

        areaFull = false;
    }

    public void resetSummary()
    {
        reset();
        sAreaUtilization=0;
        sAvgAnchorPathLength=0;
        sAvgIntersection=0;
        sAvgMindistToEntry=0;
        sAvgAnchorCount=0;
        sAvgEffAreaUtilization=0;
        sAvgDepartureIntersection =0;
        sAvgRejectCount=0;

        experimentCount = 0;

    }

}