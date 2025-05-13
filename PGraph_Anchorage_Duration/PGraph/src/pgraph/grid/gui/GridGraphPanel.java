package pgraph.grid.gui;

import pgraph.ObstacleInterface;
import pgraph.base.BaseEdge;
import pgraph.grid.GridDirectedGraph;
import pgraph.gui.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class GridGraphPanel extends JComponent implements GraphPanel {

   private static final long serialVersionUID = 1L;
   GridDirectedGraph lg;
   int scale=1;
   public boolean dispLattice = true;
   public boolean dispDiscNo = true;

   public GridGraphPanel(GridDirectedGraph lg) {
     setPreferredSize(new Dimension(900, 900));
     this.lg = lg;
   }

   public void setScale(int scale) {
       this.scale = scale;
   }
   public int getScale(){
       return scale;
   }

    @Override
    public void setViewRegion(double minX, double maxX, double minY, double maxY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setShowGrid(boolean s) {
        dispLattice = s;
    }

    public void paint(Graphics g) {
     Graphics2D g2D = (Graphics2D) g;
     g2D.setColor(Color.green);
     if(dispLattice){
         for (int x=0;x<lg.getxSize()*scale;x=x+1*scale){
             for (int y=0;y<lg.getySize()*scale;y=y+1*scale){

                 if (x!=(lg.getxSize()-1)*scale){
                     Point2D.Double start1 = lg.getPosition(x, y);
                     Point2D.Double end1 = lg.getPosition(x+1*scale, y);
                     Line2D line1 = new Line2D.Double( start1.getX(), start1.getY(), end1.getX(), end1.getY());
                     g2D.draw(line1);

                 }

                 if (y!=(lg.getySize()-1)*scale){
                     Point2D.Double start2 = lg.getPosition(x, y);
                     Point2D.Double end2 = lg.getPosition(x, y+1*scale);
                     Line2D line1 = new Line2D.Double( start2.getX(), start2.getY(), end2.getX(), end2.getY());
                     g2D.draw(line1);
                 }

                   if ((x!=(lg.getxSize()-1)*scale) && (y!=(lg.getySize()-1)*scale)){
                       Point2D.Double start3 = lg.getPosition(x, y);
                       Point2D.Double end3 = lg.getPosition(x+1*scale, y+1*scale);
                       Line2D line1 = new Line2D.Double( start3.getX(), start3.getY(), end3.getX(), end3.getY());
                       g2D.draw(line1);
                       Point2D.Double start4 = lg.getPosition(x, y+1*scale);
                       Point2D.Double end4 = lg.getPosition(x+1*scale, y);
                       Line2D line2 = new Line2D.Double( start4.getX(), start4.getY(), end4.getX(), end4.getY());
                       g2D.draw(line2);
                   }

             }

         }
     }
   /*//display coordinate plane
       g2D.setColor(Color.black);
       g2D.drawLine(20, 0, 20, 8*lg.getySize()+10*8); // y axis
       g2D.drawLine(20, 8*lg.getySize()+10, 8*lg.getxSize()+30, 8*lg.getxSize()+10*8); // x axis
       int xValue=0, yValue=0;
       //pieces of x axis
       for(int j=30;j<=8*lg.getxSize()+30;j=j+10*scale*8){
           g2D.drawLine(j, 8*lg.getySize()+7*8, j, 8*lg.getySize()+13*8);
           g2D.setFont(new Font(Font.SERIF, Font.BOLD, 9));
           g2D.drawString(Integer.toString(xValue),j-2 ,8*lg.getySize()+22*8);
           xValue+=10;
       }
       // pieces of y axis
       for(int k=8*lg.getySize();k>=0;k=k-10*scale*8){
           g.drawLine(17, k, 23, k);
           g.setFont(new Font(Font.SERIF, Font.BOLD, 9));
           g.drawString(Integer.toString(yValue),3 ,k+3*8);
           yValue+=10;
       }*/

     g2D.setColor(Color.black);
     g2D.setStroke(new BasicStroke(1));
     for (ObstacleInterface dz: lg.obstacles)
     {
            g2D.draw(dz.shape(scale));
     }

     for(int i=0;i<lg.pathList.size();i++){
         g2D.setColor(lg.pathList.get(i).getPen().color);
         for (BaseEdge e:lg.pathList.get(i).getPath().getEdgeList())
         {
             g2D.draw(e.shape(scale));
         }
     }
   }

    @Override
    public JComponent getComponent() {
        return this;
    }
}
