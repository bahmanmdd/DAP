package pgraph.grid.gui;

import org.jgrapht.GraphPath;
import pgraph.*;
import pgraph.alg.PSA;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.tcgrid.TCGridDirectedGraph;
import pgraph.util.Pen;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;


 class GridGraphCanvas extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GridDirectedGraph lg;

    double scale=1;

    boolean showIntersectingEdges = false;

    public GridGraphCanvas(GridDirectedGraph lg) {
      setSize(300, 300);
      this.lg = lg;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void paint(Graphics g) {
      Graphics2D g2D = (Graphics2D) g;
      g2D.scale(scale,scale);

      for (int x = 0 ; x<lg.getxSize(); x++)
      {
    	  Point2D.Double start = lg.getPosition(x, 0);
    	  Point2D.Double end = lg.getPosition(x, lg.getySize() - 1);
    	  Line2D line1 = new Line2D.Double( start.getX(), start.getY(), end.getX(), end.getY());
    	  g2D.draw(line1);
    	  
      }

      for (int y = 0 ; y<lg.getySize(); y++)
      {
    	  Point2D.Double start = lg.getPosition(0, y);
    	  Point2D.Double end = lg.getPosition(lg.getxSize() - 1, y);
    	  Line2D line1 = new Line2D.Double( start.getX(), start.getY(), end.getX(), end.getY());
    	  g2D.draw(line1);
      }

        if (showIntersectingEdges)
        {
            g2D.setColor(Pen.IntersectingEdgePen.color);
            for (BaseEdge e: lg.edgeSet)
            {
                if (e.pen.equals(Pen.IntersectingEdgePen))
                    g2D.draw(e.shape());
            }
        }

        g2D.setColor(Pen.DefaultPen.color);
        g2D.setStroke(new BasicStroke(3));
      for (ObstacleInterface dz: lg.obstacles)
      {
       	  g2D.draw(dz.shape());
      }

      for (Path path: lg.pathList )
      {
          g2D.setColor(path.getPen().color);
          for (BaseEdge e:path.getPath().getEdgeList())
          {
              g2D.draw(e.shape());
          }
      }
      
    }
  }

class TCGridGraphCanvas extends Canvas {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    TCGridDirectedGraph lg;

    double scale=1;
    boolean showIntersectingEdges = false;

    public TCGridGraphCanvas(TCGridDirectedGraph lg) {
        setSize(300, 300);
        this.lg = lg;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.scale(scale,scale);

        for (int x = 0 ; x<lg.template.getxSize(); x++)
        {
            Point2D.Double start = lg.getPosition(x, 0);
            Point2D.Double end = lg.getPosition(x, lg.template.getySize()-1);
            Line2D line1 = new Line2D.Double( start.getX(), start.getY(), end.getX(), end.getY());
            g2D.draw(line1);

        }

        for (int y = 0 ; y<lg.template.getySize(); y++)
        {
            Point2D.Double start = lg.getPosition(0, y);
            Point2D.Double end = lg.getPosition(lg.template.getxSize()-1, y);
            Line2D line1 = new Line2D.Double( start.getX(), start.getY(), end.getX(), end.getY());
            g2D.draw(line1);
        }

        if (showIntersectingEdges)
        {
            g2D.setColor(Pen.IntersectingEdgePen.color);
            for (BaseEdge e: lg.edgeSet)
            {
                if (  e.pen.equals(Pen.IntersectingEdgePen))
                    g2D.draw(e.shape());
            }
        }

        g2D.setColor(Pen.DefaultPen.color);
        g2D.setStroke(new BasicStroke(2));
        for (ObstacleInterface dz: lg.obstacles)
        {
            g2D.draw(dz.shape());
        }

        for (Path path: lg.pathList )
        {
            g2D.setColor(path.getPen().color);
            for (BaseEdge e:path.getPath().getEdgeList())
            {
                g2D.draw(e.shape());
            }
        }

    }
}

