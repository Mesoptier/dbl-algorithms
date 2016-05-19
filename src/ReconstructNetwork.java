import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ReconstructNetwork extends Reconstruct {

  private Curve curve;

  @Override
  public ProblemOutput start() {
    List<Curve> curves;
    findClosestPoints();
    curves = findStraightLines();
    return new ProblemOutput(vertices, curves);
  }

  private void findClosestPoints() {

    //Finds closest point and other close points for each vertex
    for (Vertex vertex1 : vertices) {
      Vertex closest = null;
      for (Vertex vertex2 : vertices) {
        if (!vertex1.equals(vertex2) && vertex1.distance(vertex2) < 0.08) {
          if (closest == null || (vertex1.distance(vertex2) < vertex1.distance(closest))) {
            closest = vertex2;
          }
          vertex1.addClose(vertex2);
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
      Vertex best1 = null;
      Vertex best2 = null;
      Double bestAngle = null;
      for (Vertex vertex2 : vertices) {
        for (Vertex vertex3 : vertices) {
          if (!vertex1.equals(vertex2) && !vertex1.equals(vertex3) && !vertex2.equals(vertex3)) {
            if (vertex1.distance(vertex2) < 0.08 && vertex2.distance(vertex3) < 0.08) {
              if (vertex1.getDegree() < 2 && vertex2.getDegree() < 1 && vertex3.getDegree() < 2) {

                Double angle = calcAngle(vertex1, vertex2, vertex3);

                if (angle < 5) {
                  if (best1 == null || (vertex1.distance(best2) - 0.005*bestAngle) > (vertex1.distance(vertex3) - 0.005*angle)){
                    best1 = vertex2;
                    best2 = vertex3;
                    bestAngle = angle;
                  }
                }
              }
            }
          }
        }
      }

      //Connect best 3 vertices if found
      if (best1 != null) {
        List<Vertex> connect = new ArrayList<>();
        connect.add(vertex1);
        connect.add(best1);
        connect.add(best2);
        connectVertices(connect);
      }
    }

    //Connect vertices with degree of 1 to closest neighbor
    //TODO: Should probably look at points in a certain radius and pick one with best angle instead

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

    //TODO: Find intersections and insert point

    curves.add(curve);
    return curves;
  }

  //Calculates angle between three vertices
  //TODO: not sure if this is correct
  private Double calcAngle(Vertex vertex1, Vertex vertex2, Vertex vertex3) {

    Edge AB = new Edge(vertex1, vertex2);
    Edge BC = new Edge(vertex2, vertex3);

    Double x = (vertex2.getX() - vertex1.getX()) * (vertex3.getX() - vertex2.getX());
    Double y = (vertex2.getY() - vertex1.getY()) * (vertex3.getY() - vertex2.getY());

    double dotProduct = x + y;

    Double angle = Math.acos(dotProduct / (AB.distance() * BC.distance())) * 180 / Math.PI;

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

    debug.addState(state);
  }
}


