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
//public class ShyAI extends GhostAI
//{
//
//  public ShyAI (ClassicPacmanGameState gameState)
//  {
//    borderColor = Color.pink;
//    //assign the important static position
//    scatterPos = new Vector2d().cartesian(0, 0);
//    spawnPos = gameState.map.getTilesOfType(PacManMapTile.Type.ghostSpawn).get(0).pos;
//    exitSpawnPos = gameState.map.getTilesOfType(PacManMapTile.Type.ghostExit).get(0).pos;
//
//    name = ClassicPacmanGameConstants.ghostNames.PINKY;
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
//    //update the target positions
//    chasePos = getPacmanPos(gameState).get(0).add(gameState.playerDirection.toVector().multiply(5 * gameState.map.tileSize));
//
//    escapePos = getPacmanPos(gameState).get(0);
//
//    if (ghost.currentMode == ClassicPacmanGameConstants.mode.CHASE) activeTarget = chasePos;
//
//    if (ghost.currentMode == ClassicPacmanGameConstants.mode.FRIGHTENED) activeTarget = escapePos;
//  }
//}
