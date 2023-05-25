//package kn.uni.games.classic.pacman.game.hud;
//
//
//import kn.uni.games.classic.pacman.game.graphics.Rendered;
//import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
//import kn.uni.games.classic.pacman.game.objects.PlacedObject;
//import kn.uni.util.Fira;
//import kn.uni.util.Vector2d;
//
//import java.awt.BasicStroke;
//import java.awt.Graphics2D;
//
//public class LevelLabel extends PlacedObject implements Rendered
//{
//  Vector2d size;
//
//  public LevelLabel (Vector2d pos, Vector2d size)
//  {
//    this.pos = pos;
//    this.size = size;
//  }
//
//  @Override
//  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
//  {
//
//    String text     = "Level:  " + "%3d".formatted(gameState.level);
//    int    fontSize = (int) ( ( ( size.x / text.length() * 32 / 20 ) / 100 * gameState.uiSize ) );
//
//    pos.use(g::translate);
//
//
//    g.setFont(Fira.getInstance().getLigatures(fontSize));
//    g.setStroke(new BasicStroke(1));
//    g.drawString(text, 3, (int) ( 18.4 * text.length() / 160. * fontSize ));
//
//
//    //    size.use((x, y) -> g.drawRect(0, 0, x, y));
//
//    pos.multiply(-1).use(g::translate);
//  }
//
//  @Override
//  public int paintLayer ()
//  {
//    return Integer.MAX_VALUE - 50;
//  }
//}