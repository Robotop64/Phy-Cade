package game;

import game.map.ClassicPacmanMap;
import util.Direction;
import util.Vector2d;

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
  public long                               score           = 900_000_000;
  public int                                level           = 1;
  public int                                uiSize          = 100;

  public ClassicPacmanGameState ()
  {
    gameObjects = new ConcurrentLinkedDeque <>();
  }
}
