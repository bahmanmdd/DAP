package pgraph.alg;

import org.jgrapht.GraphPath;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 12/14/12
 * Time: 2:41 PM
 *
 * Generic interface for neutralization algorithms.
 * NA's simply runs on BaseDirectedGraphs and takes staat and end vertex as input parameters. finds a path ,using at most K neutralization
 */
public interface NeutralizationAlgorithm {

    GraphPath<BaseVertex,BaseEdge> perform(BaseDirectedGraph g,BaseVertex s,BaseVertex e,int K) throws InstantiationException, IOException;
}
