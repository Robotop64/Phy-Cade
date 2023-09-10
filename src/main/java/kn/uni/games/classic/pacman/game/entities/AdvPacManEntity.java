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
import kn.uni.games.classic.pacman.game.items.PPelletItem;
import kn.uni.games.classic.pacman.game.items.PelletItem;
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
  public boolean empowered = false;

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

    if (Objects.equals(Config.getCurrent("Debugging/DebugView"), true))
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
    AdvPacManMap  map            = gameState.objects.maps().get(0);
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
    velocity = new Vector2d().cartesian(gameState.pacSpeed, 0).multiply(map.tileSize).divide(AdvGameConst.tps);

    double    stepSize      = velocity.rounded().x;
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
    if (Math.abs(centerDist) < stepSize && Math.abs(centerDist) > 0)
    {
      absPos = currentTilePos.multiply(map.tileSize).add(new Vector2d().cartesian(1, 1).multiply(map.tileSize).divide(2));
      mapPos = map.getTileMapPos(absPos);
      gameState.env.updateLayer.set(AdvGameState.Layer.ENTITIES.ordinal(), true);
    }
    suppressedDirections.clear();
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
      AdvTimer.getInstance(gameState).addTask(
          new AdvTimer.TimerTask(gameState.currentTick, 120L * 2, () ->
          {
            gameState.checkGameOver();
            gameState.env.reloadLevel();
          }, "restartPostDeath"), "restart level after the player died");
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
    if (collider instanceof Item item)
    {
      item.consumeAction();

      if( item instanceof PelletItem || item instanceof PPelletItem )
      {
        gameState.timeSinceLastPellet = 0;
      }

      if (item instanceof PPelletItem)
      {
        empowered = true;

        gameState.objects.entities().stream()
                         .filter(entity -> entity instanceof AdvGhostEntity)
                         .map(entity -> (AdvGhostEntity) entity)
                         .filter(ghost -> ghost.ai.mode == AdvGameConst.GhostMode.CHASE)
                         .forEach(ghost -> ghost.ai.setMode(AdvGameConst.GhostMode.FRIGHTENED));

        //remove previous powerUpTimers
        AdvTimer timer = AdvTimer.getInstance(gameState);

        timer.removeTask("powerUpTimer");
        timer.removeTask("pacAnimBlink");

        //TODO change animation/sprite

        timer.addTask(
            new AdvTimer.TimerTask(gameState.currentTick, 120L * 7, () ->
            { /*TODO change animation/sprite to blinking*/ }, "pacAnimBlink"),
            "change Pacman Animation to blinking after 7 seconds"
        );

        timer.addTask(
            new AdvTimer.TimerTask(gameState.currentTick, 120L * 10, () ->
            {
              empowered = false;
              gameState.ghostStreak = 0;
              gameState.objects.entities().stream()
                               .filter(entity -> entity instanceof AdvGhostEntity)
                               .map(entity -> (AdvGhostEntity) entity)
                               .filter(ghost -> ghost.ai.mode == AdvGameConst.GhostMode.FRIGHTENED)
                               .forEach(ghost -> ghost.ai.setMode(AdvGameConst.GhostMode.CHASE));
              /*TODO change animation/sprite back to normal*/
            }, "powerUpTimer"),
            "remove empowerment of PacMan after 10 seconds"
        );

      }
    }

    if (collider instanceof AdvGhostEntity ghost)
      if (ghost.ai.mode == AdvGameConst.GhostMode.FRIGHTENED)
      {
        ghost.die();
      }
      else if (!dead && (ghost.ai.mode == AdvGameConst.GhostMode.CHASE || ghost.ai.mode == AdvGameConst.GhostMode.SCATTER))
      {
        if (Objects.equals(Config.getCurrent("Debugging/Immortal"), false))
          die();
      }
  }
  //endregion
}
