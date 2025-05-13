package pgraph.anya;

import java.awt.geom.Rectangle2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 28.06.2014
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
public class AnyaCell {

    public static final int CORNER_COUNT=4;

    boolean traversable = true;

    AnyaVertex[] corners;
    int cx =0;
    int cy= 0;

    public AnyaCell( int x, int y, AnyaVertex[] corners,boolean traversable) {
        this.traversable = traversable;
        this.corners = corners;
        cx= x;
        cy = y;
    }

    AnyaCell( int x, int y,AnyaVertex[] cList)
    {
        corners = cList; cx= x; cy = y;
    }

    public int getCx() {
        return cx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public int getCy() {
        return cy;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    public boolean isTraversable() {
        return traversable;
    }

    public void setTraversable(boolean traversable) {
        this.traversable = traversable;
    }

    public AnyaVertex[] getCorners() {
        return corners;
    }

    public void setCorners(AnyaVertex[] corners) {
        this.corners = corners;
    }

    public Rectangle2D.Double getCellRect(double indent)
    {
        double minX = corners[0].pos.getX();;
        double minY = corners[0].pos.getY();;
        double maxX = corners[0].pos.getX();
        double maxY = corners[0].pos.getY();;
        for (int i= 1; i<CORNER_COUNT;i++ )
        {
            if (minX > corners[i].pos.getX())
                minX = corners[i].pos.getX();
            if (minY > corners[i].pos.getY())
                minY = corners[i].pos.getY();

            if (maxX < corners[i].pos.getX())
                maxX = corners[i].pos.getX();
            if (maxY < corners[i].pos.getY())
                maxY = corners[i].pos.getY();
        }
        return new Rectangle2D.Double(minX+indent,minY+indent,maxX-minX-2*indent,maxY-minY-2*indent);
    }
}
