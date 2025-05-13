package pgraph.anchorage.distributions;

import pgraph.anchorage.Arrival;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 29.01.2014
 * Time: 11:37
 * To change this template use File | Settings | File Templates.
 */
public class AhkpArrivalGenerator implements ArrivalGenerator {

    //static Random rng = new Random(System.currentTimeMillis());
    static Random rng = new Random(1);

    /*
        REFERENCE DATA (2012)

            <50     50-75       75-100     100-125     125-150      150-175     175-200     200-225     225-250   <250
        ----------------------------------------------------------------------------------------------------------------
            357	    5266        18237	   20478        12587       2619        3525        106         502        229


   */

    private final int slices[] = {25,50,75,100,125,150,175,200,225,250};
    private final int p_slices[] = { 357 , 5266 , 18237 , 20478 , 12587 , 2619 , 3525 , 106 , 502 };
    private double scale=1.0;


    public AhkpArrivalGenerator() {
    }

    public AhkpArrivalGenerator(double scale) {
        this.scale = scale;
    }

    @Override
    public Arrival generate() {

        int interval = rouletteWheel(p_slices);

        double l = generateBetween(slices[interval],slices[interval+1]);

        return new Arrival(l*scale);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

    private double generateBetween(int min, int max)  // UNIFORM BTW min and max
    {
        double l  = min;

        double d = rng.nextInt(max-min);

        return l+d;
    }

    private int rouletteWheel(int[] p_list) {
        int sum=0;
        for (int i= 0; i<p_list.length;i++)
            sum += p_list[i];

        int x = rng.nextInt(sum);

        int currentSum = 0;
        for (int i= 0; i<p_list.length;i++)
        {
            currentSum += p_list[i];
            if (x<currentSum)
                return i;
        }
        return p_list.length-1;
    }





    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }





}
