package kn.uni.ui.Swing.components;

import com.formdev.flatlaf.extras.components.FlatComboBox;
import kn.uni.ui.Swing.Style;
import kn.uni.util.Vector2d;

import javax.swing.DefaultListCellRenderer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PacComboBox extends FlatComboBox <String>
{
  public boolean isSelected;

  public boolean isFocused;

  public Runnable onSelectionChange;

  public PacComboBox (Vector2d position, Dimension size)
  {
    super();
    this.setBounds((int) position.x, (int) position.y, size.width, size.height);
    useColorSet(Style.normal);

    addListeners();
  }

  public void useColorSet (Style.ColorSet colorSet)
  {
    this.setOutline(colorSet.border());
    this.setBackground(colorSet.background());
    this.setForeground(colorSet.foreground());
    this.setRenderer(new DefaultListCellRenderer()
    {
      public void paint (Graphics g)
      {
        setBackground(colorSet.background());
        setForeground(colorSet.foreground());
        super.paint(g);
      }
    });
  }

  public void setSelected (boolean selected)
  {
    isSelected = selected;
    Style.ColorSet set = isSelected ? Style.selected : Style.normal;
    useColorSet(set);
  }

  public void setFocused (boolean focused)
  {
    isFocused = focused;
    Style.ColorSet set = isFocused ? Style.focused : Style.normal;
    useColorSet(set);
    if (isFocused)
      this.grabFocus();
  }

  private void addListeners ()
  {
    PacComboBox self = this;
    addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusGained (FocusEvent e)
      {
        useColorSet(Style.focused);
        self.showPopup();
      }

      @Override
      public void focusLost (FocusEvent e)
      {
        useColorSet(Style.normal);
      }
    });

    addActionListener(e ->
    {
      if (onSelectionChange != null) onSelectionChange.run();
    });
  }
}
