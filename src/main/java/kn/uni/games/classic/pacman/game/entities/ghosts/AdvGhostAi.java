package kn.uni.games.classic.pacman.game.entities.ghosts;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.entities.Ai;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvWaypointManager;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AdvGhostAi extends Ai
{
  public AdvGameConst.GhostNames name;
  public AdvGameConst.GhostMode  mode;
  public AdvGhostEntity          ghost;

  public AdvGhostAi (AdvGameConst.GhostNames name, AdvGhostEntity ghost)
  {
    this.name = name;
    this.ghost = ghost;
    this.mode = AdvGameConst.GhostMode.ENTER;
  }

  public AdvGameConst.GhostMode getMode ()
  {
    return mode;
  }

  public void setMode (AdvGameConst.GhostMode mode)
  {
    this.mode = mode;
  }

  public AdvGameConst.GhostNames getName ()
  {
    return name;
  }

  public void setName (AdvGameConst.GhostNames name)
  {
    this.name = name;
  }

  public AdvPacManEntity getNearestTargetEntity ()
  {
    List <AdvPacManEntity> possibleTargets = new ArrayList <>();

    gameState.layers.get(AdvGameState.Layer.ENTITIES.ordinal()).stream()
                    .filter(entity -> entity instanceof AdvPacManEntity)
                    .forEach(entity -> possibleTargets.add((AdvPacManEntity) entity));

    //get closest target to ghost
    AtomicReference <AdvPacManEntity> closestTarget = new AtomicReference <>();
    possibleTargets.forEach(target ->
    {
      if (closestTarget.get() == null)
        closestTarget.set(target);
      else if (target.absPos.subtract(ghost.absPos).length() < closestTarget.get().absPos.subtract(ghost.absPos).length())
        closestTarget.set(target);
    });

    return closestTarget.get();
  }

  public AdvGhostEntity getNextGhost (AdvGameConst.GhostNames name)
  {
    List <AdvGhostEntity> possibleTargets = new ArrayList <>();

    gameState.layers.get(AdvGameState.Layer.ENTITIES.ordinal()).stream()
                    .filter(entity -> entity instanceof AdvGhostEntity)
                    .filter(entity -> ( (AdvGhostEntity) entity ).ai.name == name)
                    .forEach(entity -> possibleTargets.add((AdvGhostEntity) entity));

    //get closest target to ghost
    AtomicReference <AdvGhostEntity> closestTarget = new AtomicReference <>();
    possibleTargets.forEach(target ->
    {
      if (closestTarget.get() == null)
        closestTarget.set(target);
      else if (target.absPos.subtract(ghost.absPos).length() < closestTarget.get().absPos.subtract(ghost.absPos).length())
        closestTarget.set(target);
    });

    return closestTarget.get();
  }

  public Vector2d getCurrentTargetPos ()
  {
    AdvPacManEntity target = getNearestTargetEntity();

    switch (name)
    {
      case BLINKY ->
      {
        //target is pacman
        return target.absPos;
      }
      case PINKY ->
      {
        //target is 5 tiles in front of pacman
        return target.absPos.add(target.facing.toVector().multiply(5 * AdvGameConst.tileSize));
      }
      case INKY ->
      {
        //target is 3 tiles in front of pacman, then mirrored to blinky
        return target.absPos.add(target.facing.toVector().multiply(3 * AdvGameConst.tileSize))
                            .subtract(getNextGhost(AdvGameConst.GhostNames.BLINKY).absPos);
      }
      case CLYDE ->
      {
        //target is pacman if more than 8 tiles away, otherwise away pacman
        if (target.absPos.subtract(ghost.absPos).length() > 8 * AdvGameConst.tileSize)
          return target.absPos;
        else
          return target.absPos.add(Objects.requireNonNull(Direction.getDirection(ghost, target)).opposite().toVector().multiply(8 * AdvGameConst.tileSize));
      }
      default ->
      {
        return null;
      }
    }
  }

  public Vector2d getModeTarget ()
  {
    switch (mode)
    {
      case CHASE, FRIGHTENED ->
      {
        return getCurrentTargetPos();
      }
      case SCATTER ->
      {
        return AdvWaypointManager.getWaypoint(name.toString() + "_SCATTER").absPos;
      }
      case EXIT, RETREAT ->
      {
        return AdvWaypointManager.getWaypoint("GhostPenOutside").absPos;
      }
      case ENTER ->
      {
        return AdvWaypointManager.getWaypoint("GhostPenInside").absPos;
      }
      default ->
      {
        return null;
      }
    }


  }

  public Direction getDirection ()
  {
    Vector2d target = getModeTarget();
    //all possible positions, unrelated if they are valid
    List <Vector2d> possiblePositions = new ArrayList <>();
    Arrays.stream(Direction.valuesCardinal()).toList()
          .forEach(direction -> possiblePositions.add(ghost.absPos.add(direction.toVector().multiply(AdvGameConst.tileSize))));
    //get the closest position to target
    Vector2d closestPosition = null;
    for (Vector2d possiblePosition : possiblePositions)
    {
      if (closestPosition == null)
        closestPosition = possiblePosition;
      else if (possiblePosition.subtract(target).length() < closestPosition.subtract(target).length())
        closestPosition = possiblePosition;
    }
    //get the direction to the closest position
    assert closestPosition != null;
    return Direction.getDirection(ghost.absPos, closestPosition);
  }
}
