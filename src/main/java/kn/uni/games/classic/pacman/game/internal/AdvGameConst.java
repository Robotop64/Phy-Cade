package kn.uni.games.classic.pacman.game.internal;

import java.awt.Color;
import java.util.Map;

public class AdvGameConst
{
  //region entity constants
  public static final double pacmanSpeed = 6;
  public static final double ghostRadius = 31;
  public static final double ghostSpeed  = 4;
  public static final int    entityHp    = 1;
  //endregion


  //radius of collision box by entity type
  public static final Map <String, Double> hitBoxes   = Map.of(
      "AdvPacManEntity", 0.5,
      "AdvGhostEntity", 0.5,
      "PelletItem", 1 / 3.,
      "PPelletItem", 1 / 3.,
      "FruitItem", 1 / 3.
  );
  //color of score text by type
  public static final Color[]              fruitColor = { Color.RED.darker(), Color.RED.brighter(), Color.ORANGE.darker(), Color.RED, Color.green.darker(), Color.BLUE.brighter(), Color.yellow.darker(), Color.ORANGE.brighter().brighter() };
  //endregion
  //score of fruit by type
  public static final int[]                fruitScore = { 100, 300, 500, 700, 1000, 2000, 3000, 5000 };
  //array deciding which fruit is will spawn, level = index+1
  public static final int[]                fruitSpawn = { 1, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8 };
  //region general constants
  public static       int                  tileSize   = 0;

  //region fruit stuff
  //available fruit types
  public enum FruitType
  { cherry, strawberry, orange, apple, melon, galaxian, bell, key }
  //endregion

  //region ghost stuff
  //available ghost names
  public enum GhostNames
  { blinky, pinky, inky, clyde }
  //endregion

  //available ghost modes
  public enum GhostMode
  { CHASE, SCATTER, FRIGHTENED, EXIT, RETREAT, ENTER }
}
