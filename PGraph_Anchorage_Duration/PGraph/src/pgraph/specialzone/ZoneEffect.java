package pgraph.specialzone;

import pgraph.base.BaseEdge;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 22.08.2013
 * Time: 11:36
 * To change this template use File | Settings | File Templates.
 */
public interface ZoneEffect {

    public String toString();
    public void fromString(String st);
    public void fromString(String st, double d);

    public void apply( BaseEdge e);
}
