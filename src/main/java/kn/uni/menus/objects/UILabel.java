package kn.uni.menus.objects;

import kn.uni.menus.interfaces.Displayed;
import kn.uni.menus.interfaces.Updating;
import kn.uni.util.Direction;
import kn.uni.util.Fira;
import kn.uni.util.PacPhiConfig;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;


public class UILabel extends UIObject implements Displayed, Updating
{
  public static final ColorSet  normal           = new ColorSet(Color.CYAN.darker(), Color.CYAN.darker(), Color.black);
  private final       Dimension buttonBounds;
  public              boolean   rollingText;
  public              boolean   loopText;
  public              Color     textColor;
  public              Color     backgroundColor;
  public              Color     borderColor;
  public              int       borderWidth;
  public              int       textThickness;
  public              int       textLineSpacing;
  public              UIText    text;
  private             Alignment alignment        = Alignment.MIDDLE_CENTER;
  private             Font      font;
  private             Vector2d  startpos;
  private             Vector2d  textpos;
  private             Vector2d  backupTextpos;
  private             Dimension textDim;
  private             Dimension textLineDim;
  private             Direction rollingDirection = Direction.right;
  private             double    rollingSpeed     = 1;
  private             Dimension rollingTime;
  private             Dimension textBounds;


  //TODO add text rotation
  public UILabel (Vector2d position, Vector2d size, String text, int paintLayer)
  {
    super();

    //create a default label
    font = Fira.getInstance().getLigatures(32f);
    useColorSet(normal);
    rollingText = false;
    loopText = false;
    borderWidth = 3;
    textThickness = 3;
    textLineSpacing = 5;
    //

    // initialise variables
    this.position = position;
    this.size = size;
    this.text = new UIText(text, textLineSpacing);
    this.paintLayer = paintLayer;
    //


    //define dimensions & bounds
    //    setDimensions(); //currently not needed, was before, currently not since called in fitText, reenable if fitText is removed from constructor
    buttonBounds = new Dimension((int) size.x, (int) size.y);
    //

    fitText(); // needs to be called after dimensions are set, before alignment is set

    //set alignment and text position
    if (textBounds.width > size.x)
      alignment = Alignment.MIDDLE_LEFT;
    setAlignment(alignment);
    //setBackupTextpos(); //needed for rolling text, if editRolling is not called but looping is enabled; called in fitText, reenable if fitText is removed from constructor
    //
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    //translate to button position
    position.rounded().use(g::translate);

    //draw the contents
    //button background
    g.setColor(backgroundColor);
    g.fillRect((int) position.x, (int) position.y, (int) size.x, (int) size.y);

    //button border
    g.setColor(borderColor);
    g.setStroke(new BasicStroke(borderWidth));
    g.drawRect(0, 0, (int) size.x, (int) size.y);

    //define clipping area
    g.setClip(new Rectangle(0, 0, (int) size.x, (int) size.y));

    //button text
    g.setFont(font);
    g.setStroke(new BasicStroke(textThickness));
    g.setColor(textColor);
    text.getText().drawString(g, (int) textpos.x, (int) textpos.y);

    //draw backup text
    if (loopText)
    {
      //      text.getText().drawString(g, (int) textpos.x, (int) textpos.y);
      text.getText().drawString(g, (int) backupTextpos.x, (int) backupTextpos.y);
      //      System.out.println("backupTextpos = " + backupTextpos);
    }

    //reset clipping area
    g.setClip(null);

    position.rounded().invert().use(g::translate);

    //draw debug stuff
    if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
    {
      g.draw(new Rectangle((int) position.x, (int) position.y, buttonBounds.width, buttonBounds.height));
      g.draw(new Rectangle((int) ( position.x + textpos.x ), (int) ( position.y + textpos.y - textLineDim.height ), textBounds.width, textBounds.height));
      if (loopText)
      {
        g.setColor(Color.red);
        g.draw(new Rectangle((int) ( position.x + backupTextpos.x ), (int) ( position.y + backupTextpos.y - textLineDim.height ), textBounds.width, textBounds.height));
      }
    }
  }

  @Override
  public int paintLayer () { return paintLayer; }


