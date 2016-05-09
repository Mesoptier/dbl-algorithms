import java.util.ArrayList;
import java.util.List;

public class ReconstructSingle extends Reconstruct {

  public ReconstructSingle(Debug debug) {
    super(debug);
  }

  @Override
  public ProblemOutput start(List<Vertex> vertices) {
    List<Edge> edges = new ArrayList<>();
    return new ProblemOutput(vertices, edges);
  }

}
