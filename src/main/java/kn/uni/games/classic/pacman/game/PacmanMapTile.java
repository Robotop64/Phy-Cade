package kn.uni.games.classic.pacman.game;


import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@SuppressWarnings("NonAsciiCharacters")
public class PacmanMapTile extends PlacedObject implements Rendered
{
  //general
  public static final List <Type>                   walkable  = List.of(Type.coin, Type.powerUp, Type.portal, Type.path, Type.ghostSpawn, Type.playerSpawn, Type.door, Type.ghostExit);
  private final       int                           size;
  public              Map <Vector2d, PacmanMapTile> neighbors = new HashMap <>();
  public              Type                          type;
  public              Color                         color;
  public              Vector2d                      center;


  public PacmanMapTile (Vector2d pos, int size, Type type)
  {
    //inherited
    this.pos = pos;
    //general
    if (pos != null) this.center = this.pos.add(new Vector2d().cartesian(size / 2., size / 2.));
    this.size = size;
    this.type = type;
    this.color = Color.black;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    g.translate((int) pos.rounded().x, (int) pos.rounded().y);
    g.setColor(color);
    g.fillRect(0, 0, size, size);

    switch (type)
    {
      case wall ->
      {
        g.setStroke(new BasicStroke((float) ( size / 11. )));
        g.setColor(Color.cyan.darker());
        int s2 = (int) ( size / 2. );
        g.translate(s2, s2);
        int neighborWallCount = (int) IntStream.range(0, 4)
                                               .mapToObj(i -> new Vector2d().polar(1, 90 * i))
                                               .map(v -> neighbors.get(v))
                                               .filter(n -> n == null || n.type == Type.wall).count();

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
        g.translate(-s2, -s2);
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
  { wall, path, none, coin, powerUp, ghostSpawn, playerSpawn, portal, ghostExit, door }
}
