package game.pacman;

import game.GameObject;
import game.pacman.map.ClassicPacmanMap;
import util.Direction;
import util.Vector2d;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ClassicPacmanGameState
{
  //engine stuff
  public int                                tps             = 165;
  public ConcurrentLinkedDeque <GameObject> gameObjects;
  public boolean                            running;
  public long                               currentTick;
  public long                               lastTickTime;
  public Direction                          playerDirection = Direction.left;
  public boolean                            logging;
  public Vector2d                           size;
  public ClassicPacmanMap                   map;
  public int                                mapOffset       = 15;

  //score and counter stuff
  public long      score      = 0;
  public int       level      = 1;
  public int       uiSize     = 100;
  public int       lives      = 1;
  public LocalTime time       = LocalTime.of(0, 0, 0, 0);
  public int       eatenPills = 0;

  //variety of fruits
  public enum Collectables
  { cherry, strawberry, apple, orange, melon, galaxian, bell, key }

  //how many points a fruit gives
  public Map <Collectables, Integer> collectionPoints = Map.of(
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
