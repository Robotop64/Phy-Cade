package game;

import java.awt.Graphics2D;

public class PacmanObject extends PlacedObject implements Rendered, Ticking
{
  public PacmanObject ()
  {
    super();
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {

  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE;
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {

  }
}
