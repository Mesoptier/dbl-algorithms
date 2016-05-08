import java.util.List;
import java.util.ListIterator;

public class LinearCurve extends Curve {

  private Vertex head;
  private Vertex tail;

  public LinearCurve(Edge edge) {
    super();

    edges.add(edge);
    head = edge.head;
    tail = edge.tail;
  }

  public void connect(Edge edge) {
//    if (head == null || tail == null) {
//      edges.add(edge);
//      head = edge.head;
//      tail = edge.tail;
//    } else
    if (tail.equals(edge.head)) {
      // Append
      edges.add(edge);
      tail = edge.tail;
    } else if (tail.equals(edge.tail)) {
      // Reverse & append
      edge.reverse();
      edges.add(edge);
      tail = edge.head;
    } else if (head.equals(edge.tail)) {
      // Prepend
      edges.add(0, edge);
      head = edge.head;
    } else if (head.equals(edge.head)) {
      // Reverse & prepend
      edge.reverse();
      edges.add(0, edge);
      head = edge.tail;
    } else {
      throw new IllegalArgumentException("Could not connect head or tail to curve");
    }
  }

  public void connect(LinearCurve curve) {
    List<Edge> edges = curve.getEdges();

    if (curve.head.equals(head) || curve.head.equals(tail)) {
      ListIterator<Edge> it = edges.listIterator(0);
      while (it.hasNext()) {
        connect(it.next());
      }
    } else {
      ListIterator<Edge> it = edges.listIterator(edges.size());
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
    if (edges.size() == 1) {
      return 0;
    }

    double mean = distanceMean();
    double sum = 0;
    for (Edge edge : edges) {
      sum += Math.pow(edge.distance() - mean, 2);
    }
    return Math.sqrt(sum / (edges.size() - 1));
  }

  public Vertex getHead() {
    return head;
  }

  public Vertex getTail() {
    return tail;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(head.toString());
    for (Edge edge : edges) {
      sb.append(", ").append(edge.getTail().toString());
    }
    return sb.toString();
  }
}
