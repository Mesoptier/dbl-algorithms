import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ReconstructSingle extends Reconstruct {

  @Override
  public ProblemOutput start(Point[] points) {
    Segment[] segments = new Segment[points.length - 1];
    //draw some lines to show drawing ability (remove later)
    for (int i = 0; i < points.length - 1; i++) {
      segments[i] = new Segment(points[i], points[i+1]);
    }
    return new ProblemOutput(points, segments);
  }
}
