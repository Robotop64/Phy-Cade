package kn.uni.games.classic.pacman.game.hud;


import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PlacedObject;
import kn.uni.games.classic.pacman.game.Rendered;
import kn.uni.games.classic.pacman.game.ScoreLabel;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

public class HUD extends PlacedObject implements Rendered
{
  Vector2d botBounds;
  Vector2d botPos;

  Vector2d sideBounds;
  Vector2d sidePos;

  int textBounds;
  int rowHeight;
  int textBuffer;

  //initialize HUD
  public HUD (ClassicPacmanGameState gameState, Vector2d mapPos, Vector2d mapBounds)
  {
    botBounds = new Vector2d().cartesian(mapBounds.x, (int) ( Gui.frameHeight - mapBounds.y - mapPos.y * 2 - mapPos.y / 2 ));
    botPos = new Vector2d().cartesian(mapPos.x, mapPos.y + mapBounds.y - mapPos.y / 2);

    sideBounds = new Vector2d().cartesian(Gui.frameWidth - mapBounds.x - mapPos.x - mapPos.x * 2 - mapPos.x / 2, mapBounds.y);
    sidePos = new Vector2d().cartesian(mapPos.x + mapBounds.x + mapPos.x / 2, mapPos.y);

    textBuffer = 15;

    textBounds = (int) ( sideBounds.x - 2 * ( textBuffer ) );

    rowHeight = gameState.uiSize - 20;

    if (botBounds.y < sideBounds.x)
    {
      gameState.gameObjects.add(new GameLabel(new Vector2d().cartesian(sidePos.x + textBuffer, 0), new Vector2d().cartesian(textBounds, gameState.uiSize)));
      gameState.gameObjects.add(new LevelLabel(new Vector2d().cartesian(sidePos.x + textBuffer, rowHeight), new Vector2d().cartesian(textBounds, gameState.uiSize)));
      gameState.gameObjects.add(new ScoreLabel(new Vector2d().cartesian(sidePos.x + textBuffer, rowHeight * 2), new Vector2d().cartesian(textBounds, gameState.uiSize)));
      gameState.gameObjects.add(new TimeLabel(new Vector2d().cartesian(sidePos.x + textBuffer, rowHeight * 3), new Vector2d().cartesian(textBounds, gameState.uiSize)));
      gameState.gameObjects.add(new LiveLabel(new Vector2d().cartesian(sidePos.x + textBuffer, rowHeight * 4), new Vector2d().cartesian(textBounds, gameState.uiSize)));
//            gameState.gameObjects.add(new CollectablesLabel());
//      gameState.gameObjects.add(new DynamicLeaderboard(new Vector2d().cartesian(sidePos.x + textBuffer, rowHeight * 8.5), new Vector2d().cartesian(textBounds, gameState.uiSize)));
    }
    else
    {
          gameState.gameObjects.add(new ScoreLabel(new Vector2d().cartesian(20, 920), new Vector2d().cartesian(400, 80)));
          gameState.gameObjects.add(new LiveLabel(new Vector2d().cartesian(400, 920), new Vector2d().cartesian(400, 80)));
    }
  }

  //paint boxes
  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    g.setColor(Color.cyan.darker());
    g.setStroke(new BasicStroke(1));
    //        g.drawRect(0, (int) botPos.y, (int) botBounds.x, (int) botBounds.y);

    g.setStroke(new BasicStroke(3));
    g.drawRect((int) sidePos.x, 0, (int) sideBounds.x, (int) sideBounds.y);

//        g.drawRect((int) ( sidePos.x + textBuffer ), 0, textBounds, (int) sideBounds.y);
//        g.drawRect((int) ( sidePos.x + textBuffer ), rowHeight * 5, textBounds, (int) sideBounds.y - rowHeight * 5);
//        g.drawRect((int) ( sidePos.x + textBuffer ), (int) ( rowHeight * 5 + ( sideBounds.y - rowHeight * 5 ) / 2 ), textBounds, (int) ( sideBounds.y - ( rowHeight * 5 + ( sideBounds.y - rowHeight * 5 ) / 2 ) ));

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
