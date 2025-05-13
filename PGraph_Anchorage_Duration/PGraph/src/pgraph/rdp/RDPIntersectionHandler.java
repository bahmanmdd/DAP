package pgraph.rdp;

import pgraph.ObstacleInterface;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.grid.GridDirectedGraph;
import pgraph.intersectionhandler.IntersectionHandler;
import pgraph.rdp.RDPObstacleInterface;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 28.01.2013
 * Time: 22:30
 *
 * Intersection handlers specifically defined for rdp problems
 */
public abstract class RDPIntersectionHandler implements IntersectionHandler {

    boolean zeroRiskState= false;

    abstract double _getIntersectionPenalty(GridDirectedGraph g, BaseEdge e, RDPObstacleInterface ro, int intersectionCount);

    @Override
    public double getIntersectionPenalty(BaseDirectedGraph g, BaseEdge e, ObstacleInterface o, int intersectionCount) {
        double penalty = zeroRiskState ? Double.MAX_VALUE:_getIntersectionPenalty((GridDirectedGraph)g,e,(RDPObstacleInterface)o,intersectionCount);
        return penalty;
    }

    public void setZeroRiskState(boolean state) {
        zeroRiskState = state;
    }
}
