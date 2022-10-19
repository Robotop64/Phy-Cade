import util.Vector2;

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
  { wall, path, none }

  private enum WallTile
  { line, tunnel, corner, tsection, xsection }

  private final Vector2   origin;
  private final int       tileSize = 10;
  private final Dimension gridSize;

  private Dimension dim;

  //assets.maps the colors to a TileType
  private static final Map <Color, Tile> colorToTiles = Map.of(
      Color.black, Tile.wall,
      Color.blue, Tile.path,
      Color.white, Tile.none
  );
  private static final Map <Tile, Color> tilesToColor = Map.of(
      Tile.wall, Color.black,
      Tile.path, Color.black,
      Tile.none, Color.black
  );


  private final Map <Vector2, Tile> tileMap = new HashMap <>();


  public GameMap () throws IOException
  {
    setBackground(Color.black);
    setLayout(null);
    setBounds(0, 0, Gui.frameWidth, Gui.frameHeight);

    origin = new Vector2().cartesian(0, 0);
    gridSize = new Dimension(this.getWidth() / tileSize, this.getHeight() / tileSize);


    drawMap("./assets/maps/PacMan Map0.bmp", origin);

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
        tileMap.put(new Vector2().cartesian(xPixel, yPixel), colorToTiles.get(color));

      }
    }
  }

  public void drawMap (String path, Vector2 pos) throws IOException
  {
    readBmpMap(path);
    for (int w = 0; w < dim.width; w++)
    {
      for (int h = 0; h < dim.height; h++)
      {
        drawTile(new Vector2().cartesian(pos.getX() + w, pos.getY() + h).multiply(tileSize), tileMap.get(new Vector2().cartesian(w, h)));
      }
    }
    SwingUtilities.invokeLater(() ->
    {
      Gui.getInstance().frame.getContentPane().invalidate();
      Gui.getInstance().frame.getContentPane().repaint();
    });
  }

  private void drawTile (Vector2 pixPos, Tile tile)
  {
    JPanel temp = new JPanel()
    {
      @Override
      protected void paintComponent (Graphics g)
      {
        super.paintComponent(g);
        Graphics2D gg = (Graphics2D) g;
        g.setColor(Color.blue);
        gg.setStroke(new BasicStroke(tileSize / 11));

        Vector2 tilePos = new Vector2().cartesian(pixPos.getX() / tileSize, pixPos.getY() / tileSize);

        int bloat = (int) IntStream.range(0, 4)
                                   .map(n -> 90 * n)
                                   .mapToObj(φ -> new Vector2().polar(1, φ))
                                   .map(tilePos::addScaled)
                                   .map(tileMap::get)
                                   .filter(Tile.wall::equals)
                                   .count();

        for (int i = 0; i < 2; i++)
        {
          if (i == 0)
          {
            g.setColor(Color.blue);
            gg.setStroke(new BasicStroke(tileSize / 4));
          }
          else
          {
            g.setColor(Color.cyan);
            gg.setStroke(new BasicStroke(tileSize / 11));
          }
          for (int φ = 0; φ < 360; φ += 90)
          {
            Tile    thisTile  = tileMap.get(tilePos);
            Vector2 offset    = new Vector2().polar(1, φ);
            Vector2 secPos    = tilePos.addScaled(offset);
            Tile    neighbour = tileMap.get(secPos);

            if (thisTile == Tile.wall && thisTile == neighbour)
            {
              Vector2 testPos  = tilePos.addScaled(offset.multiply(-1));
              Tile    testTile = tileMap.get(testPos);
              if (( testTile == Tile.path && bloat == 3 )) continue;
              Vector2 v = new Vector2().polar(tileSize / 2, φ);
              gg.drawLine(tileSize / 2, tileSize / 2, (int) ( tileSize / 2 + v.getX() ), (int) ( tileSize / 2 + v.getY() ));
            }
          }
        }
      }
    };


    //    temp.setBorder(BorderFactory.createLineBorder(Color.cyan, 2, true));
    temp.setBounds((int) pixPos.getX(), (int) pixPos.getY(), tileSize, tileSize);

    temp.setForeground(tilesToColor.get(tile));

    temp.setBackground(tilesToColor.get(tile));


    add(temp);


  }
}
