package pgraph.gui;

/** Jan 2013 (RA) **/

import pgraph.ObstacleGenerator;
import pgraph.ObstacleInterface;
import pgraph.RandomDiskObstacleGenerator;
import pgraph.base.BaseDirectedGraph;
import pgraph.grid.GridDirectedGraph;
import pgraph.grid.gui.GridGraphPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class GraphViewer extends JFrame
{
    GraphPanel mainPanel;
    JScrollPane scrollPane;
    JMenuBar mainMenu = new JMenuBar();
	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;


    void _init(BaseGraphPanel.Renderable content, GraphType type)
    {
        _createCenter(content,type);
        _createMainMenu(content);
        setSize(1400,1400);
    }

    void _createMainMenu(BaseGraphPanel.Renderable content)
    {
        JMenu file = new JMenu("File");
        JMenu view = new JMenu("View");
        JMenu zoom = new JMenu("Zoom");
        mainMenu.add(file);
        mainMenu.add(view);
        mainMenu.add(zoom);
        mainMenu.add(new JMenu("Menu2"));
        mainMenu.add(new JMenu("Menu3"));
        mainMenu.add(new JMenu("Menu4"));
        JMenu viewLattice = new JMenu("Lattice");
        JMenu viewPath = new JMenu("Paths");
        JMenu viewDiscNo = new JMenu("Disk Label");
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new MenuActionListener());
        file.add(save);
        JMenuItem print = new JMenuItem("Print");
        print.addActionListener(new MenuActionListener());
        file.add(print);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new MenuActionListener());
        file.add(exit);

        if ( content instanceof BaseDirectedGraph)
        {
            BaseDirectedGraph g = (BaseDirectedGraph)content;
            for(int i = 0; i<g.pathList.size();i++){
                JCheckBox r = new JCheckBox("Path "+i);
                r.addActionListener(new CheckBoxListener());
                r.setActionCommand(""+i);
                r.setSelected(true);
                viewPath.add(r);
            }
        }
        JMenuItem x1,x2,x3,x4,x5,custom;
        zoom.add(x1 = new JMenuItem("1"));
        x1.addActionListener(new MenuActionListener());
        zoom.add(x2 = new JMenuItem("2"));
        x2.addActionListener(new MenuActionListener());
        zoom.add(x3 = new JMenuItem("3"));
        x3.addActionListener(new MenuActionListener());
        zoom.add(x4 = new JMenuItem("4"));
        x4.addActionListener(new MenuActionListener());
        zoom.add(x5 = new JMenuItem("5"));
        x5.addActionListener(new MenuActionListener());
        zoom.add(custom = new JMenuItem("Custom"));
        custom.addActionListener(new MenuActionListener());
        view.add(viewLattice);
        view.add(viewDiscNo);
        view.add(viewPath);
        JRadioButtonMenuItem on,off,on1,off1;
        viewLattice.add(on = new JRadioButtonMenuItem("ON"));
        viewLattice.add(off = new JRadioButtonMenuItem("OFF"));
        viewDiscNo.add(on1 = new JRadioButtonMenuItem("ON"));
        viewDiscNo.add(off1 = new JRadioButtonMenuItem("OFF"));
        on.addActionListener(new MenuActionListener());
        off.addActionListener(new MenuActionListener());
        on1.addActionListener(new MenuActionListener1(mainPanel));
        off1.addActionListener(new MenuActionListener1(mainPanel));
        ButtonGroup btg = new ButtonGroup();
        ButtonGroup btg1 = new ButtonGroup();
        btg.add(on);
        btg.add(off);
        btg1.add(on1);
        btg1.add(off1);

        ///
        add(scrollPane, BorderLayout.CENTER);
        setJMenuBar(mainMenu);
    }
    void _createCenter(BaseGraphPanel.Renderable content, GraphType type)
    {
        mainPanel = _createMainPanel(content,type);
        scrollPane = new JScrollPane(mainPanel.getComponent());
        scrollPane.setPreferredSize(new Dimension(500, 500));
    }
    private GraphPanel _createMainPanel(BaseGraphPanel.Renderable content, GraphType type) {
        if (type == GraphType.GT_GRID && content instanceof GridDirectedGraph)
            return new GridGraphPanel((GridDirectedGraph)content);
        else return new BaseGraphPanel(content);
    }

    public enum GraphType { GT_GRID,GT_BASIC}

    public GraphViewer(BaseGraphPanel.Renderable content)
    {
        _init(content,GraphType.GT_BASIC);
    }




    public GraphViewer(BaseGraphPanel.Renderable content,GraphType type)
	{	
        _init(content,type);
    }

    public void setViewRegion(double minX, double maxX, double minY, double maxY)
    {
        mainPanel.setViewRegion(minX,maxX,minY,maxY);
    }


    public void setShowGrid(boolean s)
    {
        mainPanel.setShowGrid(s);
    }

    public static void showGraph(BaseDirectedGraph g,GraphViewer.GraphType type) {
        GraphViewer gv = new GraphViewer(g, type);
        gv.mainPanel.setViewRegion(-100,100,-100,100);
        gv.setVisible(true);
        gv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void showGraph(BaseDirectedGraph g,GraphViewer.GraphType type,double minX, double maxX, double minY, double maxY) {
        GraphViewer gv = new GraphViewer(g, type);
        gv.mainPanel.setViewRegion(minX,maxX,minY,maxY);
        gv.setVisible(true);
        gv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void showGraph(BaseDirectedGraph g,GraphViewer.GraphType type,double minX, double maxX, double minY, double maxY,boolean sg) {
        GraphViewer gv = new GraphViewer(g, type);
        gv.mainPanel.setViewRegion(minX,maxX,minY,maxY);
        gv.setVisible(true);
        gv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gv.setShowGrid(sg);
    }

    public static void showContent(BaseGraphPanel.Renderable content,double minX, double maxX, double minY, double maxY) {
        GraphViewer gv = new GraphViewer(content);
        gv.mainPanel.setViewRegion(minX,maxX,minY,maxY);
        gv.setVisible(true);
        gv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static final double FRAME_BUFFER=5;

    public static GraphViewer showContent(BaseGraphPanel.Renderable content) {
        GraphViewer gv = new GraphViewer(content);
        Rectangle2D boundingRect = (Rectangle2D)content.boundingRect().clone();
        if (boundingRect.getHeight() >boundingRect.getWidth())
        {
            boundingRect.setRect(boundingRect.getX(),boundingRect.getY(),boundingRect.getHeight(),boundingRect.getHeight());
        }
        else
        {
            boundingRect.setRect(boundingRect.getX(),boundingRect.getY(),boundingRect.getWidth(),boundingRect.getWidth());
        }

        gv.mainPanel.setViewRegion( boundingRect.getMinX()- FRAME_BUFFER ,boundingRect.getMaxX()+FRAME_BUFFER,boundingRect.getMinY()-FRAME_BUFFER,boundingRect.getMaxY()+ FRAME_BUFFER);
        gv.setVisible(true);
        gv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return gv;
    }


    void _actionExit()
    {
        System.exit(0);
    }
    void _actionShowLattice()
    {
        if (mainPanel instanceof GridGraphPanel)
        {
            ((GridGraphPanel)mainPanel).dispLattice=true; //show lattice
            repaint();
        }
    }
    void _actionHideLattice()
    {
        if (mainPanel instanceof GridGraphPanel)
        {
            ((GridGraphPanel)mainPanel).dispLattice=false; //show lattice
            repaint();
        }
    }
    void _actionSave()
    {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            Dimension size = mainPanel.getComponent().getSize();
            BufferedImage image = (BufferedImage) mainPanel.getComponent().createImage(size.width, size.height);
            Graphics g = image.getGraphics();
            mainPanel.getComponent().paint(g);
            g.dispose();

            try {
                File file = fc.getSelectedFile();
                String fileName = file.getPath();
                ImageIO.write(image, "jpg", new File(fileName + ".jpg"));
            }

            catch (IOException e2){
                e2.printStackTrace();

            }
        }
    }
    void _actionZoomLevelDialog()
    {
        String a =JOptionPane.showInputDialog("Please enter an integer zoom value");
        int zoomValue=Integer.parseInt(a);
        mainPanel.setScale(zoomValue);
        zoom(mainPanel.getScale());
        repaint();
    }
    void _actionPrint()
    {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setJobName(" Print Label ");

        pj.setPrintable (new Printable() {
            public int print(Graphics pg, PageFormat pf, int pageNum){
                if (pageNum > 0){
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2 = (Graphics2D) pg;
                g2.translate(pf.getImageableX(), pf.getImageableY());
                mainPanel.getComponent().paint(g2);
                return Printable.PAGE_EXISTS;
            }
        });
        if (pj.printDialog() == false)
            return;

        try {
            pj.print();
        } catch (PrinterException ex) {
            // handle exception
        }

    }
    void _actionZoom(int scale)
    {
        mainPanel.setScale(scale);
        zoom(mainPanel.getScale());
        repaint();
    }
    public void zoom(int scale){
        //mainPanel.getComponent().setPreferredSize(new Dimension(((GridGraphPanel.lg.getxSize() * (int) GridGraphPanel.lg.getUnitEdgeLen()) * scale + 30), ((GridGraphPanel.lg.getySize() * (int) GridGraphPanel.lg.getUnitEdgeLen()) * scale + 30)));
        //mainPanel.getComponent().setSize(new Dimension((GridGraphPanel.lg.getxSize() * (int) GridGraphPanel.lg.getUnitEdgeLen()) * scale + 30, (GridGraphPanel.lg.getySize() * (int) GridGraphPanel.lg.getUnitEdgeLen()) * scale + 30));
    }

    class MenuActionListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		if(e.getActionCommand() == "ON"){
    			_actionShowLattice();
    		}
    		else if(e.getActionCommand() == "OFF"){
                _actionHideLattice();
    		}
    		else if(e.getActionCommand() == "Save"){
                _actionSave();
		    }
    		else if(e.getActionCommand() == "Print"){
                _actionPrint();
    		}
    		else if(e.getActionCommand() == "Exit")
    			_actionExit();
    		else if(e.getActionCommand() == "Custom"){
                _actionZoomLevelDialog();
    		}
    		else {
                _actionZoom(Integer.parseInt(e.getActionCommand()));
    			}	
    		}
    	}

}
class MenuActionListener1 implements ActionListener {

    private final GraphPanel graphPanel;

    MenuActionListener1(GraphPanel gp)
    {
        graphPanel = gp;
    }

    public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "ON"){
            if (graphPanel instanceof GridGraphPanel)
            {
                ((GridGraphPanel)graphPanel).dispLattice=true; //show lattice
                graphPanel.getComponent().repaint();
            }
		}
		else if(e.getActionCommand() == "OFF"){
            if (graphPanel instanceof GridGraphPanel)
            {
                ((GridGraphPanel)graphPanel).dispLattice=false; //show lattice
                graphPanel.getComponent().repaint();
            }
		}
	}
}
class CheckBoxListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(""+0)){
			//System.err.println("0 checked");
		}
		if(e.getActionCommand().equals(""+1)){
			//System.err.println("1 checked");
		}
		if(e.getActionCommand().equals(""+2)){
			//System.err.println("2 checked");
		}
		if(e.getActionCommand().equals(""+3)){
			//System.err.println("3 checked");
		}
	}



}