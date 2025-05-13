package pgraph.rdp;

import pgraph.ObstacleInterface;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.intersectionhandler.IntersectionHandler;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 28.01.2013
 * Time: 22:30
 *
 * Intersection handlers specifically defined for rdp problems
 */
public class BAOInformationStateIntersectionHandler implements IntersectionHandler {


    @Override
    public double getIntersectionPenalty(BaseDirectedGraph g, BaseEdge e, ObstacleInterface o, int intersectionCount) {
        double penalty = o.isPassable() ? 0 : Double.MAX_VALUE ;
        return penalty;
    }

}
