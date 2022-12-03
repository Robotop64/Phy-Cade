package kn.uni.games.classic.pacman.game;

import kn.uni.Gui;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;

public class Particle extends PlacedObject implements Rendered, Ticking
{
  public Type     type;
  public Vector2d pos;
  public long     creationTick;
  public long     lifeTime;
  public Color    color;
  public String   display;

  public Particle (Type type, String display, Vector2d pos, long creationTick, long lifeTime, Color color)
  {
    this.type = type;
    this.display = display;
    this.pos = pos;
    this.creationTick = creationTick;
    this.lifeTime = lifeTime;
    this.color = color;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    if (type == Type.Number) paintScore(g, gameState);
  }

  private void paintScore (Graphics2D g, ClassicPacmanGameState gameState)
  {
    //scorePos X&Y are used to determine the offset&progression of the score text from the fruit origin
    int scorePosY = gameState.map.tileSize / 4;
    int scorePosX = -gameState.map.tileSize / 4;
    scorePosY += (int) ( Util.progression(creationTick, lifeTime, gameState) * -10 );
    scorePosX += (int) ( Util.progression(creationTick, lifeTime, gameState) * -5 );
    //score color fade out
    Color scoreColor = this.color;
    //ramp the alpha value of the score text (visible between progression > 0.15 with fade out)
    if (Util.progression(creationTick, lifeTime, gameState) < 0.15)
    {
      g.setColor(new Color(scoreColor.getRed(), scoreColor.getGreen(), scoreColor.getBlue(), 0));
    }
    else
    {
      g.setColor(new Color(scoreColor.getRed(), scoreColor.getGreen(), scoreColor.getBlue(), (int) ( 255 * ( 1. - Util.progression(creationTick, lifeTime, gameState) ) )));
    }
    g.setFont(Gui.getInstance().content.getFont().deriveFont(20f));
    //translate to tile center
    new Vector2d().cartesian(this.pos.x, this.pos.y).use(g::translate);
    g.drawString(display, scorePosX, scorePosY);
    new Vector2d().cartesian(this.pos.x, this.pos.y).multiply(-1).use(g::translate);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 20;
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    if (gameState.currentTick == creationTick + lifeTime)
    {
      gameState.gameObjects.remove(this);
    }
  }


  public enum Type
  { Text, Image, Particle, Number }
}
