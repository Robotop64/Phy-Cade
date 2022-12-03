package kn.uni.games.classic.pacman.game;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
import kn.uni.util.Direction;
import kn.uni.util.TextureEditor;
import kn.uni.util.Vector2d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TeleporterObject extends CollidableObject implements Rendered, Ticking
{
  private final Direction        outDirection;
  //general variables
  public        TeleporterObject pair;
  public        boolean          guided;
  //state variables
  public        boolean          enabled;
  //rendering variables
  private       BufferedImage    texture;

  public TeleporterObject (ClassicPacmanMap map, Vector2d pos, boolean guided, Direction outDirection)
  {
    super();
    //inherited variables
    this.pos = pos;
    this.movable = false;
    this.hitbox = new Vector2d().cartesian(3, 3);
    this.collideAction = this::teleport;
    //general variables
    this.guided = guided;
    //state variables
    this.outDirection = outDirection;
    this.enabled = true;
    //rendering variables
    this.texture = TextureEditor.getInstance().loadTexture("Objects", "Teleporter.png");
    this.texture = TextureEditor.getInstance().scale(this.texture, map.tileSize, map.tileSize);
  }


  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    pos.use(g::translate);
    Vector2d topLeft = new Vector2d().cartesian(texture.getWidth(), texture.getHeight()).multiply(-.5);
    topLeft.use(g::translate);
    g.drawImage(texture, 0, 0, Gui.getInstance().frame);
    topLeft.multiply(-1).use(g::translate);
    if (DebugDisplay.getDebugDisplay(gameState).enabled)
    {
      g.drawOval((int) ( -hitbox.x / 2 ), (int) ( -hitbox.y / 2 ), (int) hitbox.x, (int) hitbox.y);
    }
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
    //re-enable teleporter if there are no collisions
    if (getCollisions(this, gameState.gameObjects).stream().toList().size() == 0) enabled = true;
  }

  /**
   * Teleports the collider to the paired teleporter
   */
  private void teleport ()
  {
    if (enabled && pair != null)
    {
      //disable the immediate teleportation of the player after getting to new location by locking the receiving teleporter
      pair.enabled = false;

      //move trigger to pair location-tile-center
      this.collider.pos = pair.pos;

      //TODO implement guided teleportation
      //change player direction to match the outDirection of the teleporter
      //      if(guided) {
      //
      //      }
    }
  }

  /**
   * Used to assign a pair to the teleporter
   *
   * @param a the teleporter to be paired to
   */
  public void pair (TeleporterObject a)
  {
    a.pair = this;
    this.pair = a;
  }

}


