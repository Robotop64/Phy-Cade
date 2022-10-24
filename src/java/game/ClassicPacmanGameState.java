package game;

import game.map.ClassicPacmanMap;
import util.Direction;
import util.Vector2d;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ClassicPacmanGameState
{
  public ConcurrentLinkedDeque <GameObject> gameObjects;
  public boolean                            running;
  public long                               currentTick;
  public long                               lastTickTime;
  public Direction                          playerDirection = Direction.right;
  public boolean                            logging;
  public Vector2d                           size;
  public ClassicPacmanMap                   map;

  public long score  = 9;//00_000_000;
  public int  level  = 1;
  public int  uiSize = 100;
  public int  lives  = 7;
  public int  time   = 0;

  public enum Collectables
  { cherry, strawberry, apple, peach, melon, galaxian, bell, key }

  public Map <Collectables, Integer> collectionPoints = Map.of(
      Collectables.cherry, 100,
      Collectables.strawberry, 300,
      Collectables.peach, 500,
      Collectables.apple, 700,
      Collectables.melon, 1000,
      Collectables.galaxian, 2000,
      Collectables.bell, 3000,
      Collectables.key, 5000
  );

  public Map <Collectables, Integer> collectionCount;

  public ClassicPacmanGameState ()
  {
    gameObjects = new ConcurrentLinkedDeque <>();
  }
}
