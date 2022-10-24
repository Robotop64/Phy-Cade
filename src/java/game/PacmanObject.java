package game;

import game.map.ClassicPacmanMap.TotalPosition;
import game.map.PacmanMapTile;
import game.map.PacmanMapTile.Type;
import util.Direction;
import util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.stream.IntStream;

import static util.Util.round;
import static util.Util.sin;

public class PacmanObject extends PlacedObject implements Rendered, Ticking
{
  int r;
  public double tilesPerSecond = 6;
  //  public double tilesPerSecond = 0.2;
  Vector2d v;

  public PacmanObject (int r, Vector2d pos)
  {
    this.pos = pos;
    this.r = r;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    TotalPosition tp = gameState.map.splitPosition(pos);
    g.setStroke(new BasicStroke(4));
    g.setColor(Color.green);
    tp.ex().multiply(gameState.map.tileSize).use(g::translate);
    g.drawRect(0, 0, gameState.map.tileSize, gameState.map.tileSize);
    tp.ex().multiply(gameState.map.tileSize).multiply(-1).use(g::translate);
    pos.use(g::translate);

    g.setStroke(new BasicStroke(Math.round(r / 3.)));
    g.setColor(Color.orange.darker());
    int θ = getΘ(gameState.playerDirection);
    g.rotate(Math.toRadians(θ));

    double animationDuration = gameState.tps / 2.;
    int    angle             = (int)Math.round(20 + 40 * sin((gameState.currentTick % animationDuration) / animationDuration * 360.));
    g.drawArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
    IntStream.of(-1, 1).forEach(i -> new Vector2d().polar(r, i * angle).use((x, y) -> g.drawLine(0, 0, x, y)));
    //filling
    g.setColor(Color.yellow);
    g.fillArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
    g.rotate(Math.toRadians(-θ));
    pos.multiply(-1).use(g::translate);
    //    System.out.printf("Tile (%.0f,%.0f), internal (%.0f,%.0f)%n", tp.ex().x, tp.ex().y, tp.in().x, tp.in().y);
  }

  private static int getΘ (Direction direction)
  {
    return switch (direction)
      {
        case up -> 270;
        case down -> 90;
        case left -> 180;
        case right -> 0;
      };
  }

  private PacmanMapTile getTile (ClassicPacmanGameState gameState)
  {
    return gameState.map.tiles.get(gameState.map.splitPosition(pos).ex());
  }


  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 100;
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    if (v == null)
    {
      v = new Vector2d().cartesian(tilesPerSecond, 0).multiply(gameState.map.tileSize).divide(gameState.tps);
      System.out.println(v);
    }

    TotalPosition tp          = gameState.map.splitPosition(pos);
    PacmanMapTile currentTile = gameState.map.tiles.get(tp.ex());
    PacmanMapTile nextTile    = currentTile.neighbors.get(gameState.playerDirection.toVector());

    System.out.println(gameState.playerDirection.toVector());
    System.out.println(tp.in());

    //moving towards centre of current tile
    if (round(gameState.playerDirection.toVector().scalar(tp.in())) < 0)
    {
      System.out.println("moving towards centre");
      // far away from some axis
      if (Math.min(pos.x, pos.y) > gameState.map.tileSize / 20.)
      {
        System.out.println("far away from axis");
        pos = pos.add(tp.in().multiply(-1).orthogonalTo(gameState.playerDirection.toVector()).unitVector().multiply(v.lenght()));
      }
      pos = pos.add(v.rotate(getΘ(gameState.playerDirection)));
    }
    // walking away from centre
    else
    {
      System.out.println("try moving away from centre");
      if (tp.in().x + tp.in().y < gameState.map.tileSize / 2. / .3)
      {
        System.out.println("close to centre");
        System.out.println(nextTile);
        if (nextTile != null && PacmanMapTile.walkable.contains(nextTile.type))
        {
          System.out.println("next tile is good");
          // far away from some axis
          if (Math.min(pos.x, pos.y) > gameState.map.tileSize / 20.)
          {
            System.out.println("far away from axis");
            pos = pos.add(tp.in().multiply(-1).orthogonalTo(gameState.playerDirection.toVector()).unitVector().multiply(v.lenght()));
          }
          pos = pos.add(v.rotate(getΘ(gameState.playerDirection)));
        }
      }
    }

    if (getTile(gameState) != null && getTile(gameState).type == Type.coin)
    {
      currentTile.type = Type.path;
      gameState.score += 50;
    }
  }
}
