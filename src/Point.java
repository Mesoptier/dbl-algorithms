public class Point {

  private final int id;
  private final float x;
  private final float y;

  private boolean hasIncoming;
  private boolean hasOutgoing;

  public Point(int id, float x, float y) {
    this.id = id;
    this.x = x;
    this.y = y;
    hasIncoming = false;
    hasOutgoing = false;
  }

  public int getId() {
    return id;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public void setHasIncoming(boolean b) {
    hasIncoming = b;
  }

  public void setHasOutgoing(boolean b) {
    hasOutgoing = b;
  }

  public boolean hasIncoming(){
    return hasIncoming;
  }

  public boolean hasOutgoing() {
    return hasOutgoing;
  }


  public double distanceSquared(Point point) {
    return (Math.pow(getX()- point.getX(),2) + Math.pow(getY() - point.getY(),2));
  }

  @Override
  public String toString() {
    return id + " " + x + " " + y;
  }

}
