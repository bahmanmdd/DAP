// Ahirkapi
// TimedAnchorageManager.java

package pgraph.anchorage.distributions;

import pgraph.anchorage.TimedAnchorArea;
//import pgraph.anchorage.TimedAnchorageManager;
import pgraph.anchorage.TimedArrival;
import pgraph.anchorage.TimedArrivalInterface;
import pgraph.util.RandUtil;


/**
 * Created by bahmanmadadi on 3/5/2015.
 */
public class LognormalTAG implements TimedArrivalGenerator {

    public static int seed = 1 ;
    //RandUtil rng = new RandUtil((int) System.currentTimeMillis());
    RandUtil rng = new RandUtil(seed);


    ArrivalGenerator arrivalGenerator;

    //public static int SMALL_GENERATION_COUNT = 2;

    public static double DEP_ARR_RATIO = 0.5;


    public static double Mean_ARR_TIME = (1/53.9)*24;
    public static double S2_ARR_TIME = 1.2622;

    public static double Mean_DEP_TIME = 2.3622 ;
    public static double S2_DEP_TIME = 1.2622 ;




    public LognormalTAG(double Mean_ARR_TIME,double Mean_DEP_TIME, double S2_DEP_TIME,ArrivalGenerator ag ) {
        this.Mean_ARR_TIME = Mean_ARR_TIME;
        this.Mean_DEP_TIME = Mean_DEP_TIME;
        this.S2_DEP_TIME = S2_DEP_TIME;

        this.arrivalGenerator = ag;

    }

    public LognormalTAG(ArrivalGenerator ag ) {


        this.arrivalGenerator = ag;

    }


    private long _generated=0;

    @Override
    public TimedArrivalInterface generate(long startTime) {


        double length = arrivalGenerator.generate().getLength();

        // Exponential Test
        //long arrivalTime = startTime + (long) (60* rng.nextExp(Mean_ARR_TIME));
        //long departureTime = arrivalTime +  (long) (60* rng.nextExp(Mean_ARR_TIME*DEP_ARR_RATIO));


        // LogNormal Test
        long arrivalTime = startTime + (long) (60* rng.nextExp( Mean_ARR_TIME));
        long departureTime = arrivalTime +  (long) (60*DEP_ARR_RATIO*  Math.exp(rng.nextGaussian( Mean_DEP_TIME,  S2_DEP_TIME)));

        // Deterministic Test
        //long arrivalTime = startTime + (long) Mean_ARR_TIME *60;
        //long departureTime = (long) (arrivalTime +  60* Mean_ARR_TIME* DEP_ARR_RATIO);


        return new TimedArrival(arrivalTime,departureTime,length);
    }

    @Override
    public void setScale(double scale) {
        arrivalGenerator.setScale(scale);
    }

    @Override
    public void reset() {
        arrivalGenerator.reset();
        ++seed;
        seed = (seed % TimedAnchorArea.RUN_PER_ITERATION);
        //if (seed == TimedAnchorArea.RUN_PER_ITERATION + 1){seed = 1;}
        rng.setSeed(this.seed);
        _generated=0;
    }


}
