package pgraph.grid.tcgrid;

import pgraph.grid.GridPosition;
import pgraph.grid.GridVertex;

import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 1/24/13
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TCGridVertex extends GridVertex {
    TCGridVertex previous;
    TCGridVertex lastTurnVertex;

    public TCGridVertex(long id, Point2D.Double pos, GridPosition gridPos, TCGridVertex prev,TCGridVertex ltv) {
        super(id, pos, gridPos);
        lastTurnVertex= ltv;
        previous = prev;
    }
    public static final TCGridVertex NullVertex = new TCGridVertex(-1,new Point2D.Double(-1,-1),new GridPosition(-1,-1),null,null);

    public boolean equals(TCGridVertex v)
    {
        if (v == null)
            return false;

        return (Quadriple.same(lastTurnVertex,v.lastTurnVertex)&& Quadriple.same(previous,v.previous) && gridPos.equals(v.gridPos));
    }
}
