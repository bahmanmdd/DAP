package pgraph.util;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 19.01.2013
 * Time: 11:19
 * To change this template use File | Settings | File Templates.
 */
public class IdManager {
    static long edgeCount =0;
    static long vertexCount =0;
    static long obstacleCount =0;
    static long zoneCount = 0 ;

    public static long getEdgeId()
    {
       return edgeCount++;
    }

    public static long getVertexId()
    {
        return vertexCount++;
    }

    public static long getObstacleId()
    {
        return obstacleCount++;
    }

    public static long getZoneId()
    {
        return zoneCount++;
    }


    public static void reset()
    {
        edgeCount =0;
        vertexCount =0;
        obstacleCount =0;
        zoneCount=0;
    }
}
