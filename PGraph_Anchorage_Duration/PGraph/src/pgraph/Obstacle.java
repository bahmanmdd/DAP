package pgraph;

import pgraph.specialzone.ZoneEffect;
import pgraph.specialzone.ZoneShape;
import pgraph.util.Pen;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:20
 *
 * Base class for obstacles
 */
public class Obstacle implements ObstacleInterface {
    private static final String  ZONE_SEPERATOR ="['|']" ;
    long id = -1;
    private boolean passable = true;
    private double weight;
    Pen pen = Pen.DefaultPen;
    ObstacleShape obstacleShape;

    public boolean isPassable() {
        return passable;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    public void setPen(Pen pen) {
        this.pen = pen;
    }

    public void setObstacleShape(ObstacleShape obstacleShape) {
        this.obstacleShape = obstacleShape;
    }

    public ObstacleShape getObstacleShape() {
        return obstacleShape;
    }

    public Obstacle(long id,double weight,ObstacleShape os) {
        this.id = id;
        this.weight = weight;
        this.obstacleShape = os;
    }


    public Obstacle(long id,double weight,String  obsStr) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.id = id;
        this.weight = weight;

        String[] tokens = obsStr.split(ZONE_SEPERATOR);

        String shapeStr =tokens[0];
        obstacleShape = (ObstacleShape) Class.forName(shapeStr.split(" ")[0]).newInstance();
        obstacleShape.fromString(shapeStr);


        if (tokens.length>1)
        {
            String penStr = tokens[1];
            pen = new Pen(penStr);
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public double getObstacleWeight() {
        return (passable)? weight:Double.MAX_VALUE ;
    }

    @Override
    public void setWeight(double w) {
        weight=w;
    }

    @Override
    public Pen getPen() {
        return pen;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(ObstacleInterface o) {
        return Long.compare(this.id,o.getId());
    }

    @Override
    public Shape shape() {
        return obstacleShape.shape();  //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public Shape shape(int scale) {
        return obstacleShape.shape(scale);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Pen pen() {
        return pen;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString()
    {
        return obstacleShape.toString();

    }
    public void fromString(String st)
    {
        obstacleShape.fromString(st);
    }

    public boolean intersectsLine(Point2D.Double p1,Point2D.Double p2)
    {
        return obstacleShape.intersectsLine(p1,p2);
    }


    public int lineIntersectionCount(LineEdge le) throws InstantiationException
    {
        return  obstacleShape.lineIntersectionCount(le);
    }

}
