package game;

import util.Direction;

import java.util.concurrent.ConcurrentLinkedDeque;

public class GameState
{
  public ConcurrentLinkedDeque <GameObject> gameObjects;
  public boolean                            running;
  public long                               currentTick;
  public long                               lastTickTime;
  public Direction                          playerDirection;
  public boolean                            logging;

  public GameState ()
  {
    gameObjects = new ConcurrentLinkedDeque <>();
  }
}
