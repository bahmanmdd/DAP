package pgraph.tag.gui;

import pgraph.ObstacleInterface;
import pgraph.Path;
import pgraph.base.BaseEdge;
import pgraph.tag.TangentArcDirectedGraph;

import java.awt.*;


class TangentArcGraphCanvas extends Canvas {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	TangentArcDirectedGraph tag;
    double scale;

    public TangentArcGraphCanvas(TangentArcDirectedGraph tag) {
      setSize(300, 300);
      this.tag = tag;
    }

    public void setScale(double s)
    {
        scale = s;
    }

    public void paint(Graphics g) {
      Graphics2D g2D = (Graphics2D) g;

        g2D.scale(scale,scale);

      g2D.translate(tag.offSet.getX(),tag.offSet.getY());

      g2D.setStroke(new BasicStroke(3));

      for (ObstacleInterface d: tag.obstacles)
      {
       	  g2D.draw(d.shape());
      }

      for (BaseEdge e:tag.edgeSet())
      {
          if ((e.start == tag.start)|| e.end == tag.end ) ;
           //   g2D.draw(e.shape());
      }


        int pc = tag.pathList.size();
        for (int i = 0 ; i<pc;i++ )
        {
            Path p = tag.pathList.get(i);
            g2D.setColor(p.getPen().color);
            if (p != null)
            {
                for (BaseEdge e:p.getPath().getEdgeList())
                {
                    g2D.draw(e.shape());
                }
            }
        }

    }
  }


