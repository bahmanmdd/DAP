package pgraph.anchorage;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 09:03
 * To change this template use File | Settings | File Templates.
 */
public class Arrival implements ArrivalInterface {
    public Arrival(double radius) {
        this.length = radius;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    double length;
}
