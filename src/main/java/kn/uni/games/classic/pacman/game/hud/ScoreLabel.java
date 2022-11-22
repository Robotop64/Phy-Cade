package kn.uni.games.classic.pacman.game.hud;


import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PlacedObject;
import kn.uni.games.classic.pacman.game.Rendered;
import kn.uni.util.Fira;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
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
    //fontSize
    String text     = "❰" + "%9d".formatted(gameState.score) + "❱";
    int    fontSize = (int) ( ( ( size.x / text.length() * 32 / 20 ) / 100 * gameState.uiSize ) );
    //Draw String
    pos.use(g::translate);
    g.setFont(Fira.getInstance().getLigatures(fontSize));
    g.setStroke(new BasicStroke(1));
    g.drawString(text, 3, (int) ( 18.4 * text.length() / 160. * fontSize ));
    //dont delete
    //    g.drawRect(0, 0, 123 * 3, labelHeight);
    //    size.use((x, y) -> g.drawRect(0, 0, x, y));
    pos.multiply(-1).use(g::translate);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }
}
