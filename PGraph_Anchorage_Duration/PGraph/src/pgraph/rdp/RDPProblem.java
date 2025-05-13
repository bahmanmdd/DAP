package pgraph.rdp;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;
import pgraph.CAOStar.CAOStar;
import pgraph.ObstacleInterface;
import pgraph.Path;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridVertex;
import pgraph.intersectionhandler.IntersectionHandler;
import pgraph.util.Pen;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 27.01.2013
 * Time: 16:59
 *
 * Implemens RDP calculations
 */
public class RDPProblem {
    int disambiguationCount = 0;
    int numberOfDisagPoints = 0;
    double disambiguationCost = 0.0;
    double DPSHeuristicDTAFactor = 1.0;
    GridDirectedGraph initialGraph;
    GridDirectedGraph templateGraph;
    List<RDPObstacleInterface> roList;
    RDPIntersectionHandler intersectionHandler = null;

    public List<Path> allPathList = new ArrayList<Path>();
    public List<Path> actualPathList = new ArrayList<Path>();

    public double getDisambiguationCost() {
        return disambiguationCost;
    }

    public int getDisambiguationCount() {
        return disambiguationCount;
    }

    public GridDirectedGraph getInitialGraph() {
        return initialGraph;
    }

    public List<RDPObstacleInterface> getRoList() {
        return roList;
    }

    public RDPPathFinder getPathFinder() {
        return pathFinder;
    }

