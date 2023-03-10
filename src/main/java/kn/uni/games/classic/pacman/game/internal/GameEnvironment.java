package kn.uni.games.classic.pacman.game.internal;

import kn.uni.games.classic.pacman.game.entities.Spawner;
import kn.uni.games.classic.pacman.game.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.objects.AdvPacManMap;
import kn.uni.util.Direction;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    Thread tickerRestructure = new Thread(() ->
    {
      boolean running           = true;
      boolean paused            = false;
      double  prefTickDuration2 = ( 1_000_000_000.0 / gameState.tps ) / 1_000_000.0; //in ms, at 120 tps: 8_333_333ns

      long tick = 0;

      long   tickStart    = System.nanoTime();
      long   tickEnd      = System.nanoTime();
      double tickDuration = 0;

      long   workStart    = System.nanoTime();
      long   workEnd      = System.nanoTime();
      double workDuration = 0;

      double sleepDuration = 0;

      while (running)
      {
        if (!paused)
        {
          tickDuration = round(( tickEnd - tickStart ) / 1_000_000.0); //duration of last tick in ms
          //          System.out.println("Tick " + tick + ": last tick took: " + tickDuration + "ms | " + "Work took: " + workDuration + "ms" + " | " + "Sleeping: " + sleepDuration + "ms");
          tickStart = System.nanoTime();

          workStart = System.nanoTime();
          //region tick

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

          if (tick == 250)
          {
            AdvPacManMap map = (AdvPacManMap) gameState.layers.get(1).getFirst();
            map.spawnables.stream()
                          .filter(obj -> obj instanceof Spawner)
                          .map(obj -> (Spawner) obj)
                          .filter(spawner -> spawner.name.equals("PlayerSpawn"))
                          .forEach(Spawner::spawn);
          }

          //endregion
          workEnd = System.nanoTime();

          workDuration = round(( workEnd - workStart ) / 1_000_000.0);
          sleepDuration = round(( prefTickDuration2 - workDuration ));

          if (workDuration < prefTickDuration2)
          {
            int ms = (int) sleepDuration;
            int ns = (int) ( ( sleepDuration - ms ) * 1_000_000 );
            try
            {
              Thread.sleep(ms, ns);
            }
            catch (InterruptedException e)
            {
              throw new RuntimeException(e);
            }
          }

          tick += 1;

          tickEnd = System.nanoTime();
        }
      }

    });

    Thread online = new Thread(() ->
    {
      int     TPS     = 120, FPS = 60;
      boolean running = true;
      boolean paused  = false;

      long         initialTime = System.nanoTime();
      final double timeU       = 1000000000. / TPS;
      final double timeF       = 1000000000. / FPS;
      double       deltaU      = 0, deltaF = 0;
      int          frames      = 0, ticks = 0;
      long         timer       = System.currentTimeMillis();

      while (running)
      {

        long currentTime = System.nanoTime();
        deltaU += ( currentTime - initialTime ) / timeU;
        deltaF += ( currentTime - initialTime ) / timeF;
        initialTime = currentTime;

        if (deltaU >= 1)
        {
          //        getInput();
          //        update();
          ticks++;
          deltaU--;
        }

        if (deltaF >= 1)
        {
          render();
          frames++;
          deltaF--;
        }

        if (System.currentTimeMillis() - timer > 1000)
        {

          System.out.println(String.format("UPS: %s, FPS: %s", ticks, frames));

          frames = 0;
          ticks = 0;
          timer += 1000;
        }
      }
    });

    tickerRestructure.start();
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
    if (player > 0 && gameState.players.size() >= player)
      gameState.requestedDirections.set(player - 1, nextDir);
  }
}
