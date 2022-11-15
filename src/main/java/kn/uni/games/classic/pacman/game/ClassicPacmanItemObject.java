package kn.uni.games.classic.pacman.game;

import kn.uni.Gui;
import kn.uni.util.TextureLoader;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants.collectionColor;
import static kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants.collectionPoints;

public class ClassicPacmanItemObject extends CollidableObject implements Rendered, Ticking
{
  public  ClassicPacmanGameConstants.Collectables type;
  public  boolean                                 eatable;
  public  boolean                                 eaten;
  public  long                                    eatenTick;
  public  long                                    despawnTicks = 200;
  private BufferedImage                           icon;

  public ClassicPacmanItemObject (Vector2d pos, ClassicPacmanGameState gameState, ClassicPacmanGameConstants.Collectables type, boolean eatable, Color scoreColor)
  {
    super();
    this.pos = pos;
    this.type = type;
    this.movable = false;
    this.eatable = eatable;
    this.eaten = false;

    this.collideAction = () ->
    {
      eat(gameState);
    };


    //load icon texture
    icon = TextureLoader.getInstance().loadTexture("items", type.name() + ".png");
    this.hitbox = new Vector2d().cartesian(icon.getWidth(), icon.getHeight());
  }

  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {

    Vector2d topLeft = new Vector2d().cartesian(icon.getWidth(), icon.getHeight()).multiply(-.5).add(pos);
    topLeft.use(g::translate);
    //draw icon if item is not eaten
    if (!eaten)
    {
      g.drawImage(icon, 0, 0, Gui.getInstance().frame);
    }
    else
    //draw score earned by eating fruit
    {
      //scorePos X&Y are used to determine the offset&progression of the score text from the fruit origin
      int scorePosY = (int) ( gameState.map.tileSize + Util.progression(eatenTick, despawnTicks, gameState) * -10 );
      int scorePosX = (int) ( gameState.map.tileSize + Util.progression(eatenTick, despawnTicks, gameState) * -5 );
      //score color fade out
      Color scoreColor = collectionColor.get(type);
      g.setColor(new Color(scoreColor.getRed(), scoreColor.getGreen(), scoreColor.getBlue(), (int) ( 255 * ( 1. - Util.progression(eatenTick, despawnTicks, gameState) ) )));
      g.setFont(Gui.getInstance().content.getFont().deriveFont(20f));
      g.drawString(ClassicPacmanGameConstants.collectionPoints.get(type).toString(), scorePosX, scorePosY);
    }
    topLeft.multiply(-1).use(g::translate);
  }

  public int paintLayer () { return Integer.MAX_VALUE - 120; }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    if (eaten && ( gameState.currentTick > eatenTick + despawnTicks ))
    {
      gameState.gameObjects.remove(this);
    }
  }

  private void eat (ClassicPacmanGameState gameState)
  {
    if (!eaten)
    {
      gameState.score += collectionPoints.get(this.type);
      this.eaten = true;
      this.eatenTick = gameState.currentTick;
    }
  }
}
