package game.hud;

import game.ClassicPacmanGameState;
import game.PlacedObject;
import game.Rendered;
import util.Util;
import util.Vector2d;

import java.awt.BasicStroke;
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
    String text     = "❰" + "%9d".formatted(gameState.score) + "❱";
    int    fontSize = (int) ( ( ( size.x / text.length() * 32 / 20 ) / 100 * gameState.uiSize ) );

    pos.use(g::translate);


    g.setFont(Util.fira(fontSize, Font.PLAIN));
    g.setStroke(new BasicStroke(1));
    g.drawString(text, 3, (int) ( 18.4 * text.length() / 160. * fontSize ));


    //    int fontSize    = gameState.uiSize / 2;
    //    int labelHeight = (int) ( 227 / 200.0 * fontSize );
    //
    //    pos.use(g::translate);
    //
    //
    //    g.setFont(Util.fira(fontSize, Font.PLAIN));
    //    g.setColor(Color.cyan.darker());
    //    g.drawString("❰" + "%9d".formatted(gameState.score) + "❱", 0, (int) ( 184 / 200.0 * fontSize ));
    //
    //
    //dont delete
    //    g.drawRect(0, 0, 123 * 3, labelHeight);

    size.use((x, y) -> g.drawRect(0, 0, x, y));

    pos.multiply(-1).use(g::translate);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }
}
