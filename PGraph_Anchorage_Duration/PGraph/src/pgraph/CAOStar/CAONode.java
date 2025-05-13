package pgraph.CAOStar;

import pgraph.grid.GridVertex;
import pgraph.rdp.RDPObstacleInterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: ADMIN
 * Date: 2/10/13
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CAONode {

    private long id;
    private boolean isORNode;
    private boolean isTerminal;
    private RDPObstacleInterface disambiguatedObstacle;
    private GridVertex disambiguationPoint;

    // information state: a/t/f:
    private HashMap<RDPObstacleInterface, Character> informationState;

    // OR nodes will always have a single parent.
    // AND nodes can have multiple parents because of the caching logic:
    private LinkedList<CAOLink> parentLinks = new LinkedList<>();

    // OR nodes will have multiple children.
    // AND nodes will have exactly two children corresponding to true and false disambiguation outcomes:
    private LinkedList<CAOLink> childLinks = new LinkedList<>();

    // min traversal FROM this node to destination ("the heuristic label h()").
    // This is a lower-bound for the true label "cost-to-go-function" f():
    private double min;

    // max traversal FROM this node to destination
    // max values will be computed for each AND node - max field is not used in OR nodes:
    private double max;

    // bestKid can be terminal or non-terminal - it is used in propagating cost:
    private CAONode bestKid;

    // nonTerminalBestKid is ALWAYS non-terminal - it is used in expanding the best non-terminal child:
    private CAONode nonTerminalBestKid;

    // constructor:
    public CAONode(long id,
                   boolean _type,
                   boolean _isTerminal,
                   RDPObstacleInterface _disk,
                   GridVertex _disambiguationPoint,
                   HashMap<RDPObstacleInterface, Character> _infState,
                   CAONode _bestKid,
                   CAONode _nontermBestKid) {
        this.id = id;
        this.isORNode = _type;
        this.isTerminal = _isTerminal;
        this.disambiguatedObstacle = _disk;
        this.disambiguationPoint = _disambiguationPoint;
        this.informationState = _infState;
        this.bestKid = _bestKid;
        this.nonTerminalBestKid = _nontermBestKid;
    }


    // process new cost info for this node:
    // this processing is done after node expansion
    // if "min" of the node has changed, this function will return true and false otherwise
    public boolean updateChildren()
    {
        if (this.isTerminal()) return false; // return false to prevent cost propagation

        double originalMin = this.min;

        if(this.isORNode == true)
        {
            if (this.childLinks.isEmpty())
            {
                this.isTerminal = true;

            } else {

                this.bestKid = null;
                this.nonTerminalBestKid = null;

                // terminal or non-terminal, doesn't matter:
                CAOLink bestKidLink = this.findBestChildOfORNode(false);
                if (bestKidLink != null) {
                    this.bestKid = bestKidLink.getEndNode();
                }

                // we need a non-terminal child this time:
                CAOLink nonTerminalBestKidLink = this.findBestChildOfORNode(true);
                if (nonTerminalBestKidLink != null) {
                    this.nonTerminalBestKid = nonTerminalBestKidLink.getEndNode();
                }

                //use bestKid to propagate cost - an OR node has only one parent:
                if (this.bestKid != null) {
                    this.min = bestKidLink.getDistance() + this.bestKid.min;
                }

                //use nonTerminalBestKid to propagate terminal status
                if (this.nonTerminalBestKid == null) this.isTerminal = true;
            }

        } else {
            // this is an AND node:
            // first child must be a false orNode, second must be a true orNode.
            // no other childLinks are allowed:
            CAONode falseNode = this.childLinks.get(0).getEndNode();
            CAONode trueNode = this.childLinks.get(1).getEndNode();

            this.min = (this.disambiguatedObstacle.getP() * trueNode.min) + ( (1-this.disambiguatedObstacle.getP()) * falseNode.min);

            trueNode.min = Math.max(trueNode.min, falseNode.min);

            //for AND nodes, the best child will always be non-terminal:
            this.nonTerminalBestKid = this.findBestChildOfANDNode();

            if (this.nonTerminalBestKid == null) {
                this.isTerminal = true;
                this.max = this.min;
            }
        }
        return (originalMin != this.min);
    } // end updateChildren()


    // AND nodes have exactly two children and the best child is always be non-terminal:
    public CAONode findBestChildOfANDNode()
    {
        //this is an AND node, so it will always have only two childLinks:
        CAONode falseNode = this.childLinks.get(0).getEndNode();
        CAONode trueNode = this.childLinks.get(1).getEndNode();
        if ((falseNode.isTerminal == false) && (trueNode.isTerminal == false)) {
             if(falseNode.min < trueNode.min) {
                 return falseNode;
            } else {
                 return trueNode;
            }
        } else if ((falseNode.isTerminal == true) && (trueNode.isTerminal == false)) {
            return trueNode;
        } else if ((falseNode.isTerminal == false) && (trueNode.isTerminal == true)) {
            return falseNode;
        } else {
            return null; //both are true - there is no non-terminal bestNode for this AND node
        }
    } // end findBestChildOfANDNode()


    public CAOLink findBestChildOfORNode(boolean needNonTerminalChild)
    {
        CAOLink bestChildLink = null;
        double lowestMinValue = Double.POSITIVE_INFINITY;
        for (CAOLink currLink : this.childLinks) {
            CAONode currChild = currLink.getEndNode();
            if ((currChild.isTerminal) && (needNonTerminalChild == true)) continue;
            double currDist = currLink.getDistance();
            if (currDist + currChild.min < lowestMinValue) {
                bestChildLink = currLink;
                lowestMinValue = currDist + currChild.min;
            }
        }
        return bestChildLink;
    } // end findBestChildOfORNode


    // this function will find an OR node's lowest max value of
    // its children regardless of their terminal status
    public double findLowestMaxValue()
    {
        double lowestMaxValue = Double.POSITIVE_INFINITY;
        for (CAOLink currLink : this.childLinks) {
            CAONode currChild = currLink.getEndNode();
            double currDist = currLink.getDistance();
            if (currDist + currChild.max < lowestMaxValue) {
                lowestMaxValue = currDist + currChild.max;
            }
        }
        return lowestMaxValue;
    }  // findLowestMaxValue()


    // prune bad kids of a given OR node using min and max values:
    // loop over the AND children of this OR node and remove the ones for which
    // the min value (the lower bound, i.e. heuristic label) is higher than
    // lowestMaxValue (the upper bound, found by the DT heuristic):
    public int pruneBadChildrenOfORNode()
    {
        double lowestMaxValue = findLowestMaxValue();
        int pruneCount = 0;
        Iterator<CAOLink> childLinkIter = this.childLinks.iterator();
        while (childLinkIter.hasNext()) {
            CAOLink currLink = childLinkIter.next();
            CAONode currChild = currLink.getEndNode();
            if (currLink.getDistance() + currChild.min > lowestMaxValue) {
                //System.out.println("... Pruning a Bad AND Node Child ...");
                childLinkIter.remove();
                currChild.removeParentLink(currLink);
                pruneCount++;
            }
        }
        return pruneCount;
    } // end pruneBadChildrenOfORNode()


    public void removeParentLink(CAOLink link)
    {
        Iterator<CAOLink> parentLinkIter = this.parentLinks.iterator();
        while (parentLinkIter.hasNext()) {
            CAOLink currLink = parentLinkIter.next();
            if (currLink.getEndNode() == link.getStartNode()) {
                //System.out.println("... Removing a Parent Link...");
                parentLinkIter.remove();
                return;
            }
        }
    } // end removeParentLink()


    public int findNoOfAmbigObstacles()
    {
        int counter = 0;
        for(Character value: this.informationState.values()) {
            if (value.equals('a')) counter++;
        }
        return counter;
    }


    public LinkedList<CAOLink> getParentLinks() {
        return parentLinks;
    }

    public RDPObstacleInterface getDisambiguatedObstacle() {
        return disambiguatedObstacle;
    }

    public GridVertex getDisambiguationPoint() {
        return disambiguationPoint;
    }

    public HashMap<RDPObstacleInterface, Character> getInformationState() {
        return informationState;
    }

    public LinkedList<CAOLink> getChildLinks() {
        return childLinks;
    }

    public double getMin() {
        return min;
    }

    public CAONode getBestKid() {
        return bestKid;
    }

    public CAONode getNonTerminalBestKid() {
        return nonTerminalBestKid;
    }

    public boolean isORNode() {
        return isORNode;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public void setORNode(boolean ORNode) {
        this.isORNode = ORNode;
    }

    public void setTerminal(boolean terminal) {
        isTerminal = terminal;
    }

    public void setDisambiguatedObstacle(RDPObstacleInterface disambiguatedObstacle) {
        this.disambiguatedObstacle = disambiguatedObstacle;
    }

    public void setDisambiguationPoint(GridVertex disambiguationPoint) {
        this.disambiguationPoint = disambiguationPoint;
    }

    public void setInformationState(HashMap<RDPObstacleInterface, Character> informationState) {
        this.informationState = informationState;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setBestKid(CAONode bestKid) {
        this.bestKid = bestKid;
    }

    public void setNonTerminalBestKid(CAONode nonTerminalBestKid) {
        this.nonTerminalBestKid = nonTerminalBestKid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CAONode caoNode = (CAONode) o;

        if (id != caoNode.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
