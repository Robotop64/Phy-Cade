package kn.uni.games.classic.pacman.game;

import kn.uni.games.classic.pacman.game.PacmanMapTile.Type;
import kn.uni.games.classic.pacman.game.ghosts.Ghost;
import kn.uni.games.classic.pacman.game.ghosts.RandomWalkAI;
import kn.uni.util.Vector2d;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ClassicPacmanMap extends PlacedObject implements Rendered
{
  private static final String            bmpPath       = "pacman/map/PacManClassic Map.bmp";
  private static final Map <Color, Type> typeFromColor = Map.of(
      Color.black, Type.wall,
      Color.blue, Type.path,
      Color.yellow, Type.coin,
      new Color(255, 88, 0), Type.powerUp,
      Color.cyan, Type.portal,
      Color.red, Type.ghostSpawn,
      Color.green, Type.playerSpawn,
      Color.white, Type.none);

  /**
   * TileSpace -> Tiles
   */
  public Map <Vector2d, PacmanMapTile> tiles = new HashMap <>();
  public int                           tileSize;
  public int                           width;
  public int                           height;
  Vector2d size;
  private ClassicPacmanGameState gameState;


  public ClassicPacmanMap (ClassicPacmanGameState gameState, Vector2d pos, int width, int height)
  {
    this.gameState = gameState;
    this.pos = pos;
    this.width = width;
    this.height = height;

    loadMapData();
    addTilesToMap();

  }

  /**
   * This methode is used to set the items in
   *
   * @param oldTiles
   * @param itemLocations
   */
  public void setItems (Map <Vector2d, PacmanMapTile> oldTiles, Map <PacmanMapTile, ClassicPacmanGameState.Collectables> itemLocations)
  {
    if (itemLocations.isEmpty())
    {
      tiles.forEach((vec, tile) ->
      {
        switch (tile.type)
        {
          case coin -> tile.heldItem = ClassicPacmanGameState.Collectables.coin;
          case powerUp -> tile.heldItem = ClassicPacmanGameState.Collectables.powerUp;
          default -> tile.heldItem = null;
        }
      });
    }
    else
    {
      oldTiles.forEach((vec, tile) ->
      {
        if (tile.heldItem != null && tile.type == tiles.get(vec).type)
        {
          tiles.get(vec).heldItem = tile.heldItem;
        }
      });
    }
  }

  //background
  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    g.setColor(Color.black);
    g.translate((int) pos.rounded().x, (int) pos.rounded().y);
    g.fillRect(0, 0, width, height);
    tiles.forEach((v, tile) -> tile.paintComponent(g, gameState));

    g.setColor(Color.cyan.darker());
    g.drawRect(0, 0, width, height);
  }

  //paintLayer
  @Override
  public int paintLayer ()
  {
    return 0;
  }

  /**
   * Split given position in external and internal position
   *
   * @param pos
   * @return
   */
  public TotalPosition splitPosition (Vector2d pos)
  {
    Vector2d ex = pos.divide(tileSize).floor();
    Vector2d in = pos.subtract(ex.multiply(tileSize)).subtract(new Vector2d().cartesian(1, 1).multiply(tileSize / 2.));
    return new TotalPosition(ex, in);
  }

  /**
   * load Data from bmp
   */
  public void loadMapData ()
  {
    try
    {
      BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(bmpPath)));
      size = new Vector2d().cartesian(image.getWidth(), image.getHeight());
      tileSize = (int) Math.round(Math.min(width / size.x, height / size.y));
      size.stream().forEach(v -> tiles.put(v, new PacmanMapTile(v.multiply(tileSize), tileSize, typeFromColor.get(new Color(image.getRGB((int) v.x, (int) v.y))))));
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Add Tiles to Map
   */
  public void addTilesToMap ()
  {
    this.width = (int) Math.round(size.x * tileSize);
    this.height = (int) Math.round(size.y * tileSize);
    tiles.forEach((position, tile) -> Stream.of(new Vector2d().cartesian(1, 0), new Vector2d().cartesian(1, 1))
                                            .flatMap(v -> IntStream.range(0, 4).map(n -> 90 * n).mapToObj(v::rotate))
                                            .filter(v -> tiles.get(position.add(v)) != null)
                                            .forEach(v -> tile.neighbors.put(v, tiles.get(position.add(v)))));
    tiles.forEach((vec, tile) ->
    {
      if (tile.type == Type.playerSpawn)
      {
        gameState.gameObjects.add(
            new PacmanObject((int) ( this.tileSize * 2. / 3. ), vec.multiply(this.tileSize).add(new Vector2d().cartesian(4, 4))));
      }

      if (tile.type == Type.ghostSpawn)
      {
        gameState.gameObjects.add(
            new Ghost("nowak", tile.pos.copy().add(new Vector2d().cartesian(tileSize / 2., tileSize / 2.)), new RandomWalkAI()));
      }
    });
  }

  /**
   * Returns a map that maps the tiles of the used on map onto the items that those tiles hold
   *
   * @return Map <PacmanMapTile, ClassicPacmanGameState.Collectables>
   */
  public Map <PacmanMapTile, ClassicPacmanGameState.Collectables> getPlacedItems ()
  {
    Map <PacmanMapTile, ClassicPacmanGameState.Collectables> out = new HashMap <>();
    tiles.forEach((vec, tile) ->
        {
          if (tile.heldItem != null)
          {
            out.put(tile, tile.heldItem);
          }
        }
    );
    return out;
  }

  public record TotalPosition(Vector2d ex, Vector2d in) { }


}
