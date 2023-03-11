package kn.uni.games.classic.pacman.game.entities;

import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.games.classic.pacman.game.items.FruitItem;
import kn.uni.games.classic.pacman.game.items.PPelletItem;
import kn.uni.games.classic.pacman.game.items.PelletItem;
import kn.uni.games.classic.pacman.game.objects.AdvPlacedObject;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.util.Arrays;

public class Spawner extends Entity
{
  public AdvPlacedObject spawnable;
  public String          name;

  public Spawner (String name, AdvGameState gameState, Vector2d mapPos, AdvPlacedObject spawnable)
  {
    super();
    this.name = name;
    this.gameState = gameState;
    this.mapPos = mapPos;
    this.spawnable = spawnable;
  }

  public void spawn ()
  {
    if (spawnable instanceof PelletItem)
      gameState.spawnScaled(AdvGameState.Layer.ITEMS, spawnable);
    else if (spawnable instanceof PPelletItem)
      gameState.spawnScaled(AdvGameState.Layer.ITEMS, spawnable);
    else if (spawnable instanceof FruitItem)
      gameState.spawnScaled(AdvGameState.Layer.ITEMS, spawnable);
    else if (spawnable instanceof AdvPacManEntity)
    {
      gameState.players.add((AdvPacManEntity) spawnable);
      gameState.requestedDirections.add(Direction.down);
      gameState.spawnScaled(AdvGameState.Layer.ENTITIES, spawnable);
    }
    else
      throw new IllegalArgumentException("Invalid spawnable: " + spawnable.getClass().getSimpleName() + " " + Arrays.toString(spawnable.getClass().getInterfaces()));

  }
}
