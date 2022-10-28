package kn.uni.games.classic.pacman.game;


import kn.uni.games.classic.pacman.persistence.PacmanDatabaseProvider;
import kn.uni.games.classic.pacman.screens.LeaderboardMenu;
import kn.uni.games.classic.pacman.screens.LeaderboardMenu.LeaderboardEntry;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DynamicLeaderboard extends PlacedObject implements Rendered
{
  Vector2d                pos;
  Vector2d                size;
  List <LeaderboardEntry> dynBoard = new ArrayList <>();


  public DynamicLeaderboard (Vector2d pos, Vector2d size)
  {
    this.pos = pos;
    this.size = size;
    // in case no entries available
    dynBoard.add(new LeaderboardMenu.LeaderboardEntry(" ", 0, LocalTime.now(), LocalDate.now()));
    dynBoard.add(new LeaderboardMenu.LeaderboardEntry(" ", 0, LocalTime.now(), LocalDate.now()));
    dynBoard = PacmanDatabaseProvider.dynLeaderboard("pacman", 10);
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    String header = "Dynamic Leaderboard:";


    if (dynBoard == null)
    {
      dynBoard = PacmanDatabaseProvider.dynLeaderboard("pacman", gameState.score);
    }

    if (gameState.score > dynBoard.get(0).highScore())
    {
      dynBoard = PacmanDatabaseProvider.dynLeaderboard("pacman", gameState.score);
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
    g.setFont(Util.firaUnderlined(getFontSize(header, gameState), Font.PLAIN));
    g.drawString(header, 3, (int) ( 18.4 * header.length() / 160. * getFontSize(header, gameState) ));

    for (int i = 0; i < list.length; i++)
    {
      drawString(g, list[i], gameState, i);
    }

    pos.multiply(-1).use(g::translate);

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

  private void drawString (Graphics2D g, String text, ClassicPacmanGameState gameState, int row)
  {
    //used to shift the rows
    int rowOffset = 0;

    if (row % 2 == 0)
    {
      rowOffset = row * ( 40 );
    }
    else
    {
      rowOffset = row * 40 - 10;
    }
    rowOffset += 50;

    //used to underline the names as separation
    if (row % 2 == 0)
    {
      g.setFont(Util.fira(getFontSize(text, gameState), Font.PLAIN));
    }
    else
    {
      g.setFont(Util.firaUnderlined(getFontSize(text, gameState), Font.PLAIN));
    }

    g.setStroke(new BasicStroke(1));
    g.drawString(text, 3, (int) ( 18.4 * text.length() / 160. * getFontSize(text, gameState) + rowOffset ));
  }
}
