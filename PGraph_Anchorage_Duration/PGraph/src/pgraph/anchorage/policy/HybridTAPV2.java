package pgraph.anchorage.policy;

import math.geom2d.conic.Circle2D;
import org.jgrapht.GraphPath;
import pgraph.anchorage.*;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
//import pgraph.grid.GridDirectedGraph;

import java.awt.geom.Line2D;
//import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Userdouble: dindar
 * Date: 07.11.2013
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class HybridTAPV2 implements TimedAnchorPolicy {

    private static final double MAX_DEPTH_PENALTY = 100;
    private final double MIN_STACKING = -20;//-0.1;
    private final double MIN_SAFETY   = -5;


    private boolean SPSA = false;

    private boolean OPTIMAL = false;

    double[] optimalTheta= new double[] { 2.4000,-1.0000,-16.0000,5.8000,12.6000,1.0000,-4.4000

} ;

    private double W_AIL = 0;
    private double W_DISTANCE = 0;
    private double W_MFACTOR = 0;

    private double W_HOLEDEGREE = 0;
    private double W_SAFETY = 0 ;
    private double W_STACKING = 0.0;
    private double W_DEPTHCONST = 0.0;


    public final double maximumDistance;
    private GraphPath<BaseVertex,BaseEdge> lastAnchoragePath = null;
//    private GridDirectedGraph modelGraph;

    public double[] getOptimalTheta(){
        return optimalTheta;
    }


    public HybridTAPV2(double maximumDistance) {
        this.maximumDistance = maximumDistance;
    }

    public HybridTAPV2(double w_AIL, double w_HOLEDEGREE, double w_SAFETY, double w_DISTANCE, double w_STACKING, double maximumDistance) {
        W_AIL = w_AIL;
        W_HOLEDEGREE = w_HOLEDEGREE;
        W_SAFETY = w_SAFETY;
        W_DISTANCE = w_DISTANCE;
        W_STACKING = w_STACKING;
        this.maximumDistance = maximumDistance;
    }

    @Override
    public Anchorage createAnchorage(TimedAnchorArea a, TimedArrivalInterface arrival) {

        double length = arrival.getLength();
        Anchorage bestPlace = null;
        //       GraphPath<BaseVertex, BaseEdge> path = null;


        List<Anchorage> candidates = a.getAllCandidateAnchorages(length);

        if (candidates.isEmpty())
            candidates = a.getOtherCandidateAnchorages(length, 1);
        if (candidates.isEmpty())
            candidates = a.getOtherCandidateAnchorages(length, 2);


        if (candidates.isEmpty()) {
            return bestPlace;
        }

        if (candidates.size() == 1)
            return candidates.get(0);


        if (SPSA) {
            bestPlace = chooseBest(a, candidates);

        } else {
            if (OPTIMAL) {
                bestPlace = optimalChooseBest(a, candidates);

            } else {
                bestPlace = oldChooseBest(a, candidates);
            }
        }
        //bestPlace = optimalChooseBest(a, candidates);
        //bestPlace = chooseBest(a,candidates);
        //bestPlace = oldChooseBest(a,candidates);

        return bestPlace;
    }

    public Anchorage createRandomAnchorage(TimedAnchorArea a, TimedArrivalInterface arrival) {

        double length = arrival.getLength();
        Anchorage bestPlace = null;

        List<Anchorage> candidates = a.getAllCandidateAnchorages(length);

        if (candidates.isEmpty())
            candidates = a.getOtherCandidateAnchorages(length, 2);

        if (candidates.isEmpty()) {
            return bestPlace;
        }
        return candidates.get(0);
    }

    private Anchorage chooseBest(TimedAnchorArea a, List<Anchorage> candidates) {
        Anchorage bestPlace = null;
        //      GraphPath<BaseVertex,BaseEdge> path= null;
        double score ;
        double bestScore = -1 * Double.MAX_VALUE;
        int rCount = a.getRunCount();

        for (Anchorage candidate: candidates)
        {
            double ail_score          = calculateAILScore(candidate, a);
            double currentDIL_score   = calculateCurrentDILscore(candidate, a);
            double distance_score     = calculateDistanceScore(candidate, a);
            double hd_score           = calculateHDScore(candidate, a);
            double mfactor_score      = calculateMfactorScore(candidate, a);




//            double safety_score       = (W_SAFETY==0)      ? 0: calculateSafetyScore(candidate, a);
//            double wd_score           = (W_STACKING==0)    ? 0: calculateWDScore(candidate,a,candidates);

//            if (safety_score<MIN_SAFETY) {continue;}//System.out.println("INSECURE PATH REJECTED!!");

//            if (wd_score<MIN_STACKING) { continue;} //System.out.println("STACKING PATH REJECTED!!");

            score = a.getCurrentQvector()[0] * ail_score +
                    a.getCurrentQvector()[1] * currentDIL_score +
                    a.getCurrentQvector()[2] * distance_score +

                    a.getCurrentQvector()[3]  * ail_score * mfactor_score +
                    a.getCurrentQvector()[4]  * currentDIL_score  * mfactor_score +
                    a.getCurrentQvector()[5]  * distance_score * mfactor_score +
                    a.getCurrentQvector()[6]  * ail_score * currentDIL_score * mfactor_score ;


  /*
            score = a.getCurrentQvector()[0] * ail_score +
                    a.getCurrentQvector()[1] * currentDIL_score +
                    a.getCurrentQvector()[2] * distance_score +

                    a.getCurrentQvector()[3]  * ail_score * distance_score +
                    a.getCurrentQvector()[4]  * ail_score * currentDIL_score  +
                    a.getCurrentQvector()[5]  * ail_score * mfactor_score +
                    a.getCurrentQvector()[6]  * currentDIL_score  * mfactor_score;



            score = a.getCurrentQvector()[0] * ail_score +
                    a.getCurrentQvector()[1] * currentDIL_score +
                    a.getCurrentQvector()[2] * distance_score +
                    a.getCurrentQvector()[3] * hd_score +

                    a.getCurrentQvector()[4]  * ail_score * currentDIL_score  +
                    a.getCurrentQvector()[5]  * ail_score * distance_score +
                    a.getCurrentQvector()[6]  * ail_score * hd_score +
                    a.getCurrentQvector()[7]  * ail_score * mfactor_score +
                    a.getCurrentQvector()[8]  * currentDIL_score  * distance_score +
                    a.getCurrentQvector()[9]  * currentDIL_score  * hd_score +
                    a.getCurrentQvector()[10] * currentDIL_score  * mfactor_score +
                    a.getCurrentQvector()[11] * distance_score * hd_score +
                    a.getCurrentQvector()[12] * distance_score * mfactor_score +
                    a.getCurrentQvector()[13] * hd_score * mfactor_score;
*/

            if (score> bestScore)
            {
                bestPlace = candidate;
//                path = lastAnchoragePath;
                bestScore = score;
            }
        }

//        lastAnchoragePath = path;

        return bestPlace;
    }


    private Anchorage optimalChooseBest(TimedAnchorArea a, List<Anchorage> candidates) {
        Anchorage bestPlace = null;
//        GraphPath<BaseVertex,BaseEdge> path= null;

        double score ;
        double bestScore = -1 * Double.MAX_VALUE;
        int rCount = a.getRunCount();

        for (Anchorage candidate: candidates)
        {
            double ail_score          = calculateAILScore(candidate, a);
            double currentDIL_score   = calculateCurrentDILscore(candidate, a);
            double distance_score     = calculateDistanceScore(candidate, a);
            //   double hd_score           = calculateHDScore(candidate, a);
            double mfactor_score      = calculateMfactorScore(candidate, a);




//            double safety_score       = (W_SAFETY==0)      ? 0: calculateSafetyScore(candidate, a);
//            double wd_score           = (W_STACKING==0)    ? 0: calculateWDScore(candidate,a,candidates);
//
//            if (safety_score<MIN_SAFETY) {continue;}//System.out.println("INSECURE PATH REJECTED!!");
//
//            if (wd_score<MIN_STACKING) { continue;} //System.out.println("STACKING PATH REJECTED!!");


            // (3.6277_3.8205_-4.9322_-7.1351_3.4178_4.1181_6.9993_-6.8948_-6.2792_3.4000_3.5229_6.5058_-3.4694_4.0919)
            //  (-3.0731_6.1087_-6.1157_-4.3195_4.2433_-3.0143_3.3208_-6.2523_3.3381_2.5282_2.7855_5.1702_-3.4478_-2.5186)
            //(5.8876_-2.8519_-6.0675_-4.2713_4.1951_-2.9660_3.2725_-6.2040_-5.6226_-6.4324_-6.1752_5.1219_-3.3995_-2.4703)
            //(5.8615_6.0343_-6.0413_4.6150_4.1689_5.9203_-5.6137_-6.1779_-5.5965_2.4538_-6.1490_-3.7643_5.4867_-2.4441)
            //(4.6529_-4.6308_4.6442_4.6380_-4.6466)
//          (3.6091_3.4809_1.6305_0.6595_-2.1921_1.6296_-1.1705)
//          (3.6091_3.4809_1.6305_0.6595_-2.1921_1.6296_-1.1705)


            score = optimalTheta[0]   * ail_score +
                    optimalTheta[1]   * currentDIL_score +
                    optimalTheta[2]   * distance_score +

                    optimalTheta[3]   * ail_score * mfactor_score  +
                    optimalTheta[4]   * currentDIL_score * mfactor_score +
                    optimalTheta[5]   * distance_score  * mfactor_score  +
                    optimalTheta[6]   * ail_score * currentDIL_score * mfactor_score;


/*
            score = 5.8615   * ail_score +
                    6.0343   * currentDIL_score +
                    -6.0413  * distance_score +
                    4.6150   * hd_score +

                    4.1689   * ail_score * currentDIL_score  +
                    5.9203   * ail_score * distance_score +
                    -5.6137  * ail_score * hd_score +
                    -6.1779  * ail_score * mfactor_score +
                    -5.5965  * currentDIL_score  * distance_score +
                    2.4538   * currentDIL_score  * hd_score +
                    -6.1490  * currentDIL_score  * mfactor_score +
                    -3.7643  * distance_score * hd_score +
                    5.4867   * distance_score * mfactor_score +
                    -2.4441  * hd_score * mfactor_score;
*/

            if (score> bestScore)
            {
                bestPlace = candidate;
//                path = lastAnchoragePath;
                bestScore = score;
            }
        }

//        lastAnchoragePath = path;

        return bestPlace;
    }




    private double calculateAILScore(Anchorage candidate, TimedAnchorArea a) {

        double ail_Score=0;
        for(Circle2D ea: a.getExistingAnchoragesAsCircles()){
            if((Math.abs(candidate.getArea().center().x() - ea.center().x()) < ea.radius()) && (candidate.getArea().center().y() < ea.center().y())) {
                double IntersectionLength = 2 * Math.sqrt(Math.pow(ea.radius(), 2) - Math.pow((ea.center().x() - candidate.getArea().center().x()), 2));
                ail_Score +=  IntersectionLength;
            }else continue;
        }
        return 1-(ail_Score/maximumDistance);
    }


    private double calculateCurrentDILscore(Anchorage candidate, TimedAnchorArea a) {

        double currentDILscore = 0;
        for (Anchorage ship: a.getExistingAnchorages()){
            if (ship.getDepartureTime() > candidate.getDepartureTime()){
                if ((Math.abs(candidate.getArea().center().x() - ship.getArea().center().x()) < ship.getArea().radius()) && (candidate.getArea().center().y() < ship.getArea().center().y()) ){
                    double intersectionLength = 2 * Math.sqrt(Math.pow(ship.getArea().radius(), 2) - Math.pow(ship.getArea().center().x() - candidate.getArea().center().x(), 2));
                    currentDILscore += intersectionLength;
                }
            }
        }

        return 1-(currentDILscore/maximumDistance);
    }

    private double calculateMfactorScore(Anchorage candidate, TimedAnchorArea a) {


        double dPrime = (candidate.getDepartureTime() - candidate.getArrivalTime())/(a.getRUN_DAYS() * 1440);
        //double dPrime = Math.log(candidate.getDepartureTime() - candidate.getArrivalTime())/Math.log(a.getRUN_DAYS()* 1440);

        return dPrime;
    }

    private double oldCalculateMfactorScore(Anchorage candidate, TimedAnchorArea a) {
        int shipsWithMoreDuration=0;
        for (Anchorage ship: a.getExistingAnchorages()){
            if (ship.getDepartureTime() > candidate.getDepartureTime()){
                shipsWithMoreDuration++;
            }
        }
        double mf = a.getExistingAnchorages().size()==0 ? 0: shipsWithMoreDuration/a.getExistingAnchorages().size();

        return mf;
    }




    double calculateHDScore( Anchorage c, TimedAnchorArea a)
    {
        return a.getHoleDegree(c.getArea());
    }


    private double calculateDistanceScore(Anchorage candidate, TimedAnchorArea a)
    {
//        double distance = a.getEntrySide().ptLineDist(candidate.getArea().center().getAsDouble());
        double distance = a.getFirstEntryPoint().getY() - candidate.getArea().center().y();
        double distanceScore = distance/ maximumDistance;

        return distanceScore;
    }

    private double oldCalculateDistanceScore(Anchorage candidate, TimedAnchorArea a)
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









    private double calculateDepthConstraintScore(Anchorage candidate, TimedAnchorArea a) {

        DepthZone dz = a.depthZone(candidate);
        if (dz== null)
            return -1*MAX_DEPTH_PENALTY;



        return -1* Math.abs(candidate.getInnerRadius() - dz.getMaximumLength());  //To change body of created methods use File | Settings | File Templates.
    }

    double calculateSafetyScore( Anchorage c, TimedAnchorArea a)
    {
        lastAnchoragePath  = a.getAnchoragePath(c.getArea(),true);

        int intersectionCount = a.getTotalIntersectionCount(lastAnchoragePath);

        return -1 * intersectionCount;

    }

    double calculateWDScore( Anchorage c, TimedAnchorArea a, List<Anchorage> candidates)
    {
        double distance = a.getEntrySide().ptLineDist(c.getArea().center().getAsDouble())-c.getArea().radius(); //c.distance(a.getEntryPoint().getX(),a.getEntryPoint().getY());
        double meanDistance = calculateMeanDistance(a.getEntrySide(),candidates);
        double stackingDegree = (meanDistance-distance)/ maximumDistance;

        return -1 * stackingDegree;

    }


//    private void createModelGraph( TimedAnchorArea a) {
//
//        double minX = a.boundingRect().getMinX();
//        double maxX = a.boundingRect().getMaxX();
//        double minY = a.boundingRect().getMinY();
//        double maxY = a.boundingRect().getMaxY();
//
//        modelGraph=  new GridDirectedGraph(3,(int)(maxX-minX),(int)(maxY-minY),1,new Point2D.Double(minX,minY),0,0,0,0 );
//
//    }


    private Anchorage oldChooseBest(TimedAnchorArea a, List<Anchorage> candidates) {
        Anchorage bestPlace = null;
//        GraphPath<BaseVertex,BaseEdge> path= null;
        double score ;
        double bestScore = -1 * Double.MAX_VALUE;

        for (Anchorage candidate: candidates)
        {
            double ail_score          = (W_AIL==0)         ? 0: calculateAILScore(candidate,a);
            double hd_score           = (W_HOLEDEGREE==0)  ? 0: calculateHDScore(candidate,a);
            double safety_score       = (W_SAFETY==0)      ? 0: calculateSafetyScore(candidate,a);
            double wd_score           = (W_STACKING==0)    ? 0: calculateWDScore(candidate,a,candidates);
            double distance_score     = (W_DISTANCE==0)    ? 0: calculateDistanceScore(candidate, a);
            double depthConst_score   = (W_DEPTHCONST==0 ) ? 0: calculateDepthConstraintScore(candidate,a);


//            if (safety_score<MIN_SAFETY)
//            {
//                //System.out.println("INSECURE PATH REJECTED!!");
//                continue;
//            }

//            if (wd_score<MIN_STACKING)
//            {
//                //System.out.println("STACKING PATH REJECTED!!");
//                continue;
//            }
//

            score = W_AIL * ail_score +
                    W_HOLEDEGREE * hd_score +
                    W_SAFETY * safety_score +
                    W_DISTANCE* distance_score +
                    W_STACKING * wd_score+
                    W_DEPTHCONST * depthConst_score;

            if (score> bestScore)
            {
                bestPlace = candidate;
                //               path = lastAnchoragePath;
                bestScore = score;
            }
        }

//        lastAnchoragePath = path;

        return bestPlace;
    }

    @Override
    public GraphPath<BaseVertex, BaseEdge> getLastAnchoragePath() {
        return lastAnchoragePath;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
