package pgraph.anya;

import pgraph.util.RandUtil;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 28.06.2014
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class AnyaNode {
    AnyaNode parentNode;
    AnyaInterval interval;
    Point2D.Double root;
    private Color color;

    double f=0;

    double g;

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object obj)
    {
        if( ! (obj instanceof AnyaNode))
            return false;

        AnyaNode n = (AnyaNode)obj;
        if ( !n.interval.equals(interval) )
            return false;
        if (n.root.getX() != root.getX() || n.root.getY()!= root.getY())
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = interval != null ? interval.hashCode() : 0;
        result = 31 * result + (root != null ? root.hashCode() : 0);
        return result;
    }

    public AnyaNode(AnyaNode parent,AnyaInterval interval, Point2D.Double root) {
        this.parentNode = parent;
        this.interval = interval;
        this.root = root;
        this.color = RandUtil.randomColor(-1);

        if (parent==null)
            g = 0;
        else
            g =  parent.g + parent.root.distance(root);

        if (interval.getLeft()==7 && interval.getRight()==9 && interval.getRow()==9)
        {
            boolean aha = true;
        }
        if (interval.getLeft()==3 && interval.getRight()==6 && interval.getRow()==8)
        {
            boolean aha = true;
        }

    }

    public AnyaNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(AnyaNode parentNode) {
        this.parentNode = parentNode;
    }

    public AnyaInterval getInterval() {
        return interval;
    }

    public void setInterval(AnyaInterval interval) {
        this.interval = interval;
    }

    public Point2D.Double getRoot() {
        return root;
    }

    public void setRoot(Point2D.Double root) {
        this.root = root;
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }


    public static void addNodeToList(java.util.List<AnyaNode> nodeList, AnyaNode node)
    {
        if (notExists(nodeList,node))
            nodeList.add(node);
    }

    public static void addNodeListToList(java.util.List<AnyaNode> dest, List<AnyaNode> source)
    {
        for (AnyaNode n:source)
            addNodeToList(dest,n);
    }

    private static boolean notExists(List<AnyaNode> nodeList, AnyaNode node) {

        for (AnyaNode n: nodeList)
        {
            if (    n.getParentNode() == node.getParentNode() &&
                    n.getInterval().getRight()== node.getInterval().getRight() &&
                    n.getInterval().getLeft()== node.getInterval().getLeft()&&
                    n.getRoot() == node.getRoot())
                return false;
        }
        return true;
    }
}
