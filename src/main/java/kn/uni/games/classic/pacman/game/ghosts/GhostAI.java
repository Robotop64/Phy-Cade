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
import java.util.Random;

public abstract class GhostAI
{

  private final Random                                random       = new Random(System.nanoTime());
  public        ClassicPacmanGameConstants.ghostNames name;
  protected     Color                                 borderColor;
  protected     Vector2d                              activeTarget = new Vector2d().cartesian(0, 0);
  protected     Vector2d                              chase;
  protected     Vector2d                              scatter;
  protected     Vector2d                              exitSpawn;

  @Deprecated
  public abstract Direction getNextDirection (ClassicPacmanGameState gameState);

  public abstract void setCasePos (ClassicPacmanGameState gameState, Ghost ghost);

  public void setMode (ClassicPacmanGameConstants.mode mode, Ghost ghost)
  {
    switch (mode)
    {
      case CHASE:
        activeTarget = chase;
        ghost.currentMode = mode;
        break;
      case SCATTER:
        activeTarget = scatter;
        ghost.currentMode = mode;
        break;
      case FRIGHTENED:
        ghost.currentMode = mode;
        break;
      case EXIT:
        activeTarget = exitSpawn;
        ghost.currentMode = mode;
        break;
    }
  }

  public Direction nextDirection2 (ClassicPacmanGameState gameState, Ghost ghost, ClassicPacmanGameConstants.mode mode)
  {
    if (mode != ClassicPacmanGameConstants.mode.FRIGHTENED)
    {//find all valid tiles
      List <PacmanMapTile> possibleTiles =
          Arrays.stream(Direction.values())
                .filter(d -> d != ghost.direction.opposite())
                .map(Direction::toVector)
                .map(vec -> ghost.currentTile.neighbors.get(vec))
                .filter(Objects::nonNull)
                //              &&
                .filter(tile -> PacmanMapTile.walkable.contains(tile.type))
                .filter(tile -> !tile.type.equals(PacmanMapTile.Type.door) || ghost.canUseDoor)
                //                .filter(tile -> tile.type.equals(PacmanMapTile.Type.door) || !ghost.canUseDoor)
                .toList();

      //visual feedback of possible tiles
      if (DebugDisplay.getDebug(gameState).enabled)
      {
        gameState.map.tiles.forEach((vec, tile) -> tile.color = Color.black);
        possibleTiles.stream().forEach(tile -> tile.color = Color.blue);
      }

      //sort tiles by distance to target, target = indexed at 0
      possibleTiles =
          possibleTiles.stream()
                       .sorted((a, b) ->
                       {
                         Vector2d aVec  = a.pos.subtract(ghost.ai.activeTarget);
                         Vector2d bVec  = b.pos.subtract(ghost.ai.activeTarget);
                         double   aDist = aVec.lenght();
                         double   bDist = bVec.lenght();
                         if (aDist == bDist)
                         {
                           return 0;
                         }
                         else if (aDist < bDist)
                         {
                           return -1;
                         }
                         else
                         {
                           return 1;
                         }
                       })
                       .toList();

      //get direction of target tile
      Vector2d goTo;
      //      if possibleTiles available get best, else return old direction
      if (possibleTiles.size() > 0)
      {
        goTo = possibleTiles.get(0).pos.subtract(ghost.currentTile.pos);
        Direction newDirection = goTo.divide(goTo.lenght()).toDirection();
        ghost.direction = newDirection;
        return newDirection;
      }
      else
      {
        return ghost.direction;
      }
    }
    else
    {
      Direction currentDirection = Direction.up;
      if (random.nextFloat() < .25)
      {
        currentDirection = Direction.values()[random.nextInt(4)];
      }
      return currentDirection;
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
