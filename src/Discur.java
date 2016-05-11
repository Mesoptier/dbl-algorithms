import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Discur {

  private Debug debug;
  private DebugState state;

  private final List<Vertex> vertices;
  private List<Edge> delaunayEdges;
  private List<LinearCurve> curves = new ArrayList<>();

  public Discur(List<Vertex> vertices, Debug debug) {
    this.vertices = vertices;
    this.debug = debug;

    initialization();
    determineConnectivity();
    updateConnectivity();
  }

  /**
   * Step 1: Delaunay triangulation and initialization.
   */
  private void initialization() {
    Triangulation triangulation = new Triangulation(new ArrayList<Vertex>(vertices));
    triangulation.doWork();
    triangulation.makeEdges();
    delaunayEdges = triangulation.getEdges();

    for (Vertex vertex : vertices) {
      vertex.setData(new DiscurVertexData());
    }

    for (Edge edge : delaunayEdges) {
      edge.setData(new DiscurEdgeData());
      ((DiscurVertexData)edge.getHead().getData()).incidentEdges.add(edge);
      ((DiscurVertexData)edge.getTail().getData()).incidentEdges.add(edge);
    }

    if (debug != null) {
      DebugState state = new DebugState();
      state.addEdges(delaunayEdges);
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
      if (debug != null) {
        state = new DebugState();
      }

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
        if (debug != null) {
          state.setMessage("connecting 2 free points");
        }

        // Connect vertices
        LinearCurve curve = new LinearCurve(edge);
        headData.curveDegree += 1;
        headData.curve = curve;
        tailData.curveDegree += 1;
        tailData.curve = curve;
        curves.add(curve);

        // Remove edge
        edgeData.removed = true;
        headData.incidentEdges.remove(edge);
        tailData.incidentEdges.remove(edge);
      } else {
        if (shouldConnect(head, tail)) {
          if (debug != null) {
            state.setMessage("extending existing curve");
          }

          // Connect vertices
          connectVertices(edge, head, headData, tail, tailData);

          // Remove edge
          edgeData.removed = true;
          headData.incidentEdges.remove(edge);
          tailData.incidentEdges.remove(edge);
        } else {
          if (debug != null) {
            state.setMessage("skipping edge");
          }

          edgeData.mark = 1;
        }
      }

      // Remove unused edges
      removeExtraEdges(head, headData);
      removeExtraEdges(tail, tailData);

      if (debug != null) {
        // Display remaining incidentEdges in gray
        for (Vertex vertex : vertices) {
          state.addEdges(((DiscurVertexData)vertex.getData()).incidentEdges, Color.LIGHT_GRAY);
        }

        // Current curves
        List<Edge> debugEdges = new ArrayList<>();
        for (Curve debugCurve : curves) {
          debugEdges.addAll(debugCurve.getEdges());
        }
        state.addEdges(debugEdges);

        // Current vertices
        state.addVertices(vertices);

        // Active edge
        state.addEdge(edge, Color.GREEN);

        debug.addState(state);
      }
    }
  }

  /**
   * Step 3: Updating the connectivity of the Delaunay edges.
   * TODO: Check this thing, because it is probably fucked.
   */
  private void updateConnectivity() {
    for (Edge edge : delaunayEdges) {
      if (debug != null) {
        state = new DebugState();
      }

      DiscurEdgeData edgeData = ((DiscurEdgeData)edge.getData());

      if (edgeData.removed || edgeData.mark != 1) {
        continue;
      }

      Vertex head = edge.getHead();
      DiscurVertexData headData = (DiscurVertexData)head.getData();
      Vertex tail = edge.getTail();
      DiscurVertexData tailData = (DiscurVertexData)tail.getData();

      if (shouldConnect(head, tail)) {
        // Connect vertices
        connectVertices(edge, head, headData, tail, tailData);

        // Remove edge
        edgeData.removed = true;
        headData.incidentEdges.remove(edge);
        tailData.incidentEdges.remove(edge);

        LinearCurve curve = headData.curve;
        boolean shouldBreak = false;

        while (!shouldBreak) {
          shouldBreak = true;

          Vertex curveHead = curve.getHead();
          DiscurVertexData curveHeadData = (DiscurVertexData)curveHead.getData();
          Vertex curveTail = curve.getTail();
          DiscurVertexData curveTailData = ((DiscurVertexData)curveTail.getData());

          List<Edge> incidentEdges = curveHeadData.incidentEdges;
          incidentEdges.addAll(curveTailData.incidentEdges);

          for (Edge incidentEdge : incidentEdges) {
            DiscurEdgeData incidentEdgeData = (DiscurEdgeData)incidentEdge.getData();
            Vertex incidentHead = incidentEdge.getHead();
            DiscurVertexData incidentHeadData = (DiscurVertexData)incidentHead.getData();
            Vertex incidentTail = incidentEdge.getTail();
            DiscurVertexData incidentTailData = (DiscurVertexData)incidentTail.getData();

            if (shouldConnect(incidentHead, incidentTail)) {
              // Connect vertices
              connectVertices(incidentEdge, incidentHead, incidentHeadData, incidentTail,
                  incidentTailData);

              // Remove edge
              incidentEdgeData.removed = true;
              incidentHeadData.incidentEdges.remove(incidentEdge);
              incidentTailData.incidentEdges.remove(incidentEdge);

              curve = incidentHeadData.curve;
              shouldBreak = false;
            }
          }
        }
      } else {
        edgeData.mark = 0;
      }
    }
  }

  private void connectVertices(Edge edge, Vertex head, DiscurVertexData headData, Vertex tail,
                               DiscurVertexData tailData) {
    // TODO: Verify that my logic here is correct
    if (headData.curveDegree == 0) {
      tailData.curve.connect(edge);
      headData.curve = tailData.curve;
    } else if (tailData.curveDegree == 0) {
      headData.curve.connect(edge);
      tailData.curve = headData.curve;
    } else {
      headData.curve.connect(edge);

      // We don't need to remove the 2nd curve when they are the same!
      if (!headData.curve.equals(tailData.curve)) {
        headData.curve.connect(tailData.curve);

        LinearCurve tailCurve = tailData.curve;

        for (Edge edge1 : tailCurve.getEdges()) {
          ((DiscurVertexData)edge1.getHead().getData()).curve = headData.curve;
          ((DiscurVertexData)edge1.getTail().getData()).curve = headData.curve;
        }

        curves.remove(tailCurve);
      }
    }

    headData.curveDegree += 1;
    tailData.curveDegree += 1;
  }

  private void removeExtraEdges(Vertex vertex, DiscurVertexData vertexData) {
    if (vertexData.curveDegree != 2) {
      return;
    }

    Iterator<Edge> it = vertexData.incidentEdges.iterator();

    while (it.hasNext()) {
      Edge edge = it.next();

      if (((DiscurEdgeData)edge.getData()).mark != 0) {
        continue;
      }

      ((DiscurEdgeData)edge.getData()).removed = true;

      // Remove edge from head incident list
      if (edge.getHead().equals(vertex)) {
        it.remove();
      } else {
        ((DiscurVertexData)edge.getHead().getData()).incidentEdges.remove(edge);
      }

      // Remove edge from tail incident list
      if (edge.getTail().equals(vertex)) {
        it.remove();
      } else {
        ((DiscurVertexData)edge.getTail().getData()).incidentEdges.remove(edge);
      }
    }
  }

  private boolean shouldConnect(Vertex p1, Vertex p2) {
    double distance = p1.distance(p2);
    return distance < computeConnectivityValue(p1, p2)
        || distance < computeConnectivityValue(p2, p1);
  }

  private double computeConnectivityValue(Vertex p1, Vertex p2) {
    DiscurVertexData data1 = ((DiscurVertexData)p1.getData());
    DiscurVertexData data2 = ((DiscurVertexData)p2.getData());

    double value = 0;

    if (data2.curveDegree == 1) {
      LinearCurve curve = data2.curve;

      // TODO: Verify that these values are correct
      double hd = curve.distanceMean();
      double sd = curve.distanceStdDev();

      // Distance of the new point to the nearest endpoint of the curve
      double newDist = p1.distance(p2);

      // Distance of the nearest segment in the curve
      double endDist;

      if (p2.equals(curve.getHead())) {
        endDist = curve.getHeadEdge().distance();
      } else if (p2.equals(curve.getTail())) {
        endDist = curve.getTailEdge().distance();
      } else {
        throw new Error("wat");
      }

      double h = (newDist + endDist) / 2;
      double s = (newDist - endDist) / Math.sqrt(2);

      if (sd == 0) {
        value = hd * (h / s);
      } else {
        value = hd * (h / s) * Math.pow(1 + (hd / sd), sd / hd);
      }
    }

    return value;
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
    private LinearCurve curve;
    private List<Edge> incidentEdges = new ArrayList<>();

  }

}
