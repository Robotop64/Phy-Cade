package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.ClassicPacmanMap.TotalPosition;
import kn.uni.games.classic.pacman.game.CollidableObject;
import kn.uni.games.classic.pacman.game.PacmanMapTile;
import kn.uni.games.classic.pacman.game.Rendered;
import kn.uni.games.classic.pacman.game.Ticking;
import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Ghost extends CollidableObject implements Ticking, Rendered
{
  private final int                                   animationFrequency = 8;
  private final String                                dirPath            = "pacman/textures/ghosts/";
  public        GhostAI                               ai;
  public        Direction                             direction;
  public        PacmanMapTile                         currentTile;
  public        ClassicPacmanGameConstants.mode       currentMode;
  public        boolean                               canUseDoor;
  public        ClassicPacmanGameConstants.ghostNames name;
  private       BufferedImage                         opened;
  private       BufferedImage                         closed;
  private       PacmanMapTile                         nextTile;


  public Ghost (String profName, Vector2d pos, GhostAI ghostAI, ClassicPacmanGameConstants.ghostNames name)
  {
    loadImages("%s%s/%s-".formatted(dirPath, profName, profName));
    this.pos = pos;
    ai = ghostAI;
    this.name = name;
    movable = true;
    canUseDoor = false;
    this.hitbox = new Vector2d().cartesian(ClassicPacmanGameConstants.ghostRadius * 2, ClassicPacmanGameConstants.ghostRadius * 2);
    this.direction = Direction.up;
    ai.setMode(ClassicPacmanGameConstants.mode.EXIT, this);

  }

  private void loadImages (String path)
  {
    try
    {
      closed = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path + "closed.png")));
      opened = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path + "opened.png")));
    }
    catch (IOException e)
    {
      System.out.printf("[ERROR] failed to load images for %s%n", path);
      throw new RuntimeException(e);
    }

  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    int           duration = gameState.tps / animationFrequency;
    BufferedImage im       = ( ( gameState.currentTick % duration ) * 1. / duration > .5 ) ? closed : opened;
    Vector2d      topLeft  = new Vector2d().cartesian(im.getWidth(), im.getHeight()).multiply(-.5).add(pos);
    topLeft.use(g::translate);
    g.drawImage(im, 0, 0, Gui.getInstance().frame);
    topLeft.multiply(-1).use(g::translate);

    g.translate(pos.x, pos.y);
    g.setColor(ai.borderColor);
    g.drawOval((int) -ClassicPacmanGameConstants.ghostRadius, (int) -ClassicPacmanGameConstants.ghostRadius, (int) ( ClassicPacmanGameConstants.ghostRadius * 2 ), (int) ( ClassicPacmanGameConstants.ghostRadius * 2 ));

    if (DebugDisplay.getDebug(gameState).enabled)
    {
      Direction a = ai.nextDirection2(gameState, this, currentMode);
      Vector2d  b = a.toVector().multiply(gameState.map.tileSize);
      g.drawLine(0, 0, (int) b.x, (int) b.y);
    }
    g.translate(-pos.x, -pos.y);

  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 99;
  }

  private double getSpeedScale (ClassicPacmanGameState gameState)
  {
    return switch (gameState.level)
        {
          case 1 -> .75;
          case 2, 3, 4 -> .75;
          default -> .85;
        };
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    //player      velocity           = tileSpeed * speedScale * tileToPixel * pixelPerTick
    double        velocity = ClassicPacmanGameConstants.pacmanSpeed * getSpeedScale(gameState) * gameState.map.tileSize * 1. / gameState.tps;
    TotalPosition tp       = gameState.map.splitPosition(pos);
    int           ts       = gameState.map.tileSize;
    currentTile = gameState.map.tiles.get(tp.ex());

    if (movable)
    {
      ai.setCasePos(gameState, this);

      if (nextTile == null) nextTile = currentTile.neighbors.get(ai.nextDirection2(gameState, this, currentMode).toVector());

      Vector2d nextTilePos = gameState.map.splitPosition(nextTile.pos).ex().subtract(gameState.map.splitPosition(pos).ex());
      if (nextTile != null)
      {
        if (currentTile == nextTile)
        {


          //      System.out.println("reached target tile");
          if (nextTilePos.scalar(tp.in()) < 0)
          {
            //        System.out.println("moving towards centre");
            pos = pos.add(nextTilePos.multiply(velocity));
          }
          else
          {
            //        System.out.println("reached centre, looking for new target");
            if (currentTile.type.equals(PacmanMapTile.Type.ghostExit))
            {
              ai.setMode(ClassicPacmanGameConstants.mode.CHASE, this);
              this.canUseDoor = false;
            }
            do
            {
              //          System.out.println("rerolling");
              nextTile = currentTile.neighbors.get(ai.nextDirection2(gameState, this, currentMode).toVector());
            } while (nextTile == null || !PacmanMapTile.walkable.contains(nextTile.type));
            //        System.out.println("found new target");
          }
        }
        else
        {
          if (DebugDisplay.getDebug(gameState).enabled)
          {
            currentTile.color = Color.black;
            nextTile.color = Color.green;
          }
          //      System.out.println("walking towards target");
          pos = pos.add(nextTilePos.multiply(velocity));
        }
      }
    }

    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostName, this, String.valueOf(name));
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostPosition, this, String.valueOf(pos));
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostDirection, this, String.valueOf(direction));
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostAI, this, ai.getClass().getSimpleName());
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostState, this, String.valueOf(currentMode));
  }

}
