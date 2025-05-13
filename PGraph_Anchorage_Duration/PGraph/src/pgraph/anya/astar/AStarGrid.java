package pgraph.anya.astar;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.DijkstraWithHeuristicShortestPath;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.traverse.MHDHeuristic;
import org.jgrapht.traverse.OctileDistanceHeuristic;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

import pgraph.LineEdge;
import pgraph.Path;
import pgraph.util.TimerUtil;
import pgraph.anya.*;
import pgraph.anya.experiments.AStarExperimentLoader;
import pgraph.anya.experiments.AStarMapLoader;
import pgraph.anya.experiments.AnyaMapLoader;
import pgraph.anya.experiments.ExperimentInterface;
import pgraph.anya.experiments.MBRunnable;
import pgraph.anya.experiments.MapLoaderInterface;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridPosition;
import pgraph.grid.GridVertex;
import pgraph.gui.GraphViewer;
import pgraph.util.GraphUtil;
import pgraph.util.IdManager;
import pgraph.util.Pen;
import pgraph.util.StringUtil;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Dindar on 10.9.2014.
 */
public class AStarGrid extends BaseDirectedGraph implements MBRunnable {

    private GridVertex start;
    private  GridVertex end;
    public int xSize=0;
    public int ySize=0;

    GridVertex grid[][]= null;

    private double unitEdgeLen;
    private Point2D.Double offSet;

    public Path nonSmoothPath = null;
    public Path path= null;
    public double solutionLength = -1;

    GridVertex grid2[][] = null;
    private boolean postSmoothingEnabled=false;
    private boolean applyPlainDijkstra = false;

    private AnyaGrid baseAnyaGrid = null;
    public DijkstraWithHeuristicShortestPath<BaseVertex, BaseEdge> spAlg = null;

    public AStarGrid(AnyaGrid ag)
    {
        baseAnyaGrid = ag;
        this.xSize = ag.xSize;
        this.ySize = ag.ySize;
        this.offSet = ag.offSet;
        this.unitEdgeLen = ag.unitEdgeLen;

        _constructGraph(ag);
    }

    public AStarGrid(AnyaGrid ag,boolean postSmoothingEnabled)
    {
        baseAnyaGrid = ag;
        this.xSize = ag.xSize;
        this.ySize = ag.ySize;
        this.offSet = ag.offSet;
        this.unitEdgeLen = ag.unitEdgeLen;
        this.postSmoothingEnabled = postSmoothingEnabled;
        _constructGraph(ag);
    }

    public void setPostSmoothingEnabled(boolean postSmoothingEnabled) {
        this.postSmoothingEnabled = postSmoothingEnabled;
    }

    /**
     * Returns 2d-coordinates of the given lattice coordinates
     * @param x
     * @param y
     * @return
     */
    public Point2D.Double getPosition(int x, int y)
    {

        double xPos = x*unitEdgeLen+ offSet.getX();
        double yPos = y*unitEdgeLen+ offSet.getY();

        return new Point2D.Double(xPos,yPos);
    }

    public GridVertex getVertex2(int x, int y, AnyaVertex.VertexDirections dir)
    {
        if ( (x>= xSize) || (y>= ySize) )
            return null;

        return grid2[x*ySize+y][dir.ordinal()];
    }

    public GridVertex getVertex2(int x, int y)
    {
        if ( (x>= xSize) || (y>= ySize) )
            return null;

        return grid2[x*ySize+y][0];
    }

    public GridVertex getVertex(int x, int y, AnyaVertex.VertexDirections dir)
    {
        if ( (x>= xSize) || (y>= ySize) )
            return null;

        return grid[x*ySize+y][dir.ordinal()];
    }




    private void _constructGraph(AnyaGrid ag)
    {

        _constructGrid2();

        _constructEdges2(ag);

    }

