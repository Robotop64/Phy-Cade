package kn.uni.games.classic.pacman.game.entities;

import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.map.AdvPacManMap;
import kn.uni.games.classic.pacman.game.map.AdvPacManTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.PacPhiConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
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
  }

  //region graphics
  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();


    //    AdvPacManMap  map               = (AdvPacManMap) gameState.layers.get(1).getFirst();
    //    Vector2d      currentTilePosMap = map.getTileMapPos(this.absPos);
    //    Vector2d      currentTilePosAbs = currentTilePosMap.multiply(map.tileSize);
    //    AdvPacManTile currentTile       = map.tilesPixel.get(currentTilePosMap);
    //
    //    if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
    //    {
    //      //      g.setColor(Color.GREEN);
    //      //      g.fillRect((int) currentTilePosAbs.x, (int) currentTilePosAbs.y, iconSize, iconSize);
    //      List <AdvPacManTile> possibleTiles = Arrays.stream(Direction.valuesCardinal())
    //                                                 .map(dir -> currentTile.neighbors.get(dir))
    //                                                 .filter(Objects::nonNull)
    //                                                 .filter(tile -> Arrays.stream(validTiles.values()).toList().contains(tile.getType()))
    //                                                 .toList();
    //
    //      //        map.tilesPixel.forEach((pos, tile) -> tile.debugColor = tile.debugColor == Color.GREEN ? tile.primitiveColor : tile.debugColor);
    //      //      map.tilesPixel.forEach((pos, tile) ->
    //      //      {
    //      //        tile.debugColor = tile.debugColor == Color.GREEN ? tile.primitiveColor : tile.debugColor;
    //      //        tile.render();
    //      //      });
    //      currentTile.neighbors.forEach((dir, tile) ->
    //      {
    //        tile.debugColor = Color.GREEN;
    //        tile.primitiveColor = Color.GREEN;
    //        tile.render();
    //      });
    //      currentTile.debugColor = Color.RED;
    //      currentTile.primitiveColor = Color.RED;
    //      currentTile.render();
    //
    //      AdvPacManTile next = currentTile.neighbors.get(this.facing);
    //      if (next != null)
    //      {
    //        next.debugColor = Color.BLUE;
    //        next.primitiveColor = Color.BLUE;
    //        next.render();
    //      }
    //
    //      map.render();
    //      gameState.env.updateLayer.set(1, true);
    //    }

    g.drawImage(cachedImg, (int) absPos.x - iconSize / 2, (int) absPos.y - iconSize / 2, null);

    if (PacPhiConfig.getInstance().checkSetting("Debugging", "-", "Enabled", true))
    {
      g.setColor(Color.RED);
      g.drawOval(
          (int) ( absPos.x - AdvGameConst.hitBoxes.get("AdvPacManEntity") * AdvGameConst.tileSize ),
          (int) ( absPos.y - AdvGameConst.hitBoxes.get("AdvPacManEntity") * AdvGameConst.tileSize ),
          (int) ( AdvGameConst.hitBoxes.get("AdvPacManEntity") * 2 * AdvGameConst.tileSize ),
          (int) ( AdvGameConst.hitBoxes.get("AdvPacManEntity") * 2 * AdvGameConst.tileSize ));
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

    if (PacPhiConfig.getInstance().checkSetting("Graphics", "Advanced", "Antialiasing", true))
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
    if (!stunned)
    {
      Vector2d      currentTilePos = getMapPos();
      AdvPacManMap  map            = (AdvPacManMap) gameState.layers.get(AdvGameState.Layer.MAP.ordinal()).getFirst();
      AdvPacManTile currentTile    = map.tilesPixel.get(currentTilePos);

      List <AdvPacManTile> possibleTiles = Arrays.stream(Direction.valuesCardinal())
                                                 .map(dir -> currentTile.neighbors.get(dir))
                                                 .filter(Objects::nonNull)
                                                 .filter(tile -> Arrays.stream(validTiles.values()).map(Enum::name).toList().contains(tile.getType().name()))
                                                 .toList();

      //set velocity
      if (velocity == null) velocity = new Vector2d().cartesian(AdvGameConst.pacmanSpeed, 0).multiply(map.tileSize).divide(AdvGameConst.tps);

      double    stepSize      = velocity.x;
      Direction nextDir       = gameState.requestedDirections.get(gameState.players.indexOf(this));
      Vector2d  innerPos      = map.getTileInnerPos(absPos);
      double    centerDist    = round(this.facing.toVector().scalar(innerPos));
      boolean   centerReached = centerDist == 0;
      boolean   nextTileValid = possibleTiles.contains(currentTile.neighbors.get(this.facing));
      boolean   nextDirValid  = possibleTiles.contains(currentTile.neighbors.get(nextDir));


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

      //stats
      //      System.out.println(""
      //          + "centerDist: " + centerDist
      //          + " nextTileValid: " + nextTileValid
      //          + " nextDirValid: " + nextDirValid
      //          + " centerReached: " + centerReached
      //          + " facing: " + this.facing
      //          + " nextDir: " + nextDir
      //          + " absPos: " + absPos
      //          + " currentTilePos: "+ currentTilePos
      //          + " currentTile: " + currentTile
      //          + " possibleTiles: " + possibleTiles);

    }
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
  }
  //endregion
}
