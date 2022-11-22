package kn.uni.util;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;

public class Util
{
  public static final double precision = 1e9;

  public static int bounded (int x, int min, int max) { return Math.max(Math.min(x, max), min); }

  public static Vector2d stringToSize (String text, int fontSize)
  {
    int    stringLength      = text.length();
    double widthPerFontSize  = 20 / 32.;
    double heightPerFontSize = 40 / 32.;

    return new Vector2d().cartesian(fontSize * widthPerFontSize * stringLength, fontSize * heightPerFontSize);
  }

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

  /**
   * Rounds to pre defined precision
   *
   * @param in
   * @return
   */
  public static double round (double in)
  {
    return Math.round(in * precision) / precision;
  }

  /**
   * cutts off after n digits
   *
   * @param in
   * @return
   */
  public static double roundTo (double in, double precision)
  {
    return Math.round(in * ( 1 / precision )) / ( 1 / precision );
  }

  /**
   * gives a value between 0 and 1 for a given starttick and a waiting duration
   *
   * @param start     the start tick
   * @param duration  the duration
   * @param gameState the gamestate
   * @return a value between 0 and 1
   */
  public static double progression (long start, long duration, ClassicPacmanGameState gameState)
  {
    return Math.min(Math.max(( gameState.currentTick - start ) * 1.0 / ( duration ), 0), 1);
  }


}
