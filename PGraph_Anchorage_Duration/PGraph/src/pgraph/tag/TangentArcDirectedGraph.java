package pgraph.tag;

import org.jgrapht.GraphPath;
import pgraph.*;
import pgraph.base.BaseEdge;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseVertex;
import pgraph.intersectionhandler.IntersectionHandler;
import pgraph.intersectionhandler.StandardIntersectionHandler;
import pgraph.util.ArcMath;
import pgraph.util.ArcPoint;
import pgraph.util.IdManager;
import pgraph.util.StringUtil;


import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;


public class TangentArcDirectedGraph extends BaseDirectedGraph
{
	public Point2D.Double offSet = new Point2D.Double(0,0);
    public BaseVertex start;
    public BaseVertex end;


    IntersectionHandler intersectionHandler = new StandardIntersectionHandler();

    HashMap<BaseEdge,List<ObstacleInterface>> intersectedObstacles=new HashMap<BaseEdge,List<ObstacleInterface>>();


    public IntersectionHandler getIntersectionHandler() {
        return intersectionHandler;
    }

    public void setIntersectionHandler(IntersectionHandler intersectionHandler) {
        this.intersectionHandler = intersectionHandler;
    }

    public void save(String fileName) throws IOException
    {
    	BufferedWriter br  = new BufferedWriter(new FileWriter(fileName));
    	br.write(offSet.toString()); br.newLine();
/*
    	br.write((start).xIndex+"");br.newLine();
    	br.write(((LatticeVertex)start).yIndex+"");br.newLine();
    	br.write(((LatticeVertex)end).xIndex+"");br.newLine();
    	br.write(((LatticeVertex)end).yIndex+"");br.newLine();


    	int len = dangerZones.size();
    	br.write(len+"");br.newLine();
    	for(int i = 0; i < len; i++){
    		br.write(dangerZones.get(i).toString());br.newLine();
    	}
*/
    	br.flush();
    	br.close();
    }

    public void load(String fileName) throws NumberFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
    	vertices = null;

        FileReader fr = new FileReader((fileName));
    	BufferedReader br = new BufferedReader(fr);
    	offSet = StringUtil.pointFromString(br.readLine());

       	int len = Integer.parseInt(br.readLine());
    	for(int i = 0; i < len; i++){
    		String str = br.readLine();
    	}
    	br.close();

