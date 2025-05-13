package pgraph.anya;

import math.geom2d.conic.Circle2D;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;
import pgraph.LineEdge;
import pgraph.Path;
import pgraph.util.RandUtil;
import pgraph.util.TimerUtil;
import pgraph.anya.experiments.MBRunnable;
import pgraph.base.BaseDirectedGraph;
import pgraph.grid.GridPosition;
import pgraph.gui.BaseGraphPanel;
import pgraph.gui.GraphViewer;
import pgraph.util.IdManager;
import pgraph.util.StringUtil;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * Lattice Graph Implementation
 */
public class AnyaGrid extends BaseDirectedGraph implements MBRunnable
{
    private boolean targetFound = false;

    public boolean isReachable(AnyaVertex v) {
        return ( isTraversable( v, AnyaVertex.VertexDirections.VD_LEFT) ||
                isTraversable(v, AnyaVertex.VertexDirections.VD_RIGHT) ||
                isTraversable(v, AnyaVertex.VertexDirections.VD_DOWN) ||
                isTraversable(v, AnyaVertex.VertexDirections.VD_UP) );
    }

    @Override
    public void run() {
        search_best_first(Long.MAX_VALUE);
    }

    @Override
    public void cleanUp() {
        reset();
    }


    public enum OptimizationType {OT_NONE, OT_LEVEL1 , OT_LEVEL2, OT_LEVEL3};

    private boolean searchHappened = false;

    private static final boolean DOUBLE_CORNER_PASSABLE = false;

    private boolean allowRepeatingNodes= false;
    private boolean intervalHistoryEnabled = true;
    private boolean nodeHistoryEnabled = true;
    private boolean equalCornerPruningEnabled=false;

    public AnyaCell cells[] = null;
    public List<AnyaCell> nonTraversableCells= new ArrayList<>();

    /**
     * Data structure for fast accessing the vertices of the graph
     */
    public AnyaVertex grid[]=null;


    public ArrayList< ArrayList<AnyaNode> > searchNodes = null;

    public FibonacciHeap<AnyaNode> openList =  new FibonacciHeap<>();


    public List<Line2D.Double> path = null;

    public List<Path> upperBoundPaths= new ArrayList<>();

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
    public AnyaVertex start;

    /**
     * End of the graph
     */
    public AnyaVertex end;

    public long DebugTraceInterval=Long.MAX_VALUE;
    private String debugTraceFile= null;
    private long stepCounter=0;
    private AnyaNode lastExpandedNode= null;
    public long generatedIntervalCount=0;
    public long ignoredIntervalCount=0;
    public long visibilityCalculationCounter =0 ;


    public boolean isShowIntervals() {
        return showIntervals;
    }

    public void setShowIntervals(boolean showIntervals) {
        this.showIntervals = showIntervals;
    }

    private boolean showIntervals= false;

    public void setDebugTraceInterval(long debugTraceInterval) {
        DebugTraceInterval = debugTraceInterval;
    }

    public void setDebugTraceFile(String debugTraceFile, String caption) {
        this.debugTraceFile = debugTraceFile;

        debugTrace("----------------------------< " +caption+" >----------------------------/");
    }

    public void setEnd(AnyaVertex end) {
        this.end = end;

        //_createFirstNode();
    }

    public void setOptimizationType(OptimizationType ot)
    {
        allowRepeatingNodes = (ot == OptimizationType.OT_NONE);

        nodeHistoryEnabled = (ot == OptimizationType.OT_LEVEL2) || (ot == OptimizationType.OT_LEVEL3);
        intervalHistoryEnabled = (ot == OptimizationType.OT_LEVEL3);
    }

    public void setStart(AnyaVertex start) {
        this.start = start;

        _createFirstNode();
    }

    /**
     * Getter for start
     * @return
     */
    public AnyaVertex getStart() {
        return start;
    }

    /**
     * Getter for end
     * @return
     */
    public AnyaVertex getEnd() {
        return end;
    }

    /**
     * Saves graph to the given file
     * @return
     */
    public void save(String fileName) throws IOException
    {
    	BufferedWriter br  = new BufferedWriter(new FileWriter(fileName));
    	br.write(offSet.toString()); br.newLine();
    	br.write(xSize+"");br.newLine();
    	br.write(ySize+"");br.newLine();
    	br.write(unitEdgeLen+"");br.newLine();

    	br.write(start.gridPos.getX()+"");br.newLine();
    	br.write(start.gridPos.getY()+"");br.newLine();
    	br.write(end.gridPos.getX()+"");br.newLine();
    	br.write(end.gridPos.getY()+"");br.newLine();


    	int len = nonTraversableCells.size();
    	br.write(len+"");br.newLine();
    	for(AnyaCell o:nonTraversableCells){
    		br.write(o.toString());br.newLine();
    	}
    	br.flush();
    	br.close();
    }

    public int getRow(double rowPos)
    {
        return (int)(rowPos/unitEdgeLen);
    }

    public int getCol(double colPos)
    {
        return (int)(colPos/unitEdgeLen);
    }

    public static final double DEFAULT_OBSTACLE_WEIGHT =1;

