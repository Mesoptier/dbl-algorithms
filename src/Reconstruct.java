public abstract class Reconstruct {

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

  public abstract ProblemOutput start(Point[] points);

}
