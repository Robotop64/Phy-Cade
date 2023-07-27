package kn.uni.util;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

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
  public static double sinDeg (double φ)
  {
    return Math.sin(Math.toRadians(φ));
  }

  public static double sinRad (double φ )
  {
    return Math.sin(φ);
  }

  /**
   * Computes regular cosine
   *
   * @param φ Angle in <b>DEGREES</b>
   * @return cos(φ)
   */
  public static double cosDeg (double φ)
  {
    return Math.cos(Math.toRadians(φ));
  }

  public static double cosRad (double φ )
  {
    return Math.cos(φ);
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
   * @param start    the start tick
   * @param duration the duration
   * @param current  the current tick
   * @return a value between 0 and 1
   */
  public static double progression (long start, long duration, long current)
  {
    return Math.min(Math.max(( current - start ) * 1.0 / ( duration ), 0), 1);
  }

  /**
   * determines if a point is inside a rectangle
   * @param point the point
   * @param rect the rectangle
   * @return true if the point is inside the rectangle
   */
  public static boolean pointInRect (Vector2d point, Rectangle rect)
  {
    return point.x >= rect.x && point.x <= rect.x + rect.width && point.y >= rect.y && point.y <= rect.y + rect.height;
  }

  public static float getMaxFontSize (String text, Dimension dim, Font font)
  {
    JLabel label = new JLabel(text);
    label.setFont(font);
    label.setBounds(0, 0, dim.width, dim.height);

    int    stringWidth = label.getFontMetrics(label.getFont()).stringWidth(text);
    double widthRatio  = (double) dim.width / (double) stringWidth;

    int newFontSize = (int) ( label.getFont().getSize() * widthRatio );

    return Math.min(newFontSize - 1, dim.height) * 1.0f;
  }

  /**
   * Converts a color to a hex string
   * @param color the color
   * @return a hex string
   */
  public static String colorToHex (Color color)
  {
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
  }

  public static double degToRad (double deg)
  {
    return deg * Math.PI / 180;
  }

  public static double radToDeg (double rad)
  {
    return rad * 180 / Math.PI;
  }
}
