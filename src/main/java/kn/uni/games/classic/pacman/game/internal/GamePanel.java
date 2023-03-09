package kn.uni.games.classic.pacman.game.internal;

import kn.uni.games.classic.pacman.game.graphics.AdvRendered;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Comparator;

public class GamePanel extends JPanel
{
  public int          index;
  public AdvGameState gameState;

  public BufferedImage cachedImg;

  public GamePanel (Dimension dim, int index)
  {
    setBounds(0, 0, dim.width, dim.height);
    setOpaque(false);
    setLayout(null);
    setBackground(null);
    this.index = index;
  }

  @Override
  public void paintComponent (Graphics g)
  {
    super.paintComponent(g);

    if (cachedImg == null)
      render();

    g.drawImage(cachedImg, 0, 0, null);
  }

  public void render ()
  {
    cachedImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = cachedImg.createGraphics();

    gameState.layers.get(index).stream()
                    .filter(gameObject -> gameObject instanceof AdvRendered)
                    .map(gameObject -> (AdvRendered) gameObject)
                    .sorted(Comparator.comparingInt(AdvRendered::paintLayer))
                    .forEach(gameObject -> ( gameObject ).paintComponent(g2));
  }
}
