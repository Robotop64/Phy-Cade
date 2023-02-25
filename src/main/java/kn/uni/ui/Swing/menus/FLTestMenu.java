package kn.uni.ui.Swing.menus;

import kn.uni.ui.Swing.components.PacButton;
import kn.uni.ui.Swing.components.PacList;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

import static kn.uni.Gui.defaultFrameBounds;

public class FLTestMenu extends UIScreen
{

  public FLTestMenu (JPanel parent)
  {
    super(parent);
    setLayout(null);
    setBounds(defaultFrameBounds);
    setBackground(Color.black);
    setVisible(true);

    addComponents();

  }

  public void addComponents ()
  {
    PacList list = new PacList(new Vector2d().cartesian(20, 20), new Dimension(200, 200));

    PacButton button = new PacButton("FIRST");
    list.addObject(button);

    PacButton button2 = new PacButton("SECOND");
    list.addObject(button2);

    PacButton button3 = new PacButton("THIRD");
    list.addObject(button3);

    list.unifyFontSize(20f);

    add(list);
  }
}
