package kn.uni.ui.Swing.components;

import javax.swing.JComboBox;
import java.util.ArrayList;
import java.util.List;

public class PacComboBox<T> extends JComboBox<T> implements Runnable
{
  List <Runnable> action = new ArrayList <>();

  public PacComboBox (T[] items)
  {
    super(items);
  }

  public void setAction (Runnable action)
  {
    this.action.add(action);
  }

  @Override
  public void run ()
  {
    if (action != null)
    {
      action.forEach(Runnable::run);
    }
  }
}
