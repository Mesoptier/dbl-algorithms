import java.util.ArrayList;
import java.util.List;

public class ReconstructMultiple extends Reconstruct {

  public ReconstructMultiple(Debug debug) {
    super(debug);
  }

  @Override
  public ProblemOutput start(List<Vertex> vertices) {
    List<Edge> edges = new ArrayList<>();
    return new ProblemOutput(vertices, edges);
  }

}
