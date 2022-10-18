package util;

public class Util
{

  public static int bounded (int x, int min, int max) { return Math.max(Math.min(x, max), min); }

}
