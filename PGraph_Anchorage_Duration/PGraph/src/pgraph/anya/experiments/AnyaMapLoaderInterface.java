package pgraph.anya.experiments;

import pgraph.anya.AnyaGrid;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Dindar on 22.8.2014.
 */
public interface AnyaMapLoaderInterface extends MapLoaderInterface {

    public AnyaGrid loadMap(String mapFile) throws IOException;

}
