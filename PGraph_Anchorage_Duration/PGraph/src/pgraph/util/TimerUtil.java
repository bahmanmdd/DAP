package pgraph.util;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 09.02.2014
 * Time: 09:28
 * To change this template use File | Settings | File Templates.
 */
public class TimerUtil {

    static HashMap<String,Long> totalTimes = new HashMap<String,Long>();
    static HashMap<String,Long> times = new HashMap<String,Long>();

    static boolean enabled = false;

    public static void setEnabled(boolean enabled) {
        TimerUtil.enabled = enabled;
    }

    public static void reset(String timerName)
    {
        if (!enabled) return;
        totalTimes.remove(timerName);
        times.remove(timerName);
    }

    public  static void reset()
    {
        if (!enabled) return;
        totalTimes.clear();
        times.clear();
    }

    public static void start(String timerName)
    {
        if (!enabled) return;
        times.put(timerName,System.currentTimeMillis());
    }
    public static void stop(String timerName)
    {
        if (!enabled) return;
        totalTimes.put(timerName,getTotalTime(timerName)+getTime(timerName));
    }

    public static long getTime(String timerName)
    {
        if (!enabled) return 0;
        long time = (times.containsKey(timerName)) ? (System.currentTimeMillis()-times.get(timerName)) : 0;
        return time;
    }
    public static long getTotalTime(String timerName)
    {
        if (!enabled) return 0;
        long totalTime = (totalTimes.containsKey(timerName)) ? totalTimes.get(timerName) : 0;
        return totalTime;
    }

    public static void printTotal(String timerName)
    {
        if (!enabled) return;
        System.out.println(StringUtil.padLeft(timerName,15) + " Total : " +  StringUtil.padLeft(""+getTotalTime(timerName),10) );
    }

    public static void printTotal()
    {
        if (!enabled) return;
        for (String timerName: totalTimes.keySet())
            printTotal(timerName);
    }



    public static void printTime(String timerName)
    {
        if (!enabled) return;
        System.out.println(StringUtil.padLeft(timerName,15) + " Time : " +  StringUtil.padLeft(""+getTime(timerName),10) );
    }


}
