package pgraph;

import org.jgrapht.GraphPath;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.gui.BaseGraphPanel;
import pgraph.util.Pen;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:18
 * To change this template use File | Settings | File Templates.
 */
public class Path implements BaseGraphPanel.Renderable {
    GraphPath<BaseVertex,BaseEdge> path;
    Pen pen;



    public Path(GraphPath<BaseVertex, BaseEdge> path, Pen pen) {
        this.path = path;
        this.pen = pen;
    }

    public GraphPath<BaseVertex, BaseEdge> getPath() {
        return path;
    }

    public Pen getPen() {
        return pen;
    }

    @Override
    public void draw(Graphics2D g, BaseGraphPanel.ViewTransform transform)
    {
        if (getPath()==null)
            return;

        Color orjColor = g.getColor();
        Stroke orjStroke = g.getStroke();
        Stroke stroke = null;

        float dashPhase = 0f;
        float dash[] = {5.0f,5.0f};
        float point[] = {3.0f,12.0f};


        g.setColor(getPen().color);

        if (getPen().style == Pen.PenStyle.PS_Dashed) {
            stroke = new BasicStroke(getPen().thickness,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_MITER,
                    getPen().thickness,
                    dash,
                    dashPhase);
        }
        else if (getPen().style == Pen.PenStyle.PS_Pointed) {
            stroke = new BasicStroke(getPen().thickness,
                    BasicStroke.CAP_SQUARE,
                    BasicStroke.JOIN_BEVEL,
                    getPen().thickness,
                    point,
                    dashPhase);
        }
        else
            stroke = new BasicStroke(getPen().thickness);

        g.setStroke(stroke);
        for (BaseEdge e:getPath().getEdgeList())
        {
            g.draw(transform.createTransformedShape(e.shape()));
        }

        g.setStroke(orjStroke);
        g.setColor(orjColor);
    }

    @Override
    public Rectangle2D boundingRect() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }


    public static int   getTotalIntersectionCount(GraphPath<BaseVertex,BaseEdge> p, List<ObstacleInterface> oList) throws InstantiationException {

        //double ail = 0;
        int ic = 0;

        Set<ObstacleInterface> intersectedObstacles = new TreeSet<ObstacleInterface>();

        for (BaseEdge e: p.getEdgeList())
        {
            for(ObstacleInterface o:oList)
            {

                if ( o.lineIntersectionCount((LineEdge)e)>0) {
                    intersectedObstacles.add(o);
                     //ail += 2*Math.sqrt(Math.pow(o.radius, 2) - Math.pow((o.center.x -e.center.x ,2));
                }
            }
        }

        return intersectedObstacles.size();
        //return ail;
    }
}
