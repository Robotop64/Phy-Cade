package game.map;

import game.ClassicPacmanGameState;
import game.PlacedObject;
import game.Rendered;
import game.Ticking;

import java.awt.Graphics2D;

public class ClassicPacmanMap extends PlacedObject implements Rendered, Ticking
{
  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {

  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {

  }
}
