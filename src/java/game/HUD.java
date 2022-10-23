package game;

import ui.Gui;
import util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

public class HUD extends PlacedObject implements Rendered
{
  Vector2d botBounds;
  Vector2d botPos;

  Vector2d sideBounds;

  public HUD (ClassicPacmanGameState gameState, Vector2d mapPos, Vector2d mapBounds)
  {
    botBounds = new Vector2d().cartesian(mapBounds.x, (int) ( Gui.frameHeight - mapBounds.y - mapPos.y * 2 - mapPos.y / 2 ));
    botPos = new Vector2d().cartesian(mapPos.x, mapPos.y + mapBounds.y - mapPos.y / 2);
    int test = (int) ( Gui.frameHeight - mapBounds.y - mapPos.y * 2 );

    //    gameState.gameObjects.add(new ScoreLabel(new Vector2d().cartesian(20, 920), new Vector2d().cartesian(400, 80)));
    //    gameState.gameObjects.add(new LiveLabel(new Vector2d().cartesian(400, 920), new Vector2d().cartesian(400, 80)));
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    g.setStroke(new BasicStroke(1));
    g.drawRect(0, (int) botPos.y, (int) botBounds.x, (int) botBounds.y);


    //    g.setStroke(new BasicStroke(5));
    //    g.drawRect(0, 0, 200, 200);

    //    botPos.multiply(-1).use(g::translate);
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 25;
  }
}
