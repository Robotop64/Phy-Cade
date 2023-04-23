package kn.uni.games.classic.pacman.game.internal.objects;

import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.internal.tracker.DebugManager;

import java.awt.image.BufferedImage;

public class AdvGameObject
{
  public AdvGameState gameState;

  public BufferedImage cachedImg;

  public DebugManager debugManager;
}
