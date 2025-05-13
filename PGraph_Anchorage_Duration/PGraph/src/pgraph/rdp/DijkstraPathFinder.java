package pgraph.rdp;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.DijkstraWithHeuristicShortestPath;
import org.jgrapht.traverse.MHDHeuristic;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 28.01.2013
 * Time: 23:15
 * To change this template use File | Settings | File Templates.
 */
public class DijkstraPathFinder implements RDPPathFinder {
    @Override
    public GraphPath<BaseVertex, BaseEdge> findShortestPath(GridDirectedGraph g, int disambiguationCount) throws IOException, InstantiationException {

        //DijkstraShortestPath<BaseVertex,BaseEdge> spAlg = new DijkstraShortestPath<BaseVertex, BaseEdge>(g,g.start,g.end);

        // Use A*:
        DijkstraWithHeuristicShortestPath<BaseVertex,BaseEdge> spAlg = new DijkstraWithHeuristicShortestPath<BaseVertex, BaseEdge>(g,g.start,g.end,new MHDHeuristic<BaseVertex>(g.end));


        GraphPath<BaseVertex,BaseEdge> gp = spAlg.getPath();

        return gp;
    }
}
