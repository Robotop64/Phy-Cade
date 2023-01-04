package kn.uni.menus;

import kn.uni.util.Vector2d;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class UIButton extends UILabel implements Displayed, Updating
{
  public static final ColorSet unselected = new ColorSet(Color.CYAN.darker(), Color.CYAN.darker(), Color.black);
  public static final ColorSet selected   = new ColorSet(Color.YELLOW, Color.YELLOW, Color.black);
  public static final ColorSet hover      = new ColorSet(Color.CYAN, Color.CYAN, Color.black);

  public  boolean        isSelected;
  @SuppressWarnings("FieldMayBeFinal")
  private Set <Runnable> actions = new HashSet <>();

  public UIButton (Vector2d position, Vector2d size, String text, int paintLayer)
  {
    super(position, size, text, paintLayer);
    this.isSelected = false;
  }


  @Override
  public int paintLayer () { return paintLayer; }

  @Override
  public void update ()
  {
    Color color = isSelected ? Color.yellow : Color.cyan.darker();
    borderColor = color;
    textColor = color;
  }

  public void press ()
  {
    actions.forEach(Runnable::run);
  }

  @SuppressWarnings("unused")
  public void removeAction (Runnable a)
  {
    actions.remove(a);
  }

  public void clearActions ()
  {
    actions.clear();
  }

  public void addAction (Runnable a)
  {
    actions.add(a);
  }


}
