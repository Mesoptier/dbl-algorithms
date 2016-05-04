public class Segment {

  private final Vertex point1;
  private final Vertex point2;

  public Segment(Vertex point1, Vertex point2) {
    this.point1 = point1;
    this.point2 = point2;
  }

  public Vertex getPoint1() {
    return point1;
  }

  public Vertex getPoint2() {
    return point2;
  }

  @Override
  public String toString() {
    return "(" + point1 + ") - (" + point2 + ")";
  }

}
