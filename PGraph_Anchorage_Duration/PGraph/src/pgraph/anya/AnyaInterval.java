package pgraph.anya;

import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 28.06.2014
 * Time: 12:26
 * To change this template use File | Settings | File Templates.
 */
public class AnyaInterval {
    double left;
    double right;
    int row;

    public AnyaInterval(double left, double right, int row) {
        this.left = left;
        this.right = right;
        this.row = row;
    }

    public static final double DOUBLE_INEQUALITY_THRESHOLD = 0.0000001;

    @SuppressWarnings("unchecked")
    public boolean equals(Object obj)
    {
        if( ! (obj instanceof AnyaInterval))
            return false;
        AnyaInterval p = (AnyaInterval)obj;
        return Math.abs(p.left-left) < DOUBLE_INEQUALITY_THRESHOLD && Math.abs(p.right-right) <DOUBLE_INEQUALITY_THRESHOLD && p.row ==row;

        //return ((p.left <= left && p.right>=right)||(p.left >= left && p.right<=right) ) && p.row ==row;
    }

    public boolean covers(AnyaInterval i)
    {
        if ( Math.abs(i.left-left) < DOUBLE_INEQUALITY_THRESHOLD && Math.abs(i.right-right) <DOUBLE_INEQUALITY_THRESHOLD && i.row ==row )
            return true;

        return (left <= i.left && right>=i.right && row == i.row);

    }


    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(left);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(right);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + row;
        return result;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
