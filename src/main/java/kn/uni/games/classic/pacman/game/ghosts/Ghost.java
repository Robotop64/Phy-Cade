package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.*;
import kn.uni.games.classic.pacman.game.ClassicPacmanMap.TotalPosition;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Ghost extends PlacedObject implements Ticking, Rendered
{
  private final int           animationFrequency = 8;
  private final String        dirPath            = "pacman/textures/ghosts/";
  public        GhostAI       ai;
  public        Vector2d      pos;
  private       BufferedImage opened;
  private       BufferedImage closed;
  private       PacmanMapTile targetTile;
  private       Direction     direction;


  public Ghost (String profName, Vector2d pos, GhostAI ghostAI)
  {
    loadImages("%s%s/%s-".formatted(dirPath, profName, profName));
    this.pos = pos;
    ai = ghostAI;
    movable = true;
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
    direction = Direction.up;

    /*
    gameState.gameObjects.stream()
            .filter(gameObject -> gameObject instanceof PlacedObject)
            .filter(gameObject -> gameObject instanceof PacmanObject)
            .forEach(gameObject -> g.drawLine((int)this.pos.x,(int)this.pos.y,(int)((PacmanObject) gameObject).pos.x,(int)((PacmanObject) gameObject).pos.y));
     */
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
    double        v           = ClassicPacmanGameConstants.pacmanSpeed * getSpeedScale(gameState) * gameState.map.tileSize * 1. / gameState.tps;
    TotalPosition tp          = gameState.map.splitPosition(pos);
    int           ts          = gameState.map.tileSize;
    PacmanMapTile currentTile = gameState.map.tiles.get(tp.ex());

    if (movable){
      if (targetTile == null) {
        targetTile = Arrays.stream(Direction.values())
                .map(direction -> direction.toVector())
                .map(vec -> currentTile.neighbors.get(vec))
                .filter(Objects::nonNull)
                .filter(tile -> PacmanMapTile.walkable.contains(tile.type)).findFirst().get();
        //      direction = Arrays.stream(Direction.values()).filter(dir -> currentTile.neighbors.get(dir.toVector()) == targetTile).findFirst().get();
      }

      Vector2d direction = gameState.map.splitPosition(targetTile.pos).ex().subtract(gameState.map.splitPosition(pos).ex());

      if (currentTile == targetTile) {
        //      System.out.println("reached target tile");
        if (direction.scalar(tp.in()) < 0) {
          //        System.out.println("moving towards centre");
          pos = pos.add(direction.multiply(v));
        } else {
          //        System.out.println("reached centre, looking for new target");
          do {
            //          System.out.println("rerolling");
            targetTile = currentTile.neighbors.get(ai.getNextDirection(gameState).toVector());
          } while (targetTile == null || !PacmanMapTile.walkable.contains(targetTile.type));
          //        System.out.println("found new target");
        }
      } else {
        //      System.out.println("walking towards target");
        pos = pos.add(direction.multiply(v));
      }
    }

    final PacmanObject[] pac = new PacmanObject[1];

    gameState.gameObjects.stream()
            .filter(pacman -> pacman instanceof PlacedObject)
            .filter(pacman -> pacman instanceof PacmanObject)
            .forEach(pacman-> pac[0] = (PacmanObject) pacman);

    double critDist = ClassicPacmanGameConstants.ghostRadius + pac[0].r;

    double dist = pac[0].pos.subtract(this.pos).lenght();

    if (dist < critDist && pac[0].playerDead==false)
    {
       pac[0].death(gameState);
      pac[0].movable = false;
      gameState.gameObjects.stream()
              .filter(ghost -> ghost instanceof PlacedObject)
              .filter(ghost -> ghost instanceof Ghost)
              .forEach(ghost -> ((Ghost) ghost).movable = false);
    }
  }
}
