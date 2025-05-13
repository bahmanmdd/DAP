package pgraph.anchorage.policy;

import org.jgrapht.GraphPath;
import pgraph.anchorage.AnchorArea;
import pgraph.anchorage.Anchorage;
import pgraph.anchorage.ArrivalInterface;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.util.RandUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class RandomAnchoragePolicy implements AnchorPolicy {
    @Override
    public Anchorage createAnchorage(AnchorArea a, ArrivalInterface arrival) {

        double length = arrival.getLength();
        List<Anchorage> candidates = a.getAllCandidateAnchorages(length);

        if (candidates.isEmpty())
            return null;

        int anc = RandUtil.instance.nextInt(candidates.size());


        Anchorage bestPlace = candidates.get(anc);

        return bestPlace;

    }

    @Override
    public GraphPath<BaseVertex, BaseEdge> getLastAnchoragePath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}


