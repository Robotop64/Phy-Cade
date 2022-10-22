package game.map;

import game.ClassicPacmanGameState;
import game.PlacedObject;
import game.Rendered;
import game.Ticking;
import game.map.PacmanMapTile.Type;
import util.Vector2d;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ClassicPacmanMap extends PlacedObject implements Rendered, Ticking
{
  private static final String            bmpPath       = "src/resources/maps/PacManClassic Map.bmp";
  private static final Map <Color, Type> typeFromColor = Map.of(
    Color.black, Type.wall,
    Color.blue, Type.path,
    Color.yellow, Type.coin,
    new Color(255, 88, 0), Type.powerUp,
    Color.cyan, Type.portal,
    Color.red, Type.ghostSpawn,
    Color.green, Type.playerSpawn,
    Color.white, Type.none);

  private int width;
  private int height;
  Map <Vector2d, PacmanMapTile> tiles = new HashMap <>();
  Vector2d                      size;
  int                           tileSize;

  public ClassicPacmanMap (Vector2d pos, int width, int height)
  {
    this.pos = pos;
    this.width = width;
    this.height = height;
    try
    {
      BufferedImage image = ImageIO.read(new File(bmpPath));
      size = new Vector2d().cartesian(image.getWidth(), image.getHeight());
      tileSize = (int)Math.round(Math.min(width / size.x, height / size.y));
      size.stream().forEach(v -> tiles.put(v, new PacmanMapTile(v.multiply(tileSize), tileSize, typeFromColor.get(new Color(image.getRGB((int)v.x, (int)v.y))))));
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    this.width = (int)Math.round(size.x * tileSize);
    this.height = (int)Math.round(size.y * tileSize);
    tiles.forEach((position, tile) -> Stream.of(new Vector2d().cartesian(1, 0), new Vector2d().cartesian(1, 1))
                                            .flatMap(v -> IntStream.range(0, 4).map(n -> 90 * n).mapToObj(v::rotate))
                                            .filter(v -> tiles.get(position.add(v)) != null)
                                            .forEach(v -> tile.neighbors.put(v, tiles.get(position.add(v)))));
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    g.setColor(Color.lightGray);
    g.translate((int)pos.rounded().x, (int)pos.rounded().y);
    g.fillRect(0, 0, width, height);
    tiles.forEach((v, tile) -> tile.paintComponent(g, gameState));
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    
  }
}
