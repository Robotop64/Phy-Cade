package kn.uni.menus.objects;

import kn.uni.menus.interfaces.Displayed;
import kn.uni.menus.interfaces.Updating;
import kn.uni.util.Vector2d;

import java.awt.*;

public class UIObject
{
  public static final ColorSet  normal           = new ColorSet(Color.CYAN.darker(), Color.CYAN.darker(), Color.black);
  public boolean  visible;
  Vector2d position;
  Dimension size;
  int      paintLayer;

  public void move (Vector2d delta)
  {
    position = position.add(delta);
  }

  public record ColorSet(Color textColor, Color borderColor, Color backgroundColor) { }
}