    private void _constructEdges2(AnyaGrid ag)
    {
        for (int x = 0; x <xSize;x++)
        {
            for (int y = 0; y <ySize;y++)
            {
                AnyaVertex anyaV = ag.getVertex(x,y);

                if (!ag.isReachable(anyaV ))
                {
                    continue;
                }

                boolean doubleCorner = false;
                if (ag.isDoubleCorner(anyaV))
                {
                    _createDoubleCornerVertices(ag,anyaV);
                    doubleCorner = true;
                }

                if (ag.isTraversable(anyaV, AnyaVertex.VertexDirections.VD_LEFT))
                {
                    AnyaVertex anyaLV= ag.getVertex(anyaV, AnyaVertex.VertexDirections.VD_LEFT);
                    boolean nextDoubleCorner = ag.isDoubleCorner(anyaLV);

                    GridVertex v = doubleCorner ? getVertex2(x,y, AnyaVertex.VertexDirections.VD_LEFT):getVertex2(x,y );
                    GridVertex nv = nextDoubleCorner ? getVertex2(x-1,y, AnyaVertex.VertexDirections.VD_RIGHT):getVertex2(x-1,y);

                    addVertex(v);
                    addVertex(nv);

                    connectVertices(v, nv);

                }

                if (ag.isTraversable(anyaV, AnyaVertex.VertexDirections.VD_DOWN))
                {
                    AnyaVertex anyaDV= ag.getVertex(anyaV, AnyaVertex.VertexDirections.VD_DOWN);
                    boolean nextDoubleCorner = ag.isDoubleCorner(anyaDV);

                    GridVertex v = doubleCorner ? getVertex2(x,y, AnyaVertex.VertexDirections.VD_DOWN):getVertex2(x,y );
                    GridVertex nv = nextDoubleCorner ? getVertex2(x,y-1, AnyaVertex.VertexDirections.VD_UP):getVertex2(x,y-1);

                    addVertex(v);
                    addVertex(nv);

                    connectVertices(v, nv);
                }

            }

        }

        for (int x = 0; x <xSize;x++)
        {
            for (int y = 0; y <ySize;y++)
            {
                AnyaVertex anyaV = ag.getVertex(x,y);

                if (!ag.isReachable(anyaV ))
                {
                    continue;
                }

                boolean doubleCorner = false;
                if (ag.isDoubleCorner(anyaV))
                {
                    doubleCorner = true;
                }

                AnyaCell c = ag.getCell(anyaV, AnyaVertex.CellDirections.CD_LEFTDOWN);
                if (c != null && c.isTraversable()) {
                    AnyaVertex anyaNV= ag.getVertex(x-1,y-1);
                    boolean nextDoubleCorner = ag.isDoubleCorner(anyaNV);

                    GridVertex nv = nextDoubleCorner ? getVertex2(x - 1, y - 1, AnyaVertex.VertexDirections.VD_UP):getVertex2(x-1,y-1);
                    GridVertex v  = doubleCorner ? getVertex2(x, y, AnyaVertex.VertexDirections.VD_DOWN): getVertex2(x,y);
                    addVertex(nv);
                    addVertex(v);

                    connectVertices(nv, v);
                }


                c = ag.getCell(anyaV, AnyaVertex.CellDirections.CD_LEFTUP);
                if (c != null && c.isTraversable()) {
                    AnyaVertex anyaNV= ag.getVertex(x-1,y+1);
                    boolean nextDoubleCorner = ag.isDoubleCorner(anyaNV);

                    GridVertex nv = nextDoubleCorner ? getVertex2(x - 1, y + 1, AnyaVertex.VertexDirections.VD_DOWN):getVertex2(x-1,y+1);
                    GridVertex v  = doubleCorner ? getVertex2(x, y, AnyaVertex.VertexDirections.VD_UP): getVertex2(x,y);
                    addVertex(nv);
                    addVertex(v);

                    connectVertices(nv, v);
                }
            }

        }
    }

    /*
    *
    *
    * */


