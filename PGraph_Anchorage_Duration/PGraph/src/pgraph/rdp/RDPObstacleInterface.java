package pgraph.rdp;

import pgraph.ObstacleInterface;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 27.01.2013
 * Time: 16:28
 *
 * Obstacle interface for RDP problems
 */
public interface RDPObstacleInterface extends ObstacleInterface {
    /**
     * Getter for obstacles probability
     * @return
     */
    public double getP();

    /**
     * Setter for obstacles probability
     * @param p
     */
    public void setP(double p) ;

    /**
     * Getter for obstacles actual existence
     * @return
     */
    public boolean isTrueObstacle() ;

    /**
     * Setter for obstacles actual existence
     * @return
     */
    public void setActualExistence(boolean actualExistence) ;

    /**
     * Clones the obstacle
     * @return
     */
    public RDPObstacleInterface clone();

    /**
     * Tests equality
     * @return
     */
    public boolean equals(RDPObstacleInterface o);
}
