package pgraph.anchorage;

/**
 * Created by Dindar on 7.8.2014.
 */
public class TimedArrival implements TimedArrivalInterface{

    long arrivalTime;
    long departureTime;

    double length;

    public TimedArrival(long arrivalTime, long departureTime, double length) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.length = length;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}
