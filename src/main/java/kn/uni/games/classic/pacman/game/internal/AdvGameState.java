package kn.uni.games.classic.pacman.game.internal;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.entities.Spawner;
import kn.uni.games.classic.pacman.game.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.objects.AdvPlacedObject;
import kn.uni.util.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.IntStream;

public class AdvGameState
{

  //region game environment
  public GameEnvironment                       env;
  public int                                   tps                 = 120;
  public int                                   fps                 = 60;
  public boolean                               running             = false;
  public boolean                               paused              = false;
  public long                                  currentTick;
  public long                                  lastTickTime;
  public long                                  gameStartTime;
  public List <ConcurrentLinkedDeque <Object>> layers              = new ArrayList <>();
  public List <AdvPacManEntity>                players             = new ArrayList <>();
  public List <Direction>                      requestedDirections = new ArrayList <>();
  //endregion

  //region game stats
  public long score       = 0;
  public int  lives       = 5;
  public int  livesGained = 0;
  public int  level       = 1;
  public long time        = 0;
  //endregion

  //region trackers
  public boolean fruitSpawned = false;
  public int     pelletCount  = 0;
  public int     pelletsEaten = 0;
  //endregion

  public AdvGameState (GameEnvironment env)
  {
    this.env = env;
    IntStream.range(0, Layer.values().length).forEach(i -> layers.add(new ConcurrentLinkedDeque <Object>()));
  }

  public void addScore (long score)
  {
    long oldScore = this.score;
    long newScore = oldScore + score;
    this.score = newScore;
    //add a live if the score passed a multiple of 10000
    if (newScore / 10000 > oldScore / 10000)
    {
      livesGained++;
      lives++;
      env.gameScreen.setLives(lives);
    }
    env.gameScreen.setScore((int) this.score);
  }

  public void spawnScaled (Layer type, AdvPlacedObject obj)
  {
    obj.absPos = obj.mapPos.multiply(AdvGameConst.tileSize);
    layers.get(type.ordinal()).add(obj);
    if (type != Layer.INTERNALS)
      env.updateLayer.set(type.ordinal(), true);
  }

  public void spawn (Layer type, AdvGameObject obj)
  {
    layers.get(type.ordinal()).add(obj);
    if (type != Layer.INTERNALS)
      env.updateLayer.set(type.ordinal(), true);
  }

  public void checkFruit ()
  {
    if (fruitSpawned)
      return;

    if (pelletsEaten >= pelletCount / 2)
    {
      fruitSpawned = true;
      layers.get(Layer.ENTITIES.ordinal()).stream()
            .filter(obj -> obj instanceof Spawner)
            .map(obj -> (Spawner) obj)
            .filter(spawner -> spawner.name.equals("FruitSpawn"))
            .forEach(Spawner::spawn);
    }
  }

  public void checkProgress ()
  {
    if (pelletsEaten == pelletCount)
    {
      env.pauseGameIn(1000, () -> env.reloadLevel());
    }
  }

  public void checkGameOver ()
  {
    if (lives <= 0)
    {
      env.stopGame();
    }
  }

  public enum Layer
  {
    BACKGROUND, INTERNALS, MAP, OBJECTS, ITEMS, ENTITIES, VFX, PHYSICS
  }

}
