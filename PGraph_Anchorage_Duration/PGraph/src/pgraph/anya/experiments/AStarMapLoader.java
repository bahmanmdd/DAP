package pgraph.anya.experiments;

import pgraph.anya.AnyaGrid;
import pgraph.anya.astar.AStarGrid;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Dindar on 22.8.2014.
 */
public class AStarMapLoader implements AStarMapLoaderInterface {

    AStarGrid lastBuiltMap= null;
    String  lastBuiltMapFile= null;


    AnyaMapLoaderInterface anyaMapLoader= null;

    public AStarMapLoader(AnyaMapLoaderInterface mapLoader) {
        this.anyaMapLoader = mapLoader;
    }

    @Override
    public AStarGrid loadMap(String mapFile) throws IOException {


        AnyaGrid anyaGrid = anyaMapLoader.loadMap(mapFile);

        AStarGrid asg = lastBuiltMap;

        if (lastBuiltMapFile!= null && lastBuiltMapFile.equals(mapFile)&& lastBuiltMap != null)
        {
            return asg;
        }
        else
        {
            lastBuiltMap = new AStarGrid(anyaGrid);
            asg= lastBuiltMap;
        }

        lastBuiltMapFile = mapFile;

        return asg;
    }
}
