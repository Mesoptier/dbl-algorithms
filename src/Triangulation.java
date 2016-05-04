import java.awt.*;
import java.util.Vector;

/** This class allows the Delaunay triangulation and the crust for a set of vertices to be computed and
 * provides the capability for the edges to be drawn. The triangles are stored in a vector. The
 algorithm
 * computes the triangles in the triangulations via the Bowyer/Watson algorithm.
 * @author Martha Kosa
 */
public class Triangulation
{
    /** holds the vertices to be triangulated */
    private Vector vertices;
    /** holds the triangles of the current triangulation */
    private Vector triangles;
    /** the first vertex in the supertriangle constructed to contain all vertices to be triangulated */
    private Vertex s0;
    /** the second vertex in the supertriangle constructed to contain all vertices to be triangulated */
    private Vertex s1;
    /** the third vertex in the supertriangle constructed to contain all vertices to be triangulated */
    private Vertex s2;
    /** constructor to initialize the vertices for which the Delaunay
     * triangulation is to be computed. */
    public Triangulation(Vector vertices)
    {
        this.vertices = new Vector();
        for (int i = 0; i < vertices.size(); i++)
        {
            Vertex v = (Vertex) vertices.elementAt(i);
            this.vertices.add(new Vertex(v));
        }
        triangles = new Vector();
    }
    /** Computes the crust of the vertices according to the algorithm of Amenta, Bern, and Eppstein.
     * First, the Delaunay triangulation of the original vertices is computed. Then the
     * Voronoi vertices are determined. Finally, the Delaunay triangulation of all the
     * vertices is computed.
     */
    public void doCrustWork()
    {
        doWork();
        Vector vv = getVoronoiVertices();
        triangles.removeAllElements();
        for (int i = 0; i < vv.size(); i++)
        {
            vertices.add(vv.elementAt(i));
        }
        doWork();
    }
    /** Does the work of the incremental Bowyer/Watson Delaunay triangulation algorithm.
     * Assumes at least 3 vertices for a proper triangulation. Then initializes
     * a triangle large enough to contain all input vertices, the "supertriangle".
     * Next, inserts the vertices one at a time. Finally, deletes the supertriangle.
     * The vertex insertion code is a Java translation of C code by Paul Bourke.
     * @see <a href="http://astronomy.swin.edu.au/~pbourke/terrain/triangulate">Triangulate</a>
     */
    public void doWork()
    {
        if (vertices.size() < 3)
            return;
        setupSuperTriangle();
        Vector edges;
        for (int i = 0; i < vertices.size(); i++)
        {
            Vertex v = (Vertex) vertices.elementAt(i);
            System.out.println("Adding vertex: " + v);
            int j = 0;
            edges = new Vector();
            while (j < triangles.size())
            {

                Triangle t = (Triangle) triangles.elementAt(j);
                System.out.println("Checking " + j + " th triangle: " + t);
                if (t.inCircumCircle(v))
                {
                    System.out.println("Vertex in triangle " + j);
                    edges.add(new Edge(t.getV0(), t.getV1()));
                    edges.add(new Edge(t.getV1(), t.getV2()));
                    edges.add(new Edge(t.getV2(), t.getV0()));
                    triangles.remove(j);
                }
                else
                {
                    System.out.println("Vertex not in triangle " + j);
                    j++;
                }
            }
            System.out.println("i: " + i + " " + edges.size());
            for (j = 0; j < edges.size()-1; j++)
            {
                Edge e = (Edge) edges.elementAt(j);
                if (e.getHead() != null && e.getTail() != null)
                {
                    for (int k = j+1; k < edges.size(); k++)
                    {
                        Edge ek = (Edge) edges.elementAt(k);
                        if (ek.getHead() != null && ek.getTail() != null)
                        {
                            if (e.getHead() == ek.getTail() &&
                                    e.getTail() == ek.getHead())
                            {
                                e.nullify();
                                ek.nullify();
                            }
                        }
                    }
                }
            }

            for (j = 0; j < edges.size(); j++)
            {
                Edge e = (Edge) edges.elementAt(j);
                if (e.getHead() != null && e.getTail() != null)
                {
                    Triangle t = new Triangle(e.getHead(), e.getTail(), v);
                    triangles.add(t);
                    System.out.println("Adding new triangle: " + t);
                }
            }
            System.out.println("*** end of processing vertex " + i);
        }
        removeSuperTriangle();
    }
    /** Computes three vertices far away from the given vertices and forms
     * a triangle from them. */
    private void setupSuperTriangle()
    {
 /* set up supertriangle */
        s0 = new Vertex(0, -16384);
        s1 = new Vertex(-32768, 16384);
        s2 = new Vertex(32768, 16384);
        Triangle st = new Triangle(s0, s1, s2);
        triangles.add(st);
    }
    /** Removes any triangles having a vertex belonging to the supertriangle. */
    private void removeSuperTriangle()
    {
        int i = 0;
        while (i < triangles.size())
        {
            Triangle t = (Triangle) triangles.elementAt(i);
            if (t.getV0() == s0 || t.getV0() == s1 || t.getV0() == s2 ||
                    t.getV1() == s0 || t.getV1() == s1 || t.getV1() == s2 ||
                    t.getV2() == s0 || t.getV2() == s1 || t.getV2() == s2)
            {
                triangles.remove(i);
                System.out.println("Removing triangle: " + t);
            }
            else
                i++;
        }
    }

    /** Computes and returns the Voronoi vertices corresponding to the Delaunay triangulation.
     * A Voronoi vertex is the center of the circumcircle formed by the three points of a triangle. */
    private Vector getVoronoiVertices()
    {
        Vector vv = new Vector();
        for (int i = 0; i < triangles.size(); i++)
        {
            Triangle t = (Triangle) triangles.elementAt(i);
            Vertex tcc = t.getCircumCenter();
            tcc.setVoronoi(true);
            vv.add(tcc);
        }
        return vv;
    }
    /** Displays the triangles of the triangulation on the associated graphics context. */
    public void draw(Graphics g)
    {
        for (int i = 0; i < triangles.size(); i++)
        {
            Triangle t = (Triangle) triangles.elementAt(i);
            t.draw(g);
        }
    }

    public Vector getTriangles() {return triangles;}

    public int getTrianglesSize() {return triangles.size();}
}
