public class Triangle
{

  private Vertex v0;
  private Vertex v1;
  private Vertex v2;

  //center of the circumcirle of the triangle
  private Vertex ccc;
  //radius of the circumcircle
  private double ccr;

  public Triangle(Vertex v0, Vertex v1, Vertex v2)
  {
    this.v0 = v0;
    this.v1 = v1;
    this.v2 = v2;
    computeCircumCenter();
  }

  public Vertex getV0() { return v0; }
  public Vertex getV1()
    {
        return v1;
    }
  public Vertex getV2()
    {
        return v2;
    }

  //computes circumcenter and radius
  public void computeCircumCenter()
  {
    double ax = v0.getX();
    double ay = v0.getY();
    double bx = v1.getX();
    double by = v1.getY();
    double cx = v2.getX();
    double cy = v2.getY();
    double Dval = 2.0 * (ay*cx + by*ax - by*cx - ay*bx - cy*ax + cy*bx);
    double ccx = (by*ax*ax - cy*ax*ax - by*by*ay + cy*cy*ay + bx*bx*cy + ay*ay*by + cx*cx*ay - cy*cy*by - cx*cx*by - bx*bx*ay + by*by*cy - ay*ay*cy)/Dval;
    double ccy = (cx*ax*ax + cx*ay*ay + bx*bx*ax - bx*bx*cx + by*by*ax - by*by*cx - ax*ax*bx - ay*ay*bx - cx*cx*ax + cx*cx*bx - cy*cy*ax + cy*cy*bx)/Dval;
    ccc = new Vertex(ccx, ccy);
    ccr = Math.sqrt(Math.pow(ax - ccx, 2) + Math.pow(ay - ccy,
            2));
  }

  //returns true if vertex p lies in the circumcircle of the triangle
  public boolean inCircumCircle(Vertex p)
  {
    double ccx = ccc.getX();
    double ccy = ccc.getY();
    return Math.sqrt(Math.pow(p.getX() - ccx, 2) + Math.pow(p.getY() - ccy, 2)) <= ccr;
  }

  public Vertex getCircumCenter()
  {
      return ccc;
  }

}
