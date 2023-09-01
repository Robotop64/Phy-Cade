package kn.uni.ui.Swing.components;

import kn.uni.ui.Swing.interfaces.Action;

import javax.swing.JSpinner;
import java.util.ArrayList;
import java.util.List;

public class PacSpinner extends JSpinner implements Action
{
  List <Runnable> action = new ArrayList <>();

  public PacSpinner ()
  {
    super();
  }

  @Override
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
