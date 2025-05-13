package pgraph.anchorage.policy;

import org.jgrapht.GraphPath;
import pgraph.anchorage.AnchorArea;
import pgraph.anchorage.Anchorage;
import pgraph.anchorage.ArrivalInterface;
import pgraph.anchorage.DepthZone;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Userdouble: dindar
 * Date: 07.11.2013
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class HybridAnchoragePolicy_UniformAssumption implements AnchorPolicy {

    private static final double MAX_DEPTH_PENALTY = 100;
    private final double MIN_STACKING = -20;//-0.1;
    private final double MIN_SAFETY   = -5;

    private double depth = 0;

    private double W_HOLEDEGREE = 1;
    private double W_SAFETY = 0 ;
    private double W_DISTANCE = 100;
    private double W_STACKING = 0.0;
    private double W_DEPTHCONST = 0.0;
    private final double maximumDistance;
    private GraphPath<BaseVertex,BaseEdge> lastAnchoragePath = null;
    private GridDirectedGraph modelGraph;


    public HybridAnchoragePolicy_UniformAssumption(double maximumDistance,double depth) {
        this.maximumDistance = maximumDistance;
        this.depth = depth;
    }

    public HybridAnchoragePolicy_UniformAssumption(double w_HOLEDEGREE, double w_SAFETY, double w_DISTANCE, double w_STACKING, double maximumDistance) {
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


        List<Anchorage> candidates = a.getAllCandidateAnchorages_UniformAssumption(length,depth);

        if (candidates.size()==1)
            return candidates.get(0);

        if (candidates.isEmpty())
            candidates = a.getOtherCandidateAnchorages_UniformAssumption(length,1,depth);
        if (candidates.isEmpty())
            candidates = a.getOtherCandidateAnchorages_UniformAssumption(length,2,depth);

        if (candidates.isEmpty())
        {
            return bestPlace;
        }

        bestPlace = chooseBest(a,candidates);

        return bestPlace;

    }

    private Anchorage chooseBest(AnchorArea a, List<Anchorage> candidates) {
        Anchorage bestPlace = null;
        GraphPath<BaseVertex,BaseEdge> path= null;
        double score =0;
        double bestScore = -1 * Double.MAX_VALUE;

        for (Anchorage candidate: candidates)
        {
            double hd_score = (W_HOLEDEGREE==0)? 0: calculateHDScore(candidate,a);
            double safety_score = (W_SAFETY==0)? 0:calculateSafetyScore(candidate,a);
            double wd_score     = (W_STACKING==0)? 0:calculateWDScore(candidate,a,candidates);
            double distance_score = (W_DISTANCE==0)? 0: calculateDistanceScore(candidate, a);
            double depthConst_score = (W_DEPTHCONST==0 )? 0: calculateDepthConstraintScore(candidate,a);

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
                    W_STACKING * wd_score+
                    W_DEPTHCONST * depthConst_score;

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

    private double calculateDepthConstraintScore(Anchorage candidate, AnchorArea a) {

        DepthZone dz = a.depthZone(candidate);
        if (dz== null)
            return -1*MAX_DEPTH_PENALTY;



        return -1* Math.abs(candidate.getInnerRadius() - dz.getMaximumLength());  //To change body of created methods use File | Settings | File Templates.
    }

    private double calculateDistanceScore(Anchorage candidate, AnchorArea a)
    {
        double distance = a.getEntrySide().ptLineDist(candidate.getArea().center().getAsDouble());
        double distanceScore = distance/ maximumDistance;

        return distanceScore;
    }

    private double calculateMeanDistance(Line2D entrySide , List<Anchorage> candidates)
    {
        if (candidates.isEmpty()|| entrySide == null)
            return 0;
        double mean = 0;

        for (Anchorage c: candidates)
        {
            mean += entrySide.ptLineDist(c.getArea().center().getAsDouble())-c.getArea().radius();
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
        double distance = a.getEntrySide().ptLineDist(c.getArea().center().getAsDouble())-c.getArea().radius(); //c.distance(a.getEntryPoint().getX(),a.getEntryPoint().getY());
        double meanDistance = calculateMeanDistance(a.getEntrySide(),candidates);
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
