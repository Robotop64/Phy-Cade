package kn.uni.games.classic.pacman.game.internal.objects;

import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.internal.tracker.TagManager;

import java.awt.image.BufferedImage;

public class AdvGameObject
{
  public String id;

  public boolean frozen = false;

  public AdvGameState gameState;

  public BufferedImage cachedImg;

  public TagManager tagManager;
}
