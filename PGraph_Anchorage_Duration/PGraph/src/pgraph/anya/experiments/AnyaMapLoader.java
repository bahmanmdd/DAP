package pgraph.anya.experiments;

import pgraph.anya.AnyaCell;
import pgraph.anya.AnyaGrid;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Dindar on 22.8.2014.
 */
public class AnyaMapLoader implements AnyaMapLoaderInterface {

    AnyaGrid lastBuiltMap= null;
    String  lastBuiltMapFile= null;


    @Override
    public AnyaGrid loadMap(String mapFile) throws IOException {

        FileReader fstream = null;
        fstream = new FileReader(mapFile);
        BufferedReader in= new BufferedReader(fstream);

        String mapType = in.readLine();
        String stHeight = in.readLine();
        String stWidth = in.readLine();
        String stDensity = in.readLine();

        int xSize = Integer.parseInt( stWidth.split(" ")[1]);
        int ySize = Integer.parseInt( stHeight.split(" ")[1]);

        AnyaGrid ag = lastBuiltMap;
        if ( ag != null && ag.xSize==xSize+1 && ag.ySize==ySize+1)
        {
            ag.reset();
        }
        else{
            lastBuiltMap = new AnyaGrid(xSize+1,ySize+1,1,new Point2D.Double(0,0),0,0,0,0);
            ag= lastBuiltMap;
        }
        if (lastBuiltMapFile!= null && lastBuiltMapFile.equals(mapFile))
        {
            return ag;
        }

        lastBuiltMapFile = mapFile;

        ag.nonTraversableCells.clear();
        for (int y=0;y<ySize; y++)
        {
            String mapLine = in.readLine();
            for (int x =0 ; x<xSize;x++)
            {
                AnyaCell c = ag.getCell(x,y);
                c.setTraversable( mapLine.charAt(x)=='.');
                if (!c.isTraversable())
                    ag.nonTraversableCells.add(c);
            }
        }

        return ag;
    }


}
