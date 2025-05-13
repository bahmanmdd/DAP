package pgraph.alg;

import kshortestpath.control.YenTopKShortestPathsAlg;
import kshortestpath.model.KPath;
import kshortestpath.model.abstracts.IVertex;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.tag.TagVertex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 3/21/13
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class KSPNA implements NeutralizationAlgorithm {

    static final long MAX_ATTEMPT = Long.MAX_VALUE;

    public long returnPathCount= 0;
    public long elapsedTime = 0;

    @Override
    public GraphPath<BaseVertex, BaseEdge> perform(BaseDirectedGraph g, BaseVertex s, BaseVertex e, int K) throws InstantiationException, IOException {

        elapsedTime = 0;
        returnPathCount=0;
        System.out.println("KSPNA Started..");

        long startTime = System.currentTimeMillis();

        kshortestpath.model.Graph kg =new  kshortestpath.model.Graph();
        kg.import_from_basegraph(g);
        System.out.println("KSPNA Graph created K-Shortest Algorithm started..");
        YenTopKShortestPathsAlg kspAlg = new YenTopKShortestPathsAlg(kg,kg.get_vertex((int) s.id),kg.get_vertex((int) e.id));

        int idc =0;
        long attempt = 0;

        GraphPath<BaseVertex,BaseEdge> path = null;

        do
        {
            KPath p = kspAlg.next();

            GraphPath<BaseVertex,BaseEdge> ptmp = convertPath(g,p);

            idc = g.intersectingObstacleCount(ptmp);

            if (returnPathCount%1000 ==0)
                System.out.println("KSPNA Iteration-"+returnPathCount+" : IDC: "+ idc+" P-Weight: "+p.get_weight()+ " Path-Weight: "+ptmp.getWeight()+" Path:"+ ptmp);

            returnPathCount++;
            if (idc <= K )
            {
                path =ptmp;
                break;
            }
        }
        while (idc!= K && kspAlg.has_next() &&( (attempt++)<MAX_ATTEMPT) );


        elapsedTime = System.currentTimeMillis()- startTime;
        System.out.println("KSPNH Completed: IDC: "+ idc+" returnPathCount: "+returnPathCount);

        return path;
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
}
