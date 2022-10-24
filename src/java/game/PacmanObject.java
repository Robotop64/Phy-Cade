package game;

import game.map.ClassicPacmanMap.TotalPosition;
import util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.stream.IntStream;

import static util.Util.sin;

public class PacmanObject extends PlacedObject implements Rendered, Ticking
{
  int r;

  public PacmanObject (int r)
  {
    super();
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
    int θ = switch (gameState.playerDirection)
        {
          case up -> 270;
          case down -> 90;
          case left -> 180;
          case right -> 0;
        };
    g.rotate(Math.toRadians(θ));
    int angle = (int) Math.round(20 + 40 * sin(( gameState.currentTick % 30 ) / 30. * 360.));
    //outline
    g.drawArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
    IntStream.of(-1, 1).forEach(i -> new Vector2d().polar(r, i * angle).use((x, y) -> g.drawLine(0, 0, x, y)));
    //filling
    g.setColor(Color.yellow);
    g.fillArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
    g.rotate(Math.toRadians(-θ));
    pos.multiply(-1).use(g::translate);
    //    System.out.printf("Tile (%.0f,%.0f), internal (%.0f,%.0f)%n", tp.ex().x, tp.ex().y, tp.in().x, tp.in().y);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 100;
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    pos = pos.add(gameState.playerDirection.toVector());
    pos = pos.cartesian(( pos.x + gameState.size.x ) % gameState.size.x, ( pos.y + gameState.size.y ) % gameState.size.y);
  }
}
