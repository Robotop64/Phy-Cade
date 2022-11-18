package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PacmanMapTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.Color;

public class ConfusedAI extends GhostAI
{

  public ConfusedAI (ClassicPacmanGameState gameState)
  {
    borderColor = Color.orange;
    scatter = new Vector2d().cartesian(0, gameState.map.height);
    exitSpawn = gameState.map.getTilesOfType(PacmanMapTile.Type.ghostExit).get(0).pos;

    name = ClassicPacmanGameConstants.ghostNames.CLYDE;
  }

  @Override
  public Direction getNextDirection (ClassicPacmanGameState gameState)
  {
    return null;
  }

  @Override
  public void setCasePos (ClassicPacmanGameState gameState, Ghost ghost)
  {
    Vector2d pacPos   = getPacmanPos(gameState).get(0);
    double   distance = 8 * gameState.map.tileSize;

    if (pacPos.subtract(ghost.pos).lenght() > distance)
    {
      chase = pacPos;
    }
    else
    {
      chase = scatter;
    }
    if (ghost.currentMode == ClassicPacmanGameConstants.mode.CHASE)
    {
      activeTarget = chase;
    }
  }


}
