package pgraph.tag;

import pgraph.base.BaseVertex;
import pgraph.util.IdManager;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 19.01.2013
 * Time: 22:25
 * To change this template use File | Settings | File Templates.
 */
public class TagVertex extends BaseVertex
{
    static long _nextVertexID = 0;

    public TagVertex(Point2D.Double p)
    {
        //super(_nextVertexID++,p);
        super(IdManager.getVertexId(),p);
    }
}
