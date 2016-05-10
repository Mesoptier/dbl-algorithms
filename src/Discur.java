import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Discur {

  private Debug debug;

  private final List<Vertex> vertices;
  private List<Edge> delaunayEdges;
  private List<LinearCurve> curves = new ArrayList<>();

  public Discur(List<Vertex> vertices, Debug debug) {
    this.vertices = vertices;
    this.debug = debug;

    initialization();
    determineConnectivity();
  }

  /**
   * Step 1: Delaunay triangulation and initialization.
   */
  private void initialization() {
    Triangulation triangulation = new Triangulation(new ArrayList<Vertex>(vertices));
    triangulation.doWork();
    triangulation.makeEdges();
    delaunayEdges = triangulation.getEdges();

    for (Edge edge : delaunayEdges) {
      edge.setData(new DiscurEdgeData());
    }

    for (Vertex vertex : vertices) {
      vertex.setData(new DiscurVertexData());
    }

    if (debug != null) {
      DebugState state = new DebugState();
      state.setEdges(delaunayEdges);
      debug.addState(state);
    }
  }

  /**
   * Step 2: Determining the connectivity of the Delaunay edges.
   */
  private void determineConnectivity() {
    // Sort edges by size
    Collections.sort(delaunayEdges, new Comparator<Edge>() {
      @Override
      public int compare(Edge e1, Edge e2) {
        return Double.compare(e1.distanceSquared(), e2.distanceSquared());
      }
    });

    for (Edge edge : delaunayEdges) {
      DiscurEdgeData edgeData = ((DiscurEdgeData)edge.getData());

      if (edgeData.removed || edgeData.mark != 0) {
        continue;
      }

      Vertex head = edge.getHead();
      DiscurVertexData headData = ((DiscurVertexData)head.getData());
      Vertex tail = edge.getTail();
      DiscurVertexData tailData = ((DiscurVertexData)tail.getData());

      headData.degree += 1;
      tailData.degree += 1;

      // If both head and tail are free points
      if (headData.curveDegree == 0 && tailData.curveDegree == 0) {
        // Connect vertices
        headData.curveDegree += 1;
        tailData.curveDegree += 1;
        // TODO: finish connecting vertices

        // Remove edge
        edgeData.removed = true;
      } else {

      }
    }
  }

  public List<? extends Curve> getCurves() {
    return curves;
  }

  private class DiscurEdgeData extends EdgeData {

    private boolean removed = false;
    private int mark = 0;

  }

  private class DiscurVertexData extends VertexData {

    private int degree = 0;
    private int curveDegree = 0;

  }

}
