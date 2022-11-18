package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PacmanMapTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.Color;

public class StalkAI extends GhostAI
{

  public StalkAI (ClassicPacmanGameState gameState)
  {
    borderColor = Color.red;
    scatter = new Vector2d().cartesian(gameState.map.width, 0);
    exitSpawn = gameState.map.getTilesOfType(PacmanMapTile.Type.ghostExit).get(0).pos;
  }

  @Override
  public Direction getNextDirection (ClassicPacmanGameState gameState)
  {
    return null;
  }

  @Override
  public void setCasePos (ClassicPacmanGameState gameState, Ghost ghost)
  {
    chase = getPacmanPos(gameState).get(0);
    if (ghost.currentMode == GhostAI.mode.CHASE)
    {
      activeTarget = chase;
    }
  }


}
