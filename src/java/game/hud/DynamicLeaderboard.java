package game.hud;

import game.PlacedObject;
import game.Rendered;
import game.pacman.ClassicPacmanGameState;
import persistence.DatabaseProvider;
import ui.LeaderboardMenu;
import util.Util;
import util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

public class DynamicLeaderboard extends PlacedObject implements Rendered
{
  Vector2d                                pos;
  Vector2d                                size;
  List <LeaderboardMenu.LeaderboardEntry> dynBoard = null;


  public DynamicLeaderboard (Vector2d pos, Vector2d size)
  {
    this.pos = pos;
    this.size = size;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    String header = "Dynamic Leaderboard";

    if (gameState.score > 10)
    {
      if (dynBoard == null)
      {
        dynBoard = DatabaseProvider.dynLeaderboard("pacman", gameState.score);
      }

      if (gameState.score > dynBoard.get(0).highScore())
      {
        dynBoard = DatabaseProvider.dynLeaderboard("pacman", gameState.score);
      }

      String nextScore   = "❰" + "%9d".formatted(dynBoard.get(0).highScore()) + "❱";
      String nextName    = "%1$-16s".formatted(dynBoard.get(0).name());
      String youScore    = "❰" + "%9d".formatted(gameState.score) + "❱";
      String youName     = "%1$-16s".formatted("You");
      String behindScore = "❰" + "%9d".formatted(dynBoard.get(1).highScore()) + "❱";
      String behindName  = "%1$-16s".formatted(dynBoard.get(1).name());

      String[] list = { nextScore, nextName, youScore, youName, behindScore, behindName };


      int rowHeight = (int) ( size.y / 4 );

      pos.use(g::translate);
      drawString(g, header, gameState, 0);
      for (int i = 0; i < list.length; i++)
      {
        drawString(g, list[i], gameState, i * 38 + 50);
      }

      pos.multiply(-1).use(g::translate);
    }
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }

  private int getFontSize (String text, ClassicPacmanGameState gameState)
  {
    return (int) ( ( ( size.x / text.length() * 32 / 20 ) / 100 * gameState.uiSize ) );
  }

  private void drawString (Graphics2D g, String text, ClassicPacmanGameState gameState, int rowOffset)
  {
    g.setFont(Util.fira(getFontSize(text, gameState), Font.PLAIN));
    g.setStroke(new BasicStroke(1));
    g.drawString(text, 3, (int) ( 18.4 * text.length() / 160. * getFontSize(text, gameState) + rowOffset ));
  }
}
