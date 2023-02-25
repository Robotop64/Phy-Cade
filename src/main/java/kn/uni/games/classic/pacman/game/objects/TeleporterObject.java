package kn.uni.games.classic.pacman.game.objects;

import kn.uni.games.classic.pacman.game.graphics.Rendered;
import kn.uni.games.classic.pacman.game.graphics.Ticking;
import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TeleporterObject extends CollidableObject implements Rendered, Ticking
{
  @SuppressWarnings({ "FieldCanBeLocal", "unused" })
  private final Direction        outDirection;
  //general variables
  public        TeleporterObject pair;
  public        boolean          guided;
  //state variables
  public        boolean          enabled;
  //rendering variables
  private       BufferedImage    texture;

  public TeleporterObject (PacManMap map, Vector2d pos, boolean guided, Direction outDirection)
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
    if (pair != null)
    {
      Vector2d  other       = pair.pos;
      Direction orientation = pos.subtract(other).unitVector().toDirection();
      Vector2d  portPos     = new Vector2d().cartesian(0, 0).add(orientation.toVector().multiply(gameState.map.tileSize / 2.));

      //TODO add color table in case of multiple teleporters
      if (orientation.equals(Direction.left))
      {
        g.setColor(new Color(255, 165, 0, 40));
        g.fillOval((int) ( portPos.x - 5 ), (int) ( portPos.y - gameState.map.tileSize / 2. - gameState.map.tileSize / 6 ), 60, gameState.map.tileSize + gameState.map.tileSize / 3);
        g.setColor(new Color(255, 165, 0, 50).brighter());
        g.fillOval((int) ( portPos.x - 5 ), (int) ( portPos.y - gameState.map.tileSize / 2. ), 40, gameState.map.tileSize);
        g.setColor(new Color(255, 165, 0, 60).brighter().brighter());
        g.fillOval((int) ( portPos.x - 5 ), (int) ( portPos.y - gameState.map.tileSize / 4. ), 30, gameState.map.tileSize / 2);
        g.setColor(Color.black);
        g.fillOval((int) ( portPos.x - 5 ), (int) ( portPos.y - gameState.map.tileSize / 2. - gameState.map.tileSize / 6 ), 10, gameState.map.tileSize + gameState.map.tileSize / 3);
        g.setColor(Color.ORANGE.darker());
        g.drawOval((int) ( portPos.x - 5 ), (int) ( portPos.y - gameState.map.tileSize / 2. - gameState.map.tileSize / 6 ), 10, gameState.map.tileSize + gameState.map.tileSize / 3);
      }
      else
      {
        g.setColor(new Color(0x00, 0xFF, 0xFF, 40));
        g.fillOval((int) ( portPos.x - 60 ), (int) ( portPos.y - gameState.map.tileSize / 2. - gameState.map.tileSize / 6 ), 60, gameState.map.tileSize + gameState.map.tileSize / 3);
        g.setColor(new Color(0x00, 0xFF, 0xFF, 50).brighter());
        g.fillOval((int) ( portPos.x - 40 ), (int) ( portPos.y - gameState.map.tileSize / 2. ), 40, gameState.map.tileSize);
        g.setColor(new Color(0x00, 0xFF, 0xFF, 60).brighter().brighter());
        g.fillOval((int) ( portPos.x - 30 ), (int) ( portPos.y - gameState.map.tileSize / 4. ), 30, gameState.map.tileSize / 2);
        g.setColor(Color.black);
        g.fillOval((int) ( portPos.x - 5 ), (int) ( portPos.y - gameState.map.tileSize / 2. - gameState.map.tileSize / 6 ), 10, gameState.map.tileSize + gameState.map.tileSize / 3);
        g.setColor(Color.CYAN.darker());
        g.drawOval((int) ( portPos.x - 5 ), (int) ( portPos.y - gameState.map.tileSize / 2. - gameState.map.tileSize / 6 ), 10, gameState.map.tileSize + gameState.map.tileSize / 3);
      }
    }
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


