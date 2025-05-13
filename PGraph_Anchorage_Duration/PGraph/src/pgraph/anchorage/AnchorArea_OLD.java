package pgraph.anchorage;

import math.geom2d.conic.Circle2D;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import pgraph.CircleListObstacleGenerator;
import pgraph.ObstacleGenerator;
import pgraph.ObstacleInterface;
import pgraph.Path;
import pgraph.util.GeomUtil;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridVertex;
import pgraph.gui.BaseGraphPanel;
import pgraph.util.Polygon2D;
import pgraph.util.StringUtil;

import java.awt.*;
import java.awt.geom.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 30.10.2013
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class AnchorArea_OLD implements BaseGraphPanel.Renderable
{
    private static final double COLLISION_TRESHOLD = 0.001;
    private static final int MODEL_GRAPH_DEGREE = 2;
    private final int MODEL_GRAPH_UNIT_EDGE_LEN=2;
    private double sAvgMindistToEntry=0;
    private double sAvgIntersection=0;
    private double sAvgAnchorPathLength = 0;
    private int experimentCount=0;
    private double sAreaUtilization=0;
    private double sAvgAnchorCount = 0;
    private double sAvgEffAreaUtilization=0;


    Polygon2D area;
    List<Anchorage> existingAnchorages= new ArrayList<Anchorage>();
    List<Anchorage> candidateAnchorages = new ArrayList<Anchorage>();

    List<Anchorage> arrivals = new ArrayList<Anchorage>();

    Line2D entrySide;
    Point2D entryPoint;
    private double minDistanceToEntry = -1;
    private double areaUtilization=0;
    private double effAreaUtilization=0;
    private double totalArea = 0;

    private GridDirectedGraph modelGraph = null;
    private double totalAnchoragePathLength = 0;
    private int totalAnchorageIntersection=0;
    private double totalMinDistanceToEntry= 0;
    private double totalEffAreaUtilization=0;

    private String outputFile=null;
    private String outputSummaryFile=null;



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

        modelGraph=  new GridDirectedGraph(MODEL_GRAPH_DEGREE,(int)(maxX-minX)/MODEL_GRAPH_UNIT_EDGE_LEN+1,(int)(maxY-minY)/MODEL_GRAPH_UNIT_EDGE_LEN+1,MODEL_GRAPH_UNIT_EDGE_LEN,new Point2D.Double(minX,minY),0,0,0,0 );

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


    public void newArrival(Anchorage a)
    {
        arrivals.add(a);
    }
    public void clearArrivals()
    {
        arrivals.clear();
    }

    public Point2D getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(Point2D entryPoint) {
        this.entryPoint = entryPoint;
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

    public void setCandidateAnchoragesFromCircles(List<Circle2D> candidateAnchorages) {
        this.existingAnchorages.clear();

        for (Circle2D c: candidateAnchorages)
        {
            this.candidateAnchorages.add(new Anchorage(c));
        }
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
                    "Avg Anchorage Count: "       + StringUtil.padLeft(nf.format(sAvgAnchorCount),5) + "   "+
                    "Avg Area Utilization: "       + StringUtil.padLeft(nf.format(sAreaUtilization),5) + "   "+
                    "Avg Eff. Area Utilization: "       + StringUtil.padLeft(nf.format(sAvgEffAreaUtilization),5) + "   "+
                    "Avg Min Dist To Entry: "  + StringUtil.padLeft(nf.format(sAvgMindistToEntry),8) + "  "+
                    "Avg_AnchPathLen: " + StringUtil.padLeft(nf.format(sAvgAnchorPathLength),8) + "    "+
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

        if (!collectStatistics)
            return;

        AffineTransform t = null;
        try {
            t = transform.createInverse().createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        g.setColor(Color.black);

        if (t != null)
        {
            t.scale(1,-1);
            g.setTransform(t);
        }

        double avgAnchorPathLength      =existingAnchorages.isEmpty() ?  0 :  totalAnchoragePathLength/existingAnchorages.size() ;
        double avgIntersection          =existingAnchorages.isEmpty() ?  0 :  (double)totalAnchorageIntersection/(double)existingAnchorages.size() ;
        double avgMinDistanceToEntry    =existingAnchorages.isEmpty() ?  0 :  (double)totalMinDistanceToEntry/(double)existingAnchorages.size() ;
        double avgEffAreaUtilization    =existingAnchorages.isEmpty() ?  0 :  (double)totalEffAreaUtilization/(double)existingAnchorages.size() ;

        NumberFormat nf = NumberFormat.getInstance();

        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(3);

        g.setFont(new Font("Arial",0,6));

        g.drawString("Anchorage Count     : " + existingAnchorages.size(), (float) area.getBounds2D().getMinX(),(float) area.getBounds2D().getMinY()+20);
        g.drawString("Area Utilization    : " + nf.format(areaUtilization), (float) area.getBounds2D().getMinX(),(float) area.getBounds2D().getMinY()+30);
        g.drawString("Ef_Area Utilization : " + nf.format(effAreaUtilization), (float) area.getBounds2D().getMinX(),(float) area.getBounds2D().getMinY()+40);
        g.drawString("Avg_Ef_Area Utilization : " + nf.format(avgEffAreaUtilization), (float) area.getBounds2D().getMinX(),(float) area.getBounds2D().getMinY()+50);
        g.drawString("Min Dist. To Entry  : " + nf.format(minDistanceToEntry), (float) area.getBounds2D().getMinX(),(float) area.getBounds2D().getMinY()+60);
        g.drawString("Avg. MinDistToEntry : " + nf.format(avgMinDistanceToEntry), (float) area.getBounds2D().getMinX(),(float) area.getBounds2D().getMinY()+70);
        g.drawString("Avg AnchorPath Len  : " + nf.format(avgAnchorPathLength), (float) area.getBounds2D().getMinX(),(float) area.getBounds2D().getMinY()+80);
        g.drawString("Total Intersection  : " + totalAnchorageIntersection, (float) area.getBounds2D().getMinX(),(float) area.getBounds2D().getMinY()+90);
        g.drawString("Avg Intersection    : " + nf.format(avgIntersection), (float) area.getBounds2D().getMinX(),(float) area.getBounds2D().getMinY()+100);
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

    public List<Point2D> getAllCornerPoints(double radius)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        cpList.addAll(getSideAndSideCornerPoints(radius));
        cpList.addAll(getSideAndCircleCornerPoints(radius));
        cpList.addAll(getCircleAndCircleCornerPoints(radius));

        return cpList;
    }


    public List<Circle2D> getAllCandidateAnchorages(double radius)
    {
        List<Circle2D> caList = new ArrayList<Circle2D>();
        List<Point2D> cpList = new ArrayList<Point2D>();

        if (existingAnchorages.size()== 1)
        {
            cpList.addAll(getSideAndSideCornerPoints(radius));
            if( cpList.isEmpty())
                cpList.addAll(getSideAndCircleCornerPoints(radius));
        }  else
        {
            cpList.addAll(getSideAndSideCornerPoints(radius));
            cpList.addAll(getSideAndCircleCornerPoints(radius));
            cpList.addAll(getCircleAndCircleCornerPoints(radius));
        }


        for (Point2D p : cpList)
        {
            caList.add(new Circle2D(p.getX(),p.getY(),radius));
        }

        return caList;
    }



    private Collection<? extends Point2D> getCircleAndCircleCornerPoints(double radius)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();




        for (int i = 0; i< existingAnchorages.size()-1;i++ )
        {
            Circle2D ci = existingAnchorages.get(i).getArea();
            for (int j = i+1; j < existingAnchorages.size();j++)
            {
                Circle2D cj = existingAnchorages.get(j).getArea();
                cpList.addAll(getCircleAndCircleCornerPoints(ci,cj,radius));
            }
        }


        return cpList;
    }

    private Collection<? extends Point2D> getCircleAndCircleCornerPoints(Circle2D c1, Circle2D c2, double radius)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(new Circle2D(c1.center(), c1.radius()), new Circle2D(c2.center(), c2.radius()), radius) ;

        for (Circle2D ci:cList)
        {
            if (validAnchorage(ci))
                cpList.add(ci.center().getAsDouble());
        }

        return cpList;
    }

    private Collection<? extends Point2D> getSideAndCircleCornerPoints(double radius) {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<? extends Line2D> sides = getAreaSides();

        for (Line2D l : sides )
        {
            for (Anchorage a: existingAnchorages)
            {
                cpList.addAll(getSideAndCircleCornerPoints(l,a.getArea(),radius));
            }
        }


        return cpList;

    }

    private Collection<? extends Point2D> getSideAndCircleCornerPoints(Line2D l, Circle2D ce, double radius) {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(new Circle2D(ce.center(), ce.radius()), l, radius) ;

        for (Circle2D ci:cList)
        {
            if (validAnchorage(ci))
                cpList.add(ci.center().getAsDouble());
        }

        return cpList;
    }

    private Collection<? extends Point2D> getSideAndSideCornerPoints(double radius)
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
                cpList.addAll(getSideAndSideCornerPoints(l1, l2, radius));
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


    private Collection<? extends Point2D> getSideAndSideCornerPoints(Line2D l1, Line2D l2, double radius)
    {
        List<Point2D> cpList = new ArrayList<Point2D>();

        List<Circle2D> cList = GeomUtil.getTangentCircles(l1,l2,radius) ;

        for (Circle2D ci:cList)
        {
            if (validAnchorage(ci))
                cpList.add(ci.center().getAsDouble());
        }

        return cpList;
    }

    private boolean validAnchorage(Circle2D ci) {

        List<? extends Line2D> sides = area.getSides();


        boolean hasIntersection = false;

        if (!area.contains(ci.center().getAsDouble()))
            return false;

        for (Anchorage a: existingAnchorages)
        {
            if (ci.center().distance(a.getArea().center())< (ci.radius() + a.getArea().radius() - COLLISION_TRESHOLD ) )
            {
                hasIntersection=true;
                break;
            }
        }


        for (Line2D l : area.getSides())
        {
            if (l.ptLineDist(ci.center().getAsDouble())< (ci.radius()-COLLISION_TRESHOLD) )
            {
                hasIntersection = true;
                break;
            }

        }

        return !hasIntersection;

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

    public Circle2D getMHDCircle(List<Circle2D> candidates)
    {
        double mhd = -1*Double.MAX_VALUE;
        Circle2D mhdCircle = null;

        for (Circle2D c:candidates)
        {
            double hd = getHoleDegree(c);
            if (mhd < hd)
            {
                mhd = hd;
                mhdCircle = c;
            }
        }
        return mhdCircle;
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



    public GraphPath<BaseVertex,BaseEdge>  getAnchoragePath(Circle2D c,boolean passThroughAnchorages /*todo*/)
    {
        if (modelGraph == null)
        {
            _createModelGraph(area.getBounds2D());
        }

        /*Revision: 08.01.2014*/
        Point2D entryPoint = calculateEntryPoint(c.center().getAsDouble());

        GridVertex s = modelGraph.getClosestVertex(entryPoint);
        GridVertex t = modelGraph.getClosestVertex(c.center().getAsDouble());
        DijkstraShortestPath<BaseVertex,BaseEdge> spalg = new DijkstraShortestPath<BaseVertex, BaseEdge>(modelGraph,s,t);

        GraphPath<BaseVertex,BaseEdge> path  = spalg.getPath();
        return path;
    }

    public int getTotalIntersectionCount(GraphPath<BaseVertex,BaseEdge> path)
    {
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
        return intersectionCount;
    }

    public void addExistingAnchorage(Anchorage anchorage) {

        if  (collectStatistics)
        {
            double dist2Entry = anchorage.getArea().center().distance(entryPoint.getX(),entryPoint.getY());
            if ( minDistanceToEntry > dist2Entry || minDistanceToEntry == -1  )
                minDistanceToEntry = dist2Entry;

            totalMinDistanceToEntry +=  minDistanceToEntry;

            GraphPath<BaseVertex,BaseEdge> anchoragePath = getAnchoragePath(anchorage.getArea(),true);

            int intersectionCount = getTotalIntersectionCount(anchoragePath);

            totalAnchoragePathLength +=  anchoragePath.getWeight();
            totalAnchorageIntersection += intersectionCount;


        }

        existingAnchorages.add(anchorage);

        if  (collectStatistics)
        {
            areaUtilization  = areaUtilization + anchorage.calculateArea()/totalArea ;
            effAreaUtilization = calculateEffectiveAreaUtilization();
            totalEffAreaUtilization += effAreaUtilization;
            try {
                printStatistics();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    public void updateSummaryStatistics()
    {

        double avgAnchorPathLength  =existingAnchorages.isEmpty() ?  0 :  totalAnchoragePathLength/existingAnchorages.size() ;
        double avgIntersection      =existingAnchorages.isEmpty() ?  0 :  (double)totalAnchorageIntersection/(double)existingAnchorages.size() ;
        double avgMinDistToEntry    =existingAnchorages.isEmpty() ?  0 :  totalMinDistanceToEntry/existingAnchorages.size() ;
        double avgEffAreaUtilization  =existingAnchorages.isEmpty() ?  0 :  totalEffAreaUtilization/existingAnchorages.size() ;

        sAvgAnchorCount =  (double)(sAvgAnchorCount*experimentCount + existingAnchorages.size())/(double)(experimentCount+1);
        sAreaUtilization  = (sAreaUtilization*experimentCount + areaUtilization)/(experimentCount+1);
        sAvgAnchorPathLength  = (sAvgAnchorPathLength*experimentCount + avgAnchorPathLength)/(experimentCount+1);
        sAvgIntersection  = (sAvgIntersection*experimentCount + avgIntersection)/(experimentCount+1);
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
        totalMinDistanceToEntry = 0;
        totalEffAreaUtilization=0;

        minDistanceToEntry = -1;
        areaUtilization = 0;
        effAreaUtilization=0;

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

        experimentCount = 0;

    }

    public Point2D calculateEntryPoint(Point2D center)
    {
        return GeomUtil.getClosestPointOnSegment(entrySide,center);
    }

}