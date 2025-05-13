package pgraph.grid;

import pgraph.base.BaseVertex;

import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 15.01.2013
 * Time: 22:32
 *
 * Vertex class on Lattice Graphs
 */
public class GridVertex extends BaseVertex
{
    /**
     * Lattice coordinates of the vertex
     */
    public GridPosition gridPos;

    @Override
    public String toString() {
        return "GV["+gridPos+"]";
    }

    public GridVertex(long id, Point2D.Double pos, GridPosition gridPos) {
        super(id, pos);
        this.gridPos = gridPos;
    }
}
