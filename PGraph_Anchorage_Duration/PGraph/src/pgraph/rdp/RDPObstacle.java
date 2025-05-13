package pgraph.rdp;

import pgraph.*;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:20
 *
 * Obstacle class for RDP problems
 */
public class RDPObstacle extends Obstacle implements RDPObstacleInterface {

    double p=0 ; // Probabilty
    boolean actualExistence = false;
    double distanceToTarget=0;

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public boolean isTrueObstacle() {
        return actualExistence;
    }

    public void setActualExistence(boolean actualExistence) {
        this.actualExistence = actualExistence;
    }

    @Override
    public RDPObstacleInterface clone() {
        return new RDPObstacle(getId(), getObstacleWeight(),getObstacleShape(),p,actualExistence,distanceToTarget);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public RDPObstacle(long id, double weight, ObstacleShape os, double p, boolean actualExistence, double distanceToTarget) {
        super(id, weight, os);
        this.p = p;
        this.actualExistence = actualExistence;
        this.distanceToTarget = distanceToTarget;
    }

    protected RDPObstacle(long id, double weight,ObstacleShape os) {
        super(id,weight,os);

    }

    @Override
    public boolean equals(RDPObstacleInterface o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RDPObstacle that = (RDPObstacle) o;

        if (actualExistence != that.actualExistence) return false;
        if (Double.compare(that.distanceToTarget, distanceToTarget) != 0) return false;
        if (Double.compare(that.p, p) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = p != +0.0d ? Double.doubleToLongBits(p) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (actualExistence ? 1 : 0);
        temp = distanceToTarget != +0.0d ? Double.doubleToLongBits(distanceToTarget) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
