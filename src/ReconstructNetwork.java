import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ReconstructNetwork extends Reconstruct {

  /* The curve containing all edges */
  private Curve outputCurve = new Curve();

  /* List of inserted vertices */
  private List<Vertex> addedVertices = new ArrayList<>();

  /* List of added edges by connectLines */
  private List<Edge> addedEdges = new ArrayList<>();

  /* Maximum distance between vertices */
  private Double DISTANCE;

  /* Maximum distance between lines */
  private Double LINEDISTANCE;

  /* Weight of angle for calculating best vertex */
  private Double ANGLEWEIGHT;

  /* Maximum angle between three vertices */
  private Double ANGLE;

  /* Maximum angle between two lines */
  private Double LINEANGLE;

  /* Maximum angle between line and vertex when appending/prepending */
  private Double APPENDANGLE;

  /* State of program for debugging */
  private String programstate = "";

  /* List of straight lines found by findStraightlines */
  private List<LinearCurve> lines = new ArrayList<>();

  /* Average number of close vertices each vertex has */
  private Double avgClose = 0.0;

  private DebugState state;

  @Override
  public ProblemOutput start() {

    List<Curve> curves = new ArrayList<>();
    initialize();
    findRoundabouts();
    findStraightLines();
    connectSingleVertices();
    connectLines();
    insertIntersections();
    curves.add(outputCurve);

    return new ProblemOutput(vertices, curves);
  }

  // Initializes necessary things
  private void initialize() {

    if (debug != null) {
      programstate = "Initializing";
    }

    // Initialize angle and distance
    DISTANCE = 1 / (vertices.size() * 0.01 + 10);
    LINEDISTANCE = 1.2*DISTANCE;
    ANGLE = 165.0;
    APPENDANGLE = 0.8 * ANGLE;
    LINEANGLE = 0.5 * ANGLE;
    ANGLEWEIGHT = 0.005;

    // Calculate close vertices and closest vertex for each vertex
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
      if (vertex1.getClose() != null) {
        avgClose += vertex1.getClose().size();
      }
    }

    // Compute average number of close vertices each vertex has
    avgClose = avgClose / vertices.size();
  }

  // Tries to find roundabouts in the input
  // TODO: Finish & rename variables
  private void findRoundabouts() {
    if (debug != null) {
      programstate = "Finding roundabouts";
    }

    List<Vertex> vc = new ArrayList<>();

    for (Vertex v1 : vertices) {
      List<Vertex> close1 = v1.getClose();
      if (close1.size() > avgClose * 2) {
        for (Vertex v2 : close1) {
          for (Vertex v3 : close1) {
            if (!v1.equals(v2) && !v2.equals(v3) && !v1.equals(v3)) {

              final double TOL = 0.0000001;

              final double offset = Math.pow(v2.getX(), 2) + Math.pow(v2.getY(), 2);
              final double bc = (Math.pow(v1.getX(), 2) + Math.pow(v1.getY(), 2) - offset) / 2.0;
              final double cd = (offset - Math.pow(v3.getX(), 2) - Math.pow(v3.getY(), 2)) / 2.0;
              final double det = (v1.getX() - v2.getX()) * (v2.getY() - v3.getY()) - (v2.getX() - v3.getX()) * (v1.getY() - v2.getY());

              if (Math.abs(det) < TOL) {
                //throw new IllegalArgumentException("...");
              }

              final double idet = 1 / det;

              final double centerx = (bc * (v2.getY() - v3.getY()) - cd * (v1.getY() - v2.getY())) * idet;
              final double centery = (cd * (v1.getX() - v2.getX()) - bc * (v2.getX() - v3.getX())) * idet;
              final double radius = Math.sqrt(Math.pow(v2.getX() - centerx, 2) + Math.pow(v2.getY() - centery, 2));

              if (radius < DISTANCE) {
                Vertex v = new Vertex(centerx, centery);
                v.setRadius(radius);
                vc.add(v);
              }
            }
          }
        }
      }
    }

    for (Vertex v : vc) {
      List<Vertex> oncircle = new ArrayList<>();

      for (Vertex vertex : vertices) {
        if (vertex.distance(v) > v.getRadius() - 0.01 && vertex.distance(v) < v.getRadius() + 0.01 && v.getDegree() == 0) {
          oncircle.add(vertex);
        }
      }
      if (oncircle.size() > 7) {
        state = new DebugState();
        state.addVertices(vertices);
        state.addVertex(v, Color.GREEN);
        debug.addState(state);
        connectVertices(oncircle);
        disconnectVertices(oncircle);
      }
    }
  }

  // Tries to find straight line between 3 points, if it finds such a line it will try to append that line as far as possible.
  private void findStraightLines() {
    if (debug != null) {
      programstate = "Finding straight lines";
    }
    for (Vertex vertex1 : vertices) {
      if (vertex1.getDegree() == 0) {
        LinearCurve current = null;

        //find triple in straight line then recursively append and prepend by one
        current = find(vertex1, current, false);
        current = find(vertex1, current, true);

        if (current != null) {
          lines.add(current);
        }
      }
    }
  }

  // Tries to find two vertices such that the provided vertex forms a straight line with them
  private LinearCurve find(Vertex v, LinearCurve currentLine, Boolean prepend) {

    LinearCurve current = currentLine;

    if (v.getDegree() < 3) {
      Vertex bestVertex2 = null;
      Vertex bestVertex3 = null;
      Double bestAngle = null;
      List<Vertex> close1 = v.getClose();
      for (Vertex vertex2 : close1) {
        if (current == null) {
          if (vertex2.getDegree() == 0) {
            List<Vertex> close2 = vertex2.getClose();
            for (Vertex vertex3 : close2) {
              if (!v.equals(vertex3) && vertex3.getDegree() == 0 && v.distance(vertex3) > v.distance(vertex2)) {

                // Calculate angle between vertices 1,2,3
                Edge e1 = new Edge(v, vertex2);
                Edge e2 = new Edge(vertex2, vertex3);
                Double angle = calcAngle(e1, e2);

                // If angle between vertices 1,2,3 is good consider connecting them.
                if (angle > ANGLE) {
                  if (bestVertex2 == null || (v.distance(bestVertex2) + bestVertex2.distance(bestVertex3) + ANGLEWEIGHT * (180 - bestAngle)) > (v.distance(vertex2) + vertex2.distance(vertex3) + ANGLEWEIGHT * (180 - angle))) {
                    bestVertex2 = vertex2;
                    bestVertex3 = vertex3;
                    bestAngle = angle;
                  }
                }
              }
            }
          }
        } else {
          Double lineAngle;
          if (!prepend) {
            lineAngle = calcAngle(new Edge(current.getHead(), current.getTail()), new Edge(current.getTail(), vertex2));
            bestVertex2 = current.getTail();
          } else {
            lineAngle = calcAngle(new Edge(current.getTail(), current.getHead()), new Edge(current.getHead(), vertex2));
            bestVertex2 = current.getHead();
          }
          if (lineAngle > APPENDANGLE) {
            if (bestVertex3 == null || (bestVertex2.distance(bestVertex3) + ANGLEWEIGHT * (180 - bestAngle)) > (bestVertex2.distance(vertex2) + ANGLEWEIGHT * (180 - lineAngle))) {
              bestVertex3 = vertex2;
              bestAngle = lineAngle;
            }
          }
        }
      }

      // Connect best 3 vertices if found
      if (current == null) {
        if (bestVertex2 != null) {
          Edge edge1 = new Edge(v, bestVertex2);
          Edge edge2 = new Edge(bestVertex2, bestVertex3);

          current = new LinearCurve(edge1);
          current.connect(edge2);

          List<Vertex> connect = new ArrayList<>();
          connect.add(v);
          connect.add(bestVertex2);
          connect.add(bestVertex3);
          connectVertices(connect);

          if (!prepend) {
            current = find(current.getTail(), current, false);
          } else {
            current = find(current.getHead(), current, true);
          }
        }
      } else {
        if (bestVertex3 != null && current.getEdges().size() < 50) {
          Edge edge = new Edge(bestVertex2, bestVertex3);

          current.connect(edge);

          List<Vertex> connect = new ArrayList<>();
          connect.add(bestVertex2);
          connect.add(bestVertex3);
          connectVertices(connect);

          if (!prepend) {
            current = find(current.getTail(), current, false);
          } else {
            current = find(current.getHead(), current, true);
          }
        }
      }
    }

    return current;
  }

  // Connects vertices with degree 0 to a nearby vertex
  // TODO: connect linearcurves made by this function instead of simply adding new linearcurve for each edge
  private void connectSingleVertices() {
    if (debug != null) {
      programstate = "Connecting single vertices";
    }

    for (Vertex vertex : vertices) {
      if (vertex.getDegree() == 0) {

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
          LinearCurve currentLine = new LinearCurve(new Edge(vertex, bestVertex));
          lines.add(currentLine);
        } else {
          List<Vertex> connect = new ArrayList<>();
          connect.add(vertex);
          connect.add(vertex.getClosest());
          connectVertices(connect);
          LinearCurve currentLine = new LinearCurve(new Edge(vertex, vertex.getClosest()));
          lines.add(currentLine);
        }
      }
    }
  }

  // Connects straight lines
  private void connectLines() {
    if (debug != null) {
      programstate = "Connecting lines";
    }
    for (LinearCurve line : lines) {

      Vertex best1 = null;
      Vertex best2 = null;

      for (LinearCurve line2 : lines) {

        Vertex head1 = line.getHead();
        Vertex head2 = line2.getHead();
        Vertex tail1 = line.getTail();
        Vertex tail2 = line2.getTail();

        if (!head1.equals(head2) && !tail1.equals(tail2) && head1.getDegree() < 3 && tail1.getDegree() < 3 && head2.getDegree() < 3 && tail2.getDegree() < 3) {

          Edge hh = new Edge(tail1, head2);
          Edge ht = new Edge(tail1, tail2);
          Edge th = new Edge(head1, head2);
          Edge tt = new Edge(head1, tail2);

          Double lineAngleHeadHead = calcAngle(new Edge(head1, tail1), hh);
          Double lineAngleHeadTail = calcAngle(new Edge(head1, tail1), ht);
          Double lineAngleTailHead = calcAngle(new Edge(tail1, head1), th);
          Double lineAngleTailTail = calcAngle(new Edge(tail1, head1), tt);

          if (lineAngleHeadHead > LINEANGLE && tail1.distance(head2) < LINEDISTANCE && !addedEdges.contains(new Edge(head2, tail1))) {
            if (best1 == null || (best1.distance(best2) > tail1.distance(head2))) {
              best1 = tail1;
              best2 = head2;
            }
          } else if (lineAngleHeadTail > LINEANGLE && tail1.distance(tail2) < LINEDISTANCE && !addedEdges.contains(new Edge(tail2, tail1))) {
            if (best1 == null || (best1.distance(best2) > tail1.distance(tail2))) {
              best1 = tail1;
              best2 = tail2;
            }
          } else if (lineAngleTailHead > LINEANGLE && head1.distance(head2) < LINEDISTANCE && !addedEdges.contains(new Edge(head2, head1))) {
            if (best1 == null || (best1.distance(best2) > head1.distance(head2))) {
              best1 = head1;
              best2 = head2;
            }
          } else if (lineAngleTailTail > LINEANGLE && head1.distance(tail2) < LINEDISTANCE && !addedEdges.contains(new Edge(tail2, head1))) {
            if (best1 == null || (best1.distance(best2) > head1.distance(tail2))) {
              best1 = head1;
              best2 = tail2;
            }
          }
        }
      }

      // ???
      /*
      if (best1 == null) {
        if (line.getHead().getDegree() == 1) {
          List<Vertex> close = line.getHead().getClose();
          for (Vertex closeVertex : close) {
            for (Vertex closeVertex2 : close) {
              if (!closeVertex.equals(closeVertex2) && !line.getHead().equals(closeVertex) && !line.getHead().equals(closeVertex2)) {
                Double x;
                Vertex vertex = line.getHead();
                if (vertex.getX() > closeVertex.getX()) {
                  if (closeVertex.getX() < closeVertex2.getX()) {
                    x = closeVertex.getX() - closeVertex2.getX();
                    x = x / 2;
                    x = closeVertex2.getX() + x;
                  } else {
                    x = closeVertex2.getX() - closeVertex.getX();
                    x = x / 2;
                    x = closeVertex.getX() + x;
                  }
                } else {
                  if (closeVertex.getX() > closeVertex2.getX()) {
                    x = closeVertex.getX() - closeVertex2.getX();
                    x = x / 2;
                    x = closeVertex2.getX() + x;
                  } else {
                    x = closeVertex2.getX() - closeVertex.getX();
                    x = x / 2;
                    x = closeVertex.getX() + x;
                  }
                }
                Double y;
                if (vertex.getY() > closeVertex.getY()) {
                  if (closeVertex.getY() < closeVertex2.getY()) {
                    y = closeVertex.getY() - closeVertex2.getY();
                    y = y / 2;
                    y = closeVertex2.getY() + y;
                  } else {
                    y = closeVertex2.getY() - closeVertex.getY();
                    y = y / 2;
                    y = closeVertex.getY() + y;
                  }
                } else {
                  if (closeVertex.getY() > closeVertex2.getY()) {
                    y = closeVertex.getY() - closeVertex2.getY();
                    y = y / 2;
                    y = closeVertex2.getY() + y;
                  } else {
                    y = closeVertex2.getY() - closeVertex.getY();
                    y = y / 2;
                    y = closeVertex.getY() + y;
                  }
                }
                Vertex va = new Vertex(x, y);


                Edge e1 = new Edge(vertex, va);
                Edge e2 = new Edge(va, closeVertex);
                Double angle = calcAngle(e1, e2);

                if (angle > 80 && angle < 100) {
                  System.out.println(vertex.toString() + " " + closeVertex.toString() + " " + closeVertex2.toString());
                  curve = addVertex(va, curve);
                  va.setClosest(closeVertex);
                  Vertex bestVertex = vertices.get(vertices.size() - 1);
                  curve = disconnectVertices(closeVertex, closeVertex2, curve);
                  List<Vertex> connect = new ArrayList<>();
                  connect.add(closeVertex);
                  connect.add(va);
                  connect.add(closeVertex2);
                  curve = connectVertices(connect, curve);
                  break;
                }
              }
            }
          }
        }
        if (line.getTail().getDegree() == 1) {
            List<Vertex> close = line.getTail().getClose();
            for (Vertex closeVertex : close) {
              for (Vertex closeVertex2 : close) {
                if (!closeVertex.equals(closeVertex2) && !line.getTail().equals(closeVertex) && !line.getTail().equals(closeVertex2)) {
                  Double x;
                  Vertex vertex = line.getTail();
                  if (vertex.getX() > closeVertex.getX()) {
                    if (closeVertex.getX() < closeVertex2.getX()) {
                      x = closeVertex.getX() - closeVertex2.getX();
                      x = x / 2;
                      x = closeVertex2.getX() + x;
                    } else {
                      x = closeVertex2.getX() - closeVertex.getX();
                      x = x / 2;
                      x = closeVertex.getX() + x;
                    }
                  } else {
                    if (closeVertex.getX() > closeVertex2.getX()) {
                      x = closeVertex.getX() - closeVertex2.getX();
                      x = x / 2;
                      x = closeVertex2.getX() + x;
                    } else {
                      x = closeVertex2.getX() - closeVertex.getX();
                      x = x / 2;
                      x = closeVertex.getX() + x;
                    }
                  }
                  Double y;
                  if (vertex.getY() > closeVertex.getY()) {
                    if (closeVertex.getY() < closeVertex2.getY()) {
                      y = closeVertex.getY() - closeVertex2.getY();
                      y = y / 2;
                      y = closeVertex2.getY() + y;
                    } else {
                      y = closeVertex2.getY() - closeVertex.getY();
                      y = y / 2;
                      y = closeVertex.getY() + y;
                    }
                  } else {
                    if (closeVertex.getY() > closeVertex2.getY()) {
                      y = closeVertex.getY() - closeVertex2.getY();
                      y = y / 2;
                      y = closeVertex2.getY() + y;
                    } else {
                      y = closeVertex2.getY() - closeVertex.getY();
                      y = y / 2;
                      y = closeVertex.getY() + y;
                    }
                  }
                  Vertex va = new Vertex(x, y);


                  Edge e1 = new Edge(vertex, va);
                  Edge e2 = new Edge(va, closeVertex);
                  Double angle = calcAngle(e1, e2);

                  if (angle > 80 && angle < 100) {
                    System.out.println(vertex.toString() + " " + closeVertex.toString() + " " + closeVertex2.toString());
                    curve = addVertex(va, curve);
                    va.setClosest(closeVertex);
                    Vertex bestVertex = vertices.get(vertices.size() - 1);
                    curve = disconnectVertices(closeVertex, closeVertex2, curve);
                    List<Vertex> connect = new ArrayList<>();
                    connect.add(closeVertex);
                    connect.add(va);
                    connect.add(closeVertex2);
                    curve = connectVertices(connect, curve);
                    break;
                  }
                }
              }
            }
          }

      }*/

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
    if (debug != null) {
      programstate = "Inserting vertices at intersections";
    }
    List<Edge> edges = new ArrayList<>();
    edges.addAll(outputCurve.getEdges());

    // Check for intersections and insert vertices
    for (Edge edge1 : edges) {
      for (Edge edge2 : edges) {
        Vertex tail1 = edge1.getTail();
        Vertex tail2 = edge2.getTail();
        Vertex head1 = edge1.getHead();
        Vertex head2 = edge2.getHead();
        if (!edge1.equals(edge2) && !tail1.equals(tail2) && !tail1.equals(head2) && !head1.equals(tail2) && !head1.equals(head2)) {
          Vertex vertex = edge1.intersects(edge2);
          if (vertex != null  && !addedVertices.contains(vertex)) {
            addVertex(vertex);
            List<Vertex> disconnect = new ArrayList<>();
            disconnect.add(head1);
            disconnect.add(tail1);
            disconnectVertices(disconnect);
            disconnect.clear();
            disconnect.add(head2);
            disconnect.add(tail2);
            disconnectVertices(disconnect);
            List<Vertex> connect = new ArrayList<>();

            int size = vertices.size();
            connect.add(head1);
            connect.add(vertices.get(size - 1));
            connect.add(tail1);
            connectVertices(connect);
            connect.clear();
            connect.add(head2);
            connect.add(vertices.get(size - 1));
            connect.add(tail2);
            connectVertices(connect);
          }
        }
      }
    }
  }

  // Calculates angle between two edges
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

    String message = "";

    if (debug != null) {

      state = new DebugState();

      state.addVertices(vertices);

      message = programstate + ": Connecting ";

      // Current curves
      List<Edge> debugEdges = new ArrayList<>();
      debugEdges.addAll(outputCurve.getEdges());

      state.addEdges(debugEdges);

    }

    for (int i = 0; i < connect.size() - 1; i++) {

      Edge edge = new Edge(connect.get(i), connect.get(i+1));

      outputCurve.addEdge(edge);

      connect.get(i).incDegree();
      connect.get(i+1).incDegree();

      if (debug != null) {
        state.addEdge(edge, Color.green);

        state.addVertex(connect.get(i), Color.RED);
        state.addVertex(connect.get(i + 1), Color.RED);

        message += connect.get(i).getId() + " " + connect.get(i + 1).getId() + " ";
      }
    }

    if (debug != null) {
      state.setMessage(message);
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
      vertices.add(vertexCopy);
      addedVertices.add(vertexCopy);

      if (debug != null) {
        state = new DebugState();

        state.addVertices(vertices);

        state.addVertex(vertexCopy, Color.GREEN);

        // Current curves
        List<Edge> debugEdges = new ArrayList<>();
        debugEdges.addAll(outputCurve.getEdges());

        state.addEdges(debugEdges);

        state.setMessage(programstate + ": Inserting vertex at X: " + vertexCopy.getX() + " Y: " + vertexCopy.getY());

        debug.addState(state);
      }
    }
  }

  // Disconnects list of vertices
  private void disconnectVertices(List<Vertex> disconnect) {

    String message = "";

    if (debug != null) {

      state = new DebugState();

      state.addVertices(vertices);

      message = programstate + ": Disconnecting ";

      // Current curves
      List<Edge> debugEdges = new ArrayList<>();
      debugEdges.addAll(outputCurve.getEdges());

      state.addEdges(debugEdges);

    }

    for (int i = 0; i < disconnect.size() - 1; i++) {

      Edge edge = new Edge(disconnect.get(i), disconnect.get(i+1));

      outputCurve.removeEdge(edge);

      disconnect.get(i).decDegree();
      disconnect.get(i+1).decDegree();

      if (debug != null) {
        state.addEdge(edge, Color.RED);

        state.addVertex(disconnect.get(i), Color.RED);
        state.addVertex(disconnect.get(i + 1), Color.RED);

        message += disconnect.get(i).getId() + " " + disconnect.get(i + 1).getId() + " ";
      }
    }

    if (debug != null) {
      state.setMessage(message);
      debug.addState(state);
    }
  }
}



