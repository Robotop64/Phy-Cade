package kn.uni.games.classic.pacman.game.objects;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.entities.ghosts.AdvGhostEntity;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.graphics.Scaled;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvCollider;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.TagManager;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Blocker extends AdvPlacedObject implements AdvRendered, AdvColliding, Scaled
{
  public BufferedImage cachedImg;
  public Dimension     size;

  public Blocker (Vector2d mapPos, Dimension dim)
  {
    super();
    this.mapPos = mapPos;
    this.size = dim;
    this.tagManager = new TagManager();
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
    if (collider instanceof Entity entity)
    {
      if (entity instanceof AdvGhostEntity ghost &&
          (ghost.ai.mode.equals(AdvGameConst.GhostMode.ENTER) || ghost.ai.mode.equals(AdvGameConst.GhostMode.EXIT)))
        return;

      if (entity instanceof AdvPacManEntity || entity instanceof AdvGhostEntity)
      {
        Direction dir = AdvCollider.getDirection(entity, this);

        if (AdvCollider.distance(entity, this) <= AdvCollider.criticalDist(entity, this) + 20 )
        {
          if (!entity.suppressedDirections.contains(dir) && dir != null)
            entity.suppressedDirections.add(dir);
        }
        else
          entity.suppressedDirections.clear();
      }
    }
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();

    g.drawImage(cachedImg, (int) ( absPos.x - size.getWidth() / 2. ), (int) ( absPos.y - size.getHeight() / 2. ), null);

    if (Objects.equals(Config.getCurrent("Debugging/DebugView"), true))
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

  @Override
  public void scale (double scale)
  {
    size = new Dimension((int) ( size.getWidth()/100 * scale ), (int) ( size.getHeight()/100 * scale ));
  }
}
