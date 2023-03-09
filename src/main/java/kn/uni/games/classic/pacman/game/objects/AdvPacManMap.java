package kn.uni.games.classic.pacman.game.objects;

import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.games.classic.pacman.game.items.Item;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.PacPhiConfig;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AdvPacManMap extends AdvGameObject implements AdvRendered
{
  //dimension of the map in pixels
  public Dimension                     size;
  //dimension of the map in tiles
  public Vector2d                      mapSize;
  //size of a tile in pixels
  public int                           tileSize;
  //map of tiles with canvas coordinates
  public Map <Vector2d, AdvPacManTile> tilesAbs;
  //map of tiles with map coordinates
  public Map <Vector2d, AdvPacManTile> tilesPixel;

  public List <AdvGameObject> spawnables;

  public AdvPacManMap (AdvGameState gameState)
  {
    this.gameState = gameState;

    tilesPixel = new HashMap <>();
    tilesAbs = new HashMap <>();
    spawnables = new ArrayList <>();

    loadBasicMapTiles("pacman/map/PacManClassicMap - Tiles.bmp");
    loadBasicMapItems("pacman/map/PacManClassicMap - Spawnables.bmp");

    calculateNeighbours();
  }

  //region graphics
  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();

    g.drawImage(cachedImg, 0, 0, size.width, size.height, null);
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  public void render ()
  {
    cachedImg = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = cachedImg.createGraphics();
    tilesAbs.forEach((k, v) ->
    {
      if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
      {
        Color c = v.getType() == AdvPacManTile.TileType.FLOOR ? Color.BLUE : Color.BLACK;
        g2d.setColor(c);
        g2d.fillRect((int) k.x, (int) k.y, tileSize, tileSize);
      }

      v.paintComponent(g2d);

      Arrays.stream(Direction.valuesCardinal()).toList()
            .stream()
            .filter(d -> v.getType() == AdvPacManTile.TileType.WALL)
            .filter(d -> v.connections.containsKey(d) && v.connections.get(d).equals(true))
            .filter(d -> !( v.connectionType.equals(AdvPacManTile.ConnectionType.T)
                && v.neighbors.containsKey(d.opposite())
                && v.neighbors.get(d.opposite()).getType() == AdvPacManTile.TileType.FLOOR ))
            .filter(d -> !( v.neighboursCount[1] == 1
                && v.connectionType.equals(AdvPacManTile.ConnectionType.T)
                && v.neighbors.containsKey(d.opposite())
                && v.neighbors.get(d.opposite()).connectionType.equals(AdvPacManTile.ConnectionType.S) ))
            .filter(d -> !( v.connectionType.equals(AdvPacManTile.ConnectionType.X) ))
            .forEach(d ->
                {
                  Vector2d center = new Vector2d().cartesian(k.x + tileSize / 2., k.y + tileSize / 2.);
                  Vector2d end    = center.add(d.toVector().multiply(tileSize / 2.));

                  g2d.setStroke(new BasicStroke(3));
                  g2d.setColor(Color.CYAN.darker());
                  g2d.drawLine((int) center.x, (int) center.y, (int) end.x, (int) end.y);

                  if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
                  {
                    g2d.setColor(Color.RED);
                    g2d.drawString(v.connectionType.name(), (int) center.x, (int) center.y);
                  }
                }
            );

      Arrays.stream(Direction.valuesDiagonal()).toList()
            .stream()
            .filter(d -> v.getType() == AdvPacManTile.TileType.WALL)
            .filter(d -> v.connectionType.equals(AdvPacManTile.ConnectionType.X))
            .filter(d -> v.neighbors.containsKey(d))
            .filter(d -> v.neighbors.get(d).getType() == AdvPacManTile.TileType.FLOOR)
            .forEach(
                d ->
                {
                  Vector2d center = new Vector2d().cartesian(k.x + tileSize / 2., k.y + tileSize / 2.);
                  assert d.getCardinalsOfDiagonal() != null;
                  Vector2d endA = center.add(d.getCardinalsOfDiagonal()[0].toVector().multiply(tileSize / 2.));
                  Vector2d endB = center.add(d.getCardinalsOfDiagonal()[1].toVector().multiply(tileSize / 2.));

                  g2d.setStroke(new BasicStroke(3));
                  g2d.setColor(Color.CYAN.darker());
                  g2d.drawLine((int) center.x, (int) center.y, (int) endA.x, (int) endA.y);
                  g2d.drawLine((int) center.x, (int) center.y, (int) endB.x, (int) endB.y);

                  if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
                  {
                    g2d.setColor(Color.RED);
                    g2d.drawString(v.connectionType.name(), (int) center.x, (int) center.y);
                  }
                }
            );
    });
  }
  //endregion

  //the following methods are used to calculate the tile and map values
  //region calculations
  public void calculateAbsolutes (Dimension size)
  {
    this.size = size;
    tileSize = (int) Math.round(Math.min(size.width / mapSize.x, size.height / mapSize.y));
    AdvPlacedObject.iconSize = tileSize;
    tilesPixel.forEach((k, v) ->
    {
      v.setAbsolutePos(new Vector2d().cartesian(k.x * tileSize, k.y * tileSize));
      tilesAbs.put(v.getAbsolutePos(), v);
    });
  }

  public void calculateNeighbours ()
  {
    tilesPixel.forEach((k, v) ->
        Arrays.stream(Direction.valuesAll()).toList().forEach(d ->
        {
          Vector2d neighbourPos = k.add(d.toVector());
          if (tilesPixel.containsKey(neighbourPos))
          {
            v.addNeighbour(d, tilesPixel.get(neighbourPos));
          }
        }));
    tilesPixel.forEach((k, v) ->
        {
          v.setConnections();
          v.setConnectionType();
        }
    );
  }

  public Vector2d getTileMapPos (Vector2d pos)
  {
    return pos.divide(tileSize).floor();
  }

  public Vector2d getTileInnerPos (Vector2d pos)
  {
    return pos.subtract(getTileMapPos(pos).multiply(tileSize)).subtract(new Vector2d().cartesian(1, 1).multiply(tileSize / 2.));
  }
  //endregion

  // the following methods are used to generate the game objects
  //region spawning
  public List <AdvGameObject> generateObjects ()
  {
    ConcurrentLinkedDeque <AdvGameObject> objects = new ConcurrentLinkedDeque <>();
    tilesPixel.forEach((k, v) ->
    {
      if (v.getObjects().size() > 0)
      {
        objects.addAll(v.getObjects());
      }
    });

    spawnables.stream()
              .filter(o -> o instanceof AdvPlacedObject)
              .forEach(objects::add);

    return List.copyOf(objects);
  }

  public List <Item> generateItems ()
  {
    ConcurrentLinkedDeque <Item> items = new ConcurrentLinkedDeque <>();
    tilesPixel.forEach((k, v) ->
    {
      if (v.getItems().size() > 0)
      {
        items.addAll(v.getItems());
      }
    });

    spawnables.stream()
              .filter(o -> o instanceof Item)
              .map(o -> (Item) o)
              .forEach(items::add);

    return List.copyOf(items);
  }

  public List <Entity> generateEntities ()
  {
    ConcurrentLinkedDeque <Entity> entities = new ConcurrentLinkedDeque <>();
    tilesPixel.forEach((k, v) ->
    {
      if (v.getEntities().size() > 0)
      {
        entities.addAll(v.getEntities());
      }
    });

    spawnables.stream()
              .filter(o -> o instanceof Entity)
              .map(o -> (Entity) o)
              .forEach(entities::add);

    return List.copyOf(entities);
  }

  public void addToPool (AdvGameObject object)
  {
    spawnables.add(object);
  }
  //endregion

  //the following methods are used to initialise a primitive map from a file
  //region loading
  private void loadBasicMapTiles (String path)
  {
    try
    {
      BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path)));
      mapSize = new Vector2d().cartesian(image.getWidth(), image.getHeight());
      mapSize.stream()
             .forEach(v ->
             {
               Color         c    = new Color(image.getRGB((int) v.x, (int) v.y));
               AdvPacManTile tile = new AdvPacManTile(gameState, v, convertColorToTileType(c));
               //               tile.render();
               tilesPixel.put(v, tile);
             });
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private void loadBasicMapItems (String path)
  {
    try
    {
      BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path)));
      mapSize = new Vector2d().cartesian(image.getWidth(), image.getHeight());
      mapSize.stream()
             .forEach(v ->
             {
               Color c = new Color(image.getRGB((int) v.x, (int) v.y));
               if (convertColorToItemType(c) != null)
               {
                 tilesPixel.get(v).addItem(Item.createItem(gameState, Objects.requireNonNull(convertColorToItemType(c)), v));
               }
             });
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
  //endregion

  //the following methods are used to convert colors to tile types and item types
  //region conversion
  private AdvPacManTile.TileType convertColorToTileType (Color color)
  {
    if (color.equals(Color.BLUE))
      return AdvPacManTile.TileType.FLOOR;
    else if (color.equals(Color.BLACK))
      return AdvPacManTile.TileType.WALL;

    return AdvPacManTile.TileType.EMPTY;
  }

  private Item.ItemType convertColorToItemType (Color color)
  {
    if (color.equals(Color.YELLOW))
      return Item.ItemType.PELLET;
    else if (color.equals(new Color(255, 88, 0)))
      return Item.ItemType.PPELLET;
    return null;
  }
  //endregion
}
