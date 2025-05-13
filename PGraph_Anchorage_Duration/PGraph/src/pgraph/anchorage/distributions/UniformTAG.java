package pgraph.anchorage.distributions;

import pgraph.anchorage.TimedArrival;
import pgraph.anchorage.TimedArrivalInterface;
import pgraph.util.RandUtil;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class UniformTAG implements TimedArrivalGenerator {

   // RandUtil rng = new RandUtil((int) System.currentTimeMillis());
    RandUtil rng = new RandUtil(1);


    ArrivalGenerator arrivalGenerator;

    public static int SMALL_GENERATION_COUNT = 2;

    private long MIN_ARR_TIME = 25 ;
    private long MAX_ARR_TIME = 250;

    private long MIN_DEP_TIME = 25 ;
    private long MAX_DEP_TIME = 250;




    public UniformTAG(long MIN_ARR_TIME, long MAX_ARR_TIME,long MIN_DEP_TIME, long MAX_DEP_TIME,ArrivalGenerator ag ) {
        this.MIN_ARR_TIME = MIN_ARR_TIME;
        this.MAX_ARR_TIME = MAX_ARR_TIME;
        this.MIN_DEP_TIME = MIN_DEP_TIME;
        this.MAX_DEP_TIME = MAX_DEP_TIME;

        this.arrivalGenerator = ag;

    }



    private long _generated=0;

    @Override
    public TimedArrivalInterface generate(long startTime) {


        double length = arrivalGenerator.generate().getLength();

        long arrivalTime = startTime+ (long) rng.nextUniform(MIN_ARR_TIME,MAX_ARR_TIME);
        long departureTime = arrivalTime +  (long) rng.nextUniform(MIN_DEP_TIME,MAX_DEP_TIME);

        return new TimedArrival(arrivalTime,departureTime,length);
    }

    @Override
    public void setScale(double scale) {
        arrivalGenerator.setScale(scale);
    }

    @Override
    public void reset() {
        arrivalGenerator.reset();
        _generated=0;
    }


}
