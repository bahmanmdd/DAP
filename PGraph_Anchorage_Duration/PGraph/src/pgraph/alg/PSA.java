package pgraph.alg;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.DijkstraWithHeuristicShortestPath;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.traverse.MHDHeuristic;
import pgraph.alg.dijkstra.DijkstraWithTC;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.util.GraphUtil;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 12/14/12
 * Time: 2:41 PM
 *
 * This class implements PSA algorithm
 * It works on all graphs inheriting BaseDirectedGraph .
 */
public class PSA implements NeutralizationAlgorithm {

    public double initialAlpha;
    public double finalAlpha;
    public int dijkstraCallCount=0;
    public int solutionIDC = 0;
    double epsilon = 0.001;


    public PSA(double initialAlpha)
    {
        this.initialAlpha = initialAlpha;
    }

    public PSA(double initialAlpha, double epsilon)
    {
        this.initialAlpha = initialAlpha;
        this.epsilon =   epsilon;
    }


    /**
     * Finds shortest path on g starting from s ending at e
     * @param g
     * @param s start
     * @param e end
     * @return
     */
    public GraphPath<BaseVertex,BaseEdge>  getShortestPath(BaseDirectedGraph g,BaseVertex s,BaseVertex e)
    {
        dijkstraCallCount++;
      //  DijkstraShortestPath<BaseVertex,BaseEdge> spAlg = new DijkstraShortestPath<BaseVertex, BaseEdge>(g,s,e);

        DijkstraWithHeuristicShortestPath<BaseVertex,BaseEdge> spAlg = new DijkstraWithHeuristicShortestPath<BaseVertex, BaseEdge>(g,s,e,new MHDHeuristic<BaseVertex>(e));
        GraphPath<BaseVertex,BaseEdge> gp = spAlg.getPath();
      //  DijkstraWithTC spAlg = new DijkstraWithTC((GridDirectedGraph)g,0);
      //  GraphPath<BaseVertex,BaseEdge> gp = spAlg.getShortestPath();
        return gp;
    }




    static final double INITIAL_COEFFICIENT = 1;


    static final double MAX_COST = 20000;

    /**
     *  Performs the Neutralization algorithm
     * @param g graph
     * @param s start
     * @param e end
     * @param K neutro count
     * @return
     * @throws InstantiationException
     */
    @Override
    public GraphPath<BaseVertex,BaseEdge>  perform(BaseDirectedGraph g,BaseVertex s,BaseVertex e,int K) throws InstantiationException {
        double a = initialAlpha;
        double upperBound=-1,lowerBound= 0;
        dijkstraCallCount = 0;

        g.setObstacleCost(a);

        int lastValidIDC = 0;
        double lastValidAlpha = 0;
        GraphPath<BaseVertex,BaseEdge> lastValidPath= null;

        GraphPath<BaseVertex,BaseEdge> path = getShortestPath(g,s,e);

        int idc = g.intersectingObstacleCount(path);

        if (idc<=K)
        {
            finalAlpha =a;
            solutionIDC = idc;
            return path;
        }
        while (idc!=K && ( (a-lowerBound)>epsilon || upperBound==-1))
        {
            if (a>MAX_COST)
                throw new NullPointerException("No Solution Found");
            if (idc>K)
            {
                lowerBound = a;
                if (upperBound ==-1)
                    a = a*10;
                else
                {
                    a = (upperBound-a>1)?  Math.round((a+upperBound)/2):((a+upperBound)/2);
                }
            }
            else
            {
                lastValidIDC= idc;
                lastValidAlpha = a;
                lastValidPath = path;
                upperBound = a;
                a = (a-lowerBound>1)?  Math.round((a+lowerBound)/2):((a+lowerBound)/2);

            }
            g.setObstacleCost(a);
            path = getShortestPath(g,s,e);

            idc= g.intersectingObstacleCount(path);
        }

        if (idc > K) // Eğer epsilon sınırına ulaştığımız için durduysak.
        {
            finalAlpha =lastValidAlpha;
            solutionIDC = lastValidIDC;
            path = lastValidPath;
        }
        else
        {
            finalAlpha =a;
            solutionIDC = idc;
        }


        g.setObstacleCost(initialAlpha);
        double weight = GraphUtil.recalculatePathWeight(g, path);
        path = new GraphPathImpl<BaseVertex, BaseEdge>(g,path.getStartVertex(),path.getEndVertex(),path.getEdgeList(),weight);
        return path;
    }
}
