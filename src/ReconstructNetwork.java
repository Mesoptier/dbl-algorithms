import java.io.PrintStream;

public class ReconstructNetwork extends Reconstruct {

  @Override
  public ProblemOutput start(Vertex[] points) {
    Segment[] segments = new Segment[0];
    return new ProblemOutput(points, segments);
  }

}
