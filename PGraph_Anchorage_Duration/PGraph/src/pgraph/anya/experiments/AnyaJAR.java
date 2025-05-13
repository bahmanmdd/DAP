package pgraph.anya.experiments;

import pgraph.anya.AnyaGrid;
import pgraph.anya.AnyaObstacleGenerator;
import pgraph.anya.RandomAOG;
import pgraph.gui.GraphViewer;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * Created by Dindar on 28.8.2014.
 */
public class AnyaJAR {
    public static void main(String[] args) throws IOException {

        if (args.length!= 9 && args.length != 8 )
        {
            wrongParameters();
            return;
        }

        int startX = 0;
        int startY = 0;
        int endX = 0;
        int endY = 0;
        long maxDepth = 0; AnyaGrid ag = null;
        try {
            int xSize = Integer.parseInt(args[0]);
            int ySize = Integer.parseInt(args[1]);

            startX = Integer.parseInt(args[2]);
            startY = Integer.parseInt(args[3]);

            endX = Integer.parseInt(args[4]);
            endY = Integer.parseInt(args[5]);

            maxDepth = Long.parseLong(args[6]);

            ag = null;

            if (args.length == 9 )
            {
                if (!args[7].equals("-random")) {
                    wrongParameters();
                    return;
                }
                double or = Double.parseDouble(args[8]);

                ag = new AnyaGrid(xSize,ySize,1,new Point2D.Double(0,0),startX,startY,endX,endY);
                ag.DebugTraceInterval=Long.MAX_VALUE;

                AnyaObstacleGenerator aog = new RandomAOG(or,"anyaRandom.map");

                aog.generate(ag);
            }
            else
            {
                String mapFile = args[8];
                AnyaMapLoaderInterface mapLoader = new AnyaMapLoader();
                ag=  mapLoader.loadMap(mapFile);
            }
        } catch (NumberFormatException e) {
            wrongParameters();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ag.setStart(ag.getVertex(startX,startY));
        ag.setEnd(ag.getVertex(endX, endY));

        ag.setShowIntervals(true);
        ag.search_best_first(maxDepth);

        GraphViewer.showContent(ag);
    }

    private static void wrongParameters() {
        System.out.println("Wrong Parameters!!");
        System.out.println("Usage:");
        System.out.println("1. jarname xSize ySize startX startY endX endY startStep -random obstacleDensity");
        System.out.println("2. jarname xSize ySize startX startY endX endY startStep mapFilePath");
    }
}


