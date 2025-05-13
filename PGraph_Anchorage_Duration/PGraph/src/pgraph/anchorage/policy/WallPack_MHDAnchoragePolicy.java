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
public class WallPack_MHDAnchoragePolicy implements AnchorPolicy {
    @Override
    public Anchorage createAnchorage(AnchorArea a, ArrivalInterface arrival) {

        double length = arrival.getLength();

        List<Anchorage> candidates = a.calculateCandidateAnchorages(length,AnchorArea.AT_SIDE_SIDE);
        if (candidates.isEmpty())
            candidates = a.calculateCandidateAnchorages(length,AnchorArea.AT_SIDE_CIRCLE);
        if (candidates.isEmpty())
            candidates = a.calculateCandidateAnchorages(length,AnchorArea.AT_SIDE_DZSIDE);
        if (candidates.isEmpty())
            candidates = a.calculateCandidateAnchorages(length,AnchorArea.AT_CIRCLE_CIRCLE|AnchorArea.AT_CIRCLE_DZSIDE|AnchorArea.AT_DZSIDE_DZSIDE);

        Anchorage bestPlace = a.getMHDCircle(candidates);

        return bestPlace;

    }

    @Override
    public GraphPath<BaseVertex, BaseEdge> getLastAnchoragePath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
