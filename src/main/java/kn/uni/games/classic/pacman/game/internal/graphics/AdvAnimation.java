package kn.uni.games.classic.pacman.game.internal.graphics;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class AdvAnimation
{
  public BufferedImage animationSource;

  public int sourceFrameCount;

  public Dimension frameSize;

  public int duration;


  public AdvAnimation (BufferedImage animationSource, int sourceFrameCount, Dimension frameSize, int duration)
  {
    this.animationSource = animationSource;
    this.sourceFrameCount = sourceFrameCount;
    this.frameSize = frameSize;
    this.duration = duration;
  }

  public BufferedImage getFrame (int frame)
  {
    int frameWidth = animationSource.getWidth()/sourceFrameCount;
    Rectangle frameRegion = new Rectangle(
        frame * frameWidth,
        0,
        frameWidth,
        animationSource.getHeight()
    );
    return animationSource.getSubimage(frameRegion.x,frameRegion.y,frameRegion.width,frameRegion.height);
  }

  private void scale (Dimension frameSize)
  {
    int             w   = animationSource.getWidth();
    int             h   = animationSource.getHeight();

    BufferedImage   out = new BufferedImage(frameSize.width*sourceFrameCount, frameSize.height, BufferedImage.TYPE_INT_ARGB);

    java.awt.geom.AffineTransform at  = new java.awt.geom.AffineTransform();
    at.scale(frameSize.width / ( w * 1. ), frameSize.height / ( h * 1. ));
    java.awt.image.AffineTransformOp scaleOp =
        new java.awt.image.AffineTransformOp(at, java.awt.image.AffineTransformOp.TYPE_BILINEAR);
    out = scaleOp.filter(animationSource, out);
    animationSource = out;
  }

  public static class Set
  {
    public ArrayList<AdvAnimation> animations;

    public Set (ArrayList<AdvAnimation> animations)
    {
      this.animations = animations;
    }
  }
}
