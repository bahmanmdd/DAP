package pgraph;

import pgraph.util.Pen;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 13.01.2013
 * Time: 20:03
 * To change this template use File | Settings | File Templates.
 */
public interface Drawable {
    public Shape shape();
    public Shape shape(int scale);
    public Pen pen();
}
