package pgraph.intersectionhandler;

import pgraph.ObstacleInterface;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 28.01.2013
 * Time: 22:28
 *
 * The class for handling intersections between edges and obstacles
 * Different obstacle penalty strategies can be implemented by deriving new classes from ths base class
 */
public interface IntersectionHandler {
    public double getIntersectionPenalty(BaseDirectedGraph g ,BaseEdge e , ObstacleInterface o, int intersectionCount);
}
