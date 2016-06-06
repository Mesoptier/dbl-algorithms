import javafx.scene.shape.Circle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Discur {

  static final double FREE_POINT_CONSTANT = 1.849;
  static final double POINTCURVECONSTANT = 2;
  static final double ANGLECONSTANT = 0.8;

  private Debug debug;
  private DebugState state;

  //private List<Circle> balls;

  private final List<Vertex> vertices;
  private List<Edge> delaunayEdges;
  private List<LinearCurve> curves = new ArrayList<>();

  //private List<Edge> freepointlist;

  //private List<Vertex> pointlist;

  //private List<Pacman> pacmen;

  public Discur(List<Vertex> vertices, Debug debug) {
    this.vertices = vertices;
    this.debug = debug;

    initialization();
    determineConnectivity();
    updateConnectivity();
    postProcessCurves();
  }

  /**
   * Step 1: Delaunay triangulation and initialization.
   */
  private void initialization() {
    Triangulation triangulation = new Triangulation(new ArrayList<Vertex>(vertices));
    triangulation.triangulate();
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
        state.addVertices(vertices);
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
        if (testFreePoints(edge)) {
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
          edgeData.mark = 1;
        }
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
        state.addVertices(vertices);
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

          Iterator<Edge>  it = incidentEdges.iterator();

          while (it.hasNext()) {
            if (debug != null){
              state = new DebugState();
              state.addVertices(vertices);
            }


            Edge incidentEdge = it.next();

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

              if (incidentEdges.equals(incidentHeadData.incidentEdges)) {
                it.remove();
              } else {
                incidentHeadData.incidentEdges.remove(incidentEdge);
              }
              if (incidentEdges.equals(incidentTailData.incidentEdges)) {
                it.remove();
              } else {
                incidentTailData.incidentEdges.remove(incidentEdge);
              }
//              incidentHeadData.incidentEdges.remove(incidentEdge);
//              incidentTailData.incidentEdges.remove(incidentEdge);

              curve = incidentHeadData.curve;
              shouldBreak = false;
            }
            if (debug != null) {
              // Display remaining incidentEdges in gray
              for (Vertex vertex : vertices) {
                state.addEdges(((DiscurVertexData) vertex.getData()).incidentEdges, Color.LIGHT_GRAY);
              }

              // Current curves
              List<Edge> debugEdges = new ArrayList<>();
              for (Curve debugCurve : curves) {
                debugEdges.addAll(debugCurve.getEdges());
              }
              state.addEdges(debugEdges);

              // Active edge
              state.addEdge(incidentEdge, Color.GREEN);

              state.setMessage("step 3 edge " + incidentEdge.toString());

              debug.addState(state);
            }
          }
        }
      } else {
        edgeData.mark = 0;
        if (debug != null) {
          // Display remaining incidentEdges in gray
          for (Vertex vertex : vertices) {
            state.addEdges(((DiscurVertexData) vertex.getData()).incidentEdges, Color.LIGHT_GRAY);
          }

          // Current curves
          List<Edge> debugEdges = new ArrayList<>();
          for (Curve debugCurve : curves) {
            debugEdges.addAll(debugCurve.getEdges());
          }
          state.addEdges(debugEdges);

          // Active edge
          state.addEdge(edge, Color.GREEN);

          state.setMessage("step 3 edge " + edge.toString());

          debug.addState(state);
        }
      }
      /*
      if (debug != null) {
        // Display remaining incidentEdges in gray
        for (Vertex vertex : vertices) {
          state.addEdges(((DiscurVertexData) vertex.getData()).incidentEdges, Color.LIGHT_GRAY);
        }

        // Current curves
        List<Edge> debugEdges = new ArrayList<>();
        for (Curve debugCurve : curves) {
          debugEdges.addAll(debugCurve.getEdges());
        }
        state.addEdges(debugEdges);

        // Active edge
        state.addEdge(edge, Color.GREEN);

        state.setMessage("step 3 edge " + edge.toString());

        debug.addState(state);
      }
      */
    }
  }

  private void postProcessCurves() {
    if (debug != null) {
      state = new DebugState();
      state.setMessage("post processing curves");

      // Current curves
      List<Edge> debugEdges = new ArrayList<>();
      for (Curve debugCurve : curves) {
        debugEdges.addAll(debugCurve.getEdges());
      }
      state.addEdges(debugEdges);

      // Current vertices
      state.addVertices(vertices);

      // Free vertices
      for (Vertex vertex : vertices) {
        DiscurVertexData vertexData = (DiscurVertexData) vertex.getData();

        if (vertexData.curveDegree == 0) {
          if (debug != null) {
            state.addVertex(vertex, Color.RED);
          }
        }
      }

      debug.addState(state);
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
    DiscurVertexData data1 = ((DiscurVertexData)p1.getData());
    DiscurVertexData data2 = ((DiscurVertexData)p2.getData());
    if (data1.curveDegree == 2 || data2.curveDegree == 2) {
      return false;
    }
    if (data1.curveDegree == 1 && data2.curveDegree == 1){
      return (curveCurve(p1, p2));
    } else if (data1.curveDegree == 1){
      return pointCurve(p1, p2);
    } else if (data2.curveDegree == 1){
      return pointCurve(p2, p1);
    } else {
      return false;
    }
  }

  private boolean testFreePoints(Edge edge) {
    List<Edge> incidentEdges = ((DiscurVertexData)edge.getHead().getData()).incidentEdges;
    double distanceSquared1 = Double.MAX_VALUE;
    double distanceSquared2 = Double.MAX_VALUE;

    for (Edge incidentEdge : incidentEdges) {
      double distanceSquared = incidentEdge.distanceSquared();

      if (distanceSquared < distanceSquared1) {
        distanceSquared2 = distanceSquared1;
        distanceSquared1 = distanceSquared;
      } else if (distanceSquared < distanceSquared2) {
        distanceSquared2 = distanceSquared;
      }
    }

    double radius = 0.5 * FREE_POINT_CONSTANT * (Math.sqrt(distanceSquared1) + Math.sqrt(distanceSquared2));

    List<Vertex> vertices = getVerticesWithinRadius(edge.getHead(), radius);
    vertices.remove(edge.getHead());
    vertices.remove(edge.getTail());

    if (debug != null) {
      state.addVertices(vertices, Color.BLUE);
      state.addCircle(new Circle(edge.getHead().getX(), edge.getHead().getY(), radius * 2));
    }

    double angle = 0;

    for (Vertex vi : vertices) {
//      // DEBUG:
//      freepointlist.add(e);

      angle = Math.max(angle, Vertex.calcAngle(vi, edge.getHead(), edge.getTail()));
    }

    for (Vertex vt : vertices) {
      for (Vertex vj : vertices) {
        if (Vertex.calcAngle(vt, edge.getHead(), vj) > angle) {
          return false;
        }
      }
    }
    return true;
  }

  //TODO vertices met degree van 2
  private List<Vertex> getVerticesWithinRadius(Vertex v1, double radius) {
    return getVerticesWithinRadiusSquared(v1, radius * radius);
  }

  private List<Vertex> getVerticesWithinRadiusSquared(Vertex v1, double radiusSquared) {
    List<Vertex> vertices = new ArrayList<>();
    vertices.add(v1);

    for (int i = 0; i < vertices.size(); i++) {
      List<Edge> incidentEdges = ((DiscurVertexData)vertices.get(i).getData()).incidentEdges;

      for (Edge incidentEdge : incidentEdges) {
        if (!vertices.contains(incidentEdge.getHead())) {
          if (v1.distanceSquared(incidentEdge.getHead()) < radiusSquared) {
            vertices.add(incidentEdge.getHead());
          }
        } else if (!vertices.contains(incidentEdge.getTail())) {
          if (v1.distanceSquared(incidentEdge.getTail()) < radiusSquared) {
            vertices.add(incidentEdge.getTail());
          }
        }
      }
    }

    return vertices;
  }

    private boolean pointCurve(Vertex curvepoint, Vertex newpoint){
    LinearCurve curve = ((DiscurVertexData)curvepoint.getData()).curve;

    Vertex curvepoint2 = (curve.getHead().equals(curvepoint))
        ? curve.getHeadEdge().getTail()
        : curve.getTailEdge().getHead();

    double dm = curve.distanceMean() * POINTCURVECONSTANT;
    double dist = curvepoint.distance(newpoint);

    Vertex horizontal = new Vertex(curvepoint.getX()+1, curvepoint.getY());
    double rotation = Vertex.calcAngle(horizontal, curvepoint, curvepoint2);
    if (curvepoint2.getY() < curvepoint.getY()) {
      rotation = 360 - rotation;
    }
    if (debug != null) {
      state.addPacman(new Pacman(curvepoint, dm, rotation));
    }

    if (dist > dm || Vertex.calcAngle(curvepoint2, curvepoint, newpoint) < 45){
      return false;
    }

    return checkPointCurve(curvepoint, newpoint, curvepoint2, dm);
  }

  private boolean curveCurve(Vertex v1, Vertex v2){
    LinearCurve curve1 = ((DiscurVertexData)v1.getData()).curve;
    LinearCurve curve2 = ((DiscurVertexData)v2.getData()).curve;

    Vertex curve1point2 = (curve1.getHead().equals(v1))
        ? curve1.getHeadEdge().getTail()
        : curve1.getTailEdge().getHead();

    Vertex curve2point2 = (curve2.getHead().equals(v2))
        ? curve2.getHeadEdge().getTail()
        : curve2.getTailEdge().getHead();

    double dm1 = curve1.distanceMean() * POINTCURVECONSTANT;
    double dm2 = curve2.distanceMean() * POINTCURVECONSTANT;
    double dist = v1.distance(v2);

    Vertex horizontal = new Vertex(v1.getX()+1, v1.getY());
    double rotation1 = Vertex.calcAngle(horizontal, v1, curve1point2);
    double rotation2 = Vertex.calcAngle(horizontal, v2, curve2point2);

    if (curve1point2.getY() < v1.getY()) {
      rotation1 = 360 - rotation1;
    }
    if (curve2point2.getY() < v2.getY()) {
      rotation2 = 360 - rotation2;
    }

    if (debug != null) {
      state.addPacman(new Pacman(v1, dm1, rotation1));
      state.addPacman(new Pacman(v2, dm2, rotation2));
    }

    if ((dist > dm1 || Vertex.calcAngle(curve1point2, v1, v2) < 45) && (dist > dm2 || Vertex.calcAngle(curve2point2, v2, v1) < 45)){
      return false;
    }

    return checkPointCurve(v1, v2, curve1point2, dm1) && checkPointCurve(v2, v1, curve2point2, dm2);
  }

  private boolean checkPointCurve(Vertex curvepoint, Vertex newpoint, Vertex curvepoint2, double dm){
    double value = 0;
    value = computeConnectivityValue(newpoint, curvepoint);

    List<Vertex> vertices = getVerticesWithinRadius(curvepoint, dm);
    Iterator<Vertex> it = vertices.iterator();

    while (it.hasNext()) {
      Vertex v = it.next();
      DiscurVertexData data = (DiscurVertexData) v.getData();
      if (Vertex.calcAngle(curvepoint2, curvepoint, v) < 45 || data.curveDegree == 2) {
        it.remove();
      }
    }

    if (debug != null) {
      state.addVertices(vertices, Color.BLUE);
    }

    for (Vertex v : vertices){
      if (computeConnectivityValue(v, curvepoint) > value){
        return false;
      }
    }
    return true;
  }

  private double computeConnectivityValue(Vertex p1, Vertex p2) {
    DiscurVertexData data1 = ((DiscurVertexData)p1.getData());
    DiscurVertexData data2 = ((DiscurVertexData)p2.getData());

    double value = 0;

    if (data2.curveDegree == 1) {
      LinearCurve curve = data2.curve;

      Vertex curvepoint2 = (curve.getHead().equals(p2))
          ? curve.getHeadEdge().getTail()
          : curve.getTailEdge().getHead();

      // TODO: Verify that these values are correct
      double hd = curve.distanceMean();
      double sd = curve.distanceStdDev();
      double ad = curve.angleMean();

      // Distance of the new point to the nearest endpoint of the curve
      double newDist = p1.distance(p2);

      // Distance of the nearest segment in the curve
      double endDist;

      // Candidate angle
      double angle = Vertex.calcAngle(curvepoint2, p2, p1);

      if (p2.equals(curve.getHead())) {
        endDist = curve.getHeadEdge().distance();
      } else if (p2.equals(curve.getTail())) {
        endDist = curve.getTailEdge().distance();
      } else {
        throw new Error("wat");
      }

      double h = (newDist + endDist) / 2;
      double s = (newDist - endDist) / Math.sqrt(2);
      double c = ANGLECONSTANT;

      if (ad == 0) {
        value = Math.pow((c * Math.pow(((angle / 180) -1), 2) + ((1 - c) / 4) * Math.pow((newDist / (hd + sd)), 2) + 1), -1);
      } else {
        value = Math.pow((c * Math.pow(((angle / ad) -1), 2) + ((1 - c) / 4) * Math.pow((newDist / (hd + sd)), 2) + 1), -1);
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
