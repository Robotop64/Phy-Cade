package kn.uni.games.classic.pacman.game.internal.tracker;

import kn.uni.util.fileRelated.Config.Config;

import java.awt.Color;
import java.util.Map;

public class AdvGameConst
{
  //radius of collision box by entity type
  public static final Map <String, Double>    hitBoxes       = Map.of(
      "AdvPacManEntity", 1 / 2.,
      "AdvGhostEntity", 1 / 2.,
      "PelletItem", 1 / 4.,
      "PPelletItem", 1 / 4.,
      "FruitItem", 2 / 5.,
      "Blocker", ( 1 / 2. ) - 0.05,
      "Teleporter", 1 / 4.,
      "Waypoint", 1 / 10.
  );
  //buffer
  //region fruit stuff
  //score of fruit by type
  public static final int[]                   fruitScore     = { 100, 300, 500, 700, 1000, 2000, 3000, 5000 };
  //buffer
  //array deciding which fruit is will spawn, level = index+1
  public static final int[]                   fruitSpawn     = { 1, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8 };
  //color of score text by type
  public static final Color[]                 fruitColor     = { Color.RED.darker(), Color.RED.brighter(), Color.ORANGE.darker(), Color.RED, Color.green.darker(), Color.BLUE.brighter(), Color.yellow.darker(), Color.ORANGE.brighter().brighter() };
  public static final Map <ItemType, Integer> itemWorth      = Map.of(
      ItemType.PELLET, 10,
      ItemType.PPELLET, 50,
      ItemType.FRUIT, 0
  );
  //region entity stuff
  public static       double                  pacmanSpeed    = 0;
  public static       double                  ghostSpeed     = 0;
  //endregion
  public static       int                     playerHp       = 0;
  public static       int                     ghostHp        = 0;
  public static       int                     pointsToLife   = 0;
  public static       double                  portalDelay    = 0;
  public static       double                  portalCooldown = 0;
  //region general constants
  public static       int                     tileSize       = 0;
  //buffer
  public static       int                     tps            = 120;
  //endregion
  public static       int                     fps            = 60;

  public static void init ()
  {
    pacmanSpeed = (double) Config.getCurrent("Gameplay/PacMan/StartSpeed");
    ghostSpeed = 4 / pacmanSpeed;
    playerHp = (int) (double) Config.getCurrent("Gameplay/PacMan/PlayerHP");
    ghostHp = (int) (double) Config.getCurrent("Gameplay/PacMan/GhostHP");
    pointsToLife = (int) (double) Config.getCurrent("Gameplay/PacMan/PointsToLife");
    portalDelay = (double) Config.getCurrent("Gameplay/PacMan/PortalDelay");
    portalCooldown = (double) Config.getCurrent("Gameplay/PacMan/PortalCooldown");
  }

  public enum EntityType
  { PACMAN, GHOST }
  //endregion


  //buffer


  //available fruit types
  public enum FruitType
  { cherry, strawberry, orange, apple, melon, galaxian, bell, key }

  //region item stuff
  public enum ItemType
  { PELLET, PPELLET, FRUIT }
  //endregion


  //buffer


  //region ghost stuff
  //available ghost names
  public enum GhostNames
  { BLINKY, PINKY, INKY, CLYDE }

  //available ghost modes
  public enum GhostMode
  { CHASE, SCATTER, FRIGHTENED, EXIT, RETREAT, ENTER }
  //endregion

}
