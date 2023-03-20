package kn.uni.games.classic.pacman.game.objects;

import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvTimer;
import kn.uni.util.Direction;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.PacPhiConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Teleporter extends AdvPlacedObject implements AdvRendered, AdvColliding
{
  public BufferedImage cachedImg;
  public Teleporter    other;
  public Direction     facing;
  public boolean       onCooldown = false;
  public Color         color      = Color.BLACK;

  public Teleporter (AdvGameState gameState, Vector2d mapPos, Direction facing)
  {
    super();
    this.gameState = gameState;
    this.mapPos = mapPos;
    this.facing = facing;
  }

  //region graphics
  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();

    double angle = Util.degToRad(facing.toAngle());
    g.rotate(angle, absPos.x, absPos.y);

    g.drawImage(cachedImg, (int) ( absPos.x - cachedImg.getWidth() / 2. + cachedImg.getWidth() / 4. ), (int) ( absPos.y - cachedImg.getHeight() / 2. ), null);

    if (PacPhiConfig.checkSetting("Debugging", "-", "Enabled", true))
    {
      g.setColor(Color.RED);
      int radius = (int) ( AdvGameConst.hitBoxes.get("Teleporter") * AdvGameConst.tileSize );
      g.drawOval((int) ( absPos.x - radius ), (int) ( absPos.y - radius ), 2 * radius, 2 * radius);
    }

    g.rotate(-angle, absPos.x, absPos.y);
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  @Override
  public void render ()
  {
    cachedImg = new BufferedImage(iconSize * 2, (int) ( iconSize * 1.5 ), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = cachedImg.createGraphics();

    if (PacPhiConfig.checkSetting("Graphics", "Advanced", "Antialiasing", true))
    {
      g.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
    }

    Color c = this.color;

    //bottom highlight
    g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 40));
    double[] h1 = { 1 / 3., 1 / 6., 2 / 3., 2 / 3. };
    g.fillOval((int) ( h1[0] * cachedImg.getWidth() ), (int) ( h1[1] * cachedImg.getHeight() ), (int) ( h1[2] * cachedImg.getWidth() ), (int) ( h1[3] * cachedImg.getHeight() ));

    //middle highlight
    g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).brighter());
    double[] h2 = { 1 / 3., 1 / 4., 3 / 4. - 1 / 4., 1 / 2. };
    g.fillOval((int) ( h2[0] * cachedImg.getWidth() ), (int) ( h2[1] * cachedImg.getHeight() ), (int) ( h2[2] * cachedImg.getWidth() ), (int) ( h2[3] * cachedImg.getHeight() ));

    //top highlight
    g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60).brighter().brighter());
    double[] h3 = { 1 / 3., 1 / 3., 1 / 2. - 1 / 5., 1 / 3. };
    g.fillOval((int) ( h3[0] * cachedImg.getWidth() ), (int) ( h3[1] * cachedImg.getHeight() ), (int) ( h3[2] * cachedImg.getWidth() ), (int) ( h3[3] * cachedImg.getHeight() ));

    //black filling
    g.setColor(Color.black);
    double[] h4 = { 0.1, 0.1, 1 / 3. - 0.1, 1 - 0.2 };
    g.fillOval((int) ( h4[0] * cachedImg.getWidth() ), (int) ( h4[1] * cachedImg.getHeight() ), (int) ( h4[2] * cachedImg.getWidth() ), (int) ( h4[3] * cachedImg.getHeight() ));

    //orange halo
    g.setColor(c.darker());
    g.setStroke(new java.awt.BasicStroke(2));
    double[] h5 = { 0.1, 0.1, 1 / 3. - 0.1, 1 - 0.2 };
    g.drawOval((int) ( h5[0] * cachedImg.getWidth() ), (int) ( h5[1] * cachedImg.getHeight() ), (int) ( h5[2] * cachedImg.getWidth() ), (int) ( h5[3] * cachedImg.getHeight() ));

    g.dispose();
  }
  //endregion

  public void pair (Teleporter other, Color color)
  {
    this.other = other;
    this.color = color;
    other.other = this;
    other.color = color;
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
    if (onCooldown)
      return;

    AdvPlacedObject trigger = (AdvPlacedObject) collider;
    trigger.mapPos = other.mapPos;
    trigger.absPos = other.absPos;

    if (trigger instanceof Entity e)
      e.facing = other.facing;

    other.onCooldown = true;

    gameState.layers.get(AdvGameState.Layer.INTERNALS.ordinal()).stream()
                    .filter(o -> o instanceof AdvTimer)
                    .map(o -> (AdvTimer) o)
                    .forEach(o -> o.addTask(new AdvTimer.TimerTask(gameState.currentTick, 120, () -> other.onCooldown = false)));

    gameState.env.updateLayer.set(AdvGameState.Layer.ENTITIES.ordinal(), true);

  }

}
