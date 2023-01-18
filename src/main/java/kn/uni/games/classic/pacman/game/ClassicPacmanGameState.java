package kn.uni.games.classic.pacman.game;


import kn.uni.ui.InputListener;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ClassicPacmanGameState
{
  //frames will be drawn every second frame
  public int                                tps = 120;
  public ConcurrentLinkedDeque <GameObject> gameObjects;
  public boolean                            running;
  public InputListener.Player               player;

  public long             currentTick;
  public long             lastTickTime;
  public Direction        playerDirection = Direction.down;
  public boolean          logging;
  public Vector2d         size;
  public ClassicPacmanMap map;
  public int              mapOffset       = 15;

  //score and counter stuff
  public long      score        = 0;
  public int       level        = 1;
  public int       uiSize       = 100;
  public int       lives        = 1;
  public int       livesGained  = 0;
  public LocalTime startTime;
  public LocalTime gameDuration = LocalTime.of(0, 0, 0, 0);
  public boolean   fruitSpawned = false;


  // @David Do we need this? and if we need it, do we need it here? ~Max
  //@Max we do, needed for game summary (GameSummaryPanel) ~David
  // how many items were collected
  @SuppressWarnings("unused")
  public Map <ClassicPacmanGameConstants.Collectables, Integer> collectionCount;

  //objects added to game
  public ClassicPacmanGameState (InputListener.Player player)
  {
    this.player = player;
    gameObjects = new ConcurrentLinkedDeque <>();
  }

}
