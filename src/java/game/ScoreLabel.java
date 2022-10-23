package game;

import util.Util;
import util.Vector2d;

import java.awt.Font;
import java.awt.Graphics2D;

public class ScoreLabel extends PlacedObject implements Rendered
{
  private Vector2d size;

  public ScoreLabel (Vector2d pos, Vector2d size)
  {
    this.pos = pos;
    this.size = size;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    int fontSize    = gameState.uiSize / 2;
    int labelHeight = (int) ( 227 / 200.0 * fontSize );

    pos.use(g::translate);


    g.setFont(Util.fira(fontSize, Font.PLAIN));
    g.drawString("❰" + "%9d".formatted(gameState.score) + "❱", 0, (int) ( 184 / 200.0 * fontSize ));


    //dont delete
    g.drawRect(0, 0, 123 * 3, labelHeight);

    pos.multiply(-1).use(g::translate);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }
}
