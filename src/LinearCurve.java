import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;

public class LinearCurve extends Curve {

  private Vertex head;
  private Vertex tail;

  private double sumAngle;
  private double sumDistance;
  private double sumDistanceSquared;
  private int numEdges;

  public LinearCurve(Edge edge) {
    super();

    edges.add(edge);
    head = edge.getHead();
    tail = edge.getTail();

    sumAngle = 0;
    sumDistance = edge.distance();
    sumDistanceSquared = edge.distanceSquared();
    numEdges = 1;
  }

  public void connect(Edge edge) {
//    if (head == null || tail == null) {
//     edges.add(edge);
//      head = edge.getHead();
//      tail = edge.getTail();
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
      calcAngle(edge, getHeadEdge());
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

    sumDistance += edge.distance();
    sumDistanceSquared += edge.distanceSquared();
    numEdges += 1;
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
    return sumDistance / numEdges;
  }

  public double distanceStdDev() {
    if (numEdges == 1){
      return 0;
    }
    return Math.sqrt(sumDistanceSquared / numEdges - Math.pow(distanceMean(), 2));
  }

  public double angleMean(){
    if (numEdges == 1){
      return 0;
    }
    return sumAngle / (numEdges - 1);
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
    double x = (vertex2.getX() - vertex1.getX()) * (vertex2.getX() - vertex3.getX());
    double y = (vertex2.getY() - vertex1.getY()) * (vertex2.getY() - vertex3.getY());

    double dotProduct = x + y;

    sumAngle += Math.acos(dotProduct / (e1.distance() * e2.distance())) * 180 / Math.PI;
  }

  //TODO change disconnect
  public void disconnect(Vertex v1, Vertex newpoint, Vertex v2, Edge v1newpoint, Edge newpointv2){
    Integer pos = null;
    for (Edge e : edges){
      if (e.getHead() == v1 && e.getTail() == v2){
        pos = edges.indexOf(e);
      } else if (e.getHead() == v2 && e.getTail() == v1){
        pos = edges.indexOf(e);
      }
    }
    if (pos!=null){
      edges.remove(edges.get(pos));
      if (!(edges.get(pos-1).getHead() == v1newpoint.getTail() || edges.get(pos-1).getTail() == v1newpoint.getHead())){
        v1newpoint.reverse();
      }
      if (pos >= edges.size()){
        if (!(v1newpoint.getHead() == newpointv2.getTail() || v1newpoint.getTail() == newpointv2.getHead())){
          newpointv2.reverse();
        }
      } else {
        if (!(edges.get(pos).getTail() == newpointv2.getHead() || edges.get(pos).getHead() == newpointv2.getTail())) {
          newpointv2.reverse();
        }
      }
      edges.add(pos, newpointv2);
      edges.add(pos, v1newpoint);
    }
  }

  public List<Vertex> getAdjacent(Vertex vertex){
    List<Vertex> vertexlist = new ArrayList<>();
    for (Edge e : edges){
      if (e.getHead() == vertex){
        vertexlist.add(e.getTail());
      } else if (e.getTail() == vertex){
        vertexlist.add(e.getHead());
      }
    }
    return vertexlist;
  }
}
