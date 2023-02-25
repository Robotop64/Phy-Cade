package kn.uni.games.classic.pacman.game.entities.ghostAi;

import kn.uni.games.classic.pacman.game.entities.GhostEntity;
import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
import kn.uni.util.Direction;

import java.awt.Color;
import java.util.Random;

@SuppressWarnings("unused")
public class RandomWalkAI extends GhostAI
{
  private final Random    random           = new Random(System.nanoTime());
  private       Direction currentDirection = Direction.up;

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
  public void setCasePos (ClassicPacmanGameState gameState, GhostEntity ghost) { }
}
