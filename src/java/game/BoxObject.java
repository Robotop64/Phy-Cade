package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class BoxObject extends GameObject implements Rendered, Ticking
{
  @Override
  public void paintComponent (Graphics2D g, GameState gameState)
  {
    g.setColor(Color.blue);
    g.fillRect((int)(gameState.currentTick % 300), 100, 100, 100);
  }

  @Override
  public void tick (GameState gameState)
  {

  }
}
