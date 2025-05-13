package pgraph.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 02.02.2013
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
public interface GraphPanel {
    public JComponent getComponent();
    public void setScale(int scale);
    public int getScale();
    public void setViewRegion(double minX, double maxX, double minY, double maxY);
    public void setShowGrid(boolean s);
}
