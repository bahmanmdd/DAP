package pgraph.anya;

import java.awt.geom.Point2D;

/**
 * Created by Dindar on 25.8.2014.
 */
public class AnyaRoot {
    Point2D.Double rootPoint = null;

    public AnyaRoot(Point2D.Double rootPoint) {
        this.rootPoint = rootPoint;
    }

    public Point2D.Double getRootPoint() {
        return rootPoint;
    }

    public void setRootPoint(Point2D.Double rootPoint) {
        this.rootPoint = rootPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnyaRoot anyaRoot = (AnyaRoot) o;

        if (Math.abs( rootPoint.getX() - anyaRoot.rootPoint.getX()) > AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD ||
            Math.abs( rootPoint.getY() - anyaRoot.rootPoint.getY() ) > AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return rootPoint.hashCode();
    }
}
