import java.awt.*;
import java.util.*;
import java.util.List;

public class BetterTriangulation {

  private static final EdgePool edgePool = new EdgePool();

  private final List<Vertex> vertices;
  private final Debug debug;
  private DebugState debugState;
  private HashSet<Triangle> triangulation;
  private Set<Edge> delaunayEdges;
//  private Map<Edge, Integer> delaunayMarks;

  public BetterTriangulation(List<Vertex> vertices) {
    this(vertices, null);
  }

  public BetterTriangulation(List<Vertex> vertices, Debug debug) {
    this.vertices = vertices;
    this.debug = debug;
  }

  public void start() {
    triangulation = new HashSet<>();

    // Add super triangle
    Triangle superTriangle = new Triangle(new Vertex(0.5, 5), new Vertex(3, -1), new Vertex(-2, -1));
    triangulation.add(superTriangle);

    for (Vertex vertex : vertices) {
      if (debug != null) {
        debugState = new DebugState();
        debugState.setMessage("adding vertex " + vertex.toString());
        debugState.addVertices(vertices, Color.GRAY);
        debugState.addVertex(vertex, Color.BLUE);

        for (Triangle triangle : triangulation) {
          debugState.addEdge(triangle.e1, Color.LIGHT_GRAY);
          debugState.addEdge(triangle.e2, Color.LIGHT_GRAY);
          debugState.addEdge(triangle.e3, Color.LIGHT_GRAY);
        }
      }

      Map<Edge, Boolean> polygon = new HashMap<>();

      // Find triangles that are no longer valid after insertion
      Iterator<Triangle> it = triangulation.iterator();
      Triangle triangle;

      while (it.hasNext()) {
        triangle = it.next();

        if (triangle.circumcircleContains(vertex)) {
          polygon.put(triangle.e1, polygon.containsKey(triangle.e1) ? false : true);
          polygon.put(triangle.e2, polygon.containsKey(triangle.e2) ? false : true);
          polygon.put(triangle.e3, polygon.containsKey(triangle.e3) ? false : true);

          it.remove();

          if (debug != null) {
            debugState.addCircle(new Circle(triangle.circumcircleCenter, Math.sqrt(triangle.circumcircleRadiusSquared)));
          }
        }
      }

      for (Map.Entry<Edge, Boolean> edgeBooleanEntry : polygon.entrySet()) {
        Edge edge = edgeBooleanEntry.getKey();
        boolean keepEdge = edgeBooleanEntry.getValue();

        if (debug != null) {
          debugState.addEdge(edge, keepEdge ? Color.GREEN : Color.RED);
        }

        if (keepEdge) {
          triangulation.add(new Triangle(edge, vertex));
        } else {
          edgePool.giveBack(edge);
        }
      }

      if (debug != null) {
        debug.addState(debugState);
      }
    }

    // Remove super triangle
    Iterator<Triangle> it = triangulation.iterator();

    while (it.hasNext()) {
      if (superTriangle.sharesVertexWith(it.next())) {
        it.remove();
      }
    }

    // Create edges
    delaunayEdges = new HashSet<>();
//    delaunayMarks = new HashMap<>();

    for (Triangle triangle : triangulation) {
      delaunayEdges.add(triangle.e1);
      delaunayEdges.add(triangle.e2);
      delaunayEdges.add(triangle.e3);

//      if (triangle.containsCircumcircleCenter()) {
//        delaunayMarks.put(triangle.e1, delaunayMarks.containsKey(triangle.e1) ? delaunayMarks.get(triangle.e1) + 1 : 1);
//        delaunayMarks.put(triangle.e2, delaunayMarks.containsKey(triangle.e2) ? delaunayMarks.get(triangle.e2) + 1 : 1);
//        delaunayMarks.put(triangle.e3, delaunayMarks.containsKey(triangle.e3) ? delaunayMarks.get(triangle.e3) + 1 : 1);
//      }
    }
  }

  public Collection<Edge> getDelaunayEdges() {
    return delaunayEdges;
  }

//  public Map<Edge, Integer> getDelaunayMarks() {
//    return delaunayMarks;
//  }

  class Triangle {

    private static final double EPSILON = 1e-9;

