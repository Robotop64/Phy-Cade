package util;

import org.junit.jupiter.api.Test;

public class Vector2Test
{
  @Test
  public void testStuff ()
  {
    System.out.println(Math.cos(Math.toRadians(45)));
    System.out.println(new Vector2(5).polar(100, 45));
    System.out.println(new Vector2(5).polar(100, 45).rotate(90));
  }

}
