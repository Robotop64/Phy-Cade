package kn.uni.games.classic.pacman.game.internal.graphics;

import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Comparator;

public class GameLayer implements AdvRendered
{
  public int          index;
  public Dimension    dim;
  public AdvGameState gameState;

  public BufferedImage cachedImg;

  public GameLayer (Dimension dim, int index)
  {
    this.dim = dim;
    this.index = index;
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();

    g.drawImage(cachedImg, 0, 0, null);
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  public void render ()
  {
    cachedImg = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = cachedImg.createGraphics();

    gameState.layers.get(index).stream()
                    .filter(gameObject -> gameObject instanceof AdvRendered)
                    .map(gameObject -> (AdvRendered) gameObject)
                    .sorted(Comparator.comparingInt(AdvRendered::paintLayer))
                    .forEach(gameObject -> ( gameObject ).paintComponent(g2));
  }
}
