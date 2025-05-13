package pgraph;

import math.geom2d.conic.Circle2D;
import pgraph.util.IdManager;

import java.awt.geom.Point2D;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 12/12/12
 * Time: 7:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class CircleListObstacleGenerator implements  ObstacleGenerator{

    private final double circleExpansion;
    List<Circle2D> circleList;


    double cost;


    public CircleListObstacleGenerator(List<Circle2D> cl, double cost, double circleExpansion)
    {
        this.circleList = cl;
        this.cost = cost;
        this.circleExpansion = circleExpansion;
    }

    public List<ObstacleInterface> generate()
    {
        List<ObstacleInterface> disks = new ArrayList<ObstacleInterface>();
        for (Circle2D c: circleList)
        {
            double x = c.center().x();
            double y = c.center().y();
            disks.add(new Obstacle(IdManager.getObstacleId(), cost,new DiskObstacleShape(new Point2D.Double(x,y),c.radius()+circleExpansion)));
        }
        return disks;
    }
}
