package kn.uni.ui;


import kn.uni.util.Fira;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ImageOutline extends JPanel
{
  private static BufferedImage instance;

  private final        Map <Vector2d, Color> tileMap      = new HashMap <>();
  private final Color outline = Color.red;
  private Dimension dim;

  private ImageOutline (BufferedImage imageIn)
  {
    readBmpMap(imageIn);

    addBorder(3);
  }

  public static BufferedImage getInstance(BufferedImage imageIn)
  {
    if (instance == null)
    {
      instance = new ImageOutline(imageIn).toImage();
    }
    return instance;
  }

  public BufferedImage toImage(){
    BufferedImage out = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
    tileMap.forEach((pos, tile) -> out.setRGB((int) pos.x, (int) pos.y, tileMap.get(pos).getRGB()));
    return out;
  }

  private void readBmpMap (BufferedImage imageIn)
  {
    //save the map dimensions
    dim = new Dimension(imageIn.getWidth(), imageIn.getHeight());

    //    evaluate for in x
    for (int xPixel = 0; xPixel < imageIn.getWidth(); xPixel++)
    {
      //          evaluate for in y
      for (int yPixel = 0; yPixel < imageIn.getHeight(); yPixel++)
      {
        //            color of evaluated pixel
        Color color = new Color(imageIn.getRGB(xPixel, yPixel), true);

        //            create map entry of the evaluated pixel consisting of vector and color
        tileMap.put(new Vector2d().cartesian(xPixel, yPixel), color);

      }
    }
  }

  private void reClassify (Vector2d pixPos, Map <Vector2d, Color> outLineMap)
  {


    for (int φ = 0; φ < 360; φ += 90)
    {
      Vector2d tilePos  = new Vector2d().cartesian(pixPos.getX(), pixPos.getY());
      Color     thisPixel = outLineMap.get(tilePos);

      Vector2d offset    = new Vector2d().polar(1, φ);
      Vector2d secPos    = tilePos.add(offset);
      Color     neighbourPixel = outLineMap.get(secPos);

      if (thisPixel.getAlpha() == 0 && neighbourPixel !=null && (neighbourPixel.getAlpha() != 0 || neighbourPixel == outline))
      {
        tileMap.put(tilePos, outline);

      }
    }
  }

  private void addBorder (int width)
  {
    for (int i = 0; i < width; i++)
    {
      Map <Vector2d, Color> tileMap2 = Map.copyOf(tileMap);
      for (int w = 0; w < dim.width; w++)
      {
        for (int h = 0; h < dim.height; h++)
        {
          reClassify(new Vector2d().cartesian(w, h), tileMap2);
        }
      }
    }
  }


}
