package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PacmanMapTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.Color;

public class AggressiveAI extends GhostAI
{

  public AggressiveAI (ClassicPacmanGameState gameState)
  {
    borderColor = Color.red;
    scatterPos = new Vector2d().cartesian(gameState.map.width, 0);
    spawnPos = gameState.map.getTilesOfType(PacmanMapTile.Type.ghostSpawn).get(0).pos;
    exitSpawnPos = gameState.map.getTilesOfType(PacmanMapTile.Type.ghostExit).get(0).pos;

    name = ClassicPacmanGameConstants.ghostNames.BLINKY;
  }

  @Override
  public Direction getNextDirection (ClassicPacmanGameState gameState)
  {
    return null;
  }

  @Override
  public void setCasePos (ClassicPacmanGameState gameState, Ghost ghost)
  {
    chasePos = getPacmanPos(gameState).get(0);
    escapePos = chasePos;
    if (ghost.currentMode == ClassicPacmanGameConstants.mode.CHASE)
    {
      activeTarget = chasePos;
    }

    if (ghost.currentMode == ClassicPacmanGameConstants.mode.FRIGHTENED)
    {
      activeTarget = escapePos;
    }

  }


}
