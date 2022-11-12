package kn.uni.games.classic.pacman.game;

import java.util.Map;

public class ClassicPacmanGameConstants
{
  public static final double pacmanSpeed = 6;
  public static final double ghostRadius = 31;

  //how many points a fruit gives
  public static final Map <ClassicPacmanGameState.Collectables, Integer> collectionPoints = Map.of(
      ClassicPacmanGameState.Collectables.coin, 10,
      ClassicPacmanGameState.Collectables.powerUp, 50,
      ClassicPacmanGameState.Collectables.cherry, 100,
      ClassicPacmanGameState.Collectables.strawberry, 300,
      ClassicPacmanGameState.Collectables.orange, 500,
      ClassicPacmanGameState.Collectables.apple, 700,
      ClassicPacmanGameState.Collectables.melon, 1000,
      ClassicPacmanGameState.Collectables.galaxian, 2000,
      ClassicPacmanGameState.Collectables.bell, 3000,
      ClassicPacmanGameState.Collectables.key, 5000
  );
}
