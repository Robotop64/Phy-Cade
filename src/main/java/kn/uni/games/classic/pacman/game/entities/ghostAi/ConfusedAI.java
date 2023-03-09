//package kn.uni.games.classic.pacman.game.entities.ghostAi;
//
//import kn.uni.games.classic.pacman.game.entities.GhostEntity;
//import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameConstants;
//import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
//import kn.uni.games.classic.pacman.game.objects.PacManMapTile;
//import kn.uni.util.Direction;
//import kn.uni.util.Vector2d;
//
//import java.awt.Color;
//
//public class ConfusedAI extends GhostAI
//{
//
//  public ConfusedAI (ClassicPacmanGameState gameState)
//  {
//    borderColor = Color.orange;
//    //assign the important static position
//    scatterPos = new Vector2d().cartesian(0, gameState.map.height);
//    spawnPos = gameState.map.getTilesOfType(PacManMapTile.Type.ghostSpawn).get(0).pos;
//    exitSpawnPos = gameState.map.getTilesOfType(PacManMapTile.Type.ghostExit).get(0).pos;
//
//    name = ClassicPacmanGameConstants.ghostNames.CLYDE;
//  }
//
//  @Override
//  public Direction getNextDirection (ClassicPacmanGameState gameState)
//  {
//    return null;
//  }
//
//  @Override
//  public void setCasePos (ClassicPacmanGameState gameState, GhostEntity ghost)
//  {
//    Vector2d pacPos   = getPacmanPos(gameState).get(0);
//    double   distance = 8 * gameState.map.tileSize;
//
//    //update the target positions
//    escapePos = pacPos;
//
//    if (pacPos.subtract(ghost.pos).length() > distance)
//    {
//      chasePos = pacPos;
//    }
//    else
//    {
//      chasePos = scatterPos;
//    }
//
//    if (ghost.currentMode == ClassicPacmanGameConstants.mode.CHASE) activeTarget = chasePos;
//
//    if (ghost.currentMode == ClassicPacmanGameConstants.mode.FRIGHTENED) activeTarget = escapePos;
//  }
//}
