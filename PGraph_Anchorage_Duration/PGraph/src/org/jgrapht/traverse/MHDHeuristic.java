package org.jgrapht.traverse;

import pgraph.grid.GridVertex;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 3/29/13
 * Time: 9:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class MHDHeuristic<V> implements Heuristic<V> {
    private V target;

    public MHDHeuristic( V t) {
        target =t;
    }

    @Override
    public double getValue(V s) {

        if (s== null  )
            return 0;

        GridVertex gs = (GridVertex)s;
        GridVertex gt = (GridVertex)target;

        double  dx = Math.abs(gs.gridPos.getX() - gt.gridPos.getX());
        double  dy = Math.abs(gs.gridPos.getY()- gt.gridPos.getY() );
        return Math.sqrt( dx*dx + dy*dy ) ;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
