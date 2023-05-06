package kn.uni.games.classic.pacman.game.entities.ghosts;

import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.internal.tracker.DebugManager;
import kn.uni.games.classic.pacman.game.map.AdvPacManMap;
import kn.uni.games.classic.pacman.game.map.AdvPacManTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static kn.uni.util.Util.round;

public class AdvGhostEntity extends Entity implements AdvTicking, AdvRendered, AdvColliding
{
  public AdvGhostAi              ai;
  public AdvGameConst.GhostNames name;

  public AdvGhostEntity (AdvGameState gameState, Vector2d mapPos, AdvGameConst.GhostNames name)
  {
    super();
    this.gameState = gameState;
    this.mapPos = mapPos;
    this.stunned = false;
    this.name = name;
    this.ai = new AdvGhostAi(name, this);

    debugManager = new DebugManager();
  }

  //region graphics
  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();

    g.drawImage(cachedImg, (int) absPos.x - iconSize / 2, (int) absPos.y - iconSize / 2, null);

    if (Objects.equals(Config.getCurrent("Debugging/-/Enabled"), true))
    {
      g.setColor(Color.RED);
      g.drawOval(
          (int) ( absPos.x - AdvGameConst.hitBoxes.get("AdvPacManEntity") * AdvGameConst.tileSize ),
          (int) ( absPos.y - AdvGameConst.hitBoxes.get("AdvPacManEntity") * AdvGameConst.tileSize ),
          (int) ( AdvGameConst.hitBoxes.get("AdvPacManEntity") * 2 * AdvGameConst.tileSize ),
          (int) ( AdvGameConst.hitBoxes.get("AdvPacManEntity") * 2 * AdvGameConst.tileSize ));

      debugManager.getInfoTagged("shape").forEach(info ->
          {
            Object[] infoData = (Object[]) info.value();
            g.setColor((Color) infoData[0]);
            g.draw((Shape) infoData[1]);
          }
      );
      debugManager.getInfoTagged("direction").forEach(info ->
          {
            Object[] infoData = (Object[]) info.value();
            g.setColor((Color) infoData[0]);
            Direction dir = (Direction) infoData[1];
            Vector2d  vec = dir.toVector();

            int width  = (int) ( Math.abs(AdvGameConst.tileSize * vec.x) + 5 );
            int height = (int) ( Math.abs(AdvGameConst.tileSize * vec.y) + 5 );

            if (dir == Direction.right || dir == Direction.down)
              g.fillRect((int) absPos.x, (int) absPos.y, width, height);
            if (dir == Direction.left || dir == Direction.up)
            {
              g.rotate(Math.toRadians(180), absPos.x, absPos.y);
              g.fillRect((int) absPos.x, (int) absPos.y, width, height);
              g.rotate(Math.toRadians(-180), absPos.x, absPos.y);
            }
          }
      );
      debugManager.getInfoNamed("target").forEach(info ->
          {
            Object[] infoData = (Object[]) info.value();
            g.setColor((Color) infoData[0]);
            Vector2d target = (Vector2d) infoData[1];
            g.setStroke(new java.awt.BasicStroke(2));
            g.drawLine((int) absPos.x, (int) absPos.y, (int) target.x, (int) target.y);
          }
      );
      debugManager.getInfoNamed("bestTile").forEach(info ->
          {
            Object[] infoData = (Object[]) info.value();
            g.setColor((Color) infoData[0]);
            Vector2d target = (Vector2d) infoData[1];
            g.setStroke(new java.awt.BasicStroke(2));
            g.drawLine((int) absPos.x, (int) absPos.y, (int) target.x, (int) target.y);
          }
      );
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

    switch (name)
    {
      case BLINKY -> g.setColor(Color.RED);
      case PINKY -> g.setColor(Color.PINK);
      case INKY -> g.setColor(Color.CYAN);
      case CLYDE -> g.setColor(Color.ORANGE);
      default -> g.setColor(Color.GRAY);
    }

    g.fillOval(iconSize / 2 - 10, iconSize / 2 - 10, 20, 20);
    g.dispose();
  }
  //endregion

  //region update
  @Override
  public void tick ()
  {
    move();
  }

