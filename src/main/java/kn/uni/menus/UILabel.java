package kn.uni.menus;

import kn.uni.util.Direction;
import kn.uni.util.Fira;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class UILabel extends UIObject implements Displayed, Updating
{
  public static final ColorSet normal = new ColorSet(Color.CYAN.darker(), Color.CYAN.darker(), Color.black);


  public  boolean   rollingText      = false;
  public  Color     textColor;
  public  Color     backgroundColor;
  public  Color     borderColor;
  public  int       borderWidth;
  public  int       textThickness;
  public  int       textLineSpacing  = 5;
  public  Text      text;
  private Font      font;
  private Vector2d  textpos;
  private Dimension textDim;
  private Dimension textLineDim;
  private Direction rollingDirection = Direction.right;
  private double    rollingSpeed     = 1;

  //TODO add text alignment
  //TODO add text rotation
  //TODO add additional text for nicer slide effect
  public UILabel (Vector2d position, Vector2d size, String text, int paintLayer)
  {
    super();
    this.position = position;
    this.size = size;
    this.text = new Text(Arrays.stream(text.split("\n")).toList(), textLineSpacing);
    this.paintLayer = paintLayer;
    //create a standart label
    font = Fira.getInstance().getLigatures(32f);
    useColorSet(normal);
    borderWidth = 3;
    textThickness = 3;
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    position.rounded().use(g::translate);

    textDim = text.getTextDimension(g, font.getSize());
    textLineDim = text.getLineDimension(g, font.getSize());
    if (textpos == null)
    {
      Vector2d topLeft  = new Vector2d().cartesian(0, textLineDim.height);
      Vector2d middle   = topLeft.add(new Vector2d().cartesian(size.x / 2, size.y / 2));
      Vector2d centered = middle.subtract(new Vector2d().cartesian(textDim.width / 2., textDim.height / 2.));
      textpos = centered;
    }

    //button background
    g.setColor(backgroundColor);
    g.fillRect((int) position.x, (int) position.y, (int) size.x, (int) size.y);
    //button border
    g.setColor(borderColor);
    g.setStroke(new BasicStroke(borderWidth));
    g.drawRect(0, (int) 0, (int) size.x, (int) size.y);
    //define clipping area
    g.setClip(new Rectangle(0, 0, (int) size.x, (int) size.y));
    //button text
    g.setFont(font);
    g.setStroke(new BasicStroke(textThickness));
    g.setColor(textColor);
    text.drawString(g, (int) textpos.x, (int) textpos.y);
    //reset clipping area
    g.setClip(null);

    position.rounded().invert().use(g::translate);

    Rectangle button = new Rectangle((int) position.x, (int) position.y, (int) size.x, (int) size.y);
    Rectangle text   = new Rectangle((int) ( position.x + textpos.x ), (int) ( position.y + textpos.y - textLineDim.height ), textDim.width, textDim.height);
    g.draw(button);
    g.draw(text);

  }

  @Override
  public int paintLayer () { return paintLayer; }

  @Override
  public void update ()
  {
    if (textDim != null)
    {
      if (rollingText && ( ( textDim.width > size.x && rollingDirection.toVector().isHorizontal() ) || ( textDim.height > size.y && rollingDirection.toVector().isVertical() ) ))
      {
        //roll text
        textpos = textpos.add(rollingDirection.toVector().multiply(rollingSpeed));

        //bounds check
        Rectangle button = new Rectangle((int) position.x, (int) position.y, (int) size.x, (int) size.y);
        Rectangle text   = new Rectangle((int) ( position.x + textpos.x ), (int) ( position.y + textpos.y - textLineDim.height ), textDim.width, textDim.height);
        if (!text.intersects(button))
        {
          if (rollingDirection.toVector().isHorizontal())
          {
            int hor = rollingDirection == Direction.right ? -textDim.width : (int) size.x;
            textpos = new Vector2d().cartesian(hor, textpos.y);
          }

          if (rollingDirection.toVector().isVertical())
          {
            int hor = rollingDirection == Direction.up ? ( (int) size.y + 20 ) : 0;
            textpos = new Vector2d().cartesian(textpos.x, hor);
          }
        }
      }
    }


  }

  public void setText (String text) { this.text = new Text(Arrays.stream(text.split("\n")).toList(), textLineSpacing); }

  public void editTextRolling (Direction direction, double speed)
  {
    rollingDirection = direction;
    rollingSpeed = speed;
  }

  public void setFontSize (float size) { setFont(getFont().deriveFont(size)); }

  public Font getFont () { return font; }

  public void setFont (Font font) { this.font = font; }

  public void setColorStyle (Color textColor, Color backgroundColor, Color borderColor, int borderWidth, int textThickness)
  {
    this.textColor = textColor;
    this.backgroundColor = backgroundColor;
    this.borderColor = borderColor;
    this.borderWidth = borderWidth;
    this.textThickness = textThickness;
  }

  public void setTextStyle (Color textColor, int textThickness, int textLineSpacing)
  {
    this.textColor = textColor;
    this.textThickness = textThickness;
    this.textLineSpacing = textLineSpacing;
  }

  public void useColorSet (ColorSet set)
  {
    this.textColor = set.textColor;
    this.backgroundColor = set.backgroundColor;
    this.borderColor = set.borderColor;
  }

  public record ColorSet(Color textColor, Color borderColor, Color backgroundColor) { }

  public record Text(List <String> lines, int lineDist)
  {
    public static String getBiggest (List <String> lines)
    {
      return lines.stream().max(Comparator.comparingInt(String::length)).orElse("");
    }

    public Dimension getLineDimension (Graphics2D g, int fontSize)
    {
      FontRenderContext frc = g.getFontRenderContext();

      // Create a TextLayout object, which represents the text and its formatting
      TextLayout textLayout = new TextLayout(Text.getBiggest(this.lines), Fira.getInstance().getLigatures(fontSize), frc);

      // Determine the dimensions of the text
      int textWidth  = (int) textLayout.getBounds().getWidth();
      int textHeight = (int) textLayout.getBounds().getHeight();

      return new Dimension(textWidth, textHeight);
    }

    public Dimension getTextDimension (Graphics2D g, int fontSize)
    {
      Dimension textDim = getLineDimension(g, fontSize);

      return new Dimension(textDim.width, textDim.height * lines.size() + lineDist * ( lines.size() - 1 ));
    }

    public void drawString (Graphics2D g, int x, int y)
    {
      IntStream.range(0, lines.size()).forEach(i ->
      {
        String line = lines.get(i);
        g.drawString(line, x, y + i * ( getLineDimension(g, g.getFont().getSize()).height + lineDist ));
      });
    }
  }
}
