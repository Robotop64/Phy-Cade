package kn.uni.games.classic.pacman.game;


import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ClassicPacmanGameState
{
  //frames will be drawn every second frame
  public int                                tps             = 120;
  public ConcurrentLinkedDeque <GameObject> gameObjects;
  public boolean                            running;
  public long                               currentTick;
  public long                               lastTickTime;
  public Direction                          playerDirection = Direction.down;
  public boolean                            logging;
  public Vector2d                           size;
  public ClassicPacmanMap                   map;
  public int                                mapOffset       = 15;

  //score and counter stuff
  public long      score      = 0;
  public int       level      = 1;
  public int       uiSize     = 100;
  public int       lives      = 5;
  public LocalTime startTime;
  public int       eatenPills = 0;
  public int       pillsLeft  = 244;

  //variety of fruits
  public enum Collectables
  { coin, powerUp, cherry, strawberry, apple, orange, melon, galaxian, bell, key }

  //how many points a fruit gives
  public Map <Collectables, Integer> collectionPoints = Map.of(
      Collectables.coin, 10,
      Collectables.powerUp, 50,
      Collectables.cherry, 100,
      Collectables.strawberry, 300,
      Collectables.orange, 500,
      Collectables.apple, 700,
      Collectables.melon, 1000,
      Collectables.galaxian, 2000,
      Collectables.bell, 3000,
      Collectables.key, 5000
  );
  // how many items were collected
  public Map <Collectables, Integer> collectionCount;

  //objects added to game
  public ClassicPacmanGameState ()
  {
    gameObjects = new ConcurrentLinkedDeque <>();
  }
}
