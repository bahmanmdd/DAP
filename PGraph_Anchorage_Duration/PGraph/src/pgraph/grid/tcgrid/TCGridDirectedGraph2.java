package pgraph.grid.tcgrid;

import org.jgrapht.GraphPath;
import pgraph.LineEdge;
import pgraph.Obstacle;
import pgraph.ObstacleInterface;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.GridPosition;
import pgraph.grid.GridVertex;
import pgraph.intersectionhandler.IntersectionHandler;
import pgraph.intersectionhandler.StandardIntersectionHandler;
import pgraph.specialzone.SpecialZoneInterface;
import pgraph.util.*;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

/**
 * Turn Constrained Lattice Graph
 */
public class TCGridDirectedGraph2 extends BaseDirectedGraph
{
    /**
     * Minimum radiu of turn
     */
    public double minTurnRadius;
    private final double maxTurnAngle;

    public double getMaxTurnAngle() {
        return maxTurnAngle;
    }



    public double getMinTurnRadius() {
        return minTurnRadius;
    }

    public void setMinTurnRadius(double minTurnRadius) {
        this.minTurnRadius = minTurnRadius;
    }

    /**
     * Template Lattice
     */
    public GridDirectedGraph template = null;

    HashMap<BaseEdge,List<Obstacle>> intersectedObstacles=new HashMap<BaseEdge,List<Obstacle>>();

    TCGridVertex start;
    TCGridVertex end;
    int minTurnAngle ;

    /**
     * Hashmaps used in turn constrained vertice creation
     */
    HashMap<TCGridPosition,TCGridVertex> vertex3Map = new  HashMap<TCGridPosition,TCGridVertex>();



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
        vertex3Map.clear();
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

        vertex3Map.clear();

    	int len = Integer.parseInt(br.readLine());
    	for(int i = 0; i < len; i++){
            String str = br.readLine();
            Obstacle o = new Obstacle(IdManager.getObstacleId(),obstacleCost,str);
            addObstacle(o);
    	}

