/** This class represents a 2D vertex that may or may not be Voronoi.
 * @author Martha Kosa
 */
public class Vertex
{
    /** the x-coordinate for the vertex */
    private double x;
    /** the y-coordinate for the vertex */
    private double y;
    /** indication of whether the vertex is Voronoi */
    private boolean voronoi;
    /** @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     */
    public Vertex(double x, double y)
    {
        this.x = x;
        this.y = y;
        voronoi = false;
    }
    /** copy constructor */
    public Vertex(Vertex v)
    {
        this(v.getX(), v.getY());
    }
    /** no-argument constructor */
    public Vertex()
    {
        this(0,0);
    }
    /** Updates the Voronoi status of the vertex. */
    public void setVoronoi(boolean v)
    {
        voronoi = v;
    }
    /** Returns the Voronoi status of the vertex. */
    public boolean isVoronoi()
    {
        return voronoi;
    }
    /** Returns the x-coordinate of the vertex. */
    public double getX()
    {
        return x;
    }
    /** Returns the y-coordinate of the vertex. */
    public double getY()
    {
        return y;
    }
    /** Returns a string representation of the vertex. */
    public String toString()
    {
        return "(" + x + "," + y + ") voronoi: " + voronoi;
    }
}
