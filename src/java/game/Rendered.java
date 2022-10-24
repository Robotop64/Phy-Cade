package game;

import game.pacman.ClassicPacmanGameState;

import java.awt.Graphics2D;

public interface Rendered
{
  void paintComponent (Graphics2D g, ClassicPacmanGameState gameState);

  int paintLayer ();
}
