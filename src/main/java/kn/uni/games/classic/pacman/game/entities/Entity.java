package kn.uni.games.classic.pacman.game.entities;

import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.map.AdvPacManMap;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class Entity extends AdvPlacedObject
{
  public boolean          stunned              = false;
  public boolean          dead                 = false;
  public boolean          undead               = false;
  public boolean          edible               = false;
  public boolean          vulnerable           = false;
  public Direction        facing               = Direction.right;
  public List <Direction> suppressedDirections = new ArrayList <>();
  public Vector2d         velocity;

  public AdvGameConst.EntityType type;

  public int maxHealth;
  public int currentHealth;

  public void heal (int amount)
  {
    currentHealth += amount;
    if (currentHealth > maxHealth)
      currentHealth = maxHealth;
  }

  public void overHeal (int amount)
  {
    currentHealth += amount;
  }

  public void damage (int amount)
  {
    currentHealth -= amount;
    if (currentHealth <= 0)
      die();
  }

  public void die ()
  {

  }

  public void moveAbs (Vector2d offset)
  {
    this.absPos.add(offset);
  }

  /**
   * Returns the position of the entity in the map in tile coordinates
   *
   * @return the position of the entity in the map in tile coordinates
   */
  public Vector2d getMapPos ()
  {
    AdvPacManMap map = this.gameState.objects.maps().get(0);
    return map.getTileMapPos(this.absPos);
  }

  public enum validTiles
  {
    FLOOR
  }
}
