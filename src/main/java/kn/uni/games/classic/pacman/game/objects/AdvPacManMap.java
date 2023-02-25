package kn.uni.games.classic.pacman.game.objects;

import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.items.Item;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdvPacManMap extends JPanel
{
  //dimension of the map in pixels
  public  Dimension                     size;
  //dimension of the map in tiles
  public  Vector2d                      mapSize;
  //size of a tile in pixels
  public  int                           tileSize;
  //map of tiles with canvas coordinates
  private Map <Vector2d, AdvPacManTile> tilesAbs;
  //map of tiles with map coordinates
  private Map <Vector2d, AdvPacManTile> tilesPixel;

  public AdvPacManMap ()
  {
    tilesPixel = new HashMap <>();
    tilesAbs = new HashMap <>();

    loadBasicMapTiles("pacman/map/PacManClassicMap - Tiles.bmp");
    loadBasicMapItems("pacman/map/PacManClassicMap - Spawnables.bmp");
  }

  public void paintComponent (Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    tilesAbs.forEach((k, v) ->
    {
      //      g2d.drawImage(v.getTileImage(), (int) k.x, (int) k.y, tileSize, tileSize, null);
      Color c = v.getType() == AdvPacManTile.TileType.FLOOR ? Color.BLUE : Color.BLACK;
      g2d.setColor(c);
      g2d.fillRect((int) k.x, (int) k.y, tileSize, tileSize);
    });
  }

  //the following methods are used to calculate the tile and map values
  //region calculations
  public void calculateAbsolutes (Dimension size)
  {
    this.size = size;
    tileSize = (int) Math.round(Math.min(size.width / mapSize.x, size.height / mapSize.y));
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
  }
  //endregion

  // the following methods are used to generate the game objects
  //region spawning
  public void generateObjects (List <Object> objects)
  {
    tilesPixel.forEach((k, v) ->
    {
      if (v.getObjects().size() > 0)
      {
        objects.addAll(v.getObjects());
      }
    });
  }

  public void generateItems (List <Item> items)
  {
    tilesPixel.forEach((k, v) ->
    {
      if (v.getItems().size() > 0)
      {
        items.addAll(v.getItems());
      }
    });
  }

  public void generateEntities (List <Entity> entities)
  {
    tilesPixel.forEach((k, v) ->
    {
      if (v.getEntities().size() > 0)
      {
        entities.addAll(v.getEntities());
      }
    });
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
               AdvPacManTile tile = new AdvPacManTile(v, convertColorToTileType(c));
               tile.setTileImage();
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
                 tilesPixel.get(v).addItem(Item.createItem(Objects.requireNonNull(convertColorToItemType(c)), v));
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
    else if (color.equals(Color.ORANGE))
      return Item.ItemType.POWER_PELLET;
    return null;
  }
  //endregion
}
