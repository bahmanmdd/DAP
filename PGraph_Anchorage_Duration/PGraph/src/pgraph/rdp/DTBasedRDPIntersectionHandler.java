package pgraph.rdp;

import pgraph.base.BaseEdge;
import pgraph.grid.GridDirectedGraph;

import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 28.01.2013
 * Time: 22:55
 *
 * DT Intersection handlers specifically defined for rdp problems. It takes the distance to target (DT)
 * into consideration while calculationg the penalty
 */
public class DTBasedRDPIntersectionHandler extends RDPIntersectionHandler{

    double disambiguationCost;

    public DTBasedRDPIntersectionHandler(double disambiguationCost) {
        this.disambiguationCost = disambiguationCost;
    }

    @Override
    double _getIntersectionPenalty(GridDirectedGraph g, BaseEdge e, RDPObstacleInterface ro, int intersectionCount) {
        Point2D.Double c = new Point2D.Double( (e.start.pos.getX()+ e.end.pos.getX())/2 , (e.start.pos.getY()+ e.end.pos.getY())/2);
        //Point2D.Double c = e.start.pos;
        double distanceToTarget = c.distance(g.end.pos);
        double p = ro.getP();
        double penalty =e.getLength()+ disambiguationCost + ( Math.pow( distanceToTarget /(1-p),(-1*Math.log(1-p))));
        return 0.5*penalty;
    }
}
