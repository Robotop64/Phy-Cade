package kn.uni.ui.Swing.components;

import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.interfaces.Colored;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RoundedPanel extends JPanel implements Colored
{
  public int arc = 0;
  public int borderWidth = 0;

  public RoundedPanel ()
  {
    super();
  }

  public RoundedPanel (int arc)
  {
    super();
    this.arc = arc;
  }

  public void setArc (int arc)
  {
    this.arc = arc;
  }

  public void setBorderWidth (int borderWidth)
  {
    this.borderWidth = borderWidth;
  }

  public int getArc ()
  {
    return arc;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension arcs = new Dimension(arc, arc);
    int width = getWidth();
    int        height   = getHeight();
    Graphics2D graphics = (Graphics2D) g;
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    //Draws the rounded panel with borders
    graphics.setColor(getForeground());
    graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
    graphics.setColor(getBackground());
    graphics.fillRoundRect(borderWidth, borderWidth, width-2*borderWidth-2, height-2*borderWidth-2, arcs.width, arcs.height); //paint border
  }

  @Override
  public void useColorSet (Style.ColorSet colorSet)
  {
    Colored.super.useColorSet(colorSet);
  }
}
