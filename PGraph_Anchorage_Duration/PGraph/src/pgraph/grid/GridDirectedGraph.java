package pgraph.grid;

import org.jgrapht.GraphPath;
import pgraph.*;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.intersectionhandler.IntersectionHandler;
import pgraph.intersectionhandler.StandardIntersectionHandler;
import pgraph.specialzone.BasicSpecialZone;
import pgraph.specialzone.SpecialZoneInterface;
import pgraph.specialzone.ZoneEffect;
import pgraph.specialzone.ZoneShape;
import pgraph.util.IdManager;
import pgraph.util.Pen;
import pgraph.util.StringUtil;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Lattice Graph Implementation
 */
public class GridDirectedGraph extends BaseDirectedGraph
{
    /**
     * Conncetivity Degree
     */
    public int  degree;

    /**
     * Data structure for fast accessing the vertices of the graph
     */
    public GridVertex grid[]=null;

    /**
     * Data structure for fast accessing obstacles intersecting edges of the graph
     */
    public HashMap<BaseEdge,List< ObstacleInterface>> intersectedObstacles=new HashMap<BaseEdge,List< ObstacleInterface>>();

    /**
     * Data structure for fast accessing edges intersecting a specific obstacle in the graph
     */
    public HashMap<ObstacleInterface,Set<BaseEdge>> intersectingEdges=new HashMap<ObstacleInterface,Set< BaseEdge>>();


    /**
     * Data structure for fast accessing obstacles intersecting edges of the graph
     */
    public HashMap<BaseEdge,List< SpecialZoneInterface>> coveringZones=new HashMap<BaseEdge,List< SpecialZoneInterface>>();

    /**
     * Data structure for fast accessing edges intersecting a specific obstacle in the graph
     */
    public HashMap<SpecialZoneInterface,Set<BaseEdge>> zoneCoveredEdges=new HashMap<SpecialZoneInterface,Set< BaseEdge>>();



    /**
     * Offset of the graph on the 2D-coordinate system
     */
    public Point2D.Double offSet = new Point2D.Double(0,0);

    /**
     * X size of the lattice
     */
    public int xSize;

    /**
     * Y size of the lattice
     */
    public int ySize;

    /**
     * Unit length of the lattice
     */
    public double unitEdgeLen;

    /**
     * Start of the graph
     */
    public GridVertex start;

    /**
     * End of the graph
     */
    public GridVertex end;

    /**
     * Read documentation on IntersectionHandler interface
     */
    private IntersectionHandler intersectionHandler = new StandardIntersectionHandler();


    /**
     * Getter for intersectionHandler
     * @return
     */
    public IntersectionHandler getIntersectionHandler() {
        return intersectionHandler;
    }

    /**
     * Setter for intersectionHandler
     * @param intersectionHandler
     */
    public void setIntersectionHandler(IntersectionHandler intersectionHandler) {
        this.intersectionHandler = intersectionHandler;
    }


    /**
     * Getter for start
     * @return
     */
    public GridVertex getStart() {
        return start;
    }

    /**
     * Getter for end
     * @return
     */
    public GridVertex getEnd() {
        return end;
    }

    /**
     * Saves graph to the given file
     * @return
     */
    public void save(String fileName) throws IOException
    {
    	BufferedWriter br  = new BufferedWriter(new FileWriter(fileName));
    	br.write(degree+""); br.newLine();
    	br.write(offSet.toString()); br.newLine();
    	br.write(xSize+"");br.newLine();
    	br.write(ySize+"");br.newLine();
    	br.write(unitEdgeLen+"");br.newLine();
    	
    	br.write(start.gridPos.getX()+"");br.newLine();
    	br.write(start.gridPos.getY()+"");br.newLine();
    	br.write(end.gridPos.getX()+"");br.newLine();
    	br.write(end.gridPos.getY()+"");br.newLine();


    	int len = obstacles.size();
    	br.write(len+"");br.newLine();
    	for(ObstacleInterface o:obstacles){
    		br.write(o.toString());br.newLine();
    	}
    	br.flush();
    	br.close();
    }

    public static final double DEFAULT_OBSTACLE_WEIGHT =1;

