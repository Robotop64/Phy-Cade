//package kn.uni.menus.G2D.objects;
//
//import kn.uni.util.Vector2d;
//
//import java.awt.Color;
//import java.awt.Dimension;
//
//public class UIObject
//{
//  public static final ColorSet unselected = new ColorSet(Color.CYAN.darker(), Color.CYAN.darker(), Color.black);
//  public static final ColorSet selected   = new ColorSet(Color.YELLOW, Color.YELLOW, Color.black);
//  public static final ColorSet hover      = new ColorSet(Color.CYAN, Color.CYAN, Color.black);
//
//  public boolean   visible    = true;
//  public boolean   selectable = false;
//  public Vector2d  position;
//  public Dimension size;
//  public int       paintLayer;
//
//  public void move (Vector2d delta)
//  {
//    position = position.add(delta);
//  }
//
//  public UILabel asLabel ()
//  {
//    return (UILabel) this;
//  }
//
//  public UIButton asButton ()
//  {
//    return (UIButton) this;
//  }
//
//  public UITable asTable ()
//  {
//    return (UITable) this;
//  }
//
//  public record ColorSet(Color textColor, Color borderColor, Color backgroundColor) { }
//}
//
//
