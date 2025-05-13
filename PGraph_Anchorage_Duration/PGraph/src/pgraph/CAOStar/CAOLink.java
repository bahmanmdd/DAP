package pgraph.CAOStar;

/**
 * Created with IntelliJ IDEA.
 * User: ADMIN
 * Date: 3/21/13
 * Time: 9:28 AM
 * To change this template use File | Settings | File Templates.
 */

// BAO links establish parent-child relationships between OR and AND nodes
// these links are DIRECTED - so for such a relationship, two links need to be defined:

public class CAOLink {

    private CAONode startNode;
    private CAONode endNode;
    private double distance;

    public CAOLink(CAONode startNode, CAONode endNode, double distance) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.distance = distance;
    }

    public CAONode getStartNode() {
        return startNode;
    }

    public void setStartNode(CAONode startNode) {
        this.startNode = startNode;
    }

    public CAONode getEndNode() {
        return endNode;
    }

    public void setEndNode(CAONode endNode) {
        this.endNode = endNode;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