    /**
     * Loads obstacle from the given file
     * @param fileName
     * @throws NumberFormatException
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void load(String fileName) throws NumberFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
    	BufferedReader br = new BufferedReader(new FileReader(fileName));
    	offSet = StringUtil.pointFromString(br.readLine());
    	xSize = Integer.parseInt(br.readLine());
    	ySize = Integer.parseInt(br.readLine());
    	unitEdgeLen = Double.parseDouble(br.readLine());
    	
    	int sx = Integer.parseInt(br.readLine());
    	int sy = Integer.parseInt(br.readLine());
    	
    	int tx = Integer.parseInt(br.readLine());
    	int ty = Integer.parseInt(br.readLine());

    	_createVertices();
    	
    	int len = Integer.parseInt(br.readLine());
    	for(int i = 0; i < len; i++){
    		String str = br.readLine();
            Obstacle o = new Obstacle(IdManager.getObstacleId(),DEFAULT_OBSTACLE_WEIGHT,str);
    		addObstacle(o);
    	}
        String str = br.readLine();
        if (str !=null)
        {
            len = Integer.parseInt(str);
            for(int i = 0; i < len; i++){
                String zoneStr = br.readLine();
                SpecialZoneInterface zone = new BasicSpecialZone(IdManager.getZoneId(),zoneStr);
                addZone(zone);
            }
        }


        br.close();

		start = getVertex(sx, sy);
		end = getVertex(tx, ty);
    	
    }

    public int getTotalIntersectionCount(BaseEdge e) throws InstantiationException {
        if (!intersectedObstacles.containsKey(e))
            return 0;

        List<ObstacleInterface> oList = intersectedObstacles.get(e);

        int ic = 0;
        for(ObstacleInterface o:oList)
            ic += o.lineIntersectionCount((LineEdge)e);
        return ic;
    }

        /**
     * Constructor
     * @param fileName
     * @param d
     * @throws NumberFormatException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public GridDirectedGraph(String fileName, int d) throws NumberFormatException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
    {
        degree = d;
        load(fileName);

    }

    /**
     * Constructor
     * @param d
     * @param x
     * @param y
     * @param el
     * @param offset
     * @param sX
     * @param sY
     * @param tX
     * @param tY
     */
    public GridDirectedGraph(int d, int x, int y, double el, Point2D.Double offset, int sX, int sY, int tX, int tY)
	{
		degree = d;
        xSize = x;
		ySize = y;
		unitEdgeLen = el;
        this.offSet = offset;
		_createVertices();
		start = getVertex(sX, sY);
		end = getVertex(tX, tY);
	}

