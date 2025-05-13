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
public class ChiSquareArrivalGenerator implements ArrivalGenerator {

    //RandUtil rng = new RandUtil((int)System.currentTimeMillis());
    RandUtil rng = new RandUtil(1);


    private  final int DF  ;
    private  final double LAMBDA ;

    private  static final double MAX_LENGTH = 250;
    private  static final double MIN_LENGTH = 25;

    public ChiSquareArrivalGenerator(int DF, double LAMBDA) {
        this.DF = DF;
        this.LAMBDA = LAMBDA;
        this.scale = scale;
    }

    public ChiSquareArrivalGenerator(int DF, double LAMBDA, double scale) {
        this.DF = DF;
        this.LAMBDA = LAMBDA;
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



        double length = rng.nextChiSq(DF,LAMBDA)
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
