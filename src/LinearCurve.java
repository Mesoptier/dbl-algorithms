public class LinearCurve extends Curve {

  private Vertex head;
  private Vertex tail;

  @Override
  public void connect(Vertex head, Vertex tail) {
    if (this.head == null || this.tail == null) {
      edges.add(new Edge(head, tail));
      this.head = head;
      this.tail = tail;
    } else if (this.tail.equals(head)) {
      edges.add(new Edge(head, tail));
      this.tail = tail;
    } else if (this.tail.equals(tail)) {
      edges.add(new Edge(tail, head));
      this.tail = head;
    } else if (this.head.equals(tail)) {
      edges.add(0, new Edge(head, tail));
      this.head = head;
    } else if (this.head.equals(head)) {
      edges.add(0, new Edge(tail, head));
      this.head = tail;
    }
  }

}
