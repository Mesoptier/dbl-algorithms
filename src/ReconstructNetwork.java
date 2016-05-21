import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ReconstructNetwork extends Reconstruct {

  private Curve curve;
  private List<Vertex> addedVertices = new ArrayList<>();
  private Double DISTANCE;

  @Override
  public ProblemOutput start() {
    DISTANCE = 1 / (vertices.size() * 0.08);
    List<Curve> curves;
    findClosestPoints();
    curves = findStraightLines();
    return new ProblemOutput(vertices, curves);
  }

  private void findClosestPoints() {

    // Finds closest vertex for each vertex
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

  private List<Curve> findStraightLines() {

    List<Curve> curves = new ArrayList<>();
    curve = new Curve();

    //For each three vertices connect them if they are close enough and in a straight line
    //TODO: Might be better to check within a certain radius based on number of points instead of fixed 0.08
    for (Vertex vertex1 : vertices) {
      Vertex bestVertex2 = null;
      Vertex bestVertex3 = null;
      Double bestAngle = null;
      if (vertex1.getDegree() < 2) {
        List<Vertex> close1 = vertex1.getClose();
        for (Vertex vertex2 : close1) {
          if (vertex2.getDegree() < 1) {
            List<Vertex> close2 = vertex2.getClose();
            for (Vertex vertex3 : close2) {
              if (!vertex1.equals(vertex3) && vertex3.getDegree() < 2 && vertex1.distance(vertex3) > vertex1.distance(vertex2)) {

                Edge e1 = new Edge(vertex1, vertex2);
                Edge e2 = new Edge(vertex2, vertex3);
                Double angle = calcAngle(e1, e2);

                if (angle > 150) {
                  if (bestVertex2 == null || (vertex1.distance(bestVertex2) + bestVertex2.distance(bestVertex3) + 0.001 * (180 - bestAngle)) > (vertex1.distance(vertex2) + vertex2.distance(vertex3) + 0.001 * (180 - angle))) {
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

      //Connect best 3 vertices if found
      if (bestVertex2 != null) {
        List<Vertex> connect = new ArrayList<>();
        connect.add(vertex1);
        connect.add(bestVertex2);
        connect.add(bestVertex3);
        connectVertices(connect);
      }
    }

    //Connect vertices with degree of 1 to closest neighbor
    //TODO: Improve this, maybe look at points in a certain radius and pick one with best angle

    for (Vertex vertex : vertices) {
      if (vertex.getDegree() < 2) {
        if (vertex.getClosest().getDegree() < 2) {
          List<Vertex> connect = new ArrayList<>();
          connect.add(vertex);
          connect.add(vertex.getClosest());
          connectVertices(connect);
        }
      }
    }

    //Check for intersections and insert vertices
    List<Edge> edges = new ArrayList<>();
    edges.addAll(curve.getEdges());

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

    curves.add(curve);
    return curves;
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

    Double angle = Math.acos(dotProduct / (e1.distance() * e2.distance())) * 180 / Math.PI;

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

      state.setMessage("Inserting vertex at intersection X: " + vertexCopy.getX() + " Y: " + vertexCopy.getY());

      if (debug != null) {
        debug.addState(state);
      }
    }
  }
}


