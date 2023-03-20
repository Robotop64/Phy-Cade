package kn.uni.games.classic.pacman.game.objects;

import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.PacPhiConfig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Blocker extends AdvPlacedObject implements AdvRendered, AdvColliding
{
  public BufferedImage cachedImg;
  public Dimension     size;

  public Blocker (Vector2d mapPos, Dimension dim)
  {
    super();
    this.mapPos = mapPos;
    this.size = dim;
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
    //    if (collider instanceof Entity)
    //      ( (Entity) collider ).suppressedDirections.add(( (Entity) collider ).facing);
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();

    g.drawImage((Image) cachedImg, (int) ( absPos.x - size.getWidth() / 2. ), (int) ( absPos.y - size.getHeight() / 2. ), null);

    if (( (PacPhiConfig.Switch) PacPhiConfig.getContent("Debugging", "-", "Enabled") ).current())
    {
      g.setColor(Color.RED);
      int hitBox = (int) ( AdvGameConst.hitBoxes.get("Blocker") * AdvGameConst.tileSize ) * 2;
      g.drawOval((int) ( absPos.x - hitBox / 2 ), (int) ( absPos.y - hitBox / 2 ), hitBox, hitBox);
    }
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  @Override
  public void render ()
  {
    cachedImg = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = cachedImg.createGraphics();

    g2.setColor(Color.CYAN.darker().darker().darker().darker());
    g2.fillRect(0, 0, (int) size.getWidth(), (int) size.getHeight());

    g2.dispose();
  }
}
