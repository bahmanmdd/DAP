/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* -------------------------
 * ClosestFirstIterator.java
 * -------------------------
 * (C) Copyright 2003-2008, by John V. Sichi and Contributors.
 *
 * Original Author:  John V. Sichi
 * Contributor(s):   Barak Naveh
 *
 * $Id: ClosestFirstIterator.java 697 2009-09-08 22:21:13Z perfecthash $
 *
 * Changes
 * -------
 * 02-Sep-2003 : Initial revision (JVS);
 * 31-Jan-2004 : Reparented and changed interface to parent class (BN);
 * 29-May-2005 : Added radius support (JVS);
 * 06-Jun-2005 : Made generic (CH);
 *
 */
package org.jgrapht.traverse;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import pgraph.grid.GridVertex;


/**
 * A closest-first iterator for a directed or undirected graph. For this
 * iterator to work correctly the graph must not be modified during iteration.
 * Currently there are no means to ensure that, nor to fail-fast. The results of
 * such modifications are undefined.
 *
 * <p>The metric for <i>closest</i> here is the path length from a start vertex.
 * Graph.getEdgeWeight(Edge) is summed to calculate path length. Negative edge
 * weights will result in an IllegalArgumentException. Optionally, path length
 * may be bounded by a finite radius.</p>
 *
 * @author John V. Sichi
 * @since Sep 2, 2003
 */
