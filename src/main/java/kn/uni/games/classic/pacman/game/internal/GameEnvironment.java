package kn.uni.games.classic.pacman.game.internal;

import kn.uni.PacPhi;
import kn.uni.games.classic.pacman.game.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.items.PelletItem;
import kn.uni.games.classic.pacman.game.objects.AdvPacManMap;
import kn.uni.games.classic.pacman.screens.AdvGameScreen;
import kn.uni.util.Direction;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static kn.uni.util.Util.round;

public class GameEnvironment
{
  public AdvGameScreen gameScreen;
  public GameDisplay   display;
  public AdvGameState  gameState;

  public List <GameLayer> layer       = new ArrayList <>();
  public List <Boolean>   updateLayer = new ArrayList <>();


  public GameEnvironment (Dimension dim)
  {
    display = new GameDisplay(dim);

    gameState = new AdvGameState(this);

    //region fill displayStack with panels
    IntStream.range(0, AdvGameState.Layer.values().length).forEach(i ->
    {
      GameLayer panel = new GameLayer(dim, i);
      panel.gameState = gameState;
      layer.add(panel);
      updateLayer.add(true);
    });
    //endregion

    display.setLayers(layer);
  }

  //region display methods
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
  //endregion

  //region control game status
  public void start ()
  {
    boolean running = true;
    gameState.paused = false;

    gameState.currentTick = 0;
    gameState.lastTickTime = System.nanoTime();
    //optimal tickduration in nanoseconds
    double            prefTickDuration = 1_000_000_000.0 / gameState.tps;
    LinkedList <Long> times            = new LinkedList <>();

    Thread oldClock = new Thread(() ->
    {
      while (running)
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

          gameState.layers.forEach(layer ->
              layer.stream()
                   .filter(gameObject -> gameObject instanceof AdvTicking)
                   .forEach(gameObject -> ( (AdvTicking) gameObject ).tick()));

          //          //reRender layers if needed
          //          if (gameState.currentTick % 2 == 0)
          //          {
          //            IntStream.range(0, AdvGameState.Layer.values().length).filter(i -> updateLayer.get(i)).forEach((i) ->
          //            {
          //              update(i);
          //              updateLayer.set(i, false);
          //            });
          //          }
          //
          //          //render final image
          //          if (gameState.currentTick % 2 == 0)
          //          {
          //            display.repaint();
          //          }

          System.out.println("Time/Tps " + d / 1_000_000_000.0 + " | " + "Time per Tick " + buffer + "ms");
        }
      }

    });

    Thread ticker = new Thread(() ->
    {
      //      boolean running = true;
      boolean paused = false;

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

    int a = 0;

    Thread tickerRestructure = new Thread(() ->
    {
      //      boolean running           = true;
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
          System.out.println("Tick " + tick + ": last tick took: " + tickDuration + "ms | " + "Work took: " + workDuration + "ms" + " | " + "Sleeping: " + sleepDuration + "ms");
          tickStart = System.nanoTime();

          workStart = System.nanoTime();
          //region tick

          //          loop();
          //
          //          if (tick == 250)
          //          {
          //            AdvPacManMap map = (AdvPacManMap) gameState.layers.get(1).getFirst();
          //            map.spawnables.stream()
          //                          .filter(obj -> obj instanceof Spawner)
          //                          .map(obj -> (Spawner) obj)
          //                          .filter(spawner -> spawner.name.equals("PlayerSpawn"))
          //                          .forEach(Spawner::spawn);
          //          }

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
              Thread.sleep(1);
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

    int b = 0;
    //    Thread online = new Thread(() ->
    //    {
    //      int TPS = 120, FPS = 60;
    //      //      boolean running = true;
    //      boolean paused = false;
    //
    //      long         initialTime = System.nanoTime();
    //      final double timeU       = 1000000000. / TPS;
    //      final double timeF       = 1000000000. / FPS;
    //      double       deltaU      = 0, deltaF = 0;
    //      int          frames      = 0, ticks = 0;
    //      long         timer       = System.currentTimeMillis();
    //
    //      while (running)
    //      {
    //
    //        long currentTime = System.nanoTime();
    //        deltaU += ( currentTime - initialTime ) / timeU;
    //        deltaF += ( currentTime - initialTime ) / timeF;
    //        initialTime = currentTime;
    //
    //        if (deltaU >= 1)
    //        {
    //          //        getInput();
    //          //        update();
    //          ticks++;
    //          deltaU--;
    //        }
    //
    //        if (deltaF >= 1)
    //        {
    //          render();
    //          frames++;
    //          deltaF--;
    //        }
    //
    //        if (System.currentTimeMillis() - timer > 1000)
    //        {
    //
    //          System.out.println(String.format("UPS: %s, FPS: %s", ticks, frames));
    //
    //          frames = 0;
    //          ticks = 0;
    //          timer += 1000;
    //        }
    //      }
    //    });


    //    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    //
    //    Runnable beeper = new Runnable()
    //    {
    //      public void run ()
    //      {
    //        System.out.println("beep");
    //      }
    //    };

    //    final ScheduledFuture <?> beeperHandle = scheduler.scheduleAtFixedRate(loop, 2L, (long) prefTickDuration, NANOSECONDS);

    //    scheduler.schedule(new Runnable()
    //    {
    //      public void run () { beeperHandle.cancel(true); }
    //    }, 10, SECONDS);

    //    tickerRestructure.start();
  }

  public void startGame ()
  {
    gameState.running = true;
    double       prefTickDuration = ( 1_000_000_000.0 / gameState.tps ); //in ns, at 120 tps: 8_333_333ns
    double       frameTickRation  = gameState.tps / ( gameState.fps * 1.0 );
    final long[] absStart         = { System.nanoTime() };

    final long[]   tickStart    = { System.nanoTime() };
    final long[]   tickEnd      = { System.nanoTime() };
    final double[] tickDuration = { 0 };

    final long[]   renderStart    = { System.nanoTime() };
    final long[]   renderEnd      = { System.nanoTime() };
    final double[] renderDuration = { 0 };


    final long[]   loopStart         = { System.nanoTime() };
    final long[]   loopEnd           = { System.nanoTime() };
    final double[] loopDuration      = { 0 };
    final double[] workDuration      = { 0 };
    final double[] delayBetweenLoops = { 0 };
    final double[] idleDuration      = { 0 };
    final double[] loopTiming        = { 0 };

    Runnable render = () ->
    {
      //reRender layers if needed
      if (gameState.currentTick % frameTickRation == 0)
      {
        IntStream.range(0, AdvGameState.Layer.values().length).filter(i -> updateLayer.get(i)).forEach((i) ->
        {
          update(i);
          updateLayer.set(i, false);
        });
      }

      //render final image
      if (gameState.currentTick % frameTickRation == 0)
      {
        display.repaint();
      }
    };

    Runnable ticker = () ->
    {
      //tick all objects
      gameState.layers.forEach(layer ->
          layer.stream()
               .filter(gameObject -> gameObject instanceof AdvTicking)
               .forEach(gameObject -> ( (AdvTicking) gameObject ).tick()));
    };

    Runnable loop = () ->
    {

      if (!gameState.paused)
      {
        loopTiming[0] = round(( System.nanoTime() - loopStart[0] ) / 1_000_000.0); //duration of last tick in ms
        loopStart[0] = System.nanoTime();
        delayBetweenLoops[0] = round(( loopStart[0] - loopEnd[0] ) / 1_000_000.0); //duration of last tick in ms
        tickDuration[0] = round(( tickEnd[0] - tickStart[0] ) / 1_000_000.0); //duration of last tick in ms
        renderDuration[0] = round(( renderEnd[0] - renderStart[0] ) / 1_000_000.0); //duration of last tick in ms
        workDuration[0] = tickDuration[0] + renderDuration[0]; //duration of last tick in ms
        idleDuration[0] = round(( prefTickDuration / 1_000_000.0 - workDuration[0] )); //duration of last tick in ms

        //        System.out.println("Tick " + gameState.currentTick + "(tps:" + gameState.tps + "):"
        //            + " Loop: " + String.format("%.4f", loopDuration[0]) + "ms" + ";"
        //            + " timing: " + String.format("%.4f", loopTiming[0]) + "ms" + ";"
        //            + " loopDelay:" + String.format("%.4f", delayBetweenLoops[0]) + "ms" + "|"
        //            + " TickDur: " + String.format("%.4f", tickDuration[0]) + "ms" + "|"
        //            + " RenderDur: " + String.format("%.4f", renderDuration[0]) + "ms" + "|"
        //            + " WorkDur: " + String.format("%.4f", workDuration[0]) + "ms" + "|"
        //            + " IdleDur: " + String.format("%.4f", idleDuration[0]) + "/" + String.format("%.4f", prefTickDuration / 1_000_000.0) + "ms"
        //            + " (" + String.format("%.2f", ( idleDuration[0] / ( prefTickDuration / 1_000_000.0 ) ) * 100) + "%)" + "|");

        tickStart[0] = System.nanoTime();
        ticker.run();
        tickEnd[0] = System.nanoTime();

        renderStart[0] = System.nanoTime();
        render.run();
        renderEnd[0] = System.nanoTime();

        gameState.currentTick += 1;
        gameState.lastTickTime = System.nanoTime();
        loopEnd[0] = System.nanoTime();
        loopDuration[0] = ( loopEnd[0] - loopStart[0] ) / 1_000_000.0; //duration of last tick in ms
      }
    };

    Thread gameLoop = new Thread(() ->
    {
      render.run();

      while (gameState.running)
      {
        try
        {
          Thread.sleep(0);
        }
        catch (InterruptedException e)
        {
          throw new RuntimeException(e);
        }

        if (( System.nanoTime() - loopStart[0] ) < prefTickDuration) continue;

        loop.run();

        if (workDuration[0] < ( prefTickDuration / 1_000_000.0 ))
        {
          try
          {
            Thread.sleep((long) ( ( prefTickDuration / 1_000_000.0 - workDuration[0] ) - PacPhi.THREAD_DELAY * 2 / 3 ));
          }
          catch (InterruptedException e)
          {
            throw new RuntimeException(e);
          }
        }
      }
    });

    gameLoop.start();
  }

  public void stopGame ()
  {
    gameState.running = false;
  }

  public void pauseGame ()
  {
    gameState.paused = true;
  }

  public void resumeGame ()
  {
    gameState.paused = false;
  }
  //endregion

  //region loaders
  public void loadObjects ()
  {
    gameState.layers.get(AdvGameState.Layer.OBJECTS.ordinal()).addAll(( (AdvPacManMap) gameState.layers.get(AdvGameState.Layer.MAP.ordinal()).getFirst() ).generateObjects());
  }

  public void loadItems ()
  {
    gameState.layers.get(AdvGameState.Layer.ITEMS.ordinal()).addAll(( (AdvPacManMap) gameState.layers.get(AdvGameState.Layer.MAP.ordinal()).getFirst() ).generateItems());
    gameState.pelletCount = (int) gameState.layers.get(AdvGameState.Layer.ITEMS.ordinal()).stream()
                                                  .filter(item -> item instanceof PelletItem)
                                                  .count();
  }

  public void loadEntities ()
  {
    gameState.layers.get(AdvGameState.Layer.ENTITIES.ordinal()).addAll(( (AdvPacManMap) gameState.layers.get(AdvGameState.Layer.MAP.ordinal()).getFirst() ).generateEntities());
  }

  public void reloadLevel ()
  {
    gameState.layers.get(AdvGameState.Layer.OBJECTS.ordinal()).clear();
    gameState.layers.get(AdvGameState.Layer.ITEMS.ordinal()).clear();
    gameState.layers.get(AdvGameState.Layer.ENTITIES.ordinal()).clear();
    gameState.layers.get(AdvGameState.Layer.VFX.ordinal()).clear();

    gameState.players.clear();
    gameState.requestedDirections.clear();
    gameState.pelletCount = 0;

    loadObjects();
    loadItems();
    loadEntities();
  }
  //endregion


  public void controlPlayer (int player, Direction nextDir)
  {
    if (player > 0 && gameState.players.size() >= player)
      gameState.requestedDirections.set(player - 1, nextDir);
  }
}
