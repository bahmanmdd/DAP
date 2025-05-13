package pgraph.anchorage.policy;

import org.jgrapht.GraphPath;
import pgraph.anchorage.AnchorArea;
import pgraph.anchorage.Anchorage;
import pgraph.anchorage.ArrivalInterface;
import pgraph.anchorage.policy.AnchorPolicy;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Userdouble: dindar
 * Date: 07.11.2013
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class HybridAnchoragePolicy implements AnchorPolicy {

    private final double MIN_STACKING = -20;//-0.1;
    private final double MIN_SAFETY   = -5;

    private double W_HOLEDEGREE = 3;
    private double W_SAFETY = 1 ;
    private double W_DISTANCE = 10;
    private double W_STACKING = 0.0;
    private final double maximumDistance;
    private GraphPath<BaseVertex,BaseEdge> lastAnchoragePath = null;
    private GridDirectedGraph modelGraph;


    public HybridAnchoragePolicy(double maximumDistance) {
        this.maximumDistance = maximumDistance;
    }

    public HybridAnchoragePolicy(double w_HOLEDEGREE, double w_SAFETY, double w_DISTANCE, double w_STACKING, double maximumDistance) {
        W_HOLEDEGREE = w_HOLEDEGREE;
        W_SAFETY = w_SAFETY;
        W_DISTANCE = w_DISTANCE;
        W_STACKING = w_STACKING;
        this.maximumDistance = maximumDistance;
    }

    @Override
    public Anchorage createAnchorage(AnchorArea a, ArrivalInterface arrival) {

        double length = arrival.getLength();
        Anchorage bestPlace = null;
        GraphPath<BaseVertex,BaseEdge> path= null;


        List<Anchorage> candidates = a.getAllCandidateAnchorages(length);

        double score =0;
        double bestScore = -1 * Double.MAX_VALUE;

        for (Anchorage candidate: candidates)
        {
            double hd_score = calculateHDScore(candidate,a);
            double safety_score =(W_SAFETY==0)? 0:calculateSafetyScore(candidate,a);
            double wd_score =  calculateWDScore(candidate,a,candidates);
            double distance_score = calculateDistanceScore(candidate, a);;

            if (safety_score<MIN_SAFETY)
            {
                //System.out.println("INSECURE PATH REJECTED!!");
                continue;
            }

            if (wd_score<MIN_STACKING)
            {
                //System.out.println("STACKING PATH REJECTED!!");
                continue;
            }


            score = W_HOLEDEGREE * hd_score +
                    W_SAFETY * safety_score +
                    W_DISTANCE* distance_score +
                    W_STACKING * wd_score;

            if (score> bestScore)
            {
                bestPlace = candidate;
                path = lastAnchoragePath;
                bestScore = score;
            }
        }

         lastAnchoragePath = path;

        return bestPlace;

    }

    private double calculateDistanceScore(Anchorage candidate, AnchorArea a)
    {
        Point2D entryPoint = a.calculateEntryPoint(candidate.getArea().center().getAsDouble());
        double distance = candidate.getArea().center().distance(entryPoint.getX(),entryPoint.getY());
        double distanceScore = distance/ maximumDistance;

        return distanceScore;
    }

    private double calculateMeanDistance(Point2D entryPoint , List<Anchorage> candidates)
    {
        if (candidates.isEmpty()|| entryPoint == null)
            return 0;
        double mean = 0;

        for (Anchorage c: candidates)
        {
            mean += c.getArea().center().distance(math.geom2d.Point2D.create(entryPoint));
        }
        return mean/candidates.size();

    }


    double calculateHDScore( Anchorage c, AnchorArea a)
    {
        return a.getHoleDegree(c.getArea());
    }

    double calculateSafetyScore( Anchorage c, AnchorArea a)
    {
        lastAnchoragePath  = a.getAnchoragePath(c.getArea(),true);

        int intersectionCount = a.getTotalIntersectionCount(lastAnchoragePath);

        return -1 * intersectionCount;

    }

    double calculateWDScore( Anchorage c, AnchorArea a, List<Anchorage> candidates)
    {
        /* todo:  meandistance ta entry side kullanılacak */
        Point2D entryPoint = a.calculateEntryPoint(c.getArea().center().getAsDouble());
        double distance = c.getArea().center().distance(entryPoint.getX(),entryPoint.getY());
        double meanDistance = calculateMeanDistance(entryPoint,candidates);
        double stackingDegree = (meanDistance-distance)/ maximumDistance;

        return -1 * stackingDegree;

    }


    private void createModelGraph( AnchorArea a) {

        double minX = a.boundingRect().getMinX();
        double maxX = a.boundingRect().getMaxX();
        double minY = a.boundingRect().getMinY();
        double maxY = a.boundingRect().getMaxY();

        modelGraph=  new GridDirectedGraph(3,(int)(maxX-minX),(int)(maxY-minY),1,new Point2D.Double(minX,minY),0,0,0,0 );

    }

    @Override
    public GraphPath<BaseVertex, BaseEdge> getLastAnchoragePath() {
        return lastAnchoragePath;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
