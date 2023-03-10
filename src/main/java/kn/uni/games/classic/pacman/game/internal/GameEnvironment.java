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
import java.util.concurrent.TimeUnit;
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
      layer.get(i).paintComponent(g2);
    });
    g2.dispose();
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
    //optimal tickduration in nanoseconds
    double            prefTickDuration = 1_000_000_000.0 / gameState.tps;
    LinkedList <Long> times            = new LinkedList <>();

    Thread oldClock = new Thread(() ->
    {
      while (gameState.running)
      {
        if (!gameState.paused)
        {
          long t = System.nanoTime();
          if (t - gameState.lastTickTime < prefTickDuration) continue;
          gameState.currentTick++;

          double buffer = round(( t - gameState.lastTickTime ) / 1_000_000.0);

          gameState.lastTickTime = t;

          times.push(gameState.lastTickTime);
          List <Long> l = times.stream().limit(gameState.tps + 1).toList();
          times.clear();
          times.addAll(l);
          double d = times.getFirst() - times.getLast();

          //          gameState.layers.forEach(layer ->
          //              layer.stream()
          //                   .filter(gameObject -> gameObject instanceof AdvTicking)
          //                   .forEach(gameObject -> ( (AdvTicking) gameObject ).tick()));
          //
          //          IntStream.range(0, 6).filter(i -> updateLayer.get(i)).forEach((i) ->
          //          {
          //            update(i);
          //            updateLayer.set(i, false);
          //          });
          //
          //
          //          if (gameState.currentTick % 2 == 0)
          //          {
          //            render();
          //            display.setFinalImg(finalImg);
          //            display.repaint();
          //          }

          System.out.println("Time/Tps " + d / 1_000_000_000.0 + " | " + "Time per Tick " + buffer + "ms");
        }
      }

    });

    Thread newClock = new Thread(() ->
    {
      while (gameState.running)
      {
        if (!gameState.paused)
        {
          long t = System.nanoTime();
          if (t - gameState.lastTickTime < prefTickDuration) continue;
          gameState.currentTick++;

          double buffer = round(( t - gameState.lastTickTime ) / 1_000_000.0);

          gameState.lastTickTime = t;

          times.push(gameState.lastTickTime);
          List <Long> l = times.stream().limit(gameState.tps + 1).toList();
          times.clear();
          times.addAll(l);
          double d = times.getFirst() - times.getLast();

          //tick all objects
          gameState.layers.forEach(layer ->
              layer.stream()
                   .filter(gameObject -> gameObject instanceof AdvTicking)
                   .forEach(gameObject -> ( (AdvTicking) gameObject ).tick()));

          //reRender layers if needed
          IntStream.range(0, 6).filter(i -> updateLayer.get(i)).forEach((i) ->
          {
            update(i);
            updateLayer.set(i, false);
          });

          //render final image
          if (gameState.currentTick % 2 == 0)
          {
            render();
            display.setFinalImg(finalImg);
            display.repaint();
          }

          System.out.println("Time/Tps " + d / 1_000_000_000.0 + " | " + "Time per Tick " + buffer + "ms");
        }

        try
        {
          Thread.sleep(1);
        }
        catch (InterruptedException e)
        {
          throw new RuntimeException(e);
        }
      }

    });

    //    //a the start of rendering process
    //    long startRendering=System.nanoTime();
    //
    //... rendering here...
    //
    //    //duration of the frame rendering in ms :
    //    long durationMs=TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-startRendering);
    //
    //    // now waits
    //    if (durationMs < fps)
    //    {
    //      renderingThread.sleep(fps - durationMs);
    //    }

    Thread test = new Thread(() ->
    {
      while (gameState.running)
      {
        if (!gameState.paused)
        {
          //time of current tick
          long currentTickTime = System.nanoTime();
          //duration since last successful tick
          long timeSinceLastTick = currentTickTime - gameState.lastTickTime;
          //if last tick was finished before the optimal tick duration, sleep for the remaining time
          long sleepTime = TimeUnit.NANOSECONDS.toMillis((long) ( prefTickDuration - timeSinceLastTick ));
          //          System.out.println("Sleeping for " + sleepTime + "ms");

          //          if (timeSinceLastTick < prefTickDuration)
          //          {
          //            try
          //            {
          //              Thread.sleep(sleepTimeMs, sleepTimeNs);
          //            }
          //            catch (InterruptedException e)
          //            {
          //              throw new RuntimeException(e);
          //            }
          //          }

          gameState.currentTick++;


          gameState.lastTickTime = currentTickTime;

          times.push(gameState.lastTickTime);
          List <Long> l = times.stream().limit(gameState.tps + 1).toList();
          times.clear();
          times.addAll(l);
          double d = times.getFirst() - times.getLast();


          //          //region render
          //          //tick all objects
          //          gameState.layers.forEach(layer ->
          //              layer.stream()
          //                   .filter(gameObject -> gameObject instanceof AdvTicking)
          //                   .forEach(gameObject -> ( (AdvTicking) gameObject ).tick()));
          //
          //          //reRender layers if needed
          //          IntStream.range(0, 6).filter(i -> updateLayer.get(i)).forEach((i) ->
          //          {
          //            update(i);
          //            updateLayer.set(i, false);
          //          });
          //
          //          //render final image
          //          if (gameState.currentTick % 2 == 0)
          //          {
          //            render();
          //            display.setFinalImg(finalImg);
          //            display.repaint();
          //          }
          //          //endregion

          System.out.println("Time/Tps " + d / 1_000_000_000.0 + " | " + "Time per Tick " + round(timeSinceLastTick / 1_000_000.0) + "ms");
        }
      }
    });

    Thread ticker = new Thread(() ->
    {
      boolean running = true;
      boolean paused  = false;

      long tick              = 0;
      long lastTickTime      = System.nanoTime();
      long prefTickDuration2 = 1_000_000_000 / gameState.tps; //in ns, at 120 tps: 8_333_333ns

      while (running)
      {
        if (!paused)
        {
          //region calculate stats and wait for next tick
          //time of current tick
          long currentTickTime = System.nanoTime();
          //duration since last successful tick
          long timeSinceLastTick = currentTickTime - lastTickTime;

          if (timeSinceLastTick < prefTickDuration)
          {
            double buffer = round(( prefTickDuration - timeSinceLastTick ) / 1_000_000.0);
            int    ms     = (int) buffer;
            int    ns     = (int) ( ( buffer - ms ) * 1_000_000 );
            System.out.println("last Tick took: " + timeSinceLastTick + "ns | " + "Sleeping for " + ms + "ms" + " " + ns + "ns");
            try
            {
              Thread.sleep(ms, ns);
            }
            catch (InterruptedException e)
            {
              throw new RuntimeException(e);
            }
          }
          //endregion

          //region tick

          //          //tick all objects
          //          gameState.layers.forEach(layer ->
          //              layer.stream()
          //                   .filter(gameObject -> gameObject instanceof AdvTicking)
          //                   .forEach(gameObject -> ( (AdvTicking) gameObject ).tick()));
          //
          //          //reRender layers if needed
          //          IntStream.range(0, 6).filter(i -> updateLayer.get(i)).forEach((i) ->
          //          {
          //            update(i);
          //            updateLayer.set(i, false);
          //          });
          //
          //          //render final image
          //          if (gameState.currentTick % 2 == 0)
          //          {
          //            render();
          //            display.setFinalImg(finalImg);
          //            display.repaint();
          //          }

          //endregion


          tick += 1;
          lastTickTime = currentTickTime;
        }
      }

    });

    ticker.start();
  }

  public void stop ()
  {
    gameState.running = false;
  }

  public void pause ()
  {
    gameState.paused = false;
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
