package kn.uni.games.classic.pacman.game.entities;

import kn.uni.games.classic.pacman.game.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.internal.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.games.classic.pacman.game.objects.AdvPacManMap;
import kn.uni.games.classic.pacman.game.objects.AdvPacManTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static kn.uni.util.Util.round;

public class AdvPacManEntity extends Entity implements AdvRendered, AdvTicking
{

  public AdvPacManEntity (AdvGameState gameState, Vector2d pos)
  {
    super();
    this.gameState = gameState;
    this.absPos = pos;
    this.stunned = false;
  }

  //region graphics
  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();


    //    AdvPacManMap map            = (AdvPacManMap) gameState.layers.get(1).getFirst();
    //    Vector2d     currentTilePos = map.getTileMapPos(this.absPos).multiply(map.tileSize);
    //
    //    if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
    //    {
    //      g.setColor(Color.GREEN);
    //      g.fillRect((int) currentTilePos.x, (int) currentTilePos.y, iconSize, iconSize);
    //    }

    g.drawImage(cachedImg, (int) absPos.x - iconSize / 2, (int) absPos.y - iconSize / 2, iconSize, iconSize, null);
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

    g.setColor(Color.YELLOW);
    g.fillOval(0, 0, iconSize, iconSize);
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
      AdvPacManMap  map            = (AdvPacManMap) gameState.layers.get(1).getFirst();
      AdvPacManTile currentTile    = map.tilesPixel.get(currentTilePos);

      List <AdvPacManTile> possibleTiles = Arrays.stream(Direction.valuesCardinal())
                                                 .map(dir -> currentTile.neighbors.get(dir))
                                                 .filter(Objects::nonNull)
                                                 .filter(tile -> Arrays.stream(validTiles.values()).toList().contains(tile.getType()))
                                                 //                                                 .filter(tile -> !tile.type.equals(AdvPacManTile.Type.door) || canUseDoor)
                                                 .toList();

      //visual feedback of possible tiles
      //      if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
      //      {
      //        map.tilesPixel.forEach((pos, tile) -> tile.debugColor = tile.debugColor == Color.GREEN ? tile.primitiveColor : tile.debugColor);
      //        possibleTiles.forEach(tile -> tile.debugColor = Color.GREEN);
      //        currentTile.debugColor = Color.RED;
      //      }

      //set velocity
      if (velocity == null) velocity = new Vector2d().cartesian(AdvGameConst.pacmanSpeed, 0).multiply(map.tileSize).divide(gameState.tps);

      //next tile is valid or center has not been reached yet
      if (possibleTiles.contains(currentTile.neighbors.get(this.facing.toVector())) || round(this.facing.toVector().scalar(map.getTileInnerPos(absPos))) <= 0)
      {
      }
      absPos = absPos.add(this.facing.toVector().multiply(velocity.x));

      gameState.env.updateLayer.set(1, true);
      gameState.env.updateLayer.set(3, true);
      gameState.env.updateLayer.set(4, true);
      gameState.env.updateLayer.set(5, true);


    }

  }


  //endregion
}
