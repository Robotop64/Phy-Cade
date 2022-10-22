package game;

import util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class BoxObject extends PlacedObject implements Rendered, Ticking
{
  public BoxObject ()
  {
    pos = new Vector2d().cartesian(100, 100);
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    g.setColor(Color.blue);
    g.fillRect(100 + (int)pos.x - 50, 100 + (int)pos.y - 50, 100, 100);
    g.setColor(Color.black);
    g.setStroke(new BasicStroke(2));
    g.drawRect(100 + (int)pos.x - 50, 100 + (int)pos.y - 50, 100, 100);
  }

  @Override
  public int paintLayer ()
  {
    return 100;
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    pos = pos.add(gameState.playerDirection.toVector());
    pos = pos.cartesian((pos.x + 500) % 500, (pos.y + 500) % 500);
  }
}
