import java.io.PrintStream;

public class ReconstructMultiple extends Reconstruct {

  public ReconstructMultiple(Debug debug) {
    super(debug);
  }

  @Override
  public ProblemOutput start(Vertex[] vertices) {
    Segment[] segments = new Segment[0];
    return new ProblemOutput(vertices, segments);
  }

}
