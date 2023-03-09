//package kn.uni.games.classic.pacman.game.entities.ghostAi;
//
//import kn.uni.games.classic.pacman.game.entities.GhostEntity;
//import kn.uni.games.classic.pacman.game.entities.PacmanEntity;
//import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
//import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameConstants;
//import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
//import kn.uni.games.classic.pacman.game.objects.PacManMapTile;
//import kn.uni.util.Direction;
//import kn.uni.util.Vector2d;
//
//import java.awt.Color;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//
//public abstract class GhostAI
//{
//  //general
//  public    ClassicPacmanGameConstants.ghostNames name;
//  public    Color                                 borderColor;
//  //possible targets
//  public    Vector2d                              activeTarget = new Vector2d().cartesian(0, 0);
//  protected Vector2d                              chasePos;
//  protected Vector2d                              scatterPos;
//  protected Vector2d                              escapePos;
//  protected Vector2d                              spawnPos;
//  protected Vector2d                              exitSpawnPos;
//
//  @Deprecated
//  public abstract Direction getNextDirection (ClassicPacmanGameState gameState);
//
//  public abstract void setCasePos (ClassicPacmanGameState gameState, GhostEntity ghost);
//
//  public void setMode (ClassicPacmanGameConstants.mode mode, GhostEntity ghost)
//  {
//    switch (mode)
//    {
//      case CHASE ->
//      {
//        activeTarget = chasePos;
//        ghost.currentMode = mode;
//      }
//      case SCATTER ->
//      {
//        activeTarget = scatterPos;
//        ghost.currentMode = mode;
//      }
//      case FRIGHTENED ->
//      {
//        activeTarget = escapePos;
//        ghost.currentMode = mode;
//      }
//      case EXIT, RETREAT ->
//      {
//        activeTarget = exitSpawnPos;
//        ghost.currentMode = mode;
//      }
//      case ENTER ->
//      {
//        activeTarget = spawnPos;
//        ghost.currentMode = mode;
//      }
//    }
//  }
//
//  public void nextDirection2 (ClassicPacmanGameState gameState, GhostEntity ghost, ClassicPacmanGameConstants.mode mode)
//  {
//    //find all valid tiles
//    List <PacManMapTile> possibleTiles =
//        Arrays.stream(Direction.valuesCardinal())
//              .filter(d -> d != ghost.direction.opposite())
//              .map(Direction::toVector)
//              .map(vec -> ghost.currentTile.neighbors.get(vec))
//              .filter(Objects::nonNull)
//              .filter(tile -> PacManMapTile.walkable.contains(tile.type))
//              .filter(tile -> !tile.type.equals(PacManMapTile.Type.door) || ( ghost.canUseDoor && ghost.free ))
//              .toList();
//
//    //visual feedback of possible tiles
//    if (DebugDisplay.getDebugDisplay(gameState).enabled)
//    {
//      gameState.map.tiles.forEach((vec, tile) -> tile.color = Color.black);
//      possibleTiles.forEach(tile -> tile.color = Color.blue);
//    }
//
//    //sort tiles by distance to target, chosen target is indexed at 0
//    possibleTiles =
//        possibleTiles.stream()
//                     .sorted((a, b) ->
//                     {
//                       Vector2d aVec  = a.pos.subtract(ghost.ai.activeTarget);
//                       Vector2d bVec  = b.pos.subtract(ghost.ai.activeTarget);
//                       double   aDist = aVec.length();
//                       double   bDist = bVec.length();
//                       return Double.compare(aDist, bDist);
//                     })
//                     .toList();
//
//    //get direction of target tile
//    Vector2d goTo;
//    //if possibleTiles available get best, else return old direction
//    if (possibleTiles.size() > 0)
//    {
//      if (mode != ClassicPacmanGameConstants.mode.FRIGHTENED)
//      {
//        //reduce distance to target
//        goTo = possibleTiles.get(0).center.subtract(ghost.currentTile.center);
//      }
//      else
//      {
//        //increase distance to target
//        goTo = possibleTiles.get(possibleTiles.size() - 1).center.subtract(ghost.currentTile.center);
//      }
//      //set new ghost direction
//      ghost.direction = goTo.divide(goTo.length()).toDirection();
//    }
//    else
//    {
//      //turn 180° if no possible tiles available
//      ghost.direction = ghost.direction.opposite();
//    }
//
//  }
//
//  public List <Vector2d> getPacmanPos (ClassicPacmanGameState gameState)
//  {
//    List <Vector2d> pacPos = new ArrayList <>();
//
//    gameState.gameObjects.stream()
//                         .filter(o -> o instanceof PacmanEntity)
//                         .map(o -> (PacmanEntity) o)
//                         .forEach(o -> pacPos.add(o.absPos));
//    return pacPos;
//  }
//
//  @SuppressWarnings({ "typo", "SpellCheckingInspection" })
//  public List <Vector2d> getBlinkyPos (ClassicPacmanGameState gameState)
//  {
//    List <Vector2d> blinkyPos = new ArrayList <>();
//
//    gameState.gameObjects.stream()
//                         .filter(o -> o instanceof GhostEntity)
//                         .map(o -> (GhostEntity) o)
//                         .filter(o -> o.ai.name.equals(ClassicPacmanGameConstants.ghostNames.BLINKY))
//                         .forEach(o -> blinkyPos.add(o.pos));
//    return blinkyPos;
//  }
//
//}
