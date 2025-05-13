package pgraph.ranga;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;
import pgraph.Path;
import pgraph.alg.ExactPSA;
import pgraph.base.BaseDirectedGraph;
import pgraph.base.BaseEdge;
import pgraph.base.BaseVertex;
import pgraph.grid.GridDirectedGraph;
import pgraph.gui.GraphViewer;
import pgraph.tag.TangentArcDirectedGraph;
import pgraph.util.GraphFactory;
import pgraph.util.Pen;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dindaro
 * Date: 23.03.2013
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class RangaOutputParser {

    public static GraphPath<BaseVertex,BaseEdge> getOptimumPath(BaseDirectedGraph g,String outputFileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(outputFileName));

        String stLine = br.readLine();

        String stPath = stLine.substring(3,stLine.indexOf("}")-1);


        String[] vList = stPath.split(",");
        if (vList.length==0)
            return null;

        double rw = 0;

        ArrayList<BaseEdge> edgeList = new ArrayList<BaseEdge>();


        for (int i =0 ;i<vList.length-1;i++)
        {
            long sID = Long.parseLong(vList[i].trim())-1;
            long eID = Long.parseLong(vList[i+1].trim())-1;
            BaseVertex sv = g.getVertex(sID);
            BaseVertex ev = g.getVertex(eID);
            BaseEdge edge = g.getEdge(sv,ev );
            if (edge == null) {
                boolean aha = true;
                System.out.println("Invalid Edge Found");
                continue;
            }

            edgeList.add(edge);
            rw += edge.getEdgeWeight();
        }
        BaseVertex s = edgeList.get(0).start;
        BaseVertex e = edgeList.get(edgeList.size()-1).end;

        String stWeight = stLine.substring(stLine.indexOf("},{")+3,stLine.indexOf("}}"));
        vList = stWeight.split(",");

        double w = Double.parseDouble(vList[0]);



        GraphPath<BaseVertex,BaseEdge> path = new GraphPathImpl<BaseVertex, BaseEdge>(g,s,e,edgeList,w ) ;



        return path;
    }


    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {

        testGrid();

        //testTAG();
    }

    public static void testGrid() throws IOException, InstantiationException, InterruptedException {
        GridDirectedGraph gg = GraphFactory.createGrid("maps/random1.txt", 100, 49, 99, 49, 1, 1, 100, 100,1);
        gg.setObstacleCost(0.8);
        RangaInputPreparer.createEdgetData("EdgeData.txt",gg);
        RangaInputPreparer.createPathVars("PathVars.txt",gg,gg.start,gg.end,3);

        byte[] bytes = new byte[300];
        Process p = Runtime.getRuntime().exec("rangaInputs/WCSPP.exe");
        p.waitFor();
        p.getInputStream().read(bytes);
        String s= new String(bytes);


        GraphPath<BaseVertex,BaseEdge> pathRanga= getOptimumPath(gg, "optpath.txt");

        gg.pathList.add(new Path(pathRanga, new Pen(Color.magenta))) ;
        GraphViewer.showGraph(gg, GraphViewer.GraphType.GT_BASIC);


        ExactPSA sgnh = new ExactPSA(0.8,0.1);

        GraphPath<BaseVertex,BaseEdge> path4 = sgnh.perform(gg,gg.getStart(),gg.getEnd(),3);

        gg.pathList.add(new Path(path4,new Pen(Color.green,Pen.PenStyle.PS_Normal)));

        System.out.println("Ranga: " + pathRanga.getWeight());
        System.out.println("PSA: " + path4.getWeight());


    }

    public static void testTAG() throws IOException, InstantiationException {
        TangentArcDirectedGraph tag = GraphFactory.createTAG("maps/random1.txt", 100, new Point2D.Double(50, 100), new Point2D.Double(50, 1));
        //RangaInputPreparer.createEdgetData("rangaInputs/rtEdgeData.txt",tag);
        //RangaInputPreparer.createPathVars("rangaInputs/rtPathVars.txt",tag,tag.start,tag.end);
        tag.pathList.add(new Path(getOptimumPath(tag, "rangaInputs/optpath.txt"), new Pen(Color.magenta))) ;


        ExactPSA escnh = new ExactPSA(0,1);

        GraphPath<BaseVertex,BaseEdge> path1 = escnh.perform(tag,tag.start,tag.end,6);

        tag.pathList.add(new Path(path1,new Pen(Color.green,Pen.PenStyle.PS_Dashed)));




        GraphViewer.showGraph(tag, GraphViewer.GraphType.GT_BASIC);
    }

}
