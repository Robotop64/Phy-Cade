package kn.uni.games.classic.pacman.game;

import java.awt.Color;
import java.util.Map;

public class ClassicPacmanGameConstants
{
  public static final double pacmanSpeed = 6;
  public static final double ghostRadius = 31;

  //how many points a fruit gives
  public static final Map <Collectables, Integer> collectionPoints = Map.of(
      Collectables.coin, 10,
      Collectables.powerUp, 50,
      Collectables.cherry, 100,
      Collectables.strawberry, 300,
      Collectables.orange, 500,
      Collectables.apple, 700,
      Collectables.melon, 1000,
      Collectables.galaxian, 2000,
      Collectables.bell, 3000,
      Collectables.key, 5000
  );

  //what color the score text should be
  public static final Map <Collectables, Color> collectionColor = Map.of(
      Collectables.coin, Color.yellow,
      Collectables.powerUp, Color.orange,
      Collectables.cherry, Color.red,
      Collectables.strawberry, Color.red,
      Collectables.orange, Color.orange,
      Collectables.apple, Color.red,
      Collectables.melon, Color.green,
      Collectables.galaxian, Color.blue,
      Collectables.bell, Color.cyan,
      Collectables.key, Color.cyan
  );

  //array deciding which fruit is will spawn
  //Level= index+1
  public static final Collectables[] levelFruit = {
      Collectables.cherry, Collectables.strawberry,
      Collectables.orange, Collectables.orange,
      Collectables.apple, Collectables.apple,
      Collectables.melon, Collectables.melon,
      Collectables.galaxian, Collectables.galaxian,
      Collectables.bell, Collectables.bell,
      Collectables.key, Collectables.key,
      Collectables.key, Collectables.key,
      Collectables.key, Collectables.key,
      Collectables.key, Collectables.key };

  //variety of fruits
  @SuppressWarnings("SpellCheckingInspection")
  public enum Collectables
  { coin, powerUp, cherry, strawberry, apple, orange, melon, galaxian, bell, key }


  public enum mode
  { CHASE, SCATTER, FRIGHTENED, EXIT, RETREAT, ENTER }

  @SuppressWarnings("SpellCheckingInspection")
  public enum ghostNames
  { BLINKY, PINKY, INKY, CLYDE }
}
