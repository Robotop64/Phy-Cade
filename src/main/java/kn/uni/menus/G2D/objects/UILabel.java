package kn.uni.menus.G2D.objects;

import kn.uni.menus.G2D.interfaces.Displayed;
import kn.uni.menus.G2D.interfaces.Updating;
import kn.uni.util.Direction;
import kn.uni.util.Fira;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.PacPhiConfig;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;


public class UILabel extends UIObject implements Displayed, Updating
{
  public  boolean   rollingText;
  public  boolean   loopText;
  public  Color     textColor;
  public  Color     backgroundColor;
  public  Color     borderColor;
  public  int       borderWidth;
  public  int       textThickness;
  public  int       textLineSpacing;
  public  UIText    text;
  public  Dimension padding;
  private Dimension buttonBounds;
  private Alignment alignment        = Alignment.MIDDLE_CENTER;
  private Font      font;
  private Vector2d  startpos;
  private Vector2d  textpos;
  private Vector2d  backupTextpos;
  private Dimension textDim;
  private Dimension textLineDim;
  private Direction rollingDirection = Direction.right;
  private double    rollingSpeed     = 1;
  private Dimension rollingTime;
  private Dimension textBounds;


  //TODO add text rotation
  public UILabel (Vector2d position, Dimension size, String text, int paintLayer)
  {
    super();

    //create a default label
    font = Fira.getInstance().getLigatures(32f);
    useColorSet(unselected);
    rollingText = false;
    loopText = false;
    borderWidth = 3;
    textThickness = 3;
    textLineSpacing = 5;
    padding = new Dimension(10, 10);
    //

    // initialise variables
    this.position = position;
    this.size = size;
    this.text = new UIText(text, textLineSpacing);
    this.paintLayer = paintLayer;
    //


    //define dimensions & bounds
    setDimensions();
    //

    fitText(); // needs to be called after dimensions are set, before alignment is set

    //set alignment and text position
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
    g.fillRect((int) position.x, (int) position.y, size.width, size.height);

    //button border
    g.setColor(borderColor);
    g.setStroke(new BasicStroke(borderWidth));
    g.drawRect(0, 0, size.width, size.height);

    //define clipping area
    g.setClip(new Rectangle(0, 0, size.width, size.height));

    //button text
    g.setFont(font);
    g.setStroke(new BasicStroke(textThickness));
    g.setColor(textColor);
    text.getText().drawString(g, (int) textpos.x, (int) textpos.y);

    //draw backup text
    if (loopText && rollingText)
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
      if (loopText && rollingText)
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
    if (textDim != null && rollingText)
    {
      //text rolling
      Vector2d rollDir = rollingDirection.toVector();
      if (( textDim.width > size.width && rollDir.isHorizontal() ) || ( textDim.height > size.height && rollDir.isVertical() ))
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
    this.text.setText(text, textLineSpacing);
    setDimensions();
    setAlignment(alignment);
  }

  public void fillText ()
  {
    setFontSize((float) buttonBounds.height / text.lines - ( text.lines - 1 ) * textLineSpacing);
  }

  public void fitText ()
  {
    int targetWidth  = buttonBounds.width - 2 * padding.width;
    int targetHeight = buttonBounds.height - 2 * padding.height;
    int startSize    = 1;

    Dimension textDim = text.getText().getTextDimension(startSize);

    while (textDim.width < targetWidth && textDim.height < targetHeight)
    {
      startSize++;
      textDim = text.getText().getTextDimension(startSize);
    }

    setFontSize((float) startSize - 1);
  }

  public void editTextRolling (Direction direction, double speed)
  {
    rollingDirection = direction;
    rollingSpeed = speed;
    double rollStep = rollingDirection.toVector().multiply(rollingSpeed).length();
    rollingTime = new Dimension((int) ( size.width / rollStep ), (int) ( size.height / rollStep ));

    switch (direction)
    {
      case up -> setAlignment(Alignment.MIDDLE_TOP_SHIFTED);
      case down -> setAlignment(Alignment.MIDDLE_BOTTOM_SHIFTED);
      case left -> setAlignment(Alignment.MIDDLE_LEFT_SHIFTED);
      case right -> setAlignment(Alignment.MIDDLE_RIGHT_SHIFTED);
    }

    setBackupTextpos();
    fillText();
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
    this.textColor = set.textColor();
    this.backgroundColor = set.backgroundColor();
    this.borderColor = set.borderColor();
  }

  public void setAlignment (Alignment alignment)
  {
    this.alignment = alignment;
    textpos = getAlignmentPosition(alignment);
    this.startpos = textpos;
    setBackupTextpos();
  }

  public void enableRolling ()
  {
    rollingText = true;
  }

  public void enableLoopedRolling ()
  {
    rollingText = true;
    loopText = true;
  }

  public void setSize (Dimension size)
  {
    this.size = size;
    setDimensions();
    setAlignment(alignment);
  }


  private void setDimensions ()
  {
    textDim = text.getText().getTextDimension(font.getSize());
    textLineDim = text.getText().getLineDimension(font.getSize());
    buttonBounds = new Dimension(size.width, size.height);
    textBounds = new Dimension(textDim.width, textDim.height);
  }

  private Vector2d getAlignmentPosition (Alignment alignment)
  {
    Vector2d topLeft = new Vector2d().cartesian(0, textLineDim.height);
    Vector2d middle  = topLeft.add(new Vector2d().cartesian(size.width / 2., size.height / 2.));
    Vector2d center  = middle.subtract(new Vector2d().cartesian(textDim.width / 2., textDim.height / 2.));

    return switch (alignment)
        {
          case TOP_LEFT -> topLeft;
          case TOP_CENTER -> new Vector2d().cartesian(center.x, textLineDim.height);
          case TOP_RIGHT -> new Vector2d().cartesian(size.width - textDim.width, textLineDim.height);
          case MIDDLE_LEFT -> new Vector2d().cartesian(0, center.y);
          case MIDDLE_CENTER -> center;
          case MIDDLE_RIGHT -> new Vector2d().cartesian(size.width - textDim.width, center.y);
          case BOTTOM_LEFT -> new Vector2d().cartesian(0, size.height + textLineDim.height - textDim.height);
          case BOTTOM_CENTER -> new Vector2d().cartesian(center.x, size.height + textLineDim.height - textDim.height);
          case BOTTOM_RIGHT -> new Vector2d().cartesian(size.width - textDim.width, size.width + textLineDim.height - textDim.height);

          case MIDDLE_LEFT_SHIFTED -> new Vector2d().cartesian(middle.x, center.y);
          case MIDDLE_RIGHT_SHIFTED -> new Vector2d().cartesian(middle.x - textDim.width, center.y);
          case MIDDLE_TOP_SHIFTED -> new Vector2d().cartesian(center.x, middle.y);
          case MIDDLE_BOTTOM_SHIFTED -> new Vector2d().cartesian(center.x, middle.y - textDim.height);
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

    MIDDLE_LEFT_SHIFTED,
    MIDDLE_RIGHT_SHIFTED,
    MIDDLE_TOP_SHIFTED,
    MIDDLE_BOTTOM_SHIFTED
  }


}
