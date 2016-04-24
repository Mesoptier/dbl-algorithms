import java.io.PrintStream;

public class ReconstructMultiple extends Reconstruct {

  @Override
  public ProblemOutput start(Point[] points) {
    Segment[] segments = new Segment[0];
    return new ProblemOutput(points, segments);
  }

}
