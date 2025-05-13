package pgraph.anchorage.policy;

import math.geom2d.conic.Circle2D;
import org.jgrapht.GraphPath;
import pgraph.anchorage.AnchorArea;
import pgraph.anchorage.Anchorage;
import pgraph.anchorage.ArrivalInterface;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.util.Pen;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:04
 * To change this template use File | Settings | File Templates.
 */
public interface AnchorPolicy {

    public Anchorage createAnchorage(AnchorArea a, ArrivalInterface arrival);

    public GraphPath<BaseVertex,BaseEdge> getLastAnchoragePath();
}
