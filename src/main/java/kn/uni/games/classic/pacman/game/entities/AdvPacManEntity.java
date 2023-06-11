package kn.uni.games.classic.pacman.game.entities;

import kn.uni.games.classic.pacman.game.entities.ghosts.AdvGhostEntity;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvTimer;
import kn.uni.games.classic.pacman.game.internal.tracker.TagManager;
import kn.uni.games.classic.pacman.game.items.Item;
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

public class AdvPacManEntity extends Entity implements AdvRendered, AdvTicking, AdvColliding
{
  public AdvPacManEntity (AdvGameState gameState, Vector2d mapPos)
  {
    super();
    this.gameState = gameState;
    this.mapPos = mapPos;
    this.stunned = false;

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
            g.setColor((Color) infoData[0]);
            g.draw((Shape) infoData[1]);
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

    g.setColor(Color.YELLOW);
    g.fillOval(0, 0, iconSize, iconSize);
    g.dispose();
  }
  //endregion

  //region updating
  @Override
  public void tick ()
  {
    move();
  }

  private void move ()
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

    tagManager.remove("movement");

    //add new debugInfo
    possibleTiles.forEach(tile ->
        tagManager.addInfo("possibleTile",
            new ArrayList <>(List.of("movement", "shape")),
            new Object[]{ Color.GREEN, new Rectangle((int) tile.absPos.x, (int) tile.absPos.y, AdvGameConst.tileSize, AdvGameConst.tileSize) }
        )
    );

    //region init movement variables
    //set velocity
    if (velocity == null) velocity = new Vector2d().cartesian(AdvGameConst.pacmanSpeedBase, 0).multiply(map.tileSize).divide(AdvGameConst.tps);

    double    stepSize      = velocity.x;
    Direction nextDir       = gameState.requestedDirections.get(gameState.players.indexOf(this));
    Vector2d  innerPos      = map.getTileInnerPos(absPos);
    double    centerDist    = round(this.facing.toVector().scalar(innerPos));
    boolean   nextTileValid = possibleTiles.contains(currentTile.neighbours.get(this.facing));
    boolean   nextDirValid  = possibleTiles.contains(currentTile.neighbours.get(nextDir));
    //endregion

    //region controls turning
    //allows turning vertically if innerTilePosition x == 0 or horizontally if innerTilePosition y == 0
    if (( innerPos.rounded().x == 0 && nextDir.toVector().isVertical() ) || ( innerPos.rounded().y == 0 && nextDir.toVector().isHorizontal() ))
      //if the next direction is a valid tile
      if (nextDirValid)
        //if the next direction is not marked as suppressed
        if (!suppressedDirections.contains(nextDir))
        {
          this.facing = nextDir;
          suppressedDirections.clear();
        }
    //endregion

    //region controls moving
    //stops the entity from moving in a suppressed direction
    if (suppressedDirections.contains(this.facing))
    {
      System.out.println(this.facing + " " + nextDir + " " + suppressedDirections);
      suppressedDirections.clear();
      return;
    }

    //next tile is valid or center has not been reached yet
    if (nextTileValid || centerDist < 0)
    {
      absPos = absPos.add(this.facing.toVector().multiply(stepSize));
      mapPos = map.getTileMapPos(absPos);
      gameState.env.updateLayer.set(AdvGameState.Layer.ENTITIES.ordinal(), true);
    }
    else if (centerDist > 0)
    {
      absPos = currentTilePos.multiply(map.tileSize).add(new Vector2d().cartesian(1, 1).multiply(map.tileSize).divide(2));
      mapPos = map.getTileMapPos(absPos);
      gameState.env.updateLayer.set(AdvGameState.Layer.ENTITIES.ordinal(), true);
    }
    //endregion
  }

  public void die ()
  {
    super.die();

    this.frozen = true;
    this.dead = true;

    gameState.setLives(gameState.lives - 1);

    //check if all players are dead
    if (gameState.players.stream().allMatch(player -> player.dead))
      //reload level
      AdvTimer.getInstance(gameState).ifPresent(timer -> timer.addTask(
          new AdvTimer.TimerTask(gameState.currentTick, (long) ( 120L * 2 ), () ->
          {
            gameState.env.reloadLevel();
            gameState.checkGameOver();
          }), "restart level after the player died"));
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
    if (collider instanceof Item item)
      item.consumeAction();

    if (collider instanceof AdvGhostEntity ghost)
      if (ghost.edible)
      {

      }
      else if (!dead) die();
  }
  //endregion
}
