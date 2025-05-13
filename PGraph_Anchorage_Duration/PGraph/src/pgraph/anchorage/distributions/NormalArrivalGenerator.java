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
public class NormalArrivalGenerator implements ArrivalGenerator {

    //RandUtil rng = new RandUtil((int)System.currentTimeMillis());
    RandUtil rng = new RandUtil(1);


    private double mean = 101 ;        // Mean: 101
    private  double s2  = 1175.26;        // Variance: (34.282)^2   : 1175.26

    private  static final double MAX_LENGTH = 250;
    private  static final double MIN_LENGTH = 25;

    public NormalArrivalGenerator() {
    }

    public NormalArrivalGenerator(double mean, double S2) {
        this.mean = mean;
        this.s2 = S2;
    }

    public NormalArrivalGenerator(double mean, double S2, double scale) {
        this.mean = mean;
        this.s2 = S2;
        this.scale = scale;
    }



    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    private double scale = 1;



    @Override
    public Arrival generate() {



        double length = rng.nextGaussian(mean, s2)
               ;
        if (length>MAX_LENGTH)
            length = MAX_LENGTH;

        if (length<MIN_LENGTH)
            length = MIN_LENGTH;

        return new Arrival(length*scale );
    }

    @Override
    public void reset() {

    }


}
