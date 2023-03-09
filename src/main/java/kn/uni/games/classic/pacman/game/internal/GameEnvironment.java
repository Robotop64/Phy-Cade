package kn.uni.games.classic.pacman.game.internal;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.objects.AdvPacManMap;
import kn.uni.games.classic.pacman.game.objects.AdvPacManTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static kn.uni.util.Util.round;

public class GameEnvironment
{
  public GameDisplay   display;
  public BufferedImage finalImg;
  public AdvGameState  gameState;

  //background[0], map[1], objects[2], items[3], entities[4], vfx[5], frame[6]
  public List <GameLayer> layer       = new ArrayList <>();
  public List <Boolean>   updateLayer = new ArrayList <>();

  public GameEnvironment (Dimension dim)
  {
    display = new GameDisplay(dim);

    gameState = new AdvGameState(this);

    //region fill displayStack with panels
    IntStream.range(0, 6).forEach(i ->
    {
      GameLayer panel = new GameLayer(dim, i);
      panel.gameState = gameState;
      layer.add(panel);
      updateLayer.add(true);
    });
    //endregion
  }

  public void render ()
  {
    finalImg = new BufferedImage(display.getWidth(), display.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = finalImg.createGraphics();

    IntStream.range(0, 6).forEach(i ->
    {
      GameLayer panel = layer.get(i);
      if (panel.cachedImg == null)
        panel.render();
      g2.drawImage(panel.cachedImg, 0, 0, null);
    });
  }


  public JPanel getDisplay ()
  {
    return display;
  }

  public AdvGameState getGameState ()
  {
    return gameState;
  }

  public void updateAll ()
  {
    layer.forEach(GameLayer::render);
  }

  public void update (int index)
  {
    ( layer.get(index) ).render();
  }


  public void start ()
  {
    gameState.running = true;
    gameState.paused = false;
    gameState.currentTick = 0;
    gameState.lastTickTime = System.nanoTime();
    double            tickDuration = 1_000_000_000.0 / gameState.tps;
    LinkedList <Long> times        = new LinkedList <>();

    Thread clock = new Thread(() ->
    {
      while (gameState.running)
      {
        if (!gameState.paused)
        {
          long t = System.nanoTime();
          if (t - gameState.lastTickTime < tickDuration) continue;
          gameState.currentTick++;
          gameState.lastTickTime = t;

          times.push(gameState.lastTickTime);
          List <Long> l = times.stream().limit(gameState.tps + 1).toList();
          times.clear();
          times.addAll(l);
          double d = times.getFirst() - times.getLast();

          gameState.layers.forEach(layer ->
              layer.stream()
                   .filter(gameObject -> gameObject instanceof AdvTicking)
                   .forEach(gameObject -> ( (AdvTicking) gameObject ).tick()));

          IntStream.range(0, 6).filter(i -> updateLayer.get(i)).forEach((i) ->
          {
            update(i);
            updateLayer.set(i, false);
          });


          if (gameState.currentTick % 2 == 0)
          {
            render();
            display.setFinalImg(finalImg);
            display.repaint();
          }


          System.out.println(d / 1_000_000_000.0);
        }

        //        try
        //        {
        //          Thread.sleep(20);
        //        }
        //        catch (InterruptedException e)
        //        {
        //          throw new RuntimeException(e);
        //        }
      }

    });

    clock.start();
  }

  public void stop ()
  {
    gameState.running = false;
  }

  public void pause ()
  {
    gameState.running = false;
  }

  public void resume ()
  {
    gameState.running = true;
  }

  public void controlPlayer (int player, Direction nextDir)
  {
    AdvPacManEntity playerEntity   = gameState.players.get(player - 1);
    Vector2d        currentTilePos = playerEntity.getMapPos();
    AdvPacManMap    map            = (AdvPacManMap) gameState.layers.get(1).getFirst();
    AdvPacManTile   currentTile    = map.tilesPixel.get(currentTilePos);

    List <AdvPacManTile> possibleTiles = Arrays.stream(Direction.valuesCardinal())
                                               .map(dir -> currentTile.neighbors.get(dir))
                                               .filter(Objects::nonNull)
                                               .filter(tile -> Arrays.stream(Entity.validTiles.values()).toList().contains(tile.getType()))
                                               //                                                 .filter(tile -> !tile.type.equals(AdvPacManTile.Type.door) || canUseDoor)
                                               .toList();

    //check if requested direction is possible
    if (possibleTiles.contains(currentTile.neighbors.get(nextDir.toVector())) && round(playerEntity.facing.toVector().scalar(map.getTileInnerPos(playerEntity.absPos))) == 0)
      playerEntity.facing = nextDir;

    playerEntity.facing = nextDir;
  }
}
