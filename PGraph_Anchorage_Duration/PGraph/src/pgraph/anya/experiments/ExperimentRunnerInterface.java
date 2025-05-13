package pgraph.anya.experiments;

import pgraph.anya.AnyaGrid;

import java.io.IOException;

/**
 * Created by Dindar on 22.8.2014.
 */
public interface ExperimentRunnerInterface {

    public void runExperiment(ExperimentInterface exp) throws IOException;

    public void setOutputFile(String outputFileName);
    public void printHeader(String outputFile) throws IOException;
}
