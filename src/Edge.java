public class Edge {

  protected Vertex head;
  protected Vertex tail;

  public Edge(Vertex head, Vertex tail) {
    this.head = head;
    this.tail = tail;
  }

  public Edge(Edge edge) {
    this(edge.head, edge.tail);
  }

  public Vertex getHead() {
      return head;
  }

  public Vertex getTail() {
      return tail;
  }

  public void nullify() {
    this.head = null;
    this.tail = null;
  }

  public void reverse() {
    Vertex temp = this.head;
    this.head = this.tail;
    this.tail = temp;
  }

  public double distance() {
    return head.distance(tail);
  }

  public double distanceSquared() {
    return head.distanceSquared(tail);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Edge edge = (Edge) o;
    return (head.equals(edge.head) && tail.equals(edge.tail))
        || (head.equals(edge.tail) && tail.equals(edge.head));
  }

  @Override
  public int hashCode() {
    int headHashCode = head != null ? head.hashCode() : 0;
    int tailHashCode = tail != null ? tail.hashCode() : 0;

    if (headHashCode < tailHashCode) {
      return headHashCode + tailHashCode * 31;
    } else {
      return headHashCode * 31 + tailHashCode;
    }
  }

  @Override
  public String toString() {
    return head.toString() + ", " + tail.toString();
  }
}
