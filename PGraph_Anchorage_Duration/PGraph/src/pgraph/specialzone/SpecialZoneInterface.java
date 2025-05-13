package pgraph.specialzone;

import pgraph.Drawable;
import pgraph.LineEdge;
import pgraph.base.BaseEdge;
import pgraph.gui.BaseGraphPanel;
import pgraph.util.Pen;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:20
 * To change this template use File | Settings | File Templates.
 */
public interface SpecialZoneInterface extends Drawable , Comparable<SpecialZoneInterface>
{
    public long getId();
    public void setId(long id) ;

    public Pen getPen();
    public String toString();
    public void fromString(String st);


    public boolean isCovering(BaseEdge e);

    public void applyZoneEffect( BaseEdge e);

    public void draw(Graphics2D g2D, BaseGraphPanel.ViewTransform transform);

}
