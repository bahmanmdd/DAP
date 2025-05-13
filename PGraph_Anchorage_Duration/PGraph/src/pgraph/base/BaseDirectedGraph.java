package pgraph.base;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.GraphPath;
import pgraph.ObstacleInterface;
import pgraph.Path;
import pgraph.gui.BaseGraphPanel;
import pgraph.specialzone.SpecialZoneInterface;
import pgraph.util.Pen;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:13
 *
 * Base class for DirectedGraphs
 */
public class BaseDirectedGraph implements DirectedGraph<BaseVertex,BaseEdge> , BaseGraphPanel.Renderable
{
    public int id=-1;
    public TreeSet<BaseVertex> vertices = new TreeSet<BaseVertex>();
    public TreeSet<ObstacleInterface> obstacles = new TreeSet<ObstacleInterface>();
    public TreeSet<SpecialZoneInterface> specialZones = new TreeSet<SpecialZoneInterface>();
    public TreeSet<BaseEdge> edgeSet =new TreeSet<BaseEdge>();

    public List<Path> pathList = new ArrayList<Path>();


    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return
     */
    @Override
    public Set<BaseEdge> getAllEdges(BaseVertex sourceVertex, BaseVertex targetVertex) {

        if (sourceVertex==null || !vertices.contains(sourceVertex))
            return null;
        if (targetVertex==null || !vertices.contains(targetVertex))
            return null;
        TreeSet<BaseEdge> edges = new TreeSet<BaseEdge>();

        BaseEdge e = sourceVertex.getOutgoingTo(targetVertex);
        if (e!=null)
            edges.add(e);
        return edges;
    }


