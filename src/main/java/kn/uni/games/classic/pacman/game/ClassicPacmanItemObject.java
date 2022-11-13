package kn.uni.games.classic.pacman.game;

import kn.uni.Gui;
import kn.uni.util.TextureLoader;
import kn.uni.util.Vector2d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ClassicPacmanItemObject extends PlacedObject
{
  public  Vector2d                                pos;
  public  ClassicPacmanGameConstants.Collectables type;
  private BufferedImage                           icon;

  public ClassicPacmanItemObject (Vector2d pos, ClassicPacmanGameState gameState, ClassicPacmanGameConstants.Collectables type)
  {
    super();
    this.pos = pos;
    this.type = type;
    this.movable = false;

    //load icon texture
    icon = TextureLoader.getInstance().loadTexture("items", type.name() + ".png");
  }

  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    Vector2d topLeft = new Vector2d().cartesian(icon.getWidth(), icon.getHeight()).multiply(-.5).add(pos);
    topLeft.use(g::translate);
    g.drawImage(icon, 0, 0, Gui.getInstance().frame);
    topLeft.multiply(-1).use(g::translate);
  }

  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 120;
  }
}
