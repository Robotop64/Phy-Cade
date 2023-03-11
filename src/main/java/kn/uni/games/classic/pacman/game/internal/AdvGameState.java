package kn.uni.games.classic.pacman.game.internal;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.entities.Spawner;
import kn.uni.games.classic.pacman.game.objects.AdvPacManMap;
import kn.uni.games.classic.pacman.game.objects.AdvPlacedObject;
import kn.uni.util.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.IntStream;

public class AdvGameState
{

  //region game environment
  public GameEnvironment env;
  public int             tps     = 120;
  public int             fps     = 60;
  public boolean         paused  = false;
  public boolean         running = false;
  public long            currentTick;
  public long            lastTickTime;
  public List <ConcurrentLinkedDeque <Object>> layers              = new ArrayList <>();
  public List <AdvPacManEntity>                players             = new ArrayList <>();
  public List <Direction>                      requestedDirections = new ArrayList <>();
  //region game stats
  public long score       = 0;
  //endregion
  public int  level       = 1;
  public int  lives       = 5;
  public int  livesGained = 0;
  //region trackers
  public boolean fruitSpawned = false;
  //endregion
  public int     pelletCount  = 0;
  public int     pelletsEaten = 0;
  public AdvGameState (GameEnvironment env)
  {
    this.env = env;
    IntStream.range(0, 6).forEach(i -> layers.add(new ConcurrentLinkedDeque <Object>()));
  }
  //endregion

  public void addScore (long score)
  {
    long oldScore = this.score;
    long newScore = oldScore + score;
    //add a live if the score passed a multiple of 10000
    if (newScore / 10000 > oldScore / 10000)
      livesGained++;
  }

  public void spawnScaled (Layer type, AdvPlacedObject obj)
  {
    obj.absPos = obj.mapPos.multiply(( (AdvPacManMap) layers.get(1).getFirst() ).tileSize);
    layers.get(type.ordinal()).add(obj);
  }

  public void spawn (Layer type, AdvPlacedObject obj)
  {
    layers.get(type.ordinal()).add(obj);
  }

  public void checkFruit ()
  {
    if (fruitSpawned)
      return;

    if (layers.get(3).size() <= pelletCount / 2)
    {
      fruitSpawned = true;
      layers.get(3).stream()
            .filter(obj -> obj instanceof Spawner)
            .map(obj -> (Spawner) obj)
            .filter(spawner -> spawner.name.equals("FruitSpawn"))
            .forEach(Spawner::spawn);
    }
  }

  public void checkGameOver ()
  {
    if (lives <= 0)
    {
      env.stop();
    }
  }

  //background[0], map[1], objects[2], items[3], entities[4], vfx[5]
  public enum Layer
  {
    BACKGROUND, MAP, OBJECTS, ITEMS, ENTITIES, VFX
  }

}
