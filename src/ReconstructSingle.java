import java.util.HashSet;

public class ReconstructSingle extends Reconstruct {

  public ReconstructSingle(Debug debug) {
    super(debug);
  }

  @Override
  public ProblemOutput start(Vertex[] vertices) {
    Segment[] segments = new Segment[0];
    segments = connect(vertices);
    return new ProblemOutput(vertices, segments);
  }

  public Segment[] connect(Vertex[] vertices) {
    int index = 0;
    HashSet<Segment> segments = new HashSet<>();
    while(true) {
      vertices[index].setHasOutgoing(true);
      int newIndex = closestVertex(vertices[index], vertices);
      if (newIndex == -1) {
        break;
      }
      segments.add(new Segment(vertices[index], vertices[newIndex]));

      index = newIndex;
      vertices[index].setHasIncoming(true);
    }
    return segments.toArray(new Segment[segments.size()]);
  }

  public int closestVertex(Vertex vertex, Vertex[] vertices) {
    double smallestDist = Double.MAX_VALUE;
    int index = -1;

    for (int i = 0; i < vertices.length; i++) {
      if (vertex != vertices[i] && !vertices[i].hasIncoming()) {
        double newDistance = vertex.distanceSquared(vertices[i]);
        System.out.println(vertex.toString() + " , " + newDistance);
        if (newDistance < smallestDist) {
          smallestDist = newDistance;
          index = i;
        }
      }
    }

    return index;
  }
}
