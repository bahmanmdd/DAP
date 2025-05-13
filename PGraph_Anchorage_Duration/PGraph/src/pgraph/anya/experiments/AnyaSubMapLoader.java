package pgraph.anya.experiments;

import pgraph.anya.AnyaGrid;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Dindar on 22.8.2014.
 */
public class AnyaSubMapLoader implements AnyaMapLoaderInterface {

    AnyaGrid preBuiltMap= null;

    int minX = 0;
    int maxX = 0;
    int minY = 0;
    int maxY = 0;

    public AnyaSubMapLoader(int minX, int maxX, int minY, int maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public AnyaGrid getPreBuiltMap() {
        return preBuiltMap;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }


    @Override
    public AnyaGrid loadMap(String mapFile) throws IOException {



        FileReader fstream = null;
        fstream = new FileReader(mapFile);
        BufferedReader in= new BufferedReader(fstream);

        String mapType = in.readLine();
        String stHeight = in.readLine();
        String stWidth = in.readLine();
        String dummy = in.readLine();

        int xSize = Integer.parseInt( stWidth.split(" ")[1]);
        int ySize = Integer.parseInt( stHeight.split(" ")[1]);

        if (xSize+1 <maxX)
            maxX = xSize+1;
        if (ySize+1 <maxY)
            maxY = ySize+1;

        AnyaGrid ag = new AnyaGrid(maxX-minX+1,maxY-minY+1,1,new Point2D.Double(0,0),0,0,0,0);

        for (int y=0;y<ySize; y++)
        {
            String mapLine = in.readLine();
            if (y<minY || y>= maxY)
                continue;
            for (int x =0 ; x<xSize;x++)
            {
                if (x<minX || x>=maxX)
                    continue;
                ag.getCell(x-minX,y-minY).setTraversable( mapLine.charAt(x)=='.');
            }
        }

        return ag;
    }
}
