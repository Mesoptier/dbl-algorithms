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
    private int id;
    private boolean voronoi;
    /** @param x the x-coordinate for the vertex
     * @param y the y-coordinate for the vertex
     */

    private boolean hasIncoming;
    private boolean hasOutgoing;

    public Vertex(int id, double x, double y)
    {
        this.x = x;
        this.y = y;
        this.id = id;
        voronoi = false;
    }

    public Vertex(double x, double y){
        this.x = x;
        this.y = y;
        voronoi = false;
    }
    /** copy constructor */
    public Vertex(Vertex v)
    {
        this(v.getId(), v.getX(), v.getY());
    }
    /** no-argument constructor */
    public Vertex()
    {
        this(0,0,0);
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

    public int getId() { return id;}
    /** Returns a string representation of the vertex. */
    /*
    public String toString()
    {
        return "(" + x + "," + y + ") voronoi: " + voronoi;
    }
    */

    public void setHasIncoming(boolean b) {
      hasIncoming = b;
    }

    public void setHasOutgoing(boolean b) {
      hasOutgoing = b;
    }

    public boolean hasIncoming(){
      return hasIncoming;
    }

    public boolean hasOutgoing() {
      return hasOutgoing;
    }

    public double distanceSquared(Vertex vertex) {
      return (Math.pow(getX()- vertex.getX(),2) + Math.pow(getY() - vertex.getY(),2));
    }
    @Override
    public String toString() {
      return id + " " + x + " " + y;
    }

  // TODO: Compare using id instead of using vertex?
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Vertex vertex = (Vertex) o;
    return x == vertex.x && y == vertex.y;
  }

  // TODO: Compare using id instead of using vertex?
  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  public double distance(Vertex vertex) {
    return Math.sqrt(distanceSquared(vertex));
  }
}
