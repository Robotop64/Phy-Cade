package kn.uni.ui.Swing.components;

import kn.uni.ui.Swing.interfaces.Colored;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;

import javax.swing.JPanel;
import java.awt.Dimension;

import static javax.swing.SwingConstants.HORIZONTAL;

public class SettingEditor extends JPanel implements Colored
{
  double columnRatio;
  int    buffer;

  Config.Setting setting;

  public SettingEditor (Vector2d position, Dimension size, double columnRatio, int buffer, Config.Setting setting)
  {
    super();
    setBounds((int) position.x, (int) position.y, (int) size.width, (int) size.height);
    setLayout(null);
    setBackground(null);
    setOpaque(false);

    this.columnRatio = columnRatio;
    this.buffer = buffer;
    this.setting = setting;
    addComponents();
  }

  private void addComponents ()
  {
    //region label
    PacButton label = new PacButton(
        new Vector2d().cartesian(0, 0),
        new Dimension((int) ( getWidth() * columnRatio - buffer / 2 ), getHeight()),
        String.valueOf(setting.id));
    label.setBuffer(3);
    label.setAlignmentX(LEFT_ALIGNMENT);
    label.setHorizontalAlignment(PacLabel.LEFT);
    //TODO: add description popup on click
    //label.addAction(()->{});
    add(label);
    //endregion

    addEditor();
  }

  private void addEditor ()
  {
    switch (Config.Setting.SubClass.valueOf(setting.asSubClass().getSimpleName()))
    {
      case Range ->
      {
        Config.Range range = setting.toRange();
        PacSlider value = new PacSlider(
            new Vector2d().cartesian((int) ( getWidth() * columnRatio ), 0),
            new Dimension((int) ( getWidth() * ( 1 - columnRatio ) - buffer / 2 ), getHeight()),
            HORIZONTAL, (int) range.min, (int) range.max, (int) range.current, (int) range.stepSize);
        add(value);
        value.setAction(() ->
        {
          range.current = value.value;
          System.out.println(range.current);
          //      Config.save();
        });
      }

      case Switch ->
      {
        Config.Switch bool = setting.toSwitch();
        //        PacSwitch value = new PacSwitch(
        //            new Vector2d().cartesian((int) ( getWidth() * columnRatio ), 0),
        //            new Dimension((int) ( getWidth() * ( 1 - columnRatio )- buffer/2 ), getHeight()),
        //            bool.current);
        //        add(value);
      }

      case Digit ->
      {
        Config.Digit digit = setting.toDigit();
      }

      case Value ->
      {
        Config.Value value = setting.toValue();
      }

      case Table ->
      {
        Config.Table table = setting.toTable();
      }

      case Matrix ->
      {

      }
    }
  }

  //  @Override
  //  public void useColorSet (Style.ColorSet colorSet)
  //  {
  //    super.useColorSet(colorSet);
  //  }
}