    	br.close();
    }



    public TCGridDirectedGraph2(String fileName, int d, double obstacleCost, double mtr, double maxTurnAngle) throws NumberFormatException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
    {
        this.maxTurnAngle = maxTurnAngle;
        load(fileName,d,obstacleCost,mtr);
    }


	public TCGridDirectedGraph2(double minTurnRadius, int d, int x, int y, double el, Point2D.Double offset, int sX, int sY, int tX, int tY, double maxTurnAngle)
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
                //e.intersectingZones.add(dz) ; // If intersects add disk cost to edge
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


    public static final double TURN_COST_COEFFICIENT = 2;
    public double getTurnCost(double angle)
    {
        return Math.abs(angle)*template.getUnitEdgeLen()*TURN_COST_COEFFICIENT ;
    }


    static long savedVertexCount =0;
    static long constraintViolatedVertexCount =0;


    TCGridVertex _createAndAddToQueue(TCGridPosition tcgridPos, TCGridVertex currentVertex, TCGridVertex ltv, PriorityQueue<TCGridVertex> vQueue, double turnCost, boolean add2Queue)
    {
        TCGridVertex nv = null;
        if (!vertex3Map.containsKey(tcgridPos))
        {
            nv = new TCGridVertex(IdManager.getVertexId(),template.getPosition(tcgridPos.getGridPos().getX(),tcgridPos.getGridPos().getY()),tcgridPos.getGridPos(),currentVertex,ltv);
            vertex3Map.put(tcgridPos, nv);
            addVertex(nv);
            if (add2Queue)
                vQueue.add(nv);
        }
        else {
            if (tcgridPos.prevGridPos==TCGridVertex.NullVertex.gridPos)
            {
                boolean aha = true;
            }


            nv = vertex3Map.get(tcgridPos);
            if ((savedVertexCount++) %10000 ==1)
                System.out.println("Total Vertex Count: "+vertices.size()+"     Saved Vertex Count: "+savedVertexCount + "     Violating Vertext Count: "+ constraintViolatedVertexCount);
        }

        LineEdge ne = new LineEdge(IdManager.getEdgeId(),currentVertex,nv);

        // TODO: Environmental conditions

        ne.otherCost = turnCost;
        processSpecialzones(ne);

        addEdge(currentVertex,nv,ne);
        return nv;
    }

    private void processSpecialzones(LineEdge ne) {
        List<SpecialZoneInterface> zones = coveringZones(ne);
        for(SpecialZoneInterface zone:zones)
        {
            zone.applyZoneEffect(ne);
        }
    }


    public static final int FULL_TURN_ANGLE = 360;

    TCGridVertex _createEndVertex(GridPosition gridPos,TCGridVertex currentVertex )
    {
        TCGridPosition p = new TCGridPosition(gridPos,TCGridVertex.NullVertex.gridPos,FULL_TURN_ANGLE);
        TCGridVertex nv = null;

        if (!vertex3Map.containsKey(p))
        {
            nv = new TCGridVertex(IdManager.getVertexId(),template.getPosition(gridPos.getX(),gridPos.getY()),gridPos,TCGridVertex.NullVertex,TCGridVertex.NullVertex);
            addEndVertex(nv);
        }
        else {
            nv = vertex3Map.get(p);
            if ((savedVertexCount++) %10000 ==1)
                System.out.println("Total Vertex Count: "+vertices.size()+"     Saved Vertex Count: "+savedVertexCount + "     Violating Vertext Count: "+ constraintViolatedVertexCount);
        }
        LineEdge ne = new LineEdge(IdManager.getEdgeId(),currentVertex,nv);
        // TODO: Environmental conditions
        addEdge(currentVertex,nv,ne);
        return nv;
    }



    int turnAngleToTurnConstraint(double maxTurnAngle)
    {
        int turnConstraint = (int) (maxTurnAngle/minTurnAngle);
        turnConstraint = turnConstraint*minTurnAngle;

        return turnConstraint;
    }

    public List<SpecialZoneInterface> coveringZones(LineEdge le)
    {
        BaseEdge e = template.getEdge(le.start,le.end);
        return template.coveringZones.get(e);
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
                _createAndAddToQueue(new TCGridPosition(tvn.gridPos, v.gridPos, FULL_TURN_ANGLE), v, v, vQueue, 0, true); // nextPos - current vertex - ltv
            }
            else   // v is not start
            {
                TCGridVertex ltv = v.lastTurnVertex;


                if (ltv.equals(TCGridVertex.NullVertex))  // No turn constraints
                {
                    throw new NullPointerException();

                }
                else // Has Turn Contraints
                {
                    // TODO: Environmental conditions. update min turn radius accordingly

                    double lastTurnAngle =ltv.previous.equals(TCGridVertex.NullVertex) ? 0:ArcMath.turnAngle(ltv.previous.pos.getX(),ltv.previous.pos.getY(),
                            ltv.pos.getX(),ltv.pos.getY(),
                            v.pos.getX(),v.pos.getY());
                    double currentTurnAngle = ArcMath.turnAngle(v.previous.pos.getX(),v.previous.pos.getY(),
                            v.pos.getX(),v.pos.getY(),
                            tvn.pos.getX(),tvn.pos.getY());

                    double lastTurnFootLength = ArcMath.minTurnDistance(Math.abs(lastTurnAngle),minTurnRadius);
                    double currentTurnFootLength = ArcMath.minTurnDistance(Math.abs(currentTurnAngle),minTurnRadius);

                    if ( MathUtil.equalDoubles(currentTurnAngle,0))   // No turn
                    {
                        double distanceToLastTurn = ltv.pos.distance(tvn.pos);
                        if (distanceToLastTurn-lastTurnFootLength>straightPathLen)
                            _createAndAddToQueue(new TCGridPosition(tvn.gridPos, v.gridPos, FULL_TURN_ANGLE), v, ltv, vQueue, 0, true);
                        else
                        {
                            double maxTurnAngle = ArcMath.maxTurnAngle(distanceToLastTurn-lastTurnFootLength,minTurnRadius);
                            int turnConstraint = (lastTurnAngle>180) ? -1*turnAngleToTurnConstraint(maxTurnAngle):turnAngleToTurnConstraint(maxTurnAngle);


                            _createAndAddToQueue(new TCGridPosition(tvn.gridPos, v.gridPos, turnConstraint), v, ltv, vQueue, 0, true);
                        }
                    }
                    else if (maxTurnAngle > 0  && Math.abs(currentTurnAngle)>maxTurnAngle)
                    {
                        // This control is added for definin maximum turn angle 08.12.2013
                        constraintViolatedVertexCount++;
                        continue; // MAX TURN ANGLE IS DEFINED AND IT IS EXCEEDED
                        ///
                    }
                    else if (lastTurnAngle == 0 || !ArcMath.sameDirection(lastTurnAngle,currentTurnAngle)) //Turn direction changed no constraints
                    {
                        if (ltv.pos.distance(v.pos)<currentTurnFootLength)  // TURN CONSTRAINT VIOLATION!!!
                        {
                            constraintViolatedVertexCount++;
                            continue;
                        }
                        double distanceToTurn = v.pos.distance(tvn.pos);

                        if (distanceToTurn < currentTurnFootLength )
                            _createAndAddToQueue(new TCGridPosition(tvn.gridPos, v.gridPos, 0), v, v, vQueue, getTurnCost(currentTurnAngle), true);

                        if (distanceToTurn-currentTurnFootLength > straightPathLen)
                            _createAndAddToQueue(new TCGridPosition(tvn.gridPos, v.gridPos, FULL_TURN_ANGLE), v, v, vQueue, getTurnCost(currentTurnAngle), true);


                        double maxTurnAngle = ArcMath.maxTurnAngle(distanceToTurn-currentTurnFootLength,minTurnRadius);
                        int turnConstraint = (currentTurnAngle>180) ? -1*turnAngleToTurnConstraint(maxTurnAngle):turnAngleToTurnConstraint(maxTurnAngle);
                        _createAndAddToQueue(new TCGridPosition(tvn.gridPos, v.gridPos, turnConstraint), v, v, vQueue, getTurnCost(currentTurnAngle), true);
                    }
                    else // Same turn direction . Has Turn constraint
                    {

                        if (ltv.pos.distance(v.pos)<lastTurnFootLength+currentTurnFootLength)
                        {
                            constraintViolatedVertexCount++;
                            continue; // TURN CONSTRAINT VIOLATION !!!

                        }
                        else
                        {
                            double distanceToTurn = v.pos.distance(tvn.pos);

                            if (distanceToTurn < currentTurnFootLength )
                                _createAndAddToQueue(new TCGridPosition(tvn.gridPos, v.gridPos, 0), v, v, vQueue, getTurnCost(currentTurnAngle), true);

                            else if (distanceToTurn-currentTurnFootLength > straightPathLen)
                                _createAndAddToQueue(new TCGridPosition(tvn.gridPos, v.gridPos, FULL_TURN_ANGLE), v, v, vQueue, getTurnCost(currentTurnAngle), true);

                            else
                            {
                                double maxTurnAngle = ArcMath.maxTurnAngle(distanceToTurn-currentTurnFootLength,minTurnRadius);
                                int turnConstraint = (currentTurnAngle>180) ? -1*turnAngleToTurnConstraint(maxTurnAngle):turnAngleToTurnConstraint(maxTurnAngle);
                                _createAndAddToQueue(new TCGridPosition(tvn.gridPos, v.gridPos, turnConstraint), v, v, vQueue, getTurnCost(currentTurnAngle), true);
                            }
                        }
                    }
                } // Has Turn Contraints
            } // v is not start

        }  // END OF FOR
    }


    public boolean addEndVertex(TCGridVertex v) {
        vertex3Map.put(new TCGridPosition(v.gridPos,TCGridVertex.NullVertex.gridPos,FULL_TURN_ANGLE),v);
        return addVertex(v);
    }



    void _createVertices()
	{
        /*todo:*/
        straightPathLen = ArcMath.minTurnDistanceFromDegree(template.degree, minTurnRadius);
        minTurnAngle = 45/template.degree;

        start = new TCGridVertex(IdManager.getVertexId(),template.start.pos,template.start.gridPos,TCGridVertex.NullVertex, TCGridVertex.NullVertex);

        addVertex(start);
        PriorityQueue<TCGridVertex> vQueue = new PriorityQueue<TCGridVertex>();
        vQueue.add(start);
        while(!vQueue.isEmpty())
        {
            _createVertices(vQueue.poll(), vQueue);
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
