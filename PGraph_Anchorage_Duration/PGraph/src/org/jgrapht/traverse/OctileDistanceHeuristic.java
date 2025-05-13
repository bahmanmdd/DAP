package org.jgrapht.traverse;

import pgraph.grid.GridVertex;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 3/29/13
 * Time: 9:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class OctileDistanceHeuristic<V> implements Heuristic<V> {
    private static final double ROOT_TWO = 1.414213562f;
    private V target;

    public OctileDistanceHeuristic(V t) {
        target =t;
    }

    @Override
    public double getValue(V s) {

        if (s== null  )
            return 0;

        GridVertex gs = (GridVertex)s;
        GridVertex gt = (GridVertex)target;

        int  dx = Math.abs(gs.gridPos.getX() - gt.gridPos.getX());
        int  dy = Math.abs(gs.gridPos.getY()- gt.gridPos.getY() );

        int min = (dx<dy) ? dx:dy;

        double octileDistance = ((int)Math.abs(dx-dy)) + min*ROOT_TWO;

        return octileDistance;
    }
}
