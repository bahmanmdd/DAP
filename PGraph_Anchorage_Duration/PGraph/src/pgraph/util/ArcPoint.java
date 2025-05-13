package pgraph.util;

import pgraph.DiskObstacleShape;
import pgraph.tag.TagVertex;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 1/14/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArcPoint implements Comparable<ArcPoint> {

    static int _nextArcPointID = 0;

    public int id =0 ;
    TagVertex p= null;
    DiskObstacleShape disk=null;
    DiskObstacleShape otherDisk = null;
    TagVertex otherPoint = null;
    double angle;

    public TagVertex getP() {
        return p;
    }

    public ArcPoint(DiskObstacleShape d, TagVertex p, DiskObstacleShape od)
    {
        id = _nextArcPointID++;
        disk = d;
        this.p = p;
        otherDisk = od;
        angle = ArcMath.getAngle(p.pos, d.getCenter());
        angle = angle>0 ? angle:360+angle;
    }

    public ArcPoint(DiskObstacleShape d,TagVertex p, TagVertex op)
    {
        id = _nextArcPointID++;
        disk = d;
        this.p = p;
        otherPoint= op;

        angle = ArcMath.getAngle(p.pos,d.getCenter());
        angle = angle>0 ? angle:360+angle;
    }


    public boolean comingFrom(DiskObstacleShape d)
    {
        if (otherDisk != null)
            return (d==otherDisk);
        else return false;
    }
    public boolean comingFrom(TagVertex v)
    {
        if (otherPoint != null)
            return (v==otherPoint);
        else return false;
    }
    public boolean hasCommonSource(ArcPoint ap)
    {
        boolean  b = ((otherDisk != null) && (otherDisk ==ap.otherDisk))||((otherPoint !=null)&& (otherPoint==ap.otherPoint));
        return b;
    }

    @Override
    public int compareTo(ArcPoint o)
    {
        if (id == o.id)
            return 0;
        else if (id>o.id)
            return 1;
        else
            return -1;
    }

    public static void sortByClockWise(ArcPoint[] arcpoints)
    {
        ArcPoint tmp;

        for (int i = 0 ;i<arcpoints.length;i++)
        {
            for (int j = i+1; j<arcpoints.length;j++)
            {
                if (arcpoints[i].angle>arcpoints[j].angle)
                {
                    tmp = arcpoints[j];
                    arcpoints[j]=arcpoints[i];
                    arcpoints[i]=tmp;
                }
            }
        }
    }
}
