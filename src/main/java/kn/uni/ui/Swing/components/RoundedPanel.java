package kn.uni.ui.Swing.components;

import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.interfaces.Colored;

import javax.swing.JPanel;
import java.awt.BasicStroke;
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
    Graphics2D g2D = (Graphics2D) g;

    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2D.setColor(getBackground());
    g2D.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);

    g2D.setStroke(new BasicStroke(borderWidth));
    g2D.setColor(getForeground());
    g2D.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
  }

  @Override
  public void useColorSet (Style.ColorSet colorSet)
  {
    Colored.super.useColorSet(colorSet);
  }
}
