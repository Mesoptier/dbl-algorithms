public class Pacman {
  private Vertex center;
  private double rotation;
  private double radius;

  public Pacman(Vertex center, double radius, double rotation) {
    this.center = center;
    this.rotation = rotation;
    this.radius = radius;
  }

  public double getCenterX(){ return center.getX(); }

  public double getCenterY(){ return center.getY(); }

  public double getRadius(){ return radius; }

  public double getRotation(){ return rotation; }

}
