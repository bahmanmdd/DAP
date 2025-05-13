package pgraph.alg.dijkstra;

import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;
import pgraph.grid.GridVertex;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 25.07.2013
 * Time: 20:18
 * To change this template use File | Settings | File Templates.
 */
public class DijkstraMapEntry {
    private double distance = Double.POSITIVE_INFINITY;
    private GridVertex previous= null;
    private FibonacciHeapNode<DijkstraHeapNode> heapNode=null;

    public FibonacciHeapNode<DijkstraHeapNode> getHeapNode() {
        return heapNode;
    }

    public void setHeapNode(FibonacciHeapNode<DijkstraHeapNode> heapNode) {
        this.heapNode = heapNode;
    }

    public DijkstraMapEntry() {
    }

    public DijkstraMapEntry(double distance, GridVertex previous) {
        this.distance = distance;
        this.previous = previous;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public GridVertex getPrevious() {
        return previous;
    }

    public void setPrevious(GridVertex previous) {
        this.previous = previous;
    }
}
