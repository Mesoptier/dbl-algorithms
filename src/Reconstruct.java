import java.util.List;

public abstract class Reconstruct {

  protected List<Vertex> vertices;
  protected Debug debug;

  public static Reconstruct fromVariant(String variant) {
    switch (variant) {
      case "single":
        return new ReconstructSingle();
      case "multiple":
        return new ReconstructMultiple();
      case "network":
        return new ReconstructNetwork();
      default:
        throw new IllegalArgumentException("unrecognized reconstruct variant: " + variant);
    }
  }

  public void setVertices(List<Vertex> vertices) {
    this.vertices = vertices;
  }

  void setDebug(Debug debug){
    this.debug = debug;
  }

  public abstract ProblemOutput start();

}
