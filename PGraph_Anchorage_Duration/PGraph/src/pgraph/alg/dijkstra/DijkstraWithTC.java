package pgraph.alg.dijkstra;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridVertex;
import pgraph.grid.tcgrid.TCGridVertex;
import pgraph.util.ArcMath;
import pgraph.util.MathUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 25.07.2013
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */
public class DijkstraWithTC {

    public HashMap<GridVertex,DijkstraMapEntry> dijkstraMap= new HashMap<GridVertex,DijkstraMapEntry>();

    private final GridDirectedGraph graph;
    private final double minTurnRadius;
    private double maxTurnAngle = 0 ;

    FibonacciHeap<DijkstraHeapNode> heap = new FibonacciHeap<>();
    private double straightPathLen=0;
    private boolean _debug= true;


    public DijkstraWithTC(GridDirectedGraph g, double mtr)
    {
        graph = g;
        minTurnRadius = mtr;
        initMap();
    }

    public DijkstraWithTC(GridDirectedGraph g, double mtr, double maxTurnAngle)
    {
        graph = g;
        minTurnRadius = mtr;
        this.maxTurnAngle = maxTurnAngle;
        initMap();
    }

    private void initMap() {
        //To change body of created methods use File | Settings | File Templates.
        for (BaseVertex gv: graph.vertexSet())
        {
            dijkstraMap.put((GridVertex) gv, new DijkstraMapEntry());
        }

        dijkstraMap.get(graph.start).setDistance(0);

        heap.clear();

        for (BaseVertex v: graph.vertexSet())
        {
            GridVertex gv  = (GridVertex) v;
            DijkstraMapEntry entry = dijkstraMap.get(gv);
            entry.setHeapNode(new FibonacciHeapNode<DijkstraHeapNode>(new DijkstraHeapNode(gv,null,0,0)));
            heap.insert(entry.getHeapNode(),entry.getDistance());
        }

        straightPathLen = ArcMath.minTurnDistanceFromDegree(graph.degree, minTurnRadius);

    }


    public GraphPath<BaseVertex,BaseEdge> getShortestPath()
    {
        while(!heap.isEmpty())
        {
            DijkstraHeapNode currentNode = heap.removeMin().getData();
            GridVertex currentVertex = currentNode.getCurrentVertex();
            DijkstraMapEntry currentVertexEntry = dijkstraMap.get(currentVertex);

            if (currentVertexEntry.getDistance()>= Double.POSITIVE_INFINITY )
                break;

            for (BaseVertex v:currentVertex.getOutgoingNeighbors())
            {
                GridVertex nextVertex = (GridVertex)v;

                DijkstraHeapNode tempNode = new DijkstraHeapNode(nextVertex,null,0,0);

                if (minTurnRadius >0 && (! constraintsSatisfied(currentVertexEntry,currentNode,nextVertex,tempNode)))
                    continue; // CONSTRAINTS VIOLATED !!

                DijkstraMapEntry nextVertexEntry = dijkstraMap.get(nextVertex);
                double dist = currentVertexEntry.getDistance() + graph.getEdge(currentVertex,nextVertex).getEdgeWeight();

                if (dist < nextVertexEntry.getDistance())
                {
                    nextVertexEntry.setDistance(dist);
                    nextVertexEntry.setPrevious(currentVertex);
                    DijkstraHeapNode nextVertexNode = nextVertexEntry.getHeapNode().getData();
                    nextVertexNode.setLastTurnVertex(tempNode.getLastTurnVertex());
                    nextVertexNode.setLastTurnFootLength(tempNode.getLastTurnFootLength());
                    nextVertexNode.setLastTurnAngle(tempNode.getLastTurnAngle());
                    heap.decreaseKey(nextVertexEntry.getHeapNode(),dist);
                }
            }
        }


        GraphPath<BaseVertex,BaseEdge> path = createShortestPath(graph.start,graph.end);


        return path;
    }

