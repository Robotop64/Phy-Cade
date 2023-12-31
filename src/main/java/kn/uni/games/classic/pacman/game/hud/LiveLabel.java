package kn.uni.games.classic.pacman.game.hud;


import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PlacedObject;
import kn.uni.games.classic.pacman.game.Rendered;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.stream.IntStream;

import static kn.uni.util.Util.sin;


public class LiveLabel extends PlacedObject implements Rendered
{
  private Vector2d size;
  private int      liveCount;

  public LiveLabel (Vector2d pos, Vector2d size)
  {
    this.pos = pos;
    this.size = size;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    //fontSize
    String text     = "❰" + "%9d".formatted(gameState.score) + "❱";
    int    fontSize = (int)(((size.x / text.length() * 32 / 20) / 100 * gameState.uiSize));
    //Draw Icon and String
    pos.use(g::translate);
    //icon radius
    int r     = 20;
    int angle = (int)Math.round(20 + 40 * sin(30));
    //outline
    g.setStroke(new BasicStroke(Math.round(r / 3.)));
    g.setColor(Color.orange.darker());
    g.drawArc(r, (int)(size.y / 2 - r), 2 * r, 2 * r, angle, 360 - 2 * angle);
    //outline
    IntStream.of(-1, 1)
             .forEach(i -> new Vector2d()
               .polar(r, i * angle)
               .use((x, y) -> g.drawLine(2 * r,
                 (int)(size.y / 2),
                 2 * r + x,
                 (int)(size.y / 2) + y)));
    //filling
    g.setColor(Color.yellow);
    g.fillArc(r, (int)(size.y / 2 - r), 2 * r, 2 * r, angle, 360 - 2 * angle);
    //live tracker
    g.drawString("" + String.valueOf(gameState.lives), 4 * r, (int)(18.4 * text.length() / 160. * fontSize));
    g.setStroke(new BasicStroke(1));
    //    size.use((x, y) -> g.drawRect(0, 0, x, y));
    pos.multiply(-1).use(g::translate);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }
}
