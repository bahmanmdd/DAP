package pgraph.tag;

import pgraph.DiskObstacleShape;
import pgraph.Obstacle;
import pgraph.base.BaseVertex;
import pgraph.util.ArcPoint;
import pgraph.util.MathUtil;

import java.awt.geom.Point2D;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 19.01.2013
 * Time: 23:35
 * To change this template use File | Settings | File Templates.
 */
public class TagDisk extends Obstacle
{
    public Set<ArcPoint> arcPoints = new TreeSet<ArcPoint>();

    public TagDisk(long id, double weight, Point2D.Double center, double radius) {
        super(id, weight,new DiskObstacleShape( center, radius));
    }

    public BaseVertex getArcPoint(Point2D.Double p)
    {
        for (ArcPoint ap:arcPoints )
        {
            if (MathUtil.equalPoints(ap.getP().pos,p)  )
                return ap.getP();
        }
        return null;
    }
}
