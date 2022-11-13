package kn.uni.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class TextureLoader
{
  private static final String texturePath = "pacman/textures";

  //singleton pattern
  private static final TextureLoader instance = new TextureLoader();

  private TextureLoader ()
  {
  }

  public static TextureLoader getInstance ()
  {
    return instance;
  }

  public BufferedImage loadTexture (String category, String name)
  {
    try
    {
      //      BufferedImage out = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("pacman/textures/ghosts/nowak/nowak-closed.png")));
      BufferedImage out = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(texturePath + "/" + category + "/" + name)));
      System.out.println("loaded texture " + texturePath + "/" + category + "/" + name);
      System.out.println(out);
      return out;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.out.println("failed to load texture " + texturePath + "/" + category + "/" + name);
    }
    return null;
  }
}
