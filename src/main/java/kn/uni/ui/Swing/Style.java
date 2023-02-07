package kn.uni.ui.Swing;

import java.awt.Color;

public class Style
{
  public static final ColorSet focused  = new ColorSet(Color.yellow, Color.black, Color.yellow);
  public static final ColorSet selected = new ColorSet(Color.orange.darker(), Color.black, Color.orange.darker());
  public static final ColorSet normal   = new ColorSet(Color.cyan.darker(), Color.black, Color.cyan.darker());

  public record ColorSet(Color foreground, Color background, Color border) { }
}
