package pgraph.anchorage;

import pgraph.gui.BaseGraphPanel;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar
 * Date: 25.11.2013
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class AnchorAreaList implements  BaseGraphPanel.Renderable {
    List<AnchorArea> areaList;

    public AnchorAreaList(List<AnchorArea> areaList) {
        this.areaList = areaList;
    }

    public List<AnchorArea> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<AnchorArea> areaList) {
        this.areaList = areaList;
    }

    @Override
    public void draw(Graphics2D g, BaseGraphPanel.ViewTransform transform) {
       for (AnchorArea a: areaList)
           a.draw(g,transform);
    }

    @Override
    public Rectangle2D boundingRect() {

        Rectangle2D br = new Rectangle2D.Double(0,0,0,0);

        for (AnchorArea a: areaList)
        {
            br = br.createUnion(a.boundingRect());
        }
        return br;
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }


    public void reset() {
        for (AnchorArea a: areaList)
            a.reset();
    }

    public void resetSummary() {
        for (AnchorArea a: areaList)
            a.resetSummary();
    }

    public void updateSummary() {
        for (AnchorArea a: areaList)
            a.updateSummaryStatistics();
    }

    public void printSummary(String configHeader) throws IOException {
        for (AnchorArea a: areaList)
            a.printSummaryStatistics(configHeader);
    }
}
