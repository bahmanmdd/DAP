package pgraph.rdp;

import org.jgrapht.GraphPath;
import pgraph.alg.NeutralizationAlgorithm;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 28.01.2013
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public class NeutroAlgPathFinder implements RDPPathFinder{
    NeutralizationAlgorithm neutroAlg =null;

    public NeutroAlgPathFinder(NeutralizationAlgorithm neutroAlg) {
        this.neutroAlg = neutroAlg;
    }

    @Override
    public GraphPath<BaseVertex, BaseEdge> findShortestPath(GridDirectedGraph g, int disambiguationCount) throws IOException, InstantiationException {
        return neutroAlg.perform(g, g.start,g.end,disambiguationCount);
    }
}
