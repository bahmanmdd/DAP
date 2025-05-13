package pgraph.util;

import org.jgrapht.GraphPath;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 04.05.2013
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class GraphUtil {

    public static double recalculatePathWeight(BaseDirectedGraph g , GraphPath<BaseVertex,BaseEdge> path)
    {
        double weight = 0;
        for (BaseEdge e : path.getEdgeList())
        {
            weight += e.getEdgeWeight();
        }
        return weight;
    }
}
