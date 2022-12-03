package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PacmanMapTile;
import kn.uni.games.classic.pacman.game.PacmanObject;
import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class GhostAI
{
  public    ClassicPacmanGameConstants.ghostNames name;
  protected Color                                 borderColor;
  protected Vector2d                              activeTarget = new Vector2d().cartesian(0, 0);
  protected Vector2d                              chasePos;
  protected Vector2d                              scatterPos;
  protected Vector2d                              escapePos;
  protected Vector2d                              spawnPos;
  protected Vector2d                              exitSpawnPos;

  @Deprecated
  public abstract Direction getNextDirection (ClassicPacmanGameState gameState);

  public abstract void setCasePos (ClassicPacmanGameState gameState, Ghost ghost);

  public void setMode (ClassicPacmanGameConstants.mode mode, Ghost ghost)
  {
    switch (mode)
    {
      case CHASE ->
      {
        activeTarget = chasePos;
        ghost.currentMode = mode;
      }
      case SCATTER ->
      {
        activeTarget = scatterPos;
        ghost.currentMode = mode;
      }
      case FRIGHTENED ->
      {
        activeTarget = escapePos;
        ghost.currentMode = mode;
      }
      case EXIT, RETREAT ->
      {
        activeTarget = exitSpawnPos;
        ghost.currentMode = mode;
      }
      case ENTER ->
      {
        activeTarget = spawnPos;
        ghost.currentMode = mode;
      }
    }
  }

  public void nextDirection2 (ClassicPacmanGameState gameState, Ghost ghost, ClassicPacmanGameConstants.mode mode)
  {


    //find all valid tiles
    List <PacmanMapTile> possibleTiles =
        Arrays.stream(Direction.values())
              .filter(d -> d != ghost.direction.opposite())
              .map(Direction::toVector)
              .map(vec -> ghost.currentTile.neighbors.get(vec))
              .filter(Objects::nonNull)
              .filter(tile -> PacmanMapTile.walkable.contains(tile.type))
              .filter(tile -> !tile.type.equals(PacmanMapTile.Type.door) || ghost.canUseDoor)
              .toList();

    //visual feedback of possible tiles
    if (DebugDisplay.getDebugDisplay(gameState).enabled)
    {
      gameState.map.tiles.forEach((vec, tile) -> tile.color = Color.black);
      possibleTiles.forEach(tile -> tile.color = Color.blue);
    }

    //sort tiles by distance to target, target = indexed at 0
    possibleTiles =
        possibleTiles.stream()
                     .sorted((a, b) ->
                     {
                       Vector2d aVec  = a.pos.subtract(ghost.ai.activeTarget);
                       Vector2d bVec  = b.pos.subtract(ghost.ai.activeTarget);
                       double   aDist = aVec.length();
                       double   bDist = bVec.length();
                       return Double.compare(aDist, bDist);
                     })
                     .toList();


    //get direction of target tile
    Vector2d goTo;
    //      if possibleTiles available get best, else return old direction
    if (possibleTiles.size() > 0)
    {
      if (mode != ClassicPacmanGameConstants.mode.FRIGHTENED)
      {
        //reduce distance to target
        goTo = possibleTiles.get(0).center.subtract(ghost.currentTile.center);
      }
      else
      {
        //increase distance to target
        goTo = possibleTiles.get(possibleTiles.size() - 1).center.subtract(ghost.currentTile.center);
      }
      ghost.direction = goTo.divide(goTo.length()).toDirection();
    }
    else
    {
      ghost.direction = ghost.direction.opposite();
    }

  }

  public List <Vector2d> getPacmanPos (ClassicPacmanGameState gameState)
  {
    List <Vector2d> pacPos = new ArrayList <>();

    gameState.gameObjects.stream()
                         .filter(o -> o instanceof PacmanObject)
                         .map(o -> (PacmanObject) o)
                         .forEach(o -> pacPos.add(o.pos));
    return pacPos;
  }

  @SuppressWarnings({ "typo", "SpellCheckingInspection" })
  public List <Vector2d> getBlinkyPos (ClassicPacmanGameState gameState)
  {
    List <Vector2d> blinkyPos = new ArrayList <>();

    gameState.gameObjects.stream()
                         .filter(o -> o instanceof Ghost)
                         .map(o -> (Ghost) o)
                         .filter(o -> o.ai.name.equals(ClassicPacmanGameConstants.ghostNames.BLINKY))
                         .forEach(o -> blinkyPos.add(o.pos));
    return blinkyPos;
  }

}
