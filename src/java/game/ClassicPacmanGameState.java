package game;

import util.Direction;

import java.util.concurrent.ConcurrentLinkedDeque;

public class ClassicPacmanGameState
{
  public ConcurrentLinkedDeque <GameObject> gameObjects;
  public boolean                            running;
  public long                               currentTick;
  public long                               lastTickTime;
  public Direction                          playerDirection = Direction.right;
  public boolean                            logging;

  public ClassicPacmanGameState ()
  {
    gameObjects = new ConcurrentLinkedDeque <>();
  }
}