    /**
     * Clones graphs without obstacles
     * @return
     * @throws InstantiationException
     */
    public GridDirectedGraph cloneGraphWithoutObstacles()  throws InstantiationException
    {
        GridDirectedGraph g = new GridDirectedGraph(degree, xSize, ySize, unitEdgeLen, offSet, start.gridPos.getX(),start.gridPos.getY(),end.gridPos.getX(),end.gridPos.getY());
        return g;
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

    /**
     * Updates weights of edges intersecting the given obstacle
     * @param o
     * @throws InstantiationException
     */
    void _updateEdges(ObstacleInterface o) throws InstantiationException {
		TreeSet<BaseEdge> edges=null;
        if (!intersectingEdges.containsKey(o))
            intersectingEdges.put(o,new TreeSet<BaseEdge>());


        for(BaseEdge e :edgeSet)
        {
            if (e == null)
                continue;
            if (e.end == null)
                continue;
            int ic =o.lineIntersectionCount((LineEdge)e );
            if ( ic > 0)
            {
                e.pen = Pen.IntersectingEdgePen;
                if (!o.isPassable())
                    e.weight = Double.MAX_VALUE;
                else e.weight = e.weight + intersectionHandler.getIntersectionPenalty(this,e,o,ic);
                if (!intersectedObstacles.containsKey(e))
                    intersectedObstacles.put(e,new ArrayList<ObstacleInterface>());
                intersectedObstacles.get(e).add(o);

                intersectingEdges.get(o).add(e);
            }
        }
	}


    /**
     * Updates weights of edges intersecting the given obstacle
     * @param z
     * @throws InstantiationException
     */
    void _updateEdges(SpecialZoneInterface z) {
        TreeSet<BaseEdge> edges=null;
        if (!zoneCoveredEdges.containsKey(z))
            zoneCoveredEdges.put(z,new TreeSet<BaseEdge>());


        for(BaseEdge e :edgeSet)
        {
            if (e == null)
                continue;
            if (e.end == null)
                continue;

            if ( z.isCovering(e))
            {
                z.applyZoneEffect(e);
                if (!coveringZones.containsKey(e))
                    coveringZones.put(e,new ArrayList<SpecialZoneInterface>());
                coveringZones.get(e).add(z);

                zoneCoveredEdges.get(z).add(e);
            }
        }
    }

    /**
     * Update edge weights on obstacle removal
     * @param o
     * @throws InstantiationException
     */
    void _updateEdgesOnRemove(ObstacleInterface o) throws InstantiationException {
        TreeSet<BaseEdge> edges=null;
        if (intersectingEdges.containsKey(o))
            intersectingEdges.remove(o);


        for(BaseEdge e :edgeSet)
        {
            if (e == null)
                continue;
            if (e.end == null)
                continue;
            int ic =o.lineIntersectionCount((LineEdge)e );
            if ( ic > 0)
            {
                e.pen = Pen.DefaultPen;
                if (intersectedObstacles.containsKey(e))
                {
                    List<ObstacleInterface>oList = intersectedObstacles.get(e);
                    oList.remove(o);
                    if (oList.isEmpty())
                        intersectedObstacles.remove(e);
                }
                _updateIntersectingEdgeCost(e); // Updates the cost of e
            }
        }
    }


    /**
     * Update edge weights on obstacle removal
     * @param z
     * @throws InstantiationException
     */
    void _updateEdgesOnRemove(SpecialZoneInterface z) {
        TreeSet<BaseEdge> edges=null;
        if (zoneCoveredEdges.containsKey(z))
            zoneCoveredEdges.remove(z);


        for(BaseEdge e :edgeSet)
        {
            if (e == null)
                continue;
            if (e.end == null)
                continue;

            if ( z.isCovering(e))
            {
                if (coveringZones.containsKey(e))
                {
                    List<SpecialZoneInterface>zList = coveringZones.get(e);
                    zList.remove(z);
                    if (zList.isEmpty())
                        coveringZones.remove(e);
                }
                _updateCoveredEdgeCost(e); // Updates the cost of e
            }
        }
    }


    /**
     * Removes obstacle from graph
     * @param o
     * @throws InstantiationException
     */
    public void removeObstacle(ObstacleInterface o) throws InstantiationException {
        _updateEdgesOnRemove(o);
        obstacles.remove(o);
    }


    /**
     * Adds a new obstacle to graph
     * @param o
     * @throws InstantiationException
     */
    public void addObstacle(ObstacleInterface o) throws InstantiationException {
        obstacles.add(o);
		_updateEdges(o);
	}

    /**
     * Adds obstacles to graph
     * @param oList
     * @throws InstantiationException
     */
    public void addObstacles(List<? extends ObstacleInterface> oList) throws InstantiationException {
        for(ObstacleInterface o:oList)
            addObstacle(o);
    }

    /**
     * Adds obstacles to graph
     * @param oList
     * @throws InstantiationException
     */
    public void addObstacles(ObstacleInterface[] oList) throws InstantiationException {
        for(ObstacleInterface o:oList)
            addObstacle(o);
    }


    /**
     * Adds a new obstacle to graph
     * @param z
     * @throws InstantiationException
     */
    public void addZone(SpecialZoneInterface z) throws InstantiationException {
        specialZones.add(z);
        _updateEdges(z);
    }

    /**
     * Adds obstacles to graph
     * @param zList
     * @throws InstantiationException
     */
    public void addZones(List<? extends SpecialZoneInterface> zList) throws InstantiationException {
        for(SpecialZoneInterface z:zList)
            addZone(z);
    }

    /**
     * Adds obstacles to graph
     * @param zList
     * @throws InstantiationException
     */
    public void addZones(SpecialZoneInterface[] zList) throws InstantiationException {
        for(SpecialZoneInterface z:zList)
            addZone(z);
    }




    /**
     * Returns the vertex at the given lattice coordinates
     * @param x
     * @param y
     * @return
     */
    public GridVertex getVertex(int x, int y)
	{
		if ( (x>= xSize) || (y>= ySize) )
			return null;
		
		return grid[x*ySize+y];
			
	}

    /**
     * calculates the euclidian length of the hypotenus of (1-d)
     * @param d
     * @return
     */
    double _calculateEuclidian(int d)
    {
         return (Math.sqrt((d * d) + 1)*unitEdgeLen);
    }


    /**
     * returns the closest vertex to the given position
     * @param pos
     * @return
     */
    public GridVertex getClosestVertex(Point2D pos)
    {
       int x = (int)((pos.getX()-offSet.getX())/unitEdgeLen);
       int y = (int)((pos.getY()-offSet.getY())/unitEdgeLen);
       return getVertex(x,y);
    }

    /**
     * Creates edges of the vertex v according to the connectivity degree
     * @param v
     */
    void _createAdjacencies(GridVertex v)
    {
        int x = v.gridPos.getX();
        int y = v.gridPos.getY();

        int a = 0;
        if (y>0)
            addEdge(v,getVertex(x, y-1), new LineEdge(IdManager.getEdgeId(),v,getVertex(x, y-1))) ;
        if (y<(ySize-1))
            addEdge(v, getVertex(x, y + 1), new LineEdge(IdManager.getEdgeId(), v, getVertex(x, y + 1))) ;
        if (x>0)
            addEdge(v, getVertex(x - 1, y), new LineEdge(IdManager.getEdgeId(), v, getVertex(x - 1, y))) ;
        if (x<(xSize-1))
            addEdge(v, getVertex(x + 1, y), new LineEdge(IdManager.getEdgeId(), v, getVertex(x + 1, y))) ;

        for (int d = 1; d<=degree; d++)
        {
            if ((x-d)>= 0      && (y-1)>=0)
                addEdge(v, getVertex(x - d, y - 1), new LineEdge(IdManager.getEdgeId(), v, getVertex(x - d, y - 1))) ;
            if ((x+d)<xSize    && (y-1)>=0)
                addEdge(v, getVertex(x + d, y - 1), new LineEdge(IdManager.getEdgeId(), v, getVertex(x + d, y - 1))) ;
            if ((x-d)>= 0      && (y+1)<ySize)
                addEdge(v, getVertex(x - d, y + 1), new LineEdge(IdManager.getEdgeId(), v, getVertex(x - d, y + 1))) ;
            if ((x+d)<xSize    && (y+1)<ySize)
                addEdge(v, getVertex(x + d, y + 1), new LineEdge(IdManager.getEdgeId(), v, getVertex(x + d, y + 1))) ;


            if (d>1)
            {
                if ((x-1)>=0    && (y+d)<ySize)
                    addEdge(v, getVertex(x - 1, y + d), new LineEdge(IdManager.getEdgeId(), v, getVertex(x - 1, y + d))) ;
                if ((x-1)>=0    && (y-d)>=0)
                    addEdge(v, getVertex(x - 1, y - d), new LineEdge(IdManager.getEdgeId(), v, getVertex(x - 1, y - d))) ;
                if ((x+1)<xSize && (y+d)<ySize)
                    addEdge(v, getVertex(x + 1, y + d), new LineEdge(IdManager.getEdgeId(), v, getVertex(x + 1, y + d))) ;
                if ((x+1)<xSize && (y-d)>=0)
                    addEdge(v, getVertex(x + 1, y - d), new LineEdge(IdManager.getEdgeId(), v, getVertex(x + 1, y - d))) ;
            }
        }
    	
    }

    /**
     * Creates edges of the graph  according to the connectivity degree
     */
    void _createAdjacencies()
    {
        for (GridVertex gv:grid)
            _createAdjacencies(gv);
    }

    /**
     * Creates vertices
     */
    void _createVertices()
	{
		grid = new GridVertex[xSize*ySize];

		for (int x = 0; x <xSize;x++)
		{
			for (int y = 0; y <ySize;y++)
			{
				GridVertex gv = new GridVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));
				grid[x*ySize+y] = gv;
                vertices.add(gv);
			}
		}
		_createAdjacencies();
	}

    /**
     * Changes the passability of the given obstacle
     * @param o
     * @throws InstantiationException
     */
     public void setObstaclePassability( ObstacleInterface o, boolean b) throws InstantiationException
    {
        o.setPassable(b);

        _updateIntersectingEdgeCost(o);
    }


    /**
     * Read the documentation on the base class
     * @param c
     * @throws InstantiationException
     */
    @Override
    public void setObstacleCost( double c) throws InstantiationException {
		for (ObstacleInterface o : obstacles)
		{
			o.setWeight(c);
		}
		_updateIntersectingEdgeCosts();
	}

    public void updateIntersectingEdgeCosts() throws InstantiationException {
        _updateIntersectingEdgeCosts();
    }

    private void _updateIntersectingEdgeCost(ObstacleInterface o) throws InstantiationException
    {
        Set<BaseEdge> edges = intersectingEdges.get(o);

        for (BaseEdge e :edges)
            _updateIntersectingEdgeCost(e);
    }


    private void _updateIntersectingEdgeCost(BaseEdge e) throws InstantiationException
    {
        double w = 0;
        if (intersectedObstacles.containsKey(e))
        {
            List<ObstacleInterface> oList = intersectedObstacles.get(e);

            for (ObstacleInterface o:oList)
            {
                if (!o.isPassable())
                {
                    e.weight = Double.MAX_VALUE;
                    return;
                }
                w += intersectionHandler.getIntersectionPenalty(this, e, o, o.lineIntersectionCount((LineEdge) e));
            }
        }
        e.weight = e.getLength() + w+ e.zoneCost;
    }


    private void _updateCoveredEdgeCost(BaseEdge e)
    {
        e.zoneCost = 0;
        if (coveringZones.containsKey(e))
        {
            List<SpecialZoneInterface> zList = coveringZones.get(e);

            for (SpecialZoneInterface z :zList)
            {
                z.applyZoneEffect(e);
            }
        }
    }

    /**
     * Updates weights of intersecting edges
     * @throws InstantiationException
     */
    private void _updateIntersectingEdgeCosts() throws InstantiationException {
        for (BaseEdge e:intersectedObstacles.keySet())
        {
            _updateIntersectingEdgeCost(e);
        }
    }

    /**
     * Read the documentation on the base class
     * @param path
     * @throws InstantiationException
     */
    @Override
    public int intersectingObstacleCount(GraphPath<BaseVertex,BaseEdge> path)
	{
		TreeSet<ObstacleInterface> oList = new TreeSet<ObstacleInterface>();
        List<BaseEdge> edges = path.getEdgeList();
        for (BaseEdge e: edges)
        {
            if (e == null) continue;
            List<ObstacleInterface> obstacles = intersectedObstacles.get(e);
            if (obstacles != null)
                oList.addAll(obstacles);
        }

		return oList.size();
	}

    /**
     * Gets intersectingEdgesOf Obstacle o
     * @param o
     * @return
     */
    public Set<BaseEdge> intersectingEdgesOf(ObstacleInterface o)
    {
        return intersectingEdges.get(o);
    }

    /**
     * Getter
     * @return
     */
    public int getxSize() {
		return xSize;
	}

    /**
     * Setter
     * @return
     */
    public void setxSize(int xSize) {
		this.xSize = xSize;
	}

    /**
     * Getter
     * @return
     */
    public int getySize() {
		return ySize;
	}

    /**
     * Setter
     * @return
     */
    public void setySize(int ySize) {
		this.ySize = ySize;
	}

    /**
     * Getter
     * @return
     */
    public double getUnitEdgeLen() {
		return unitEdgeLen;
	}

    /**
     * Setter
     * @return
     */
    public void setUnitEdgeLen(double unitEdgeLen) {
		this.unitEdgeLen = unitEdgeLen;
	}
	
	
}
