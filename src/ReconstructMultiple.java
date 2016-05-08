import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ReconstructMultiple extends Reconstruct {

  public ReconstructMultiple(Debug debug) {
    super(debug);
  }

  @Override
  public ProblemOutput start(List<Vertex> vertices) {
    List<Curve> curves = new ArrayList<Curve>();
    return new ProblemOutput(vertices, curves);
  }

}
