package pgraph.anchorage.distributions;

import pgraph.anchorage.Arrival;
import pgraph.util.RandUtil;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class UniformArrivalGenerator implements ArrivalGenerator {

    //RandUtil rng = new RandUtil((int) System.currentTimeMillis());
    RandUtil rng = new RandUtil(1);



    public static int SMALL_GENERATION_COUNT = 2;

    private double MIN_LENGTH = 25 ;
    private double MAX_LENGTH = 250;

    double scale = 1;

    public UniformArrivalGenerator() {
        this.scale = scale;
    }

    public UniformArrivalGenerator(double scale) {
        this.scale = scale;
    }

    public UniformArrivalGenerator(double MIN_LENGTH, double MAX_LENGTH) {
        this.MIN_LENGTH = MIN_LENGTH;
        this.MAX_LENGTH = MAX_LENGTH;
    }

    public UniformArrivalGenerator(double MIN_LENGTH, double MAX_LENGTH, double scale) {
        this.MIN_LENGTH = MIN_LENGTH;
        this.MAX_LENGTH = MAX_LENGTH;
        this.scale = scale;
    }

    private long _generated=0;

    @Override
    public Arrival generate() {


        double length = rng.nextUniform(MIN_LENGTH,MAX_LENGTH);


        if (_generated++<SMALL_GENERATION_COUNT)
            length = MIN_LENGTH;



        return new Arrival(length*scale );
    }

    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    public void reset() {
        _generated=0;
    }


}