        _createTAG();

    }

    public int getTotalIntersectionCount(BaseEdge e) throws InstantiationException {
        if (!intersectedObstacles.containsKey(e))
            return 0;

        List<ObstacleInterface> oList = intersectedObstacles.get(e);

        int ic = 0;
        for(ObstacleInterface o:oList)
            ic += e.intersectionCount(o.shape());

        return ic;
    }

    public  void disk2diskTangents(TagDisk td1, TagDisk td2)
    {
        DiskObstacleShape d1 = (DiskObstacleShape)td1.getObstacleShape();
        DiskObstacleShape d2 = (DiskObstacleShape)td2.getObstacleShape();
        Point2D.Double[][] pList = ArcMath.getTangents(d1.getCenter(), d1.getRadius(), d2.getCenter(), d2.getRadius());

        Vector<BaseVertex> vertices =  new Vector<BaseVertex>();

        boolean sCreated = false, tCreated=false;
        for (int i= 0; i<pList.length;i++)
        {
            BaseVertex s=td1.getArcPoint(pList[i][0]);
            if (s==null)
            {
               s = new TagVertex(pList[i][0]);
               addVertex(s);
               td1.arcPoints.add(new ArcPoint(d1,(TagVertex)s,d2));
            }
            BaseVertex t =td2.getArcPoint(pList[i][1]);
            if (t==null)
            {
                t = new TagVertex(pList[i][1]);
                addVertex(t);
                td2.arcPoints.add(new ArcPoint(d2,(TagVertex)t,d1));
            }

            addEdge(s, t, new LineEdge(IdManager.getEdgeId(), s, t));
            addEdge(t,s,new LineEdge(IdManager.getEdgeId(),t,s));
        }
    }

    public void disk2pointTangents(TagDisk td1, BaseVertex p)
    {
        DiskObstacleShape d1 = (DiskObstacleShape)td1.getObstacleShape();

        Point2D.Double[][] pList = ArcMath.getTangents(d1.getCenter(), d1.getRadius(), p.pos, 0);


        boolean  sCreated= false;
        for (int i= 0; i<pList.length;i++)
        {
            BaseVertex s = td1.getArcPoint(pList[i][0]);
            if (s==null)
            {
                s = new TagVertex(pList[i][0]);
                td1.arcPoints.add(new ArcPoint(d1,(TagVertex)s,(TagVertex)p));
                addVertex(s);
            }
            BaseVertex t = p;

            addEdge(s,t,new LineEdge(IdManager.getEdgeId(),s,t));
            addEdge(t,s,new LineEdge(IdManager.getEdgeId(),t,s));
        }
    }

    void _createArcs(TagDisk td)
    {
        DiskObstacleShape d = (DiskObstacleShape)td.getObstacleShape();
        Object[] objects = td.arcPoints.toArray();
        ArcPoint[] arcPoints = new ArcPoint[objects.length];

        for (int i = 0; i<td.arcPoints.size();i++)
        {
            arcPoints[i] = (ArcPoint)objects[i];
        }

        ArcPoint.sortByClockWise(arcPoints);

        for (int i = 0; i<td.arcPoints.size()-1;i++)
        {
            ArcPoint ap = arcPoints[i];
            ArcPoint ap2 = arcPoints[i+1];

            TagVertex v1 = ap.getP();
            TagVertex v2 = ap2.getP();

            if (v1 != v2)
            {
                addEdge(v1,v2,new ArcEdge(IdManager.getEdgeId(),v1,v2,d.getCenter(),d.getRadius()));
                addEdge(v2,v1,new ArcEdge(IdManager.getEdgeId(),v2,v1,d.getCenter(),d.getRadius()));
            }
        }
        TagVertex v1 = arcPoints[0].getP();
        TagVertex v2 = arcPoints[arcPoints.length-1].getP();
        if (v1 != v2)
        {
            addEdge(v1,v2,new ArcEdge(IdManager.getEdgeId(),v1,v2,d.getCenter(),d.getRadius()));
            addEdge(v2,v1,new ArcEdge(IdManager.getEdgeId(),v2,v1,d.getCenter(),d.getRadius()));
        }
    }

    void createArcs()
    {
        for (ObstacleInterface d: obstacles)
        {
            _createArcs((TagDisk)d);
        }
    }

    public void _createTAG()
    {
        int a = obstacles.size();
        int v = 0 ;

        addVertex(start);
        addVertex(end);

        ArrayList<ObstacleInterface> olist = new ArrayList<ObstacleInterface>();
        olist.addAll(obstacles);

        for (int i = 0; i<olist.size();i++)
        {
            TagDisk d1 = (TagDisk) olist.get(i);
            for (int j = i+1; j<olist.size();j++)
            {
                TagDisk d2 = (TagDisk) olist.get(j);
                if (d1 != d2)
                {
                    disk2diskTangents(d1, d2);
                }
            }
            disk2pointTangents(d1, start);
            disk2pointTangents(d1,end);
        }

        createArcs();

        addEdge(start,end,new LineEdge(IdManager.getEdgeId(),start,end));
        addEdge(end,start,new LineEdge(IdManager.getEdgeId(),end,start));
    }

    public TangentArcDirectedGraph(String fileName) throws NumberFormatException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
    {
        load(fileName);
    }

    public TangentArcDirectedGraph(TreeSet<ObstacleInterface> dList, BaseVertex s, BaseVertex e, Point2D.Double o) throws InstantiationException {
        start = s;
        end = e;
        offSet = o;
        obstacles = dList;
        _createTAG();
        _updateEdges();
    }


    @Override
    public void setObstacleCost( double c) throws InstantiationException {
        for (ObstacleInterface o : obstacles)
        {
            o.setWeight(c);
        }
        _updateIntersectingEdgeCosts();
    }


    private void _updateIntersectingEdgeCosts() throws InstantiationException {
        for (BaseEdge e:intersectedObstacles.keySet())
        {
            List<ObstacleInterface> oList = intersectedObstacles.get(e);
            double w = 0;
            for (ObstacleInterface o:oList)
                w += intersectionHandler.getIntersectionPenalty(this,e,o,e.intersectionCount(o.shape()));

            e.weight = e.getLength() + w;
        }
    }

    void _updateEdges(ObstacleInterface o) throws InstantiationException {
        Ellipse2D.Double ds = (Ellipse2D.Double) o.shape();
        for(BaseEdge e :edgeSet)
        {
            if (e == null)
                continue;
            if (e.end == null)
                continue;
            int ic = e.intersectionCount(o.shape());
            if (  ic>0 )
            {
                e.weight = e.weight + intersectionHandler.getIntersectionPenalty(this,e,o,ic);
                if (!intersectedObstacles.containsKey(e))
                    intersectedObstacles.put(e,new ArrayList<ObstacleInterface>());
                intersectedObstacles.get(e).add(o);
            }
        }

        //System.out.println("Edge Update For Disk-"+o.getId()+" completed");
    }

    void _updateEdges() throws InstantiationException {
        System.out.println("Updating Edges..");
        for (ObstacleInterface d:obstacles)
            _updateEdges(d);
    }

    @Override
    public int intersectingObstacleCount(GraphPath<BaseVertex,BaseEdge> path)
    {
        TreeSet<ObstacleInterface> intersected = new TreeSet<ObstacleInterface>();
        List<BaseEdge> edges = path.getEdgeList();

        for (BaseEdge e: edges)
        {
            if (e == null) continue;
            if (intersectedObstacles.containsKey(e))
                intersected.addAll(intersectedObstacles.get(e));
        }

        return intersected.size();
    }


	
}
