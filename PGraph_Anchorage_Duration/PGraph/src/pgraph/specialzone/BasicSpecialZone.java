package pgraph.specialzone;

import pgraph.base.BaseEdge;
import pgraph.gui.BaseGraphPanel;
import pgraph.util.IdManager;
import pgraph.util.Pen;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 22.08.2013
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public class BasicSpecialZone implements SpecialZoneInterface {

    public static final String ZONE_SEPERATOR = "['|']";
    long id= -1;
    ZoneEffect zoneEffect;
    ZoneShape  zoneShape;
    private Pen pen = Pen.DefaultPen;

    String title= null;
    float titleX=0;
    float titleY=0;

    public BasicSpecialZone(long id,String str) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        this.id = id;
        String[] tokens = str.split(ZONE_SEPERATOR);
        String effectStr = tokens[0];
        zoneEffect= (ZoneEffect) Class.forName(effectStr.split(" ")[0]).newInstance();
        zoneEffect.fromString(effectStr);

        String shapeStr = tokens[1];
        zoneShape = (ZoneShape) Class.forName(shapeStr.split(" ")[0]).newInstance();
        zoneShape.fromString(shapeStr);

        String penStr = tokens[2];
        pen = new Pen(penStr);
    }

    public BasicSpecialZone(long id, ZoneEffect zoneEffect, ZoneShape zoneShape, Pen pen, String title, float titleX, float titleY) {
        this.id = id;
        this.zoneEffect = zoneEffect;
        this.zoneShape = zoneShape;
        this.pen = pen;
        this.title = title;
        this.titleX = titleX;
        this.titleY = titleY;
    }

    public BasicSpecialZone(long id, ZoneEffect zoneEffect, ZoneShape zoneShape) {
        this.id = id;
        this.zoneEffect = zoneEffect;
        this.zoneShape = zoneShape;
    }

    public BasicSpecialZone(long id, ZoneEffect zoneEffect, ZoneShape zoneShape, Pen pen) {
        this.id = id;
        this.zoneEffect = zoneEffect;
        this.zoneShape = zoneShape;
        this.pen = pen;
    }

    public ZoneEffect getZoneEffect() {
        return zoneEffect;
    }

    public void setZoneEffect(ZoneEffect zoneEffect) {
        this.zoneEffect = zoneEffect;
    }

    public ZoneShape getZoneShape() {
        return zoneShape;
    }

    public void setZoneShape(ZoneShape zoneShape) {
        this.zoneShape = zoneShape;
    }

    @Override
    public long getId() {
        return id;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Pen getPen() {
        return pen;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void fromString(String st) {
        String[] stList = st.split(ZONE_SEPERATOR);

        zoneEffect.fromString(stList[0]);
        zoneShape.fromString(stList[1]);
    }

    @Override
    public boolean isCovering(BaseEdge e) {
        return zoneShape.isCovering(e);
    }

    @Override
    public void applyZoneEffect(BaseEdge e) {
        zoneEffect.apply(e);
    }

    @Override
    public void draw(Graphics2D g2D, BaseGraphPanel.ViewTransform transform) {

        Color orjColor = g2D.getColor();
        AffineTransform tOrj  = g2D.getTransform();
        Paint pOrj = g2D.getPaint();

        Stroke stroke = null;
        float dashPhase = 0f;
        float dash[] = {5.0f,5.0f};
        float point[] = {3.0f,12.0f};

        g2D.setColor(pen.color);
        if (pen.style == Pen.PenStyle.PS_Dashed) {
            stroke = new BasicStroke(pen.thickness,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_MITER,
                    pen.thickness,
                    dash,
                    dashPhase);
        }
        else  if (pen.style == Pen.PenStyle.PS_Pointed) {
            stroke = new BasicStroke(pen.thickness,
                    BasicStroke.CAP_SQUARE,
                    BasicStroke.JOIN_BEVEL,
                    pen.thickness,
                    dash,
                    dashPhase);
        }
        else
            stroke = new BasicStroke(pen.thickness);
        //if(o.isBuffered())

        ///   TEXTURE CREATION
        TexturePaint tp;
        BufferedImage bim = null;

        int width = 2500, height = 2500;
        bim = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bim.createGraphics();
        for (int i = 0; i < width; i+=5) {
            g2.setPaint(pen.color);
            g2.drawLine(0, i, i, 0);
            g2.drawLine(width - i, height, width, height - i);
        }
        Rectangle r = new Rectangle(0, 0, bim.getWidth(), bim.getHeight());
        tp = new TexturePaint(bim, r);

        ///


        Shape s = transform.createTransformedShape(zoneShape.shape());
        g2D.setPaint(tp);
        g2D.fill(s);

        g2D.setPaint(pOrj);
        transform.scale(1,-1);
        g2D.setTransform(transform);



        if (title!=null && !title.isEmpty())
            g2D.drawString(title,titleX,-1*titleY);


        transform.scale(1,-1);

        g2D.setTransform(tOrj);
        g2D.setColor(orjColor);
    }

    @Override
    public int compareTo(SpecialZoneInterface o) {
        return Long.compare(this.id,o.getId());  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Shape shape() {
        return zoneShape.shape();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Shape shape(int scale) {
        return zoneShape.shape(scale);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Pen pen() {
        return pen;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
