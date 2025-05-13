package pgraph.anchorage.policy;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import pgraph.CircleListObstacleGenerator;
import pgraph.ObstacleGenerator;
import pgraph.anchorage.AnchorArea;
import pgraph.anchorage.Anchorage;
import pgraph.anchorage.ArrivalInterface;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridVertex;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class AMHDAnchoragePolicy implements AnchorPolicy {
    private GraphPath<BaseVertex,BaseEdge> lastAnchoragePath = null;

    @Override
    public Anchorage createAnchorage(AnchorArea a, ArrivalInterface arrival) {

        double length = arrival.getLength();

        double minX = a.boundingRect().getMinX();
        double maxX = a.boundingRect().getMaxX();
        double minY = a.boundingRect().getMinY();
        double maxY = a.boundingRect().getMaxY();

        GridDirectedGraph gg = new GridDirectedGraph(3,(int)(maxX-minX),(int)(maxY-minY),1,new Point2D.Double(minX,minY),0,0,0,0 );

        ObstacleGenerator og = new CircleListObstacleGenerator(a.getExistingAnchoragesAsCircles(),10000,3);

        try {
            gg.addObstacles(og.generate());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        List<Anchorage> candidates = a.getAllCandidateAnchorages(length);
        Anchorage bestPlace = a.getMHDCircle(candidates);

        if (bestPlace == null)
            return null;


        boolean found = false;
        GraphPath<BaseVertex,BaseEdge> path = null;

        while (!found)
        {
            Point2D entryPoint = a.calculateEntryPoint(bestPlace.getArea().center().getAsDouble());
            GridVertex s = gg.getClosestVertex(entryPoint);
            GridVertex t = gg.getClosestVertex(bestPlace.getArea().center().getAsDouble());

            DijkstraShortestPath<BaseVertex,BaseEdge> spalg = new DijkstraShortestPath<BaseVertex, BaseEdge>(gg,s,t);

            path = spalg.getPath();

            if (path!= null && path.getWeight()<10000)
            {
                found = true;

                break;
            }   else
            {
                candidates.remove(bestPlace);
                if (candidates.isEmpty())
                    return null;

                bestPlace = a.getMHDCircle(candidates);
            }
        }


         lastAnchoragePath = path;

        return bestPlace;

    }

    @Override
    public GraphPath<BaseVertex, BaseEdge> getLastAnchoragePath() {
        return lastAnchoragePath;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
