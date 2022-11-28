package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.util.Direction;

import java.awt.Color;
import java.util.Random;

public class RandomWalkAI extends GhostAI
{
  private Direction currentDirection = Direction.up;
  private final Random    random           = new Random(System.nanoTime());

  public RandomWalkAI ()
  {
    borderColor = Color.orange;
  }

  @Override
  public Direction getNextDirection (ClassicPacmanGameState gameState)
  {
    if (random.nextFloat() < .25)
    {
      currentDirection = Direction.values()[random.nextInt(4)];
    }
    return currentDirection;
  }

  @Override
  public void setCasePos (ClassicPacmanGameState gameState, Ghost ghost) { }
}
