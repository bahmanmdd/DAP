package pgraph.base;

import java.awt.geom.Point2D;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:01
 * To change this template use File | Settings | File Templates.
 */
public class BaseVertex implements Comparable<BaseVertex>{
    public long id;
    public Point2D.Double pos;



    /**
     * Outgoing Edges
     */
    TreeSet<BaseEdge> outgoings =new TreeSet<BaseEdge>();

    /**
     * Incoming Edges
     */
    TreeSet<BaseEdge> incomings=new TreeSet<BaseEdge>();

    /**
     * Union of outgoings and incomings
     */
    TreeSet<BaseEdge> touchings=new TreeSet<BaseEdge>();


    /**
     * Constructor
     * @param id
     * @param pos
     */
    public BaseVertex(long id, Point2D.Double pos) {
        this.id = id;
        this.pos = pos;
    }

    /**
     * Getter for outgoings
     * @return
     */
    public TreeSet<BaseEdge> getOutgoings() {
        return outgoings;
    }


    /**
     * Getter for outgoings
     * @return
     */
    public TreeSet<BaseVertex> getOutgoingNeighbors() {
        TreeSet<BaseVertex>  on = new TreeSet<BaseVertex>();

        for(BaseEdge e:outgoings)
        {
            on.add(e.end);
        }
        return on;
    }

    /**
     * Getter for incomings
     * @return
     */
    public TreeSet<BaseEdge> getIncomings() {
        return incomings;
    }

    /**
     * Tests if this vertex is equal to another
     * @param o
     * @return
     */
    public boolean equals(BaseVertex o) {
        return id == o.id;
    }

    /**
     * Returns the edge outgoing to the given target
     * @param target
     * @return
     */
    public BaseEdge getOutgoingTo(BaseVertex target)
    {
        if (target ==null)
            return null;
        for (BaseEdge e: outgoings)
        {
            if (e.end.equals(target))
                return e;
        }
        return null;
    }

    /**
     * Returns the edge incoming from the given start
     * @param start
     * @return
     */
    public BaseEdge getIncomingFrom(BaseVertex start)
    {
        for (BaseEdge e: incomings)
        {
            if (e.start.equals(start))
                return e;
        }
        return null;
    }

    /**
     * Adds new outgoing edge
     * @param e
     */
    void addOutgoing(BaseEdge e)
    {
        outgoings.add(e);
        touchings.add(e);
    }

    /**
     * Adds new incoming edge
     * @param e
     */
    void addIncoming(BaseEdge e)
    {
        incomings.add(e);
        touchings.add(e);
    }

    /**
     * removes the given incoming edge
     * @param e
     */
    void removeIncoming(BaseEdge e)
    {
        incomings.remove(e);
        touchings.remove(e);
    }

    /**
     * Removes the given outgoing edge
     * @param e
     */
    void removeOutgoing(BaseEdge e)
    {
        outgoings.remove(e);
        touchings.remove(e);
    }


    /**
     * Compare this edge to another
     * @param o
     * @return
     */
    @Override
    public int compareTo(BaseVertex o) {
        return Long.compare(this.id,o.id);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
