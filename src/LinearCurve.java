import java.util.List;
import java.util.ListIterator;

public class LinearCurve extends Curve {

  private Vertex head;
  private Vertex tail;

  public LinearCurve(Edge edge) {
    super();

    edges.add(edge);
    head = edge.getHead();
    tail = edge.getTail();
  }

  public void connect(Edge edge) {
    if (head == null || tail == null) {
//     edges.add(edge);
//      head = edge.getHead();
//      tail = edge.getTail();
    } else
    if (tail.equals(edge.getHead())) {
      // Append
      edges.add(edge);
      tail = edge.getTail();
    } else if (tail.equals(edge.getTail())) {
      // Reverse & append
      edge.reverse();
      edges.add(edge);
      tail = edge.getTail();
    } else if (head.equals(edge.getTail())) {
      // Prepend
      edges.add(0, edge);
      head = edge.getHead();
    } else if (head.equals(edge.getHead())) {
      // Reverse & prepend
      edge.reverse();
      edges.add(0, edge);
      head = edge.getHead();
    } else {
      throw new IllegalArgumentException("Could not connect head or tail to curve " + toString() + " " + edge.toString());
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

}