public class HeuristicallyClosestFirstIterator<V, E>
    extends CrossComponentIterator<V,
        E, FibonacciHeapNode<HeuristicallyClosestFirstIterator.HQueueEntry<V, E>>>
{
    //~ Instance fields --------------------------------------------------------

    /**
     * Priority queue of fringe vertices.
     */
    private FibonacciHeap<HQueueEntry<V, E>> heap =
        new FibonacciHeap<HQueueEntry<V, E>>();

    /**
     * Maximum distance to search.
     */
    private double radius = Double.POSITIVE_INFINITY;

    private boolean initialized = false;

    private Heuristic<V> heuristic= null;


    private final boolean verbose;

    public long generatedNodes = 0; // added by ANYA team
    public long expandedNodes=0; // added by ANYA team

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new closest-first iterator for the specified graph.
     *
     * @param g the graph to be iterated.
     */
    public HeuristicallyClosestFirstIterator(Graph<V, E> g,Heuristic<V> h)
    {
        this(g, null,h);
    }

    /**
     * Creates a new closest-first iterator for the specified graph. Iteration
     * will start at the specified start vertex and will be limited to the
     * connected component that includes that vertex. If the specified start
     * vertex is <code>null</code>, iteration will start at an arbitrary vertex
     * and will not be limited, that is, will be able to traverse all the graph.
     *
     * @param g the graph to be iterated.
     * @param startVertex the vertex iteration to be started.
     */
    public HeuristicallyClosestFirstIterator(Graph<V, E> g, V startVertex, Heuristic<V> h)
    {
        this(g, startVertex, Double.POSITIVE_INFINITY,h);
    }

    /**
     * Creates a new radius-bounded closest-first iterator for the specified
     * graph. Iteration will start at the specified start vertex and will be
     * limited to the subset of the connected component which includes that
     * vertex and is reachable via paths of length less than or equal to the
     * specified radius. The specified start vertex may not be <code>
     * null</code>.
     *
     * @param g the graph to be iterated.
     * @param startVertex the vertex iteration to be started.
     * @param radius limit on path length, or Double.POSITIVE_INFINITY for
     * unbounded search.
     */
    public HeuristicallyClosestFirstIterator(Graph<V, E> g, V startVertex, double radius,Heuristic<V> h)
    {
    	this(g, startVertex, radius, h, false);
    }
    
    public HeuristicallyClosestFirstIterator(Graph<V, E> g, V startVertex, double radius,Heuristic<V> h, boolean verbose)
    {
        super(g, startVertex);
        this.verbose = verbose;
        this.radius = radius;
        heuristic = h;
        checkRadiusTraversal(isCrossComponentTraversal());
        initialized = true;

        if(verbose)
        {
            System.out.println("**** new instance ****");
        }
    }
    

    //~ Methods ----------------------------------------------------------------

    // override AbstractGraphIterator
    public void setCrossComponentTraversal(boolean crossComponentTraversal)
    {
        if (initialized) {
            checkRadiusTraversal(crossComponentTraversal);
        }
        super.setCrossComponentTraversal(crossComponentTraversal);
    }

    /**
     * Get the length of the shortest path known to the given vertex. If the
     * vertex has already been visited, then it is truly the shortest path
     * length; otherwise, it is the best known upper bound.
     *
     * @param vertex vertex being sought from start vertex
     *
     * @return length of shortest path known, or Double.POSITIVE_INFINITY if no
     * path found yet
     */
    public double getShortestPathLength(V vertex)
    {
        FibonacciHeapNode<HQueueEntry<V, E>> node = getSeenData(vertex);

        if (node == null) {
            return Double.POSITIVE_INFINITY;
        }

        return node.getData().calculatedDistanceSoFar;
    }

    /**
     * Get the spanning tree edge reaching a vertex which has been seen already
     * in this traversal. This edge is the last link in the shortest known path
     * between the start vertex and the requested vertex. If the vertex has
     * already been visited, then it is truly the minimum spanning tree edge;
     * otherwise, it is the best candidate seen so far.
     *
     * @param vertex the spanned vertex.
     *
     * @return the spanning tree edge, or null if the vertex either has not been
     * seen yet or is the start vertex.
     */
    public E getSpanningTreeEdge(V vertex)
    {
        FibonacciHeapNode<HQueueEntry<V, E>> node = getSeenData(vertex);

        if (node == null) {
            return null;
        }

        return node.getData().spanningTreeEdge;
    }

    /**
     * @see org.jgrapht.traverse.CrossComponentIterator#isConnectedComponentExhausted()
     */
    protected boolean isConnectedComponentExhausted()
    {
        if (heap.size() == 0) {
            return true;
        } else {
            if (heap.min().getKey() > radius) {
                heap.clear();

                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * @see org.jgrapht.traverse.CrossComponentIterator#encounterVertex(Object, Object)
     */
    protected void encounterVertex(V vertex, E edge)
    {
        double shortestPathLength;
        if (edge == null) {
            shortestPathLength = 0.0;
        } else {
            shortestPathLength = calculatePathLength(vertex, edge);
        }

        double distanceWithHeuristic = shortestPathLength + heuristic.getValue(vertex);

        FibonacciHeapNode<HQueueEntry<V, E>> node = createSeenData(vertex, edge,shortestPathLength);
        putSeenData(vertex, node);

        generatedNodes++; // added by ANYA team
        heap.insert(node, distanceWithHeuristic, shortestPathLength);

        if(verbose)
        {
            GridVertex v = (GridVertex)vertex;
            System.out.println("\tgenerating " + v.gridPos.getX() + ", "+v.gridPos.getY() + 
                    "; g: "+shortestPathLength + " f: "+distanceWithHeuristic);
        }
    }

    /**
     * Override superclass. When we see a vertex again, we need to see if the
     * new edge provides a shorter path than the old edge.
     *
     * @param vertex the vertex re-encountered
     * @param edge the edge via which the vertex was re-encountered
     */
    protected void encounterVertexAgain(V vertex, E edge)
    {
        FibonacciHeapNode<HQueueEntry<V, E>> node = getSeenData(vertex);

        if (node.getData().frozen) {
            // no improvement for this vertex possible
            return;
        }

        double candidatePathLength = calculatePathLength(vertex, edge);

        if (candidatePathLength < node.getData().calculatedDistanceSoFar) {
            node.getData().spanningTreeEdge = edge;
            node.getData().calculatedDistanceSoFar = candidatePathLength;
            heap.decreaseKey(node, candidatePathLength + heuristic.getValue(vertex), 
            		candidatePathLength);

            if(verbose)
            {
                GridVertex v = (GridVertex)vertex;
                System.out.println("\tupdating " + v.gridPos.getX() + ", "+v.gridPos.getY() + 
                        "; newg: "+candidatePathLength + " f: "+
                        (candidatePathLength + heuristic.getValue(vertex)));
            }
        }
        else
        {
            if(verbose)
            {
                GridVertex v = (GridVertex)vertex;
                System.out.println("\tnot updating " + v.gridPos.getX() +
                        ", "+v.gridPos.getY());
            }
        }        
    }

    /**
     * @see org.jgrapht.traverse.CrossComponentIterator#provideNextVertex()
     */
    protected V provideNextVertex()
    {
        FibonacciHeapNode<HQueueEntry<V, E>> node = heap.removeMin();
        node.getData().frozen = true;

        expandedNodes++; // added by ANYA team

        if(verbose)
        {
            GridVertex v = (GridVertex)node.getData().vertex;
            System.out.println("expanding " + v.gridPos.getX() + ", "+v.gridPos.getY() 
                    + " g: "+node.getData().calculatedDistanceSoFar
                    + " f: "+(node.getData().calculatedDistanceSoFar + heuristic.getValue(node.getData().vertex)));
        }

        return node.getData().vertex;
    }

    private void assertNonNegativeEdge(E edge)
    {
        if (getGraph().getEdgeWeight(edge) < 0) {
            throw new IllegalArgumentException(
                "negative edge weights not allowed");
        }
    }

    /**
     * Determine path length to a vertex via an edge, using the path length for
     * the opposite vertex.
     *
     * @param vertex the vertex for which to calculate the path length.
     * @param edge the edge via which the path is being extended.
     *
     * @return calculated path length.
     */
    private double calculatePathLength(V vertex, E edge)
    {
        assertNonNegativeEdge(edge);

        V otherVertex = Graphs.getOppositeVertex(getGraph(), edge, vertex);
        FibonacciHeapNode<HQueueEntry<V, E>> otherEntry =
            getSeenData(otherVertex);

        return otherEntry.getData().calculatedDistanceSoFar
            + getGraph().getEdgeWeight(edge);
    }

    private void checkRadiusTraversal(boolean crossComponentTraversal)
    {
        if (crossComponentTraversal && (radius != Double.POSITIVE_INFINITY)) {
            throw new IllegalArgumentException(
                "radius may not be specified for cross-component traversal");
        }
    }

    /**
     * The first time we see a vertex, make up a new heap node for it.
     *
     * @param vertex a vertex which has just been encountered.
     * @param edge the edge via which the vertex was encountered.
     *
     * @return the new heap node.
     */
    private FibonacciHeapNode<HQueueEntry<V, E>> createSeenData(
        V vertex,
        E edge,
        double d)
    {
        HQueueEntry<V, E> entry = new HQueueEntry<V, E>();
        entry.vertex = vertex;
        entry.spanningTreeEdge = edge;
        entry.calculatedDistanceSoFar = d;
        entry.frozen = false;

        return new FibonacciHeapNode<HQueueEntry<V, E>>(entry);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Private data to associate with each entry in the priority queue.
     */
    static class HQueueEntry<V, E>
    {
        /**
         * Best spanning tree edge to vertex seen so far.
         */
        E spanningTreeEdge;

        /**
         * The vertex reached.
         */
        V vertex;

        double calculatedDistanceSoFar= 0.0;

        /**
         * True once spanningTreeEdge is guaranteed to be the true minimum.
         */
        boolean frozen = false;

        HQueueEntry()
        {
        }
    }
}

// End ClosestFirstIterator.java
