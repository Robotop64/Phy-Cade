package kn.uni.games.classic.pacman.game.entities;

import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.items.FruitItem;
import kn.uni.games.classic.pacman.game.items.PPelletItem;
import kn.uni.games.classic.pacman.game.items.PelletItem;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

public class Spawner extends Entity
{
  public String      name;
  public SpawnerType type;
  public Vector2d    spawnMapPos;

  public Spawner (String name, AdvGameState gameState, Vector2d mapPos, SpawnerType type, Vector2d spawnMapPos)
  {
    super();
    this.name = name;
    this.gameState = gameState;
    this.mapPos = mapPos;
    this.type = type;
    this.spawnMapPos = spawnMapPos;
  }

  public void spawn ()
  {
    if (type == SpawnerType.PELLET)
      gameState.spawnScaled(AdvGameState.Layer.ITEMS, new PelletItem(gameState, spawnMapPos));
    else if (type == SpawnerType.PPELLET)
      gameState.spawnScaled(AdvGameState.Layer.ITEMS, new PPelletItem(gameState, spawnMapPos));
    else if (type == SpawnerType.FRUIT)
      gameState.spawnScaled(AdvGameState.Layer.ITEMS, new FruitItem(gameState, spawnMapPos));
    else if (type == SpawnerType.PLAYER)
    {
      AdvPacManEntity player = new AdvPacManEntity(gameState, spawnMapPos);
      gameState.players.add(player);
      gameState.requestedDirections.add(Direction.down);
      gameState.spawnScaled(AdvGameState.Layer.ENTITIES, player);
    }
    //    else if (type == SpawnerType.TELEPORTER)
    //      gameState.spawnScaled(AdvGameState.Layer.ENTITIES, new Teleporter(gameState, spawnMapPos));

  }

  public enum SpawnerType
  {
    PELLET, PPELLET, FRUIT, PLAYER, TELEPORTER
  }
}

