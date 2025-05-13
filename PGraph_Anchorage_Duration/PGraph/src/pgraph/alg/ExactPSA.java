package pgraph.alg;

import kshortestpath.control.YenTopKShortestPathsAlg;
import kshortestpath.model.KPath;
import kshortestpath.model.abstracts.IVertex;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.GraphPathImpl;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.util.GraphUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 12/14/12
 * Time: 2:41 PM
 *
 * This class implements Exact-PSA algorithm which is based on PSA and K-ShortestPath algorithms
 * It works on TAG graphs.
 */
public class ExactPSA implements NeutralizationAlgorithm {

    public static final int MAX_ATTEMPT= 100000;

    public long returnPathCount=0;
    public int neutroCount = 0;
    double initialAlpha;
    public double currentAlpha=0;
    double increment;
    private boolean _completed = false;

    public  boolean pathFound = false;
    private long startTime;
    public  long elapsedTimePSA;


    public enum ReturnPolicy {RP_ReturnFromLowerBound, RP_ReturnFromUpperBound};

    public ReturnPolicy returnPolicy = ExactPSA.ReturnPolicy.RP_ReturnFromLowerBound;

    public long elapsedTime;


    public GraphPath<BaseVertex,BaseEdge> firstCDPath=null;
    public GraphPath<BaseVertex,BaseEdge> secondCDPath=null;




    public ExactPSA(double initialAlpha, double increment)
    {
        this.initialAlpha = initialAlpha;
        this.increment = increment;
    }



    public GraphPath<BaseVertex,BaseEdge>  getShortestPath(BaseDirectedGraph g,BaseVertex s,BaseVertex e)
    {
        DijkstraShortestPath<BaseVertex,BaseEdge> spAlg = new DijkstraShortestPath<BaseVertex, BaseEdge>(g,s,e);

        GraphPath<BaseVertex,BaseEdge> gp = spAlg.getPath();

        return gp;
    }

    public GraphPath<BaseVertex,BaseEdge> convertPath(BaseDirectedGraph g,KPath p)
    {
        List<IVertex> vlist = p.get_vertices();


        double w =0;

        ArrayList<BaseEdge> elist = new ArrayList<BaseEdge>();

        BaseVertex start = g.getVertex(Long.valueOf(vlist.get(0).get_id()));
        BaseVertex end = g.getVertex(Long.valueOf(vlist.get(vlist.size() - 1).get_id()));

        for (int i= 0 ; i<(vlist.size()-1);i++)
        {
            BaseVertex vs = g.getVertex(Long.valueOf(vlist.get(i).get_id()));
            BaseVertex vt = g.getVertex(Long.valueOf(vlist.get(i+1).get_id()));
            BaseEdge e = vs.getOutgoingTo(vt);
            if (e != null)
            {
                elist.add(e);
                w += e.getEdgeWeight();
            }
        }

        return new GraphPathImpl<BaseVertex, BaseEdge>(g,start,end,elist,w);
    }




    static final double INITIAL_ALPHA = 1;
    public static final double MIN_INTERVAL =0.000001;
    public final double MAX_ALPHA = 1000000;

    public GraphPath<BaseVertex,BaseEdge> performSCNH_Binary(int K,BaseVertex start,BaseVertex end,BaseDirectedGraph g) throws InstantiationException {
        currentAlpha = initialAlpha;
        double upperBound=-1,lowerBound= 0;

        g.setObstacleCost(currentAlpha);

        GraphPath<BaseVertex,BaseEdge> path = getShortestPath(g,start,end);
        int idc = g.intersectingObstacleCount(path);

        if (idc <=K)
        {
            _completed = true;
            pathFound = true;
            return path;
        }
        while ( idc!=K &&
                ( (currentAlpha-lowerBound)>MIN_INTERVAL ||
                  ( (returnPolicy== ReturnPolicy.RP_ReturnFromLowerBound && idc<K)||
                    ( returnPolicy == ReturnPolicy.RP_ReturnFromUpperBound && idc>K)  ) ))
        {
            if (currentAlpha>MAX_ALPHA)
                return null;
            if (idc>K)
            {
                lowerBound = currentAlpha;
                if (upperBound ==-1)
                    currentAlpha = currentAlpha*10;
                else
                {
                    currentAlpha = ((currentAlpha+upperBound)/2);
                }
            }
            else
            {
                upperBound = currentAlpha;
                currentAlpha = ((currentAlpha+lowerBound)/2);
            }
            g.setObstacleCost(currentAlpha);
            path = getShortestPath(g,start,end);
            idc= g.intersectingObstacleCount(path);
        }
        return path;
    }

   public GraphPath<BaseVertex,BaseEdge> bisectionPath;

