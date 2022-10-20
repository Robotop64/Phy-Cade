import util.Vector2;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ImageOutline extends JPanel
{
  private enum Tile
  { none, textur, outline }

  private final        Map <Vector2, Tile> tileMap      = new HashMap <>();
  private static final Map <Tile, Color>   tilesToColor = Map.of(
      Tile.none, Color.green,
      Tile.textur, Color.BLUE,
      Tile.outline, Color.red
  );

  private Dimension dim;

  public ImageOutline (BufferedImage imageIn)
  {


    readBmpMap(imageIn);

    Dimension buffer = new Dimension(dim.width + 4, dim.height + 24);


    addBorder(2);


  }

  private void readBmpMap (BufferedImage imageIn)
  {
    //read bmp file
    BufferedImage image = imageIn;

    //save the map dimensions
    dim = new Dimension(image.getWidth(), image.getHeight());

    //    evaluate for in x
    for (int xPixel = 0; xPixel < image.getWidth(); xPixel++)
    {
      //          evaluate for in y
      for (int yPixel = 0; yPixel < image.getHeight(); yPixel++)
      {
        //            color of evaluated pixel
        Color color = new Color(image.getRGB(xPixel, yPixel), true);

        Tile now;
        if (color.getAlpha() == 0)
        {
          now = Tile.none;
        }
        else
        {
          now = Tile.textur;
        }

        //            create map entry of the evaluated pixel consisting of vector and TileType
        tileMap.put(new Vector2().cartesian(xPixel, yPixel), now);

      }
    }
  }

  private void reClassify (Vector2 pixPos, Map <Vector2, Tile> outLineMap)
  {


    for (int φ = 0; φ < 360; φ += 90)
    {
      Vector2 tilePos  = new Vector2().cartesian(pixPos.getX(), pixPos.getY());
      Tile    thisTile = outLineMap.get(tilePos);

      Vector2 offset    = new Vector2().polar(1, φ);
      Vector2 secPos    = tilePos.addScaled(offset);
      Tile    neighbour = outLineMap.get(secPos);

      if (thisTile == Tile.none && ( neighbour == Tile.textur || neighbour == Tile.outline ))
      {
        tileMap.put(tilePos, Tile.outline);

      }
    }
  }

  private void addBorder (int width)
  {
    for (int i = 0; i < width; i++)
    {
      Map <Vector2, Tile> tileMap2 = Map.copyOf(tileMap);
      for (int w = 0; w < dim.width; w++)
      {
        for (int h = 0; h < dim.height; h++)
        {
          reClassify(new Vector2().cartesian(w, h), tileMap2);
        }
      }
    }
  }


}
