package kn.uni.ui.Swing.components;

import com.formdev.flatlaf.extras.components.FlatLabel;
import kn.uni.ui.Swing.Style;
import kn.uni.util.Vector2d;

import javax.swing.BorderFactory;
import java.awt.Dimension;

public class PacLabel extends FlatLabel
{
  public int    borderWidth = 1;
  public String header      = "";

  public PacLabel (Vector2d position, Dimension size, String text)
  {
    super();
    setBounds((int) position.x, (int) position.y, size.width, size.height);
    setText(text);
    setOpaque(true);
    useColorSet(Style.normal);
  }

  public void useColorSet (Style.ColorSet colorSet)
  {
    setOpaque(true);
    setForeground(colorSet.foreground());
    setBackground(colorSet.background());
    setBorder(BorderFactory.createLineBorder(colorSet.border(), borderWidth));
  }

  public void setHeader (String header)
  {
    this.header = header;
  }

  public void setText (String text)
  {
    if (header == null || header.equals(""))
      super.setText(text);
    else
      super.setText(header + " " + text);
  }
}
