public class Segment {

  private final Vertex vertex1;
  private final Vertex vertex2;

  public Segment(Vertex vertex1, Vertex vertex2) {
    this.vertex1 = vertex1;
    this.vertex2 = vertex2;
  }

  public Vertex getVertex1() {
    return vertex1;
  }

  public Vertex getVertex2() {
    return vertex2;
  }

  @Override
  public String toString() {
    return "(" + vertex1 + ") - (" + vertex2 + ")";
  }

}
