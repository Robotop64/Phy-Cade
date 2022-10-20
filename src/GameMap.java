import util.Vector2d;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class GameMap extends JPanel
{
  private enum Tile
  { wall, path, none, coin, powerUp, gSpawn, pSpawn, portal }

  private enum WallTile
  { line, tunnel, corner, tsection, xsection }

  private final Vector2d origin;
  private final int      tileSize;

  private Dimension dim;

  //assets.maps the colors to a TileType
  private static final Map <Color, Tile> colorToTiles = Map.of(Color.black, Tile.wall, Color.blue, Tile.path, Color.yellow, Tile.coin, new Color(255, 88, 0), Tile.powerUp, Color.cyan, Tile.portal, Color.red, Tile.gSpawn, Color.green, Tile.pSpawn, Color.white, Tile.none);
  private static final Map <Tile, Color> tilesToColor = Map.of(Tile.wall, Color.black, Tile.path, Color.black, Tile.coin, Color.yellow, Tile.powerUp, Color.orange, Tile.portal, Color.cyan, Tile.gSpawn, Color.red, Tile.pSpawn, Color.green, Tile.none, Color.black);

  private final Map <Vector2d, Tile> tileMap = new HashMap <>();

  public GameMap (int width, int height) throws IOException
  {
    setBackground(Color.black);
    setLayout(null);

    readBmpMap("./assets/maps/PacManClassic Map.bmp");

    double ratio     = (double)dim.height / dim.width;
    double newHeight = (double)(ratio * height);
    double newWidth  = (double)(width * ratio);
    double min       = Math.min(newHeight, newWidth);

    setBounds(0, 0, 900, 900);

    tileSize = 25;


    origin = new Vector2d().cartesian((long)(width / 2) - dim.width / 2 * tileSize, (long)(Gui.frameHeight / 2) - dim.height / 2 * tileSize);


    drawMap();


    SwingUtilities.invokeLater(() ->
    {
      Gui.getInstance().frame.getContentPane().invalidate();
      Gui.getInstance().frame.getContentPane().repaint();
    });
  }


  /**
   * This method is used to read a .bmp file and create
   *
   * @param path where the image is located
   * @throws IOException if no image is found
   */
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
        Color color = new Color(image.getRGB(xPixel, yPixel));

        //            create map entry of the evaluated pixel consisting of vector and TileType
        tileMap.put(new Vector2d().cartesian(xPixel, yPixel), colorToTiles.get(color));

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
    JPanel temp = new JPanel()
    {
      @Override
      protected void paintComponent (Graphics g)
      {
        super.paintComponent(g);
        Graphics2D gg      = (Graphics2D)g;
        Vector2d   tilePos = new Vector2d().cartesian(pixPos.getX() / tileSize, pixPos.getY() / tileSize);

        int bloat = (int)IntStream.range(0, 4).map(n -> 90 * n).mapToObj(φ -> new Vector2d().polar(1, φ)).map(tilePos::add).map(tileMap::get).filter(tile -> Tile.wall == tile || tile == null).count();

        for (int i = 0; i < 2; i++)
        {
          Tile thisTile = tileMap.get(tilePos);

          if (thisTile == Tile.coin)
          {
            g.setColor(Color.yellow);
            gg.setStroke(new BasicStroke((float)(tileSize / 11.0)));
            gg.fillOval(tileSize / 3, tileSize / 3, tileSize / 3, tileSize / 2);
          }
          if (thisTile == Tile.powerUp)
          {
            g.setColor(Color.red);
            gg.setStroke(new BasicStroke((float)(tileSize / 11.0)));
            gg.fillOval(tileSize / 3, tileSize / 3, tileSize / 2, tileSize / 2);
          }

          if (thisTile == Tile.wall)
          {
            if (i == 0)
            //color ground line
            {
              g.setColor(Color.blue);
              gg.setStroke(new BasicStroke((float)(tileSize / 4.0)));
            }
            else
            //color topLine
            {
              g.setColor(Color.cyan);
              gg.setStroke(new BasicStroke((float)(tileSize / 11.0)));
            }
          }


          for (int φ = 0; φ < 360; φ += 90)
          {
            if (thisTile == Tile.wall)
            {
              //diagonal neighbours
              if (bloat == 4)
              {
                {
                  Vector2d eX         = new Vector2d().cartesian(1, 0).rotate(φ);
                  Vector2d eY         = new Vector2d().cartesian(0, 1).rotate(φ);
                  Vector2d offsetDiag = eX.add(eY);
                  Vector2d secPos     = tilePos.add(offsetDiag);
                  Tile     neighbour  = tileMap.get(secPos);
                  if (neighbour == Tile.coin || neighbour == Tile.path || neighbour == Tile.powerUp)
                  {
                    gg.drawLine(tileSize / 2, tileSize / 2, (int)(tileSize / 2 + eX.getX() * tileSize / 2), (int)(tileSize / 2 + eX.getY() * tileSize / 2));
                    gg.drawLine(tileSize / 2, tileSize / 2, (int)(tileSize / 2 + eY.getX() * tileSize / 2), (int)(tileSize / 2 + eY.getY() * tileSize / 2));
                  }
                }
              }
              //direct neighbours
              {
                Vector2d offset    = new Vector2d().polar(1, φ);
                Vector2d secPos    = tilePos.add(offset);
                Tile     neighbour = tileMap.get(secPos);

                if (thisTile == neighbour)
                {
                  Vector2d testPos  = tilePos.add(offset.multiply(-1));
                  Tile     testTile = tileMap.get(testPos);
                  if (((testTile != Tile.wall && testTile != Tile.none && testTile != null) && bloat == 3 || bloat == 4)) continue;

                  Vector2d v = new Vector2d().polar(tileSize / 2.0, φ);
                  gg.drawLine(tileSize / 2, tileSize / 2, (int)(tileSize / 2 + v.getX()), (int)(tileSize / 2 + v.getY()));
                }
              }
            }
          }
        }
      }
    };


    //    temp.setBorder(BorderFactory.createLineBorder(Color.cyan, 2, true));
    temp.setBounds((int)((int)pixPos.getX() + origin.getX()), (int)((int)pixPos.getY() + origin.getY()), tileSize, tileSize);

    temp.setBackground(Color.black);
    //    temp.setBackground(tilesToColor.get(tile));


    add(temp);
  }


}
