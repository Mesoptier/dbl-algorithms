import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ReconstructNetwork extends Reconstruct {

  private Curve curve;
  private List<Vertex> addedVertices = new ArrayList<>();
  private List<Edge> addedEdges = new ArrayList<>();
  private Double DISTANCE;
  private Double LINEDISTANCE;
  private Double ANGLE;
  private LinearCurve currentLine = null;
  private List<LinearCurve> lines = new ArrayList<>();

  @Override
  public ProblemOutput start() {
    DISTANCE = 1 / (vertices.size() * 0.01 + 10);
    LINEDISTANCE = 1.2*DISTANCE;
    ANGLE = 160.0;

    List<Curve> curves = new ArrayList<>();
    findClosestPoints();
    findStraightLines();
    //findRoundabouts();
    connectSingleVertices();
    connectLines();
    insertIntersections();
    curves.add(curve);

    return new ProblemOutput(vertices, curves);
  }

  // Finds close vertices and closest vertex for each vertex in vertices
  private void findClosestPoints() {
    for (Vertex vertex1 : vertices) {
      Vertex closest = null;
      for (Vertex vertex2 : vertices) {
        if (!vertex1.equals(vertex2)) {
          if (closest == null || (vertex1.distance(vertex2) < vertex1.distance(closest))) {
            closest = vertex2;
          }
          if (vertex1.distance(vertex2) < DISTANCE) {
            vertex1.addClose(vertex2);
          }
        }
      }
      vertex1.setClosest(closest);
    }
  }

  // Tries to find straight line between 3 points, if it finds such a line it will try to append that line as far as possible.
  private void findStraightLines() {

    curve = new Curve();
    currentLine = null;

    for (Vertex vertex1 : vertices) {
      find(vertex1);
      find(vertex1);
    }
  }

  // Tries to find two vertices such that the provided vertex forms a straight line with them,
  // then recursively tries to append that line as far as possible.
  private void find(Vertex v) {
    if (v.getDegree() < 2) {
      Vertex bestVertex2 = null;
      Vertex bestVertex3 = null;
      Double bestAngle = null;
      List<Vertex> close1 = v.getClose();
      for (Vertex vertex2 : close1) {
        if (vertex2.getDegree() < 1) {
          List<Vertex> close2 = vertex2.getClose();
          for (Vertex vertex3 : close2) {
            if (!v.equals(vertex3) && vertex3.getDegree() < 2 && v.distance(vertex3) > v.distance(vertex2)) {

              // Calculate angle between vertices 1,2,3
              Edge e1 = new Edge(v, vertex2);
              Edge e2 = new Edge(vertex2, vertex3);
              Double angle = calcAngle(e1, e2);

              // If angle between vertices 1,2,3 is good consider connecting them.
              // If vertex1 is on a line also check if 2,3 are in front of the line.
              if (angle > ANGLE) {
                if (currentLine == null) {
                  if (bestVertex2 == null || (v.distance(bestVertex2) + bestVertex2.distance(bestVertex3) + 0.001 * (180 - bestAngle)) > (v.distance(vertex2) + vertex2.distance(vertex3) + 0.001 * (180 - angle))) {
                    bestVertex2 = vertex2;
                    bestVertex3 = vertex3;
                    bestAngle = angle;
                  }
                } else {
                  Double lineAngle = calcAngle(new Edge(currentLine.getHead(), currentLine.getTail()), new Edge(currentLine.getTail(), vertex3));
                  if (lineAngle > ANGLE) {
                    if (bestVertex2 == null || (v.distance(bestVertex2) + bestVertex2.distance(bestVertex3) + 0.001 * (180 - bestAngle)) > (v.distance(vertex2) + vertex2.distance(vertex3) + 0.001 * (180 - angle))) {
                      bestVertex2 = vertex2;
                      bestVertex3 = vertex3;
                      bestAngle = angle;
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Connect best 3 vertices if found
      if (bestVertex2 != null) {
        List<Vertex> connect = new ArrayList<>();
        connect.add(v);
        connect.add(bestVertex2);
        connect.add(bestVertex3);
        connectVertices(connect);
        find(currentLine.getTail());
      // Else stop appending
      } else {
        if (currentLine != null) {
          lines.add(currentLine);
        }
        currentLine = null;
      }
    }
  }

  // Connects vertices with degree 0 to a nearby vertex
  private void connectSingleVertices() {
    for (Vertex vertex : vertices) {
      currentLine = null;
      if (vertex.getDegree() < 1) {

        // First consider connecting to close vertices with degree < 2
        List<Vertex> close = vertex.getClose();
        Vertex bestVertex = null;
        for (Vertex closeVertex : close) {
          if (closeVertex.getDegree() < 2) {
            if (bestVertex == null || vertex.distance(closeVertex) < vertex.distance(bestVertex)) {
              bestVertex = closeVertex;
            }
          }
        }

        if (bestVertex != null) {
          List<Vertex> connect = new ArrayList<>();
          connect.add(vertex);
          connect.add(bestVertex);
          connectVertices(connect);
          lines.add(currentLine);
        } else {
          List<Vertex> connect = new ArrayList<>();
          connect.add(vertex);
          connect.add(vertex.getClosest());
          connectVertices(connect);
          lines.add(currentLine);
        }
      }
    }
  }

  // Connects straight lines
  private void connectLines() {
    for (LinearCurve line : lines) {

      Vertex best1 = null;
      Vertex best2 = null;

      for (LinearCurve line2 : lines) {

        Vertex head1 = line.getHead();
        Vertex head2 = line2.getHead();
        Vertex tail1 = line.getTail();
        Vertex tail2 = line2.getTail();

        if (!head1.equals(head2) && !tail1.equals(tail2)) {

          currentLine = null;

          Edge hh = new Edge(tail1, head2);
          Edge ht = new Edge(tail1, tail2);
          Edge th = new Edge(head1, head2);
          Edge tt = new Edge(head1, tail2);

          Double lineAngleHeadHead = calcAngle(new Edge(head1, tail1), hh);
          Double lineAngleHeadTail = calcAngle(new Edge(head1, tail1), ht);
          Double lineAngleTailHead = calcAngle(new Edge(tail1, head1), th);
          Double lineAngleTailTail = calcAngle(new Edge(tail1, head1), tt);

          if (lineAngleHeadHead > ANGLE && tail1.distance(head2) < LINEDISTANCE && !addedEdges.contains(new Edge(head2, tail1))) {
            if (best1 == null || (best1.distance(best2) > tail1.distance(head2))) {
              best1 = tail1;
              best2 = head2;
            }
          } else if (lineAngleHeadTail > ANGLE && tail1.distance(tail2) < LINEDISTANCE && !addedEdges.contains(new Edge(tail2, tail1))) {
            if (best1 == null || (best1.distance(best2) > tail1.distance(tail2))) {
              best1 = tail1;
              best2 = tail2;
            }
          } else if (lineAngleTailHead > ANGLE && head1.distance(head2) < LINEDISTANCE && !addedEdges.contains(new Edge(head2, head1))) {
            if (best1 == null || (best1.distance(best2) > head1.distance(head2))) {
              best1 = head1;
              best2 = head2;
            }
          } else if (lineAngleTailTail > ANGLE && head1.distance(tail2) < LINEDISTANCE && !addedEdges.contains(new Edge(tail2, head1))) {
            if (best1 == null || (best1.distance(best2) > head1.distance(tail2))) {
              best1 = head1;
              best2 = tail2;
            }
          }
        }
      }
      if (best1 != null) {
        List<Vertex> connect = new ArrayList<>();
        connect.add(best1);
        connect.add(best2);
        connectVertices(connect);
        addedEdges.add(new Edge(best1, best2));
      }
    }
  }

  // Insert vertices at intersections
  private void insertIntersections() {

    List<Edge> edges = new ArrayList<>();
    edges.addAll(curve.getEdges());

    // Check for intersections and insert vertices
    for (Edge edge1 : edges) {
      for (Edge edge2 : edges) {
        Vertex tail1 = edge1.getTail();
        Vertex tail2 = edge2.getTail();
        Vertex head1 = edge1.getHead();
        Vertex head2 = edge2.getHead();
        if (!edge1.equals(edge2) && !tail1.equals(tail2) && !tail1.equals(head2) && !head1.equals(tail2) && !head1.equals(head2)) {
          Vertex vertex = edge1.intersects(edge2);
          if (vertex != null) {
            addVertex(vertex);
          }
        }
      }
    }

  }

  // Calculates angle between three vertices
  private Double calcAngle(Edge e1, Edge e2) {

    Vertex vertex1, vertex2, vertex3;

    if (e1.getHead().equals(e2.getHead())){
      vertex2 = e1.getHead();
      vertex1 = e1.getTail();
      vertex3 = e2.getTail();
    } else if(e1.getTail().equals(e2.getTail())){
      vertex2 = e1.getTail();
      vertex1 = e1.getHead();
      vertex3 = e2.getHead();
    } else if(e1.getHead().equals(e2.getTail())){
      vertex2 = e1.getHead();
      vertex1 = e1.getTail();
      vertex3 = e2.getHead();
    } else {
      vertex2 = e1.getTail();
      vertex1 = e1.getHead();
      vertex3 = e2.getTail();
    }
    Double x = (vertex2.getX() - vertex1.getX()) * (vertex2.getX() - vertex3.getX());
    Double y = (vertex2.getY() - vertex1.getY()) * (vertex2.getY() - vertex3.getY());

    double dotProduct = x + y;

    Double value = dotProduct / (e1.distance() * e2.distance());

    //Sometimes value can get lower than -1 (maybe because of rounding ?) which will result in Math.acos throwing an error.
    //Have not encountered a case where value gets greater than 1 but included it in case it happens
    if (value < -1.0) {
      value = -1.0;
    }
    if (value > 1.0) {
      value = 1.0;
    }
    Double angle = Math.acos(value) * 180 / Math.PI;

    return angle;
  }

  // Connects a list of vertices
  private void connectVertices(List<Vertex> connect) {

    DebugState state = new DebugState();

    state.addVertices(vertices);

    String message = "Connecting ";

    // Current curves
    List<Edge> debugEdges = new ArrayList<>();
    debugEdges.addAll(curve.getEdges());

    state.addEdges(debugEdges);

    for (int i = 0; i < connect.size() - 1; i++) {

      Edge edge = new Edge(connect.get(i), connect.get(i+1));

      if (currentLine == null) {
        currentLine = new LinearCurve(edge);
      } else {
        currentLine.connect(edge);
      }

      state.addEdge(edge, Color.green);

      state.addVertex(connect.get(i), Color.RED);
      state.addVertex(connect.get(i+1), Color.RED);

      message += connect.get(i).getId() + " " + connect.get(i+1).getId() + " ";

      curve.addEdge(edge);

      connect.get(i).incDegree();
      connect.get(i+1).incDegree();
    }

    state.setMessage(message);

    if (debug != null) {
      debug.addState(state);
    }
  }

  // Adds vertex to output
  private void addVertex(Vertex vertex) {
    int id = vertices.size() + 1;
    Double vertexX = vertex.getX();
    Double vertexY = vertex.getY();
    Vertex vertexCopy = new Vertex(id, vertexX, vertexY);

    if (!addedVertices.contains(vertexCopy)) {
      DebugState state = new DebugState();

      state.addVertices(vertices);

      state.addVertex(vertexCopy, Color.GREEN);
      vertices.add(vertexCopy);
      addedVertices.add(vertexCopy);

      // Current curves
      List<Edge> debugEdges = new ArrayList<>();
      debugEdges.addAll(curve.getEdges());

      state.addEdges(debugEdges);

      state.setMessage("Inserting vertex at X: " + vertexCopy.getX() + " Y: " + vertexCopy.getY());

      if (debug != null) {
        debug.addState(state);
      }
    }
  }

  /*
  private void disconnectVertices(Vertex v1, Vertex v2) {
    DebugState state = new DebugState();

    state.addVertices(vertices);

    String message = "Disconnecting " + v1.getId() + " " + v2.getId();

    Edge edge = new Edge(v1, v2);
    curve.removeEdge(edge);

    // Current curves
    List<Edge> debugEdges = new ArrayList<>();
    debugEdges.addAll(curve.getEdges());

    state.addEdges(debugEdges);

    state.addEdge(edge, Color.RED);

    state.addVertex(v1, Color.RED);
    state.addVertex(v2, Color.RED);

    state.setMessage(message);

    if (debug != null) {
      debug.addState(state);
    }
  }*/


  // might use some of this later
  /*
  private void connectLines() {
    List<Vertex> currentVertices = new ArrayList<>();
    currentVertices.addAll(vertices);
    for (Vertex vertex : currentVertices) {
      if (vertex.getDegree() < 2) {

        // First consider connecting to close vertices with degree < 2
        List<Vertex> close = vertex.getClose();
        Vertex bestVertex = null;
        for (Vertex closeVertex : close) {
          if (closeVertex.getDegree() < 2) {
            if (bestVertex == null || vertex.distance(closeVertex) < vertex.distance(bestVertex)) {
              bestVertex = closeVertex;
            }
          }
        }

        //
        if (bestVertex == null) {
          Vertex closest = vertex.getClosest();
          for (Vertex closeVertex : close) {
            if (bestVertex != null) {break;}
            List<Vertex> close2 = closeVertex.getClose();
            for (Vertex close2Vertex : close2) {

              if (!vertex.equals(close2Vertex) && vertex.distance(close2Vertex) > vertex.distance(closeVertex)) {
                Double x;
                if (vertex.getX() > closeVertex.getX()) {
                  if (closeVertex.getX() < close2Vertex.getX()) {
                    x = closeVertex.getX() - close2Vertex.getX();
                    x = x / 2;
                    x = close2Vertex.getX() + x;
                  } else {
                    x = close2Vertex.getX() - closeVertex.getX();
                    x = x / 2;
                    x = closeVertex.getX() + x;
                  }
                } else {
                  if (closeVertex.getX() > close2Vertex.getX()) {
                    x = closeVertex.getX() - close2Vertex.getX();
                    x = x / 2;
                    x = close2Vertex.getX() + x;
                  } else {
                    x = close2Vertex.getX() - closeVertex.getX();
                    x = x / 2;
                    x = closeVertex.getX() + x;
                  }
                }
                Double y;
                if (vertex.getY() > closeVertex.getY()) {
                  if (closeVertex.getY() < close2Vertex.getY()) {
                    y = closeVertex.getY() - close2Vertex.getY();
                    y = y / 2;
                    y = close2Vertex.getY() + y;
                  } else {
                    y = close2Vertex.getY() - closeVertex.getY();
                    y = y / 2;
                    y = closeVertex.getY() + y;
                  }
                } else {
                  if (closeVertex.getY() > close2Vertex.getY()) {
                    y = closeVertex.getY() - close2Vertex.getY();
                    y = y / 2;
                    y = close2Vertex.getY() + y;
                  } else {
                    y = close2Vertex.getY() - closeVertex.getY();
                    y = y / 2;
                    y = closeVertex.getY() + y;
                  }
                }
                Vertex va = new Vertex(x, y);



                Edge e1 = new Edge(vertex, va);
                Edge e2 = new Edge(va, closeVertex);
                Double angle = calcAngle(e1, e2);

                if (angle > 80 && angle < 100) {
                  System.out.println(vertex.toString() + " " + closeVertex.toString() + " " + close2Vertex.toString());
                  addVertex(va);
                  va.setClosest(closeVertex);
                  bestVertex = vertices.get(vertices.size() - 1);
                  disconnectVertices(closeVertex, close2Vertex);
                  List<Vertex> connect = new ArrayList<>();
                  connect.add(closeVertex);
                  connect.add(va);
                  connect.add(close2Vertex);
                  connectVertices(connect);
                  break;
                }
              }
            }
          }
        }

        if (bestVertex != null) {
          List<Vertex> connect = new ArrayList<>();
          connect.add(vertex);
          connect.add(bestVertex);
          connectVertices(connect);
        }
      }
    }
  }*/
}