    private GraphPath<BaseVertex, BaseEdge> createShortestPath(GridVertex start,GridVertex end) {
        List edgelist= new ArrayList<BaseEdge>();
        List reverseList= new ArrayList<BaseEdge>();
        double totalCost= 0;
        GridVertex current = end;
        while(current != null && current!=start)
        {
            DijkstraMapEntry currentEntry = dijkstraMap.get(current);
            GridVertex prev = currentEntry.getPrevious();
            BaseEdge e= graph.getEdge(prev,current);
            edgelist.add(e);
            totalCost += e.getEdgeWeight();
            current = prev;
        }

        for (int i = edgelist.size()-1; i>=0;i--)
        {
            reverseList.add(edgelist.get(i));
        }


        GraphPath<BaseVertex,BaseEdge> path = new GraphPathImpl<BaseVertex, BaseEdge>(graph,graph.start,end,reverseList,totalCost);
        return path;
    }

    private boolean constraintsSatisfied(DijkstraMapEntry currentEntry,DijkstraHeapNode currentNode, GridVertex nextVertex, DijkstraHeapNode nextNode ) {

        nextNode.setCurrentVertex(nextVertex);

        if (currentNode == null)
        {
            nextNode.setLastTurnVertex(null);
            nextNode.setLastTurnAngle(0);
            nextNode.setLastTurnFootLength(0);
            return true;
        }

        if (currentNode.getLastTurnVertex()==null)
        {
            nextNode.setLastTurnVertex(currentNode.getCurrentVertex());
            nextNode.setLastTurnAngle(0);
            nextNode.setLastTurnFootLength(0);
            return true;
        }

        GridVertex v = currentNode.getCurrentVertex();
        GridVertex ltv = currentNode.getLastTurnVertex();

        GridVertex prev = currentEntry.getPrevious()  ;


        double currentTurnAngle = ArcMath.turnAngle(ltv.pos.getX(),ltv.pos.getY(),
                v.pos.getX(),v.pos.getY(),
                nextVertex.pos.getX(),nextVertex.pos.getY());


        if ( MathUtil.equalDoubles(currentTurnAngle, 0))   // No turn
        {
            nextNode.setLastTurnVertex(currentNode.getLastTurnVertex());
            nextNode.setLastTurnAngle(currentNode.getLastTurnAngle());
            nextNode.setLastTurnFootLength(currentNode.getLastTurnFootLength());
            return true;
        }
        else if (maxTurnAngle>0 && Math.abs(currentTurnAngle) > maxTurnAngle)  // Maksimum donme açısı kontrolü yeni eklendi 08.12.2013
        {
            if (_debug)
                System.out.println("TC Violation.  LTV: "+ ltv.pos +" CV: "+ v.pos + "  NV: "+ nextVertex.pos);
            return false;
        }
        else if (currentNode.getLastTurnAngle() == 0 || !ArcMath.sameDirection(currentNode.getLastTurnAngle(),currentTurnAngle)) //Turn direction changed no constraints
        {
            double currentTurnFootLength = ArcMath.minTurnDistance(Math.abs(currentTurnAngle),minTurnRadius);
            if (ltv.pos.distance(v.pos)<currentTurnFootLength )  // TURN CONSTRAINT VIOLATION!!!
            {
                if (_debug)
                    System.out.println("TC Violation.  LTV: "+ ltv.pos +" CV: "+ v.pos + "  NV: "+ nextVertex.pos);
                return false;
            }
            else
            {
                nextNode.setLastTurnVertex(currentNode.getCurrentVertex());
                nextNode.setLastTurnAngle(currentTurnAngle);
                nextNode.setLastTurnFootLength(currentTurnFootLength);

                return true;
            }
        }
        else // Same turn direction . Has Turn constraint
        {

            double currentTurnFootLength = ArcMath.minTurnDistance(Math.abs(currentTurnAngle),minTurnRadius);
            if (ltv.pos.distance(v.pos)<currentNode.getLastTurnFootLength()+currentTurnFootLength)
            {
               if (_debug)
                   System.out.println("TC Violation.  LTV: "+ ltv.pos +" CV: "+ v.pos + "  NV: "+ nextVertex.pos);
               return false;
            }
            else
            {
                nextNode.setLastTurnVertex(currentNode.getCurrentVertex());
                nextNode.setLastTurnAngle(currentTurnAngle);
                nextNode.setLastTurnFootLength(currentTurnFootLength);
                return true;
            }
        }


    }
}
