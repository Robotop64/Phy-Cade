package kn.uni.games.classic.pacman.game.entities.ghosts;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.entities.Ai;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvWaypointManager;
import kn.uni.games.classic.pacman.game.map.AdvPacManTile;
import kn.uni.util.Direction;
import kn.uni.util.PrettyPrint;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AdvGhostAi extends Ai
{
  public AdvGameConst.GhostNames name;
  public AdvGameConst.GhostMode  mode;
  public AdvGhostEntity          ghost;
  public AdvPacManTile[]         memory;

  public AdvGhostAi (AdvGameConst.GhostNames name, AdvGhostEntity ghost)
  {
    this.name = name;
    this.ghost = ghost;
    this.gameState = ghost.gameState;
    this.mode = AdvGameConst.GhostMode.EXIT;
    this.memory = new AdvPacManTile[2];
  }

  public AdvGameConst.GhostMode getMode ()
  {
    return mode;
  }

  public void setMode (AdvGameConst.GhostMode mode)
  {
    PrettyPrint.startGroup(PrettyPrint.Type.Event, "Action");
    PrettyPrint.bullet("Ghost " + name + " changed mode from " + this.mode + " to " + mode);
    PrettyPrint.endGroup();
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
    List <AdvPacManEntity> possibleTargets = ghost.gameState.players;

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

  public Vector2d getCurrentTargetPos (AdvGameConst.GhostNames name)
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
        Vector2d offset = target.absPos.add(target.facing.toVector().multiply(3 * AdvGameConst.tileSize));
        Vector2d targetToOffset = offset.subtract(getNextGhost(AdvGameConst.GhostNames.BLINKY).absPos);
        //target is 3 tiles in front of pacman, then mirrored to blinky
        return offset.add(targetToOffset);
      }
      case CLYDE ->
      {
        //target is pacman if more than 8 tiles away, otherwise away from pacman
        if (target.absPos.subtract(ghost.absPos).length() > 8 * AdvGameConst.tileSize)
          return target.absPos;
        else
          return target.absPos.add(Objects.requireNonNull(target.absPos.subtract(ghost.absPos).approxDirection()).opposite().toVector().multiply(8 * AdvGameConst.tileSize));
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
      case CHASE ->
      {
        return getCurrentTargetPos(name);
      }
      case FRIGHTENED ->
      {
        return getCurrentTargetPos(AdvGameConst.GhostNames.BLINKY);
      }
      case SCATTER ->
      {
        return AdvWaypointManager.getInstance(ghost.gameState).get().getWaypoint(name.toString() + "_SCATTER").absPos;
      }
      case EXIT, RETREAT ->
      {
        return AdvWaypointManager.getInstance(ghost.gameState).get().getWaypoint("GhostPenOutside").absPos;
      }
      case ENTER ->
      {
        return AdvWaypointManager.getInstance(ghost.gameState).get().getWaypoint("GhostPenInside").absPos;
      }
      default ->
      {
        return null;
      }
    }
  }

  public Direction getDirection (AdvPacManTile current, List <AdvPacManTile> possibleTiles)
  {
    //region tile memory
    //set initial tile
    if (memory[0] == null)
      memory[0] = current;
    //update previous tile
    if (memory[1] != null && current != memory[1])
      memory[0] = memory[1];
    //update current tile
    memory[1] = current;
    //endregion

    //turn 180° if no possible tiles are available
    if (possibleTiles.size() == 0)
      return ghost.facing.opposite();


    Vector2d targetPos = getModeTarget();

    //sort possible tiles by distance to target
    //the closest tile is first, furthest tile is last
    possibleTiles =
        possibleTiles.stream()
                     .sorted((a, b) ->
                     {
                       Vector2d aVec  = a.absPos.subtract(targetPos);
                       Vector2d bVec  = b.absPos.subtract(targetPos);
                       double   aDist = aVec.length();
                       double   bDist = bVec.length();
                       return Double.compare(aDist, bDist);
                     })
                     .toList();

    ghost.tagManager.remove("ai");
    ghost.tagManager.addInfo("target",
        new ArrayList <>(List.of("ai")),
        new Object[]{ Color.GREEN, targetPos });
    ghost.tagManager.addInfo("bestTile",
        new ArrayList <>(List.of("ai")),
        new Object[]{ Color.CYAN, possibleTiles.get(0).absPos });


    List <AdvPacManTile> finalPossibleTiles = possibleTiles;

    return current.neighbours.keySet().stream()
                             //filter out all directions that don't lead to a possible tile
                             .filter(direction -> current.neighbours.get(direction) != null)
                             //filter out directions that lead to the previous tile
                             .filter(direction -> current.neighbours.get(direction) != memory[0])
                             //filter out all directions that don't lead to the best tile for the current mode
                             .filter(direction ->
                             {
                               switch (mode)
                               {
                                 case FRIGHTENED ->
                                 {
                                   return current.neighbours.get(direction) == finalPossibleTiles.get(finalPossibleTiles.size() - 1);
                                 }
                                 default ->
                                 {
                                   return current.neighbours.get(direction) == finalPossibleTiles.get(0);
                                 }
                               }
                             })
                             .findFirst()
                             .orElseGet(() ->
                             {
                               //if no possible tile is found, turn 180°
                               return ghost.facing.opposite();
                             });
  }
}
