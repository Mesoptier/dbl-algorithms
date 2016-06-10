import java.util.ArrayList;
import java.util.List;

public class Vertex implements Comparable<Vertex>{

  private double x;
  private double y;
  private int id;
  private boolean voronoi;
  private VertexData data;
  private Vertex closest;
  private int degree = 0;
  private List<Vertex> close = new ArrayList<>();
  private LinearCurve line;

  private boolean hasIncoming;
  private boolean hasOutgoing;

  public Vertex(int id, double x, double y) {
    this.x = x;
    this.y = y;
    this.id = id;
    voronoi = false;
  }

  public Vertex(double x, double y) {
    this(0, x, y);
  }

  public Vertex(Vertex v) {
    this(v.getId(), v.getX(), v.getY());
  }

  public Vertex() {
    this(0, 0, 0);
  }

  public void setVoronoi(boolean v) {
    voronoi = v;
  }

  public boolean isVoronoi() {
    return voronoi;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public int getId() {
    return id;
  }

  public void setHasIncoming(boolean b) {
    hasIncoming = b;
  }

  public void setHasOutgoing(boolean b) {
    hasOutgoing = b;
  }

  public boolean hasIncoming() {
    return hasIncoming;
  }

  public boolean hasOutgoing() {
    return hasOutgoing;
  }

  public VertexData getData() {
    return data;
  }

  public void setData(VertexData data) {
    this.data = data;
  }

  public double distance(Vertex vertex) {
    return Math.sqrt(distanceSquared(vertex));
  }

  public double distanceSquared(Vertex vertex) {
    return (Math.pow(getX() - vertex.getX(), 2) + Math.pow(getY() - vertex.getY(), 2));
  }

  public void setClosest(Vertex v) {
    closest = v;
  }

  public void addClose(Vertex v) {close.add(v); }

  public List<Vertex> getClose() { return close; }

  public Vertex getClosest() { return closest; }

  public void incDegree() { degree++; }

  public int getDegree() { return degree; }

  public void setLine(LinearCurve c) { line = c; }

  public LinearCurve getLine() { return line; }

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

  @Override
  public String toString() {
    return id + " " + x + " " + y;
  }

  //Calculates the angle for /_(v1v2v3) (v2 is the middle vertex)
  public static double calcAngle(Vertex v1, Vertex v2, Vertex v3){
    Double x = (v2.getX() - v1.getX()) * (v2.getX() - v3.getX());
    Double y = (v2.getY() - v1.getY()) * (v2.getY() - v3.getY());

    double dotProduct = x + y;

    Double angle = Math.acos(dotProduct / (v2.distance(v1) * v2.distance(v3))) * 180 / Math.PI;

    return angle;
  }

  @Override
  public int compareTo(Vertex o) {
    if (this.getX() < o.getX()) {
      return -1;
    } else if (this.getX() < o.getX()){
      return 1;
    } else {
      return 0;
    }
  }
}
