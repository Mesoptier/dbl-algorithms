import java.awt.*;

/** This class represents a 2D triangle.
 * @author Martha Kosa
 */
public class Triangle
{
    /** the first vertex of the triangle */
    private Vertex v0;
    /** the second vertex of the triangle */
    private Vertex v1;
    /** the third vertex of the triangle */
    private Vertex v2;

    /** the center of the circumcircle formed by the vertices of the triangle */
    private Vertex ccc;
    /** the radius of the circumcircle formed by the vertices of the triangle */
    private double ccr;
    /** constructor to initialize a triangle from the three given vertices */
    public Triangle(Vertex v0, Vertex v1, Vertex v2)
    {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        computeCircumCenter();
    }
    /** Returns the first vertex of the triangle. */
    public Vertex getV0()
    {
        return v0;
    }
    /** Returns the second vertex of the triangle. */
    public Vertex getV1()
    {
        return v1;
    }
    /** Returns the third vertex of the triangle. */
    public Vertex getV2()
    {
        return v2;
    }
    /** Returns a string representation of the triangle. */
    public String toString()
    {
        return "Vertices: " + v0 + "/" + v1 + "/" + v2;
    }
    /** Computes the center of the circumcircle formed by the vertices of the triangle.
     * @see <a href="http://mathworld.wolfram.com/Circumcircle.html">Eric W. Weisstein. "Circumcircle."
     * From MathWorld--A Wolfram Web Resource</a> [9] */
    public void computeCircumCenter()
    {
        double ax = v0.getX();
        double ay = v0.getY();
        double bx = v1.getX();
        double by = v1.getY();
        double cx = v2.getX();
        double cy = v2.getY();
        double Dval = 2.0 * (ay*cx + by*ax - by*cx - ay*bx - cy*ax +
                cy*bx);
        double ccx = (by*ax*ax - cy*ax*ax - by*by*ay + cy*cy*ay +
                bx*bx*cy + ay*ay*by + cx*cx*ay - cy*cy*by -
                cx*cx*by - bx*bx*ay + by*by*cy - ay*ay*cy)/Dval;
        double ccy = (cx*ax*ax + cx*ay*ay + bx*bx*ax - bx*bx*cx +
                by*by*ax - by*by*cx - ax*ax*bx - ay*ay*bx -
                cx*cx*ax + cx*cx*bx - cy*cy*ax + cy*cy*bx)/Dval;
        ccc = new Vertex(ccx, ccy);
        ccr = Math.sqrt(Math.pow(ax - ccx, 2) + Math.pow(ay - ccy,
                2));
    }
    /** Returns true if and only if the given vertex lies in the circumcircle formed
     * by the triangle's vertices. */
    public boolean inCircumCircle(Vertex p)
    {
        double ccx = ccc.getX();
        double ccy = ccc.getY();
        return Math.sqrt(Math.pow(p.getX() - ccx, 2) +
                Math.pow(p.getY() - ccy, 2)) <= ccr;
    }
    /** Returns the center vertex of the circumcircle formed by the triangle's
     * vertices. */
    public Vertex getCircumCenter()
    {
        return ccc;
    }

    /** Displays the edges of the triangle on the associated graphics context.
     * Voronoi endpoints are colored black. If the edge has
     * two Voronoi endpoints, it is colored magenta. If it
     * has one Voronoi endpoint, it is colored green. If it
     * has no Voronoi endpoints, it is colored blue. */
    public void draw(Graphics g)
    {
        int multiplier = 300;
        int v0x, v0y, v1x, v1y, v2x, v2y;
        v0x = (int) Math.round(v0.getX()*multiplier);
        v0y = (int) Math.round(v0.getY()*multiplier);
        v1x = (int) Math.round(v1.getX()*multiplier);
        v1y = (int) Math.round(v1.getY()*multiplier);
        v2x = (int) Math.round(v2.getX()*multiplier);
        v2y = (int) Math.round(v2.getY()*multiplier);
        Color oldColor = g.getColor();
        if (v0.isVoronoi())
        {
            g.setColor(Color.black);
            g.fillOval(v0x, v0y, 3, 3);
        }
        if (v1.isVoronoi())
        {
            g.setColor(Color.black);
            g.fillOval(v1x, v1y, 3, 3);
        }
        if (v2.isVoronoi())
        {
            g.setColor(Color.black);
            g.fillOval(v2x, v2y, 3, 3);
        }
        if (v0.isVoronoi() && v1.isVoronoi())
            g.setColor(Color.magenta);
        else if (v0.isVoronoi() || v1.isVoronoi())
            g.setColor(Color.green);
        else
            g.setColor(Color.blue);
        g.drawLine(v0x, v0y, v1x, v1y);
        if (v1.isVoronoi() && v2.isVoronoi())
            g.setColor(Color.magenta);
        else if (v1.isVoronoi() || v2.isVoronoi())
            g.setColor(Color.green);
        else
            g.setColor(Color.blue);
        g.drawLine(v1x, v1y, v2x, v2y);
        if (v2.isVoronoi() && v0.isVoronoi())
            g.setColor(Color.magenta);
        else if (v2.isVoronoi() || v0.isVoronoi())
            g.setColor(Color.green);
        else
            g.setColor(Color.blue);
        g.drawLine(v2x, v2y, v0x, v0y);
        g.setColor(oldColor);
    }
}
