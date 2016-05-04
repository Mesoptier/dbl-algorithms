public abstract class Reconstruct {

  protected Debug debug;

  public Reconstruct(Debug debug) {
    this.debug = debug;
  }

  public static Reconstruct fromVariant(String variant) {
    return fromVariant(variant, null);
  }

  public static Reconstruct fromVariant(String variant, Debug debug) {
    switch (variant) {
      case "single":
        return new ReconstructSingle(debug);
      case "multiple":
        return new ReconstructMultiple(debug);
      case "network":
        return new ReconstructNetwork(debug);
      default:
        throw new IllegalArgumentException("unrecognized reconstruct variant: " + variant);
    }
  }

  public abstract ProblemOutput start(Vertex[] points);

}
