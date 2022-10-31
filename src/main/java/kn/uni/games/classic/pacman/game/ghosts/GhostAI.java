package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.util.Direction;

import java.awt.Color;

public abstract class GhostAI
{
  protected Color borderColor;

  public abstract Direction getNextDirection (ClassicPacmanGameState gameState);
}
