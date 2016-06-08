import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ReconstructNetwork extends Reconstruct {

  /* The curve containing all edges */
  private Curve outputCurve = new Curve();

  /* List of inserted vertices */
  private List<Vertex> addedVertices;

  /* List of added edges by connectLines */
  private List<Edge> addedEdges;

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
  private String programstate;

  /* List of straight lines found by findStraightlines */
  private List<LinearCurve> lines;

  /* Average number of close vertices each vertex has */
  private Double avgClose;

  /* Maximum distance a point can lie from the circle to still be considered as part of the circle */
  private Double CIRCLEDEVIATION;

  private DebugState state;
  private Logger logger;

  @Override
  public ProblemOutput start() {

    List<Curve> curves = new ArrayList<>();

    /* Initialize variables and calculate close(st) vertices */
    initialize();
    /* Try to detects roundabouts */
    //findRoundabouts();
    /* Try to find straight lines */
    findStraightLines();
    /* Connect vertices that have degree 0 */
    connectSingleVertices();
    /* Connect individual lines */
    connectLines();
    /* Insert vertices at intersections */
    insertIntersections();
    /* Connect graph if disconnected */
    connectGraph2();

    curves.add(outputCurve);

    return new ProblemOutput(vertices, curves);
  }

  /* Initializes Distance and Angle variables and calculates close(st) vertices */
  private void initialize() {

    /* Initialize angle and distance */
    DISTANCE = 1 / (vertices.size() * 0.05 + 5);
    LINEDISTANCE = 2 * DISTANCE;
    ANGLE = DISTANCE * 150;
    APPENDANGLE = 3.2 * ANGLE;
    LINEANGLE = 4.3 * ANGLE;
    ANGLEWEIGHT = 0.002;
    CIRCLEDEVIATION = 0.125 * DISTANCE;

    addedVertices = new ArrayList<>();
    addedEdges = new ArrayList<>();
    lines = new ArrayList<>();
    avgClose = 0.0;

    if (debug != null) {
      logger = new Logger();
      programstate = "Initializing";
      logger.log("Distance: " + DISTANCE.toString());
      logger.log("Angle: " + ANGLE.toString());
      logger.log("LineDistance: " + LINEDISTANCE.toString());
      logger.log("AppendAngle: " + APPENDANGLE.toString());
      logger.log("LineAngle : " + LINEANGLE.toString());
      logger.log("Circle Deviation: " + CIRCLEDEVIATION);
    }

    /* Calculate close vertices and closest vertex for each vertex */
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

    /* Compute average number of close vertices each vertex has */
    avgClose = avgClose / vertices.size();
  }

  /* Tries to find roundabouts in the input. Starts by finding vertices that have more than average close vertices.
   * Then checks if a circle can be formed at those points.
   *
   * Works as long as there are no roundabouts too close to each other and roundabout has at least 7 points.
   *
   * Center calculation adapted from http://mathforum.org/library/drmath/view/54323.html
   */
  //TODO: Find better method, connect roundabouts, parameters for distance between two roundabouts & parameter for minimum number of points on roundabout
  private void findRoundabouts() {
    if (debug != null) {
      programstate = "Finding roundabouts";
    }

    /* Calculate centers of potential roundabouts */
    List<Vertex> centers = new ArrayList<>();

    for (Vertex v1 : vertices) {
      List<Vertex> close1 = v1.getClose();
      if (close1.size() > avgClose) {

        for (Vertex v2 : close1) {
          List<Vertex> close2 = v2.getClose();
          if (close2.size() > avgClose) {

            for (Vertex v3 : close2) {
              if (!v1.equals(v2) && !v2.equals(v3) && !v1.equals(v3) && v3.getClose().size() > avgClose) {

                final double TOL = 0.00000000001;

                final double offset = Math.pow(v2.getX(), 2) + Math.pow(v2.getY(), 2);
                final double bc = (Math.pow(v1.getX(), 2) + Math.pow(v1.getY(), 2) - offset) / 2.0;
                final double cd = (offset - Math.pow(v3.getX(), 2) - Math.pow(v3.getY(), 2)) / 2.0;
                final double det = (v1.getX() - v2.getX()) * (v2.getY() - v3.getY()) - (v2.getX() - v3.getX()) * (v1.getY() - v2.getY());

                /* If points lie in (almost) straight line the center will become infinitely far away, if that happens skip the points */
                if (Math.abs(det) < TOL) {
                  if (debug != null) {
                    logger.log("Warning: probably dividing by 0 at points " + v1.getId() + " " + v2.getId() + " " + v3.getId() + " - skipping");
                  }
                } else {
                  final double idet = 1 / det;

                  final double centerx = (bc * (v2.getY() - v3.getY()) - cd * (v1.getY() - v2.getY())) * idet;
                  final double centery = (cd * (v1.getX() - v2.getX()) - bc * (v2.getX() - v3.getX())) * idet;
                  final double radius = Math.sqrt(Math.pow(v2.getX() - centerx, 2) + Math.pow(v2.getY() - centery, 2));

                  if (radius < DISTANCE) {
                    Vertex center = new Vertex(centerx, centery);
                    center.setRadius(radius);
                    centers.add(center);
                  }
                }
              }
            }
          }
        }
      }
    }

    /* After finding centers compute the average X and Y of centers that are close to each other */
    List<Vertex> finalCenters = new ArrayList<>();

    for (int i = 0; i < centers.size() - 1; i++) {
      if (!centers.get(i).getConsidered()) {
        Double xMean = centers.get(i).getX();
        Double yMean = centers.get(i).getY();
        Double radiusMean = centers.get(i).getRadius();
        int count = 1;
        for (int j = i + 1; j < centers.size(); j++) {
          Double x1 = centers.get(i).getX();
          Double x2 = centers.get(j).getX();
          Double y1 = centers.get(i).getY();
          Double y2 = centers.get(j).getY();
          if (x1 > x2 - 0.2 && x1 < x2 + 0.2 && y1 > y2 - 0.2 && y1 < y2 + 0.2) {
            centers.get(j).setConsidered(true);
            xMean += x2;
            yMean += y2;
            radiusMean += centers.get(j).getRadius();
            count++;
          }
        }
        Double x = xMean / count;
        Double y = yMean / count;
        Double radius = radiusMean / count;
        Vertex meanCenter = new Vertex(x, y);
        meanCenter.setRadius(radius);
        finalCenters.add(meanCenter);
      }
    }

    /* For the centers that we get compute the vertices that lie approximately on the circle, if more than
     * 6 points lie on the circle assume it's a roundabout.
     */
    for (Vertex center : finalCenters) {
      List<Vertex> onCircle = null;
      Double radius = center.getRadius();
      for (Vertex vertex : vertices) {
        Double distance = vertex.distance(center);
        if (distance > radius - 2 * CIRCLEDEVIATION && distance < radius + CIRCLEDEVIATION) {
          if (onCircle == null) {
            onCircle = new ArrayList<>();
          }
          onCircle.add(vertex);
        }
      }
      if (onCircle != null && onCircle.size() > 6) {
        if (debug != null) {
          state = new DebugState();
          state.addVertices(vertices);
          state.addVertices(onCircle, Color.GREEN);
          state.addVertex(center, Color.RED);
          debug.addState(state);
        }
        for (Vertex on : onCircle) {
          on.setOnRoundabout(true);
        }
        /*
        List<Vertex> add = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
          Double j = i * 20 * Math.PI / 180;
          Double x = center.getX() + radius * Math.sin(j);
          Double y = center.getY() + radius * Math.cos(j);
          Vertex ll = new Vertex(x, y);
          addVertex(ll);
          add.add(ll);
        }
        add.add(add.get(0));
        connectVertices(add);
        for (Vertex v : onCircle) {
          //vertices.remove(v);
          verticesCopy.remove(v);
        }*/
      }
    }
  }

  /* Finds straight lines in the input.
   * Starts by finding three vertices that lie in a straight line.
   * If such a triple is found it will try to recursively append and prepend until no more vertices can be found.
   */
  private void findStraightLines() {
    if (debug != null) {
      programstate = "Finding straight lines";
    }
    for (Vertex vertex1 : vertices) {
      if (vertex1.getDegree() == 0 && !vertex1.getOnRoundabout()) {

        LinearCurve current = findStraightTriple(vertex1);

        /* Try to append and prepend */
        if (current != null) {
          current = append(current.getTail(), current, false, 0);
          current = append(current.getHead(), current, true, 0);
          lines.add(current);
        }
      }
    }
  }

  /* Tries to find two points close to v with degree 0 that form a straight line
   * If there are multiple options it will try to take the points with the best angle and smallest distance.
   */
  private LinearCurve findStraightTriple(Vertex v) {

    LinearCurve current = null;

    if (v.getDegree() < 3 && !v.getOnRoundabout()) {
      Vertex bestVertex2 = null;
      Vertex bestVertex3 = null;
      Double bestAngle = null;
      List<Vertex> close1 = v.getClose();
      for (Vertex vertex2 : close1) {
        if (!vertex2.getOnRoundabout()) {
          if (vertex2.getDegree() == 0) {
            List<Vertex> close2 = vertex2.getClose();
            for (Vertex vertex3 : close2) {
              if (!v.equals(vertex3) && vertex3.getDegree() == 0 && v.distance(vertex3) > v.distance(vertex2) && !vertex3.getOnRoundabout()) {

                // Calculate angle between vertices 1,2,3
                Edge e1 = new Edge(v, vertex2);
                Edge e2 = new Edge(vertex2, vertex3);
                Double angle = 180 - calcAngle(e1, e2);

                // If angle between vertices 1,2,3 is good consider connecting them.
                if (angle < ANGLE) {
                  if (bestVertex2 == null || (v.distance(bestVertex2) + bestVertex2.distance(bestVertex3) + ANGLEWEIGHT * bestAngle) > (v.distance(vertex2) + vertex2.distance(vertex3) + ANGLEWEIGHT * angle)) {
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

      // Connect best 3 vertices if found
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
        }
    }

    /* Return curve (with 3 points) or null if no curve found */
    return current;
  }

  /* Tries to append or prepend one vertex at a time, until no vertices can be found
   * Amount of recursions limited to 50 to prevent stack overflow
   * Tries to find the point with best distance and angle if there are multiple candidate points
   */
  private LinearCurve append(Vertex v, LinearCurve current, Boolean prepend, int recursions) {

    if (v.getDegree() < 3 && !v.getOnRoundabout()) {
      Vertex bestVertex2 = null;
      Vertex bestVertex3 = null;
      Double bestAngle = null;
      List<Vertex> close1 = v.getClose();
      for (Vertex vertex2 : close1) {
        Double lineAngle;
        if (!prepend) {
          lineAngle = 180 - calcAngle(new Edge(current.getHead(), current.getTail()), new Edge(current.getTail(), vertex2));
          bestVertex2 = current.getTail();
        } else {
          lineAngle = 180 - calcAngle(new Edge(current.getTail(), current.getHead()), new Edge(current.getHead(), vertex2));
          bestVertex2 = current.getHead();
        }
        if (lineAngle < APPENDANGLE) {
          if (bestVertex3 == null || (bestVertex2.distance(bestVertex3) + ANGLEWEIGHT * bestAngle) > (bestVertex2.distance(vertex2) + ANGLEWEIGHT * lineAngle)) {
            bestVertex3 = vertex2;
            bestAngle = lineAngle;
          }
        }
      }

      if (bestVertex3 != null && recursions < 50) {
        Edge edge = new Edge(bestVertex2, bestVertex3);

        current.connect(edge);

        List<Vertex> connect = new ArrayList<>();
        connect.add(bestVertex2);
        connect.add(bestVertex3);
        connectVertices(connect);

        int n = recursions + 1;

        if (!prepend) {
          current = append(current.getTail(), current, false, n);
        } else {
          current = append(current.getHead(), current, true, n);
        }
      }
    }

    return current;
  }

  /* If there are vertices with degree 0 this function connects them to either a close vertex with degree <= 1 or
   * the closest vertex.
   */
  private void connectSingleVertices() {
    if (debug != null) {
      programstate = "Connecting single vertices";
    }

    for (Vertex vertex : vertices) {
      if (vertex.getDegree() == 0 && !vertex.getOnRoundabout()) {

        // First consider connecting to close vertices with degree < 2
        List<Vertex> close = vertex.getClose();
        Vertex bestVertex = null;
        for (Vertex closeVertex : close) {
          if (closeVertex.getDegree() < 2 && !vertex.getOnRoundabout()) {
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
          //LinearCurve currentLine = new LinearCurve(new Edge(vertex, vertex.getClosest()));
          //lines.add(currentLine);
        }
      }
    }
  }

  /* Connects individual straight lines */
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

        if (!head1.equals(head2) && !tail1.equals(tail2)) {

          Edge hh = new Edge(tail1, head2);
          Edge ht = new Edge(tail1, tail2);
          Edge th = new Edge(head1, head2);
          Edge tt = new Edge(head1, tail2);

          Double lineAngleHeadHead = 180 - calcAngle(new Edge(head1, tail1), hh);
          Double lineAngleHeadTail = 180 - calcAngle(new Edge(head1, tail1), ht);
          Double lineAngleTailHead = 180 - calcAngle(new Edge(tail1, head1), th);
          Double lineAngleTailTail = 180 - calcAngle(new Edge(tail1, head1), tt);

          if (lineAngleHeadHead < LINEANGLE && tail1.distance(head2) < LINEDISTANCE && !addedEdges.contains(new Edge(head2, tail1)) && tail1.getDegree() < 2 && head2.getDegree() < 3 && !tail1.getOnRoundabout()) {
            if (best1 == null || (best1.distance(best2) > tail1.distance(head2))) {
              best1 = tail1;
              best2 = head2;
            }
          } else if (lineAngleHeadTail < LINEANGLE && tail1.distance(tail2) < LINEDISTANCE && !addedEdges.contains(new Edge(tail2, tail1)) && tail1.getDegree() < 2 && tail2.getDegree() < 3 && !tail1.getOnRoundabout()) {
            if (best1 == null || (best1.distance(best2) > tail1.distance(tail2))) {
              best1 = tail1;
              best2 = tail2;
            }
          } else if (lineAngleTailHead < LINEANGLE && head1.distance(head2) < LINEDISTANCE && !addedEdges.contains(new Edge(head2, head1)) && head1.getDegree() < 2 && head2.getDegree() < 3 && !head1.getOnRoundabout()) {
            if (best1 == null || (best1.distance(best2) > head1.distance(head2))) {
              best1 = head1;
              best2 = head2;
            }
          } else if (lineAngleTailTail < LINEANGLE && head1.distance(tail2) < LINEDISTANCE && !addedEdges.contains(new Edge(tail2, head1)) && head1.getDegree() < 2 && tail2.getDegree() < 3 && !head1.getOnRoundabout()) {
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

  /* Finds intersections and inserts a vertex at the intersection.
   * Removes the original edges that intersected and connects the vertices that had those edges with the
   * inserted vertex.
   */
  private void insertIntersections() {
    if (debug != null) {
      programstate = "Inserting vertices at intersections";
    }
    List<Edge> edges = new ArrayList<>();
    edges.addAll(outputCurve.getEdges());

    /* For every two edges check if they intersect, if they do, create new vertex and reconnect the 5 vertices */
    for (int i = 0; i < edges.size(); i++) {
      for (int j = 0; j < edges.size(); j++) {
        Edge edge1 = edges.get(i);
        Edge edge2 = edges.get(j);
        Vertex tail1 = edge1.getTail();
        Vertex tail2 = edge2.getTail();
        Vertex head1 = edge1.getHead();
        Vertex head2 = edge2.getHead();
        if (!edge1.equals(edge2) && !tail1.equals(tail2) && !tail1.equals(head2) && !head1.equals(tail2) && !head1.equals(head2)) {
          Vertex vertex = edge1.intersects(edge2);
          if (vertex != null && !addedVertices.contains(vertex)) {
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

            edges.remove(edge1);
            edges.remove(edge2);
            edges.add(new Edge(head1, vertices.get(size - 1)));
            edges.add(new Edge(vertices.get(size - 1), tail1));
            edges.add(new Edge(head2, vertices.get(size - 1)));
            edges.add(new Edge(vertices.get(size - 1), tail2));
            if (i > 0) {
              i--;
            }
            if (j > 0) {
              j--;
            }
          }
        }
      }
    }
  }

  /* Uses BFS to mark all vertices connected to the first vertex.
   * Then loops over all vertices to check if there are any that were not marked.
   * If any of them were not marked it will start a new BFS from that vertex.
   * This is applied until all vertices are marked to find all disconnected parts of the network.
   */
  private Vertex checkConnected(Vertex root) {
    ArrayList<Edge> edges = new ArrayList<>();
    edges.addAll(outputCurve.getEdges());

    Queue<Vertex> queue = new LinkedList<>();
    queue.add(root);
    root.setConsidered(true);

    if (debug != null) {
      state = new DebugState();
      state.addVertices(vertices);
      state.addEdges(edges);
      programstate = "Checking graph connected: ";
      state.addVertex(queue.peek(), Color.GREEN);
    }

    while (!queue.isEmpty()) {
      Vertex vertex = queue.remove();
      for (Edge edge : edges) {
        if (edge.getHead().equals(vertex) && !edge.getTail().getConsidered()) {
          queue.add(edge.getTail());
          edge.getTail().setConsidered(true);
          if (debug != null) {
            state.addVertex(edge.getTail(), Color.GREEN);
          }
        } else if (edge.getTail().equals(vertex) && !edge.getHead().getConsidered()) {
          queue.add(edge.getHead());
          edge.getHead().setConsidered(true);
          if (debug != null) {
            state.addVertex(edge.getHead(), Color.GREEN);
          }
        }
      }
    }

    for (Vertex vertex : vertices) {
      if (!vertex.getConsidered()) {
        if (debug != null) {
          state.addVertex(vertex, Color.RED);
          programstate += "false";
          state.setMessage(programstate);
          debug.addState(state);
        }
        return vertex;
      }
    }
    if (debug != null) {
      programstate += "true";
      state.setMessage(programstate);
      debug.addState(state);
    }
    return null;
  }

  /* Checks if graph is connected
   * If not it will connect it
   * TODO: improve connection
   */
  private void connectGraph() {

    /* Find all disconnected vertices if there are any */
    for (Vertex vertex : vertices) {
      vertex.setConsidered(false);
    }

    /* Recursive BFS to find vertices of disconnected parts */
    List<Vertex> disconnected = new ArrayList<>();
    disconnected.add(vertices.get(0));
    Boolean connected = false;
    Vertex last = vertices.get(0);

    while (!connected) {
      Vertex vertex = checkConnected(last);
      if (vertex == null) {
        connected = true;
      } else {
        disconnected.add(vertex);
        last = vertex;
      }
    }

    if (disconnected.size() > 1) {
      connectVertices(disconnected);
      insertIntersections();
    }
  }

  // todo: fix
  private void connectGraph2() {

    /* Find all disconnected vertices if there are any */
    for (Vertex vertex : vertices) {
      vertex.setConsidered(false);
    }

    /* Recursive BFS to find vertices of disconnected parts */
    List<Vertex> disconnected = new ArrayList<>();
    disconnected.add(vertices.get(0));
    Boolean connected = false;
    Vertex last = vertices.get(0);

    while (!connected) {
      Vertex vertex = checkConnected2(last);
      if (vertex == null) {
        connected = true;
      } else {
        last = vertex;
      }
    }

    boolean zz[] = new boolean[10000];
    boolean zzz[][] = new boolean[10000][10000];
    for (int i = 0; i < 10000; i++) {
        zz[i] = false;
      for (int j = 0; j < 10000; j++) {
        zzz[i][j] = false;
      }
    }

    for (int i = 0; i < parts.size(); i++) {
      List<Vertex> l = parts.get(i);
      Vertex b1 = null;
      Vertex b2 = null;
      int k = 0;
      for (Vertex v : l) {
        for (int j = 0; j < parts.size(); j++) {
          if (zz[i] == false && zzz[i][j] == false && zzz[j][i] == false) {
            List<Vertex> l2 = parts.get(j);
            for (Vertex w : l2) {
              if (!l.equals(l2)) {
                if (b1 == null || v.distance(w) < b1.distance(b2)) {
                  b1 = v;
                  b2 = w;
                  k = j;
                }
              }
            }
          }
        }
      }

      if (b1 != null) {
        zz[i] = true;
        zzz[i][k] = true;
        zzz[k][i] = true;
        List<Vertex> connect = new ArrayList<>();
        connect.add(b1);
        connect.add(b2);
        connectVertices(connect);
      }
    }
  }

  List<List<Vertex>> parts = new ArrayList<>();
  private Vertex checkConnected2(Vertex root) {
    List<Vertex> cur = new ArrayList<>();

    ArrayList<Edge> edges = new ArrayList<>();
    edges.addAll(outputCurve.getEdges());

    Queue<Vertex> queue = new LinkedList<>();
    queue.add(root);
    cur.add(root);
    root.setConsidered(true);

    if (debug != null) {
      state = new DebugState();
      state.addVertices(vertices);
      state.addEdges(edges);
      programstate = "Checking graph connected: ";
      state.addVertex(queue.peek(), Color.GREEN);
    }

    while (!queue.isEmpty()) {
      Vertex vertex = queue.remove();
      for (Edge edge : edges) {
        if (edge.getHead().equals(vertex) && !edge.getTail().getConsidered()) {
          queue.add(edge.getTail());
          edge.getTail().setConsidered(true);
          cur.add(edge.getTail());
          if (debug != null) {
            state.addVertex(edge.getTail(), Color.GREEN);
          }
        } else if (edge.getTail().equals(vertex) && !edge.getHead().getConsidered()) {
          queue.add(edge.getHead());
          edge.getHead().setConsidered(true);
          cur.add(edge.getHead());
          if (debug != null) {
            state.addVertex(edge.getHead(), Color.GREEN);
          }
        }
      }
    }

    parts.add(cur);

    for (Vertex vertex : vertices) {
      if (!vertex.getConsidered()) {
        if (debug != null) {
          state.addVertex(vertex, Color.RED);
          programstate += "false";
          state.setMessage(programstate);
          debug.addState(state);
        }
        return vertex;
      }
    }
    if (debug != null) {
      programstate += "true";
      state.setMessage(programstate);
      debug.addState(state);
    }
    return null;
  }

  /* Calculates the angle between two edges.
   * If one edge is between vertices (A, B) and the other is between (B, C) it will return the angle ABC.
   */
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

    /* Sometimes value can get lower than -1 or 1 which will result in Math.acos throwing an error.
     * Have not encountered a case where value gets greater than 1 but included it in case it happens.
     */
    if (value < -1.0) {
      value = -1.0;
    }
    if (value > 1.0) {
      value = 1.0;
    }
    Double angle = Math.acos(value) * 180 / Math.PI;

    return angle;
  }

  /* Connects a list of vertices.
   * Adds the edges to the outputCurve and increases the degrees of the vertices.
   * Also creates a debugState if using GUI
   */
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

  /* Adds vertex to the list of vertices and makes a debugState
   * Also creates a debugState if using GUI
   */
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

  /* Disconnects a list of vertices.
   * Remove the edges from the outputCurve and decreases the degrees of the vertices.
   * Also creates a debugState if using GUI
   */
  private void disconnectVertices(List<Vertex> disconnect) {

    String message = "";

    if (debug != null) {

      state = new DebugState();

      state.addVertices(vertices);

      message = programstate + ": Disconnecting ";

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



