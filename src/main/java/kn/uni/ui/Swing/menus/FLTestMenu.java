package kn.uni.ui.Swing.menus;

import kn.uni.ui.Swing.components.PacButton;
import kn.uni.ui.Swing.components.PacComboBox;
import kn.uni.util.Vector2d;

import javax.swing.JLayeredPane;
import java.awt.Color;
import java.awt.Dimension;

import static kn.uni.Gui.defaultFrameBounds;

public class FLTestMenu extends JLayeredPane
{

  public FLTestMenu ()
  {
    setLayout(null);
    setBounds(defaultFrameBounds);
    setBackground(Color.black);

    addComponents();
  }

  public void addComponents ()
  {
    PacComboBox comboBox = new PacComboBox(new Vector2d().cartesian(50, 50), new Dimension(200, 30));
    comboBox.addItem("Item 1");
    comboBox.addItem("Item 2");
    comboBox.addItem("Item 3");
    comboBox.addItem("Item 4");
    comboBox.onSelectionChange = () -> System.out.println("Selected: " + comboBox.getSelectedItem());
    add(comboBox);

    PacButton button = new PacButton(new Vector2d().cartesian(300, 100), new Dimension(100, 30), "1");
    button.addAction(comboBox::showPopup);
    add(button);

    PacButton button2 = new PacButton(new Vector2d().cartesian(400, 100), new Dimension(100, 30), "2");
    button2.addAction(() -> comboBox.setSelectedIndex(2));
    add(button2);

    PacButton button3 = new PacButton(new Vector2d().cartesian(500, 100), new Dimension(100, 30), "3");
    button3.addAction(comboBox::showPopup);
    add(button3);

    PacButton button4 = new PacButton(new Vector2d().cartesian(600, 100), new Dimension(100, 30), "4");
    button4.addAction(comboBox::hidePopup);
    add(button4);
  }
}
