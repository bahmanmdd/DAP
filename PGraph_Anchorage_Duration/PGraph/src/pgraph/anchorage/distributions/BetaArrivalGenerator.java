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
public class BetaArrivalGenerator implements ArrivalGenerator {

    //RandUtil rng = new RandUtil((int)System.currentTimeMillis());
    RandUtil rng = new RandUtil(1);


    private double alfa = 2.4162 ;
    private  double beta  = 2.0395;

    private  static final double MAX_LENGTH = 150;
    private  static final double MIN_LENGTH = 50;

    public BetaArrivalGenerator() {
    }

    public BetaArrivalGenerator(double alfa, double beta) {
        this.alfa = alfa;
        this.beta = beta;
    }

    public BetaArrivalGenerator(double alfa, double beta, double scale) {
        this.alfa = alfa;
        this.beta = beta;
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

        double length = MIN_LENGTH + (MAX_LENGTH-MIN_LENGTH)*rng.nextBeta(alfa, beta);

               ;
        if (length>MAX_LENGTH)
            length = MAX_LENGTH;

        if (length<MIN_LENGTH)
            length = MIN_LENGTH;

        return new Arrival(length*scale );
    }

    @Override
    public void reset() {

        rng.setSeed(LognormalTAG.seed);
    }


}
