package pgraph.anya;

import java.io.*;

/**
 * Created by Dindar on 20.8.2014.
 */
public class FileAOG implements AnyaObstacleGenerator{

    String fileName=null;

    public FileAOG(String fileName) {
        this.fileName = fileName;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void generate(AnyaGrid g) {

        boolean traversable = false;

        g.nonTraversableCells.clear();
        FileReader fstream = null;
        try {
            fstream = new FileReader(fileName);
            BufferedReader in= new BufferedReader(fstream);
            int obsCount = Integer.parseInt(in.readLine());
            for (int i=0;i <obsCount;i++)
            {
                String cellLine = in.readLine();
                String coords[] = cellLine.split(" ");
                if (coords.length!= 2)
                    continue;
                int x = Integer.parseInt( coords[0]);
                int y = Integer.parseInt( coords[1]);
                g.getCell(x,y).setTraversable(false);
                g.nonTraversableCells.add(g.getCell(x,y));
            }
            in.close();
            fstream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
