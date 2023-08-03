package kn.uni.games.classic.pacman.game.entities;

import kn.uni.games.classic.pacman.game.entities.ghosts.AdvGhostAi;
import kn.uni.games.classic.pacman.game.entities.ghosts.AdvGhostEntity;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvTimer;
import kn.uni.games.classic.pacman.game.items.FruitItem;
import kn.uni.games.classic.pacman.game.items.PPelletItem;
import kn.uni.games.classic.pacman.game.items.PelletItem;
import kn.uni.util.Direction;
import kn.uni.util.PrettyPrint;
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
    PrettyPrint.startGroup(PrettyPrint.Type.Event, "Spawning");
    String fruitNumber = ( type == SpawnerType.FRUIT ) ? " (" + Integer.toString(gameState.fruitSpawned + 1) + ")" : "";
    PrettyPrint.bullet("Type: " + type + fruitNumber);

    if (type == SpawnerType.GHOST)
    {
      PrettyPrint.bullet("SubType: " + name.replace("Spawn", ""));
      PrettyPrint.bullet("Mode:" + AdvGhostAi.defaultMode);
    }

    PrettyPrint.bullet("MapPos: " + mapPos);
    PrettyPrint.endGroup();

    if (type == SpawnerType.PELLET)
      gameState.addScaled(AdvGameState.Layer.ITEMS, new PelletItem(gameState, spawnMapPos));
    else if (type == SpawnerType.PPELLET)
      gameState.addScaled(AdvGameState.Layer.ITEMS, new PPelletItem(gameState, spawnMapPos));
    else if (type == SpawnerType.FRUIT)
    {
      FruitItem fruitItem = new FruitItem(gameState, spawnMapPos);
      gameState.addScaled(AdvGameState.Layer.ITEMS, fruitItem);

      AdvTimer.getInstance(gameState).addTask(
          new AdvTimer.TimerTask(
              gameState.currentTick,
              AdvGameConst.tps * 10L,
              () ->
              {
                gameState.objects.remove(AdvGameState.Layer.ITEMS, fruitItem);
                gameState.env.updateLayer.set(AdvGameState.Layer.ITEMS.ordinal(), true);
              }, "spoilFruit"), "spoil Fruit");
    }
    else if (type == SpawnerType.PLAYER)
    {
      AdvPacManEntity player = new AdvPacManEntity(gameState, spawnMapPos);
      gameState.players.add(player);
      gameState.requestedDirections.add(Direction.down);
      gameState.addScaled(AdvGameState.Layer.ENTITIES, player);
    }
    else if (type == SpawnerType.GHOST)
    {
      AdvGameConst.GhostNames name;
      if (this.name.contains("Blinky"))
        name = AdvGameConst.GhostNames.BLINKY;
      else if (this.name.contains("Pinky"))
        name = AdvGameConst.GhostNames.PINKY;
      else if (this.name.contains("Inky"))
        name = AdvGameConst.GhostNames.INKY;
      else if (this.name.contains("Clyde"))
        name = AdvGameConst.GhostNames.CLYDE;
      else
        throw new RuntimeException("Unknown ghost name: " + this.name);

      gameState.addScaled(AdvGameState.Layer.ENTITIES, new AdvGhostEntity(gameState, spawnMapPos, name));
    }

  }

  public enum SpawnerType
  {
    PELLET, PPELLET, FRUIT, PLAYER, GHOST, TELEPORTER
  }
}

