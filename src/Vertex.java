import java.util.ArrayList;
import java.util.List;

public class Vertex implements Comparable<Vertex> {

  private double x;
  private double y;
  private int id;
  private VertexData data;
  private Vertex closest;
  private int degree = 0;
  private List<Vertex> close = new ArrayList<>();
  private Boolean considered;

  public Vertex(int id, double x, double y) {
    this.x = x;
    this.y = y;
    this.id = id;
    considered = false;
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

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public int getId() {
    return id;
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

  public void decDegree() { degree--; }

  public int getDegree() { return degree; }

  public Boolean getConsidered() { return considered; }

  public void setConsidered(Boolean b) { considered = b; }

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
