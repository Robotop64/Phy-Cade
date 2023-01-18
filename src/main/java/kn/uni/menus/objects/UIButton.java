package kn.uni.menus.objects;

import kn.uni.menus.interfaces.Displayed;
import kn.uni.menus.interfaces.Updating;
import kn.uni.util.Vector2d;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class UIButton extends UILabel implements Displayed, Updating
{


  public  boolean        isSelected;
  @SuppressWarnings("FieldMayBeFinal")
  private Set <Runnable> actions = new HashSet <>();

  public UIButton (Vector2d position, Dimension size, String text, int paintLayer)
  {
    super(position, size, text, paintLayer);
    this.isSelected = false;
    this.selectable = true;
  }


  @Override
  public int paintLayer () { return paintLayer; }

  @Override
  public void update ()
  {
    super.update();
    useColorSet(isSelected ? selected : unselected);
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
