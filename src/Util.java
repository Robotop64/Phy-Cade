public class Util
{

  public static int bounded (int x, int min, int max) { return Math.max(Math.min(x, max), min); }

  public static int ptToPx (int pt) { return (int) ( pt * 1.333 ); }

  public static int pxToPt (int px) { return (int) ( px / 1.333 ); }
}
