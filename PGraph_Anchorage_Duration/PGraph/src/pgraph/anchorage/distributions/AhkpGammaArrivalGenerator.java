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
public class AhkpGammaArrivalGenerator implements ArrivalGenerator {

   // RandUtil rng = new RandUtil((int)System.currentTimeMillis());
    RandUtil rng = new RandUtil(1);


    public static int SMALL_GENERATION_COUNT = 2;

    private static final double ALPHA   = 8.6912 ;
    private static final double BETA    = 11.629 ;
    private static final double LAMBDA  = 0 ;

    private  static final double MAX_LENGTH = 150;
    private  static final double MIN_LENGTH = 50;

    public AhkpGammaArrivalGenerator() {
    }

    public AhkpGammaArrivalGenerator(double scale) {
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

        double length = rng.nextGamma(ALPHA,BETA,LAMBDA);

        // Deterministic Test
        // double length = 2* ALPHA/BETA;



        while (length>MAX_LENGTH || length<MIN_LENGTH){
            length = rng.nextGamma(ALPHA,BETA,LAMBDA);
        }

      //  if (length>MAX_LENGTH)
       //     length = MAX_LENGTH;

      //  if (length<MIN_LENGTH)
      //      length = MIN_LENGTH;

        return new Arrival(length*scale );
    }

    @Override
    public void reset() {
        rng.setSeed(LognormalTAG.seed);

    }


}
