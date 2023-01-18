package kn.uni.menus.objects;

import kn.uni.menus.engine.Projector;
import kn.uni.util.Fira;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class UIText
{
  public  int    lines;
  public  int    lineDist;
  private String initialText;
  private Text   formatted;

  public UIText (String text, int lineSpacing)
  {
    initialText = text;
    formatted = new Text(text, lineSpacing);
    lines = formatted.lines.size();
  }

  public Text getText ()
  {
    return formatted;
  }

  public void setText (String text, int lineSpacing)
  {
    this.initialText = text;
    formatted = new Text(Arrays.stream(text.split("\n")).toList(), lineSpacing);
  }

  public Graphics2D render (Graphics2D g)
  {
    //TODO render text

    return g;
  }


  public record Text(List <String> lines, int lineDist)
  {
    public Text (String text, int lineDist)
    {
      this(Arrays.stream(text.split("\n")).toList(), lineDist);
    }

    public String getBiggest ()
    {
      return this.lines.stream().max(Comparator.comparingInt(String::length)).orElse("");
    }

    public Dimension getLineDimension (int fontSize)
    {
      FontRenderContext frc = Projector.scene.getFontRenderContext();

      // Create a TextLayout object, which represents the text and its formatting
      TextLayout textLayout = new TextLayout(getBiggest(), Fira.getInstance().getLigatures(fontSize), frc);

      // Determine the dimensions of the text
      int textWidth  = (int) textLayout.getBounds().getWidth() + (int) ( 0.2 * fontSize );
      int textHeight = (int) textLayout.getBounds().getHeight();

      return new Dimension(textWidth, textHeight);
    }

    public Dimension getTextDimension (int fontSize)
    {
      Dimension textDim = getLineDimension(fontSize);

      return new Dimension(textDim.width, textDim.height * lines.size() + lineDist * ( lines.size() - 1 ));
    }

    public void drawString (Graphics2D g, int x, int y)
    {
      IntStream.range(0, lines.size()).forEach(i ->
      {
        String line = lines.get(i);
        g.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.drawString(line, x, y + i * ( getLineDimension(g.getFont().getSize()).height + lineDist ));
      });
    }

    public boolean equals (String other)
    {
      return lines.stream().allMatch(line -> line.equals(other));
    }
  }
}
