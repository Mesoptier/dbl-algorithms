import java.util.List;

public class ReconstructSingle extends Reconstruct {

  @Override
  public ProblemOutput start() {
    DiscurSingle discursingle = new DiscurSingle(vertices, debug);
    List<? extends Curve> curves = discursingle.getCurves();
    return new ProblemOutput(vertices, curves);
  }

}