    private void _createDoubleCornerVertices(AnyaGrid ag, AnyaVertex anyaV)
    {
        int x= anyaV.gridPos.getX();
        int y= anyaV.gridPos.getY();
        grid2[x*ySize+y][0] = new GridVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));
        grid2[x*ySize+y][1] = new GridVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));
        grid2[x*ySize+y][2] = new GridVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));
        grid2[x*ySize+y][3] = new GridVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));

        GridVertex avR= grid2[x*ySize+y][AnyaVertex.VertexDirections.VD_RIGHT.ordinal()];
        GridVertex avL= grid2[x*ySize+y][AnyaVertex.VertexDirections.VD_LEFT.ordinal()];
        GridVertex avD= grid2[x*ySize+y][AnyaVertex.VertexDirections.VD_DOWN.ordinal()];
        GridVertex avU= grid2[x*ySize+y][AnyaVertex.VertexDirections.VD_UP.ordinal()];

        if (ag.getCell(anyaV, AnyaVertex.CellDirections.CD_RIGHTUP).isTraversable())
        {
            connectVertices(avL,avD);
            connectVertices(avR,avU);
        }
        else
        {
            connectVertices(avL, avU);
            connectVertices(avR, avD);
        }

    }

    private void _constructEdges(AnyaGrid ag) {
        for (int x = 0; x <xSize;x++)
        {
            for (int y = 0; y <ySize;y++)
            {
                AnyaVertex anyaV = ag.getVertex(x,y);

                if (!ag.isReachable(anyaV ))
                {
                    continue;
                }

                _createInVertexEdges(ag,anyaV);

                if (ag.isTraversable(anyaV, AnyaVertex.VertexDirections.VD_LEFT))
                {
                    GridVertex lvr = getVertex(x-1,y,AnyaVertex.VertexDirections.VD_RIGHT);
                    GridVertex vl = getVertex(x,y,AnyaVertex.VertexDirections.VD_LEFT);
                    addVertex(lvr);
                    addVertex(vl);

                    connectVertices(lvr, vl);

                }
                if (ag.isTraversable(anyaV, AnyaVertex.VertexDirections.VD_RIGHT))
                {
                    GridVertex rvl = getVertex(x+1,y,AnyaVertex.VertexDirections.VD_LEFT);
                    GridVertex vr = getVertex(x,y,AnyaVertex.VertexDirections.VD_RIGHT);
                    addVertex(rvl);
                    addVertex(vr);

                    connectVertices(rvl, vr);
                }
                if (ag.isTraversable(anyaV, AnyaVertex.VertexDirections.VD_UP))
                {
                    GridVertex uvd = getVertex(x,y+1,AnyaVertex.VertexDirections.VD_DOWN);
                    GridVertex vu = getVertex(x,y,AnyaVertex.VertexDirections.VD_UP);
                    addVertex(uvd);
                    addVertex(vu);

                    connectVertices(uvd, vu);
                }
                if (ag.isTraversable(anyaV, AnyaVertex.VertexDirections.VD_DOWN))
                {
                    GridVertex dvu = getVertex(x,y-1,AnyaVertex.VertexDirections.VD_UP);
                    GridVertex vd = getVertex(x,y,AnyaVertex.VertexDirections.VD_DOWN);
                    addVertex(dvu);
                    addVertex(vd);

                    connectVertices(dvu, vd);
                }

                AnyaCell c= ag.getCell(anyaV, AnyaVertex.CellDirections.CD_LEFTDOWN);
                if (c!=null && c.isTraversable())
                {
                    GridVertex tv = getVertex(x-1,y-1,AnyaVertex.VertexDirections.VD_UP);
                    GridVertex v = getVertex(x,y,AnyaVertex.VertexDirections.VD_DOWN);
                    addVertex(tv);
                    addVertex(v);

                    connectVertices(tv, v);
                }


                c = ag.getCell(anyaV, AnyaVertex.CellDirections.CD_LEFTUP);
                if (c!=null && c.isTraversable())
                {
                    GridVertex tv = getVertex(x-1,y+1,AnyaVertex.VertexDirections.VD_DOWN);
                    GridVertex v = getVertex(x,y,AnyaVertex.VertexDirections.VD_UP);
                    addVertex(tv);
                    addVertex(v);

                    connectVertices(tv, v);
                }


                c= ag.getCell(anyaV, AnyaVertex.CellDirections.CD_RIGHTDOWN);
                if (c!=null && c.isTraversable())
                {
                    GridVertex tv = getVertex(x+1,y-1,AnyaVertex.VertexDirections.VD_UP);
                    GridVertex v = getVertex(x,y,AnyaVertex.VertexDirections.VD_DOWN);
                    addVertex(tv);
                    addVertex(v);

                    connectVertices(tv, v);
                }

                c = ag.getCell(anyaV, AnyaVertex.CellDirections.CD_RIGHTUP);
                if (c!=null && c.isTraversable())
                {
                    GridVertex tv = getVertex(x+1,y+1,AnyaVertex.VertexDirections.VD_DOWN);
                    GridVertex v = getVertex(x,y,AnyaVertex.VertexDirections.VD_UP);
                    addVertex(tv);
                    addVertex(v);

                    connectVertices(tv, v);
                }

            }
        }
        start = getVertex(ag.start.gridPos.getX(),ag.start.gridPos.getY(), AnyaVertex.VertexDirections.VD_UP);
        end = getVertex(ag.end.gridPos.getX(),ag.end.gridPos.getY(), AnyaVertex.VertexDirections.VD_UP);
    }

    private void connectVertices(BaseVertex v1, BaseVertex v2)
    {
        addEdge(v1,v2);
        addEdge(v2,v1);
    }

    private void _createInVertexEdges(AnyaGrid ag, AnyaVertex anyaV) {
        GridVertex avL = getVertex(anyaV.gridPos.getX(),anyaV.gridPos.getY(), AnyaVertex.VertexDirections.VD_LEFT);
        addVertex(avL);
        GridVertex avR = getVertex(anyaV.gridPos.getX(),anyaV.gridPos.getY(), AnyaVertex.VertexDirections.VD_RIGHT);
        addVertex(avR);
        GridVertex avD = getVertex(anyaV.gridPos.getX(),anyaV.gridPos.getY(), AnyaVertex.VertexDirections.VD_DOWN);
        addVertex(avD);
        GridVertex avU = getVertex(anyaV.gridPos.getX(),anyaV.gridPos.getY(), AnyaVertex.VertexDirections.VD_UP);
        addVertex(avU);
        if (!ag.isDoubleCorner(anyaV))
        {
            connectVertices(avL,avR);
            connectVertices(avD,avU);
            connectVertices(avL,avD);
            connectVertices(avL,avU);
            connectVertices(avR,avD);
            connectVertices(avR,avU);
        }
        else if (ag.getCell(anyaV, AnyaVertex.CellDirections.CD_RIGHTUP).isTraversable())
        {
            connectVertices(avL,avD);
            connectVertices(avR,avU);
        }
        else
        {
            connectVertices(avL, avU);
            connectVertices(avR, avD);
        }
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

        BaseEdge e =  getEdge(sourceVertex,targetVertex );

        if (e!=null )
            return e;

        LineEdge le = new LineEdge(IdManager.getEdgeId(),sourceVertex,targetVertex);
        addEdge(sourceVertex,targetVertex,le);

        return le;
    }

    private void _constructGrid2()
    {
        grid2 = new GridVertex[xSize*ySize][4];

        for (int x = 0; x <xSize;x++)
        {
            for (int y = 0; y <ySize;y++)
            {
                GridVertex v = new GridVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));

                grid2[x*ySize+y][0] = v;
            }
        }
    }


    private void _constructGrid()
    {
        grid = new GridVertex[xSize*ySize][4];

        for (int x = 0; x <xSize;x++)
        {
            for (int y = 0; y <ySize;y++)
            {
                GridVertex avL = new GridVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));
                GridVertex avR = new GridVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));
                GridVertex avD = new GridVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));
                GridVertex avU = new GridVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));
                grid[x*ySize+y][AnyaVertex.VertexDirections.VD_LEFT.ordinal()] = avL;
                grid[x*ySize+y][AnyaVertex.VertexDirections.VD_RIGHT.ordinal()] = avR;
                grid[x*ySize+y][AnyaVertex.VertexDirections.VD_DOWN.ordinal()] = avD;
                grid[x*ySize+y][AnyaVertex.VertexDirections.VD_UP.ordinal()] = avU;
            }
        }
    }

    public void solve() {
        if (start ==null || end == null)
            return;

        solve(start.gridPos.getX(),start.gridPos.getY(),end.gridPos.getX(),end.gridPos.getY());

    }

    private void _connectDoubleCornerEdges(int x , int y)
    {
        GridVertex avL = getVertex2(x,y, AnyaVertex.VertexDirections.VD_LEFT);
        addVertex(avL);
        GridVertex avR = getVertex2(x,y, AnyaVertex.VertexDirections.VD_RIGHT);
        addVertex(avR);
        GridVertex avD = getVertex2(x,y, AnyaVertex.VertexDirections.VD_DOWN);
        addVertex(avD);
        GridVertex avU = getVertex2(x,y, AnyaVertex.VertexDirections.VD_UP);
        addVertex(avU);

        connectVertices(avL,avR);
        connectVertices(avD,avU);
        connectVertices(avL,avD);
        connectVertices(avL,avU);
        connectVertices(avR,avD);
        connectVertices(avR,avU);
    }

    private void _reAdjustDoubleCornerEdges(int x , int y)
    {
        GridVertex avL = getVertex2(x,y, AnyaVertex.VertexDirections.VD_LEFT);
        addVertex(avL);
        GridVertex avR = getVertex2(x,y, AnyaVertex.VertexDirections.VD_RIGHT);
        addVertex(avR);
        GridVertex avD = getVertex2(x,y, AnyaVertex.VertexDirections.VD_DOWN);
        addVertex(avD);
        GridVertex avU = getVertex2(x,y, AnyaVertex.VertexDirections.VD_UP);
        addVertex(avU);

        AnyaVertex anyaV = baseAnyaGrid.getVertex(x,y);

        if (!baseAnyaGrid.isDoubleCorner(anyaV))
            return;

        removeAllEdges(avL,avR);
        removeAllEdges(avR,avL);
        removeAllEdges(avD,avU);
        removeAllEdges(avU,avD);

        if (baseAnyaGrid.getCell(anyaV, AnyaVertex.CellDirections.CD_RIGHTUP).isTraversable())
        {
            removeAllEdges(avL,avU);
            removeAllEdges(avU,avL);
            removeAllEdges(avR,avD);
            removeAllEdges(avD,avR);
        }
        if (baseAnyaGrid.getCell(anyaV, AnyaVertex.CellDirections.CD_RIGHTDOWN).isTraversable())
        {
            removeAllEdges(avL,avD);
            removeAllEdges(avD,avL);
            removeAllEdges(avR,avU);
            removeAllEdges(avU,avR);
        }


    }

    public Path solve(int startX, int startY, int endX, int endY) {

        BaseVertex start = getVertex2(startX,startY);
        BaseVertex end = getVertex2(endX,endY);

        AnyaVertex anyaStart = baseAnyaGrid.getVertex(startX,startY);
        AnyaVertex anyaEnd = baseAnyaGrid.getVertex(endX,endY);

        if (baseAnyaGrid.isDoubleCorner(anyaStart))
            _connectDoubleCornerEdges(startX,startY);

        if (baseAnyaGrid.isDoubleCorner(anyaEnd))
            _connectDoubleCornerEdges(endX,endY);

        GraphPath<BaseVertex,BaseEdge> gp = null;

        if (!applyPlainDijkstra)
        {
            spAlg = new DijkstraWithHeuristicShortestPath<BaseVertex, BaseEdge>(this, start, end, new OctileDistanceHeuristic<BaseVertex>(end));
        }
        else
        {
            spAlg = new DijkstraWithHeuristicShortestPath<BaseVertex, BaseEdge>(this,start,end,new NullHeuristic<BaseVertex>());
        }

        gp = spAlg.getPath();
        solutionLength = -1;
        if (gp != null)
        {
            nonSmoothPath = new Path(gp,new Pen(Color.green));
            if (postSmoothingEnabled)
                gp = smoothPath(gp);
            path  = new Path(gp,new Pen(Color.blue));
            solutionLength = GraphUtil.recalculatePathWeight(this,gp);
        }


        if (baseAnyaGrid.isDoubleCorner(anyaStart))
            _reAdjustDoubleCornerEdges(startX,startY);

        if (baseAnyaGrid.isDoubleCorner(anyaEnd))
            _reAdjustDoubleCornerEdges(endX,endY);


        return path;
    }

    private GraphPath<BaseVertex, BaseEdge> smoothPath(GraphPath<BaseVertex, BaseEdge> gp) {

        java.util.List<BaseEdge> el = gp.getEdgeList();
        java.util.List<BaseEdge> nel = new ArrayList<>();

        BaseEdge nextEdge = null;
        GridVertex  currentVertex =(GridVertex)gp.getStartVertex();

        double weight = 0;
        for (int i =1 ;i<el.size();i++)
        {
            nextEdge = el.get(i);
            GridVertex nv = (GridVertex)nextEdge.end;
            if (!baseAnyaGrid.isVisible(currentVertex.gridPos.getX(),currentVertex.gridPos.getY(),nv.gridPos.getX(),nv.gridPos.getY()))
            {
                BaseEdge newEdge = new LineEdge(IdManager.getEdgeId(),currentVertex,nextEdge.start);
                nel.add(newEdge);
                weight += newEdge.getEdgeWeight();
                currentVertex= (GridVertex)nextEdge.start;
            }
        }
        BaseEdge newEdge = new LineEdge(IdManager.getEdgeId(),currentVertex,gp.getEndVertex());
        nel.add(newEdge);
        weight += newEdge.getEdgeWeight();


        GraphPath<BaseVertex,BaseEdge> sp = new GraphPathImpl<BaseVertex, BaseEdge>(gp.getGraph(),gp.getStartVertex(),gp.getEndVertex(),nel,weight);

        return sp;
    }


    public void setStart(int startX, int startY) {
        start = getVertex2(startX,startY);
    }

    public void setEnd(int endX, int endY) {
        end = getVertex2(endX,endY);
    }


    @Override
    public void run() {
        solve();

    }

    @Override
    public void cleanUp() {

    }

    public static void main(String[] args) throws IOException {
    	testFibHeap();
        //testScenario();
        test2();

    }

    public static void test1() throws IOException {
        AnyaMapLoader mapLoader = new AnyaMapLoader();
        AStarMapLoader asMapLoader = new AStarMapLoader(mapLoader);

        AnyaGrid ag = mapLoader.loadMap("maps/dao/arena.map");

        ag.setStart(ag.getVertex(1,11));
        ag.setEnd(ag.getVertex(21,17));

        AStarGrid asg = new AStarGrid(ag);

        asg.solve(1,11,21,17);

        ag.upperBoundPaths.add(asg.path);

        System.out.println("S: " + asg.solutionLength);

        GraphViewer.showContent(ag);


    }

    public static void testFibHeap()
    {
        FibonacciHeap<Integer> heap = new FibonacciHeap<Integer>();
        FibonacciHeapNode<Integer> node1 =
            new FibonacciHeapNode<Integer>(new Integer(42));
        FibonacciHeapNode<Integer> node2 =
            new FibonacciHeapNode<Integer>(new Integer(42));
        FibonacciHeapNode<Integer> node3 =
            new FibonacciHeapNode<Integer>(new Integer(42));
        FibonacciHeapNode<Integer> node4 =
            new FibonacciHeapNode<Integer>(new Integer(42));

        heap.insert(node1, 100, 50);
        heap.insert(node2, 100, 55);
        heap.insert(node3, 99, 0);
        heap.insert(node4, 101, 10);

        boolean result = false;

        FibonacciHeapNode<Integer> min = heap.removeMin();
        result = min.getKey() == 99;
        System.err.println("testFibHeap: insert + removeMin: " 
                + (result?"Pass":"Fail"));
        assert(result);

        min = heap.removeMin();
        result = min.getKey() == 100 && min.getSecondaryKey() == 55;
        System.err.println("testFibHeap: removeMin (tiebreak): " + 
                (result?"Pass":"Fail"));
        assert(result);

        heap.decreaseKey(node4, 100, 60);
        min = heap.removeMin();
        result = min.getKey() == 100 && min.getSecondaryKey() == 60;
        System.err.println("testFibHeap: update + removeMin (tiebreak): " 
                + (result?"Pass":"Fail"));
        assert(result);

        System.err.println("testFibHeap: all tests complete.");
    }
    
    public static void testScenario()
    {
        //String mapFile = new String("/Users/dharabor/src/anya/trunk/experiments/scenarios/dao/orz701d.map.scen");
    	//String scenarioFile = new String("/Users/dharabor/src/anya/trunk/experiments/scenarios/dao/orz701d.map.scen");
    	String scenarioFile = new String("/Users/dharabor/src/anya/trunk/src/test.map.scen");
        AStarExperimentLoader expLoader = new AStarExperimentLoader();

		try {
	        java.util.List<ExperimentInterface> experiments = expLoader.loadExperiments(scenarioFile);
	        AStarMapLoader mapLoader = new AStarMapLoader(new AnyaMapLoader());
            //AStarExperimentRunner expRunner = new AStarExperimentRunner(mapLoader, false);

            System.out.println("Running scenario "+scenarioFile);
            //for (int i = 0; i < experiments.size() ; i++)
          	for (int i = 26; i < 27 ; i++)
            {
                ExperimentInterface exp = experiments.get(i);
                AStarGrid asg = mapLoader.loadMap(exp.getMapFile());
                asg.solve(exp.getStartX(), exp.getStartY(), exp.getEndX(), exp.getEndY());

                Calendar cal = Calendar.getInstance();
                cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTime =  sdf.format(cal.getTime()) ;

                DecimalFormat nf = StringUtil.getMyDecimalFormatter();


                double solution = asg.solutionLength;
                double duration = 0; // blah
                long expanded = asg.spAlg.iter.expandedNodes;
                long generated = asg.spAlg.iter.generatedNodes;

                String outputLine =  currentTime + ";"+
                        duration + ";"+
                        expanded + ";"+
                        generated + ";"+
                        exp.getMapFile() +";"+
                        "("+ exp.getStartX()+","+ exp.getStartY() + ")" +";"+
                        "(" + exp.getEndX() + "," + exp.getEndY() + ")" +";"+
                        nf.format(exp.getUpperBound())  +";"+
                        nf.format(solution)+";";
                if (solution-exp.getUpperBound()>0.1)
                    outputLine+= "FAILED";
                else outputLine+= "SUCCESS";

                System.out.println("exp " + i + " " + outputLine);
            }
            System.out.println("done");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void test2()
    {
        TimerUtil.setEnabled(true);

        AnyaGrid ag = new AnyaGrid(10,10,1,new Point2D.Double(0,0),5,0,7,9);
        ag.DebugTraceInterval=Long.MAX_VALUE;

        AnyaObstacleGenerator aog = new RandomAOG(0.20,"anyaRandom2.txt");

        aog.generate(ag);

        AStarGrid asg = new AStarGrid(ag,true);

        TimerUtil.start("A-STAR");
        Path p =  asg.solve(ag.start.gridPos.getX(),ag.start.gridPos.getY(),ag.end.gridPos.getX(),ag.end.gridPos.getY());
        TimerUtil.stop("A-STAR");

        double s = (p!=null) ? GraphUtil.recalculatePathWeight(asg,p.getPath()):-1;

        ag.upperBoundPaths.add(asg.nonSmoothPath);
        ag.upperBoundPaths.add(p);




        ag.setAllowRepeatingNodes(false);


        ag.setShowIntervals(false);

        TimerUtil.start("ANYA");
        ag.search_best_first(100000);
        TimerUtil.stop("ANYA");

        TimerUtil.printTotal();

        System.out.println("ANYA Result: "+ ag.getPathLength() + "A-STAR Result: "+  s);

        GraphViewer.showContent(ag);
    }


    public void setApplyPlainDijkstra(boolean applyPlainDijkstra) {
        this.applyPlainDijkstra = applyPlainDijkstra;
    }
}
