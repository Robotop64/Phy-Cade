package kn.uni.games.classic.pacman.game;

import kn.uni.Gui;
import kn.uni.util.Direction;
import kn.uni.util.TextureEditor;
import kn.uni.util.Vector2d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TeleporterObject extends CollidableObject implements Rendered, Ticking
{
  public  TeleporterObject pair;
  public  boolean          enabled;
  private Direction        outDirection;
  private BufferedImage    texture;

  public TeleporterObject (ClassicPacmanGameState gameState, ClassicPacmanMap map, Vector2d pos, Direction outDirection)
  {
    super();
    this.pos = pos;
    this.movable = false;

    this.outDirection = outDirection;
    this.enabled = true;

    this.texture = TextureEditor.getInstance().loadTexture("Objects", "Teleporter.png");
    this.texture = TextureEditor.getInstance().scale(this.texture, map.tileSize, map.tileSize);
    this.hitbox = new Vector2d().cartesian(3, 3);
    this.collideAction = () -> teleport(gameState);
  }


  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    pos.use(g::translate);
    Vector2d topLeft = new Vector2d().cartesian(texture.getWidth(), texture.getHeight()).multiply(-.5);
    topLeft.use(g::translate);
    g.drawImage(texture, 0, 0, Gui.getInstance().frame);
    topLeft.multiply(-1).use(g::translate);
    g.drawOval((int) ( -hitbox.x / 2 ), (int) ( -hitbox.y / 2 ), (int) hitbox.x, (int) hitbox.y);
    pos.multiply(-1).use(g::translate);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 110;
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    if (getCollisions(this, gameState.gameObjects).stream().toList().size() == 0)
    {
      enabled = true;
    }
  }

  private void teleport (ClassicPacmanGameState gameState)
  {
    if (enabled)
    {
      //disable the immediate teleportation of the player after getting to new location by locking the receiving teleporter
      enabled = false;
      pair.enabled = false;

      System.out.println("Teleporting " + this.pos + " -> " + pair.pos);

      //move trigger to pair location-tile-center
      this.collider.pos = pair.pos;
      //change player direction to match the outDirection of the teleporter
      //      gameState.playerDirection = outDirection;
    }
  }

  public void pair (TeleporterObject a)
  {
    a.pair = this;
    this.pair = a;
  }

}


