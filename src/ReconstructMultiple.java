import java.util.ArrayList;
import java.util.List;

public class ReconstructMultiple extends Reconstruct {

  @Override
  public ProblemOutput start() {
    List<Edge> edges = new ArrayList<>();
    return new ProblemOutput(vertices, edges);
  }

}
