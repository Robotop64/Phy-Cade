package kn.uni.ui.Swing.renderer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class ColoredListCellRenderer extends DefaultListCellRenderer
{

  private Color selectedColor;
  private Color backgroundColor;
  private Color lastColor;
  private Color textColor;

  public ColoredListCellRenderer (Color backgroundColor, Color selectedColor, Color textColor)
  {
    this.selectedColor = selectedColor;
    this.backgroundColor = backgroundColor;
    this.textColor = textColor;
    lastColor = backgroundColor;
  }

  @Override
  public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
  {
    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    if (isSelected)
    {
      lastColor = selectedColor;
    }
    else
    {
      lastColor = backgroundColor;
    }
    c.setBackground(lastColor);
    return c;
  }

  @Override
  public Color getBackground ()
  {
    return lastColor;
  }

  public void paint (Graphics g)
  {
    setBackground(backgroundColor);
    setForeground(textColor);
    super.paint(g);
  }

}
