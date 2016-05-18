import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ReconstructNetwork extends Reconstruct {

  @Override
  public ProblemOutput start() {
    List<Curve> curves;
    findClosestPoints();
    curves = findStraightLines();
    return new ProblemOutput(vertices, curves);
  }

  private void findClosestPoints() {

    for (Vertex vertex1 : vertices) {
      Vertex closest = null;
      for (Vertex vertex2 : vertices) {
        if (!vertex2.equals(vertex1) && (closest == null || (vertex1.distance(vertex2) < vertex1.distance(closest)))) {
          if(vertex2.getClosest() != null) {
            if(!(vertex2.getClosest().equals(vertex1))) {
              closest = vertex2;
            }
          } else {
            closest = vertex2;
          }
        }
      }
      vertex1.setClosest(closest);
    }
  }

  private List<Curve> findStraightLines() {

    List<Curve> curves = new ArrayList<>();
    Curve curve = new Curve();

    //For each three vertices connect them if they are close enough and in a straight line
    //TODO: Might be better to check within a certain radius based on number of points instead of fixed 0.08
    //TODO: Also consider angle when calculating best
    for (Vertex vertex1 : vertices) {
      Vertex best1 = null;
      Vertex best2 = null;
      for (Vertex vertex2 : vertices) {
        for (Vertex vertex3 : vertices) {
          if (!vertex1.equals(vertex2) && !vertex1.equals(vertex3) && !vertex2.equals(vertex3)) {
            if (vertex1.distance(vertex2) < 0.08 && vertex2.distance(vertex3) < 0.08) {
              if (vertex1.getDegree() < 2 && vertex2.getDegree() < 1 && vertex3.getDegree() < 2) {

                Edge AB = new Edge(vertex1, vertex2);
                Edge BC = new Edge(vertex2, vertex3);

                Double x = (vertex2.getX() - vertex1.getX()) * (vertex3.getX() - vertex2.getX());
                Double y = (vertex2.getY() - vertex1.getY()) * (vertex3.getY() - vertex2.getY());

                double dotProduct = x + y;

                Double angle = Math.acos(dotProduct / (AB.distance() * BC.distance())) * 180 / Math.PI;

                if (angle < 10) {

                  if (best1 == null || (vertex1.distance(best1) + best1.distance(best2)) > (vertex1.distance(vertex2) + vertex2.distance(vertex3))){
                    best1 = vertex2;
                    best2 = vertex3;
                  }

                }
              }
            }
          }
        }
      }

      //Connect best 3 vertices if found
      if (best1 != null) {

        DebugState state = new DebugState();
        Edge AB = new Edge(vertex1, best1);
        Edge BC = new Edge(best1, best2);
        state.addEdge(AB, Color.green);
        state.addEdge(BC, Color.green);
        state.addVertices(vertices);

        state.addVertex(vertex1, Color.RED);
        state.addVertex(best1, Color.RED);
        state.addVertex(best2, Color.RED);

        // Current curves
        List<Edge> debugEdges = new ArrayList<>();
        debugEdges.addAll(curve.getEdges());

        state.addEdges(debugEdges);

        state.setMessage("Connecting " + vertex1.getId() + " " + best1.getId() + " " + best2.getId());

        debug.addState(state);

        curve.addEdge(AB);
        curve.addEdge(BC);

        vertex1.incDegree();
        best1.incDegree();
        best1.incDegree();
        best2.incDegree();

      }
    }

    //Connect vertices with degree of 1 to closest neighbor
    //TODO: Should probably look at points in a certain radius and pick one with best angle instead
    for (Vertex vertex : vertices) {
      if (vertex.getDegree() < 2) {
        if (vertex.getClosest().getDegree() < 1) {
          DebugState state = new DebugState();
          Edge AB = new Edge(vertex, vertex.getClosest());
          state.addEdge(AB, Color.green);
          state.addVertices(vertices);

          state.addVertex(vertex, Color.RED);
          state.addVertex(vertex.getClosest(), Color.RED);

          // Current curves
          List<Edge> debugEdges = new ArrayList<>();
          debugEdges.addAll(curve.getEdges());

          state.addEdges(debugEdges);

          state.setMessage("Connecting free points" + vertex.getId() + " " + vertex.getClosest().getId());

          debug.addState(state);

          curve.addEdge(AB);

          vertex.incDegree();
          vertex.getClosest().incDegree();
        }
      }
    }

    //TODO: Find intersections and insert point

    curves.add(curve);
    return curves;
  }
}


