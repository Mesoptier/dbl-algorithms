public class EdgePool extends ObjectPool<Edge> {

  @Override
  public Edge createExpensiveObject() {
    return new Edge(null, null);
  }

  public Edge borrow(Vertex v1, Vertex v2) {
    Edge edge = borrow();
    edge.setHead(v1);
    edge.setTail(v2);
    return edge;
  }

}
