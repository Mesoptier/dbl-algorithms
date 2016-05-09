public class Vertex
{
    private double x;
    private double y;
    private int id;
    private boolean voronoi;

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
    public Vertex(Vertex v)
    {
        this(v.getId(), v.getX(), v.getY());
    }

    public Vertex()
    {
        this(0,0,0);
    }

    public void setVoronoi(boolean v)
    {
        voronoi = v;
    }

    public boolean isVoronoi()
    {
        return voronoi;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public int getId() { return id;}

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

}
