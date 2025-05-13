package pgraph.anchorage;

import math.geom2d.conic.Circle2D;
import pgraph.gui.BaseGraphPanel;
import pgraph.util.Pen;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 07.11.2013
 * Time: 08:48
 * To change this template use File | Settings | File Templates.
 */
public class Anchorage implements BaseGraphPanel.Renderable{
    Circle2D area;
    double outerRadius;
    double innerRadius;
    Pen pen = Pen.DefaultPen;

    long arrivalTime;
    long departureTime;

    Object tangentItem1 = null;
    Object tangentItem2 = null;

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
    }

    public double getOuterRadius() {
        return outerRadius;
    }

    public void setOuterRadius(double outerRadius) {
        this.outerRadius = outerRadius;
    }

    public double getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(double innerRadius) {
        this.innerRadius = innerRadius;
    }

    private boolean detailedView=false;

    public Anchorage(Circle2D area) {
        this.area = area;
    }


    public Anchorage(Circle2D area, double outerRadius, double innerRadius, Object t1, Object t2) {
        this.area = area;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        tangentItem1 = t1;
        tangentItem2 = t2;
    }

    public Anchorage(Circle2D area, double outerRadius, double innerRadius) {
        this.area = area;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
    }

    public Anchorage(Circle2D area, double outerRadius, double innerRadius, Pen pen) {
        this.area = area;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.pen = pen;
    }

    public Anchorage(Circle2D area, Pen pen) {
        this.area = area;
        this.pen = pen;
    }

    public Circle2D getArea() {
        return area;
    }

    public void setArea(Circle2D area) {
        this.area = area;
    }

    public Pen getPen() {
        return pen;
    }

    public void setPen(Pen pen) {
        this.pen = pen;
    }

    public boolean isDetailedView() {
        return detailedView;
    }

    public void setDetailedView(boolean detailedView) {
        this.detailedView = detailedView;
    }

    @Override
    public void draw(Graphics2D g, BaseGraphPanel.ViewTransform transform) {
        Stroke s_org = g.getStroke();
        Color c_org = g.getColor();

        if (detailedView)
            draw_Detailed(g,transform);
        else draw_Classic(g,transform);

        g.setStroke(s_org);
        g.setColor(c_org);
    }


    public void draw_Detailed(Graphics2D g, BaseGraphPanel.ViewTransform transform)
    {
        g.setColor(Color.black);
        Stroke sInner = new BasicStroke(2);
        Circle2D innerCircle = new Circle2D(area.center(),innerRadius);
        g.setStroke(sInner);
        g.draw(transform.createTransformedShape(innerCircle.asAwtShape()));

        Stroke sReal = new BasicStroke(1);
        g.setStroke(sReal);
        g.setColor(Color.red);
        g.draw(transform.createTransformedShape(area.asAwtShape()));

        float dashPhase = 0f;
        float dash[] = {5.0f,5.0f};
        Stroke sOuter = new BasicStroke(1,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_MITER,
                1,
                dash,
                dashPhase);
        g.setStroke(sOuter);


        g.setColor(Color.blue);
        Circle2D outerCircle = new Circle2D(area.center(),outerRadius);
        g.draw(transform.createTransformedShape(outerCircle.asAwtShape()));
    }

    public void draw_Classic(Graphics2D g, BaseGraphPanel.ViewTransform transform)
    {
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

        g.draw(transform.createTransformedShape(area.asAwtShape()));
    }

    @Override
    public Rectangle2D boundingRect() {
        return area.boundingBox().asAwtRectangle2D();
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }


    public double calculateArea()
    {
        double a = Math.PI* area.radius()*area.radius();
        return a;
    }
}
