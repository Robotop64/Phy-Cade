package game.pacman.map;

import game.PlacedObject;
import game.Rendered;
import game.pacman.ClassicPacmanGameState;
import util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class PacmanMapTile extends PlacedObject implements Rendered
{
  public static final List <Type>                         walkable  = List.of(Type.coin, Type.powerUp, Type.portal, Type.path, Type.ghostSpawn, Type.playerSpawn);
  public              Map <Vector2d, PacmanMapTile>       neighbors = new HashMap <>();
  public              Type                                type;
  private             int                                 size;
  public              ClassicPacmanGameState.Collectables heldItem;


  public PacmanMapTile (Vector2d pos, int size, Type type)
  {
    this.pos = pos;
    this.size = size;
    this.type = type;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    g.translate((int) pos.rounded().x, (int) pos.rounded().y);
    g.setColor(Color.black);
    g.fillRect(0, 0, size, size);

    switch (type)
    {
      case wall ->
      {
        heldItem = null;
        g.setStroke(new BasicStroke((float) ( size / 11. )));
        g.setColor(Color.cyan.darker());
        int s2 = (int) ( size / 2. );
        g.translate(s2, s2);
        int neighborWallCount = (int) IntStream.range(0, 4)
                                               .mapToObj(i -> new Vector2d().polar(1, 90 * i))
                                               .map(v -> neighbors.get(v))
                                               .filter(n -> n == null || n.type == Type.wall).count();
        //        g.drawString(String.valueOf(neighborWallCount), 0, 0);
        if (neighborWallCount == 4)
        {
          IntStream.range(0, 4)
                   .map(i -> 90 * i)
                   .forEach(φ ->
                   {    //ToDo projection magic
                     Vector2d      x = new Vector2d().cartesian(1, 0).rotate(φ);
                     Vector2d      y = new Vector2d().cartesian(0, 1).rotate(φ);
                     PacmanMapTile n = neighbors.get(x.add(y));
                     if (n != null)
                     {
                       if (walkable.contains(n.type))
                       {
                         g.drawLine(0, 0, s2 * (int) x.x, s2 * (int) x.y);
                         g.drawLine(0, 0, s2 * (int) y.x, s2 * (int) y.y);
                       }
                     }
                   });
        }
        else
        {
          IntStream.range(0, 4)
                   .mapToObj(i -> new Vector2d().polar(1, 90 * i))
                   .filter(v -> neighborWallCount != 3 || !walkable.contains(neighbors.getOrDefault(v.multiply(-1), new PacmanMapTile(null, 0, Type.none)).type))
                   .filter(v -> neighbors.getOrDefault(v, new PacmanMapTile(null, 0, Type.path)).type == Type.wall)
                   .map(v -> v.multiply(s2))
                   .forEach(v -> g.drawLine(0, 0, (int) v.x, (int) v.y));
        }
        //        if (neighborWallCount == 2)
        //        {
        //          IntStream.range(0, 4)
        //                   .mapToObj(i -> new Vector2d().polar(1, 90 * i))
        //                   .filter(v -> neighbors.get(v) == null)
        //                   .map(v -> v.multiply(s2))
        //                   .forEach(v -> g.drawLine(0, 0, (int)v.x, (int)v.y));
        //        }
        g.translate(-s2, -s2);
      }
      case path, none, ghostSpawn, playerSpawn ->
      {
        heldItem = null;
      }
      case coin ->
      {
        heldItem = ClassicPacmanGameState.Collectables.coin;
        //        g.setColor(Color.yellow);
        //        g.fillOval(size / 4, size / 4, size / 2, size / 2);
      }
      case powerUp ->
      {
        heldItem = ClassicPacmanGameState.Collectables.powerUp;
        //        g.setColor(Color.green);
        //        g.fillOval(size / 4, size / 4, size / 2, size / 2);
      }
      case portal ->
      {
        g.setColor(Color.orange);
        g.fillRect(0, 0, size, size);
      }
    }
    g.translate(-(int) pos.rounded().x, -(int) pos.rounded().y);
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  public enum Type
  { wall, path, none, coin, powerUp, ghostSpawn, playerSpawn, portal }
}
