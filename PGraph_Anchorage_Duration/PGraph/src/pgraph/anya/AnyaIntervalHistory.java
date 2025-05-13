package pgraph.anya;

import kshortestpath.model.Pair;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dindar on 30.8.2014.
 */
public class AnyaIntervalHistory {

    int rowCount =0;
    List< List<Pair<AnyaInterval,Pair<Double,Double>>> > intervals = new ArrayList<>();

    public AnyaIntervalHistory(int rowCount) {
        this.rowCount = rowCount;

        _createInnerLists();
    }

    private void _createInnerLists() {
        for (int i=0; i<rowCount ; i++)
            intervals.add(i, new ArrayList<>());
    }





    private Pair<Double,Double> calculateDistances(AnyaNode n, double y) {

        Point2D.Double lp =  new Point2D.Double(n.interval.getLeft(),y);
        Point2D.Double rp =  new Point2D.Double(n.interval.getRight(),y);

        double ld = lp.distance(n.root);
        double rd= rp.distance(n.root);

        if (lp.getX()>=n.root.getX())
        {
            double max =n.g + rd;
            double min =n.g + ld;
            return new Pair<>(min,max);
        }
        else if (rp.getX()<= n.root.getX())
        {
            double max =n.g + ld;
            double min =n.g + rd;
            return new Pair<>(min,max);
        }

        double max = n.g +  ((ld>rd)? ld:rd);
        double min = n.g + Math.abs(n.root.getY()-y) ;

        return new Pair<Double,Double>(min,max);
    }

    public boolean ignorableNode(AnyaNode n,double y)
    {
        List<Pair<AnyaInterval,Pair<Double,Double>>> list = intervals.get(n.interval.getRow());
        List<Pair<AnyaInterval,Pair<Double,Double>>> removeList= null;

        AnyaInterval i = n.interval;
        Pair<Double,Double> v=  calculateDistances(n,y);

        for (Pair<AnyaInterval,Pair<Double,Double>> p: list)
        {
            AnyaInterval hi = p.first();
            Pair<Double,Double> hv = p.second();

            if (hi.covers(i))
            {
                if ( v.first()-hv.second() > AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
                    return true;
            }

            if (i.covers(hi))
            {
                if ( hv.first()-v.second() > AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
                {
                    if (removeList == null)
                        removeList = new ArrayList<>();
                    removeList.add(p);
                }
            }

        }

        if (removeList != null) {
            for (Pair<AnyaInterval, Pair<Double, Double>> p : removeList) {
                list.remove(p);
            }
        }

        list.add(new Pair<>(i,v));

        return  false;
    }

    public void clear() {
        intervals.clear();
    }
}
