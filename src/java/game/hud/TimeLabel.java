package game.hud;

import game.ClassicPacmanGameState;
import game.PlacedObject;
import game.Rendered;
import util.Util;
import util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeLabel extends PlacedObject implements Rendered
{
  Vector2d pos;
  Vector2d size;

  public TimeLabel (Vector2d pos, Vector2d size)
  {
    this.pos = pos;
    this.size = size;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss.SSS");
    LocalTime         current   = gameState.time;
    String            time      = current.truncatedTo(ChronoUnit.MILLIS).format(formatter);


    String text     = " Time:  " + time + " ";
    int    fontSize = (int) ( ( ( size.x / text.length() * 32 / 20 ) / 100 * gameState.uiSize ) );

    pos.use(g::translate);


    g.setFont(Util.fira(fontSize, Font.PLAIN));
    g.setStroke(new BasicStroke(1));
    g.drawString(text, 3, (int) ( 18.4 * text.length() / 160. * fontSize ));


    //    size.use((x, y) -> g.drawRect(0, 0, x, y));

    pos.multiply(-1).use(g::translate);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }
}
