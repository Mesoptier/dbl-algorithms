import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Edge {

  private Vertex head;
  private Vertex tail;
  private EdgeData data;

  public Edge(Vertex head, Vertex tail, EdgeData data) {
    this.head = head;
    this.tail = tail;
    this.data = data;
  }

  public Edge(Vertex head, Vertex tail) {
    this(head, tail, null);
  }

  public Edge(Edge edge) {
    this(edge.head, edge.tail, edge.data);
  }

  public Vertex getHead() {
      return head;
  }

  public Vertex getTail() {
      return tail;
  }

  public EdgeData getData() {
    return data;
  }

  public void setData(EdgeData data) {
    this.data = data;
  }

  public void nullify() {
    this.head = null;
    this.tail = null;
    this.data = null;
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

  public Vertex intersects(Edge edge) {
    Line2D line1 = new Line2D() {
      @Override
      public double getX1() {
        return head.getX();
      }

      @Override
      public double getY1() {
        return head.getY();
      }

      @Override
      public Point2D getP1() {
        /*return new Point2D() {
          @Override
          public double getX() {
            return head.getX();
          }

          @Override
          public double getY() {
            return head.getY();
          }

          @Override
          public void setLocation(double x, double y) {

          }

        };*/
      return null;
      }

      @Override
      public double getX2() {
        return tail.getX();
      }

      @Override
      public double getY2() {
        return tail.getY();
      }

      @Override
      public Point2D getP2() {
        return null;
      }

      @Override
      public void setLine(double x1, double y1, double x2, double y2) {

      }

      @Override
      public Rectangle2D getBounds2D() {
        return null;
      }
    };
    Line2D line2 = new Line2D() {
      @Override
      public double getX1() {
        return edge.getHead().getX();
      }

      @Override
      public double getY1() {
        return edge.getHead().getY();
      }

      @Override
      public Point2D getP1() {
        return null;
      }

      @Override
      public double getX2() {
        return edge.getTail().getX();
      }

      @Override
      public double getY2() {
        return edge.getTail().getY();
      }

      @Override
      public Point2D getP2() {
        return null;
      }

      @Override
      public void setLine(double x1, double y1, double x2, double y2) {

      }

      @Override
      public Rectangle2D getBounds2D() {
        return null;
      }
    };
    if (line1.intersectsLine(line2) ) {
      double x1 = line1.getX1();
      double y1 = line1.getY1();
      double dx1 = line1.getX2() - x1;
      double dy1 = line1.getY2() - y1;

      double x2 = line2.getX1();
      double y2 = line2.getY1();
      double dx2 = line2.getX2() - x2;
      double dy2 = line2.getY2() - y2;

      double det = dx2 * dy1 - dy2 * dx1;
      if (det != 0) {
        double z = (dx2 * (y2 - y1) + dy2 * (x1 - x2)) / det;
        if (!(z == 0 && z == 1)) { //else it intersects of edge
          return new Vertex(x1 + z * dx1, y1 + z * dy1);
        }
      }
    }
    return null;
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
