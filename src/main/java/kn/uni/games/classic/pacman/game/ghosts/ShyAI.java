package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PacmanMapTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.Color;

public class ShyAI extends GhostAI
{

  public ShyAI (ClassicPacmanGameState gameState)
  {
    borderColor = Color.pink;
    scatter = new Vector2d().cartesian(0, 0);
    exitSpawn = gameState.map.getTilesOfType(PacmanMapTile.Type.ghostExit).get(0).pos;

    name = ClassicPacmanGameConstants.ghostNames.PINKY;
  }

  @Override
  public Direction getNextDirection (ClassicPacmanGameState gameState)
  {
    return null;
  }

  @Override
  public void setCasePos (ClassicPacmanGameState gameState, Ghost ghost)
  {
    chase = getPacmanPos(gameState).get(0).add(gameState.playerDirection.toVector().multiply(5 * gameState.map.tileSize));

    escape = getPacmanPos(gameState).get(0);

    if (ghost.currentMode == ClassicPacmanGameConstants.mode.CHASE)
    {
      activeTarget = chase;
    }

    if (ghost.currentMode == ClassicPacmanGameConstants.mode.FRIGHTENED){
      activeTarget = escape;
    }
  }


}