public class GraphPlotter extends JFrame
{
    GridGraphCanvas canvas;
    TCGridGraphCanvas tcCanvas;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;

	
	public GraphPlotter(GridDirectedGraph lg)
	{
		canvas = new GridGraphCanvas(lg);
        canvas.setScale(0.8);
   	    getContentPane().add(canvas);
	}
    public GraphPlotter(TCGridDirectedGraph lg)
    {
        tcCanvas = new TCGridGraphCanvas(lg);
        tcCanvas.setScale(0.8);
        getContentPane().add(tcCanvas);
    }


    static GridDirectedGraph generateRandomGraph() throws IOException, InstantiationException {
        GridDirectedGraph lg = new GridDirectedGraph(5,100, 100, 8,new Point2D.Double(10,10), 0 , 99,99,0);


        ObstacleGenerator dzg = new RandomDiskObstacleGenerator(200,lg.getPosition(3,3),lg.getPosition(96,96),20,0);
        List<? extends ObstacleInterface> disks = dzg.generate();

        lg.addObstacles(disks);

        try {
            lg.save("LatticeGraph1.lg");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lg;
    }

    static GridDirectedGraph generateFromFile() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        GridDirectedGraph lg= null;
        try {
            lg = new GridDirectedGraph("LatticeGraph1.lg",5);
        } catch (NumberFormatException | InstantiationException
                | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lg;
    }

    static TCGridDirectedGraph generateTCGridFromFile() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        TCGridDirectedGraph lg= null;
        try {
            lg = new TCGridDirectedGraph("LatticeGraph3.lg",2,2000.0,400, 0);
        } catch (NumberFormatException | InstantiationException
                | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return lg;
    }

    public static GridDirectedGraph performGridExperiment() throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        GridDirectedGraph gg = generateFromFile();
        //GridDirectedGraph gg = generateRandomGraph();

        PSA sgnh = new PSA(1);

        GraphPath<BaseVertex,BaseEdge> path1 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),0);
        GraphPath<BaseVertex,BaseEdge> path2 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),1);
        GraphPath<BaseVertex,BaseEdge> path3 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),3);
        GraphPath<BaseVertex,BaseEdge> path4 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),5);

        gg.pathList.add(new Path(path1,new Pen(Color.black,1,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new Path(path2,new Pen(Color.red,1,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new Path(path3,new Pen(Color.blue,1,Pen.PenStyle.PS_Normal)));
        gg.pathList.add(new Path(path4,new Pen(Color.green,1,Pen.PenStyle.PS_Normal)));

        return gg;
    }

    public static TCGridDirectedGraph performTCGridExperiment() throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        TCGridDirectedGraph gg = generateTCGridFromFile();
        //GridDirectedGraph gg = generateRandomGraph();

        System.out.println("PSA started..");
        PSA sgnh = new PSA(6);

        GraphPath<BaseVertex,BaseEdge> path1 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),2);
    //    GraphPath<BaseVertex,BaseEdge> path2 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),1);
    //    GraphPath<BaseVertex,BaseEdge> path3 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),3);
    //    GraphPath<BaseVertex,BaseEdge> path4 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),5);

        gg.pathList.add(new Path(path1,new Pen(Color.black,1,Pen.PenStyle.PS_Normal)));
    //    gg.pathList.add(new Path(path2,new Pen(Color.red,1,Pen.PenStyle.PS_Normal)));
    //    gg.pathList.add(new Path(path3,new Pen(Color.blue,1,Pen.PenStyle.PS_Normal)));
    //    gg.pathList.add(new Path(path4,new Pen(Color.green,1,Pen.PenStyle.PS_Normal)));

        return gg;
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        //BaseDirectedGraph g = performTCGridExperiment();
        BaseDirectedGraph g = performGridExperiment();


		//GraphPlotter frame = new GraphPlotter((TCGridDirectedGraph)g);
        GraphPlotter frame = new GraphPlotter((GridDirectedGraph)g);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
	}

}
