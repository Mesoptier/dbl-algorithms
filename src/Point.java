public class Point {

  private final int id;
  private final float x;
  private final float y;

  public Point(int id, float x, float y) {
    this.id = id;
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ") #" + id;
  }
}