    public void setPathFinder(RDPPathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    RDPPathFinder pathFinder = null;

    public RDPIntersectionHandler getIntersectionHandler() {
        return intersectionHandler;
    }

    public void setIntersectionHandler(RDPIntersectionHandler intersectionHandler) {
        this.intersectionHandler = intersectionHandler;
    }

    public RDPProblem(List<RDPObstacleInterface> roList, int disambiguationCount, int numberOfDisagPoints, double disambiguationCost, GridDirectedGraph g) throws InstantiationException {
        initialGraph = g;
        templateGraph = initialGraph.cloneGraphWithoutObstacles();
        templateGraph.setIntersectionHandler(new BAOInformationStateIntersectionHandler());
        templateGraph.addObstacles(roList);
        this.roList = roList;
        this.disambiguationCount = disambiguationCount;
        this.numberOfDisagPoints = numberOfDisagPoints;
        this.disambiguationCost = disambiguationCost;
    }

    public RDPProblem(int disambiguationCount, double disambiguationCost, GridDirectedGraph g)
    {
        templateGraph = g;
        this.disambiguationCount = disambiguationCount;
        this.disambiguationCost = disambiguationCost;
    }

    public RDPProblem(List<RDPObstacleInterface> roList, int disambiguationCount, int numberOfDisagPoints, double disambiguationCost,int d, int x, int y, double el, Point2D.Double offset, int sX, int sY, int tX, int tY ) {
        initialGraph = new GridDirectedGraph(d,x,y,el,offset, sX, sY, tX, tY);
        this.roList = roList;
        this.disambiguationCount = disambiguationCount;
        this.numberOfDisagPoints = numberOfDisagPoints;
        this.disambiguationCost = disambiguationCost;
    }

    public double calculateExpectedWeight() throws InstantiationException, IOException {
        allPathList.clear();
        return _calculateExpectedWeightNoCopy(templateGraph.start.gridPos.getX(),templateGraph.start.gridPos.getY(),disambiguationCount,true);
    }

    public double calculateActualWeight() throws InstantiationException, IOException {
        actualPathList.clear();
        return _calculateActualWeightNoCopy(templateGraph.start.gridPos.getX(), templateGraph.start.gridPos.getY(), disambiguationCount,true);
    }

    List<RDPObstacleInterface> _createNewROList(List<RDPObstacleInterface> roListCurrent, RDPObstacleInterface roCurrent,boolean roState,int disambiguationCount )
    {
        List<RDPObstacleInterface> roList= new ArrayList<RDPObstacleInterface>();
        for (RDPObstacleInterface ro:roListCurrent)
        {
            RDPObstacleInterface r = ro.clone();
            if (disambiguationCount==0)
                r.setPassable(false);
            if (r.equals(roCurrent))
            {
                if (!roState)
                    continue;
                else
                    r.setPassable(false);
            }
            roList.add(r);
        }
        return roList;
    }

    GraphPath<BaseVertex,BaseEdge> _processPath(GridDirectedGraph g, GraphPath<BaseVertex,BaseEdge> gp)
    {
        int i=0;
        BaseEdge e = gp.getEdgeList().get(i++);
        ArrayList<BaseEdge> edgeList = new ArrayList<BaseEdge>();
        double totalCost = 0;

        while((!g.intersectedObstacles.containsKey(e) || g.intersectedObstacles.get(e).isEmpty())&&i<gp.getEdgeList().size())
        {
            totalCost += e.getEdgeWeight();
            edgeList.add(e);
            e = gp.getEdgeList().get(i++);
        }
        edgeList.add(e);
        return new GraphPathImpl<BaseVertex, BaseEdge>(g,edgeList.get(0).start,edgeList.get(edgeList.size()-1).end,edgeList,totalCost);
    }

    double _calculateExpectedWeight(List<RDPObstacleInterface> roList,int sX,int sY,int disambiguationCount) throws InstantiationException, IOException {
        System.out.println("Expected Weight Calculation Started From Point ["+sX+","+sY+"] K:"+disambiguationCount);

        GridDirectedGraph g = initialGraph.cloneGraphWithoutObstacles();
        g.setIntersectionHandler(intersectionHandler);
        g.start = g.getVertex(sX,sY);
        g.addObstacles(roList);

        // Fİnd Shortest path with current disambiguationCount
        GraphPath<BaseVertex,BaseEdge>gp = pathFinder.findShortestPath(g,disambiguationCount);

        double totalCost = 0;
        int i=0;
        BaseEdge e = gp.getEdgeList().get(i++);
        List<BaseEdge> subPathEdges = new ArrayList<BaseEdge>();

        while((!g.intersectedObstacles.containsKey(e) || g.intersectedObstacles.get(e).isEmpty())&&i<gp.getEdgeList().size())
        {
            subPathEdges.add(e);
            totalCost += e.getEdgeWeight();
            e = gp.getEdgeList().get(i++);
        }
        if (!subPathEdges.isEmpty())
        {
            if (!g.intersectedObstacles.containsKey(e) || g.intersectedObstacles.get(e).isEmpty())
            {
                subPathEdges.add(e);
                totalCost+=e.getEdgeWeight();
            }
            GraphPath<BaseVertex,BaseEdge>subPath = new GraphPathImpl<BaseVertex, BaseEdge>(g,subPathEdges.get(0).start,subPathEdges.get(subPathEdges.size()-1).end,subPathEdges,totalCost);
            allPathList.add(new Path(subPath, Pen.DefaultPen));
        }
        if (!g.intersectedObstacles.containsKey(e) || g.intersectedObstacles.get(e).isEmpty())
            return totalCost;


        List<ObstacleInterface> iolist = g.intersectedObstacles.get(e);
        RDPObstacleInterface o =(RDPObstacleInterface) iolist.get(0);

        int newSX= ((GridVertex)e.start).gridPos.getX();
        int newSY= ((GridVertex)e.start).gridPos.getY();


        double trueCost = _calculateExpectedWeight(_createNewROList(roList,o,true,disambiguationCount-1),newSX,newSY,disambiguationCount-1);
        double falseCost = _calculateExpectedWeight(_createNewROList(roList,o,false,disambiguationCount-1),newSX,newSY,disambiguationCount-1);

        totalCost = totalCost + disambiguationCost + o.getP()*trueCost + (1-o.getP())*falseCost;
        return totalCost;
    }

    double _calculateZeroRiskPath(List<Path> pathList, boolean disambiguationResult) throws InstantiationException, IOException
    {
        ((RDPIntersectionHandler)templateGraph.getIntersectionHandler()).setZeroRiskState(true);
        templateGraph.updateIntersectingEdgeCosts();


        GraphPath<BaseVertex,BaseEdge>gp = pathFinder.findShortestPath(templateGraph,disambiguationCount);

        double cost = gp.getWeight();

        ((RDPIntersectionHandler)templateGraph.getIntersectionHandler()).setZeroRiskState(false);
        templateGraph.updateIntersectingEdgeCosts();

        if (!gp.getEdgeList().isEmpty())
            pathList.add(new Path(gp,disambiguationResult? new Pen(Color.red, Pen.PenStyle.PS_Normal):new Pen(Color.red, Pen.PenStyle.PS_Dashed)));

        return cost;
    }

    public double zeroRiskLengthForUpperBound(GridVertex startVertex, GridVertex endVertex, HashMap<RDPObstacleInterface, Character> informationState) throws InstantiationException, IOException
    {
        templateGraph.start = templateGraph.getVertex(startVertex.gridPos.getX(),startVertex.gridPos.getY());
        templateGraph.end = templateGraph.getVertex(endVertex.gridPos.getX(),endVertex.gridPos.getY());

        Iterator it = informationState.keySet().iterator();
        while (it.hasNext()) {
            RDPObstacleInterface key = (RDPObstacleInterface) it.next();
            if (informationState.get(key).equals('t') || informationState.get(key).equals('a')) {
                if (key.shape().contains(startVertex.pos) || key.shape().contains(endVertex.pos)) {
                    return  Double.POSITIVE_INFINITY;
                } else {
                    templateGraph.setObstaclePassability(key,false);
                }
            } else {
                templateGraph.setObstaclePassability(key,true);
            }
        }

        GraphPath<BaseVertex,BaseEdge>gp = pathFinder.findShortestPath(templateGraph,0);
        double cost = gp.getWeight();

        return cost;
    }

    public double zeroRiskLengthForLowerBound(GridVertex startVertex, GridVertex endVertex, HashMap<RDPObstacleInterface, Character> informationState) throws InstantiationException, IOException
    {
        templateGraph.start = templateGraph.getVertex(startVertex.gridPos.getX(),startVertex.gridPos.getY());
        templateGraph.end = templateGraph.getVertex(endVertex.gridPos.getX(),endVertex.gridPos.getY());

        Iterator it = informationState.keySet().iterator();
        while (it.hasNext()) {
            RDPObstacleInterface key = (RDPObstacleInterface) it.next();
            if ( (informationState.get(key).equals('t') || informationState.get(key).equals('a')) && (key.shape().contains(startVertex.pos) || key.shape().contains(endVertex.pos)) ) {
                return  Double.POSITIVE_INFINITY;
            } else {
                if (informationState.get(key).equals('t')) {
                    templateGraph.setObstaclePassability(key,false);
                } else {
                    //System.out.println(key);
                    templateGraph.setObstaclePassability(key,true);
                }
            }
        }

        GraphPath<BaseVertex,BaseEdge>gp = pathFinder.findShortestPath(templateGraph,0);
        double cost = gp.getWeight();

        return cost;
    }

    public double calculateExpectedWeight(GridVertex startVertex, GridVertex endVertex, HashMap<RDPObstacleInterface, Character> informationState,RDPIntersectionHandler intersectionHandler) throws InstantiationException, IOException {
        IntersectionHandler swapIntersectionHandler = templateGraph.getIntersectionHandler();
        templateGraph.setIntersectionHandler(intersectionHandler);
        templateGraph.updateIntersectingEdgeCosts();
        allPathList.clear();
        int allowedDisambiguationCount = disambiguationCount;
        Iterator it = informationState.keySet().iterator();
        while (it.hasNext()) {
            RDPObstacleInterface key = (RDPObstacleInterface) it.next();
            if (informationState.get(key).equals('t')) {
                allowedDisambiguationCount--;
                if (key.shape().contains(startVertex.pos) || key.shape().contains(endVertex.pos)) {
                    return  Double.POSITIVE_INFINITY;
                } else {
                    templateGraph.setObstaclePassability(key,false);
                }
            } else {
                if (informationState.get(key).equals('f')) allowedDisambiguationCount--;
                templateGraph.setObstaclePassability(key,true);
            }
        }

        double expectedLength = _calculateExpectedWeightNoCopy(startVertex.gridPos.getX(),startVertex.gridPos.getY(),allowedDisambiguationCount,true);

        templateGraph.setIntersectionHandler(swapIntersectionHandler);
        templateGraph.updateIntersectingEdgeCosts();

        return expectedLength;
    }

    public double runBAOStar(CAOStar CAOStar) throws IOException, InstantiationException {

        System.out.println("*** BAO* IS STARTING ***");

        double optValue = CAOStar.executeAlgorithm(this);

        return optValue;
    }


    double _calculateExpectedWeightNoCopy(int sX,int sY,int disambiguationCount,boolean disambiguationReult) throws InstantiationException, IOException {
        //System.out.println("Expected Weight Calculation Started From Point ["+sX+","+sY+"] K:"+disambiguationCount);
        templateGraph.start = templateGraph.getVertex(sX,sY);

        double totalCost = 0;
        if (disambiguationCount<=0)
        {
            totalCost = _calculateZeroRiskPath(allPathList,disambiguationReult);
            return totalCost;
        }

        // Fİnd Shortest path with current disambiguationCount
        GraphPath<BaseVertex,BaseEdge>gp = pathFinder.findShortestPath(templateGraph,disambiguationCount);
        //System.out.println("Shortest Path Len = "+gp.getWeight());

        int i=0;
        BaseEdge e = gp.getEdgeList().get(i++);
        List<BaseEdge> subPathEdges = new ArrayList<BaseEdge>();

        while((!templateGraph.intersectedObstacles.containsKey(e) || templateGraph.intersectedObstacles.get(e).isEmpty())&&i<gp.getEdgeList().size())
        {
            subPathEdges.add(e);
            totalCost += e.getEdgeWeight();
            e = gp.getEdgeList().get(i++);
        }
        if (!subPathEdges.isEmpty())
        {
            if (!templateGraph.intersectedObstacles.containsKey(e) || templateGraph.intersectedObstacles.get(e).isEmpty())
            {
                subPathEdges.add(e);
                totalCost+=e.getEdgeWeight();
            }
            //System.out.println("Current Cost : " + totalCost);
            GraphPath<BaseVertex,BaseEdge>subPath = new GraphPathImpl<BaseVertex, BaseEdge>(templateGraph,subPathEdges.get(0).start,subPathEdges.get(subPathEdges.size()-1).end,subPathEdges,totalCost);
            allPathList.add(new Path(subPath, disambiguationReult ? new Pen(Color.blue, Pen.PenStyle.PS_Normal):new Pen(Color.blue, Pen.PenStyle.PS_Dashed)));
        }
        if (!templateGraph.intersectedObstacles.containsKey(e) || templateGraph.intersectedObstacles.get(e).isEmpty())
            return totalCost;


        List<ObstacleInterface> iolist = templateGraph.intersectedObstacles.get(e);
        RDPObstacleInterface o =(RDPObstacleInterface) iolist.get(0);

        int newSX= ((GridVertex)e.start).gridPos.getX();
        int newSY= ((GridVertex)e.start).gridPos.getY();

        templateGraph.setObstaclePassability(o,false);
        double trueCost = _calculateExpectedWeightNoCopy(newSX,newSY,disambiguationCount-1,true);
        templateGraph.setObstaclePassability(o,true);


        templateGraph.removeObstacle(o);
        double falseCost = _calculateExpectedWeightNoCopy(newSX,newSY,disambiguationCount-1,false);
        templateGraph.addObstacle(o);

        totalCost = totalCost + disambiguationCost + o.getP()*trueCost + (1-o.getP())*falseCost;
        return totalCost;
    }


    double _calculateActualWeight(List<RDPObstacleInterface> roList,int sX,int sY,int disambiguationCount) throws InstantiationException, IOException {
        System.out.println("Expected Weight Calculation Started From Point ["+sX+","+sY+"] K:"+disambiguationCount);
        GridDirectedGraph g = initialGraph.cloneGraphWithoutObstacles();
        g.setIntersectionHandler(intersectionHandler);
        g.start = g.getVertex(sX,sY);
        g.addObstacles(roList);

        /*todo: finding shortest with K disambiguation must be performed here*/
        // Fİnd Shortest path with current disambiguationCount
        GraphPath<BaseVertex,BaseEdge>gp = pathFinder.findShortestPath(g, disambiguationCount);
        /*todo: finding shortest with K disambiguation must be performed here*/

        double totalCost = 0;
        int i=0;
        BaseEdge e = gp.getEdgeList().get(i++);
        List<BaseEdge> subPathEdges = new ArrayList<BaseEdge>();

        while((!g.intersectedObstacles.containsKey(e) || g.intersectedObstacles.get(e).isEmpty())&& i<gp.getEdgeList().size())
        {
            subPathEdges.add(e);
            totalCost += e.getEdgeWeight();
            e = gp.getEdgeList().get(i++);
        }
        if (!subPathEdges.isEmpty())
        {
            if (!g.intersectedObstacles.containsKey(e) || g.intersectedObstacles.get(e).isEmpty())
            {
                subPathEdges.add(e);
                totalCost+=e.getEdgeWeight();
            }
            GraphPath<BaseVertex,BaseEdge>subPath = new GraphPathImpl<BaseVertex, BaseEdge>(g,subPathEdges.get(0).start,subPathEdges.get(subPathEdges.size()-1).end,subPathEdges,totalCost);
            actualPathList.add(new Path(subPath, Pen.DefaultPen));
        }
        if (!g.intersectedObstacles.containsKey(e) || g.intersectedObstacles.get(e).isEmpty())
            return totalCost;

        RDPObstacleInterface o =(RDPObstacleInterface) g.intersectedObstacles.get(e).get(0);

        int newSX= ((GridVertex)e.start).gridPos.getX();
        int newSY= ((GridVertex)e.start).gridPos.getY();

        double cost= o.isTrueObstacle()? _calculateActualWeight(_createNewROList(roList, o, true,disambiguationCount-1), newSX, newSY, disambiguationCount - 1):_calculateActualWeight(_createNewROList(roList, o, false,disambiguationCount-1), newSX, newSY, disambiguationCount - 1);

        totalCost = totalCost + disambiguationCost + cost;
        return totalCost;
    }


    double _calculateActualWeightNoCopy(int sX,int sY,int disambiguationCount,boolean disambiguationResult) throws InstantiationException, IOException {
        System.out.println("Actual Weight Calculation Started From Point ["+sX+","+sY+"] K:"+disambiguationCount);

        templateGraph.start = templateGraph.getVertex(sX,sY);

        double totalCost = 0;
        if (disambiguationCount==0)
        {
            totalCost = _calculateZeroRiskPath(actualPathList, disambiguationResult);
            return totalCost;
        }

        /*todo: finding shortest with K disambiguation must be performed here*/
        // Fİnd Shortest path with current disambiguationCount
        GraphPath<BaseVertex,BaseEdge>gp = pathFinder.findShortestPath(templateGraph, disambiguationCount);
        System.out.println("Shortest Path Len = "+gp.getWeight());
        /*todo: finding shortest with K disambiguation must be performed here*/


        int i=0;
        BaseEdge e = gp.getEdgeList().get(i++);
        List<BaseEdge> subPathEdges = new ArrayList<BaseEdge>();

        while((!templateGraph.intersectedObstacles.containsKey(e) || templateGraph.intersectedObstacles.get(e).isEmpty())&& i<gp.getEdgeList().size())
        {
            subPathEdges.add(e);
            totalCost += e.getEdgeWeight();
            e = gp.getEdgeList().get(i++);
        }
        if (!subPathEdges.isEmpty()) // If we have traversed some edges
        {
            if (!templateGraph.intersectedObstacles.containsKey(e) || templateGraph.intersectedObstacles.get(e).isEmpty())
            {
                subPathEdges.add(e);
                totalCost+=e.getEdgeWeight();
            }
            GraphPath<BaseVertex,BaseEdge>subPath = new GraphPathImpl<BaseVertex, BaseEdge>(templateGraph,subPathEdges.get(0).start,subPathEdges.get(subPathEdges.size()-1).end,subPathEdges,totalCost);
            actualPathList.add(new Path(subPath, Pen.DefaultPen));
        }
        if (!templateGraph.intersectedObstacles.containsKey(e) || templateGraph.intersectedObstacles.get(e).isEmpty())
            return totalCost;

        RDPObstacleInterface o =(RDPObstacleInterface) templateGraph.intersectedObstacles.get(e).get(0);

        int newSX= ((GridVertex)e.start).gridPos.getX();
        int newSY= ((GridVertex)e.start).gridPos.getY();

        double cost = 0;
        if (o.isTrueObstacle())
        {
            templateGraph.setObstaclePassability(o,false);

            cost=  _calculateActualWeightNoCopy(newSX, newSY, disambiguationCount - 1,true);

            templateGraph.setObstaclePassability(o,true);
        }
        else // Actual existence : FALSE , So remove obstacle
        {
            templateGraph.removeObstacle(o);
            cost = _calculateActualWeightNoCopy(newSX, newSY, disambiguationCount - 1,false);
            templateGraph.addObstacle(o);
        }

        totalCost = totalCost + disambiguationCost+cost;
        return totalCost;
    }

    public int getNumberOfDisagPoints() {
        return numberOfDisagPoints;
    }

    public void setNumberOfDisagPoints(int numberOfDisagPoints) {
        this.numberOfDisagPoints = numberOfDisagPoints;
    }

    public void setDisambiguationCount(int disambiguationCount) {
        this.disambiguationCount = disambiguationCount;
    }

    public double getDPSHeuristicDTAFactor() {
        return DPSHeuristicDTAFactor;
    }

    public void setDPSHeuristicDTAFactor(double DPSHeuristicDTAFactor) {
        this.DPSHeuristicDTAFactor = DPSHeuristicDTAFactor;
    }
}
