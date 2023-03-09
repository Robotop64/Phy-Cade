//package kn.uni.games.classic.pacman.game.hud;
//
//
//import kn.uni.Gui;
//import kn.uni.games.classic.pacman.game.graphics.Rendered;
//import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
//import kn.uni.games.classic.pacman.game.objects.PlacedObject;
//import kn.uni.util.Vector2d;
//
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Graphics2D;
//
//public class HUD extends PlacedObject implements Rendered
//{
//  Vector2d botBounds;
//  Vector2d botPos;
//
//  Vector2d sideBounds;
//  Vector2d sidePos;
//
//  int textBounds;
//  int rowHeight;
//  int textBuffer;
//
//  //ToDo remove own will. position and size should be determined by parent object
//  //initialize HUD
//  public HUD (ClassicPacmanGameState gameState, Vector2d mapPos, Vector2d mapBounds)
//  {
//    botBounds = new Vector2d().cartesian(mapBounds.x, (int) ( Gui.frameHeight - mapBounds.y - mapPos.y * 2 - mapPos.y / 2 ));
//    botPos = new Vector2d().cartesian(mapPos.x, mapPos.y + mapBounds.y - mapPos.y / 2);
//
//    sideBounds = new Vector2d().cartesian(Gui.frameWidth - mapBounds.x - mapPos.x - mapPos.x * 2 - mapPos.x / 2, mapBounds.y);
//    sidePos = new Vector2d().cartesian(mapPos.x + mapBounds.x + mapPos.x / 2, mapPos.y);
//
//    textBuffer = 15;
//
//    textBounds = (int) ( sideBounds.x - 2 * ( textBuffer ) );
//
//    rowHeight = gameState.uiSize - 20;
//
//    if (botBounds.y < sideBounds.x)
//    {
//      gameState.gameObjects.add(new GameLabel(new Vector2d().cartesian(sidePos.x + textBuffer, 0), new Vector2d().cartesian(textBounds, gameState.uiSize)));
//      gameState.gameObjects.add(new LevelLabel(new Vector2d().cartesian(sidePos.x + textBuffer, rowHeight), new Vector2d().cartesian(textBounds, gameState.uiSize)));
//      gameState.gameObjects.add(new ScoreLabel(new Vector2d().cartesian(sidePos.x + textBuffer, rowHeight * 2), new Vector2d().cartesian(textBounds, gameState.uiSize)));
//      gameState.gameObjects.add(new TimeLabel(new Vector2d().cartesian(sidePos.x + textBuffer, rowHeight * 3), new Vector2d().cartesian(textBounds, gameState.uiSize)));
//      gameState.gameObjects.add(new LiveLabel(new Vector2d().cartesian(sidePos.x + textBuffer, rowHeight * 4), new Vector2d().cartesian(textBounds, gameState.uiSize)));
//      //          gameState.gameObjects.add(new CollectablesLabel());
//      gameState.gameObjects.add(new DynamicLeaderboard(new Vector2d().cartesian(sidePos.x + textBuffer, rowHeight * 8.5), new Vector2d().cartesian(textBounds, gameState.uiSize)));
//    }
//  }
//
//  //paint boxes
//  @Override
//  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
//  {
//    sidePos.use(g::translate);
//    g.setColor(Color.cyan.darker());
//    //    g.setStroke(new BasicStroke(1));
//    //    g.drawRect(0, (int) botPos.y, (int) botBounds.x, (int) botBounds.y);
//
//    g.setColor(Color.yellow);
//    g.setStroke(new BasicStroke(3));
//    g.drawRect(0, -15, (int) sideBounds.x, (int) sideBounds.y);
//
//    //        g.drawRect((int) ( sidePos.x + textBuffer ), 0, textBounds, (int) sideBounds.y);
//    //        g.drawRect((int) ( sidePos.x + textBuffer ), rowHeight * 5, textBounds, (int) sideBounds.y - rowHeight * 5);
//    //        g.drawRect((int) ( sidePos.x + textBuffer ), (int) ( rowHeight * 5 + ( sideBounds.y - rowHeight * 5 ) / 2 ), textBounds, (int) ( sideBounds.y - ( rowHeight * 5 + ( sideBounds.y - rowHeight * 5 ) / 2 ) ));
//
//    sidePos.multiply(-1).use(g::translate);
//  }
//
//  //paintingLayer
//  @Override
//  public int paintLayer ()
//  {
//    return Integer.MAX_VALUE - 25;
//  }
//
//}
