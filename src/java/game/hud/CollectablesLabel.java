package game.hud;

import game.PlacedObject;
import game.Rendered;
import game.pacman.ClassicPacmanGameState;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectablesLabel extends PlacedObject implements Rendered
{
  public Map <ClassicPacmanGameState.Collectables, BufferedImage> icons = new HashMap <>();

  public CollectablesLabel () throws IOException
  {
    String path = "src/resources/textures/collectables/PH/";

    List <ClassicPacmanGameState.Collectables> a = List.of(ClassicPacmanGameState.Collectables.values());

    for (int i = 0; i < ClassicPacmanGameState.Collectables.values().length; i++)
    {
      icons.put(a.get(i), ImageIO.read(new File(path + a.get(i).toString() + ".png")));
    }

  }


  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    g.drawImage(icons.get(ClassicPacmanGameState.Collectables.cherry), 0, 0, 500, 500, null);
    g.drawRect(0, 0, 500, 500);

  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 50;
  }
}
