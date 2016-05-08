import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReconstructSingle extends Reconstruct {

  public ReconstructSingle(Debug debug) {
    super(debug);
  }

  @Override
  public ProblemOutput start(List<Vertex> vertices) {
    List<Curve> curves = new ArrayList<Curve>();
    return new ProblemOutput(vertices, curves);
  }

}
