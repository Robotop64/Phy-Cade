package util;

import org.junit.jupiter.api.Test;

public class Vector2dTest
{
  @Test
  public void testRotation ()
  {
    Vector2d v = new Vector2d().cartesian(1, 0).rotate(90);
    System.out.println(v);

    System.out.println(new Vector2d().cartesian(4, 3).rotate(90));
  }

}
