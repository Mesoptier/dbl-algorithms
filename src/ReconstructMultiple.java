import java.util.List;

public class ReconstructMultiple extends Reconstruct {

  @Override
  public ProblemOutput start() {
    Discur discur = new Discur(vertices, debug);
    List<? extends Curve> curves = discur.getCurves();
    return new ProblemOutput(vertices, curves);
  }

}
