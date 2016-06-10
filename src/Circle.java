public class Circle {

  private Vertex center;
  private double radius;

  public Circle(Vertex center, double radius) {
    this.center = center;
    this.radius = radius;
  }

  public double getCenterX() {
    return center.getX();
  }

  public double getCenterY() {
    return center.getY();
  }

  public double getRadius() {
    return radius;
  }

}
