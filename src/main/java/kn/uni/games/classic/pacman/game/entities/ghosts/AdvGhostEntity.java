package kn.uni.games.classic.pacman.game.entities.ghosts;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.internal.tracker.TagManager;
import kn.uni.games.classic.pacman.game.map.AdvPacManMap;
import kn.uni.games.classic.pacman.game.map.AdvPacManTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;
import kn.uni.util.fileRelated.TextureEditor;

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
  public double                 speed;

  public AdvGhostEntity (AdvGameState gameState, Vector2d mapPos, AdvGameConst.GhostNames name)
  {
    super();
    this.gameState = gameState;
    this.mapPos = mapPos;
    this.stunned = false;
    this.name = name;
    this.ai = new AdvGhostAi(name, this);
    this.speed = AdvGameConst.speedBase;

    tagManager = new TagManager();
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

      tagManager.getInfoTagged("shape").forEach(info ->
          {
            Object[] infoData = (Object[]) info.value();
            if (info.isTagged("color"))
              g.setColor((Color) infoData[0]);
            if (info.isTagged("fill"))
              g.fill((Shape) infoData[1]);
            else
              g.draw((Shape) infoData[1]);
          }
      );
      tagManager.getInfoTagged("direction").forEach(info ->
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
      tagManager.getInfoNamed("target").forEach(info ->
          {
            Object[] infoData = (Object[]) info.value();
            g.setColor((Color) infoData[0]);
            Vector2d target = (Vector2d) infoData[1];
            g.setStroke(new java.awt.BasicStroke(2));
            g.drawLine((int) absPos.x, (int) absPos.y, (int) target.x, (int) target.y);
          }
      );
      tagManager.getInfoNamed("bestTile").forEach(info ->
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

    Color color;

    switch (name)
    {
      case BLINKY -> color = Color.RED;
      case PINKY -> color = Color.PINK;
      case INKY -> color = Color.CYAN;
      case CLYDE -> color = Color.ORANGE;
      default -> color = Color.WHITE;
    }

    if (Objects.equals(Config.getCurrent("Debugging/-/Enabled"), false))
    {
      String path = "ghosts/classic/";
      try
      {
        cachedImg = TextureEditor.getInstance().loadTexture(path, name.toString().toLowerCase()+".png");
        cachedImg = TextureEditor.getInstance().scale(cachedImg, iconSize, iconSize);
      }
      catch (Exception e)
      {
        g.setColor(color);
        g.fillOval(iconSize / 2 - 10, iconSize / 2 - 10, 20, 20);
      }
    }


    if (Objects.equals(Config.getCurrent("Debugging/-/Enabled"), true))
    {
      g.setColor(color);
      g.fillOval(iconSize / 2 - 10, iconSize / 2 - 10, 20, 20);
    }
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
    AdvPacManMap  map            = gameState.objects.maps().get(0);
    AdvPacManTile currentTile    = map.tilesPixel.get(currentTilePos);

    List <AdvPacManTile> possibleTiles = Arrays.stream(Direction.valuesCardinal())
                                               .filter(dir -> dir != this.facing.opposite())
                                               .map(dir -> currentTile.neighbours.get(dir))
                                               .filter(Objects::nonNull)
                                               .filter(tile -> Arrays.stream(validTiles.values()).map(Enum::name).toList().contains(tile.getType().name()))
                                               .toList();

    //remove old debugInfo from previous tick
    tagManager.remove("movement");

    //add new debugInfo
    possibleTiles.forEach(tile ->
        tagManager.addInfo("possibleTile",
            new ArrayList <>(List.of("debug", "movement", "shape")),
            new Object[]{ Color.ORANGE, new Rectangle((int) tile.absPos.x, (int) tile.absPos.y, AdvGameConst.tileSize, AdvGameConst.tileSize) }
        )
    );

    //region init movement variables
    //set initial velocity
    velocity = new Vector2d().cartesian(this.speed, 0).multiply(AdvGameConst.tileSize).divide(AdvGameConst.tps);

    double    stepSize      = velocity.rounded().x;
    Direction nextDir       = ai.getDirection(currentTile, possibleTiles);
    Vector2d  innerPos      = map.getTileInnerPos(absPos);
    double    centerDist    = round(this.facing.toVector().scalar(innerPos));
    boolean   nextTileValid = possibleTiles.contains(currentTile.neighbours.get(this.facing));
    boolean   nextDirValid  = possibleTiles.contains(currentTile.neighbours.get(nextDir));
    //endregion

    tagManager.addInfo("facing",
        new ArrayList <>(List.of("debug", "movement", "direction")),
        new Object[]{ Color.YELLOW, this.facing }
    );
    tagManager.addInfo("nextDir",
        new ArrayList <>(List.of("debug", "movement", "direction")),
        new Object[]{ Color.MAGENTA, nextDir }
    );


    //region control turning
    //if at center of tile
    if (( innerPos.rounded().x == 0 && nextDir.toVector().isVertical() ) || ( innerPos.rounded().y == 0 && nextDir.toVector().isHorizontal() ))
    {
      //if the next direction is a valid tile
      if (nextDirValid)
        //if the next direction is not marked as suppressed
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

    //next tile is valid or center has not been reached yet
    //move forward
    if (nextTileValid || centerDist < 0)
    {
      absPos = absPos.add(this.facing.toVector().multiply(stepSize));
      mapPos = map.getTileMapPos(absPos);
      gameState.env.updateLayer.set(AdvGameState.Layer.ENTITIES.ordinal(), true);
    }

    //if next tile is under or over shoot
    //move to center of tile to enable turning
    if (Math.abs(centerDist)<stepSize && Math.abs(centerDist) > 0)
    {
      absPos = currentTilePos.multiply(map.tileSize).add(new Vector2d().cartesian(1, 1).multiply(map.tileSize).divide(2));
      mapPos = map.getTileMapPos(absPos);
      gameState.env.updateLayer.set(AdvGameState.Layer.ENTITIES.ordinal(), true);
    }
    suppressedDirections.clear();
    //endregion
  }

  public void die()
  {
    super.die();

    ai.setMode(AdvGameConst.GhostMode.RETREAT);

    //determine the amount of points to add
    if (gameState.ghostStreak == 0)
      gameState.ghostStreak = 1;
    else
      gameState.ghostStreak *= 2;
    gameState.addScore(200L * gameState.ghostStreak);
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
    if (ai.getMode() == AdvGameConst.GhostMode.FRIGHTENED
        && collider instanceof AdvPacManEntity player
        && player.empowered)
    {
      this.die();
    }
  }
  //endregion
}
