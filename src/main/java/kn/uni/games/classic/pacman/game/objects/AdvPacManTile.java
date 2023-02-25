package kn.uni.games.classic.pacman.game.objects;

import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.items.Item;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdvPacManTile
{
  //position of the tile on the canvas
  private Vector2d                       absolutePos;
  //position of the tile on the map
  private Vector2d                       mapPos;
  private Map <Direction, AdvPacManTile> neighbours;
  private BufferedImage                  tileImage;

  //type of the tile
  private TileType                 type;
  private ConnectionType           connectionType;
  private Map <Direction, Boolean> connections;

  //objects that should be spawned on this tile
  private List <Item>    items;
  private List <Entity>  entities;
  private List <Objects> objects;


  public AdvPacManTile (Vector2d mapPos, TileType type)
  {
    this.mapPos = mapPos;
    this.type = type;

    items = new ArrayList <>();
    entities = new ArrayList <>();
    objects = new ArrayList <>();
  }

  //region getters and setters

  //region absolutes
  public Vector2d getAbsolutePos ()
  {
    return absolutePos;
  }

  public void setAbsolutePos (Vector2d pos)
  {
    absolutePos = pos;
  }
  //endregion

  //region map
  public Vector2d getMapPos ()
  {
    return mapPos;
  }

  public void setMapPos (Vector2d pos)
  {
    mapPos = pos;
  }
  //endregion

  //region neighbours
  public Map <Direction, AdvPacManTile> getNeighbours ()
  {
    return neighbours;
  }

  public void setNeighbours (Map <Direction, AdvPacManTile> neighbours)
  {
    this.neighbours = neighbours;
  }

  public void addNeighbour (Direction dir, AdvPacManTile tile)
  {
    neighbours.put(dir, tile);
  }

  public AdvPacManTile getNeighbour (Direction dir)
  {
    return neighbours.get(dir);
  }
  //endregion

  //region type
  public TileType getType ()
  {
    return type;
  }

  public void setType (TileType type)
  {
    this.type = type;
  }
  //endregion

  //region items
  public List <Item> getItems ()
  {
    return items;
  }

  public void setItems (List <Item> items)
  {
    this.items = items;
  }

  public void addItem (Item item)
  {
    items.add(item);
  }
  //endregion

  //region entities
  public List <Entity> getEntities ()
  {
    return entities;
  }

  public void setEntities (List <Entity> entities)
  {
    this.entities = entities;
  }

  public void addEntity (Entity entity)
  {
    entities.add(entity);
  }
  //endregion

  //region objects
  public List <Objects> getObjects ()
  {
    return objects;
  }

  public void setObjects (List <Objects> objects)
  {
    this.objects = objects;
  }

  public void addObject (Objects object)
  {
    objects.add(object);
  }
  //endregion

  //endregion

  public void setConnections ()
  {
    List <Direction> dirs = List.of(Direction.up, Direction.down, Direction.left, Direction.right);
    dirs.forEach(dir ->
    {
      if (neighbours.get(dir).getType() == this.type)
      {
        connections.put(dir, true);
      }
      else
      {
        connections.put(dir, false);
      }
    });

  }

  public void setTileImage ()
  {
    String        path = "map/classic/" + type.toString().toLowerCase();
    BufferedImage img  = null;
    try
    {
      img = TextureEditor.getInstance().loadTexture(path, "X.png");
    }
    catch (Exception e)
    {
      Color c = this.type == TileType.FLOOR ? Color.BLUE : Color.BLACK;
      img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
      Vector2d      dim      = new Vector2d().cartesian(64, 64);
      BufferedImage finalImg = img;
      dim.stream().forEach(pos ->
      {
        finalImg.setRGB((int) pos.x, (int) pos.y, c.getRGB());
      });
    }
  }

  public BufferedImage getTileImage ()
  {
    return tileImage;
  }

  //region enums
  public enum TileType
  {
    EMPTY, WALL, FLOOR
  }

  /**
   * The connection type of a tile.
   * H: Horizontal
   * V: Vertical
   * TT: T-Top
   * TB: T-Bottom
   * TL: T-Left
   * TR: T-Right
   * CLT: Corner-Left-Top
   * CLB: Corner-Left-Bottom
   * CRT: Corner-Right-Top
   * CRB: Corner-Right-Bottom
   * X: Cross
   */
  public enum ConnectionType
  {
    H, V, TT, TB, TL, TR, CLT, CLB, CRT, CRB, X
  }
  //endregion

}
