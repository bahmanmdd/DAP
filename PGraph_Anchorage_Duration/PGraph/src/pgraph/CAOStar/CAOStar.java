package pgraph.CAOStar;

import pgraph.DiskObstacleShape;
import pgraph.grid.GridVertex;
import pgraph.intersectionhandler.DTBasedRDPIntersectionHandler;
import pgraph.rdp.RDPObstacle;
import pgraph.rdp.RDPObstacleInterface;
import pgraph.rdp.RDPProblem;
import pgraph.rdp.RDPUtil;
import pgraph.util.IdManager;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;


public class CAOStar {

    private int numberOfNodeExpansions = 0;
    private int numberOfNodesCached = 0;
    private int numberOfRevisitedStates = 0;
    private int numberOfPrunedChildren = 0;

    // sometimes searching the cache is more costly than no caching at all,
    // so its use is controlled via the variable isCachingUsed:
    private boolean isCachingUsed = true;

    private HashMap<TripleKey, CAONode> visitedStates = new HashMap<>(); // needed for the original caching logic

    //**********************************************************************************
    //**********************************************************************************

    public double executeAlgorithm(RDPProblem p) throws IOException, InstantiationException
    {
        HashMap<RDPObstacleInterface, Character> ambInfState = new HashMap<>(p.getRoList().size());

        for (RDPObstacleInterface obstacle : p.getRoList()) ambInfState.put(obstacle, 'a');

        RDPObstacleInterface pointDiskStart = new RDPObstacle(IdManager.getObstacleId(),1,
                new DiskObstacleShape(new Point2D.Double(p.getInitialGraph().start.gridPos.getX(),p.getInitialGraph().start.gridPos.getY()),0.00001),0.0,false,0);

        // generate the root OR node:
        CAONode root = new CAONode(IdManager.getVertexId(), true, false, pointDiskStart, p.getInitialGraph().start, ambInfState, null, null);

        root.getParentLinks().add(new CAOLink(root, null, 0.0));
        root.setMin(Double.POSITIVE_INFINITY);

        while (root.isTerminal() == false)
        {
            this.increaseNumberOfNodeExpansions();
            this.expandBestPartialSolution(root, p);
        }

        return root.getMin();
    }

    //**********************************************************************************
    //**********************************************************************************

    // only OR Nodes are expanded:
    public void expandBestPartialSolution(CAONode currNode, RDPProblem p) throws IOException, InstantiationException
    {
        if (currNode == null) return;

        // totalPathDistUptoNode: the ACTUAL distance from the root up to and including this node
        double totalPathDistUptoNode = 0.0;
        CAONode expandNode = currNode; //this is the best non-terminal OR node that will be expanded

        while (currNode != null)
        {
            expandNode = currNode;
            if (currNode.isORNode())
            {
                //this is an OR node, so its distance to its parent will be zero:
                CAOLink currLink = currNode.findBestChildOfORNode(true); // need non-terminal child
                if (currLink != null)
                {
                    currNode = currLink.getEndNode();
                    totalPathDistUptoNode += currLink.getDistance();
                } else {
                    currNode = null;
                }
            } else {
                // this is an AND node
                currNode = currNode.findBestChildOfANDNode(); // will always be non-terminal
            }
        }

        System.out.println(getNumberOfNodeExpansions() + "-th Expansion: The OR node at (" +
                expandNode.getDisambiguationPoint().gridPos.getX() + ", " +
                expandNode.getDisambiguationPoint().gridPos.getY() + ") ");
                //+ " with inf. state " + expandNode.getInformationState().values().toString() );

        // at this point, expandNode is a leaf node that is the best non-terminal child &
        // there are no better terminal children then expandNode:

        //expandNode is always a non-terminal OR node:
        expandORNode(expandNode, p);

        // propagate new costs all the way up to the root node:
        recursivePropCost(expandNode, totalPathDistUptoNode);
    }

    //**********************************************************************************
    //**********************************************************************************

    // propagate new costs all the way up to the root node from the given expandNode:
    public void recursivePropCost(CAONode expandNode, double totalPathDistUptoNode)
    {
        if (expandNode == null) return;

        if (expandNode.isTerminal()) return;

        boolean isMinChanged = true;  // initialize to true
        do {
            if (expandNode.isORNode())
            {
                int pruneCount = expandNode.pruneBadChildrenOfORNode();
                this.setNumberOfPrunedChildren(this.getNumberOfPrunedChildren() + pruneCount);

                isMinChanged = expandNode.updateChildren();
                if (!isMinChanged) return; // no need to continue propagating
                expandNode = expandNode.getParentLinks().get(0).getEndNode();

            } else {

                isMinChanged = expandNode.updateChildren();
                if (!isMinChanged) return;

                // we cannot remove elements from a LinkedList while looping
                // so, as a workaround, we make a (shallow) copy and loop over that copy:
                LinkedList<CAOLink> parentLinksCopy = new LinkedList<>(expandNode.getParentLinks());
                Iterator<CAOLink> iter = parentLinksCopy.iterator();
                while (iter.hasNext()) {
                    //System.out.println("------ Updating Children of an AND Node's Parent -------");
                    CAOLink currLink = iter.next();
                    recursivePropCost(currLink.getEndNode(), totalPathDistUptoNode - currLink.getDistance());
                }
                //System.out.println("---- Done Updating ----");
                return;
            } // end else

        } while (expandNode != null);
    }

