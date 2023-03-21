package kn.uni.games.classic.pacman.game.map;

import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.items.Item;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AdvPacManTile extends AdvPlacedObject implements AdvRendered
{
  public  Map <Direction, Boolean>       connections;
  public  ConnectionType                 connectionType;
  public  int[]                          neighboursCount = new int[2];
  public  Map <Direction, AdvPacManTile> neighbors;
  public  Color                          debugColor      = Color.BLACK;
  public  Color                          primitiveColor  = Color.BLACK;
  //type of the tile
  private TileType                       type;
  //objects that should be spawned on this tile
  private List <Item>                    items;
  private List <Entity>                  entities;
  private List <AdvPlacedObject>         objects;


  public AdvPacManTile (AdvGameState gameState, Vector2d mapPos, TileType type)
  {
    this.mapPos = mapPos;
    this.type = type;

    neighbors = new HashMap <>();
    connections = new HashMap <>();

    items = new ArrayList <>();
    entities = new ArrayList <>();
    objects = new ArrayList <>();

    if (type == TileType.WALL)
      primitiveColor = Color.BLACK;
    else if (type == TileType.FLOOR)
      primitiveColor = Color.BLUE;
  }

  //region getters and setters

  //region absolutes
  public Vector2d getAbsolutePos ()
  {
    return absPos;
  }

  public void setAbsolutePos (Vector2d pos)
  {
    absPos = pos;

    items.forEach(item ->
    {
      item.absPos = pos.add(new Vector2d().cartesian(1, 1).multiply(AdvGameConst.tileSize / 2.));
    });
    entities.forEach(entity ->
    {
      entity.absPos = pos.add(new Vector2d().cartesian(1, 1).multiply(AdvGameConst.tileSize / 2.));
    });
    objects.forEach(object ->
    {
      object.absPos = pos.add(new Vector2d().cartesian(1, 1).multiply(AdvGameConst.tileSize / 2.));
    });
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
  public Map <Direction, AdvPacManTile> getNeighbors ()
  {
    return neighbors;
  }

  public void setNeighbors (Map <Direction, AdvPacManTile> neighbors)
  {
    this.neighbors = neighbors;
  }

  public void addNeighbour (Direction dir, AdvPacManTile tile)
  {
    neighbors.put(dir, tile);
  }

  public AdvPacManTile getNeighbour (Direction dir)
  {
    return neighbors.get(dir);
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
  public Collection <Item> getItems ()
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
  public Collection <AdvPlacedObject> getObjects ()
  {
    return objects;
  }

  public void setObjects (List <AdvPlacedObject> objects)
  {
    this.objects = objects;
  }

  public void addObject (AdvPlacedObject object)
  {
    objects.add(object);
  }
  //endregion

  //endregion

  public void setConnections ()
  {
    Arrays.stream(Direction.valuesAll()).forEach(dir ->
    {
      if (neighbors.containsKey(dir) && neighbors.get(dir).getType() == this.type)
        connections.put(dir, true);
      else
        connections.put(dir, false);
    });
  }

  public void setConnectionType ()
  {
    AtomicInteger cCount = new AtomicInteger();
    Arrays.stream(Direction.valuesCardinal()).forEach(dir ->
    {
      if (connections.get(dir))
        cCount.getAndIncrement();
    });
    neighboursCount[0] = cCount.get();

    AtomicInteger fCount = new AtomicInteger();
    Arrays.stream(Direction.valuesDiagonal()).forEach(dir ->
    {
      if (neighbors.containsKey(dir) && neighbors.get(dir).getType() == this.type)
        fCount.getAndIncrement();
    });
    neighboursCount[1] = fCount.get();

    if (cCount.get() == 4)
      connectionType = ConnectionType.X;
    else if (cCount.get() == 3)
      connectionType = ConnectionType.T;
    else if (cCount.get() == 2)
      connectionType = ConnectionType.S;
    else if (cCount.get() == 1)
      connectionType = ConnectionType.E;
    else
      connectionType = ConnectionType.N;
  }

  //region graphics
  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();

    g.drawImage((Image) cachedImg, (int) absPos.x, (int) absPos.y, null);
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  public void render ()
  {
    cachedImg = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = cachedImg.createGraphics();

    if (Objects.equals(Config.getCurrent("Debugging/-/Enabled"), true))
    {
      g.setColor(primitiveColor);
      g.fillRect(0, 0, iconSize, iconSize);
    }
    else
    {
      String path = "map/classic/" + type.toString().toLowerCase();
      try
      {
        cachedImg = TextureEditor.getInstance().loadTexture(path, "X.png");
      }
      catch (Exception e)
      {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, iconSize, iconSize);
      }
    }
    g.dispose();
  }
  //endregion

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
    //    H, V, TT, TB, TL, TR, CLT, CLB, CRT, CRB, X
    X, T, S, E, N
  }
  //endregion

}
