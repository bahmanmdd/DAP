package pgraph.anya.experiments;

import pgraph.util.StringUtil;

/**
 * Created by Dindar on 22.8.2014.
 */
public interface ExperimentInterface {

    public String getTitle();
    public String getMapFile();
    public int getXSize();
    public int getYSize();
    public int getStartX();
    public int getStartY();
    public int getEndX();
    public int getEndY();
    public double getUpperBound();

    public void setTitle(String t);

}
