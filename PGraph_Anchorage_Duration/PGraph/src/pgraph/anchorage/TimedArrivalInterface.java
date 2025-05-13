package pgraph.anchorage;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 30.01.2014
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
public interface TimedArrivalInterface extends ArrivalInterface{
    public long getArrivalTime();
    public long getDepartureTime();

}
