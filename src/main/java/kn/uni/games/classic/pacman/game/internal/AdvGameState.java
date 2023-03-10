package kn.uni.games.classic.pacman.game.internal;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
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
  public GameEnvironment                       env;
  public int                                   tps                 = 120;
  public int                                   fps                 = 60;
  public boolean                               paused              = false;
  public boolean                               running             = false;
  public long                                  currentTick;
  public long                                  lastTickTime;
  //background[0], map[1], objects[2], items[3], entities[4], vfx[5]
  public List <ConcurrentLinkedDeque <Object>> layers              = new ArrayList <>();
  public List <AdvPacManEntity>                players             = new ArrayList <>();
  public List <Direction>                      requestedDirections = new ArrayList <>();
  //endregion

  //region game stats
  public long score       = 0;
  public int  level       = 1;
  public int  lives       = 5;
  public int  livesGained = 0;
  //endregion

  public AdvGameState (GameEnvironment env)
  {
    this.env = env;
    IntStream.range(0, 6).forEach(i -> layers.add(new ConcurrentLinkedDeque <Object>()));
  }

  public void addScore (long score)
  {
    long oldScore = this.score;
    long newScore = oldScore + score;
    //add a live if the score passed a multiple of 10000
    if (newScore / 10000 > oldScore / 10000)
      livesGained++;
  }

  public void spawnScaled (int type, AdvPlacedObject obj)
  {
    obj.absPos = obj.mapPos.multiply(( (AdvPacManMap) layers.get(1).getFirst() ).tileSize);
    layers.get(type).add(obj);
  }

  public void spawn (int type, AdvPlacedObject obj)
  {
    layers.get(type).add(obj);
  }
}
