package pgraph.ranga;

import pgraph.LineEdge;
import pgraph.ObstacleInterface;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.tag.TangentArcDirectedGraph;
import pgraph.util.GraphFactory;
import pgraph.util.Pen;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 17.03.2013
 * Time: 09:26
 * To change this template use File | Settings | File Templates.
 */
public class RangaInputPreparer {

    public static void  createEdgetData(String edFileName,GridDirectedGraph g) throws IOException, InstantiationException {
        BufferedWriter br  = new BufferedWriter(new FileWriter(edFileName));
        int i=0;
        for(BaseEdge e :g.edgeSet)
        {
            if (e == null)
                continue;
            if (e.end == null)
                continue;


            double weight = 0.5*g.getTotalIntersectionCount(e);

            br.write((e.start.id+1)+"\t"+(e.end.id+1)+"\t"+ e.getEdgeWeight()+ "\t"+ weight );br.newLine();

        }

     br.close();
    }

    public static void createEdgetData(String edFileName,BaseDirectedGraph tag) throws IOException, InstantiationException {
        BufferedWriter br  = new BufferedWriter(new FileWriter(edFileName));

        for(BaseEdge e :tag.edgeSet)
        {
            if (e == null)
                continue;
            if (e.end == null)
                continue;


            double weight = 0.5*tag.getTotalIntersectionCount(e);

            br.write((e.start.id+1)+"\t"+(e.end.id+1)+"\t"+ e.getEdgeWeight()+ "\t"+ weight );br.newLine();
        }

        br.close();
    }


    public static void createPathVars(String edFileName,BaseDirectedGraph g,BaseVertex s, BaseVertex e,int K) throws IOException, InstantiationException {
        BufferedWriter br  = new BufferedWriter(new FileWriter(edFileName));

        br.write(""+g.vertices.size());br.newLine();           // Vertex Count
        br.write(""+g.edgeSet.size());br.newLine();            // Edge Count
        br.write(""+1);br.newLine();                           // Number of Resource always 1
        br.write(""+((int)s.id+1));br.newLine();                 // Start
        br.write(""+((int)e.id+1));br.newLine();                 // End
        br.write(""+K);  br.newLine();                        // Weight Limit
        br.write(""+1);  br.newLine();
        br.write(""+g.vertices.size());  br.newLine();
        br.close();
    }


    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        testGrid();
        //testTAG();
    }

    public static void testGrid() throws IOException, InstantiationException {
        GridDirectedGraph g = GraphFactory.createGrid("maps/random1.txt",100,0,49,99,49,1,100,100,1);
        createEdgetData("rangaInputs/EdgeData.txt",g);
        createPathVars("rangaInputs/PathVars.txt",g,g.start,g.end,2);
    }

    public static void testTAG() throws IOException, InstantiationException {
        TangentArcDirectedGraph tag = GraphFactory.createTAG("maps/random1.txt", 100, new Point2D.Double(50, 100), new Point2D.Double(50, 1));
        createEdgetData("rangaInputs/EdgeData.txt",tag);
        createPathVars("rangaInputs/PathVars.txt",tag,tag.start,tag.end,2);
    }

}
