import static org.junit.Assert.*;
import org.junit.Test;

public class VertexTest {

  private static final double DELTA = 0.0001;

  @Test
  public void verticesWithSameCoordinatesAreEqual() {
    Vertex v1 = new Vertex(4, 2);
    Vertex v2 = new Vertex(4, 2);

    assertEquals(v1, v2);
  }

  @Test
  public void verticesWithSwappedCoordinatesAreNotEqual() {
    Vertex v1 = new Vertex(4, 2);
    Vertex v2 = new Vertex(2, 4);

    assertNotEquals(v1, v2);
  }

  @Test
  public void distanceWorksHorizontal() {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(2, 0);

    assertEquals(2, v1.distance(v2), DELTA);
  }

  @Test
  public void distanceWorksVertical() {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(0, 2);

    assertEquals(2, v1.distance(v2), DELTA);
  }

  @Test
  public void distanceWorksDiagonal1() {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(2, 2);

    assertEquals(2.82842712, v1.distance(v2), DELTA);
  }

  @Test
  public void distanceWorksDiagonal2() {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(1, 2);

    assertEquals(2.23606797, v1.distance(v2), DELTA);
  }

  @Test
  public void distanceEqualsSquareRootOfDistanceSquared() {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(2, 2);

    assertEquals(Math.sqrt(v1.distanceSquared(v2)), v1.distance(v2), DELTA);
  }

  @Test
  public void calcAngle90() {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(2, 0);
    Vertex v3 = new Vertex(2, 2);

    assertEquals(90, Vertex.calcAngle(v1, v2, v3), DELTA);
  }

  @Test
  public void calcAngle180() {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(2, 0);
    Vertex v3 = new Vertex(4, 0);

    assertEquals(180, Vertex.calcAngle(v1, v2, v3), DELTA);
  }

  @Test
  public void calcAngle135() {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(2, 0);
    Vertex v3 = new Vertex(4, 2);

    assertEquals(135, Vertex.calcAngle(v1, v2, v3), DELTA);
  }

  @Test
  public void calcAngle45() {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(2, 0);
    Vertex v3 = new Vertex(0, 2);

    assertEquals(45, Vertex.calcAngle(v1, v2, v3), DELTA);
  }

  @Test
  public void calcAngle0() {
    Vertex v1 = new Vertex(0, 0);
    Vertex v2 = new Vertex(2, 0);
    Vertex v3 = new Vertex(0, 0);

    assertEquals(0, Vertex.calcAngle(v1, v2, v3), DELTA);
  }

}
