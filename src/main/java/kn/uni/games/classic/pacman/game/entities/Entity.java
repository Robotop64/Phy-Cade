package kn.uni.games.classic.pacman.game.entities;

import kn.uni.games.classic.pacman.game.objects.AdvPacManMap;
import kn.uni.games.classic.pacman.game.objects.AdvPlacedObject;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

public class Entity extends AdvPlacedObject
{
  public boolean   stunned    = false;
  public boolean   edible     = false;
  public boolean   vulnerable = false;
  public Direction facing     = Direction.right;
  public Vector2d  velocity;

  public EntityType type;

  public int maxHealth;
  public int currentHealth;

  //  public static Entity createEntity (AdvGameState gameState, EntityType type)
  //  {
  //    return switch (type)
  //        {
  //          case PACMAN -> new Pacman();
  //          case GHOST -> new Ghost();
  //          default -> null;
  //        };
  //  }

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

  public void expire ()
  {
    this.gameState.layers.get(3).remove(this);
  }

  public void moveAbs (Vector2d offset)
  {
    this.absPos.add(offset);
  }

  public Vector2d getMapPos ()
  {
    AdvPacManMap map = (AdvPacManMap) this.gameState.layers.get(1).getFirst();
    return map.getTileMapPos(this.absPos);
  }

  public enum EntityType
  {
    PACMAN, GHOST
  }

  public enum validTiles
  {
    WALL
  }
}
