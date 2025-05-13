package pgraph.gui;

import org.jgrapht.GraphPath;
import pgraph.FileBasedTagDiskGenerator;
import pgraph.ObstacleInterface;
import pgraph.Path;
import pgraph.alg.ExactPSA;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.tag.TagVertex;
import pgraph.tag.TangentArcDirectedGraph;
import pgraph.util.Pen;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dindar.oz
 * Date: 1/31/13
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseRenderer implements BaseGraphPanel.Renderable {
    BaseGraphPanel.Renderable content;

    List<Shape> shapeList = new ArrayList<Shape>();

    public BaseRenderer(BaseGraphPanel.Renderable bg) {
        this.content = bg;
    }



    @Override
    public void draw(Graphics2D g2D, BaseGraphPanel.ViewTransform transform)
    {
       content.draw(g2D, transform);
    }

    @Override
    public Rectangle2D boundingRect() {
        return content.boundingRect();
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }

    public static TangentArcDirectedGraph createDemoTAG() throws IOException, InstantiationException {

        FileBasedTagDiskGenerator fdg = new FileBasedTagDiskGenerator("maps/random1001.txt",100);
        java.util.List<? extends ObstacleInterface> dzList = fdg.generate();
        TreeSet<ObstacleInterface> disks = new TreeSet<ObstacleInterface>();
        disks.addAll(dzList);

        TangentArcDirectedGraph tag = new TangentArcDirectedGraph(disks,new TagVertex(new Point2D.Double(500, 0)),new TagVertex(new Point2D.Double(500,1000)),new Point2D.Double(10,10));

        ExactPSA escnh = new ExactPSA(0,1);

        GraphPath<BaseVertex,BaseEdge> path1 = escnh.perform(tag,tag.start,tag.end,1);
        GraphPath<BaseVertex,BaseEdge> path2 = escnh.perform(tag,tag.start,tag.end,2);
        GraphPath<BaseVertex,BaseEdge> path3 = escnh.perform(tag,tag.start,tag.end,3);
        GraphPath<BaseVertex,BaseEdge> path4 = escnh.perform(tag,tag.start,tag.end,0);

        tag.pathList.add(new Path(path1,new Pen(Color.red,1,Pen.PenStyle.PS_Normal)));
        tag.pathList.add(new Path(path2,new Pen(Color.green,1,Pen.PenStyle.PS_Normal)));
        tag.pathList.add(new Path(path3,new Pen(Color.blue,1,Pen.PenStyle.PS_Normal)));
        tag.pathList.add(new Path(path4,new Pen(Color.black,1,Pen.PenStyle.PS_Normal)));

        return tag;
    }

    public static TangentArcDirectedGraph createExperimentTag() throws IOException, InstantiationException {
        FileBasedTagDiskGenerator fdg = new FileBasedTagDiskGenerator("maps/random1001.txt",100);
        java.util.List<? extends ObstacleInterface> dzList = fdg.generate();
        TreeSet<ObstacleInterface> disks = new TreeSet<ObstacleInterface>();
        disks.addAll(dzList);


        TangentArcDirectedGraph tag = new TangentArcDirectedGraph(disks,new TagVertex(new Point2D.Double(500,0)),new TagVertex(new Point2D.Double(500,1000)),new Point2D.Double(10,10));

        ExactPSA escnh = new ExactPSA(0,1);
        GraphPath<BaseVertex,BaseEdge> path0 = escnh.perform(tag,tag.start,tag.end,0);
        GraphPath<BaseVertex,BaseEdge> path1 = escnh.perform(tag,tag.start,tag.end,1);
        GraphPath<BaseVertex,BaseEdge> path2 = escnh.perform(tag,tag.start,tag.end,2);
        GraphPath<BaseVertex,BaseEdge> path3 = escnh.perform(tag,tag.start,tag.end,3);
        GraphPath<BaseVertex,BaseEdge> path4 = escnh.perform(tag,tag.start,tag.end,4);
        GraphPath<BaseVertex,BaseEdge> path5 = escnh.perform(tag,tag.start,tag.end,5);
        GraphPath<BaseVertex,BaseEdge> path6 = escnh.perform(tag,tag.start,tag.end,6);

        tag.pathList.add(new Path(path0,new Pen(Color.pink,1,Pen.PenStyle.PS_Normal)));
        tag.pathList.add(new Path(path1,new Pen(Color.red,1,Pen.PenStyle.PS_Normal)));
        tag.pathList.add(new Path(path2,new Pen(Color.green,1,Pen.PenStyle.PS_Normal)));
        tag.pathList.add(new Path(path3,new Pen(Color.blue,1,Pen.PenStyle.PS_Normal)));
        tag.pathList.add(new Path(path4,new Pen(Color.black,1,Pen.PenStyle.PS_Normal)));
        tag.pathList.add(new Path(path5,new Pen(Color.cyan,1,Pen.PenStyle.PS_Normal)));
        tag.pathList.add(new Path(path6,new Pen(Color.magenta,1,Pen.PenStyle.PS_Normal)));

        return tag;
    }



    public static void main(String[] args) throws IOException, InstantiationException
    {
        System.out.println("Graph is being created..");
        TangentArcDirectedGraph tag = createDemoTAG();
        GraphViewer gv = new GraphViewer(tag, GraphViewer.GraphType.GT_BASIC);
        ((BaseGraphPanel)gv.mainPanel).setViewRegion(-100,1000,-100,1000);
        gv.setVisible(true);

    }
}
