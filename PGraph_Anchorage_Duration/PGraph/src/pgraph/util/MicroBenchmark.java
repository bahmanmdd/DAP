package pgraph.util;

import pgraph.anya.experiments.MBRunnable;

/**
 * Created by Dindar on 21.9.2014.
 */
public class MicroBenchmark {

    MBRunnable runnable= null;

    long avgTime = -1;
    long maxTime = -1;
    long minTime = Long.MAX_VALUE;

    public MicroBenchmark(MBRunnable runnable) {
        this.runnable = runnable;
    }

    public long getAvgTime() {
        return avgTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getMinTime() {
        return minTime;
    }

    public void benchmark(int reps)
    {
        //System.gc();

        if (reps<=0)
            return;

        for (int i= 0; i<reps+2; i++)
        {
            run(i!=0);
            if (i< reps+1)
                cleanUp();
        }
        avgTime -= maxTime; // Discarding any possible outlier

        avgTime = avgTime/reps;


    }

    private void cleanUp() {
        runnable.cleanUp();
    }

    private void run(boolean validIteration)
    {
        long start = System.nanoTime();
        runnable.run();
        long time = (System.nanoTime()-start)/1000; // converting to microseconds

        if(!validIteration)
            return;
        //System.out.println(" Time : "+ time);

        avgTime += time;

        if (maxTime<time)
            maxTime = time;

        if (minTime>time )
            minTime= time;


    }


}
