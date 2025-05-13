package pgraph.anya.experiments;

import pgraph.anya.AnyaGrid;
import pgraph.anya.astar.AStarGrid;

import java.io.IOException;

/**
 * Created by Dindar on 22.8.2014.
 */
public interface AStarMapLoaderInterface extends MapLoaderInterface{

    public AStarGrid loadMap(String mapFile) throws IOException;

}