    public GraphPath<BaseVertex,BaseEdge> perform_with_Yens_Algorithm(BaseDirectedGraph g,BaseVertex start,BaseVertex end,int K) throws InstantiationException, IOException {

        System.out.println("E-PSA Started..");
        System.out.println("E-PSA Preforming Phase-1");

        int attempt= 0;

        GraphPath<BaseVertex,BaseEdge> path = performSCNH_Binary(K,start,end,g);

        if (path == null)
            throw new NullPointerException("No path Found");

        bisectionPath = path;
        elapsedTimePSA = System.currentTimeMillis()- startTime;

        int idc = g.intersectingObstacleCount(path);

        System.out.println("E-PSA Phase-1 Completed. IDC: " + idc + " K: " + K);

        if (_completed || idc == K)
        {
            neutroCount = idc;
            firstCDPath = path;
            pathFound = true;
            return path;
        }
        System.out.println("E-PSA  Phase-1 Result: IDC: "+ idc+" Weight: "+path.getWeight()+ " Path:"+ path);
        System.out.println("E-PSA Phase-2 Started..");

        kshortestpath.model.Graph kg =new  kshortestpath.model.Graph();
        kg.import_from_basegraph(g);
        System.out.println("E-PSA  Phase-2 K-Shortest KPath started..");
        YenTopKShortestPathsAlg kspAlg = new YenTopKShortestPathsAlg(kg,kg.get_vertex((int) start.id),kg.get_vertex((int) end.id));


        double delta=0;
        double currentDelta =Double.POSITIVE_INFINITY;
        GraphPath<BaseVertex,BaseEdge> candidatePath = null;
        int candidateIDC = 0;

        candidatePath = bisectionPath;
        candidateIDC = idc;
        currentDelta = (K-candidateIDC)*(currentAlpha-initialAlpha);
        do
        {
            KPath p = kspAlg.next();

            GraphPath<BaseVertex,BaseEdge> ptmp = convertPath(g,p);

            idc = g.intersectingObstacleCount(ptmp);

            if (returnPathCount%1000 ==0)
                System.out.println("E-PSA  Phase-2 Iteration-"+returnPathCount +" : IDC: "+ idc+" P-Weight: "+p.get_weight()+ " Path-Weight: "+ptmp.getWeight()+" Path:"+ ptmp);

            returnPathCount++;

            if (idc <=K)
            {
                delta = (K-idc)*(currentAlpha-initialAlpha);
                if ((candidatePath==null)|| ( (candidatePath.getWeight() + (idc-candidateIDC)*(currentAlpha-initialAlpha) )> ptmp.getWeight()  ))
                {
                    if ((firstCDPath==null) || (secondCDPath==null) )
                    {
                        if (firstCDPath==null)
                            firstCDPath = ptmp;
                        else
                            secondCDPath = ptmp;
                    }


                    candidatePath = ptmp;
                    candidateIDC = idc;
                    currentDelta = delta;
                }
            }

            if ((candidatePath!= null) && ((ptmp.getWeight()-candidatePath.getWeight()) >= currentDelta))
            {
                path=candidatePath;
                pathFound= true;
               break;
            }

        }
        while (candidateIDC!= K && kspAlg.has_next() && returnPathCount< MAX_ATTEMPT );

        neutroCount =    idc = g.intersectingObstacleCount(path);

        System.out.println("E-PSA  Phase-2 Completed: Path Found: " + pathFound+" IDC: "+ idc+" returnPolicy: "+returnPolicy+" returnPathCount: "+returnPathCount);


        return path;
    }



    @Override
    public GraphPath<BaseVertex,BaseEdge> perform(BaseDirectedGraph g,BaseVertex start,BaseVertex end,int K) throws InstantiationException, IOException {
        returnPathCount = 0;
        elapsedTime = 0;
        pathFound = false;
        startTime = System.currentTimeMillis();

        GraphPath<BaseVertex,BaseEdge> path = perform_with_Yens_Algorithm(g,start,end,K);

        elapsedTime = System.currentTimeMillis()- startTime;

        g.setObstacleCost(initialAlpha);
        double weight = GraphUtil.recalculatePathWeight(g,path);
        double bweight = GraphUtil.recalculatePathWeight(g,bisectionPath);
        path = new GraphPathImpl<BaseVertex, BaseEdge>(g,path.getStartVertex(),path.getEndVertex(),path.getEdgeList(),weight);
        bisectionPath = new GraphPathImpl<BaseVertex, BaseEdge>(g,bisectionPath.getStartVertex(),bisectionPath.getEndVertex(),bisectionPath.getEdgeList(),bweight);

        System.out.println("E-PSA COMPLETED Path Found: "+ pathFound+ " idc: "+ neutroCount+" cost: " + weight);



        return path;
    }

}


