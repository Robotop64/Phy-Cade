package kn.uni.games.classic.pacman.game.internal;

import java.awt.Color;

public class AdvGameConst
{
  //region entity constants
  public static final double pacmanSpeed = 6;
  public static final double ghostRadius = 31;
  public static final double ghostSpeed  = 4;
  public static final int    entityHp    = 1;
  //endregion
  //color of score text by type
  public static final Color[] fruitColor = { Color.RED.darker(), Color.RED.brighter(), Color.ORANGE.darker(), Color.RED, Color.green.darker(), Color.BLUE.brighter(), Color.yellow.darker(), Color.ORANGE.brighter().brighter() };
  //score of fruit by type
  public static final int[]   fruitScore = { 100, 300, 500, 700, 1000, 2000, 3000, 5000 };
  //array deciding which fruit is will spawn, level = index+1
  public static final int[]   fruitSpawn = { 1, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8 };
  //region fruit map
  //available fruit types
  public enum FruitType
  { cherry, strawberry, orange, apple, melon, galaxian, bell, key }
  //endregion

  //region ghost map
  //available ghost names
  public enum GhostNames
  { blinky, pinky, inky, clyde }

  //available ghost modes
  public enum GhostMode
  { CHASE, SCATTER, FRIGHTENED, EXIT, RETREAT, ENTER }
  //endregion
}