  public void move ()
  {
    if (stunned) return;

    Vector2d      currentTilePos = getMapPos();
    AdvPacManMap  map            = (AdvPacManMap) gameState.layers.get(AdvGameState.Layer.MAP.ordinal()).getFirst();
    AdvPacManTile currentTile    = map.tilesPixel.get(currentTilePos);

    List <AdvPacManTile> possibleTiles = Arrays.stream(Direction.valuesCardinal())
                                               .map(dir -> currentTile.neighbours.get(dir))
                                               .filter(Objects::nonNull)
                                               .filter(tile -> Arrays.stream(validTiles.values()).map(Enum::name).toList().contains(tile.getType().name()))
                                               .toList();

    //remove old debugInfo from previous tick
    debugManager.remove("movement");

    //add new debugInfo
    possibleTiles.forEach(tile ->
        debugManager.addInfo("possibleTile",
            new ArrayList <>(List.of("movement", "shape")),
            new Object[]{ Color.GREEN, new Rectangle((int) tile.absPos.x, (int) tile.absPos.y, AdvGameConst.tileSize, AdvGameConst.tileSize) }
        )
    );

    //region init movement variables
    //set initial velocity
    if (velocity == null) velocity = new Vector2d().cartesian(AdvGameConst.ghostSpeedBase, 0).multiply(AdvGameConst.tileSize).divide(AdvGameConst.tps);

    double    stepSize      = velocity.x;
    Vector2d  innerPos      = map.getTileInnerPos(absPos);
    double    centerDist    = round(this.facing.toVector().scalar(innerPos));
    boolean   nextTileValid = possibleTiles.contains(currentTile.neighbours.get(this.facing));
    Direction nextDir       = ai.getDirection(currentTile, possibleTiles);
    boolean   nextDirValid  = possibleTiles.contains(currentTile.neighbours.get(nextDir));
    //endregion

    debugManager.addInfo("facing",
        new ArrayList <>(List.of("movement", "direction")),
        new Object[]{ Color.YELLOW, this.facing }
    );
    debugManager.addInfo("nextDir",
        new ArrayList <>(List.of("movement", "direction")),
        new Object[]{ Color.MAGENTA, nextDir }
    );

    //    System.out.println(innerPos.rounded());
    //region control turning
    //allows turning vertically if innerTilePosition x == 0 or horizontally if innerTilePosition y == 0
    if (( innerPos.rounded().x == 0 && nextDir.toVector().isVertical() ) || ( innerPos.rounded().y == 0 && nextDir.toVector().isHorizontal() ))
      //if the next direction is a valid tile
      if (nextDirValid)
      {
        //if the next direction is not marked as suppressed turn into it and clear the suppressed directions
        if (!suppressedDirections.contains(nextDir))
        {
          this.facing = nextDir;
          suppressedDirections.clear();
        }
      }
    //endregion

    //region control movement
    //stops the entity from moving in a suppressed direction
    if (suppressedDirections.contains(this.facing))
    {
      suppressedDirections.clear();
      return;
    }

    Vector2d currentPos     = absPos;
    Vector2d tileCenter     = currentTilePos.multiply(map.tileSize).add(new Vector2d().cartesian(1, 1).multiply(map.tileSize).divide(2));
    Vector2d nextPos        = currentPos.add(this.facing.toVector().multiply(stepSize * 0.85));
    double   nextCenterDist = round(this.facing.toVector().scalar(nextPos));

    System.out.println("reset?: " + ( centerDist < 0 && nextCenterDist > 0 ) + " | centerDist: " + centerDist + " | innerPos: " + innerPos);

    //center would have been passed over (turning not possible), reset position to center
    if (( centerDist < 0 && nextCenterDist > 0 ) || ( centerDist > 0 && !nextTileValid ))
    {
      absPos = tileCenter;
      mapPos = map.getTileMapPos(absPos);
      gameState.env.updateLayer.set(AdvGameState.Layer.ENTITIES.ordinal(), true);
      //          return;
    }


    //next tile is valid or center has not been reached yet (distance to center is negative, positive if center has been passed)
    else if (nextTileValid || centerDist < 0)//(centerDist < 0 || ( centerDist > 0 && nextTileValid ))
    {
      absPos = nextPos;
      mapPos = map.getTileMapPos(absPos);
      //      gameState.env.updateLayer.set(AdvGameState.Layer.ENTITIES.ordinal(), true);
    }
    gameState.env.updateLayer.set(AdvGameState.Layer.ENTITIES.ordinal(), true);
    //endregion
  }
  //endregion

  @Override
  public void onCollision (AdvGameObject collider)
  {
  }
}
