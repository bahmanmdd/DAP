package pgraph;

import org.jgrapht.GraphPath;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.gui.BaseGraphPanel;
import pgraph.util.Pen;
import umontreal.iro.lecuyer.functionfit.SmoothingCubicSpline;
import umontreal.iro.lecuyer.functions.Polynomial;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:18
 * To change this template use File | Settings | File Templates.
 */
public class SmoothPath extends Path {


    public SmoothPath(Path p) {
        super(p.path,p.pen);
    }

    public SmoothPath(GraphPath<BaseVertex, BaseEdge> path, Pen pen) {
        super(path,pen);
    }


    public SmoothingCubicSpline getSpline()
    {
        List<BaseEdge> edgeList = path.getEdgeList();
        double xPoints[] = new double[edgeList.size()+1];
        double yPoints[] = new double[edgeList.size()+1];
        xPoints[0] = path.getStartVertex().pos.getX();
        yPoints[0] = path.getStartVertex().pos.getY();
        for (int i =0; i<edgeList.size();i++)
        {
            xPoints[i] = edgeList.get(i).end.pos.getX();
            yPoints[i] = edgeList.get(i).end.pos.getY();
        }
        SmoothingCubicSpline spl = new SmoothingCubicSpline(xPoints,yPoints,1);

        return spl;
    }


    public GeneralPath.Double getSmoothPath()
    {
        List<BaseEdge> edgeList = path.getEdgeList();
        double xPoints[] = new double[edgeList.size()+1];
        double yPoints[] = new double[edgeList.size()+1];
        xPoints[0] = path.getStartVertex().pos.getX();
        yPoints[0] = path.getStartVertex().pos.getY();

        GeneralPath.Double gp = new GeneralPath.Double();
        gp.moveTo(path.getStartVertex().pos.getX(),path.getStartVertex().pos.getY());

        if (edgeList.size()==1)
        {
            gp.lineTo(edgeList.get(0).start.pos.getX(),edgeList.get(0).start.pos.getY());
            return gp;
        }
        if (edgeList.size()==0)
        {
            return gp;
        }

        BaseEdge se = edgeList.get(0);
        double mx = ( se.start.pos.getX() + se.end.pos.getX() ) / 2;
        double my = ( se.start.pos.getY() + se.end.pos.getY() ) / 2;
        gp.lineTo(mx,my);

        for (int i=0; i<edgeList.size()-1;i++)
        {
            BaseEdge e = edgeList.get(i);
            BaseEdge ne = edgeList.get(i+1);
            double mx1 = ( e.start.pos.getX() + e.end.pos.getX() ) / 2;
            double mx2 = ( ne.start.pos.getX() + ne.end.pos.getX() ) / 2;
            double my1 = ( e.start.pos.getY() + e.end.pos.getY() ) / 2;
            double my2 = ( ne.start.pos.getY() + ne.end.pos.getY() ) / 2;
            gp.quadTo(ne.start.pos.getX(),ne.start.pos.getY(),mx2,my2);
        }


        gp.lineTo(path.getEndVertex().pos.getX(),path.getEndVertex().pos.getY());

        return gp;
    }

    public GeneralPath.Double getSmoothPath2()
    {
        List<BaseEdge> edgeList = path.getEdgeList();
        double xPoints[] = new double[edgeList.size()+1];
        double yPoints[] = new double[edgeList.size()+1];
        xPoints[0] = path.getStartVertex().pos.getX();
        yPoints[0] = path.getStartVertex().pos.getY();

        GeneralPath.Double gp = new GeneralPath.Double();
        gp.moveTo(path.getStartVertex().pos.getX(),path.getStartVertex().pos.getY());

        if (edgeList.size()==1)
        {
            gp.lineTo(edgeList.get(0).start.pos.getX(),edgeList.get(0).start.pos.getY());
            return gp;
        }
        if (edgeList.size()==0)
        {
            return gp;
        }
        if (edgeList.size()==2)
        {
            gp.quadTo(edgeList.get(0).start.pos.getX(),edgeList.get(0).start.pos.getY(),edgeList.get(1).start.pos.getX(),edgeList.get(1).start.pos.getY());
            return gp;
        }

        BaseEdge se = edgeList.get(0);
        double mx = se.start.pos.getX() ;
        double my = se.start.pos.getY() ;


        for (int i=1; i<edgeList.size()-1;i+=2)
        {
            BaseEdge ne = edgeList.get(i);
            BaseEdge nne = edgeList.get(i+1);


            if  (i+2 == edgeList.size())
            {
                mx = nne.end.pos.getX();
                my = nne.end.pos.getY();
            }
            else
            {
                mx = ((nne.start.pos.getX()+ nne.end.pos.getX())/2);
                my = ((nne.start.pos.getY()+ nne.end.pos.getY())/2);
            }
            gp.curveTo(ne.start.pos.getX(),ne.start.pos.getY(),ne.end.pos.getX(),ne.end.pos.getY(),mx,my);


            if  (i+3 == edgeList.size())
            {
                BaseEdge le = edgeList.get(i+2);
                gp.quadTo(le.start.pos.getX(),le.start.pos.getY(),le.end.pos.getX(),le.end.pos.getY());
                break;
            }
        }


        return gp;
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

        g.draw(getSmoothPath2().createTransformedShape(transform));

        g.setStroke(orjStroke);
        g.setColor(orjColor);

    }


}
