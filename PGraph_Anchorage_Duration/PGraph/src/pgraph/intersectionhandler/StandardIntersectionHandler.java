package pgraph.intersectionhandler;

import pgraph.ObstacleInterface;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 28.01.2013
 * Time: 22:33
 *
 * Standart handler just giving the penalty of 0.5 * Obstacle Weight * Intersection Count
 */
public class StandardIntersectionHandler implements IntersectionHandler {
    @Override
    public double getIntersectionPenalty(BaseDirectedGraph g, BaseEdge e, ObstacleInterface o, int intersectionCount) {
        return 0.5*intersectionCount*o.getObstacleWeight();
    }
}
