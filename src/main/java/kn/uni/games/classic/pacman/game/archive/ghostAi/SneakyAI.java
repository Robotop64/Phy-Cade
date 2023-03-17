//package kn.uni.games.classic.pacman.game.archive.ghostAi;
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
//public class SneakyAI extends GhostAI
//{
//
//  public SneakyAI (ClassicPacmanGameState gameState)
//  {
//    borderColor = Color.cyan;
//    //assign the important static position
//    scatterPos = new Vector2d().cartesian(gameState.map.width, gameState.map.height);
//    spawnPos = gameState.map.getTilesOfType(PacManMapTile.Type.ghostSpawn).get(0).pos;
//    exitSpawnPos = gameState.map.getTilesOfType(PacManMapTile.Type.ghostExit).get(0).pos;
//
//    name = ClassicPacmanGameConstants.ghostNames.INKY;
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
//    Vector2d pacOffset = getPacmanPos(gameState).get(0).add(gameState.playerDirection.toVector().multiply(3 * gameState.map.tileSize));
//    Vector2d rad       = pacOffset.subtract(getBlinkyPos(gameState).get(0));
//
//    //update the target positions
//    chasePos = pacOffset.add(rad);
//
//    escapePos = getPacmanPos(gameState).get(0);
//
//    if (ghost.currentMode == ClassicPacmanGameConstants.mode.CHASE) activeTarget = chasePos;
//
//    if (ghost.currentMode == ClassicPacmanGameConstants.mode.FRIGHTENED) activeTarget = escapePos;
//  }
//}
