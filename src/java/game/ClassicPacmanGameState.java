package game;

import game.map.ClassicPacmanMap;
import util.Direction;
import util.Vector2d;

import java.util.concurrent.ConcurrentLinkedDeque;

public class ClassicPacmanGameState
{
  public int                                tps             = 165;
  public ConcurrentLinkedDeque <GameObject> gameObjects;
  public boolean                            running;
  public long                               currentTick;
  public long                               lastTickTime;
  public Direction                          playerDirection = Direction.left;
  public boolean                            logging;
  public Vector2d                           size;
  public ClassicPacmanMap                   map;
  public long                               score;

  public ClassicPacmanGameState ()
  {
    gameObjects = new ConcurrentLinkedDeque <>();
  }
}
