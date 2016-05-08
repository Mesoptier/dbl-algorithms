import java.util.List;
import java.util.ListIterator;

import javax.sound.sampled.Line;

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

}
