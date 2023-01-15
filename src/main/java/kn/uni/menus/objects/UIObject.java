package kn.uni.menus.objects;

import kn.uni.util.Vector2d;

import java.awt.Color;
import java.awt.Dimension;

public class UIObject
{
  public static final ColorSet normal  = new ColorSet(Color.CYAN.darker(), Color.CYAN.darker(), Color.black);
  public              boolean  visible = true;
  Vector2d  position;
  Dimension size;
  int       paintLayer;

  public void move (Vector2d delta)
  {
    position = position.add(delta);
  }

  public UILabel asLabel ()
  {
    return (UILabel) this;
  }

  public record ColorSet(Color textColor, Color borderColor, Color backgroundColor) { }
}


