import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class LinearCurve extends Curve {

  private Vertex head;
  private Vertex tail;
  private ArrayList<Double> angles;

  public LinearCurve(Edge edge) {
    super();

    edges.add(edge);
    head = edge.getHead();
    tail = edge.getTail();
    angles = new ArrayList<>();
  }

  public void connect(Edge edge) {
//    if (head == null || tail == null) {
//      edges.add(edge);
//      head = edge.head;
//      tail = edge.tail;
//    } else
    if (tail.equals(edge.getHead())) {
      // Append
      calcAngle(edge, getTailEdge());
      edges.add(edge);
      tail = edge.getTail();
    } else if (tail.equals(edge.getTail())) {
      // Reverse & append
      calcAngle(edge, getTailEdge());
      edge.reverse();
      edges.add(edge);
      tail = edge.getTail();
    } else if (head.equals(edge.getTail())) {
      // Prepend
      calcAngle(edge, getTailEdge());
      edges.add(0, edge);
      head = edge.getHead();
    } else if (head.equals(edge.getHead())) {
      // Reverse & prepend
      calcAngle(edge, getHeadEdge());
      edge.reverse();
      edges.add(0, edge);
      head = edge.getHead();
    } else {
      throw new IllegalArgumentException("Could not connect head or tail to curve");
    }
  }

  public void connect(LinearCurve curve) {
    if (curve.equals(this)) {
      throw new IllegalArgumentException("Curve cannot be connected to itself using this method");
    }

    List<Edge> curveEdges = curve.getEdges();

    if (curve.head.equals(this.head) || curve.head.equals(this.tail)) {
      ListIterator<Edge> it = curveEdges.listIterator(0);
      while (it.hasNext()) {
        connect(it.next());
      }
    } else {
      ListIterator<Edge> it = curveEdges.listIterator(curveEdges.size());
      while (it.hasPrevious()) {
        connect(it.previous());
      }
    }
  }

  public double distanceMean() {
    double sum = 0;
    for (Edge edge : edges) {
      sum += edge.distance();
    }
    return sum / edges.size();
  }

  public double distanceStdDev() {
    if (edges.size() <= 1) {
      return 0;
    }

    double mean = distanceMean();
    double sum = 0;
    for (Edge edge : edges) {
      sum += Math.pow(edge.distance() - mean, 2);
    }
    return Math.sqrt(sum / (edges.size() - 1));
  }

  public double angleMean(){
    double sum = 0;
    for (double angle : angles){
      sum += angle;
    }
    return sum;
  }

  public Vertex getHead() {
    return head;
  }

  public Vertex getTail() {
    return tail;
  }

  public Edge getHeadEdge() {
    return edges.get(0);
  }

  public Edge getTailEdge() {
    return edges.get(edges.size() - 1);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(head.toString());
    for (Edge edge : edges) {
      sb.append(", ").append(edge.getTail().toString());
    }
    return sb.toString();
  }

  private void calcAngle(Edge e1, Edge e2){

    Vertex vertex1, vertex2, vertex3;

    if (e1.getHead().equals(e2.getHead())){
      vertex2 = e1.getHead();
      vertex1 = e1.getTail();
      vertex3 = e2.getTail();
    } else if(e1.getTail().equals(e2.getTail())){
      vertex2 = e1.getTail();
      vertex1 = e1.getHead();
      vertex3 = e2.getHead();
    } else if(e1.getHead().equals(e2.getTail())){
      vertex2 = e1.getHead();
      vertex1 = e1.getTail();
      vertex3 = e2.getHead();
    } else {
      vertex2 = e1.getTail();
      vertex1 = e1.getHead();
      vertex3 = e2.getTail();
    }
    Double x = (vertex2.getX() - vertex1.getX()) * (vertex2.getX() - vertex3.getX());
    Double y = (vertex2.getY() - vertex1.getY()) * (vertex2.getY() - vertex3.getY());

    double dotProduct = x + y;

    Double angle = Math.acos(dotProduct / (e1.distance() * e2.distance())) * 180 / Math.PI;

    angles.add(angle);

    //return angle;
  }

}
