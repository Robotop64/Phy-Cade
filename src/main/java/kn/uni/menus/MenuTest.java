package kn.uni.menus;

import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;

public class MenuTest extends Menu
{

  public MenuTest (JPanel parent)
  {
    super(parent);

    createUIComponents();
  }

  private void createUIComponents ()
  {
    UILabel a = new UILabel(new Vector2d().cartesian(300, 300), new Vector2d().cartesian(300, 300), "100100000\n00\n00\n000\n0\n0000000\n0000\n0000\n000000000\n2002\n3003\n4004\n5005", 100);
    a.editTextRolling(Direction.up, 1);
    a.rollingText = true;
    elements.add(a);
  }
}
