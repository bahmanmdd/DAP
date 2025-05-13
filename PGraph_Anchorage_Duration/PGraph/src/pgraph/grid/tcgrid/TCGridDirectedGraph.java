package pgraph.grid.tcgrid;

import org.jgrapht.GraphPath;
import pgraph.*;
import pgraph.intersectionhandler.IntersectionHandler;
import pgraph.intersectionhandler.StandardIntersectionHandler;
import pgraph.util.*;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridPosition;
import pgraph.grid.GridVertex;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

/**
 * Turn Constrained Lattice Graph
 */
public class TCGridDirectedGraph extends BaseDirectedGraph
{
    /**
     * Minimum radiu of turn
     */
    public double minTurnRadius;

    public double getMaxTurnAngle() {
        return maxTurnAngle;
    }



    private final double maxTurnAngle;
    /**
     * Template Lattice
     */
    public GridDirectedGraph template = null;

    HashMap<BaseEdge,List<Obstacle>> intersectedObstacles=new HashMap<BaseEdge,List<Obstacle>>();

    TCGridVertex start;
    TCGridVertex end;

    /**
     * Hashmaps used in turn constrained vertice creation
     */
    HashMap<Quadriple<GridPosition, GridPosition ,GridPosition,GridPosition>,TCGridVertex> vertex4Map = new  HashMap<Quadriple<GridPosition,GridPosition, GridPosition,GridPosition>,TCGridVertex>();
    HashMap<Triple<GridPosition, GridPosition ,GridPosition>,TCGridVertex> vertex3Map = new  HashMap<Triple<GridPosition,GridPosition, GridPosition>,TCGridVertex>();

    double straightPathLen =0;

    public IntersectionHandler getIntersectionHandler() {
        return intersectionHandler;
    }

    public void setIntersectionHandler(IntersectionHandler intersectionHandler) {
        this.intersectionHandler = intersectionHandler;
    }

    IntersectionHandler intersectionHandler = new StandardIntersectionHandler();

    public TCGridVertex getStart() {
        return start;
    }

    public TCGridVertex getEnd() {
        return end;
    }