    //**********************************************************************************
    //**********************************************************************************

    public void expandORNode(CAONode orNode, RDPProblem p) throws IOException, InstantiationException
    {
        if (orNode == null) return;

        int numberOfObstacles = orNode.getInformationState().size();

        //first, add the destination node to the children list:
        double zeroRiskDistToDestination = p.zeroRiskLengthForUpperBound(orNode.getDisambiguationPoint(),
                p.getInitialGraph().end, orNode.getInformationState());

        //destination is an OR node, which is also terminal:
        RDPObstacleInterface pointDiskTerminal = new RDPObstacle(IdManager.getObstacleId(),1,
                new DiskObstacleShape(new Point2D.Double(p.getInitialGraph().end.gridPos.getX(),p.getInitialGraph().end.gridPos.getY()),0.00001),0.0,false,0);

        CAONode destNode = new CAONode(IdManager.getVertexId(),true, true, pointDiskTerminal, p.getInitialGraph().end,
                orNode.getInformationState(), null, null);

        orNode.getChildLinks().add(new CAOLink(orNode, destNode, zeroRiskDistToDestination));

        destNode.setMin(0.0);
        destNode.setMax(0.0);

        int noDisagsLeft = p.getDisambiguationCount() - (numberOfObstacles - orNode.findNoOfAmbigObstacles());

        // check to see if there are enough disambiguations left to add non-destination nodes
        if (noDisagsLeft == 0) return;

        // compute the DT length from this OR node to destination, but only when K > 1
        // this value will be used to prevent addition of bad children:
        double ORNodeDistToDestinationDTA = -1.0;
        if (noDisagsLeft > 1) {
            int savedDisagCount = p.getDisambiguationCount();
            p.setDisambiguationCount(noDisagsLeft);
            ORNodeDistToDestinationDTA = p.calculateExpectedWeight(orNode.getDisambiguationPoint(), p.getInitialGraph().end,
                    orNode.getInformationState(), new DTBasedRDPIntersectionHandler(p.getDisambiguationCost()));
            p.setDisambiguationCount(savedDisagCount);
        }

        //add the disambiguation points associated with ambiguous detections:
        List<RDPObstacleInterface> obstacles = p.getRoList();
        for(RDPObstacleInterface obstacle : obstacles)
        {
            if ((obstacle.equals(orNode.getDisambiguatedObstacle()) ||  (orNode.getInformationState().get(obstacle) != 'a'))) continue;

            Set<GridVertex> disambiguationPoints = RDPUtil.getDisambiguationPoints(p.getInitialGraph(), obstacle, p.getNumberOfDisagPoints());

            for(GridVertex disambiguationPoint : disambiguationPoints)
            {
                // NOTICE: this is where we are adding the disambiguation cost
                // thus, we assume that right after we get to the AND node, we disambiguate it
                // distance between an AND parent and a OR child will be zero
                double distToParent = p.getDisambiguationCost() +
                        p.zeroRiskLengthForUpperBound(orNode.getDisambiguationPoint(), disambiguationPoint, orNode.getInformationState());

                if (distToParent == Double.POSITIVE_INFINITY) continue;

                HashMap<RDPObstacleInterface, Character> falseInfState = new HashMap<>(orNode.getInformationState());
                HashMap<RDPObstacleInterface, Character> trueInfState =  new HashMap<>(orNode.getInformationState());

                falseInfState.put(obstacle,'f');
                trueInfState.put(obstacle, 't');

                double bestFalseDistToDestination = p.zeroRiskLengthForLowerBound(disambiguationPoint, p.getInitialGraph().end, falseInfState);
                double bestTrueDistToDestination  = p.zeroRiskLengthForLowerBound(disambiguationPoint, p.getInitialGraph().end, trueInfState);
                double expectedBestDistToDestination = obstacle.getP()*bestTrueDistToDestination + (1-obstacle.getP())*bestFalseDistToDestination;

                // if the following condition is true, no need to add this AND node as a child:
                if (noDisagsLeft > 1)  // ThIs check Is requIred for the algo the work correctly:
                {
                    if (distToParent + expectedBestDistToDestination >= ORNodeDistToDestinationDTA * p.getDPSHeuristicDTAFactor()) continue;
                }

                // the following pruning sometimes breaks optimality,
                // but it will be used in the DPS Heuristic to speed up the search considerably:
                if (p.getDPSHeuristicDTAFactor() > 1.0)
                {
                    if (bestFalseDistToDestination == bestTrueDistToDestination) continue;
                }

                TripleKey tripleKey = new TripleKey(obstacle, disambiguationPoint, orNode.getInformationState());
                if (isCachingUsed)
                {
                    // we only cache AND nodes, so tripleKey is always for an AND Node:
                    if (visitedStates.containsKey(tripleKey))
                    {
                        //System.out.println("------ Revisiting an AND Node ------- ");
                        CAONode andChild = visitedStates.get(tripleKey);
                        andChild.getParentLinks().add(new CAOLink(andChild, orNode, distToParent));
                        orNode.getChildLinks().add(new CAOLink(orNode, andChild, distToParent));
                        increaseNumberOfRevisitedStates();
                        continue;
                    }
                }

                double worstFalseDistToDestination = p.zeroRiskLengthForUpperBound(disambiguationPoint, p.getInitialGraph().end, falseInfState);
                double worstTrueDistToDestination  = p.zeroRiskLengthForUpperBound(disambiguationPoint, p.getInitialGraph().end, trueInfState);
                double expectedWorstDistToDestination = obstacle.getP()*worstTrueDistToDestination + (1-obstacle.getP())*worstFalseDistToDestination;

                //First, define the AND node and initialize its parent to the orNode:
                CAONode andChild = new CAONode(IdManager.getVertexId(), false, false, obstacle, disambiguationPoint,
                        orNode.getInformationState(), null, null);

                // now add the AND node as child of the OR node:
                // if there is only one disambiguation left, don't add any OR nodes - just process it and mark as terminal
                if (noDisagsLeft == 1)
                {
                    andChild.setMin(expectedWorstDistToDestination);
                    andChild.setMax(expectedWorstDistToDestination);
                    andChild.setTerminal(true);
                }
                else
                {
                    andChild.setMin(expectedBestDistToDestination);
                    andChild.setMax(expectedWorstDistToDestination);

                    /*
                    // use DTA to compute an alternative upper bound from this AND node:
                    int savedDisagCount = p.getDisambiguationCount();
                    p.setDisambiguationCount(noDisagsLeft);
                    double expectedWorstDistToDestinationDTA = p.calculateExpectedWeight(disambiguationPoint, p.getInitialGraph().end,
                            orNode.getInformationState(), new DTBasedRDPIntersectionHandler(p.getDisambiguationCost()));
                    p.setDisambiguationCount(savedDisagCount);
                    // set AND node max to min of the two upper bounds:
                    andChild.setMax(Math.min(expectedWorstDistToDestination,expectedWorstDistToDestinationDTA * p.getDPSHeuristicDTAFactor()));
                    */

                    // add non-terminal OR nodes corresponding to T and F possibilities:

                    CAONode falseORNode = new CAONode( IdManager.getVertexId(), true, false, andChild.getDisambiguatedObstacle(),
                            andChild.getDisambiguationPoint(),
                            falseInfState, null, null);

                    CAONode trueORNode = new CAONode( IdManager.getVertexId(), true, false, andChild.getDisambiguatedObstacle(),
                            andChild.getDisambiguationPoint(),
                            trueInfState, null, null);

                    falseORNode.getParentLinks().add(new CAOLink(falseORNode, andChild, 0.0));
                    trueORNode.getParentLinks().add(new CAOLink(trueORNode, andChild, 0.0));

                    falseORNode.setMin(bestFalseDistToDestination);
                    trueORNode.setMin(bestTrueDistToDestination);

                    //falseORNode MUST be the first node and trueORNode MUST be the second:
                    andChild.getChildLinks().add(new CAOLink(andChild, falseORNode, 0.0));
                    andChild.getChildLinks().add(new CAOLink(andChild, trueORNode, 0.0));

                    //propagation will not process this deep - initialize these:
                    andChild.setBestKid(falseORNode);
                    andChild.setNonTerminalBestKid(falseORNode);
                }

                // add this AND Node as a child to the OR Node and vice versa:
                orNode.getChildLinks().add(new CAOLink(orNode, andChild, distToParent));
                andChild.getParentLinks().add(new CAOLink(andChild, orNode, distToParent));

                if (isCachingUsed)
                {
                    //System.out.println("------ Adding an AND Node to Cache ------- ");
                    visitedStates.put(tripleKey, andChild);
                    increaseNumberOfNodesCached();
                }
            }  // end for disambiguationPoint
        } // end for obstacle
    } //expandORNode()

    public int getNumberOfNodeExpansions() {
        return numberOfNodeExpansions;
    }

    public void increaseNumberOfNodeExpansions() {
        numberOfNodeExpansions++;
    }

    public int getNumberOfRevisitedStates() {
        return numberOfRevisitedStates;
    }

    public int getNumberOfNodesCached() {
        return numberOfNodesCached;
    }

    public void increaseNumberOfNodesCached() {
        numberOfNodesCached++;
    }

    public void increaseNumberOfRevisitedStates() {
        numberOfRevisitedStates++;
    }

    public int getNumberOfPrunedChildren() {
        return numberOfPrunedChildren;
    }

    public void setNumberOfPrunedChildren(int numberOfPrunedChildren) {
        this.numberOfPrunedChildren = numberOfPrunedChildren;
    }

}
