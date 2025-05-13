package pgraph.anya;

import pgraph.util.RandUtil;
import pgraph.grid.GridPosition;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Dindar on 20.8.2014.
 */
public class RandomAOG implements AnyaObstacleGenerator{
    double density = 0;

    String fileName=null;


    public RandomAOG(double density, String fileName) {
        this.density = density;
        this.fileName = fileName;
    }


    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void generate(AnyaGrid g) {

        boolean obstacle = false;

        List<AnyaCell> cellList = null;
        FileWriter fstream = null;
        BufferedWriter out = null;

        g.nonTraversableCells.clear();

        for (AnyaCell c: g.cells)
        {
            obstacle = RandUtil.instance.nextBoolean(density);

            c.setTraversable(!obstacle);
            if (g.isObscured(g.start)|| g.isObscured(g.end)) {
                obstacle = false;
                c.setTraversable(!obstacle);
            }
            if (obstacle)
                g.nonTraversableCells.add(c);
        }

        if ( fileName != null)
        {
            try {
                fstream = new FileWriter(fileName);
                out = new BufferedWriter(fstream);
                out.write(""+ g.nonTraversableCells.size());
                out.newLine();

                for(AnyaCell c:g.nonTraversableCells)
                {
                    out.write(c.getCx()+ " "+ c.getCy());
                    out.newLine();
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void saveToMovingAIFile(String fileName,AnyaGrid ag) throws IOException
    {
        FileWriter fstream = new FileWriter(fileName);
        BufferedWriter out = new BufferedWriter(fstream);

        out.write("type octile");
        out.newLine();
        out.write("height "+ (ag.xSize-1) );
        out.newLine();
        out.write("width " + (ag.ySize-1) );
        out.newLine();
        out.write("map");
        out.newLine();
        for (int y= 0 ; y<ag.ySize-1;y++)
        {
            for (int x= 0; x<ag.xSize-1;x++)
            {
                if (ag.getCell(x,y).isTraversable())
                    out.write(".");
                else
                    out.write("@");
            }
            out.newLine();
        }

        out.close();
    }


    public static AnyaGrid createValidMap(int xSize, int ySize, double density,GridPosition s ,GridPosition t)
    {
        RandomAOG r = new RandomAOG(density,null);
        AnyaGrid ag = new AnyaGrid(xSize,ySize,1, new Point2D.Double(0,0),s.getX(),s.getY(),t.getX(),t.getY());

        while (true) {

            r.generate(ag);
            ag.reset();
            ag.search_best_first(Long.MAX_VALUE);
            if (ag.getPathLength()>=0)
                return ag;
        }

    }

    public static void generateRandomMaps(int mapCount, String scenarioFile, String mapFolder , int xSize, int ySize, double density,GridPosition s ,GridPosition t ) throws IOException {
;       FileWriter fstream = new FileWriter(scenarioFile);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("version 1.0");
        out.newLine();

        for (int i=1; i<=mapCount;i++)
        {
            String mapfile = mapFolder+ "/random_"+ xSize+"x"+ySize+"_"+density+"_"+i+".map";

            out.write("1\t"+ mapfile+ "\t" +(xSize-1)+ "\t"+ (ySize-1)+ "\t"+ s.getX()+ "\t"+s.getY()+ "\t"+ t.getX()+ "\t"+t.getY()+"\t"+"100000" );
            out.newLine();
            AnyaGrid ag = createValidMap(xSize,ySize,density,s,t);

            saveToMovingAIFile(mapfile,ag);
        }

        out.close();

    }


    public static void main(String args[]) throws IOException {

        Random r = new Random();
//        long l = 0;
//        while(l<1)
//            System.out.println(r.nextDouble()<0.2);

        GridPosition s = new GridPosition(2,0);
        GridPosition t = new GridPosition(97,100);

        //generateRandomMaps(500,"maps/randoms/scenarios/random_100_0.1.scen","maps/randoms",101,101,0.1,s,t);
        //generateRandomMaps(500,"maps/randoms/scenarios/random_100_0.2.scen","maps/randoms",101,101,0.2,s,t);
        generateRandomMaps(500,"maps/randoms/scenarios/random_100_0.3.scen","maps/randoms",101,101,0.3,s,t);
        generateRandomMaps(500,"maps/randoms/scenarios/random_100_0.05.scen","maps/randoms",101,101,0.05,s,t);
        generateRandomMaps(500,"maps/randoms/scenarios/random_100_0.00.scen","maps/randoms",101,101,0.0,s,t);

        s = new GridPosition(2,0);
        t = new GridPosition(497,500);

        generateRandomMaps(500,"maps/randoms/scenarios/random_500_0.1.scen","maps/randoms",501,501,0.1,s,t);
        generateRandomMaps(500,"maps/randoms/scenarios/random_500_0.2.scen","maps/randoms",501,501,0.2,s,t);
        generateRandomMaps(500,"maps/randoms/scenarios/random_500_0.3.scen","maps/randoms",501,501,0.3,s,t);
        generateRandomMaps(500,"maps/randoms/scenarios/random_500_0.05.scen","maps/randoms",501,501,0.05,s,t);
        generateRandomMaps(500,"maps/randoms/scenarios/random_500_0.00.scen","maps/randoms",501,501,0.0,s,t);
    }
}
