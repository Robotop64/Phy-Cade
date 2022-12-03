package kn.uni.games.classic.pacman.game;


public interface Ticking
{
  /**
   * actions to be performed every tick
   *
   * @param gameState the game state
   */
  void tick (ClassicPacmanGameState gameState);
}
