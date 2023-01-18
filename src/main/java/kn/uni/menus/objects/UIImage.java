package kn.uni.menus.objects;

import kn.uni.menus.interfaces.Displayed;
import kn.uni.menus.interfaces.Updating;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class UIImage extends UIObject implements Displayed, Updating
{
  UIText.Text label;
  private String        path;
  private Dimension     imageDim;
  private Dimension     contentDim;
  private Dimension     propedImgDim;
  private Dimension     propedLabelDim;
  private BufferedImage image;
  private int           lineDist;
  private int           borderBuffer;
  private Alignment     alignment;
  private double        proportion;
  private Vector2d      labelPos;
  private Vector2d      imagePos;

  public UIImage (Vector2d pos, Dimension size, int paintLayer, Dimension imageDimension, String path)
  {
    this.position = pos;
    this.size = size;
    this.imageDim = imageDimension;
    this.paintLayer = paintLayer;
    this.path = path;

    lineDist = 5;
    borderBuffer = 5;
    proportion = 2 / 3.;
    label = null;
    alignment = Alignment.CENTER;

    //    image = TextureEditor.getInstance().loadTexture("Icon", path);
    setDimensions();
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    position.use(g::translate);
    g.setColor(Color.red);
    g.draw(new Rectangle(0, 0, size.width, size.height));
    g.setColor(Color.blue);
    g.draw(new Rectangle(borderBuffer, borderBuffer, contentDim.width, contentDim.height));
    g.setColor(Color.yellow);
    g.draw(new Rectangle((int) imagePos.x, (int) imagePos.y, propedImgDim.width, propedImgDim.height));
    g.setColor(Color.green);
    g.draw(new Rectangle((int) labelPos.x, (int) labelPos.y, propedLabelDim.width, propedLabelDim.height));


    position.invert().use(g::translate);
  }

  @Override
  public int paintLayer () { return paintLayer; }

  @Override
  public void update ()
  {

  }

  public void setLabel (String text)
  {
    this.label = new UIText.Text(text, lineDist);
  }

  private void setDimensions ()
  {
    contentDim = new Dimension(size.width - borderBuffer * 2, size.height - borderBuffer * 2);

    if (alignment == Alignment.LEFT || alignment == Alignment.RIGHT)
    {
      propedImgDim = new Dimension((int) ( contentDim.width * proportion ), contentDim.height);
      propedLabelDim = new Dimension((int) ( contentDim.width * ( 1 - proportion ) ), contentDim.height);

    }
    if (alignment == Alignment.TOP || alignment == Alignment.BOTTOM)
    {
      propedImgDim = new Dimension(contentDim.width, (int) ( contentDim.height * proportion ));
      propedLabelDim = new Dimension(contentDim.width, (int) ( contentDim.height * ( 1 - proportion ) ));
    }
    if (alignment == Alignment.CENTER)
    {
      propedImgDim = new Dimension(contentDim.width, contentDim.height);
      propedLabelDim = new Dimension(contentDim.width, contentDim.height);
    }
    imagePos = getImgAlignmentPosition(alignment);
    labelPos = getLabelAlignmentPosition(alignment);
  }

  private Vector2d getImgAlignmentPosition (Alignment alignment)
  {
    Vector2d topLeft = new Vector2d().cartesian(borderBuffer, borderBuffer);
    Vector2d center  = new Vector2d().cartesian(borderBuffer + contentDim.width / 2., borderBuffer + contentDim.height / 2.);
    Vector2d middle  = topLeft.add(new Vector2d().cartesian(contentDim.width / 2., contentDim.height / 2.));

    return switch (alignment)
        {
          case TOP -> center.add(new Vector2d().cartesian(-propedImgDim.width / 2., -contentDim.height / 2.));
          case BOTTOM -> center.add(new Vector2d().cartesian(-propedImgDim.width / 2., contentDim.height / 2. - propedImgDim.height));
          case LEFT -> center.add(new Vector2d().cartesian(-contentDim.width / 2., -propedImgDim.height / 2.));
          case RIGHT -> center.add(new Vector2d().cartesian(contentDim.width / 2. - propedImgDim.width, -propedImgDim.height / 2.));
          case CENTER -> middle.subtract(new Vector2d().cartesian(propedImgDim.width / 2., propedImgDim.height / 2.));
        };
  }

  private Vector2d getLabelAlignmentPosition (Alignment alignment)
  {
    Vector2d topLeft = new Vector2d().cartesian(borderBuffer, borderBuffer);
    Vector2d center  = new Vector2d().cartesian(borderBuffer + contentDim.width / 2., borderBuffer + contentDim.height / 2.);
    Vector2d middle  = topLeft.add(new Vector2d().cartesian(contentDim.width / 2., contentDim.height / 2.));

    return switch (alignment)
        {
          case BOTTOM -> center.add(new Vector2d().cartesian(-propedLabelDim.width / 2., -contentDim.height / 2.));
          case TOP -> center.add(new Vector2d().cartesian(-propedLabelDim.width / 2., contentDim.height / 2. - propedLabelDim.height));
          case RIGHT -> center.add(new Vector2d().cartesian(-contentDim.width / 2., -propedLabelDim.height / 2.));
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
