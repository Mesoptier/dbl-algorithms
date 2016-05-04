import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/** This class represents a GUI for displaying Delaunay triangulations and/or
 * crusts for 2D vertices. Vertices can be read in from a file upon initialization,
 * and the user can choose to add vertices via mouse clicks.
 17
 18
 * @author Martha Kosa
 */
public class SimpleTriangulator extends JFrame
{
    /** drawing area for the vertices and edges */
    private TriPanel drawPanel;
    /** container to hold crust checkbox and triangulation button */
    private JPanel crustPanel;
    /** holds the vertices to be triangulated */
    private Vector points;
    /** clicked when the user wants to see the triangulation */
    private JButton triButton;
    /** clicked when the user wants to remove all vertices and clear the display */
    private JButton clearButton;
    /** container holding all GUI components */
    private Container guiContainer;
    /** the triangulation for the set of vertices, calculated on request only */
    private Triangulation tri;
    /** selected when the user wants to compute the crust of the vertices */
    private JCheckBox crustCheck;
    /** constructor assuming no vertices yet */
    public SimpleTriangulator()
    {
        super("Simple Triangulator");

        points = new Vector();
        guiContainer = getContentPane();
        drawPanel = new TriPanel();
        drawPanel.addMouseListener(new MyMouseListener());
        guiContainer.add(drawPanel, BorderLayout.CENTER);
        crustPanel = new JPanel();
        crustCheck = new JCheckBox("Crust");
        crustPanel.add(crustCheck);
        triButton = new JButton("Triangulate");
        triButton.addActionListener(new MyTriangulateListener());
        crustPanel.add(triButton);
        guiContainer.add(crustPanel, BorderLayout.NORTH);
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new MyClearListener());
        guiContainer.add(clearButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        show();
    }
    /** constructor reading vertices from an input file with the given file name */
    public SimpleTriangulator(String fileName)
    {
        this();
        try
        {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null)
            {
                StringTokenizer st = new StringTokenizer(line);
                int x = Integer.parseInt(st.nextToken());
                int y = Integer.parseInt(st.nextToken());
                Vertex v = new Vertex(x, y);
                points.add(v);
                line = br.readLine();
            }
        }
        catch (IOException ioe)
        {
            System.out.println("IO problem");
        }
        drawPanel.repaint();
    }
    public SimpleTriangulator(ProblemInput problemInput){
      points = new Vector();
      guiContainer = getContentPane();
      drawPanel = new TriPanel();
      drawPanel.addMouseListener(new MyMouseListener());
      guiContainer.add(drawPanel, BorderLayout.CENTER);
      crustPanel = new JPanel();
      crustCheck = new JCheckBox("Crust");
      crustPanel.add(crustCheck);
      triButton = new JButton("Triangulate");
      triButton.addActionListener(new MyTriangulateListener());
      crustPanel.add(triButton);
      guiContainer.add(crustPanel, BorderLayout.NORTH);
      clearButton = new JButton("Clear");
      clearButton.addActionListener(new MyClearListener());
      guiContainer.add(clearButton, BorderLayout.SOUTH);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      for (int i=0; i<problemInput.getNumVertices(); i++){
        Vertex vertex = problemInput.getVertices()[i];
        points.add(vertex);
      }
      setSize(500,500);
      show();
    }
    /** inner class to perform event handling for mouse presses */
    public class MyMouseListener extends MouseAdapter
    {
        /** Event handler for mouse presses. Creates a vertex with coordinates
         * corresponding to the mouse coordinates, adds the vertex to the collection,
         * and displays the new vertex. */
        public void mousePressed(MouseEvent e)
        {
            tri = null;
            Vertex p = new Vertex(e.getX(), e.getY());
            points.add(p);
            drawPanel.repaint();
        }
    }
    /** inner class to perform event handling for the triangulation button */
    public class MyTriangulateListener implements ActionListener
    {
        /** Event handler for the triangulation button. Determines if the user wants to see the
         * crust for the vertices in addition to the Delaunay triangulation. */
        public void actionPerformed(ActionEvent e)
        {
            tri = new Triangulation(points);
            if (crustCheck.isSelected())
                tri.doCrustWork();
            else
                tri.doWork();
            drawPanel.repaint();
            System.out.println(tri.getTriangles());
            System.out.println(tri.getTrianglesSize());
        }
    }
    /** inner class to perform event handling for the clear button */
    public class MyClearListener implements ActionListener
    {
        /** Event handler for the clear button. Removes all vertices and clears any previously
         * drawn vertices and/or triangulations. */
        public void actionPerformed(ActionEvent e)
        {
            points.removeAllElements();
            tri = null;
            drawPanel.repaint();
        }
    }
    /** inner class for the drawing area to display vertices and triangulations */
    public class TriPanel extends JPanel
    {
        /** Displays the vertices in red and the triangulation (if it has been computed). */
        public void paint(Graphics g)
        {
            g.clearRect(0,0,getWidth(),getHeight());
            Color oldColor = g.getColor();
            g.setColor(Color.red);
            for (int i = 0; i < points.size(); i++)
            {
                Vertex curr = (Vertex) points.elementAt(i);
                int x = (int) (curr.getX()*300);
                int y = (int) (curr.getY()*300);
                g.fillOval(x,y,3,3);
            }
            g.setColor(oldColor);
            if (tri != null)
                tri.draw(g);
        }
    }
    /** Entry point for the application. If no command-line argument is specified, the application
     * begins with no vertices. Otherwise, the application attempts to read vertices from the file
     * name specified by the first argument and displays the vertices that have been successfully
     * read. */
    public static void main(String[] args)
    {
        SimpleTriangulator st;
        if (args.length == 0)
            st = new SimpleTriangulator();
        else
            st = new SimpleTriangulator(args[0]);
    }
}
