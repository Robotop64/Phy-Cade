package kn.uni.ui.Swing.interfaces;

import kn.uni.ui.Swing.Style;

import java.awt.Color;

public interface Colored
{
  default void useColorSet (Style.ColorSet colorSet)
  {
    this.setBackground(colorSet.background());
    this.setForeground(colorSet.foreground());
  }

  void setBackground (Color color);
  void setForeground (Color color);
}
