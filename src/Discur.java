import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Discur {

  private List<Vertex> vertices;
  private List<Edge> delaunayEdges;
  private List<LinearCurve> curves;
  private Map<Vertex, LinearCurve> vertexCurveMap;
  private Map<Edge, Integer> mark;
  private Map<Vertex, Integer> degree;
  private Map<Vertex, Map<Vertex, Double>> connectivity;

  public Discur(List<Vertex> vertices) {
    this.vertices = vertices;
    this.curves = new ArrayList<>();
    this.vertexCurveMap = new HashMap<>();
    this.mark = new HashMap<>();
    this.degree = new HashMap<>();
    this.connectivity = new HashMap<>();

    initialization();
    determineConnectivity();
    updateConnectivity();
  }

  public List<Curve> getCurves() {
    List<Curve> clone = new ArrayList<>(curves);

//    // DEBUG
//    Curve delaunayCurve = new Curve();
//    for (Edge edge : delaunayEdges) {
//      delaunayCurve.addEdge(edge);
//    }
//    clone.add(delaunayCurve);

    return clone;
  }

  /**
   * Step 1: Delaunay triangulation and initialization.
   */
  private void initialization() {
    Triangulation triangulation = new Triangulation(new Vector<Vertex>(vertices));
    triangulation.doWork();
    triangulation.makeEdges();
    delaunayEdges = triangulation.getEdges();

    for (Edge edge : delaunayEdges) {
      mark.put(edge, 0);
    }

    for (Vertex vertex : vertices) {
      degree.put(vertex, 0);
    }
  }

  /**
   * Step 2: Determining the connectivity of the Delaunay edges.
   */
  private void determineConnectivity() {
    // Sort edges by length
    Collections.sort(delaunayEdges, new Comparator<Edge>() {
      @Override
      public int compare(Edge e1, Edge e2) {
        return Double.compare(e1.lengthSquared(), e2.lengthSquared());
      }
    });

    for (Edge edge : delaunayEdges) {
      if (mark.get(edge) != 0) {
        continue;
      }

      Vertex head = edge.getHead();
      Vertex tail = edge.getTail();

      degree.put(head, degree.get(head) + 1);
      degree.put(tail, degree.get(tail) + 1);

      // If both head and tail are free points
      if (!vertexCurveMap.containsKey(head) && !vertexCurveMap.containsKey(tail)) {
        // Connect vertices
        LinearCurve curve = new LinearCurve(edge);
        curves.add(curve);
        vertexCurveMap.put(head, curve);
        vertexCurveMap.put(tail, curve);

        // Remove edge from consideration
        mark.put(edge, -1);
      } else {
        double headConnectivity = computeConnectivity(head, tail);
        double tailConnectivity = computeConnectivity(tail, head);

        if (head.distance(tail) < Math.max(headConnectivity, tailConnectivity)) {
          // Connect vertices
          if (headConnectivity == 0) { // head = free, tail = endpoint
            LinearCurve tailCurve = vertexCurveMap.get(tail);
            tailCurve.connect(edge);
            vertexCurveMap.put(head, tailCurve);
          } else if (tailConnectivity == 0) { // head = endpoint, tail = free
            LinearCurve headCurve = vertexCurveMap.get(head);
            headCurve.connect(edge);
            vertexCurveMap.put(tail, headCurve);
          } else { // head = endpoint, tail = endpoint
            LinearCurve headCurve = vertexCurveMap.get(head);
            LinearCurve tailCurve = vertexCurveMap.get(tail);

            headCurve.connect(tailCurve);

            for (Edge edge1 : tailCurve.getEdges()) {
              vertexCurveMap.put(edge1.getHead(), headCurve);
              vertexCurveMap.put(edge1.getTail(), headCurve);
            }

            curves.remove(tailCurve);
          }

          // Remove edge from consideration
          mark.put(edge, -1);
        } else {
          mark.put(edge, 1);
        }
      }

      removeExtraEdges(head);
      removeExtraEdges(tail);
    }
  }

  private double computeConnectivity(Vertex p2, Vertex p1) {
    Curve curve = vertexCurveMap.get(p1);

    // TODO: Compute connectivity
    double value = 0;

    if (!connectivity.containsKey(p2)) {
      connectivity.put(p2, new HashMap<>());
    }
    connectivity.get(p2).put(p1, value);
    return value;
  }

  private void removeExtraEdges(Vertex vertex) {
    if (degree.get(vertex) != 2) {
      return;
    }

    for (Edge edge : delaunayEdges) {
      if (mark.get(edge) == 0 && (edge.getHead().equals(vertex) || edge.getTail().equals(vertex))) {
        mark.put(edge, -1);
      }
    }
  }

  /**
   * Step 3: Updating the connectivity of the Delaunay edges.
   */
  private void updateConnectivity() {

  }
}
