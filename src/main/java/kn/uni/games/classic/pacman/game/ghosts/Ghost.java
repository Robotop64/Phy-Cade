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
import kn.uni.util.TextureEditor;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Ghost extends CollidableObject implements Ticking, Rendered
{
  @SuppressWarnings("FieldCanBeLocal")
  private final int    animationFrequency = 8;
  @SuppressWarnings("FieldCanBeLocal")
  private final String dirPath            = "pacman/textures/ghosts/";

  public GhostAI                               ai;
  public Direction                             direction;
  public PacmanMapTile                         currentTile;
  public ClassicPacmanGameConstants.mode       currentMode;
  public boolean                               canUseDoor;
  public boolean                               vulnerable;
  public int                                   pixSpeed;
  public ClassicPacmanGameConstants.ghostNames name;

  private BufferedImage opened;
  private BufferedImage openedOutline;
  private BufferedImage closed;
  private BufferedImage closedOutline;
  private final String        profName;


  public Ghost (String profName, Vector2d pos, GhostAI ghostAI, ClassicPacmanGameConstants.ghostNames name)
  {
    ai = ghostAI;
    this.profName = profName;
    this.pos = pos;
    this.name = name;
    movable = true;
    canUseDoor = false;
    vulnerable = false;
    this.hitbox = new Vector2d().cartesian(ClassicPacmanGameConstants.ghostRadius * 2, ClassicPacmanGameConstants.ghostRadius * 2);
    this.direction = Direction.up;

    loadImages("%s%s/%s-".formatted(dirPath, profName, profName));
    ai.setMode(ClassicPacmanGameConstants.mode.EXIT, this);
  }

  private void loadImages (String path)
  {
    try
    {
      closed = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path + "closed.png")));
      opened = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path + "opened.png")));

      closedOutline = TextureEditor.getInstance().createOutline(closed, 3, ai.borderColor);
      openedOutline = TextureEditor.getInstance().createOutline(opened, 3, ai.borderColor);
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
    int           duration  = gameState.tps / animationFrequency;
    BufferedImage im        = ( ( gameState.currentTick % duration ) * 1. / duration > .5 ) ? closed : opened;
    BufferedImage imOutline = ( ( gameState.currentTick % duration ) * 1. / duration > .5 ) ? closedOutline : openedOutline;
    Vector2d      topLeft   = new Vector2d().cartesian(im.getWidth(), im.getHeight()).multiply(-.5).add(pos);
    topLeft.use(g::translate);
    g.drawImage(im, 0, 0, Gui.getInstance().frame);
    g.drawImage(imOutline, 0, 0, Gui.getInstance().frame);
    topLeft.multiply(-1).use(g::translate);


    if (DebugDisplay.getDebugDisplay(gameState).enabled)
    {
      g.translate(pos.x, pos.y);
      //set color to character color
      g.setColor(ai.borderColor);
      //draw debug outline
      g.drawOval((int) -ClassicPacmanGameConstants.ghostRadius, (int) -ClassicPacmanGameConstants.ghostRadius, (int) ( ClassicPacmanGameConstants.ghostRadius * 2 ), (int) ( ClassicPacmanGameConstants.ghostRadius * 2 ));
      Vector2d d = direction.toVector().multiply(gameState.map.tileSize);
      //draw debug looking direction
      g.drawLine(0, 0, (int) d.x, (int) d.y);
      g.translate(-pos.x, -pos.y);

      g.translate(ai.activeTarget.x, ai.activeTarget.y);
      //draw debug target location
      g.fillOval(-10, -10, 20, 20);
      g.translate(-ai.activeTarget.x, -ai.activeTarget.y);
    }


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
          case 1, 2, 3, 4 -> .75;
          default -> .85;
        };
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    //player      velocity           = tileSpeed * speedScale * tileToPixel * pixelPerTick
    double velocity = ClassicPacmanGameConstants.pacmanSpeed * getSpeedScale(gameState) * gameState.map.tileSize * 1. / gameState.tps;
    this.pixSpeed = (int) velocity;
    TotalPosition tp = gameState.map.splitPosition(pos);
    currentTile = gameState.map.tiles.get(tp.ex());

    if (movable)
    {
      //retrieve the position of the targets if BLINKYs pos is available
      if (ai.getBlinkyPos(gameState).size() > 0) ai.setCasePos(gameState, this);

      //skip check if current tile mid has not been reached
      if (currentTile.center.subtract(this.pos).length() < 1) ai.nextDirection2(gameState, this, currentMode);
      //move the ghost
      pos = pos.add(direction.toVector().multiply(velocity));
      //change mode if the reached tile identifies as exit
      if (currentTile.type.equals(PacmanMapTile.Type.ghostExit))
      {
        ai.setMode(ClassicPacmanGameConstants.mode.CHASE, this);
        this.canUseDoor = false;
      }
    }

    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostName, this, String.valueOf(name));
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostPosition, this, "(" + Util.roundTo(pos.x, 0.1) + "," + Util.roundTo(pos.y, 0.1) + ")");
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostDirection, this, String.valueOf(direction));
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostSpeed, this, String.valueOf(pixSpeed));
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostAI, this, ai.getClass().getSimpleName());
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostState, this, String.valueOf(currentMode));
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostVulnerable, this, String.valueOf(this.vulnerable));
    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostTargetDist, this, "(" + Util.roundTo(this.pos.subtract(ai.activeTarget).length(), 0.1) + ")");
  }

}
