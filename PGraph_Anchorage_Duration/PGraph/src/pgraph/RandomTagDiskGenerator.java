package pgraph;

import pgraph.tag.TagDisk;
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
public class RandomTagDiskGenerator implements  ObstacleGenerator{

    Point2D.Double p1;
    Point2D.Double p2;
    double radius;
    double cost;
    int count;

    public RandomTagDiskGenerator(int count, Point2D.Double p1, Point2D.Double p2, double radius, double cost)
    {
        this.count = count;
        this.p1 = p1;
        this.p2 = p2;
        this.radius = radius;
        this.cost = cost;
    }

    public List<ObstacleInterface> generate()
    {
        List<ObstacleInterface> disks = new ArrayList<ObstacleInterface>(count);
        SecureRandom sr = new SecureRandom();
        for (int i = 0 ;i<count;i++)
        {
            double x = p1.getX() + (p2.getX()-p1.getX())*sr.nextDouble();
            double y = p1.getY() + (p2.getY()-p1.getY())*sr.nextDouble();
            ObstacleInterface d = new TagDisk(IdManager.getObstacleId(),cost,new Point2D.Double(x,y),radius );
            disks.add(d);
        }
        return disks;
    }
}
