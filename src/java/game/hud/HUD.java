package game.hud;

import game.PlacedObject;
import game.Rendered;
import game.pacman.ClassicPacmanGameState;
import ui.Gui;
import util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.io.IOException;

public class HUD extends PlacedObject implements Rendered
{
  Vector2d botBounds;
  Vector2d botPos;

  Vector2d sideBounds;
  Vector2d sidePos;

  //initialize HUD
  public HUD (ClassicPacmanGameState gameState, Vector2d mapPos, Vector2d mapBounds) throws IOException
  {
    botBounds = new Vector2d().cartesian(mapBounds.x, (int) ( Gui.frameHeight - mapBounds.y - mapPos.y * 2 - mapPos.y / 2 ));
    botPos = new Vector2d().cartesian(mapPos.x, mapPos.y + mapBounds.y - mapPos.y / 2);

    sideBounds = new Vector2d().cartesian(Gui.frameWidth - mapBounds.x - mapPos.x - mapPos.x * 2 + mapPos.x / 2, mapBounds.y);
    sidePos = new Vector2d().cartesian(mapPos.x + mapBounds.x - mapPos.x / 2, mapPos.y);

    if (botBounds.y < sideBounds.x)
    {
      gameState.gameObjects.add(new GameLabel(new Vector2d().cartesian(sidePos.x, 0), new Vector2d().cartesian(sideBounds.x, gameState.uiSize)));
      gameState.gameObjects.add(new LevelLabel(new Vector2d().cartesian(sidePos.x, gameState.uiSize), new Vector2d().cartesian(sideBounds.x, gameState.uiSize)));
      gameState.gameObjects.add(new ScoreLabel(new Vector2d().cartesian(sidePos.x, gameState.uiSize * 2), new Vector2d().cartesian(sideBounds.x, gameState.uiSize)));
      gameState.gameObjects.add(new TimeLabel(new Vector2d().cartesian(sidePos.x, gameState.uiSize * 3), new Vector2d().cartesian(sideBounds.x, gameState.uiSize)));
      gameState.gameObjects.add(new LiveLabel(new Vector2d().cartesian(sidePos.x, gameState.uiSize * 4), new Vector2d().cartesian(sideBounds.x, gameState.uiSize)));
      //      gameState.gameObjects.add(new CollectablesLabel());
    }
    else
    {
      //    gameState.gameObjects.add(new ScoreLabel(new Vector2d().cartesian(20, 920), new Vector2d().cartesian(400, 80)));
      //    gameState.gameObjects.add(new LiveLabel(new Vector2d().cartesian(400, 920), new Vector2d().cartesian(400, 80)));
    }
  }

  //paint boxes
  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    g.setStroke(new BasicStroke(1));
    //    g.drawRect(0, (int) botPos.y, (int) botBounds.x, (int) botBounds.y);

    g.setStroke(new BasicStroke(1));
    //    g.drawRect((int) sidePos.x, 0, (int) sideBounds.x, (int) sideBounds.y);

  }

  //paintingLayer
  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 25;
  }

  //create HUD
  public static void createHUD (ClassicPacmanGameState gameState) throws IOException
  {
    gameState.gameObjects.add(new HUD(gameState, new Vector2d().cartesian(gameState.mapOffset, gameState.mapOffset), new Vector2d().cartesian(gameState.map.width, gameState.map.height)));
  }
}
