package pgraph.rdp;

import kshortestpath.model.Pair;
import pgraph.ObstacleInterface;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridVertex;
import pgraph.util.GraphFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 1/29/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class RDPUtil {

    private static final double MIN_DISTANCE = 2.0;

    /**
     * Returns disambiguation points of the given obstacle o in the given graph g
     * @param g
     * @param o
     * @return
     */
    public static Set<GridVertex> getDisambiguationPoints(GridDirectedGraph g, ObstacleInterface o)
    {
        if (dpCache.containsKey(o))
            return dpCache.get(o);

        TreeSet<GridVertex> disambiguationPoints = new TreeSet<GridVertex>();
        Set<BaseEdge> intersectingEdges = g.intersectingEdgesOf(o);
        for (BaseEdge e:intersectingEdges)
        {
            GridVertex gv = (GridVertex)e.start;
            if (!o.shape().contains(gv.pos))
                disambiguationPoints.add(gv);
        }

        dpCache.put(o,disambiguationPoints);

        return disambiguationPoints;
    }


    public static HashMap<Pair<ObstacleInterface,Integer> ,Set<GridVertex> > dpNCache = new HashMap();
    public static HashMap<ObstacleInterface,Set<GridVertex> > dpCache = new HashMap();
    public static HashMap<ObstacleInterface,Set<GridVertex> > cdpCache = new HashMap();



    public static final double DISTANCE_THRESHOLD = 1.5;


    public static boolean appendToSetWithThreshold(Set<GridVertex> vs, GridVertex v,double distanceThreshold)
    {
        for (GridVertex gv:vs)
        {
            if (gv.pos.distance(v.pos)<distanceThreshold)
                return false;
        }
        vs.add(v);
        return true;
    }

    /**
     * Returns disambiguation points of given obctsacle o which are also dp of some of the given other obstacles
     * @param o
     * @return
     */
    public static Set<GridVertex> getCrossDisambiguationPoints(GridDirectedGraph g,ObstacleInterface o)
    {
        if (cdpCache.containsKey(o))
            return cdpCache.get(o);

        Set<GridVertex> disambiguationPoints = getDisambiguationPoints(g,o);
        Set<GridVertex> crossDisambiguationPoints =new  TreeSet<GridVertex>();


        for (GridVertex dp:disambiguationPoints)
        {
            for (ObstacleInterface other:g.obstacles)
            {
                if (other.equals(o))
                    continue;
                Set<GridVertex> otherDisambiguationPoints = getDisambiguationPoints(g,other);
                if (otherDisambiguationPoints.contains(dp))
                {
                    appendToSetWithThreshold(crossDisambiguationPoints, dp, DISTANCE_THRESHOLD);
                    break;
                }

            }
        }
        cdpCache.put(o,crossDisambiguationPoints);

        return crossDisambiguationPoints;
    }


    public static void clearCaches()
    {
        dpCache.clear();
        dpNCache.clear();
        cdpCache.clear();
    }

    public static Set<GridVertex> getDisambiguationPoints(GridDirectedGraph g, ObstacleInterface o,int n)
    {
        Pair<ObstacleInterface,Integer>key = new Pair<ObstacleInterface,Integer>(o,n);
        if (dpNCache.containsKey(key))
            return dpNCache.get(key);

        Set<GridVertex> disambiguationPoints = getDisambiguationPoints(g,o);

        if ((n>= disambiguationPoints.size()) || (n == -1))
            return  disambiguationPoints;

        List<GridVertex> dpList = new ArrayList<>(disambiguationPoints);
        GridVertex base = dpList.get(0);
        double minDistance = base.pos.distance(dpList.get(1).pos);
        for (int i=1;i<dpList.size();i++)
        {
            double d = base.pos.distance(dpList.get(i).pos);
            if(d<minDistance)
                minDistance =d;
        }

        double distance = minDistance* (disambiguationPoints.size()/n);
        Set<GridVertex> dpSet = getPointsAtDistance(dpList,distance,n);

        dpNCache.put(key, dpSet);

        return  dpSet;
    }

    public static Set<GridVertex> getPointsAtDistance(List<GridVertex> vList,double distance,int maxPoints )
    {
        TreeSet<GridVertex> dpSet = new TreeSet<GridVertex>();
        GridVertex current = vList.get(0);
        GridVertex other = null;
        dpSet.add(current);

        for (int i=1;i<vList.size();i++)
        {
            GridVertex v = getPointAtDistance(current, other, vList, distance);
            dpSet.add(v);
            if (dpSet.size()>= maxPoints )
                break;
            other = current;
            current = v;
        }
        return dpSet;
    }

    public static GridVertex getPointAtDistance(GridVertex v,GridVertex other,List<GridVertex> vList,double distance )
    {
        double minDistance = 100*distance;
        int minDistanceIndex = -1;
        for (int i=0;i<vList.size();i++)
        {
            GridVertex vi =vList.get(i);
            if(v == vi || vi == other || (other !=null &&  vi.pos.distance(other.pos)<MIN_DISTANCE))
                continue;
            double d = Math.abs(v.pos.distance(vi.pos)-distance);
            if(d<minDistance)
            {
                minDistance =d;
                minDistanceIndex = i;
            }
        }
        return vList.get(minDistanceIndex);
    }


    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        testGrid();

    }

    public static void testGrid() throws IOException, InstantiationException {
        GridDirectedGraph g = GraphFactory.createGrid("maps/random1.txt", 100, 0, 49, 99, 49, 1, 100, 100,1);


        Set<GridVertex> dpSet3 = getDisambiguationPoints(g,g.obstacles.first(),4);
        Iterator<ObstacleInterface> oi = g.obstacles.iterator();

        while (oi.hasNext())
        {
            Set<GridVertex> cdpSet = getCrossDisambiguationPoints(g,oi.next());
            int a = 0;
        }




        Set<GridVertex> dpSet = getDisambiguationPoints(g,g.obstacles.first());
        Set<GridVertex> dpSet2 = getDisambiguationPoints(g,g.obstacles.first());
    }

}

