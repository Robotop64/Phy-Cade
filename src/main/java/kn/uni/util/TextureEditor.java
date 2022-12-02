package kn.uni.util;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.IntStream;

public class TextureEditor
{
  private static final String texturePath = "pacman/textures";

  //singleton pattern
  private static final TextureEditor instance = new TextureEditor();

  private TextureEditor () { }

  public static TextureEditor getInstance ()
  {
    return instance;
  }

  public void saveImage (BufferedImage imageIn, String path, String type) throws IOException
  {
    String source = "src/main/resources/";
    ImageIO.write(imageIn, type, new File(source + texturePath + path + "." + type.toLowerCase()));
  }

  public BufferedImage loadTexture (String category, String name)
  {
    try
    {
      return ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(texturePath + "/" + category + "/" + name)));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.out.println("failed to load texture " + texturePath + "/" + category + "/" + name);
    }
    return null;
  }

  public BufferedImage loadResource(String path){
    try
    {
      return ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path)));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.out.println("failed to load texture " + path);
    }
    return null;
  }

  public BufferedImage createOutline (BufferedImage imageIn, int thickness, Color c)
  {
    //create outputImage
    BufferedImage output = new BufferedImage(imageIn.getWidth(), imageIn.getHeight(), BufferedImage.TYPE_INT_ARGB);
    //for each pixelRow in the image
    IntStream.range(0, imageIn.getWidth() - thickness).forEach(xPixel ->
    {
      //for each pixelColumn in the image
      IntStream.range(0, imageIn.getHeight() - thickness).forEach(yPixel ->
      {
        //color of evaluated pixel
        Color currentPixel = new Color(imageIn.getRGB(xPixel, yPixel), true);
        //exclude pixels with texture
        if (currentPixel.getAlpha() == 0)
        {
          //check if pixel has texture neighbors
          for (int φ = 0; φ < 360; φ += 90)
          {
            Vector2d v              = new Vector2d().polar(thickness, φ);
            int      nextX          = Util.bounded((int) ( xPixel + v.x ), 0, imageIn.getWidth());
            int      nextY          = Util.bounded((int) ( yPixel + v.y ), 0, imageIn.getHeight());
            Color    neighbourPixel = new Color(imageIn.getRGB(nextX, nextY), true);
            //if neighbor is not transparent, set pixel to outline color
            if (neighbourPixel.getAlpha() != 0)
            {
              output.setRGB(xPixel, yPixel, c.getRGB());
            }
          }
        }
      });
    });
    return output;
  }

  public BufferedImage scale(BufferedImage imageIn, int width, int height){
    int w = imageIn.getWidth();
    int h = imageIn.getHeight();
    BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();
    at.scale(width/(w*1.), height/(h*1.));
    AffineTransformOp scaleOp =
            new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    out = scaleOp.filter(imageIn, out);
    return out;
  }
}
