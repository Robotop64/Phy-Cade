package kn.uni.games.classic.pacman.game;

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

  //variety of fruits
  public enum Collectables
  { coin, powerUp, cherry, strawberry, apple, orange, melon, galaxian, bell, key }
}
