package pgraph.anya.experiments;

import java.io.IOException;
import java.util.List;

/**
 * Created by Dindar on 22.8.2014.
 */
public interface ExperimentLoaderInterface {

    public List<ExperimentInterface> loadExperiments(String expFile) throws IOException;
}
