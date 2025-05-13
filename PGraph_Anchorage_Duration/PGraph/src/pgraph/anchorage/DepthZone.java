package pgraph.anchorage;

import pgraph.anchorage.util.ShipDynamics;
import pgraph.gui.BaseGraphPanel;
import pgraph.util.Pen;
import pgraph.util.Polygon2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 08:48
 * To change this template use File | Settings | File Templates.
 */
public class DepthZone implements BaseGraphPanel.Renderable{

    Polygon2D area;

    public double getDepth() {
        return depth;
    }

    double depth;
    String title;
    Pen pen = Pen.DefaultPen;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    boolean visible=true;

    public DepthZone(Polygon2D area,double depth,String title) {
        this.area = area;
        this.depth = depth;
        this.title = title;
    }



    public Polygon2D getArea() {
        return area;
    }

    public void setArea(Polygon2D area) {
        this.area = area;
    }

    public Pen getPen() {
        return pen;
    }

    public void setPen(Pen pen) {
        this.pen = pen;
    }

    @Override
    public void draw(Graphics2D g, BaseGraphPanel.ViewTransform transform) {

        if (!visible)
            return;

        Stroke s_org = g.getStroke();
        Color c_org = g.getColor();

        if (pen.style != Pen.PenStyle.PS_Normal)
        {
            float dashPhase = 0f;
            float dash[] = {5.0f,5.0f};
            Stroke s = new BasicStroke(1,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_MITER,
                    1,
                    dash,
                    dashPhase);
            g.setStroke(s);
        }

        g.setColor(pen.color);

        Shape s = transform.createTransformedShape(area);
        g.fill(s);

        g.setStroke(s_org);
        g.setColor(c_org);
    }

    @Override
    public Rectangle2D boundingRect() {
        return area.getBounds2D();
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }


    public double getMaximumLength() {
        double safeLength =  ShipDynamics.getSafeLength(depth);
        return safeLength;
    }
}
