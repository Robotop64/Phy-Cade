package game;

import util.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;

import static util.Util.sin;

public class LiveLabel extends PlacedObject implements Rendered
{
  private Vector2d size;

  public LiveLabel (Vector2d pos, Vector2d size)
  {
    this.pos = pos;
    this.size = size;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    //icon radius
    int r     = 30;
    int angle = (int) Math.round(20 + 40 * sin(30));

    //    g.drawArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
    //    IntStream.of(-1, 1).forEach(i -> new Vector2d().polar(r, i * angle).use((x, y) -> g.drawLine(0, 0, x, y)));
    g.setColor(Color.yellow);
    g.fillArc((int) pos.x, (int) pos.y, 2 * r, 2 * r, angle, 360 - 2 * angle);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }
}
