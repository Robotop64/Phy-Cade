package game.hud;

import game.ClassicPacmanGameState;
import game.PlacedObject;
import game.Rendered;

import java.awt.Graphics2D;

public class CollectablesLabel extends PlacedObject implements Rendered
{
  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }
}
