package util;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

public class Util
{
  public static final double precision = 1e9;

  public static int bounded (int x, int min, int max) { return Math.max(Math.min(x, max), min); }

  public static int ptToPx (int pt) { return (int) ( pt * 1.333 ); }

  public static int pxToPt (int px) { return (int) ( px / 1.333 ); }

  public static Vector2d stringToSize (String text, int fontSize)
  {
    int    stringLength      = text.length();
    double widthPerFontSize  = 20 / 32.;
    double heightPerFontSize = 40 / 32.;

    return new Vector2d().cartesian(fontSize * widthPerFontSize * stringLength, fontSize * heightPerFontSize);
  }

  public static Font sizeToFont (String text, Vector2d size)
  {
    int fontSize = (int) ( size.x / text.length() * 32 / 20 );

    return fira(fontSize, Font.PLAIN);
  }

  public static Font fira (int size, int style)
  {
    Font font       = new Font("Fira Code", style, size);
    Map  attributes = font.getAttributes();
    attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
    font = font.deriveFont(attributes);
    return font;
  }

  public static Font firaUnderlined (int size, int style)
  {
    Font font       = new Font("Fira Code", style, size);
    Map  attributes = font.getAttributes();
    attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
    attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    font = font.deriveFont(attributes);
    return font;
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

  public static double round (double in)
  {
    return Math.round(in * precision) / precision;
  }

}