    /**
     * Loads obstacle from the given file
     * @param fileName
     * @throws NumberFormatException
     * @throws java.io.IOException
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

        br.close();

		start = getVertex(sx, sy);
		end = getVertex(tx, ty);

        reset();

    }



        /**
     * Constructor
     * @param fileName
     * @param d
     * @throws NumberFormatException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws java.io.IOException
     */
    public AnyaGrid(String fileName) throws NumberFormatException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
    {
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
    public AnyaGrid(int x, int y, double el, Point2D.Double offset, int sX, int sY, int tX, int tY)
	{
        xSize = x;
		ySize = y;
		unitEdgeLen = el;
        this.offSet = offset;
		_createVertices();
		start = getVertex(sX, sY);
		end = getVertex(tX, tY);

        reset();

	}


    private double calculateFValue(AnyaNode n)
    {
        return calculateFValue(n.g,n.interval,n.root,end.pos);
    }

    private double calculateFValue(double g,AnyaInterval interval,Point2D.Double root,Point2D.Double target )
    {
        if (interval ==null || root==null || target== null)
            return 0;

        Line2D.Double lineToTarget = new Line2D.Double(root,target);

        double intervalY = getPosition(0,interval.getRow()).getY();


        Point2D.Double leftPoint = new Point2D.Double(interval.getLeft(),intervalY);
        Point2D.Double rightPoint = new Point2D.Double(interval.getRight(),intervalY);

        if (interval.getLeft() == interval.getRight()) // Zero Length interval
            return g + root.distance(leftPoint)+ leftPoint.distance(target);

        Line2D.Double intervalLine = new Line2D.Double(leftPoint,rightPoint);

        if (lineToTarget.intersectsLine(intervalLine)) // Line to Target intersects the interval
            return g + root.distance(target);


        double fValue =0;

        if (root.getY()<target.getY()) // MUST GO UP
        {
            if (intervalY>target.getY())
            {
                Point2D.Double mirrorTarget = new Point2D.Double(target.getX(),intervalY+(intervalY-target.getY()));
                fValue = calculateFValue(g,interval,root,mirrorTarget);
            }
            else
            {
                double ld = leftPoint.distance(target)+ leftPoint.distance(root);
                double rd = rightPoint.distance(target) + rightPoint.distance(root);
                fValue = g + Math.min(ld,rd);
            }
        }
        else
        {
            if (intervalY<target.getY())
            {
                Point2D.Double mirrorTarget = new Point2D.Double(target.getX(),intervalY-(target.getY()-intervalY));
                fValue = calculateFValue(g,interval,root,mirrorTarget);
            }
            else
            {
                double ld = leftPoint.distance(target)+ leftPoint.distance(root);
                double rd = rightPoint.distance(target) + rightPoint.distance(root);
                fValue = g + Math.min(ld,rd);
            }

        }

        return fValue;


    }

    private void _createFirstNode()
    {
        AnyaNode firstNode = new AnyaNode(  null,
                new AnyaInterval(start.pos.getX(),start.pos.getX(),start.gridPos.getY()),
                new Point2D.Double(start.pos.getX(),start.pos.getY()));

        ArrayList<AnyaNode> l = new ArrayList<>();
        l.add(firstNode);


        searchNodes = new ArrayList<>();
        searchNodes.add(l);
    }

    /**
     * Clones graphs without obstacles
     * @return
     * @throws InstantiationException
     */
    public AnyaGrid cloneGraphWithoutObstacles()  throws InstantiationException
    {
        AnyaGrid g = new AnyaGrid( xSize, ySize, unitEdgeLen, offSet, start.gridPos.getX(),start.gridPos.getY(),end.gridPos.getX(),end.gridPos.getY());
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
     * Returns the vertex at the given lattice coordinates
     * @param x
     * @param y
     * @return
     */
    public AnyaVertex getVertex(int x, int y)
	{
		if ( (x>= xSize) || (y>= ySize) )
			return null;
		
		return grid[x*ySize+y];
			
	}

    /**
     * Returns the vertex at the given lattice coordinates
     * @param x
     * @param y
     * @return
     */
    public AnyaCell getCell(int x, int y)
    {
        if ( (x>= (xSize-1)) || (y>= (ySize-1)) )
            return null;

        return cells[x*(ySize-1)+y];

    }


    public AnyaCell getCell(AnyaVertex v, AnyaVertex.CellDirections direction)
    {
        switch (direction)
        {
            case CD_LEFTDOWN: {
                if (v.gridPos.getX()==0 || v.gridPos.getY()==0)
                    return null;
                return getCell(v.gridPos.getX()-1,v.gridPos.getY()-1);
            }
            case CD_RIGHTDOWN: {
                if (v.gridPos.getX()>=xSize-1 || v.gridPos.getY()==0)
                    return null;
                return getCell(v.gridPos.getX(),v.gridPos.getY()-1);
            }
            case CD_RIGHTUP: {
                if (v.gridPos.getX()>=xSize-1 || v.gridPos.getY()>=ySize-1)
                    return null;
                return getCell(v.gridPos.getX(),v.gridPos.getY());
            }
            case CD_LEFTUP: {
                if (v.gridPos.getX()==0 || v.gridPos.getY()>=ySize-1)
                    return null;
                return getCell(v.gridPos.getX()-1,v.gridPos.getY());
            }
        }
        return null;
    }

    public AnyaVertex getVertex(AnyaVertex v, AnyaVertex.VertexDirections direction)
    {
        switch (direction)
        {
            case VD_LEFT: {
                if (v.gridPos.getX()==0 )
                    return null;

                return getVertex(v.gridPos.getX()-1,v.gridPos.getY());
            }
            case VD_RIGHT: {
                if (v.gridPos.getX()>=xSize-1 )
                    return null;
                return getVertex(v.gridPos.getX()+1,v.gridPos.getY());
            }
            case VD_DOWN: {
                if (v.gridPos.getY()==0 )
                    return null;
                return getVertex(v.gridPos.getX(),v.gridPos.getY()-1);
            }
            case VD_UP: {
                if (v.gridPos.getY()==0 )
                    return null;
                return getVertex(v.gridPos.getX(),v.gridPos.getY()+1);
            }
        }

        return null;
    }

    public AnyaVertex getVertex(double x,double y , AnyaVertex.VertexDirections direction)
    {
        int ix = (int)x/(int)unitEdgeLen;
        int iy = (int)y/(int)unitEdgeLen;

        switch (direction)
        {
            case VD_LEFT: {
                if (x < 0 )
                    return null;

                return getVertex( ix,iy);
            }
            case VD_RIGHT: {
                if ( x >=(xSize-1)*unitEdgeLen )
                    return null;
                return getVertex(ix+1,iy);
            }
            case VD_DOWN: {
                if (y < 0 )
                    return null;
                return getVertex(ix,iy);
            }
            case VD_UP: {
                if (y >=(ySize-1)*unitEdgeLen )
                    return null;
                return getVertex(ix,iy+1);
            }
        }

        return null;
    }


    public boolean isTraversable(double x,int row, AnyaVertex.VertexDirections direction)
    {
        AnyaVertex v = getVertex(x,row, AnyaVertex.VertexDirections.VD_LEFT);

        boolean discretePoint = true;
        if (x-v.pos.getX()>AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD) // Vertex is on the left of the point.
        {                                                            // So we have to look our right direction.
            discretePoint = false;
            if (direction == AnyaVertex.VertexDirections.VD_LEFT)
                direction = AnyaVertex.VertexDirections.VD_RIGHT;
        }

        if (v == null )
            return false;

        switch (direction)
        {
            case VD_LEFT: {
                if (v.gridPos.getX()==0 )
                    return false;

                AnyaCell c1 = getCell(v, AnyaVertex.CellDirections.CD_LEFTDOWN);
                AnyaCell c2 = getCell(v, AnyaVertex.CellDirections.CD_LEFTUP);

                if (c1 == null && c2 == null) return false;
                if (c1 == null)
                    return c2 != null && c2.isTraversable();
                else return c1.isTraversable() || (c2 != null && c2.isTraversable());
            }
            case VD_RIGHT: {
                if (v.gridPos.getX()>=xSize-1 )
                    return false;
                AnyaCell c1 = getCell(v, AnyaVertex.CellDirections.CD_RIGHTDOWN);
                AnyaCell c2 = getCell(v, AnyaVertex.CellDirections.CD_RIGHTUP);

                if (c1 == null && c2 == null) return false;
                if (c1 == null)
                    return c2 != null && c2.isTraversable();
                else return c1.isTraversable() || (c2 != null && c2.isTraversable());
            }
            case VD_DOWN: {
                if (v.gridPos.getY()==0 )
                    return false;
                AnyaCell c1 = getCell(v, AnyaVertex.CellDirections.CD_LEFTDOWN);
                AnyaCell c2 = getCell(v, AnyaVertex.CellDirections.CD_RIGHTDOWN);

                if (!discretePoint )
                    return c2.isTraversable();

                if (c1 == null && c2 == null) return false;
                if (c1 == null)
                    return c2 != null && c2.isTraversable();
                else return c1.isTraversable() || (c2 != null && c2.isTraversable());
            }
            case VD_UP: {
                if (v.gridPos.getY()>=ySize-1 )
                    return false;
                AnyaCell c1 = getCell(v, AnyaVertex.CellDirections.CD_LEFTUP);
                AnyaCell c2 = getCell(v, AnyaVertex.CellDirections.CD_RIGHTUP);

                if (!discretePoint)
                    return c2.isTraversable();

                if (c1 == null && c2 == null) return false;
                if (c1 == null)
                    return c2 != null && c2.isTraversable();
                else return c1.isTraversable() || (c2 != null && c2.isTraversable());
            }
        }

        return false;
    }

    public boolean isTraversable(AnyaVertex v, AnyaVertex.VertexDirections direction)
    {
        if (v == null )
            return false;
        switch (direction)
        {
            case VD_LEFT: {
                if (v.gridPos.getX()==0 )
                    return false;

                AnyaCell c1 = getCell(v, AnyaVertex.CellDirections.CD_LEFTDOWN);
                AnyaCell c2 = getCell(v, AnyaVertex.CellDirections.CD_LEFTUP);

                if (c1 == null && c2 == null) return false;
                if (c1 == null)
                    return c2 != null && c2.isTraversable();
                else return c1.isTraversable() || (c2 != null && c2.isTraversable());


        }
            case VD_RIGHT: {
                if (v.gridPos.getX()>=xSize-1 )
                    return false;
                AnyaCell c1 = getCell(v, AnyaVertex.CellDirections.CD_RIGHTDOWN);
                AnyaCell c2 = getCell(v, AnyaVertex.CellDirections.CD_RIGHTUP);

                if (c1 == null && c2 == null) return false;
                if (c1 == null)
                    return c2 != null && c2.isTraversable();
                else return c1.isTraversable() || (c2 != null && c2.isTraversable());
            }
            case VD_DOWN: {
                if (v.gridPos.getY()==0 )
                    return false;
                AnyaCell c1 = getCell(v, AnyaVertex.CellDirections.CD_LEFTDOWN);
                AnyaCell c2 = getCell(v, AnyaVertex.CellDirections.CD_RIGHTDOWN);

                if (c1 == null && c2 == null) return false;
                if (c1 == null)
                    return c2 != null && c2.isTraversable();
                else return c1.isTraversable() || (c2 != null && c2.isTraversable());
            }
            case VD_UP: {
                if (v.gridPos.getY()==0 )
                    return false;
                AnyaCell c1 = getCell(v, AnyaVertex.CellDirections.CD_LEFTUP);
                AnyaCell c2 = getCell(v, AnyaVertex.CellDirections.CD_RIGHTUP);

                if (c1 == null && c2 == null) return false;
                if (c1 == null)
                    return c2 != null && c2.isTraversable();
                else return c1.isTraversable() || (c2 != null && c2.isTraversable());
            }
        }

        return false;
    }

    public boolean isCorner(AnyaVertex v)
    {
        if (v == null)
            return false;
        int tc= 0;

        AnyaCell cLD = getCell(v, AnyaVertex.CellDirections.CD_LEFTDOWN);
        AnyaCell cRD = getCell(v, AnyaVertex.CellDirections.CD_RIGHTDOWN);
        AnyaCell cLU = getCell(v, AnyaVertex.CellDirections.CD_LEFTUP);
        AnyaCell cRU = getCell(v, AnyaVertex.CellDirections.CD_RIGHTUP);

        if (cLD != null && cLD.isTraversable())
            tc++;
        if (cRD != null && cRD.isTraversable())
            tc++;
        if (cLU != null && cLU.isTraversable())
            tc++;
        if (cRU != null && cRU.isTraversable())
            tc++;

        if (tc == 2 )
        {
            if ( v.gridPos.getX()!= 0 && v.gridPos.getX() != xSize-1 && v.gridPos.getY()!=0 && v.gridPos.getY()!=ySize-1)
            {
                if ( (cLD != null && cLD.isTraversable()) && (cRU != null && cRU.isTraversable()) )
                    return true;
                if ((cRD != null && cRD.isTraversable()) && (cLU != null && cLU.isTraversable()) )
                    return true;
            }
        }

        return tc == 3;
    }


    public List<AnyaInterval> constructRightIntervals_old(double x,int row,Point2D.Double root)
    {
        List<AnyaInterval> intervals = new ArrayList<AnyaInterval>();

        AnyaVertex crv = null;

        if (!isTraversable(x,row, AnyaVertex.VertexDirections.VD_RIGHT))
            return intervals;

        double cl = x;

        crv = getVertex(x,row, AnyaVertex.VertexDirections.VD_RIGHT);
        
        while (crv!= null && isTraversable(crv, AnyaVertex.VertexDirections.VD_RIGHT) )
        {
            if (isCorner(crv) && isVisible(root,crv.pos))
            {
                intervals.add(new AnyaInterval(cl,crv.pos.getX(),row));
                cl = crv.pos.getX();
            }

            AnyaVertex nv = getVertex(crv, AnyaVertex.VertexDirections.VD_RIGHT);

            if (!isVisible(root,nv.pos))
                break;
            crv = nv;
        }
        if (crv!=null && crv.pos.getX()!= cl && isVisible(root,crv.pos) )
            intervals.add(new AnyaInterval(cl,crv.pos.getX(),row));
        return intervals;
    }

    public List<AnyaInterval> constructRightIntervals(double x,int row,Point2D.Double root)
    {
        List<AnyaInterval> intervals = new ArrayList<AnyaInterval>();

        AnyaVertex crv = null;

        if (!isTraversable(x,row, AnyaVertex.VertexDirections.VD_RIGHT))
            return intervals;


        int rootRow = getVertex(root.getX(),root.getY(), AnyaVertex.VertexDirections.VD_LEFT).gridPos.getY();
        AnyaVertex rightMostVertex= getVertex(xSize-1,row);

        int lowerRow = (row>rootRow ) ? rootRow:row;
        double cl = x;

        double rMax = firstNontraversibleRight(x,rightMostVertex.pos.getX(),lowerRow);

        crv = getVertex(x,row, AnyaVertex.VertexDirections.VD_RIGHT);

        while (crv!= null && isTraversable(crv, AnyaVertex.VertexDirections.VD_RIGHT) && crv.pos.getX()<= rMax)
        {
            if (isCorner(crv))
            {
                intervals.add(new AnyaInterval(cl,crv.pos.getX(),row));
                cl = crv.pos.getX();
            }

            AnyaVertex nv = getVertex(crv, AnyaVertex.VertexDirections.VD_RIGHT);

            crv = nv;
        }
        if (crv!=null && Math.abs(crv.pos.getX()- cl)>AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD && crv.pos.getX()<=rMax )
            intervals.add(new AnyaInterval(cl,crv.pos.getX(),row));
        return intervals;
    }

    public List<AnyaInterval> constructLeftIntervals(double x,int row,Point2D.Double root)
    {
        List<AnyaInterval> intervals = new ArrayList<AnyaInterval>();

        AnyaVertex clv = null;

        if (!isTraversable(x,row, AnyaVertex.VertexDirections.VD_LEFT))
            return intervals;


        int rootRow = getVertex(root.getX(),root.getY(), AnyaVertex.VertexDirections.VD_LEFT).gridPos.getY();
        AnyaVertex leftMostVertex= getVertex(0,row);

        int lowerRow = (row>rootRow ) ? rootRow:row;
        double cr = x;

        double lMin = firstNontraversibleLeft(x,leftMostVertex.pos.getX(),lowerRow);

        clv = getVertex(x,row, AnyaVertex.VertexDirections.VD_LEFT);

        while (clv!= null && isTraversable(clv, AnyaVertex.VertexDirections.VD_LEFT) && clv.pos.getX()>= lMin)
        {
            if (isCorner(clv) && (cr-clv.pos.getX()>AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD))
            {
                intervals.add(new AnyaInterval(clv.pos.getX(),cr,row));
                cr = clv.pos.getX();
            }

            AnyaVertex nv = getVertex(clv, AnyaVertex.VertexDirections.VD_LEFT);

            clv = nv;
        }
        if (clv!=null && Math.abs(clv.pos.getX()- cr)>AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD && clv.pos.getX()>=lMin )
            intervals.add(new AnyaInterval(clv.pos.getX(),cr,row));
        return intervals;
    }

    public List<AnyaInterval> constructLeftIntervals_old(double x,int row,Point2D.Double root)
    {
        List<AnyaInterval> intervals = new ArrayList<AnyaInterval>();


        if (!isTraversable(x,row, AnyaVertex.VertexDirections.VD_LEFT))
            return intervals;

        double cr = x;
        AnyaVertex clv  = getVertex(cr,row, AnyaVertex.VertexDirections.VD_LEFT);
        if (clv.pos.getX() == cr )
            clv = getVertex(clv, AnyaVertex.VertexDirections.VD_LEFT);

        while (clv != null && isTraversable(clv, AnyaVertex.VertexDirections.VD_LEFT) )
        {
            if (isCorner(clv)&& isVisible(root,clv.pos))
            {
                intervals.add(new AnyaInterval(clv.pos.getX(),cr,row));
                cr = clv.pos.getX();
            }

            AnyaVertex nv = getVertex(clv, AnyaVertex.VertexDirections.VD_LEFT);
            if (!isVisible(root,nv.pos))
                break;

            clv = nv;
        }

        if (clv!=null && clv.pos.getX()<cr && isVisible(root,clv.pos))
            intervals.add(new AnyaInterval(clv.pos.getX(),cr,row));

        return intervals;
    }

    public AnyaInterval constructLeftInterval(double x,int row)
    {

        double cl , cr ;

        if (!isTraversable(x,row, AnyaVertex.VertexDirections.VD_LEFT))
            return null;

        cr=  x;
        AnyaVertex clv  = getVertex(cr,row, AnyaVertex.VertexDirections.VD_LEFT);
        if (clv.pos.getX() == cr )
            clv = getVertex(clv, AnyaVertex.VertexDirections.VD_LEFT);

        while (clv != null && isTraversable(clv, AnyaVertex.VertexDirections.VD_LEFT) && !isCorner(clv)  )
        {
            clv = getVertex(clv, AnyaVertex.VertexDirections.VD_LEFT);
        }

        if (clv.pos.getX()<cr)
            return new AnyaInterval(clv.pos.getX(),cr,row);
        else return null;
    }

    public AnyaInterval constructRightInterval(double x, int row)
    {

        double  cl, cr;

        if (!isTraversable(x,row, AnyaVertex.VertexDirections.VD_RIGHT))
            return null;

        cl= x;
        AnyaVertex crv = getVertex(x,row, AnyaVertex.VertexDirections.VD_RIGHT);
        while (isTraversable(crv, AnyaVertex.VertexDirections.VD_RIGHT) && !isCorner(crv)  )
        {
            crv = getVertex(crv, AnyaVertex.VertexDirections.VD_RIGHT);
        }

        return new AnyaInterval(cl,crv.gridPos.getX(),row);
    }

    public List<AnyaInterval> constructIntervals(double x, int row,Point2D.Double root)
    {
        List<AnyaInterval> intervals = new ArrayList<AnyaInterval>();

        if (!isTraversable(x,row, AnyaVertex.VertexDirections.VD_RIGHT) && !isTraversable(x,row, AnyaVertex.VertexDirections.VD_LEFT))
            return intervals;

        AnyaVertex clv = getVertex(x,row, AnyaVertex.VertexDirections.VD_LEFT);

        while (isTraversable(clv, AnyaVertex.VertexDirections.VD_LEFT) && !isCorner(clv))
        {
            clv = getVertex(clv, AnyaVertex.VertexDirections.VD_LEFT);
        }

        intervals.addAll(constructLeftIntervals(clv.pos.getX(),row,root));
        intervals.addAll(constructRightIntervals(clv.pos.getX(),row,root));


        return intervals;

    }

    public List<AnyaInterval> constructIntervalsBetween(double left,double right, int row)
    {
        List<AnyaInterval> intervals = new ArrayList<AnyaInterval>();

        if (left<0 || right<0 ||left >= right)
            return intervals;

        double currentLeft = skipNonTraversableBlock(left,  row,right);
        AnyaVertex crv= getVertex(currentLeft,row, AnyaVertex.VertexDirections.VD_RIGHT);
        if (crv == null || crv.pos.getX()>right)
        {
            intervals.add(new AnyaInterval(currentLeft,right,row));
            return intervals;
        }

        while (crv.pos.getX()<=right)
        {
            while (!isCorner(crv)&& isTraversable(crv, AnyaVertex.VertexDirections.VD_RIGHT))
                crv= getVertex(crv, AnyaVertex.VertexDirections.VD_RIGHT);

            if (crv.pos.getX()>right)
            {
                intervals.add(new AnyaInterval(currentLeft,right,row));
                break;
            }
            intervals.add(new AnyaInterval(currentLeft,crv.pos.getX(),row));

            if (!isTraversable(crv, AnyaVertex.VertexDirections.VD_RIGHT))
                break;

            currentLeft = crv.pos.getX();
            crv= getVertex(currentLeft,row, AnyaVertex.VertexDirections.VD_RIGHT);
            if (crv.pos.getX()>right && currentLeft<right)
            {
                intervals.add(new AnyaInterval(currentLeft,right,row));
                break;
            }
        }

        return intervals;
    }

    private double skipNonTraversableBlock(double x, int row,double maxX) {
        if (isTraversable(x,row, AnyaVertex.VertexDirections.VD_RIGHT))
            return x;

        AnyaVertex v = getVertex(x,row, AnyaVertex.VertexDirections.VD_RIGHT);

        while (!isTraversable(v, AnyaVertex.VertexDirections.VD_RIGHT) && v.pos.getX()<maxX)
            v = getVertex(v, AnyaVertex.VertexDirections.VD_RIGHT);

        return v.pos.getX();
    }

    public ArrayList<AnyaNode> firstSuccessors(AnyaNode node)
    {
        ArrayList<AnyaNode> successors = new ArrayList<>();
        double left = node.interval.getLeft();
        double right = node.interval.getRight();

        if (left != right || node.root.getX() != left )
            return successors;

        AnyaInterval leftInterval = constructLeftInterval(left,node.interval.row);
        AnyaInterval rightInterval = constructRightInterval(right,node.interval.row);

        if (leftInterval != null)
            successors.add(new AnyaNode(node,leftInterval,node.root));

        if (rightInterval!= null)
            successors.add(new AnyaNode(node,rightInterval,node.root));

        List<AnyaInterval> intervals =new ArrayList<AnyaInterval>();
        if ( isTraversable(left, node.interval.getRow() , AnyaVertex.VertexDirections.VD_DOWN))
        {
            intervals.addAll(constructIntervals(left,node.interval.row-1,node.root));
        }
        if ( isTraversable(left, node.interval.getRow(),AnyaVertex.VertexDirections.VD_UP))
        {
            intervals.addAll(constructIntervals(left,node.interval.row+1,node.root));
        }
        for (AnyaInterval i:intervals)
        {
            successors.add(new AnyaNode(node,i,node.root));
        }
        return successors;
    }

    public ArrayList<AnyaNode> observableSuccessors(AnyaNode node)
    {
        ArrayList<AnyaNode> successors = new ArrayList<>();

        double left = node.interval.left;
        double right = node.interval.right;
        int row = node.interval.getRow();

        AnyaVertex rightVertex = getVertex(right,row, AnyaVertex.VertexDirections.VD_LEFT);
        AnyaVertex leftVertex = getVertex(left,row, AnyaVertex.VertexDirections.VD_LEFT);
        boolean discreteRight = Math.abs(rightVertex.pos.getX()-right) < 0.00000001;
        boolean discreteLeft = Math.abs(leftVertex.pos.getX()-left) < 0.00000001;

        if (row ==  getRow(node.root.getY()))
        {
            if (left == right)
            {
                if (left == node.root.getX() )
                {
                    AnyaInterval leftInterval = constructLeftInterval(left,row);
                    AnyaInterval rightInterval = constructRightInterval(right,row);
                    if (leftInterval!=null)
                        successors.add(new AnyaNode(node.parentNode,leftInterval,node.root));
                    if (rightInterval!=null)
                        successors.add(new AnyaNode(node.parentNode,rightInterval,node.root));
                }
            }
            else
            {
                if (left >= node.root.getX())
                {
                    if (!(discreteRight && isDoubleCorner(rightVertex)))
                    {
                        AnyaInterval rightInterval = constructRightInterval(right, row);
                        if (rightInterval != null)
                            successors.add(new AnyaNode(node.parentNode, rightInterval, node.root));
                    }
                }
                if (right <= node.root.getX())
                {
                    if (!(discreteLeft && isDoubleCorner(leftVertex)))
                    {
                        AnyaInterval leftInterval = constructLeftInterval(left, row);
                        if (leftInterval != null)
                            successors.add(new AnyaNode(node.parentNode, leftInterval, node.root));
                    }
                }
            }
        }
        else
        {
            List<AnyaInterval> pList = getObservableProjection(node.interval, node.root);
            for (AnyaInterval i:pList)
            {
                successors.add(new AnyaNode(node.parentNode,i,node.root));
            }
        }

        return successors;
    }



    private List<AnyaInterval> getObservableProjection(AnyaInterval interval, Point2D.Double root) {

        double left = interval.left;
        double right = interval.right;

        double rowPos = getPosition(0,interval.getRow()).getY();

        if (root.getY()==interval.getRow()) // rows can not be equal!
            return null;

        int nextRow = (root.getY()>interval.getRow()) ? interval.getRow()-1:interval.getRow()+1;
        if (nextRow<0 || nextRow>=ySize)
            return new ArrayList<>();

        if ( (nextRow<interval.row) && ( !isTraversable(left,interval.row, AnyaVertex.VertexDirections.VD_DOWN) ||
                                         !isTraversable(right,interval.row, AnyaVertex.VertexDirections.VD_DOWN) )  )
            return new ArrayList<>();

        if ( (nextRow>interval.row) && ( !isTraversable(left,interval.row, AnyaVertex.VertexDirections.VD_UP) ||
                                         !isTraversable(right,interval.row, AnyaVertex.VertexDirections.VD_UP) )  )
            return new ArrayList<>();

        double maxX = getVertex(xSize-1,0).pos.getX();
        boolean leftBorder = (root.getX()>=left)&& (left ==0);
        boolean rightBorder = (root.getX()<=right)&& (right ==maxX);
        double nextLeft = leftBorder ?  0 : getObservableLeftProjectionPoint(root, left, interval.getRow());
        double nextRight = rightBorder ? maxX : getObservableRightProjectionPoint(root, right, interval.getRow());

        if ( nextLeft <0 || nextRight <0 || !(nextRight-nextLeft>AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)  )
            return new ArrayList<>();

/*        if (!isVisible(new Point2D.Double(left,rowPos),new Point2D.Double(nextRight,nextRowPos)))
        {
            List<AnyaInterval> list =  constructIntervalsBetween(nextLeft,nextLeft,nextRow); // empty list
            return list;
        }

        if (!isVisible(new Point2D.Double(right,rowPos),new Point2D.Double(nextLeft,nextRowPos)))
        {
            List<AnyaInterval> list =  constructIntervalsBetween(nextLeft,nextLeft,nextRow); // empty list
            return list;
        }*/

        List<AnyaInterval> list =  constructIntervalsBetween(nextLeft,nextRight,nextRow);  //To change body of created methods use File | Settings | File Templates.

        return list;
    }



    private double getObservableLeftProjectionPoint(Point2D.Double root, double left, int row) {

        int rootRow = getVertex(root.getX(),root.getY(), AnyaVertex.VertexDirections.VD_LEFT).gridPos.getY();
        int nextRow = (rootRow>row) ? row-1:row+1;

        double pp = left + (left-root.getX())*( 1.0/ Math.abs(rootRow-row) );

        double minX = getVertex(0,0).pos.getX();
        pp = (pp < minX) ? minX:pp;

        if (pp > getVertex(xSize-1,0).pos.getX())
        {
            return -1; // Interval is outside of map just drop it.
        }

        Point2D.Double pLeft = new Point2D.Double(left,getVertex(0,row).pos.getY());
        AnyaVertex vLeft = getClosestVertex(pLeft);

        boolean discretePoint = false;
        if (Math.abs(pLeft.getX()-vLeft.pos.getX()) <AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
        {
            discretePoint =true;
        }

        if ( (!discretePoint) && Math.abs(root.getX()-left)<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD) // vertical move. simply return the same.
            return left;

        if (discretePoint && (nextRow>row) && isBottomCorner(vLeft) && isLeftCorner(vLeft))
        {
            return -1;
        }
        if (discretePoint && (nextRow<row) && isUpCorner(vLeft) && isLeftCorner(vLeft))
        {
            return -1;
        }

        double nextLeft =-1;
        if (left>root.getX() )
        {
            nextLeft = ( nextRow<row) ?   firstNontraversibleRight(left, pp, nextRow ) :firstNontraversibleRight(left, pp, row );
        }
        else nextLeft = ( nextRow<row) ?   firstNontraversibleLeft(left, pp, nextRow) :firstNontraversibleLeft(left, pp, row);

        return nextLeft;

    }

    private double getNonObservableLeftProjectionPoint(Point2D.Double root, double left, int row) {

        int rootRow = getVertex(root.getX(),root.getY(), AnyaVertex.VertexDirections.VD_LEFT).gridPos.getY();
        int nextRow = (rootRow>row) ? row-1:row+1;

        double pp = left + (left-root.getX())*( 1.0/ Math.abs(rootRow-row) );


        pp = (pp < getVertex(0,0).pos.getX()) ? getVertex(0,0).pos.getX():pp;
        if (pp > getVertex(xSize-1,0).pos.getX())
        {
            return -1; // Interval is outside of map just drop it.
        }

        Point2D.Double pLeft = new Point2D.Double(left,getVertex(0,row).pos.getY());
        AnyaVertex vLeft = getClosestVertex(pLeft);

        boolean discretePoint = false;
        if (Math.abs(pLeft.getX()-vLeft.pos.getX()) <AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
        {
            discretePoint =true;
        }

        if ( (!discretePoint) && Math.abs(root.getX()-left)<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD) // vertical move. simply return the same.
            return left;

        double nextLeft =-1;
        if (left>root.getX() )
        {
            nextLeft = ( nextRow<row) ?   firstNontraversibleRight(left, pp, nextRow ) :firstNontraversibleRight(left, pp, row );
        }
        else nextLeft = ( nextRow<row) ?   firstNontraversibleLeft(left, pp, nextRow) :firstNontraversibleLeft(left, pp, row);

        return nextLeft;

    }

    private double firstNontraversibleRight(double left, double pp, int row) {
        double rowPoint = getVertex(0,row).pos.getY();
        AnyaVertex v = getVertex(left,rowPoint, AnyaVertex.VertexDirections.VD_LEFT);
        AnyaCell c = getCell(v, AnyaVertex.CellDirections.CD_RIGHTUP);
        while (v.pos.getX()<pp && c.isTraversable())
        {
            v = getVertex(v, AnyaVertex.VertexDirections.VD_RIGHT);
            c = getCell(v, AnyaVertex.CellDirections.CD_RIGHTUP);
        }
        return v.pos.getX()>=pp ? pp:v.pos.getX();
    }

    private double firstNontraversibleLeft(double right, double pp, int row) {
        double rowPoint = getVertex(0,row).pos.getY();
        AnyaVertex v = getVertex(right,rowPoint, AnyaVertex.VertexDirections.VD_LEFT);
        while (v.pos.getX()>pp && getCell(v, AnyaVertex.CellDirections.CD_LEFTUP).isTraversable())
        {
            v = getVertex(v, AnyaVertex.VertexDirections.VD_LEFT);
        }
        return v.pos.getX()<=pp ? pp:v.pos.getX();

    }

    private double getObservableRightProjectionPoint(Point2D.Double root, double right, int row)
    {
        int rootRow = getVertex(root.getX(),root.getY(), AnyaVertex.VertexDirections.VD_LEFT).gridPos.getY();
        int nextRow = (rootRow>row) ? row-1:row+1;

        double pp = right + (right-root.getX())*( 1.0/ Math.abs(rootRow-row) );

        double maxX = getVertex(xSize-1,0).pos.getX();
        pp = (pp > maxX ) ? maxX:pp;
        if (pp < 0)
        {
            return -1; // Interval is outside of map just drop it.
        }
        Point2D.Double pRight = new Point2D.Double(right,getVertex(0,row).pos.getY());
        AnyaVertex vRight = getClosestVertex(pRight);

        boolean discretePoint = false;
        if (Math.abs(pRight.getX()-vRight.pos.getX()) <AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
        {
            discretePoint =true;
        }

        if (( !discretePoint) && Math.abs(root.getX()-right)<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD) // vertical move. simply return the same.
            return right;

        if (discretePoint && (nextRow>row) && isBottomCorner(vRight) && isRightCorner(vRight))
        {
            return -1;
        }
        if (discretePoint && (nextRow<row) && isUpCorner(vRight) && isRightCorner(vRight))
        {
            return -1;
        }

        double nextLeft =-1;
        if (right>root.getX() )
        {
            nextLeft = ( nextRow<row) ?   firstNontraversibleRight(right, pp, nextRow ) :firstNontraversibleRight(right, pp, row );
        }
        else nextLeft = ( nextRow<row) ?   firstNontraversibleLeft(right, pp, nextRow) :firstNontraversibleLeft(right, pp, row);

        return nextLeft;

    }


    private double getNonObservableRightProjectionPoint(Point2D.Double root, double right, int row)
    {
        int rootRow = getVertex(root.getX(),root.getY(), AnyaVertex.VertexDirections.VD_LEFT).gridPos.getY();
        int nextRow = (rootRow>row) ? row-1:row+1;

        double pp = right + (right-root.getX())*( 1.0/ Math.abs(rootRow-row) );


        pp = (pp > getVertex(xSize-1,0).pos.getX() ) ? getVertex(xSize-1,0).pos.getX():pp;
        if (pp < 0)
        {
            return -1; // Interval is outside of map just drop it.
        }
        Point2D.Double pRight = new Point2D.Double(right,getVertex(0,row).pos.getY());
        AnyaVertex vRight = getClosestVertex(pRight);

        boolean discretePoint = false;
        if (Math.abs(pRight.getX()-vRight.pos.getX()) <AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
        {
            discretePoint =true;
        }

        if (( !discretePoint) && Math.abs(root.getX()-right)<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD) // vertical move. simply return the same.
            return right;


        double nextLeft =-1;
        if (right>root.getX() )
        {
            nextLeft = ( nextRow<row) ?   firstNontraversibleRight(right, pp, nextRow ) :firstNontraversibleRight(right, pp, row );
        }
        else nextLeft = ( nextRow<row) ?   firstNontraversibleLeft(right, pp, nextRow) :firstNontraversibleLeft(right, pp, row);

        return nextLeft;

    }

    private double getProjectionPoint(Point2D.Double projectionRoot,Point2D.Double visibilityRoot, double left, int row, int nextRow) {

        double pp = left + (left-projectionRoot.getX())*( Math.abs(nextRow-row)/ Math.abs(projectionRoot.getY()-row) );



        pp = (pp < getVertex(0,0).pos.getX()) ? getVertex(0,0).pos.getX():pp;
        pp = (pp > getVertex(xSize-1,0).pos.getX()) ? getVertex(xSize-1,0).pos.getX():pp;


        Point2D.Double p1 = new Point2D.Double(pp,getVertex(0,nextRow).pos.getY());


        AnyaVertex closest = getClosestVertex(p1);
        double dist = closest.pos.distance(p1);
        if (dist> 0 && dist <0.00000000001)
        {
            p1 = closest.pos;
            pp= p1.getX();
        }



        if (isVisible(visibilityRoot,p1))
            return pp;


        AnyaVertex v =  getVertex(p1.getX(),p1.getY(), AnyaVertex.VertexDirections.VD_LEFT);

        boolean leftofTheRoot = visibilityRoot.getX()>v.pos.getX();


        while(!isVisible(visibilityRoot,v.pos))
        {
            if (visibilityRoot.getX()<v.pos.getX() )
            {
                if (!leftofTheRoot)
                    v = getVertex(v, AnyaVertex.VertexDirections.VD_LEFT);
                else return visibilityRoot.getX();
            }
            else if (visibilityRoot.getX() == v.pos.getX())
                break;

            else if (visibilityRoot.getX()>v.pos.getX())
            {
                if (leftofTheRoot
                        )v = getVertex(v, AnyaVertex.VertexDirections.VD_RIGHT);
                else return visibilityRoot.getX();
            }
        }


        return v.pos.getX();
    }


    public boolean isDoubleCorner(AnyaVertex v)
    {
        return ( isUpCorner(v) && isLeftCorner(v) && isBottomCorner(v) && isRightCorner(v) && !DOUBLE_CORNER_PASSABLE );
    }

    public ArrayList<AnyaNode> nonObservableSuccessors(AnyaNode node)
    {
        ArrayList<AnyaNode> successors = new ArrayList<>();

        double left = node.interval.left;
        double right = node.interval.right;

        boolean discreteRight = false;



        AnyaVertex rightVertexL = getVertex(right,getPosition(0,node.interval.getRow()).getY(), AnyaVertex.VertexDirections.VD_LEFT);
        AnyaVertex rightVertexR = getVertex(right,getPosition(0,node.interval.getRow()).getY(), AnyaVertex.VertexDirections.VD_RIGHT);

        AnyaVertex rightVertex = rightVertexR;

        if (rightVertexR!=null && Math.abs(rightVertexR.pos.getX()-right)<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
        {
            rightVertex =rightVertexR;
            right = rightVertexR.pos.getX();
            discreteRight =true;
        }
        if (rightVertexL!= null && Math.abs(rightVertexL.pos.getX()-right)<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
        {
            rightVertex =rightVertexL;
            right = rightVertexL.pos.getX();
            discreteRight =true;
        }

        AnyaVertex leftVertexL = getVertex(left,getPosition(0,node.interval.getRow()).getY(), AnyaVertex.VertexDirections.VD_LEFT);
        AnyaVertex leftVertexR = getVertex(left,getPosition(0,node.interval.getRow()).getY(), AnyaVertex.VertexDirections.VD_RIGHT);
        AnyaVertex leftVertex = leftVertexL;
        boolean discreteLeft = false;

        if (leftVertexR!= null&&  Math.abs(leftVertexR.pos.getX()-left)<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
        {
            leftVertex =leftVertexR;
            left = leftVertexR.pos.getX();
            discreteLeft =true;
        }
        if (leftVertexL!= null&& Math.abs(leftVertexL.pos.getX()-left)<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
        {
            leftVertex =leftVertexL;
            left = leftVertexL.pos.getX();
            discreteLeft =true;
        }



        Point2D.Double root = node.root;

        int row =node.interval.row;

        if (left == right)
            return successors;

        if (row == node.root.getY())    // SAME ROW
        {
            if (left>=node.root.getX())
            {
                if ( !isCorner(rightVertex))
                    return successors;

                if (isUpCorner(rightVertex)&&isRightCorner(rightVertex) && discreteRight && !isDoubleCorner(rightVertex) )
                {
                    AnyaVertex down = getVertex(rightVertex, AnyaVertex.VertexDirections.VD_DOWN);

                    // todo bug
                    List<AnyaInterval>intervals = constructRightIntervalsFromCorner(down, rightVertex.pos);
                    for (AnyaInterval i : intervals)
                    {
                        successors.add(new AnyaNode(node,i,rightVertex.pos));
                    }
                }
                else if (isBottomCorner(rightVertex) && isRightCorner(rightVertex)&&discreteRight && !isDoubleCorner(rightVertex) )
                {
                    AnyaVertex up = getVertex(rightVertex, AnyaVertex.VertexDirections.VD_UP);

                    List<AnyaInterval>intervals = constructRightIntervalsFromCorner(up, rightVertex.pos);
                    for (AnyaInterval i : intervals)
                    {
                        successors.add(new AnyaNode(node,i,rightVertex.pos));
                    }
                }
            }
            if (right<=node.root.getX())
            {
                if ( !isCorner(leftVertex))
                    return successors;

                if (isUpCorner(leftVertex)&& isLeftCorner(leftVertex)&&discreteLeft && !isDoubleCorner(leftVertex) )
                {
                    AnyaVertex down = getVertex(leftVertex, AnyaVertex.VertexDirections.VD_DOWN);

                    List<AnyaInterval>intervals = constructLeftIntervalsFromCorner(down,leftVertex.pos);
                    for (AnyaInterval i : intervals)
                    {
                        successors.add(new AnyaNode(node,i,leftVertex.pos));
                    }
                }
                else if (isBottomCorner(leftVertex) && isLeftCorner(leftVertex)&&discreteLeft&& !isDoubleCorner(leftVertex) )
                {
                    AnyaVertex up = getVertex(leftVertex, AnyaVertex.VertexDirections.VD_UP);

                    List<AnyaInterval>intervals = constructLeftIntervalsFromCorner(up,leftVertex.pos);
                    for (AnyaInterval i : intervals)
                    {
                        successors.add(new AnyaNode(node,i,leftVertex.pos));
                    }
                }
            }
        }
        else     // different Row
        {
            int nextRow = (root.getY()>node.interval.getRow()) ? node.interval.getRow()-1:node.interval.getRow()+1;

            if (nextRow>row) // GOING UP
            {
                if (isRightCorner(leftVertex) && discreteLeft && !isDoubleCorner(leftVertex))
                {
                    if (isUpCorner(leftVertex))
                    {
                        AnyaInterval ai = constructLeftInterval(left,row);
                        successors.add(new AnyaNode(node,ai,new Point2D.Double(left,getPosition(0,row).getY())));

                        double nextLeft = getObservableLeftProjectionPoint(root, left, node.interval.getRow());
                        List<AnyaInterval> intervals  = constructLeftIntervals(nextLeft, nextRow, leftVertex.pos);

                        for (AnyaInterval i : intervals) {
                            successors.add(new AnyaNode(node, i, new Point2D.Double(left, getPosition(0, row).getY())));
                        }
                    }
                    else  // leftVertex is DownRightCorner
                    {
                        if (root.getX()<leftVertex.pos.getX())
                        {
                            //double nextRight = getProjectionPoint(root,leftVertex.pos, left, node.interval.getRow(), nextRow);
                            double nextRight = getNonObservableRightProjectionPoint(root,left,node.interval.getRow());
                            double nextLeft = leftVertex.pos.getX();
                            List<AnyaInterval> intervals = constructIntervalsBetween(nextLeft, nextRight, nextRow);

                            for (AnyaInterval i : intervals) {
                                successors.add(new AnyaNode(node, i, new Point2D.Double(left, getPosition(0, row).getY())));
                            }
                        }
                    }
                }
                if (isLeftCorner(leftVertex)&& discreteLeft && !isDoubleCorner(leftVertex))
                {
                    if (isBottomCorner(leftVertex) ) // leftVertex is DownLeft Corner
                    {
                        if (root.getX()>leftVertex.pos.getX()) {
                            //double nextLeft = getProjectionPoint(root, leftVertex.pos,left, node.interval.getRow(), nextRow);
                            double nextLeft = getNonObservableLeftProjectionPoint(root, left, node.interval.getRow());
                            double nextRight = leftVertex.pos.getX();
                            List<AnyaInterval> intervals = constructIntervalsBetween(nextLeft, nextRight, nextRow);

                            for (AnyaInterval i : intervals) {
                                successors.add(new AnyaNode(node, i, new Point2D.Double(left, getPosition(0, row).getY())));
                            }
                        }
                    }
                    else
                    {
                        //nothing todo yet
                    }
                }


                if (isLeftCorner(rightVertex)&&discreteRight&& !isDoubleCorner(rightVertex))
                {
                    if (isUpCorner(rightVertex)) // rightVertex is upLeft Corner
                    {
                        AnyaInterval ai = constructRightInterval(right,row);
                        successors.add(new AnyaNode(node,ai,new Point2D.Double(right,getPosition(0,row).getY())));

                        double nextRight = getObservableRightProjectionPoint(root, right, node.interval.getRow());
                        List<AnyaInterval> intervals  = constructRightIntervals(nextRight, nextRow, rightVertex.pos);

                        for (AnyaInterval i : intervals) {
                            successors.add(new AnyaNode(node, i, new Point2D.Double(right, getPosition(0, row).getY())));
                        }
                    }
                    else  // rightVertex is DownLeftCorner
                    {
                        if (root.getX()>rightVertex.pos.getX())
                        {
                            //double nextLeft = getProjectionPoint(root,rightVertex.pos, right, node.interval.getRow(), nextRow);
                            double nextLeft = getNonObservableLeftProjectionPoint(root,right,node.interval.getRow());
                            double nextRight = rightVertex.pos.getX();
                            List<AnyaInterval> intervals = constructIntervalsBetween(nextLeft, nextRight, nextRow);

                            for (AnyaInterval i : intervals) {
                                successors.add(new AnyaNode(node, i, new Point2D.Double(right, getPosition(0, row).getY())));
                            }
                        }
                    }
                }
                if (isRightCorner(rightVertex)&&discreteRight && !isDoubleCorner(rightVertex)) // rightVertex is RightCorner
                {
                    if (isBottomCorner(rightVertex)) // rightVertex is DownRightCorner
                    {
                        if (root.getX()<rightVertex.pos.getX())
                        {
                            //double nextRight = getProjectionPoint(root,rightVertex.pos, right, node.interval.getRow(), nextRow);
                            double nextRight = getNonObservableRightProjectionPoint(root,right,node.interval.getRow());
                            double nextLeft = rightVertex.pos.getX();
                            List<AnyaInterval> intervals = constructIntervalsBetween(nextLeft, nextRight, nextRow);

                            for (AnyaInterval i : intervals) {
                                successors.add(new AnyaNode(node, i, new Point2D.Double(right, getPosition(0, row).getY())));
                            }
                        }
                    }
                    else
                    {
                        //nothing todo yet
                    }
                }

            }
            else  // GOING DOWN
            {
                if (isRightCorner(leftVertex)&&discreteLeft && !isDoubleCorner(leftVertex))
                {
                    if (isBottomCorner(leftVertex))  // leftVertex is DownRight Corner
                    {
                        AnyaInterval ai = constructLeftInterval(left,row);
                        successors.add(new AnyaNode(node,ai,new Point2D.Double(left,getPosition(0,row).getY())));

                        double nextLeft = getObservableLeftProjectionPoint(root, left, node.interval.getRow());
                        List<AnyaInterval> intervals  = constructLeftIntervals(nextLeft, nextRow, leftVertex.pos);

                        for (AnyaInterval i : intervals) {
                            successors.add(new AnyaNode(node, i, new Point2D.Double(left, getPosition(0, row).getY())));
                        }
                    }
                    else  // leftVertex is UpRight Corner
                    {
                        if (root.getX()<leftVertex.pos.getX())
                        {
                            double nextRight = getNonObservableRightProjectionPoint(root, left, node.interval.getRow());
                            double nextLeft = leftVertex.pos.getX();
                            List<AnyaInterval> intervals = constructIntervalsBetween(nextLeft, nextRight, nextRow);

                            for (AnyaInterval i : intervals) {
                                successors.add(new AnyaNode(node, i, new Point2D.Double(left, getPosition(0, row).getY())));
                            }
                        }
                    }
                }
                if (isLeftCorner(leftVertex)&&discreteLeft && !isDoubleCorner(leftVertex)) // leftVertex is LeftCorner
                {
                    if (isUpCorner(leftVertex)) // leftVertex is UpLeft Corner
                    {
                        if (root.getX()>leftVertex.pos.getX()) {
                            double nextLeft = getNonObservableLeftProjectionPoint(root, left, node.interval.getRow());
                            double nextRight = leftVertex.pos.getX();
                            List<AnyaInterval> intervals = constructIntervalsBetween(nextLeft, nextRight, nextRow);

                            for (AnyaInterval i : intervals) {
                                successors.add(new AnyaNode(node, i, new Point2D.Double(left, getPosition(0, row).getY())));
                            }
                        }
                    }
                    else // leftVertex is DownLeft Corner
                    {
                        //nothing todo yet
                    }
                }

                if (isLeftCorner(rightVertex)&&discreteRight && !isDoubleCorner(rightVertex))
                {
                    if (isBottomCorner(rightVertex))  // rightVertex is DownLeft Corner
                    {
                        AnyaInterval ai = constructRightInterval(right,row);
                        successors.add(new AnyaNode(node,ai,new Point2D.Double(right,getPosition(0,row).getY())));

                        double nextRight = getObservableRightProjectionPoint(root, right, node.interval.getRow());
                        List<AnyaInterval> intervals  = constructRightIntervals(nextRight, nextRow, rightVertex.pos);

                        for (AnyaInterval i : intervals) {
                            successors.add(new AnyaNode(node, i, new Point2D.Double(right, getPosition(0, row).getY())));
                        }
                    }
                    else  // rightVertex is UpLeftCorner
                    {
                        if (root.getX()>rightVertex.pos.getX())
                        {
                            double nextLeft = getNonObservableLeftProjectionPoint(root, right, node.interval.getRow());
                            double nextRight = rightVertex.pos.getX();
                            List<AnyaInterval> intervals = constructIntervalsBetween(nextLeft, nextRight, nextRow);

                            for (AnyaInterval i : intervals) {
                                successors.add(new AnyaNode(node, i, new Point2D.Double(right, getPosition(0, row).getY())));
                            }
                        }
                    }
                }
                if (isRightCorner(rightVertex)&&discreteRight && !isDoubleCorner(rightVertex)) // rightVertex is RightCorner
                {
                    if (isUpCorner(rightVertex)) // rightVertex is UpRight Corner
                    {
                        if (root.getX()<rightVertex.pos.getX())
                        {
                            double nextRight = getNonObservableRightProjectionPoint(root, right, node.interval.getRow());
                            double nextLeft = rightVertex.pos.getX();
                            List<AnyaInterval> intervals = constructIntervalsBetween(nextLeft, nextRight, nextRow);

                            for (AnyaInterval i : intervals) {
                                successors.add(new AnyaNode(node, i, new Point2D.Double(right, getPosition(0, row).getY())));
                            }
                        }
                    }
                    else // rightVertex is DownRight Corner
                    {
                        //nothing todo yet
                    }
                }
            }
        }
        return successors;
    }

    private List<AnyaInterval> constructRightIntervalsFromCorner(AnyaVertex cornerVertex, Point2D.Double root)
    {
        List<AnyaInterval> intervals = new ArrayList<>();

        boolean goingUP = root.getY()<cornerVertex.pos.getY();



        AnyaVertex v = cornerVertex;
        AnyaVertex previousV = cornerVertex;

        if (goingUP )
        {
            while ( isTraversable(v, AnyaVertex.VertexDirections.VD_RIGHT) && getCell(v, AnyaVertex.CellDirections.CD_RIGHTDOWN).isTraversable())
            {
                v = getVertex(v, AnyaVertex.VertexDirections.VD_RIGHT);
                if (isBottomCorner(v))
                {
                    AnyaInterval i = new AnyaInterval(previousV.pos.getX(),v.pos.getX(),cornerVertex.gridPos.getY());
                    intervals.add(i);
                    previousV = v;
                }
            }
            if (v != previousV)
            {
                AnyaInterval i = new AnyaInterval(previousV.pos.getX(),v.pos.getX(),cornerVertex.gridPos.getY());
                intervals.add(i);
            }
        }
        else  // Going down
        {
            while ( isTraversable(v, AnyaVertex.VertexDirections.VD_RIGHT) && getCell(v, AnyaVertex.CellDirections.CD_RIGHTUP).isTraversable())
            {
                v = getVertex(v, AnyaVertex.VertexDirections.VD_RIGHT);
                if (isUpCorner(v))
                {
                    AnyaInterval i = new AnyaInterval(previousV.pos.getX(),v.pos.getX(),cornerVertex.gridPos.getY());
                    intervals.add(i);
                    previousV = v;
                }
            }
            if (v != previousV)
            {
                AnyaInterval i = new AnyaInterval(previousV.pos.getX(),v.pos.getX(),cornerVertex.gridPos.getY());
                intervals.add(i);
            }
        }
        return intervals;
    }

    private List<AnyaInterval> constructLeftIntervalsFromCorner(AnyaVertex cornerVertex, Point2D.Double root)
    {
        List<AnyaInterval> intervals = new ArrayList<>();

        boolean goingUP = root.getY()<cornerVertex.pos.getY();

        AnyaVertex v = cornerVertex;
        AnyaVertex previousV = cornerVertex;

        if (goingUP )
        {
            while ( isTraversable(v, AnyaVertex.VertexDirections.VD_LEFT) && getCell(v, AnyaVertex.CellDirections.CD_LEFTDOWN).isTraversable())
            {
                v = getVertex(v, AnyaVertex.VertexDirections.VD_LEFT);
                if (isBottomCorner(v))
                {
                    AnyaInterval i = new AnyaInterval(v.pos.getX(),previousV.pos.getX(),cornerVertex.gridPos.getY());
                    intervals.add(i);
                    previousV = v;
                }
            }
            if (v != previousV)
            {
                AnyaInterval i = new AnyaInterval(v.pos.getX(),previousV.pos.getX(),cornerVertex.gridPos.getY());
                intervals.add(i);
            }
        }
        else  // Going down
        {
            while ( isTraversable(v, AnyaVertex.VertexDirections.VD_LEFT) && getCell(v, AnyaVertex.CellDirections.CD_LEFTUP).isTraversable())
            {
                v = getVertex(v, AnyaVertex.VertexDirections.VD_LEFT);
                if (isUpCorner(v))
                {
                    AnyaInterval i = new AnyaInterval(v.pos.getX(),previousV.pos.getX(),cornerVertex.gridPos.getY());
                    intervals.add(i);
                    previousV = v;
                }
            }
            if (v != previousV)
            {
                AnyaInterval i = new AnyaInterval(v.pos.getX(),previousV.pos.getX(),cornerVertex.gridPos.getY());
                intervals.add(i);
            }
        }
        return intervals;
    }


    private boolean isLeftCorner(AnyaVertex v) {
        if (!isCorner(v))
            return false;

        AnyaCell ru  =  getCell(v, AnyaVertex.CellDirections.CD_RIGHTUP);
        AnyaCell rd  =  getCell(v, AnyaVertex.CellDirections.CD_RIGHTDOWN);

        return !(ru.isTraversable() && rd.isTraversable() );
    }

    private boolean isRightCorner(AnyaVertex v) {
        if (!isCorner(v))
            return false;

        AnyaCell lu  =  getCell(v, AnyaVertex.CellDirections.CD_LEFTUP);
        AnyaCell ld  =  getCell(v, AnyaVertex.CellDirections.CD_LEFTDOWN);

        return !(lu.isTraversable() && ld.isTraversable() );
    }


    private boolean isUpCorner(AnyaVertex v) {
        if (!isCorner(v))
            return false;

        AnyaCell ld =  getCell(v, AnyaVertex.CellDirections.CD_LEFTDOWN);
        AnyaCell rd =  getCell(v, AnyaVertex.CellDirections.CD_RIGHTDOWN);

        return !(ld.isTraversable() && rd.isTraversable() );
    }

    private boolean isBottomCorner(AnyaVertex v) {
        if (!isCorner(v))
            return false;

        AnyaCell lu =  getCell(v, AnyaVertex.CellDirections.CD_LEFTUP);
        AnyaCell ru =  getCell(v, AnyaVertex.CellDirections.CD_RIGHTUP);

        return !(lu.isTraversable() && ru.isTraversable() );
    }
    private boolean isVisible(Point2D.Double root, AnyaVertex vertex) {

        for (AnyaCell c:cells)
        {
            if (!c.isTraversable())
            {
                if (c.getCellRect(0.01).intersectsLine(root.getX(),root.getY(),vertex.pos.getX(),vertex.pos.getY()))
                    return false;
            }
        }
        return true;
    }

    private boolean isVisible_old(Point2D.Double root, Point2D.Double point) {

        visibilityCalculationCounter++;
        /****TIME PROFILING */ TimerUtil.start("VISIBILITY CALC");
        for (AnyaCell c:cells)
        {
            if (!c.isTraversable())
            {
                if (c.getCellRect(0.01).intersectsLine(root.getX(),root.getY(),point.getX(),point.getY()))
                {
                    /****TIME PROFILING */ TimerUtil.stop("VISIBILITY CALC");
                    return false;
                }
            }
        }
        /****TIME PROFILING */ TimerUtil.stop("VISIBILITY CALC");
        return true;
    }

    public boolean isVisible(int x1, int y1, int x2, int y2 ) {


        visibilityCalculationCounter++;

//        if (x1==x2)
//            return true;

        // /****TIME PROFILING */ TimerUtil.start("VISIBILITY");

        Line2D line = new Line2D.Double(getPosition(x1,y1),getPosition(x2,y2));

        int top, bottom,left,right;

        if (y1>y2)
        {
            top = y1;
            bottom = y2;
        }
        else
        {
            top = y2;
            bottom = y1;
        }

        if (x1>x2)
        {
            left = x2;
            right  = x1;
        }
        else
        {
            left = x1;
            right = x2;
        }

        for (int x =left; x<=right;x++)
        {
            for (int y = bottom; y<=top; y++)
            {
                AnyaCell c= getCell(x,y);

                if (c!=null &&  !c.isTraversable()) {
                    //if (line.intersects(c.getCellRect(0.0001))) {
                    if (c.getCellRect(0.0001).intersectsLine(line)) {
                        // /****TIME PROFILING */ TimerUtil.stop("VISIBILITY");
                        return false;
                    }
                }
            }
        }
        // /****TIME PROFILING */ TimerUtil.stop("VISIBILITY");
        return true;
    }



    public boolean isVisible(Point2D.Double root, Point2D.Double point) {


        visibilityCalculationCounter++;

        if (Math.abs(root.getX()-point.getX())<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
            return true;

        // /****TIME PROFILING */ TimerUtil.start("VISIBILITY");

        int top, bottom,left,right;

        if (root.getY()>point.getY())
        {
            top = (int)root.getY();
            bottom = (int)point.getY();
        }
        else
        {
            top = (int)point.getY();
            bottom = (int)root.getY();
        }

        if (root.getX()>point.getX())
        {
            left = (int)getVertex(point.getX(),point.getY(), AnyaVertex.VertexDirections.VD_LEFT).gridPos.getX();
            right  = (int)getVertex(root.getX(),root.getY(), AnyaVertex.VertexDirections.VD_LEFT).gridPos.getX();
        }
        else
        {
            left = (int)getVertex(root.getX(),root.getY(), AnyaVertex.VertexDirections.VD_LEFT).gridPos.getX();
            right = (int)getVertex(point.getX(),point.getY(), AnyaVertex.VertexDirections.VD_LEFT).gridPos.getX();
        }

        for (int x =left; x<=right;x++)
        {
            for (int y = bottom; y<top; y++)
            {
                AnyaCell c= getCell(x,y);

                if (c!=null &&  !c.isTraversable()) {
                    if (c.getCellRect(0.01).intersectsLine(root.getX(), root.getY(), point.getX(), point.getY())) {
                        // /****TIME PROFILING */ TimerUtil.stop("VISIBILITY");
                        return false;
                    }
                }
            }
        }
        // /****TIME PROFILING */ TimerUtil.stop("VISIBILITY");
        return true;
    }

    public void search_breadFirst(int maxDepth)
    {
        AnyaNode firstNode = searchNodes.get(0).get(0);
        ArrayList<AnyaNode> sList=  firstSuccessors(firstNode);
        searchNodes.add(sList);
        for (int i =1 ;i<maxDepth;i++)
        {
            ArrayList<AnyaNode> nodes = searchNodes.get(i);
            ArrayList<AnyaNode> successors= new ArrayList<>();

            for (AnyaNode n:nodes)
            {
                ArrayList<AnyaNode> oList = observableSuccessors(n);
                ArrayList<AnyaNode> noList = nonObservableSuccessors(n);

                AnyaNode.addNodeListToList(successors,oList);
                AnyaNode.addNodeListToList(successors,noList);
            }

            searchNodes.add(successors);

        }
    }

    public void search_best_first(long maxDepth)
    {
        if (nonTraversableCells.isEmpty())
        {
            searchHappened = true;
            constructStraightPath();
            return;
        }
        if (isObscured(start))
        {
            System.out.println("Start is obscured!");
            return;
        }
        if (isObscured(end))
        {
            System.out.println("Target is obscured!");
            return;
        }

        _createFirstNode();
        AnyaNode firstNode = searchNodes.get(0).get(0);

        searchHappened = true;

        if (maxDepth<=0)
            return;



        ArrayList<AnyaNode> sList=  firstSuccessors(firstNode);

        addNodesToHeap(sList);

        if (showIntervals)
            searchNodes.add(sList);

        for (stepCounter  =1 ;stepCounter<maxDepth;stepCounter++)
        {
            List<AnyaNode> list = null;
            ArrayList<AnyaNode> successors= new ArrayList<>();


            FibonacciHeapNode<AnyaNode> minNode = openList.removeMin();
            if (minNode==null)
            {
                System.out.println("Target is unreachable");
                break;
            }

            AnyaNode n = minNode.getData();
            lastExpandedNode = n;

            if ( stepCounter% DebugTraceInterval ==0 )
                debugTrace("Depth: "+ stepCounter + "  OpenList-Size: " + openList.size()+"   G: "+n.g+"   F: "+ calculateFValue(n));

            if (containsTarget(n))
            {
                constructPath(n);
                break;
            }

            // /****TIME PROFILING */ TimerUtil.start("OBS");
            ArrayList<AnyaNode> oList = observableSuccessors(n);
            list = addObservableNodesToHeap(oList);
            if (targetFound) // Target found among observable succesors
                break;
            if (list != null&& showIntervals);
                successors.addAll(list);


            // /****TIME PROFILING */ TimerUtil.stop("OBS");

            // /****TIME PROFILING */ TimerUtil.start("NON-OBS");
            ArrayList<AnyaNode> noList = nonObservableSuccessors(n);
            list = addNonObservableNodesToHeap(noList);
            if (list!= null && showIntervals)
                successors.addAll(list);
            // /****TIME PROFILING */ TimerUtil.stop("NON-OBS");


            if (showIntervals)
                searchNodes.add(successors);
        }
    }

    private void debugTrace(String s) {
        if (debugTraceFile == null) {
            System.out.println(s);
            return;
        }
        FileWriter fstream = null;
        try {
            fstream = new FileWriter(debugTraceFile,true);
            BufferedWriter out = new BufferedWriter(fstream);

            out.write(s);
            out.newLine();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void constructStraightPath() {
        path = new ArrayList<>();
        path.add(new Line2D.Double(end.pos,start.pos));
    }

    private void constructPath(AnyaNode n) {
        path = new ArrayList<>();
        path.add(new Line2D.Double(end.pos,n.root));
        AnyaNode parent = null;
        while (n.getParentNode()!=null && (n.root.getX() != start.pos.getX() || n.root.getY() != start.pos.getY() ))
        {
            parent = n.getParentNode();
            path.add(new Line2D.Double(n.root,parent.getRoot()));
            n = parent;
        }
    }

    private boolean containsTarget(AnyaNode n) {
        if (n==null || n.interval==null )
            return false;

        double intervalY = getPosition(0, n.interval.getRow()).getY();

        if (Math.abs(end.pos.getY()-intervalY)>AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD )
            return false;

        if ( Math.abs(n.interval.getLeft()-end.pos.getX())<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD )
            return true;

        if ( Math.abs(n.interval.getRight()-end.pos.getX())<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD )
            return true;

        return (n.interval.getLeft()<=end.pos.getX() && n.interval.getRight()>=end.pos.getX());
    }



    private List<AnyaNode> addNodesToHeap(List<AnyaNode> nodeList)
    {
        ArrayList<AnyaNode> successors = new ArrayList<>();
        for (AnyaNode n:nodeList)
        {

            if (!allowRepeatingNodes && (alreadyDiscovered(n) || ( intervalHistoryEnabled && intervalHistory.ignorableNode(n,getVertex(0,n.interval.getRow()).pos.getY()) )) ) {
                ignoredIntervalCount++;
                continue;
            }

            double f = calculateFValue(n);
            n.setF(f);
            generatedIntervalCount++;
            openList.insert(new FibonacciHeapNode<>(n),f);
            if (showIntervals)
                successors.add(n);
        }

        return successors;
    }

    private List<AnyaNode> addNonObservableNodesToHeap(List<AnyaNode> nodeList)
    {
        return addNodesToHeap(nodeList);
    }


    private List<AnyaNode> addObservableNodesToHeap(List<AnyaNode> nodeList)
    {
        ArrayList<AnyaNode> successors = new ArrayList<>();
        for (AnyaNode n:nodeList)
        {
            if (intermediateProjection(n.interval))
            {
                List<AnyaNode> olist = processIntermadiateProjection(n);
                if (targetFound) // Target found in the intermediate projection
                    return null;
                successors.addAll(olist);
            }
            else
                successors.add(n);
        }
        return addNodesToHeap(successors);

    }

    private List<AnyaNode> processIntermadiateProjection(AnyaNode n) {
        List<AnyaNode> list = null;

        if (containsTarget(n))
        {
            targetFound = true;
            constructPath(n);
            return null;
        }

        List<AnyaNode> ol = observableSuccessors(n);

        if (ol.size()>1 || ol.isEmpty() || !intermediateProjection(ol.get(0).interval) )
            return ol;

        else return processIntermadiateProjection(ol.get(0));

    }

    private boolean intermediateProjection(AnyaInterval interval) {
        AnyaVertex lv = getClosestVertex(new Point2D.Double(interval.getLeft(), getPosition(0,interval.getRow()).getY()));
        AnyaVertex rv = getClosestVertex(new Point2D.Double(interval.getRight(), getPosition(0,interval.getRow()).getY()));
        boolean discreteLeft = Math.abs(lv.pos.getX()-interval.getLeft())<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD;

        if (discreteLeft && isCorner(lv)&& !isDoubleCorner(lv))
            return false;

        boolean discreteRight = Math.abs(rv.pos.getX()-interval.getRight())<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD;

        if (discreteRight && isCorner(rv)&& !isDoubleCorner(rv))
            return false;

        return true;
    }

    HashMap<AnyaRoot,Double> rootHistory = new HashMap<>();
    HashMap<AnyaNode,AnyaNode> nodeHistory = new HashMap<>();

    private boolean alreadyDiscovered(AnyaNode n) {

        AnyaRoot anyaRoot = new AnyaRoot(n.root);
        if (rootHistory.containsKey(anyaRoot)) // Previously visited root
        {
            double hg = rootHistory.get(anyaRoot);
            if ( n.g-hg > AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
            {
                AnyaVertex v = getVertex(anyaRoot.rootPoint.getX(),anyaRoot.rootPoint.getY(), AnyaVertex.VertexDirections.VD_LEFT);
                boolean rootAtvertex = anyaRoot.rootPoint.getX()- v.pos.getX()<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD;
                if (!rootAtvertex || !isDoubleCorner(v) )
                    return true;
            }
            else if ( hg-n.g > AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
            {
                AnyaVertex v = getVertex(anyaRoot.rootPoint.getX(),anyaRoot.rootPoint.getY(), AnyaVertex.VertexDirections.VD_LEFT);
                boolean rootAtvertex = anyaRoot.rootPoint.getX()- v.pos.getX()<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD;
                if (!rootAtvertex || !isDoubleCorner(v) )
                {
                    rootHistory.remove(anyaRoot);
                    rootHistory.put(anyaRoot,n.g);
                    nodeHistory.remove(n);
                    nodeHistory.put(n,n);
                    return false;
                }
            }
            if (!nodeHistoryEnabled)
                return false;

            if (nodeHistory.containsKey(n)) // SAME interval SAME root with NOT BETTER g
            {
                AnyaNode hNode = nodeHistory.get(n);

                if (hNode.interval.getRight()!= n.interval.getRight() || hNode.interval.getLeft() != n.interval.getLeft()) {
                    boolean aha = true;
                }

                if (hNode.g<n.g || Math.abs(hNode.g-n.g)<AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD)
                    return true;
                else
                {
                    nodeHistory.remove(n);
                    nodeHistory.put(n,n);
                    return false;
                }
            }
            else // New interval with the root
            {
                nodeHistory.put(n, n);
                return false;
            }

        }
        else // New root
        {
            rootHistory.put(anyaRoot, n.g);
            nodeHistory.put(n, n);
            return false;
        }
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
    public AnyaVertex getClosestVertex(Point2D pos)
    {
       int x = (int)Math.round((pos.getX()-offSet.getX())/unitEdgeLen);
       int y = (int)Math.round((pos.getY()-offSet.getY())/unitEdgeLen);
       return getVertex(x,y);
    }

    /**
     * Creates edges of the vertex v according to the connectivity degree
     * @param v
     */
    void _createAdjacencies(AnyaVertex v)
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


    }

    /**
     * Creates edges of the graph  according to the connectivity degree
     */
    void _createAdjacencies()
    {
        for (AnyaVertex av:grid)
            _createAdjacencies(av);
    }

    /**
     * Creates vertices
     */
    void _createVertices()
	{
		grid = new AnyaVertex[xSize*ySize];

		for (int x = 0; x <xSize;x++)
		{
			for (int y = 0; y <ySize;y++)
			{
				AnyaVertex av = new AnyaVertex(IdManager.getVertexId(),getPosition(x,y),new GridPosition(x,y));
				grid[x*ySize+y] = av;
                vertices.add(av);
			}
		}
		_createAdjacencies();
        _createCells();

        searchNodes = new ArrayList<>();
	}

    private void _createCells() {

        cells = new AnyaCell[(xSize-1)*(ySize-1)];

        for (int x = 0; x <xSize-1;x++)
        {
            for (int y = 0; y <ySize-1;y++)
            {
                AnyaCell ac = new AnyaCell(x,y,new AnyaVertex[]{getVertex(x,y),getVertex(x,y+1),getVertex(x+1,y+1),getVertex(x+1,y)});
                cells[x*(ySize-1)+y] = ac;
            }
        }
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



    public void setCell(int x, int y, boolean traversable)
    {
        if (getCell(x,y) != null)
            getCell(x,y).setTraversable(traversable);
    }

    @Override
    public void draw(Graphics2D g2D, BaseGraphPanel.ViewTransform transform) {
        Color orjColor = g2D.getColor();
        Stroke orjStroke = g2D.getStroke();
        Stroke stroke = null;

        float dashPhase = 0f;
        float dash[] = {5.0f,5.0f};
        float point[] = {3.0f,12.0f};

        for (int dy =0 ; dy<ySize; dy++)
        {
            AnyaVertex lv = getVertex(0,dy);
            AnyaVertex rv = getVertex(xSize-1,dy);

            Line2D.Double l = new Line2D.Double(lv.pos,rv.pos);

            g2D.draw(transform.createTransformedShape(l));
        }

        for (int dx =0 ; dx<xSize; dx++)
        {
            AnyaVertex uv = getVertex(dx,0);
            AnyaVertex bv = getVertex(dx,ySize-1);

            Line2D.Double l = new Line2D.Double(uv.pos,bv.pos);

            g2D.draw(transform.createTransformedShape(l));
        }

        g2D.setColor(Color.black);
        for (AnyaCell c: cells )
        {
            if (c.isTraversable())
                continue;

            g2D.fill(transform.createTransformedShape(c.getCellRect(0)));
        }
        g2D.setColor(orjColor);

        drawNodes(g2D,transform);


        g2D.setStroke(orjStroke);
        g2D.setColor(orjColor);

        drawPath(g2D,transform);

        g2D.setColor(Color.red);
        g2D.draw(transform.createTransformedShape((new Circle2D(new math.geom2d.Point2D(start.pos), 0.2)).asAwtShape()));
        g2D.draw(transform.createTransformedShape((new Circle2D(new math.geom2d.Point2D(end.pos),0.2)).asAwtShape() ));



        g2D.setStroke(orjStroke);
        g2D.setColor(orjColor);
    }

    private void drawNodes(Graphics2D g2D, AffineTransform transform) {
        Stroke orjStroke = g2D.getStroke();
        g2D.setStroke( new BasicStroke(3f));
        int c=0;

        for ( int i =0; i< searchNodes.size();i++ )
        {
            ArrayList<AnyaNode> nodes= searchNodes.get(i);
            boolean rootVisible = (i == searchNodes.size()-1);

            for (AnyaNode node:nodes)
            {
                drawNode(g2D,transform,node,RandUtil.randomColor(c++),rootVisible);
                g2D.setStroke(orjStroke);
            }

        }

    }

    private void drawPath(Graphics2D g2D,BaseGraphPanel.ViewTransform transform) {

        for (Path upperBoundPath:upperBoundPaths) {
            if (upperBoundPath != null)
                upperBoundPath.draw(g2D, transform);
        }
        if (path ==null || path.isEmpty())
            return;

        g2D.setColor(Color.red);
        g2D.setStroke(new BasicStroke(5.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f));

        for (Line2D.Double l: path)
            g2D.draw(transform.createTransformedShape(l));




        return ;

    }

    private void drawNode(Graphics2D g2D,AffineTransform transform,AnyaNode node, Color color,boolean rootVisible)
    {

        g2D.setColor(color);
        Point2D.Double lp = new Point2D.Double(node.interval.getLeft(),getPosition(0,node.interval.getRow()).getY());
        Point2D.Double rp = new Point2D.Double(node.interval.getRight(),getPosition(0,node.interval.getRow()).getY());
        Line2D.Double l = new Line2D.Double(lp,rp);

        g2D.fill(transform.createTransformedShape((new Circle2D(new math.geom2d.Point2D(lp),0.1)).asAwtShape() ));
        g2D.fill(transform.createTransformedShape((new Circle2D(new math.geom2d.Point2D(rp),0.1)).asAwtShape() ));

        if (node == lastExpandedNode )
            g2D.setStroke(new BasicStroke(10));

        g2D.draw(transform.createTransformedShape(l));


        if (rootVisible)
        {
            float dashPhase = 0f;
            float dash[] = {5.0f,5.0f};
            float point[] = {3.0f,12.0f};

            Stroke stroke = new BasicStroke(2f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_MITER,
                    2f,
                    dash,
                    dashPhase);

            g2D.setStroke(stroke);

            Line2D leftLine = new Line2D.Double(new Point2D.Double(node.interval.getLeft(), getPosition(0,node.interval.getRow()).getY()),node.getRoot() );
            Line2D rightLine = new Line2D.Double(new Point2D.Double(node.interval.getRight(), getPosition(0,node.interval.getRow()).getY()),node.getRoot() );

            g2D.draw(transform.createTransformedShape(leftLine));
            g2D.draw(transform.createTransformedShape(rightLine));
        }

        AffineTransform orgT = g2D.getTransform();

        NumberFormat nf = NumberFormat.getInstance();

        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        AffineTransform t = null;
        try {
            t = transform.createInverse().createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        g2D.setColor(color);
        g2D.setFont(new Font("Arial",0,1));

        if (t != null)
        {
            t.scale(0.2,-0.2);
            g2D.setTransform(t);
        }
        else
            return;

        if (node.getF()>0)
        {
            g2D.drawString(""+nf.format(node.getF()),(float)(lp.getX()*5),(float)((lp.getY()+0.1)*-5 ) );
        }

        g2D.setTransform(orgT);
    }



    @Override
    public Rectangle2D boundingRect() {

        Rectangle2D.Double r = new Rectangle2D.Double(getVertex(0,0).pos.getX(),getVertex(0,0).pos.getY(),xSize*unitEdgeLen,ySize*unitEdgeLen);
        return r;
    }


    public boolean isObscured(AnyaVertex v)
    {
        if (    !isTraversable(v, AnyaVertex.VertexDirections.VD_LEFT) &&
                !isTraversable(v, AnyaVertex.VertexDirections.VD_RIGHT)&&
                !isTraversable(v, AnyaVertex.VertexDirections.VD_DOWN) &&
                !isTraversable(v, AnyaVertex.VertexDirections.VD_UP))
            return true;

        return false;

    }

    public void setAllowRepeatingNodes(boolean allowRepeatingNodes) {
        this.allowRepeatingNodes = allowRepeatingNodes;
    }

    public boolean isAllowRepeatingNodes() {
        return allowRepeatingNodes;
    }




    public double getPathLength() {
        if (path==null )
            return -1;

        double len =0;

        for (Line2D.Double l:path)
        {
            len += l.getP1().distance(l.getP2());
        }

        return len;

    }


    AnyaIntervalHistory intervalHistory = null;
    public void reset() {
        path = null;
        upperBoundPaths.clear();
        searchHappened= false;
        targetFound = false;
        nodeHistory.clear();
        rootHistory.clear();
        openList.clear();
        searchNodes.clear();

        intervalHistory = new AnyaIntervalHistory(ySize);

        generatedIntervalCount =0;
        ignoredIntervalCount=0;
        visibilityCalculationCounter=0;
        setStart(start);


    }

    public long getStepCounter() {
        return stepCounter;
    }


    @Override
    public void next() {
        if (!searchHappened)
            return;

        long nextMaxDepth = stepCounter+1;

        reset();

        search_best_first(nextMaxDepth);
    }

    @Override
    public void previous() {
        if (!searchHappened)
            return;

        long nextMaxDepth = stepCounter-1;

        reset();

        search_best_first(nextMaxDepth);
    }

    public static void test2()
    {
        AnyaGrid ag = new AnyaGrid(200,200,1,new Point2D.Double(0,0),5,0,197,199);
        ag.DebugTraceInterval=Long.MAX_VALUE;

        AnyaObstacleGenerator aog = new RandomAOG(0.0,"anyaRandom2.txt");

        aog.generate(ag);



        ag.setAllowRepeatingNodes(false);

        TimerUtil.setEnabled(true);
        ag.setShowIntervals(false);

        TimerUtil.start("TOTAL");
        ag.search_best_first(1000);
        TimerUtil.stop("TOTAL");

        TimerUtil.printTotal();

        System.out.println("Step Count: "+ ag.getStepCounter());
        System.out.println("Visibility Check Count: "+ ag.visibilityCalculationCounter);
        System.out.println("Generated Interval Count: "+ ag.generatedIntervalCount);
        System.out.println("Ignored Interval Count: "+ ag.ignoredIntervalCount);

        GraphViewer.showContent(ag);
    }

    public static void test3() {
        AnyaGrid ag = new AnyaGrid(200, 200, 1, new Point2D.Double(0, 0), 5, 0, 197, 199);



        ag.setAllowRepeatingNodes(false);



        AnyaObstacleGenerator aog = new FileAOG("anyaRandom2.txt");

        aog.generate(ag);

        TimerUtil.setEnabled(true);
        ag.setShowIntervals(true);

        ag.search_best_first(10000);

        TimerUtil.printTotal();

        System.out.println("Step Count: "+ ag.getStepCounter());

        GraphViewer.showContent(ag);
    }
    public static void main(String[] args)
    {
        test2();

    }

}

