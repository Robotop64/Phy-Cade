package kn.uni.ui;

import kn.uni.Gui;
import kn.uni.util.Vector2d;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageBasicShadow extends JPanel
{
  private enum Tile
  { none, textur, outline }

  private final        Map <Vector2d, Tile> tileMap      = new HashMap <>();
  private static final Map <Tile, Color>    tilesToColor = Map.of(
    Tile.none, Color.green,
    Tile.textur, Color.BLUE,
    Tile.outline, Color.red
  );

  private       Dimension dim;
  private final Vector2d  origin;
  private final int       tileSize;

  public ImageBasicShadow (int width) throws IOException
  {
    setBackground(Color.black);
    setLayout(null);
    setBounds(getX(), getY(), width, Gui.frameHeight);

    readBmpMap("/textures/skins/ghosts/baum/baum.png");

    Dimension buffer = new Dimension(dim.width + 4, dim.height + 24);

    if (width > Gui.frameHeight)
    {
      tileSize = width / buffer.height;
    }
    else
    {
      tileSize = width / buffer.width;
    }

    origin = new Vector2d().cartesian((long)(width / 2) - dim.width / 2 * tileSize, (long)(Gui.frameHeight / 2) - dim.height / 2 * tileSize);


    drawMap();
    drawMap();


    SwingUtilities.invokeLater(() ->
    {
      Gui.getInstance().frame.getContentPane().invalidate();
      Gui.getInstance().frame.getContentPane().repaint();
    });

  }

  public void readBmpMap (String path) throws IOException
  {
    //read bmp file
    BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResource(path)));

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
        tileMap.put(new Vector2d().cartesian(xPixel, yPixel), now);

      }
    }
  }

  public void drawMap () throws IOException
  {
    for (int w = 0; w < dim.width; w++)
    {
      for (int h = 0; h < dim.height; h++)
      {
        drawTile(new Vector2d().cartesian(w, h).multiply(tileSize), tileMap.get(new Vector2d().cartesian(w, h)));
      }
    }
    SwingUtilities.invokeLater(() ->
    {
      Gui.getInstance().frame.getContentPane().invalidate();
      Gui.getInstance().frame.getContentPane().repaint();
    });
  }

  private void drawTile (Vector2d pixPos, Tile tile)
  {
    JPanel temp = new JPanel();
    temp.setBackground(tilesToColor.get(tile));

    for (int φ = 0; φ < 360; φ += 90)
    {
      Vector2d tilePos  = new Vector2d().cartesian(pixPos.getX() / tileSize, pixPos.getY() / tileSize);
      Tile     thisTile = tileMap.get(tilePos);

      Vector2d offset    = new Vector2d().polar(1, φ);
      Vector2d secPos    = tilePos.add(offset);
      Tile     neighbour = tileMap.get(secPos);

      if (thisTile == Tile.none && neighbour == Tile.textur)
      {
        tileMap.put(tilePos, Tile.outline);
        temp.setBackground(tilesToColor.get(Tile.outline));
      }
      if (thisTile == Tile.none && neighbour == Tile.outline)
      {
        temp.setBackground(tilesToColor.get(Tile.outline));
      }
    }


    //    temp.setBorder(BorderFactory.createLineBorder(Color.cyan, 2, true));
    temp.setBounds((int)((int)pixPos.getX() + origin.getX()), (int)((int)pixPos.getY() + origin.getY()), tileSize, tileSize);


    //    temp.setBackground(tilesToColor.get(tile));


    add(temp);
  }
}
