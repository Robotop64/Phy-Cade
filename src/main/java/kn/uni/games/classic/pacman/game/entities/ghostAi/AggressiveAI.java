package kn.uni.games.classic.pacman.game.entities.ghostAi;

import kn.uni.games.classic.pacman.game.entities.GhostEntity;
import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameConstants;
import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.objects.PacManMapTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.Color;

public class AggressiveAI extends GhostAI
{

  public AggressiveAI (ClassicPacmanGameState gameState)
  {
    borderColor = Color.red;
    //assign the important static position
    scatterPos = new Vector2d().cartesian(gameState.map.width, 0);
    spawnPos = gameState.map.getTilesOfType(PacManMapTile.Type.ghostSpawn).get(0).pos;
    exitSpawnPos = gameState.map.getTilesOfType(PacManMapTile.Type.ghostExit).get(0).pos;

    name = ClassicPacmanGameConstants.ghostNames.BLINKY;
  }

  @Override
  public Direction getNextDirection (ClassicPacmanGameState gameState) { return null; }

  @Override
  public void setCasePos (ClassicPacmanGameState gameState, GhostEntity ghost)
  {
    //update the target positions
    chasePos = getPacmanPos(gameState).get(0);
    escapePos = chasePos;

    if (ghost.currentMode == ClassicPacmanGameConstants.mode.CHASE)
      activeTarget = chasePos;

    if (ghost.currentMode == ClassicPacmanGameConstants.mode.FRIGHTENED)
      activeTarget = escapePos;
  }


}
