package kn.uni.ui.Swing.components;

import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.interfaces.Action;
import kn.uni.ui.Swing.interfaces.Colored;

import javax.swing.JSlider;
import java.util.ArrayList;
import java.util.List;

public class PacSlider extends JSlider implements Colored, Action
{
  List <Runnable> action = new ArrayList <>();

  public PacSlider (int orientation, int min, int max, int value, int tick)
  {
    super(orientation, min, max, value);
    setBackground(null);
    setSnapToTicks(true);
    setMajorTickSpacing(max / 10);
    setMinorTickSpacing(tick);
    setPaintTicks(true);
    setPaintLabels(true);
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

  public void useColorSet (Style.ColorSet style)
  {
    //TODO
    putClientProperty("trackColor", style.foreground());
    putClientProperty("thumbColor", style.foreground());
  }

}
