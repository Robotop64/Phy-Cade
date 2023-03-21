package kn.uni.games.classic.pacman.game.internal.tracker;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.entities.Spawner;
import kn.uni.games.classic.pacman.game.internal.GameEnvironment;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;
import kn.uni.util.Direction;
import kn.uni.util.fileRelated.Config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.IntStream;

public class AdvGameState
{

  //region game environment
  public GameEnvironment                       env;
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
  public int  lives       = 0;
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

    lives = (int) (double) Config.getCurrent("Gameplay/PacMan/StartLives");
  }

  public void addScore (long score)
  {
    long oldScore = this.score;
    long newScore = oldScore + score;
    this.score = newScore;
    //add a live if the score passed a multiple of 10000 (AdvGameConst.pointsToLife)
    if (newScore / AdvGameConst.pointsToLife > oldScore / AdvGameConst.pointsToLife)
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
      env.pauseGameIn(1000, () ->
      {
      });

      new Thread(() ->
      {
        try
        {
          Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
          throw new RuntimeException(e);
        }
        env.reloadLevel();
      }).start();
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
