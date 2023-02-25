package kn.uni.games.classic.pacman.game.graphics;


import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;

public interface Ticking
{
  /**
   * actions to be performed every tick
   *
   * @param gameState the game state
   */
  void tick (ClassicPacmanGameState gameState);
}