    public BaseVertex getVertex(long id)
    {
        for(BaseVertex v:vertices)
        {
            if (v.id == id)
                return v;
        }
        return null;
    }


    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return
     */
    @Override
    public BaseEdge getEdge(BaseVertex sourceVertex, BaseVertex targetVertex) {
        return sourceVertex.getOutgoingTo(targetVertex);
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @return
     */
    @Override
    public EdgeFactory<BaseVertex, BaseEdge> getEdgeFactory() {
        throw new NotImplementedException();
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return
     */
    @Override
    public BaseEdge addEdge(BaseVertex sourceVertex, BaseVertex targetVertex) {
        throw new NotImplementedException();
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     * @param baseEdge
     * @return
     */
    @Override
    public boolean addEdge(BaseVertex sourceVertex, BaseVertex targetVertex, BaseEdge baseEdge)
    {
        BaseEdge e = sourceVertex.getOutgoingTo(targetVertex);
        if (e==null || !e.equals(baseEdge) )
        {
            sourceVertex.addOutgoing(baseEdge);
            targetVertex.addIncoming(baseEdge);
            edgeSet.add(baseEdge);
            return true;
        }
        else return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param baseVertex
     * @return
     */
    @Override
    public boolean addVertex(BaseVertex baseVertex) {
        edgeSet.addAll(baseVertex.touchings);
        return vertices.add(baseVertex);
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return
     */
    @Override
    public boolean containsEdge(BaseVertex sourceVertex, BaseVertex targetVertex) {
        return (sourceVertex.getOutgoingTo(targetVertex) != null);  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param baseEdge
     * @return
     */
    @Override
    public boolean containsEdge(BaseEdge baseEdge) {
        return edgeSet.contains(baseEdge);
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param baseVertex
     * @return
     */
    @Override
    public boolean containsVertex(BaseVertex baseVertex) {
        return vertices.contains(baseVertex);  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @return
     */
    @Override
    public Set<BaseEdge> edgeSet() {
        return edgeSet;
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param vertex the vertex for which a set of touching edges is to be
     * returned.
     *
     * @return
     */
    @Override
    public Set<BaseEdge> edgesOf(BaseVertex vertex) {

        return vertex.touchings;
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param edges edges to be removed from this graph.
     *
     * @return
     */
    @Override
    public boolean removeAllEdges(Collection<? extends BaseEdge> edges) {
        boolean b=false;
        for(BaseEdge e:edges)
        {
            b = b || removeEdge(e);
        }
        return b;
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return
     */
    @Override
    public Set<BaseEdge> removeAllEdges(BaseVertex sourceVertex, BaseVertex targetVertex) {
        BaseEdge e = removeEdge(sourceVertex,targetVertex);
        TreeSet<BaseEdge> edges= new TreeSet<BaseEdge>();
        edges.add(e);
        return edges;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param vlist
     * @return
     */
    @Override
    public boolean removeAllVertices(Collection<? extends BaseVertex> vlist) {
        return this.vertices.removeAll(vlist);
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param sourceVertex source vertex of the edge.
     * @param targetVertex target vertex of the edge.
     *
     * @return
     */
    @Override
    public BaseEdge removeEdge(BaseVertex sourceVertex, BaseVertex targetVertex) {
        BaseEdge e = sourceVertex.getOutgoingTo(targetVertex);
        removeEdge(e);
        return e;
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param baseEdge
     * @return
     */
    @Override
    public boolean removeEdge(BaseEdge baseEdge) {
        if (baseEdge==null ||  !containsEdge(baseEdge))
            return false;

        baseEdge.start.removeOutgoing(baseEdge);
        baseEdge.end.removeIncoming(baseEdge);
        edgeSet.remove(baseEdge);
        return true;
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param baseVertex
     * @return
     */
    @Override
    public boolean removeVertex(BaseVertex baseVertex) {
        if (baseVertex==null || !containsVertex(baseVertex))
            return false;
        Set<BaseEdge> edges = edgesOf(baseVertex);
        removeAllEdges(edges);
        return  vertices.remove(baseVertex);
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @return
     */
    @Override
    public Set<BaseVertex> vertexSet() {
        return vertices;
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param baseEdge
     * @return
     */
    @Override
    public BaseVertex getEdgeSource(BaseEdge baseEdge) {
        return baseEdge.start;
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param baseEdge
     * @return
     */
    @Override
    public BaseVertex getEdgeTarget(BaseEdge baseEdge) {
        return baseEdge.end;
    }

    /**
     * Read documentation on the Graph<V,E> interface definition
     * @param baseEdge
     * @return
     */
    @Override
    public double getEdgeWeight(BaseEdge baseEdge) {
        return baseEdge.getEdgeWeight();
    }


    /**
     * Finds the number of Obstacles intersected by given path
     * @param path
     * @return
     */
    public int intersectingObstacleCount(GraphPath<BaseVertex,BaseEdge> path)
    {
        int oc=0;
        List<BaseEdge> edges = path.getEdgeList();
        for (ObstacleInterface o:obstacles)
        {
            for (BaseEdge e: edges)
            {
                if (e == null) continue;
                if (e.intersects(o.shape()))
                    oc++;
            }
        }
        return oc;
    }

    /**
     * Sets the costs of all obstacles to c
     * @param c
     * @throws InstantiationException
     */
    public void setObstacleCost( double c) throws InstantiationException {
        for (ObstacleInterface o : obstacles)
        {
            o.setWeight(c);
        }
    }


    /**
     * Converts graph into string
     * @return
     */
    public String convert2String()
    {
        String st= new String();

        st +=""+vertices.size()+"\n\n";

        for(BaseEdge e:edgeSet)
        {
            st+=e.start.id+" "+e.end.id+" "+e.getEdgeWeight()+"\n";
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter("GraphText.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(st);;
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return st;
    }

    /**
     * Writest the graph to a given file
     * @param fileName
     * @throws IOException
     */
    public void write2File(String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw= new BufferedWriter(fw);
        String st= new String();

        st +=""+vertices.size()+"\n\n";

        bw.write(st);
        for(BaseEdge e:edgeSet)
        {
            st=e.start.id+" "+e.end.id+" "+e.getEdgeWeight()+"\n";
            bw.write(st);
        }
        bw.close();

    }


    /**
     * Read documentation on the DirectedGraph<V,E> interface definition
     * @param vertex vertex whose degree is to be calculated.
     *
     * @return
     */
    @Override
    public int inDegreeOf(BaseVertex vertex) {
        return vertex.incomings.size();
    }

    /**
     * Read documentation on the DirectedGraph<V,E> interface definition
     * @param vertex the vertex for which the list of incoming edges to be
     * returned.
     *
     * @return
     */
    @Override
    public Set<BaseEdge> incomingEdgesOf(BaseVertex vertex) {
        return vertex.incomings;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Read documentation on the DirectedGraph<V,E> interface definition
     * @param vertex vertex whose degree is to be calculated.
     *
     * @return
     */
    @Override
    public int outDegreeOf(BaseVertex vertex) {
        return vertex.outgoings.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Read documentation on the DirectedGraph<V,E> interface definition
     * @param vertex the vertex for which the list of outgoing edges to be
     * returned.
     *
     * @return
     */
    @Override
    public Set<BaseEdge> outgoingEdgesOf(BaseVertex vertex) {
        return vertex.outgoings;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Tests two paths if they are equal
     * @param p1
     * @param p2
     * @return
     */
    public static boolean isEqual(GraphPath<BaseVertex,BaseEdge> p1 , GraphPath<BaseVertex,BaseEdge> p2)
    {
        boolean b = true;
        List<BaseEdge> el1= p1.getEdgeList();
        List<BaseEdge> el2= p2.getEdgeList();
        if(el1.size()!=el2.size())
            return false;

        for(int i =0 ; i<el1.size();i++)
        {
            if (!el1.get(i).equals(el2.get(i)))
            {
                System.out.println("NOT EQUAL id1="+el1.get(i).id+" w1="+el1.get(i).getEdgeWeight()+" id2="+el2.get(i).id+" w2="+el2.get(i).getEdgeWeight());
                b=false;
            }
        }
        return b;
    }


    public int getTotalIntersectionCount(BaseEdge e) throws InstantiationException
    {
        throw new NotImplementedException();
    }

    @Override
    public void draw(Graphics2D g2D, BaseGraphPanel.ViewTransform transform) {
        Color orjColor = g2D.getColor();
        Stroke orjStroke = g2D.getStroke();
        Stroke stroke = null;

        float dashPhase = 0f;
        float dash[] = {5.0f,5.0f};
        float point[] = {3.0f,12.0f};

        for (ObstacleInterface o: obstacles)
        {
            g2D.setColor(o.getPen().color);
            if (o.getPen().style == Pen.PenStyle.PS_Dashed) {
                stroke = new BasicStroke(o.getPen().thickness,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_MITER,
                        o.getPen().thickness,
                        dash,
                        dashPhase);
            }
            else  if (o.getPen().style == Pen.PenStyle.PS_Pointed) {
                stroke = new BasicStroke(o.getPen().thickness,
                        BasicStroke.CAP_SQUARE,
                        BasicStroke.JOIN_BEVEL,
                        o.getPen().thickness,
                        dash,
                        dashPhase);
            }
            else
                stroke = new BasicStroke(o.getPen().thickness);

            g2D.draw(transform.createTransformedShape(o.shape()));
        }

        for (SpecialZoneInterface zone: specialZones)
        {
            zone.draw(g2D,transform);
        }

        for (Path path: pathList )
        {
            path.draw(g2D,transform);
        }
        g2D.setStroke(orjStroke);
        g2D.setColor(orjColor);
    }

    @Override
    public Rectangle2D boundingRect() {
        throw new NotImplementedException();
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }
}

