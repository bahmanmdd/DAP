package pgraph.anchorage.policy;

import org.jgrapht.GraphPath;
import pgraph.anchorage.*;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:04
 * To change this template use File | Settings | File Templates.
 */
public interface TimedAnchorPolicy {

    public Anchorage createAnchorage(TimedAnchorArea a, TimedArrivalInterface arrival);
    public Anchorage createRandomAnchorage(TimedAnchorArea a, TimedArrivalInterface arrival);


    public GraphPath<BaseVertex,BaseEdge> getLastAnchoragePath();
}
