package kn.uni.games.classic.pacman.game;


import java.awt.Graphics2D;

public interface Rendered
{
  /**
   * how to render this object
   *
   * @param g         graphics context
   * @param gameState current game state
   */
  void paintComponent (Graphics2D g, ClassicPacmanGameState gameState);

  /**
   * Layer the object is painted on
   *
   * @return a value between 0 and Integer.MAX_VALUE
   */
  int paintLayer ();
}
