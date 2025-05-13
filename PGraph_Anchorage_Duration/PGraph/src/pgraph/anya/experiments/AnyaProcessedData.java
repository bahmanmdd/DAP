package pgraph.anya.experiments;

import pgraph.anya.AnyaGrid;
import pgraph.anya.AnyaInterval;
import pgraph.anya.AnyaVertex;

/**
 * Created by Dindar on 6.10.2014.
 */
public class AnyaProcessedData {

    static final int MAX_X = 1024;
    static final int MAX_Y = 1024;

    static  final byte LEFT_SIDE= 0;
    static  final byte RIGHT_SIDE= 1;

    int cornerPoints[][][]= new int [MAX_X][MAX_Y][2];

    AnyaGrid baseGrid = null;

    AnyaProcessedData(AnyaGrid ag)
    {
        baseGrid = ag;
        _buildData();
    }

    private void _buildData()
    {
        AnyaGrid ag = baseGrid;
        for (int y = 0; y<ag.ySize; y++  )
        {
            AnyaVertex v= ag.getVertex(0,y);
            AnyaVertex pv = v;

            cornerPoints[y][0][LEFT_SIDE] = -1;
            while (pv.gridPos.getX()<ag.xSize-1)
            {
                while (ag.isTraversable(v, AnyaVertex.VertexDirections.VD_RIGHT) && !ag.isCorner(v))
                    v = ag.getVertex(v, AnyaVertex.VertexDirections.VD_RIGHT);

                for (int x = pv.gridPos.getX(); x<v.gridPos.getX();x++)
                {
                    cornerPoints[y][x][RIGHT_SIDE] = v.gridPos.getX();
                    cornerPoints[y][x+1][LEFT_SIDE] = pv.gridPos.getX();
                }


                pv = v;


                while (!ag.isTraversable(v, AnyaVertex.VertexDirections.VD_RIGHT))
                {
                    v= ag.getVertex(v, AnyaVertex.VertexDirections.VD_RIGHT);
                }

                for (int x = pv.gridPos.getX(); x<v.gridPos.getX();x++)
                {
                    cornerPoints[y][x][RIGHT_SIDE] = v.gridPos.getX();
                    cornerPoints[y][x+1][LEFT_SIDE]= pv.gridPos.getX();
                }

                pv = v;
            }

            cornerPoints[y][ag.xSize-1][RIGHT_SIDE]= -1;

        }

    }

    public int getLeftCornerPoint(int x, int y)
    {
        return cornerPoints[y][x][LEFT_SIDE];
    }

    public int getLeftCornerPoint( double x , int row)
    {
        AnyaVertex v= baseGrid.getVertex(x,baseGrid.getPosition(0,row).getY(), AnyaVertex.VertexDirections.VD_LEFT);

        if ( Math.abs(v.pos.getX()-x)< AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD )
            return getLeftCornerPoint(v.gridPos.getX(), row);
        else {
            AnyaVertex rv = baseGrid.getVertex(v, AnyaVertex.VertexDirections.VD_RIGHT);
            return getLeftCornerPoint(rv.gridPos.getX(),row);
        }
    }

    public int getRightCornerPoint(int x, int y)
    {
        return cornerPoints[y][x][LEFT_SIDE];
    }

    public int getRightCornerPoint( double x , int row)
    {
        AnyaVertex v= baseGrid.getVertex(x,baseGrid.getPosition(0,row).getY(), AnyaVertex.VertexDirections.VD_LEFT);

        if ( Math.abs(v.pos.getX()-x)< AnyaInterval.DOUBLE_INEQUALITY_THRESHOLD )
            return getRightCornerPoint(v.gridPos.getX(), row);
        else {
            return getRightCornerPoint(v.gridPos.getX(),row);
        }
    }
}
