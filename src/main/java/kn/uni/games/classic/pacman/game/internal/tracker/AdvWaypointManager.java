package kn.uni.games.classic.pacman.game.internal.tracker;

import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.graphics.Scaled;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdvWaypointManager extends AdvGameObject
{
  public List <Waypoint> waypoints = new ArrayList <>();

  public AdvGameState gameState;

  public AdvWaypointManager (AdvGameState gameState)
  {
    this.gameState = gameState;
  }

  public static AdvWaypointManager getInstance (AdvGameState gameState)
  {
    return (AdvWaypointManager) gameState.objects.find(AdvGameState.Layer.INTERNALS, AdvWaypointManager.class).get(0);
  }

  public void removeWaypoint (String name)
  {
    waypoints.removeIf(waypoint -> waypoint.name.equals(name));
  }

  public Waypoint getWaypoint (String name)
  {
    return waypoints.stream().filter(waypoint -> waypoint.name.equals(name)).findFirst().orElse(null);
  }

  public void addWaypoint (String name, Vector2d mapPos, Runnable onCollision)
  {
    Waypoint waypoint = new Waypoint(name, mapPos, onCollision);

    gameState.add(AdvGameState.Layer.INFORMATIONAL, waypoint);
    waypoints.add(waypoint);
  }

  public static class Waypoint extends AdvPlacedObject implements AdvColliding, AdvRendered, Scaled
  {
    public Vector2d mapPos;
    public Runnable onCollision;
    public String   name;

    public AdvGameObject collider;

    public BufferedImage cachedImg;

    public Waypoint (String name, Vector2d mapPos, Runnable onCollision)
    {
      super();
      this.name = name;
      this.mapPos = mapPos;
      this.onCollision = onCollision;
    }

    @Override
    public void onCollision (AdvGameObject collider)
    {
      this.collider = collider;
      onCollision.run();
      this.collider = null;
    }

    @Override
    public void paintComponent (Graphics2D g)
    {
      if (Objects.equals(Config.getCurrent("Debugging/DebugView"), true))
      {
        if (cachedImg == null)
          render();

        g.drawImage(cachedImg, (int) absPos.x - iconSize / 2, (int) absPos.y - iconSize / 2, null);
        g.setColor(Color.RED);
        g.drawString(name, (int) absPos.x, (int) absPos.y - iconSize / 2);
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
      cachedImg = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = cachedImg.createGraphics();
      if (Objects.equals(Config.getCurrent("Graphics/Advanced/Antialiasing"), true))
      {
        g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
      }

      g.setColor(Color.RED);
      g.fillOval(iconSize / 2 - 5, iconSize / 2 - 5, 10, 10);
    }

    @Override
    public void scale (double scale)
    {
      absPos = mapPos.multiply(scale);
    }
  }

}
