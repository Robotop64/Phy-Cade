package kn.uni.ui.Swing.components;

import kn.uni.ui.Swing.interfaces.Action;

import javax.swing.JCheckBox;
import java.util.ArrayList;
import java.util.List;

public class PacToggle extends JCheckBox implements Action
{
  List <Runnable> action = new ArrayList <>();

  public PacToggle ()
  {
    super();
  }

  public void addAction (Runnable action)
  {
    this.action.add(action);
  }

  @Override
  public void run ()
  {
    if (!action.isEmpty())
    {
      action.forEach(Runnable::run);
    }
  }
}
