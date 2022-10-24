package util;

import org.junit.jupiter.api.Test;

public class Vector2DTest
{
  @Test
  public void testRotation ()
  {
    Vector2d v = new Vector2d().cartesian(1, 0).rotate(90);
    System.out.println(v);

    System.out.println(new Vector2d().cartesian(4, 3).rotate(90));
  }

  @Test
  public void testProjection ()
  {
    System.out.println(new Vector2d().cartesian(1, 1).projectOn(new Vector2d().cartesian(1, 0)));
    System.out.println(new Vector2d().cartesian(1, 1).orthogonalTo(new Vector2d().cartesian(1, 0)));
  }

}
