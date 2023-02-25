package kn.uni.games.classic.pacman.game.objects;

import kn.uni.games.classic.pacman.game.entities.GhostEntity;
import kn.uni.games.classic.pacman.game.entities.PacmanEntity;
import kn.uni.games.classic.pacman.game.entities.ghostAi.AggressiveAI;
import kn.uni.games.classic.pacman.game.entities.ghostAi.ConfusedAI;
import kn.uni.games.classic.pacman.game.entities.ghostAi.GhostAI;
import kn.uni.games.classic.pacman.game.entities.ghostAi.ShyAI;
import kn.uni.games.classic.pacman.game.entities.ghostAi.SneakyAI;
import kn.uni.games.classic.pacman.game.graphics.Rendered;
import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameConstants;
import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.items.ItemPH;
import kn.uni.games.classic.pacman.game.objects.PacManMapTile.Type;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PacManMap extends PlacedObject implements Rendered
{
  //constants
  private static final String                        bmpPath       = "pacman/map/PacManClassic Map.bmp";
  private static final Map <Color, Type>             typeFromColor = Map.of(
      Color.black, Type.wall,
      Color.blue, Type.path,
      Color.yellow, Type.coin,
      new Color(255, 88, 0), Type.powerUp,
      Color.cyan, Type.portal,
      Color.red, Type.ghostSpawn,
      Color.green, Type.playerSpawn,
      Color.pink, Type.ghostExit,
      new Color(255, 0, 255), Type.door,
      Color.white, Type.none);
  //general variables
  private final        ClassicPacmanGameState        gameState;
  /**
   * TileSpace -> Tiles
   */
  public               Map <Vector2d, PacManMapTile> tiles         = new HashMap <>();
  public               int                           tileSize;
  public               int                           width;
  public               int                           height;
  public               Vector2d                      size;


  public PacManMap (ClassicPacmanGameState gameState, Vector2d pos, int width, int height)
  {
    //inherited
    this.pos = pos;
    //general variables
    this.gameState = gameState;
    this.width = width;
    this.height = height;
    //initialisation
    loadMapData();
    addTilesToMap();
    addObjects(gameState);
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    //outline of the map
    g.setColor(Color.black);
    g.translate((int) pos.rounded().x, (int) pos.rounded().y);
    g.fillRect(0, 0, width, height);
    tiles.forEach((v, tile) -> tile.paintComponent(g, gameState));

    g.setColor(Color.cyan.darker());
    g.drawRect(0, 0, width, height);
  }

  @Override
  public int paintLayer ()
  {
    return 0;
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
      size.stream().forEach(v -> tiles.put(v, new PacManMapTile(v.multiply(tileSize), tileSize, typeFromColor.get(new Color(image.getRGB((int) v.x, (int) v.y))))));
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
  }

  /**
   * Add "static" Objects to Map
   *
   * @param gameState GameState
   */
  public void addObjects (ClassicPacmanGameState gameState)
  {
    //search each tile
    tiles.forEach((vec, tile) ->
    {
      if (tile.type == Type.portal)
      {
        gameState.gameObjects.add(
            new TeleporterObject(this, tile.center, false, Direction.down));
      }

    });
    //pair the portals
    gameState.gameObjects.stream()
                         .filter(o -> o instanceof TeleporterObject)
                         .map(o -> (TeleporterObject) o)
                         .filter(o -> o.pair == null)
                         .forEach(o -> o.pair(Objects.requireNonNull(gameState.gameObjects.stream()
                                                                                          .filter(o2 -> o2 instanceof TeleporterObject)
                                                                                          .map(o2 -> (TeleporterObject) o2)
                                                                                          .filter(o2 -> o2.pair == null)
                                                                                          .filter(o2 -> !o2.equals(o))
                                                                                          .findFirst()
                                                                                          .orElse(null))));
  }

  /**
   * spawn entities
   *
   * @param gameState GameState
   */
  public void addEntities (ClassicPacmanGameState gameState)
  {
    //a list of the ghost to be spawned
    ConcurrentLinkedDeque <GhostAI> subAIs = new ConcurrentLinkedDeque <>();
    subAIs.add(new AggressiveAI(gameState));
    subAIs.add(new SneakyAI(gameState));
    subAIs.add(new ConfusedAI(gameState));
    subAIs.add(new ShyAI(gameState));

    tiles.forEach((vec, tile) ->
    {
      if (tile.type == Type.playerSpawn)
      {
        gameState.gameObjects.add(
            new PacmanEntity((int) ( this.tileSize * 2. / 3. ), tile.center, gameState)
        );
      }

      if (tile.type == Type.ghostSpawn)
      {
        while (subAIs.size() != 0)
        {
          gameState.gameObjects.add(
              new GhostEntity("nowak", tile.center, subAIs.getFirst(), subAIs.pop().name)
          );
        }
      }
    });

    //allows the first ghost to exit the spawn box
    gameState.gameObjects.stream()
                         .filter(o -> o instanceof GhostEntity)
                         .map(o -> (GhostEntity) o)
                         .filter(o -> o.name.equals(ClassicPacmanGameConstants.ghostNames.BLINKY))
                         .forEach(o ->
                         {
                           o.canUseDoor = true;
                           o.free = true;
                         });
  }

  /**
   * This methode is used to set the items in
   */
  public void setItems (List <ItemPH> items)
  {
    //spawn new items if there are no leftover items from the last round
    if (items.size() == 0)
    {
      tiles.forEach((vec, tile) ->
      {
        switch (tile.type)
        {
          case coin -> gameState.gameObjects.add(
              new ItemPH(
                  tile.pos.add(new Vector2d().cartesian(tileSize / 2., tileSize / 2.)),
                  gameState,
                  ClassicPacmanGameConstants.Collectables.coin,
                  true,
                  ClassicPacmanGameConstants.collectionColor.get(ClassicPacmanGameConstants.Collectables.coin)));

          case powerUp -> gameState.gameObjects.add(
              new ItemPH(
                  tile.pos.add(new Vector2d().cartesian(tileSize / 2., tileSize / 2.)),
                  gameState,
                  ClassicPacmanGameConstants.Collectables.powerUp,
                  true,
                  ClassicPacmanGameConstants.collectionColor.get(ClassicPacmanGameConstants.Collectables.powerUp)));

          default ->
          {
          }
        }
      });
    }
    else
    {
      //add the leftover items to the game
      items.forEach(o -> o.expired = false);

      gameState.gameObjects.addAll(items);
    }
  }

  /**
   * Split given position in external and internal position
   *
   * @param pos position to split
   * @return [external, internal]
   */
  public TotalPosition splitPosition (Vector2d pos)
  {
    Vector2d ex = pos.divide(tileSize).floor();
    Vector2d in = pos.subtract(ex.multiply(tileSize)).subtract(new Vector2d().cartesian(1, 1).multiply(tileSize / 2.));
    return new TotalPosition(ex, in);
  }

  /**
   * Returns a map that maps the tiles of the used on map onto the items that those tiles hold
   *
   * @return Map <PacmanMapTile, ClassicPacmanGameState.Collectables>
   */
  public List <ItemPH> getPlacedItems ()
  {
    return gameState.gameObjects.stream()
                                .filter(o -> o instanceof ItemPH)
                                .map(o -> (ItemPH) o)
                                .toList();
  }

  /**
   * returns an int representing the remaining items on the current map
   *
   * @return int with item count
   */
  public int getPillCount ()
  {
    List <ItemPH> pillCount = gameState.map.getPlacedItems().stream()
                                           .filter(o -> o.type == ClassicPacmanGameConstants.Collectables.coin || o.type == ClassicPacmanGameConstants.Collectables.powerUp)
                                           .toList();
    return pillCount.size();
  }

  /**
   * returns a list containing all tiles of a given type
   *
   * @param type the type of the tiles to be returned
   * @return list of tiles
   */
  public List <PacManMapTile> getTilesOfType (Type type) { return tiles.values().stream().filter(tile -> tile.type == type).toList(); }

  public record TotalPosition(Vector2d ex, Vector2d in) { }
}
