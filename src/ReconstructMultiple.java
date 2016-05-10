import java.util.ArrayList;
import java.util.List;

public class ReconstructMultiple extends Reconstruct {

  @Override
  public ProblemOutput start() {
    List<Curve> curves = new ArrayList<>();
    return new ProblemOutput(vertices, curves);
  }

}
