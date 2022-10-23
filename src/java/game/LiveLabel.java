package game;

import util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.stream.IntStream;

import static util.Util.sin;

public class LiveLabel extends PlacedObject implements Rendered
{
  private int size;
  private int liveCount;

  public LiveLabel (Vector2d pos, int size, int liveCount)
  {
    this.pos = pos;
    this.size = (int) ( size / 3. );
    this.liveCount = liveCount;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    //icon radius
    int r     = size;
    int angle = (int) Math.round(20 + 40 * sin(30));
    //outline
    g.setStroke(new BasicStroke(Math.round(r / 3.)));
    g.setColor(Color.orange.darker());
    g.drawArc((int) pos.x, (int) pos.y, 2 * r, 2 * r, angle, 360 - 2 * angle);
    g.drawLine((int) ( pos.x + r ), (int) ( pos.y + r ), (int) ( pos.x + r ), (int) ( pos.y + r ));
    IntStream.of(-1, 1)
             .forEach(i -> new Vector2d()
                 .polar(r, i * angle)
                 .use((x, y) -> g.drawLine(
                     (int) ( pos.x + r ),
                     (int) ( pos.y + r ),
                     x + (int) ( pos.x + r ),
                     (int) pos.y + r + y)));

    //filling
    g.setColor(Color.yellow);
    g.fillArc((int) pos.x, (int) pos.y, 2 * r, 2 * r, angle, 360 - 2 * angle);
    //live tracker
    g.drawString("x" + String.valueOf(liveCount), (int) ( pos.x + 5 / 2. * r ), (int) ( pos.y + 3.5 / 2. * r ));
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }
}
