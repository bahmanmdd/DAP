package pgraph.alg.dijkstra;

import pgraph.grid.GridVertex;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 25.07.2013
 * Time: 20:09
 * To change this template use File | Settings | File Templates.
 */
public class DijkstraHeapNode {
    private GridVertex currentVertex;
    private GridVertex lastTurnVertex;
    private double lastTurnFootLength;
    private double lastTurnAngle;

    public double getLastTurnFootLength() {
        return lastTurnFootLength;
    }

    public void setLastTurnFootLength(double lastTurnFootLength) {
        this.lastTurnFootLength = lastTurnFootLength;
    }

    public double getLastTurnAngle() {
        return lastTurnAngle;
    }

    public void setLastTurnAngle(double lastTurnAngle) {
        this.lastTurnAngle = lastTurnAngle;
    }

    public DijkstraHeapNode(GridVertex currentVertex, GridVertex lastTurnVertex, double lastTurnFootLength, double lastTurnAngle) {
        this.currentVertex = currentVertex;
        this.lastTurnVertex = lastTurnVertex;
        this.lastTurnFootLength = lastTurnFootLength;
        this.lastTurnAngle = lastTurnAngle;
    }

    public GridVertex getCurrentVertex() {
        return currentVertex;
    }

    public void setCurrentVertex(GridVertex currentVertex) {
        this.currentVertex = currentVertex;
    }

    public GridVertex getLastTurnVertex() {
        return lastTurnVertex;
    }

    public void setLastTurnVertex(GridVertex lastTurnVertex) {
        this.lastTurnVertex = lastTurnVertex;
    }


}
