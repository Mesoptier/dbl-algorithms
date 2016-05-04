public class Edge {

  private Vertex head;
  private Vertex tail;
  private int mark;

  public Edge(Vertex head, Vertex tail) {
    this(head, tail, 0);
  }

  public Edge(Vertex head, Vertex tail, int mark) {
    this.head = head;
    this.tail = tail;
    this.mark = mark;
  }

  public Edge(Edge edge) {
    this(edge.head, edge.tail, edge.mark);
  }

  public Vertex getHead() {
      return head;
  }

  public Vertex getTail() {
      return tail;
  }

  public int getMark() {
    return mark;
  }

  public void setMark(int mark) {
    this.mark = mark;
  }

  public void nullify() {
    this.head = null;
    this.tail = null;
  }

}
