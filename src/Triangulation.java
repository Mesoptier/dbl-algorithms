import java.util.ArrayList;
import java.util.List;

public class Triangulation {

  private List<Vertex> vertices;
  private List<Triangle> triangles;

  //supertriangle
  private Vertex v0;
  private Vertex v1;
  private Vertex v2;

  //list for dalaunay edges
  private ArrayList<Edge> edgeslist;

  public Triangulation(List<Vertex> vertices) {
    this.vertices = vertices;
    this.triangles = new ArrayList<>();
  }

  public void triangulate() {
    if (vertices.size() < 3)
      return;
    addSuperTriangle();
    List<Edge> edges;
    //loop through all vertices
    for (int i = 0; i < vertices.size(); i++) {
      Vertex vertex = vertices.get(i);
      int j = 0;
      edges = new ArrayList<>();
      while (j < triangles.size()) {

        //checking if current vertex v is in the circumcircle of a triangle t
        Triangle triangle = triangles.get(j);
        if (triangle.inCircumCircle(vertex)) {
          //if v is in the cc of the triangle t, t gets removed and new edges are added
          edges.add(new Edge(triangle.getV0(), triangle.getV1()));
          edges.add(new Edge(triangle.getV1(), triangle.getV2()));
          edges.add(new Edge(triangle.getV2(), triangle.getV0()));
          triangles.remove(j);
        } else {
          j++;
        }
      }

      //remove double edges
      for (j = 0; j < edges.size() - 1; j++) {
        Edge e1 = edges.get(j);
        if (e1.getHead() != null && e1.getTail() != null) {
          for (int k = j + 1; k < edges.size(); k++) {
            Edge e2 = edges.get(k);
            if (e2.getHead() != null && e2.getTail() != null) {
              if (e1.getHead() == e2.getTail() && e1.getTail() == e2.getHead()) {
                e1.nullify();
                e2.nullify();
              }
            }
          }
        }
      }

      //add triangles
      for (j = 0; j < edges.size(); j++) {
        Edge e = (Edge) edges.get(j);
        if (e.getHead() != null && e.getTail() != null) {
          Triangle t = new Triangle(e.getHead(), e.getTail(), vertex);
          triangles.add(t);
        }
      }
    }
    removeSuperTriangle();
    //add all delaunay edges to edgeslist
    makeEdges();
  }

  //set up supertriangle
  private void addSuperTriangle() {
    v0 = new Vertex(0, -16384);
    v1 = new Vertex(-32768, 16384);
    v2 = new Vertex(32768, 16384);
    Triangle st = new Triangle(v0, v1, v2);
    triangles.add(st);
  }

  //remove supertriangle
  private void removeSuperTriangle() {
    int i = 0;
    while (i < triangles.size()) {
      Triangle triangle = triangles.get(i);
      if (triangle.getV0() == v0 || triangle.getV0() == v1 || triangle.getV0() == v2 ||
          triangle.getV1() == v0 || triangle.getV1() == v1 || triangle.getV1() == v2 ||
          triangle.getV2() == v0 || triangle.getV2() == v1 || triangle.getV2() == v2) {
        triangles.remove(i);
      } else
        i++;
    }
  }

  //making the list of delaunay edges
  public void makeEdges() {
    edgeslist = new ArrayList<Edge>();
    for (int i = 0; i < triangles.size(); i++) {
      Triangle t = (Triangle) triangles.get(i);
      Edge e1, e2, e3;
      e1 = new Edge(t.getV0(), t.getV1());
      e2 = new Edge(t.getV1(), t.getV2());
      e3 = new Edge(t.getV0(), t.getV2());
      for (int j = 0; j < edgeslist.size(); j++) {
        Edge edge = edgeslist.get(j);
        if (e1 != null && ((edge.getHead() == e1.getHead() && edge.getTail() == e1.getTail()) || (edge.getHead() == e1.getTail() && edge.getTail() == e1.getHead()))) {
          e1 = null;
        }
        if (e2 != null && ((edge.getHead() == e2.getHead() && edge.getTail() == e2.getTail()) || (edge.getHead() == e2.getTail() && edge.getTail() == e2.getHead()))) {
          e2 = null;
        }
        if (e3 != null && ((edge.getHead() == e3.getHead() && edge.getTail() == e3.getTail()) || (edge.getHead() == e3.getTail() && edge.getTail() == e3.getHead()))) {
          e3 = null;
        }
      }
      if (e1 != null) edgeslist.add(e1);
      if (e2 != null) edgeslist.add(e2);
      if (e3 != null) edgeslist.add(e3);
    }
  }

  public ArrayList<Edge> getEdges() {
    return edgeslist;
  }
}
