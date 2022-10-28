package kn.uni.games.classic.pacman.game;


import java.awt.Graphics2D;

public interface Rendered
{
  void paintComponent (Graphics2D g, ClassicPacmanGameState gameState);

  int paintLayer ();
}
