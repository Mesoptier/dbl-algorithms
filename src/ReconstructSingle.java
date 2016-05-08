import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReconstructSingle extends Reconstruct {

  public ReconstructSingle(Debug debug) {
    super(debug);
  }

  @Override
  public ProblemOutput start(List<Vertex> vertices) {
    Discur discur = new Discur(vertices);
    return new ProblemOutput(vertices, discur.getCurves());
  }

}
