package pgraph;

import pgraph.tag.TagDisk;
import pgraph.util.IdManager;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 12/12/12
 * Time: 7:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileBasedTagDiskGenerator implements ObstacleGenerator {

    String fileName= null;
    int count = 0;

    public FileBasedTagDiskGenerator(String inputFile, int c)
    {
        this.fileName = inputFile;
        this.count= c;
    }

    public List<? extends ObstacleInterface> generate() throws IOException
    {
        ArrayList<ObstacleInterface> disks = new ArrayList<ObstacleInterface>();

        FileReader fr = new FileReader((fileName));
        BufferedReader br = new BufferedReader(fr);


        for (int i=0;i<count;i++)
        {
            String line = br.readLine();
            String[] parsed = line.split("[( )+\t]");
            ObstacleInterface d = new TagDisk(IdManager.getObstacleId(),1,new Point2D.Double(Double.parseDouble(parsed[0]),Double.parseDouble(parsed[1])),Double.parseDouble(parsed[2]) );
            disks.add(d);

        }

        return disks;
    }
}