    private Vertex v1;
    private Vertex v2;
    private Vertex v3;
    private Edge e1;
    private Edge e2;
    private Edge e3;
    private Vertex circumcircleCenter;
    private double circumcircleRadiusSquared;

    Triangle(Vertex v1, Vertex v2, Vertex v3) {
      this.v1 = v1;
      this.v2 = v2;
      this.v3 = v3;

      e1 = edgePool.borrow(v1, v2);
      e2 = edgePool.borrow(v2, v3);
      e3 = edgePool.borrow(v3, v1);

      computeCircumcircle();
    }

    Triangle(Edge e1, Vertex v3) {
      this.v1 = e1.getHead();
      this.v2 = e1.getTail();
      this.v3 = v3;

      this.e1 = e1;
      e2 = edgePool.borrow(v2, v3);
      e3 = edgePool.borrow(v3, v1);

      computeCircumcircle();
    }

    private boolean sharesVertexWith(Triangle triangle) {
      return v1.equals(triangle.v1) || v1.equals(triangle.v2) || v1.equals(triangle.v3)
          || v2.equals(triangle.v1) || v2.equals(triangle.v2) || v2.equals(triangle.v3)
          || v3.equals(triangle.v1) || v3.equals(triangle.v2) || v3.equals(triangle.v3);
    }

    private boolean circumcircleContains(Vertex vertex) {
      return circumcircleCenter.distanceSquared(vertex) <= circumcircleRadiusSquared;
    }

    public boolean contains(Vertex vertex) {
      double alpha = ((v2.getY() - v3.getY())*(vertex.getX() - v3.getX()) + (v3.getX() - v2.getX())*(vertex.getY() - v3.getY())) /
          ((v2.getY() - v3.getY())*(v1.getX() - v3.getX()) + (v3.getX() - v2.getX())*(v1.getY() - v3.getY()));
      double beta = ((v3.getY() - v1.getY())*(vertex.getX() - v3.getX()) + (v1.getX() - v3.getX())*(vertex.getY() - v3.getY())) /
          ((v2.getY() - v3.getY())*(v1.getX() - v3.getX()) + (v3.getX() - v2.getX())*(v1.getY() - v3.getY()));
      double gamma = 1.0d - alpha - beta;

      return gamma > 0 && alpha > 0 && beta > 0;
    }

    public boolean containsCircumcircleCenter() {
      return contains(circumcircleCenter);
    }

    // Code inspired by: https://gist.github.com/mutoo/5617691
    private void computeCircumcircle() {
      double x1 = v1.getX();
      double y1 = v1.getY();
      double x2 = v2.getX();
      double y2 = v2.getY();
      double x3 = v3.getX();
      double y3 = v3.getY();

      double absdy1y2 = Math.abs(y1 - y2);
      double absdy2y3 = Math.abs(y2 - y3);

      double xc;
      double yc;
      double m1;
      double m2;
      double mx1;
      double my1;
      double mx2;
      double my2;

      if (absdy1y2 < EPSILON) {
        m2 = -((x3 - x2) / (y3 - y2));
        mx2 = (x2 + x3) / 2.0;
        my2 = (y2 + y3) / 2.0;
        xc  = (x2 + x1) / 2.0;
        yc  = m2 * (xc - mx2) + my2;
      } else if (absdy2y3 < EPSILON) {
        m1  = -((x2 - x1) / (y2 - y1));
        mx1 = (x1 + x2) / 2.0;
        my1 = (y1 + y2) / 2.0;
        xc  = (x3 + x2) / 2.0;
        yc  = m1 * (xc - mx1) + my1;
      } else {
        m1  = -((x2 - x1) / (y2 - y1));
        m2  = -((x3 - x2) / (y3 - y2));
        mx1 = (x1 + x2) / 2.0;
        mx2 = (x2 + x3) / 2.0;
        my1 = (y1 + y2) / 2.0;
        my2 = (y2 + y3) / 2.0;
        xc  = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
        yc  = (absdy1y2 > absdy2y3) ?
            m1 * (xc - mx1) + my1 :
            m2 * (xc - mx2) + my2;
      }

      double dx = x2 - xc;
      double dy = y2 - yc;
      this.circumcircleCenter = new Vertex(xc, yc);
      this.circumcircleRadiusSquared = dx * dx + dy * dy;
    }

  }

}
