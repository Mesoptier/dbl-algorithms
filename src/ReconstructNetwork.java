import java.util.ArrayList;
import java.util.List;

public class ReconstructNetwork extends Reconstruct {

  @Override
  public ProblemOutput start() {
    List<Curve> curves = new ArrayList<>();
    return new ProblemOutput(vertices, curves);
  }

}
