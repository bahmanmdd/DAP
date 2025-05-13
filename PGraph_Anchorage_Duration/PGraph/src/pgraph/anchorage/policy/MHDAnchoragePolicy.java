package pgraph.anchorage.policy;

import org.jgrapht.GraphPath;
import pgraph.anchorage.AnchorArea;
import pgraph.anchorage.Anchorage;
import pgraph.anchorage.ArrivalInterface;
import pgraph.anchorage.policy.AnchorPolicy;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class MHDAnchoragePolicy implements AnchorPolicy {
    @Override
    public Anchorage createAnchorage(AnchorArea a, ArrivalInterface arrival) {

        double length = arrival.getLength();
        List<Anchorage> candidates = a.getAllCandidateAnchorages(length);
        Anchorage bestPlace = a.getMHDCircle(candidates);

        return bestPlace;

    }

    @Override
    public GraphPath<BaseVertex, BaseEdge> getLastAnchoragePath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}


