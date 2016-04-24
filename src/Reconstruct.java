public abstract class Reconstruct {

  private Point[] points;

  public void setPoints(Point[] points) {
    this.points = points;
  }

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

}
