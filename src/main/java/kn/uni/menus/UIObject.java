package kn.uni.menus;

import kn.uni.util.Vector2d;

public class UIObject
{
  boolean  visible;
  Vector2d position;
  Vector2d size;
  int      paintLayer;

  public void move (Vector2d delta)
  {
    position = position.add(delta);
  }
}


