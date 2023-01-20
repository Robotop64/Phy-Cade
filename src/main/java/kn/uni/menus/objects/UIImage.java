package kn.uni.menus.objects;

import kn.uni.menus.interfaces.Displayed;
import kn.uni.menus.interfaces.Updating;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.PacPhiConfig;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class UIImage extends UIObject implements Displayed, Updating
{
  public  UILabel       label;
  public  int           borderWidth;
  public  Vector2d      labelPos;
  private String        path;
  private Dimension     imageDim;
  private Dimension     contentDim;
  private Dimension     propedContentDim;
  private Dimension     propedLabelDim;
  private BufferedImage image;
  private int           lineDist;
  private int           borderBuffer;
  private int           innerBuffer;
  private int           contentBuffer;
  private Alignment     alignment;
  private double        proportion;
  private Vector2d      imageBoundsPos;
  private Vector2d      imagePos;

  public UIImage (Vector2d pos, Dimension size, int paintLayer, String path)
  {
    this.position = pos;
    this.size = size;
    this.paintLayer = paintLayer;
    this.path = path;

    borderWidth = 3;
    lineDist = 5;
    borderBuffer = 10;
    contentBuffer = 10;
    proportion = 2 / 3.;
    label = null;
    alignment = Alignment.RIGHT;

    image = TextureEditor.getInstance().loadResource(path);
    setDimensions();
    scaleImage();
    setPositions();
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    if (label != null)
    {
      label.paintComponent(g);
    }

    position.use(g::translate);

    g.setStroke(new BasicStroke(borderWidth));

    if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
    {
      g.setColor(Color.red);
      g.draw(new Rectangle(0, 0, size.width, size.height));
      g.setColor(Color.blue);
      g.draw(new Rectangle(borderBuffer, borderBuffer, contentDim.width, contentDim.height));
      g.setColor(Color.yellow);
      g.draw(new Rectangle((int) imageBoundsPos.x, (int) imageBoundsPos.y, propedContentDim.width, propedContentDim.height));
      g.setColor(Color.green);
      g.draw(new Rectangle((int) labelPos.x, (int) labelPos.y, propedLabelDim.width, propedLabelDim.height));
    }
    g.drawImage(image, (int) imagePos.x, (int) imagePos.y, imageDim.width, imageDim.height, null);


    position.invert().use(g::translate);


  }

  @Override
  public int paintLayer () { return paintLayer; }

  @Override
  public void update ()
  {
  }

  public void setText (String text)
  {
    if (label == null)
    {
      label = new UILabel(labelPos, propedLabelDim, text, paintLayer);
    }
    else
    {
      label.setText(text);
    }
    updateLabel();
  }

  public void setAlignment (Alignment alignment)
  {
    this.alignment = alignment;
    setDimensions();
    scaleImage();
    setPositions();
    updateLabel();
  }

  public void setImageDim (Dimension imageDim)
  {
    this.imageDim = imageDim;
    setDimensions();
    setPositions();
  }

  public void setProportion (double proportion)
  {
    this.proportion = proportion;
    setDimensions();
    scaleImage();
    setPositions();
    updateLabel();
  }

  private void updateLabel ()
  {
    if (label != null)
    {
      label.setSize(propedLabelDim);
      label.position = labelPos;
      label.fitText();
    }
  }

  private void setDimensions ()
  {
    contentDim = new Dimension(size.width - borderBuffer * 2, size.height - borderBuffer * 2);

    if (alignment == Alignment.LEFT || alignment == Alignment.RIGHT)
    {
      propedContentDim = new Dimension((int) ( ( contentDim.width * proportion ) - contentBuffer / 2. ), contentDim.height);
      propedLabelDim = new Dimension((int) ( ( contentDim.width * ( 1 - proportion ) ) - contentBuffer / 2. ), contentDim.height);
    }
    if (alignment == Alignment.TOP || alignment == Alignment.BOTTOM)
    {
      propedContentDim = new Dimension(contentDim.width, (int) ( ( contentDim.height * proportion ) - contentBuffer / 2. ));
      propedLabelDim = new Dimension(contentDim.width, (int) ( ( contentDim.height * ( 1 - proportion ) ) - contentBuffer / 2. ));
    }
    if (alignment == Alignment.CENTER)
    {
      propedContentDim = new Dimension(contentDim.width, contentDim.height);
      propedLabelDim = new Dimension(contentDim.width, contentDim.height);
    }
  }

  private void setPositions ()
  {
    imageBoundsPos = getImgAlignmentPosition(alignment);
    labelPos = getLabelAlignmentPosition(alignment);
    imagePos = imageBoundsPos.add(new Vector2d().cartesian(propedContentDim.width / 2., propedContentDim.height / 2.))
                             .add(new Vector2d().cartesian(-imageDim.width / 2., -imageDim.height / 2.));
  }

  private void scaleImage ()
  {
    if (propedContentDim.width > propedContentDim.height)
    {
      imageDim = new Dimension(propedContentDim.height, propedContentDim.height);
    }
    else
    {
      imageDim = new Dimension(propedContentDim.width, propedContentDim.width);
    }
  }

  private Vector2d getImgAlignmentPosition (Alignment alignment)
  {
    Vector2d topLeft = new Vector2d().cartesian(borderBuffer, borderBuffer);
    Vector2d center  = new Vector2d().cartesian(borderBuffer + contentDim.width / 2., borderBuffer + contentDim.height / 2.);
    Vector2d middle  = topLeft.add(new Vector2d().cartesian(contentDim.width / 2., contentDim.height / 2.));

    return switch (alignment)
        {
          case TOP -> center.add(new Vector2d().cartesian(-propedContentDim.width / 2., -contentDim.height / 2.));
          case BOTTOM -> center.add(new Vector2d().cartesian(-propedContentDim.width / 2., contentDim.height / 2. - propedContentDim.height));
          case LEFT -> center.add(new Vector2d().cartesian(-contentDim.width / 2., -propedContentDim.height / 2.));
          case RIGHT -> center.add(new Vector2d().cartesian(contentDim.width / 2. - propedContentDim.width, -propedContentDim.height / 2.));
          case CENTER -> middle.subtract(new Vector2d().cartesian(propedContentDim.width / 2., propedContentDim.height / 2.));
        };
  }

  private Vector2d getLabelAlignmentPosition (Alignment alignment)
  {
    Vector2d topLeft = position.add(new Vector2d().cartesian(borderBuffer, borderBuffer));
    Vector2d center  = position.add(new Vector2d().cartesian(borderBuffer + contentDim.width / 2., borderBuffer + contentDim.height / 2.));
    Vector2d middle  = topLeft.add(new Vector2d().cartesian(contentDim.width / 2., contentDim.height / 2.));


    return switch (alignment)
        {
          case BOTTOM -> center.add(new Vector2d().cartesian(-propedLabelDim.width / 2., -contentDim.height / 2.));
          case TOP -> center.add(new Vector2d().cartesian(-propedLabelDim.width / 2., contentDim.height / 2. - propedLabelDim.height));
          case RIGHT -> topLeft;//.add(new Vector2d().cartesian(-contentDim.width / 2., -propedLabelDim.height / 2.));
          case LEFT -> center.add(new Vector2d().cartesian(contentDim.width / 2. - propedLabelDim.width, -propedLabelDim.height / 2.));
          case CENTER -> middle.add(new Vector2d().cartesian(-propedLabelDim.width / 2., -propedLabelDim.height / 2.));
        };
  }

  public enum Alignment
  {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    CENTER
  }
}
