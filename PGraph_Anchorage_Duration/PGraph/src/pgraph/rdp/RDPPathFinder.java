package pgraph.rdp;

import org.jgrapht.GraphPath;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 28.01.2013
 * Time: 23:09
 *
 * Generic path finder class runs in each recursive iteration of RDP calculations
 * Different strategies for finding new paths can be implemented by deriving new subclasses
 */
public interface RDPPathFinder {
   public GraphPath<BaseVertex,BaseEdge> findShortestPath(GridDirectedGraph g,int disambiguationCount) throws IOException, InstantiationException;
}
