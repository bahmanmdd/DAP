package pgraph.grid.tcgrid;

import pgraph.grid.GridPosition;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 05.08.2013
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class TCGridPosition {
    GridPosition gridPos;
    GridPosition prevGridPos;
    int turnConstraint;


    public TCGridPosition(GridPosition gridPos, GridPosition prevGridPos, int turnConstraint) {
        this.gridPos = gridPos;
        this.turnConstraint = turnConstraint;
        this.prevGridPos = prevGridPos;
    }

    public GridPosition getGridPos() {
        return gridPos;
    }

    public void setGridPos(GridPosition gridPos) {
        this.gridPos = gridPos;
    }

    public int getTurnConstraint() {
        return turnConstraint;
    }

    public void setTurnConstraint(int turnConstraint) {
        this.turnConstraint = turnConstraint;
    }

    public GridPosition getPrevGridPos() {
        return prevGridPos;
    }

    public void setPrevGridPos(GridPosition prevGridPos) {
        this.prevGridPos = prevGridPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TCGridPosition that = (TCGridPosition) o;

        if (turnConstraint != that.turnConstraint) return false;
        if (gridPos != null ? !gridPos.equals(that.gridPos) : that.gridPos != null) return false;
        if (prevGridPos != null ? !prevGridPos.equals(that.prevGridPos) : that.prevGridPos != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gridPos != null ? gridPos.hashCode() : 0;
        result = 31 * result + (prevGridPos != null ? prevGridPos.hashCode() : 0);
        result = 31 * result + turnConstraint;
        return result;
    }
}