    public void save(String fileName) throws IOException
    {
    	BufferedWriter br  = new BufferedWriter(new FileWriter(fileName));
    	br.write(template.offSet.toString()); br.newLine();
    	br.write(template.xSize+"");br.newLine();
    	br.write(template.ySize+"");br.newLine();
    	br.write(template.unitEdgeLen+"");br.newLine();

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

    public void load(String fileName,int d,double  obstacleCost,double mtr) throws NumberFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {

    	BufferedReader br = new BufferedReader(new FileReader(fileName));
        Point2D.Double offSet = StringUtil.pointFromString(br.readLine());
    	int xSize = Integer.parseInt(br.readLine());
    	int ySize = Integer.parseInt(br.readLine());
    	double unitEdgeLen = Double.parseDouble(br.readLine());

    	int sx = Integer.parseInt(br.readLine());
    	int sy = Integer.parseInt(br.readLine());

    	int tx = Integer.parseInt(br.readLine());
    	int ty = Integer.parseInt(br.readLine());

        template = new GridDirectedGraph(d,xSize,ySize,unitEdgeLen,offSet,sx,sy,tx,ty);

        minTurnRadius = mtr;



        _createVertices();
        vertex4Map.clear();  vertex3Map.clear();

    	int len = Integer.parseInt(br.readLine());
    	for(int i = 0; i < len; i++){
    		String str = br.readLine();
            Obstacle o = new Obstacle(IdManager.getObstacleId(),obstacleCost,str);
    		addObstacle(o);
    	}
    	br.close();
    }



    public TCGridDirectedGraph(String fileName, int d, double obstacleCost, double mtr, double maxTurnAngle) throws NumberFormatException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
    {
        this.maxTurnAngle = maxTurnAngle;
        load(fileName,d,obstacleCost,mtr);
    }


	public TCGridDirectedGraph(double minTurnRadius, int d, int x, int y, double el, Point2D.Double offset, int sX, int sY, int tX, int tY, double maxTurnAngle)
	{
        this.maxTurnAngle = maxTurnAngle;
        template = new GridDirectedGraph(d,x,y,el,offset,sX,sY,tX,tY);
		_createVertices();
	}
	
	public Point2D.Double getPosition(int x, int y)
	{
		double xPos = x*template.unitEdgeLen+ template.offSet.getX();
		double yPos = y*template.unitEdgeLen+ template.offSet.getY();
		
		return new Point2D.Double(xPos,yPos);
	}
    public Point2D.Double getPosition2D(int x, int y)
    {
        double xPos = x*template.unitEdgeLen+ template.offSet.getX();
        double yPos = y*template.unitEdgeLen+ template.offSet.getY();

        return new Point2D.Double(xPos,yPos);
    }

    void _updateEdges(Obstacle o) throws InstantiationException {
		TreeSet<BaseEdge> edges=null;
        for(BaseEdge e: edgeSet){
            if (e == null)
                continue;
            if (e.end == null)
                continue;
            Point2D.Double v1Pos = getPosition(((GridVertex)e.start).gridPos.getX(), ((GridVertex)e.start).gridPos.getY());
            Point2D.Double v2Pos = getPosition(((GridVertex)e.end).gridPos.getX(), ((GridVertex)e.end).gridPos.getY());

            int ic = o.lineIntersectionCount((LineEdge)e );
            if (  ic > 0)
            {
                e.pen = Pen.IntersectingEdgePen;
                e.weight = e.weight +intersectionHandler.getIntersectionPenalty(this,e,o,ic);
                if (!intersectedObstacles.containsKey(e.id))
                    intersectedObstacles.put(e,new ArrayList<Obstacle>());
                intersectedObstacles.get(e).add(o);
            }
		}
	}
	

	public void addObstacle(Obstacle o) throws InstantiationException {
        obstacles.add(o);
		_updateEdges(o);
	}

    public void addObstacles(List<? extends Obstacle> oList) throws InstantiationException {
        for(Obstacle o:oList)
            addObstacle(o);
    }


	public GridVertex getTemplateVertex(int x, int y)
	{
		if ( (x>= template.xSize) || (y>= template.ySize) )
			return null;
		
		return template.grid[x*template.ySize+y];
			
	}

    double _calculateEuclidian(int d)
    {
         return (Math.sqrt((d * d) + 1)*template.unitEdgeLen);
    }


    public static final double TURN_COST_COEFFICIENT = 1;         // The cost of turn must be adjusted here
    public double getTurnCost(double angle)
    {
        return Math.abs(angle)*template.getUnitEdgeLen()*TURN_COST_COEFFICIENT ;
    }

    TCGridVertex _createVertex4(GridPosition gridPos,TCGridVertex currentVertex,TCGridVertex ltv,PriorityQueue<TCGridVertex> vQueue)
    {
        return _createVertex4(gridPos, currentVertex, ltv, vQueue, 0, true);
    }

    TCGridVertex _createVertex4(GridPosition gridPos,TCGridVertex currentVertex,TCGridVertex ltv,PriorityQueue<TCGridVertex> vQueue,double turnCost)
    {
        return _createVertex4(gridPos, currentVertex, ltv, vQueue, turnCost, true);
    }

    TCGridVertex _createVertex4(GridPosition gridPos,TCGridVertex currentVertex,TCGridVertex ltv)
    {
        return _createVertex4(gridPos, currentVertex, ltv, null, 0, false);
    }

    static long savedVertexCount =0;

    TCGridVertex _createVertex3_1(GridPosition gridPos,TCGridVertex currentVertex,TCGridVertex ltv,PriorityQueue<TCGridVertex> vQueue, double turnCost ,boolean add2Queue )
    {
        GridPosition ltv_p_gridPos = ltv.equals(TCGridVertex.NullVertex) ? ltv.gridPos: ltv.previous.gridPos;
        GridPosition ltv_gridPos = ltv.gridPos;
        Triple<GridPosition,GridPosition, GridPosition> q = new Triple< GridPosition, GridPosition, GridPosition>(gridPos,ltv_gridPos,ltv_p_gridPos);
        TCGridVertex nv = null;
        if (!vertex3Map.containsKey(q))
        {
            nv = new TCGridVertex(IdManager.getVertexId(),template.getPosition(gridPos.getX(),gridPos.getY()),gridPos,currentVertex,ltv);
            vertex3Map.put(q, nv);
            addVertex(nv);
            if (add2Queue)
                vQueue.add(nv);
        }
        else {
            nv = vertex3Map.get(q);
            if ((savedVertexCount++) %10000 ==1)
                System.out.println("Total Vertex Count: "+vertices.size()+"     Saved Vertex Count: "+savedVertexCount);

        }

        LineEdge ne = new LineEdge(IdManager.getEdgeId(),currentVertex,nv);
        ne.otherCost = turnCost;
        addEdge(currentVertex,nv,ne);
        return nv;
    }

    TCGridVertex _createVertex4(GridPosition gridPos,TCGridVertex currentVertex,TCGridVertex ltv,PriorityQueue<TCGridVertex> vQueue, double turnCost ,boolean add2Queue )
    {
        GridPosition ltv_p_gridPos = ltv.previous == null ? null:ltv.previous.gridPos;
        GridPosition ltv_gridPos = ltv.equals(TCGridVertex.NullVertex)? null:ltv.gridPos;
        Quadriple<GridPosition,GridPosition, GridPosition,GridPosition> q = new Quadriple<GridPosition, GridPosition, GridPosition, GridPosition>(gridPos,currentVertex.gridPos,ltv_gridPos,ltv_p_gridPos);
        TCGridVertex nv = null;
        if (!vertex4Map.containsKey(q))
        {
            nv = new TCGridVertex(IdManager.getVertexId(),template.getPosition(gridPos.getX(),gridPos.getY()),gridPos,currentVertex,ltv);
            vertex4Map.put(q, nv);
            addVertex(nv);
            if (add2Queue)
                vQueue.add(nv);
        }
        else nv = vertex4Map.get(q);
        LineEdge ne = new LineEdge(IdManager.getEdgeId(),currentVertex,nv);
        ne.otherCost = turnCost;
        addEdge(currentVertex,nv,ne);
        return nv;
    }

    TCGridVertex _createVertex3(GridPosition gridPos,TCGridVertex currentVertex,TCGridVertex ltv,PriorityQueue<TCGridVertex> vQueue )
    {
        return _createVertex3(gridPos,currentVertex,ltv,vQueue,0);
    }

    TCGridVertex _createVertex3(GridPosition gridPos,TCGridVertex currentVertex,TCGridVertex ltv,PriorityQueue<TCGridVertex> vQueue,double  turnCost )
    {
        GridPosition ltv_gridPos = ltv.equals(TCGridVertex.NullVertex)? null:ltv.gridPos;

        if (ltv.equals(currentVertex))
            ltv_gridPos = currentVertex.lastTurnVertex.gridPos;

        Triple<GridPosition,GridPosition, GridPosition> q = new Triple<GridPosition, GridPosition, GridPosition>(gridPos,currentVertex.gridPos,ltv_gridPos);
        TCGridVertex nv = null;
        if (!vertex3Map.containsKey(q))
        {
            nv = new TCGridVertex(IdManager.getVertexId(),template.getPosition(gridPos.getX(),gridPos.getY()),gridPos,currentVertex,ltv);
            vertex3Map.put(q, nv);
            addVertex(nv);
            vQueue.add(nv);
        }
        else nv = vertex3Map.get(q);
        LineEdge ne = new LineEdge(IdManager.getEdgeId(),currentVertex,nv);
        ne.otherCost = turnCost;
        addEdge(currentVertex,nv,ne);
        return nv;
    }


    TCGridVertex _createEndVertex(GridPosition gridPos,TCGridVertex currentVertex )
    {
        Triple<GridPosition, GridPosition,GridPosition> q = new Triple< GridPosition, GridPosition, GridPosition>(gridPos,null,null);
        TCGridVertex nv = null;
        if (!vertex3Map.containsKey(q))
        {
            nv = new TCGridVertex(IdManager.getVertexId(),template.getPosition(gridPos.getX(),gridPos.getY()),gridPos,TCGridVertex.NullVertex,TCGridVertex.NullVertex);
            addEndVertex(nv);
        }
        else {
            nv = vertex3Map.get(q);
            if ((savedVertexCount++) %10000 ==1)
                System.out.println("Total Vertex Count: "+vertices.size()+"     Saved Vertex Count: "+savedVertexCount);
        }
        LineEdge ne = new LineEdge(IdManager.getEdgeId(),currentVertex,nv);
        addEdge(currentVertex,nv,ne);
        return nv;
    }


    void _createVertices(TCGridVertex v,PriorityQueue<TCGridVertex> vQueue)
    {
        GridVertex tv = template.getVertex(v.gridPos.getX(),v.gridPos.getY());
        TCGridVertex nv = null;

        if (tv==template.end)
        {
            nv =_createEndVertex(tv.gridPos, v);
            if (nv != end)
                end = nv;
            return;
        }

        for (BaseEdge e:tv.getOutgoings())
        {
            GridVertex tvn = (GridVertex)e.end;



            if (v == start)
            {
                _createVertex3(tvn.gridPos, v, v, vQueue); // nextPos - current vertex - ltv
            }
            else   // v is not start
            {
                TCGridVertex ltv = v.lastTurnVertex;
                if (ltv.equals(TCGridVertex.NullVertex))  // No turn constraints
                {
                    double turnAngle = ArcMath.turnAngle(v.previous.pos.getX(),v.previous.pos.getY(),
                                                         v.pos.getX(),v.pos.getY(),
                                                         tvn.pos.getX(),tvn.pos.getY());
                    if ( MathUtil.equalDoubles(turnAngle,0))
                        _createVertex3(tvn.gridPos, v, TCGridVertex.NullVertex, vQueue);
                    else _createVertex3(tvn.gridPos, v, v, vQueue, getTurnCost(turnAngle));
                }
                else // Has Turn Contraints
                {
                    // DEBUG
                    if (v.gridPos.equals(new GridPosition(0,23)) &&
                            v.lastTurnVertex.gridPos.equals(new GridPosition(0,49))&&
                            tvn.gridPos.equals(new GridPosition(1,20)))
                        tvn = tvn;
                    // DEBUG
                    double lastTurnAngle =ltv.previous.equals(TCGridVertex.NullVertex) ? 0:ArcMath.turnAngle(ltv.previous.pos.getX(),ltv.previous.pos.getY(),
                                                             ltv.pos.getX(),ltv.pos.getY(),
                                                             v.pos.getX(),v.pos.getY());
                    double currentTurnAngle = ArcMath.turnAngle(v.previous.pos.getX(),v.previous.pos.getY(),
                            v.pos.getX(),v.pos.getY(),
                            tvn.pos.getX(),tvn.pos.getY());

                    if ( MathUtil.equalDoubles(currentTurnAngle,0))   // No turn
                    {
                        double distanceToLastTurn = ltv.pos.distance(tvn.pos);
                        if (distanceToLastTurn>straightPathLen)
                            _createVertex4(tvn.gridPos, v, TCGridVertex.NullVertex, vQueue);
                        else _createVertex4(tvn.gridPos, v, ltv, vQueue);
                    }
                    else if (lastTurnAngle == 0 || !ArcMath.sameDirection(lastTurnAngle,currentTurnAngle)) //Turn direction changed no constraints
                    {
                        double currentTurnFootLength = ArcMath.minTurnDistance(Math.abs(currentTurnAngle),minTurnRadius);
                        if (ltv.pos.distance(v.pos)<currentTurnFootLength)  // TURN CONSTRAINT VIOLATION!!!
                            continue;
                        _createVertex3(tvn.gridPos, v, v, vQueue, getTurnCost(currentTurnAngle));
                     }
                    else // Same turn direction . Has Turn constraint
                    {
                        double lastTurnFootLength = ArcMath.minTurnDistance(Math.abs(lastTurnAngle),minTurnRadius);
                        double currentTurnFootLength = ArcMath.minTurnDistance(Math.abs(currentTurnAngle),minTurnRadius);
                        if (ltv.pos.distance(v.pos)<lastTurnFootLength+currentTurnFootLength)
                            continue; // TURN CONSTRAINT VIOLATION !!!
                        else
                        {
                            _createVertex3(tvn.gridPos, v, v, vQueue, getTurnCost(currentTurnAngle));
                        }
                    }
                } // Has Turn Contraints
            } // v is not start

        }  // END OF FOR
    }


    void _createVertices_2(TCGridVertex v,PriorityQueue<TCGridVertex> vQueue)
    {
        GridVertex tv = template.getVertex(v.gridPos.getX(),v.gridPos.getY());
        TCGridVertex nv = null;

        if (tv==template.end)
        {
            nv =_createEndVertex(tv.gridPos, v);
            if (nv != end)
                end = nv;
            return;
        }

        for (BaseEdge e:tv.getOutgoings())
        {
            GridVertex tvn = (GridVertex)e.end;



            if (v == start)
            {
                _createVertex3_1(tvn.gridPos, v, v, vQueue,0,true); // nextPos - current vertex - ltv
            }
            else   // v is not start
            {
                TCGridVertex ltv = v.lastTurnVertex;
                if (ltv.equals(TCGridVertex.NullVertex))  // No turn constraints
                {
                    double turnAngle = ArcMath.turnAngle(v.previous.pos.getX(),v.previous.pos.getY(),
                            v.pos.getX(),v.pos.getY(),
                            tvn.pos.getX(),tvn.pos.getY());
                    if ( MathUtil.equalDoubles(turnAngle,0))
                        _createVertex3_1(tvn.gridPos, v, TCGridVertex.NullVertex, vQueue,0,true);
                    else _createVertex3_1(tvn.gridPos, v, v, vQueue, getTurnCost(turnAngle),true);
                }
                else // Has Turn Contraints
                {
                    // DEBUG
                    if (v.gridPos.equals(new GridPosition(0,23)) &&
                            v.lastTurnVertex.gridPos.equals(new GridPosition(0,49))&&
                            tvn.gridPos.equals(new GridPosition(1,20)))
                        tvn = tvn;
                    // DEBUG
                    double lastTurnAngle =ltv.previous.equals(TCGridVertex.NullVertex) ? 0:ArcMath.turnAngle(ltv.previous.pos.getX(),ltv.previous.pos.getY(),
                            ltv.pos.getX(),ltv.pos.getY(),
                            v.pos.getX(),v.pos.getY());
                    double currentTurnAngle = ArcMath.turnAngle(v.previous.pos.getX(),v.previous.pos.getY(),
                            v.pos.getX(),v.pos.getY(),
                            tvn.pos.getX(),tvn.pos.getY());


                    // This control is added for definin maximum turn angle 08.12.2013

                    if (maxTurnAngle>0 && Math.abs(currentTurnAngle)>maxTurnAngle)
                        continue; // MAX TURN ANGLE IS DEFINED AND IT IS EXCEEDED

                    ///

                    if ( MathUtil.equalDoubles(currentTurnAngle,0))   // No turn
                    {
                        double distanceToLastTurn = ltv.pos.distance(tvn.pos);
                        if (distanceToLastTurn>straightPathLen)
                            _createVertex3_1(tvn.gridPos, v, TCGridVertex.NullVertex, vQueue,0,true);
                        else _createVertex3_1(tvn.gridPos, v, ltv, vQueue,0,true);
                    }
                    else if (lastTurnAngle == 0 || !ArcMath.sameDirection(lastTurnAngle,currentTurnAngle)) //Turn direction changed no constraints
                    {
                        double currentTurnFootLength = ArcMath.minTurnDistance(Math.abs(currentTurnAngle), minTurnRadius);
                        if (ltv.pos.distance(v.pos)<currentTurnFootLength)  // TURN CONSTRAINT VIOLATION!!!
                            continue;
                        _createVertex3_1(tvn.gridPos, v, v, vQueue, getTurnCost(currentTurnAngle),true);
                    }
                    else // Same turn direction . Has Turn constraint
                    {
                        double lastTurnFootLength = ArcMath.minTurnDistance(Math.abs(lastTurnAngle),minTurnRadius);
                        double currentTurnFootLength = ArcMath.minTurnDistance(Math.abs(currentTurnAngle),minTurnRadius);
                        if (ltv.pos.distance(v.pos)<lastTurnFootLength+currentTurnFootLength)
                            continue; // TURN CONSTRAINT VIOLATION !!!
                        else
                        {
                            _createVertex3_1(tvn.gridPos, v, v, vQueue, getTurnCost(currentTurnAngle),true);
                        }
                    }
                } // Has Turn Contraints
            } // v is not start

        }  // END OF FOR
    }


    public boolean addEndVertex(TCGridVertex v) {
        vertex3Map.put(new Triple<GridPosition, GridPosition, GridPosition>(v.gridPos,null,null),v);
        return addVertex(v);
    }



    void _createVertices()
	{
        /*todo:*/
        straightPathLen = ArcMath.minTurnDistanceFromDegree(template.degree, minTurnRadius);

        start = new TCGridVertex(IdManager.getVertexId(),template.start.pos,template.start.gridPos,TCGridVertex.NullVertex, TCGridVertex.NullVertex);

        addVertex(start);
        PriorityQueue<TCGridVertex> vQueue = new PriorityQueue<TCGridVertex>();
        vQueue.add(start);
        while(!vQueue.isEmpty())
        {
            _createVertices_2(vQueue.poll(),vQueue);
        }
        _applyOtherCost();
	}

    @Override
    public void setObstacleCost( double c) throws InstantiationException {
		for (ObstacleInterface o : obstacles)
		{
			o.setWeight(c);
		}
		_updateIntersectingEdgeCosts();
	}

    private void _applyOtherCost()
    {
        for (BaseEdge e:edgeSet)
        {
            e.weight = e.getLength()+e.otherCost ;
        }
    }

    private void _updateIntersectingEdgeCosts() throws InstantiationException {
        for (BaseEdge e:intersectedObstacles.keySet())
        {
            List<Obstacle> oList = intersectedObstacles.get(e);
            double w = 0;
            for (Obstacle o:oList)
               w += intersectionHandler.getIntersectionPenalty(this,e,o,o.lineIntersectionCount((LineEdge)e));

            e.weight = e.getLength()+e.otherCost + w;
        }
    }

    @Override
    public int intersectingObstacleCount(GraphPath<BaseVertex,BaseEdge> path)
	{
		TreeSet<Obstacle> oList = new TreeSet<Obstacle>();
        List<BaseEdge> edges = path.getEdgeList();
        for (BaseEdge e: edges)
        {
            if (e == null) continue;
            List<Obstacle> obstacles = intersectedObstacles.get(e);
            if (obstacles != null)
                oList.addAll(obstacles);
        }

		return oList.size();
	}

    public static void main(String[] args)
    {
        Quadriple<GridPosition,GridPosition,GridPosition,GridPosition> t1 = new Quadriple<GridPosition,GridPosition,GridPosition,GridPosition>(new GridPosition(1,3),new GridPosition(1,2), null,new GridPosition(1,2));
        Quadriple<GridPosition,GridPosition,GridPosition,GridPosition> t2 = new Quadriple<GridPosition,GridPosition,GridPosition,GridPosition>(new GridPosition(1,3),new GridPosition(1,2), new GridPosition(1,2),null);
        HashMap<Quadriple<GridPosition,GridPosition,GridPosition,GridPosition>,String> map = new HashMap<Quadriple<GridPosition,GridPosition,GridPosition,GridPosition>,String>();
        map.put(t1,new String("T1"));
        map.put(t2,new String("T2"));
        System.out.println(map.get(t1));
        System.out.println(map.get(t2));
    }
}
