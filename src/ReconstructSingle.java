import java.util.HashSet;

public class ReconstructSingle extends Reconstruct {

  public ReconstructSingle(Debug debug) {
    super(debug);
  }

  @Override
  public ProblemOutput start(Vertex[] points) {
    Segment[] segments = new Segment[0];
    segments = connect(points);
    return new ProblemOutput(points, segments);
  }

  public Segment[] connect(Vertex[] points) {
    int index = 0;
    HashSet<Segment> segments = new HashSet<>();
    while(true) {
      points[index].setHasOutgoing(true);
      int newIndex = closestPoint(points[index], points);
      if (newIndex == -1) {
        break;
      }
      segments.add(new Segment(points[index], points[newIndex]));

      index = newIndex;
      points[index].setHasIncoming(true);
    }
    return segments.toArray(new Segment[segments.size()]);
  }

  public int closestPoint(Vertex point, Vertex[] points) {
    double smallestDist = Double.MAX_VALUE;
    int index = -1;

    for (int i = 0; i < points.length; i++) {
      if (point != points[i] && !points[i].hasIncoming()) {
        double newDistance = point.distanceSquared(points[i]);
        System.out.println(point.toString() + " , " + newDistance);
        if (newDistance < smallestDist) {
          smallestDist = newDistance;
          index = i;
        }
      }
    }

    return index;
  }
}
