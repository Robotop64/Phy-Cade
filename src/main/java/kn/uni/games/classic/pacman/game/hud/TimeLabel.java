package kn.uni.games.classic.pacman.game.hud;


import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PlacedObject;
import kn.uni.games.classic.pacman.game.Rendered;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.MILLIS;

public class TimeLabel extends PlacedObject implements Rendered
{
  Vector2d size;

  public TimeLabel (Vector2d pos, Vector2d size)
  {
    this.pos = pos;
    this.size = size;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    //format timeCounter
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss.SSS");

    LocalTime currentTime = LocalTime.now();

    long millisBetween = MILLIS.between(gameState.startTime, currentTime);

    LocalTime diff = LocalTime.of(0, 0, 0, 0).plus(millisBetween, MILLIS);
    gameState.gameDuration = diff;
    String text     = "Time: " + diff;
    int    fontSize = (int)(((size.x / text.length() * 32 / 20) / 100 * gameState.uiSize));

    //Draw String
    pos.use(g::translate);
    g.setFont(Util.fira(fontSize, Font.PLAIN));
    g.setStroke(new BasicStroke(1));
    g.drawString(text, 3, (int)(18.4 * text.length() / 160. * fontSize));
    //    size.use((x, y) -> g.drawRect(0, 0, x, y));
    pos.multiply(-1).use(g::translate);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }
}
