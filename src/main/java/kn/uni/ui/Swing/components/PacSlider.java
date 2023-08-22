package kn.uni.ui.Swing.components;

import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.interfaces.Colored;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.Dimension;

public class PacSlider extends JPanel implements Colored
{
  int orientation;
  double          min;
  double          max;
  double          value;
  JSlider        slider;

  public PacSlider (Vector2d position, Dimension size, int orientation, int min, int max, int value, int tick)
  {
    setBounds((int) position.x, (int) position.y, (int) size.width, (int) size.height);
    setLayout(null);
    setBackground(null);
    slider = new JSlider(orientation, min, max, value);
    slider.setBounds(0, 0, (int) size.width, (int) size.height);
    slider.setSnapToTicks(true);
    slider.setMajorTickSpacing(max/10);
    slider.setMinorTickSpacing(tick);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    this.orientation = orientation;
    this.min = min;
    this.max = max;
    this.value = value;
    add(slider);
  }

  public void setAction(Runnable action)
  {
    slider.addChangeListener(e -> {
      value = slider.getValue();
      action.run();
    });
  }

  public void useColorSet(Style.ColorSet style)
  {
    //TODO
    slider.putClientProperty("trackColor", style.foreground());
    slider.putClientProperty("thumbColor", style.foreground());
  }

}