  @Override
  public void update ()
  {
    //prevent null exception
    if (textDim != null)
    {
      //text rolling
      Vector2d rollDir = rollingDirection.toVector();
      if (rollingText && ( ( textDim.width > size.x && rollDir.isHorizontal() ) || ( textDim.height > size.y && rollDir.isVertical() ) ))
      {
        //roll text
        textpos = textpos.add(rollingDirection.toVector().multiply(rollingSpeed));

        //move backup text if enabled
        if (loopText)
          backupTextpos = backupTextpos.add(rollingDirection.toVector().multiply(rollingSpeed));

        //reset text position
        if (loopText && startpos.equals(backupTextpos))
        {
          textpos = startpos;
          setBackupTextpos();
        }
      }

      Rectangle buttonBox = new Rectangle((int) position.x, (int) position.y, buttonBounds.width, buttonBounds.height);
      Rectangle textBox   = new Rectangle((int) ( position.x + textpos.x ), (int) ( position.y + textpos.y - textLineDim.height ), textBounds.width, textBounds.height);

      //only roll once if loop is disabled
      if (!loopText && !textBox.intersects(buttonBox))
      {
        rollingText = false;
      }
    }
  }


  public void setText (String text)
  {
    this.text.setText(text);
    setDimensions();
    setAlignment(alignment);
  }

  public void fitText ()
  {
    setFontSize((float) buttonBounds.height / text.lines - ( text.lines - 1 ) * textLineSpacing);
  }

  public void editTextRolling (Direction direction, double speed)
  {
    rollingDirection = direction;
    rollingSpeed = speed;
    double rollStep = rollingDirection.toVector().multiply(rollingSpeed).length();
    rollingTime = new Dimension((int) ( size.x / rollStep ), (int) ( size.y / rollStep ));

    setBackupTextpos();
  }

  public void setFontSize (float size)
  {
    setFont(getFont().deriveFont(size));
    setDimensions();
    setAlignment(alignment);
    setBackupTextpos();
  }

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

  public void setTextStyle (Color textColor, int textThickness, int textLineSpacing, Alignment alignment)
  {
    this.textColor = textColor;
    this.textThickness = textThickness;
    this.textLineSpacing = textLineSpacing;
    this.alignment = alignment;
  }

  public void useColorSet (ColorSet set)
  {
    this.textColor = set.textColor;
    this.backgroundColor = set.backgroundColor;
    this.borderColor = set.borderColor;
  }

  public void setAlignment (Alignment alignment)
  {
    this.alignment = alignment;
    textpos = getAlignmentPosition(alignment);
    this.startpos = textpos;
    setBackupTextpos();
  }


  private void setDimensions ()
  {
    textDim = text.getText().getTextDimension(font.getSize());
    textLineDim = text.getText().getLineDimension(font.getSize());
    textBounds = new Dimension(textDim.width, textDim.height);
    System.out.println("DIM");
  }

  private Vector2d getAlignmentPosition (Alignment alignment)
  {
    Vector2d topLeft = new Vector2d().cartesian(0, textLineDim.height);
    Vector2d middle  = topLeft.add(new Vector2d().cartesian(size.x / 2, size.y / 2));
    Vector2d center  = middle.subtract(new Vector2d().cartesian(textDim.width / 2., textDim.height / 2.));

    return switch (alignment)
        {
          case TOP_LEFT -> topLeft;
          case TOP_CENTER -> new Vector2d().cartesian(center.x, textLineDim.height);
          case TOP_RIGHT -> new Vector2d().cartesian(size.x - textDim.width, textLineDim.height);
          case MIDDLE_LEFT -> new Vector2d().cartesian(0, center.y);
          case MIDDLE_LEFT_SHIFTED -> new Vector2d().cartesian(middle.x, center.y);
          case MIDDLE_CENTER -> center;
          case MIDDLE_RIGHT -> new Vector2d().cartesian(size.x - textDim.width, center.y);
          case BOTTOM_LEFT -> new Vector2d().cartesian(0, size.y + textLineDim.height - textDim.height);
          case BOTTOM_CENTER -> new Vector2d().cartesian(center.x, size.y + textLineDim.height - textDim.height);
          case BOTTOM_RIGHT -> new Vector2d().cartesian(size.x - textDim.width, size.y + textLineDim.height - textDim.height);
        };
  }

  private void setBackupTextpos ()
  {
    Vector2d rollDir = rollingDirection.opposite().toVector();
    backupTextpos = textpos.add(new Vector2d().cartesian(( textBounds.width + 15 ) * rollDir.x, ( textBounds.height + 15 ) * rollDir.y));
  }

  public enum Alignment
  {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,

    MIDDLE_LEFT,
    MIDDLE_CENTER,
    MIDDLE_RIGHT,

    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT,

    MIDDLE_LEFT_SHIFTED
  }

  public record ColorSet(Color textColor, Color borderColor, Color backgroundColor) { }
}
