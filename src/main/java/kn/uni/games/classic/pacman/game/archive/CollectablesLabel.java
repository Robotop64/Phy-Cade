//package kn.uni.games.classic.pacman.game.hud;
//
//import kn.uni.games.classic.pacman.game.graphics.Rendered;
//import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameConstants;
//import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
//import kn.uni.games.classic.pacman.game.objects.PlacedObject;
//
//import javax.imageio.ImageIO;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@SuppressWarnings("unused")
//public class CollectablesLabel extends PlacedObject implements Rendered
//{
//  public Map <ClassicPacmanGameConstants.Collectables, BufferedImage> icons = new HashMap <>();
//
//  public CollectablesLabel () throws IOException
//  {
//    String path = "src/resources/textures/collectables/PH/";
//
//    List <ClassicPacmanGameConstants.Collectables> a = List.of(ClassicPacmanGameConstants.Collectables.values());
//
//    for (int i = 0; i < ClassicPacmanGameConstants.Collectables.values().length; i++)
//    {
//      icons.put(a.get(i), ImageIO.read(new File(path + a.get(i).toString() + ".png")));
//    }
//
//  }
//
//
//  @Override
//  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
//  {
//    g.drawImage(icons.get(ClassicPacmanGameConstants.Collectables.cherry), 0, 0, 500, 500, null);
//    g.drawRect(0, 0, 500, 500);
//
//  }
//
//  @Override
//  public int paintLayer ()
//  {
//    return Integer.MAX_VALUE - 50;
//  }
//}
