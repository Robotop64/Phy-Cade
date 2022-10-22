package util;

public class Util
{

  public static int bounded (int x, int min, int max) {return Math.max(Math.min(x, max), min);}

  public static int ptToPx (int pt) {return (int)(pt * 1.333);}

  public static int pxToPt (int px) {return (int)(px / 1.333);}

  /**
   * Computes regular sine
   *
   * @param φ Angle in <b>DEGREES</b>
   * @return sin(φ)
   */
  public static double sin (double φ)
  {
    return Math.sin(Math.toRadians(φ));
  }

  /**
   * Computes regular cosine
   *
   * @param φ Angle in <b>DEGREES</b>
   * @return cos(φ)
   */
  public static double cos (double φ)
  {
    return Math.cos(Math.toRadians(φ));
  }
}
