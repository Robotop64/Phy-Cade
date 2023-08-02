package kn.uni.games.classic.pacman.game.internal;

import kn.uni.PacPhi;
import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.entities.Spawner;
import kn.uni.games.classic.pacman.game.internal.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.internal.graphics.GameDisplay;
import kn.uni.games.classic.pacman.game.internal.graphics.GameLayer;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.items.Item;
import kn.uni.games.classic.pacman.game.items.PelletItem;
import kn.uni.games.classic.pacman.screens.AdvGameScreen;
import kn.uni.util.Direction;
import kn.uni.util.PrettyPrint;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static kn.uni.util.Util.round;

public class GameEnvironment
{
  public AdvGameScreen gameScreen;
  public GameDisplay   display;
  public AdvGameState  gameState;

  public List <GameLayer> layer       = new ArrayList <>();
  public List <Boolean>   updateLayer = new ArrayList <>();

  private Runnable render;
  private Runnable ticker;
  private Runnable loop;


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
  public void startGame ()
  {
    gameState.running = true;
    double prefTickDuration = ( 1_000_000_000.0 / AdvGameConst.tps ); //in ns, at 120 tps: 8_333_333ns
    double frameTickRation  = AdvGameConst.tps / ( AdvGameConst.fps * 1.0 );

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

    render = () ->
    {
      //TODO only repaint regions that have changed using .repaint(rectangle)
      //reRender layers if needed
      if (gameState.currentTick % frameTickRation == 0 || gameState.paused)
      {
        IntStream.range(0, AdvGameState.Layer.values().length).filter(i -> updateLayer.get(i)).forEach((i) ->
        {
          update(i);
          updateLayer.set(i, false);
        });
      }

      //render final image
      if (gameState.currentTick % frameTickRation == 0 || gameState.paused)
      {
        display.repaint();
      }
    };

    ticker = () ->
    {
      //tick all objects
      IntStream.range(0, gameState.objects.list.size() - 1)
               .mapToObj(i -> gameState.objects.get(i))
               .filter(Objects::nonNull)
               .filter(gameObject -> gameObject instanceof AdvTicking)
               .filter(obj -> !obj.frozen)
               .forEachOrdered(gameObject -> ( (AdvTicking) gameObject ).tick());

      gameState.objects.cleanUp();

      gameState.time += (long) prefTickDuration;
      gameScreen.setTime((long) ( gameState.time / 1_000_000.0 ));
    };

    loop = () ->
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

        //        System.out.println("Tick " + gameState.currentTick + "(tps:" + AdvGameConst.tps + "):"
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
    System.out.println("Stopping game");
  }

  public void pauseGame ()
  {
    gameState.paused = true;
    System.out.println("Pausing game");
  }

  public void resumeGame ()
  {
    gameState.paused = false;
    System.out.println("Resuming game");
  }

  public void pauseGameIn (long ms, Runnable task)
  {
    new Thread(() ->
    {
      try
      {
        Thread.sleep(ms);
      }
      catch (InterruptedException e)
      {
        throw new RuntimeException(e);
      }
      pauseGame();
      task.run();
    }).start();
  }

  public void resumeGameIn (long ms, Runnable task)
  {
    new Thread(() ->
    {
      try
      {
        Thread.sleep(ms);
      }
      catch (InterruptedException e)
      {
        throw new RuntimeException(e);
      }
      resumeGame();
      task.run();
    }).start();
  }

  public void stopGameIn (long ms, Runnable task)
  {
    new Thread(() ->
    {
      try
      {
        Thread.sleep(ms);
      }
      catch (InterruptedException e)
      {
        throw new RuntimeException(e);
      }
      stopGame();
      task.run();
    }).start();
  }

  public void reloadLevel ()
  {
    if (!gameState.running) return;

    pauseGameIn(1000, () ->
    {
    });

    new Thread(() ->
    {
      try
      {
        Thread.sleep(2000);
      }
      catch (InterruptedException e)
      {
        throw new RuntimeException(e);
      }
      reloadLevelContent();
    }).start();
  }
  //endregion

  //region loaders
  public void loadObjects ()
  {
    Collection <AdvGameObject> objects = gameState.objects.maps().get(0).generateObjects();

    objects.forEach(object -> gameState.objects.add(AdvGameState.Layer.OBJECTS, object));
  }

  public void loadItems ()
  {
    Collection <Item> items = gameState.objects.maps().get(0).generateItems();

    items.forEach(item -> gameState.objects.add(AdvGameState.Layer.ITEMS, item));

    gameState.pelletCount = (int) items.stream()
                                       .filter(item -> item instanceof PelletItem)
                                       .count();
  }

  public void loadEntities ()
  {
    Collection <Entity> entities = gameState.objects.maps().get(0).generateEntities();

    entities.forEach(entity -> gameState.objects.add(AdvGameState.Layer.ENTITIES, entity));
  }

  public void spawnPlayers ()
  {
    gameState.objects.entities().stream()
                     .filter(obj -> obj instanceof Spawner)
                     .map(obj -> (Spawner) obj)
                     .filter(spawner -> spawner.name.equals("PlayerSpawn"))
                     .forEach(Spawner::spawn);
  }

  public void spawnGhosts ()
  {
    gameState.objects.entities().stream()
                     .filter(obj -> obj instanceof Spawner)
                     .map(obj -> (Spawner) obj)
                     .filter(spawner -> spawner.type.name().equals("GHOST"))
                     .forEach(Spawner::spawn);
  }

  public void reloadLevelContent ()
  {
    boolean hardReset = gameState.pelletsEaten == gameState.pelletCount;

    PrettyPrint.startGroup(PrettyPrint.Type.Message, "Reloading level");

    gameState.objects.clearLayer(AdvGameState.Layer.OBJECTS);
    if (hardReset) gameState.objects.clearLayer(AdvGameState.Layer.ITEMS);
    gameState.objects.clearLayer(AdvGameState.Layer.ENTITIES);
    gameState.objects.clearLayer(AdvGameState.Layer.VFX);

    PrettyPrint.bullet("Cleared layers");

    gameState.players.clear();
    gameState.requestedDirections.clear();
    if (hardReset)
    {
      gameState.pelletCount = 0;
      gameState.pelletsEaten = 0;
      gameState.fruitSpawned = false;
    }
    PrettyPrint.bullet("Reset players and trackers");

    switch (gameState.level)
    {
      case 5 ->
      {
        AdvGameConst.ghostSpeedBase = AdvGameConst.pacmanSpeedBase * 0.85;
        PrettyPrint.bullet("Reached Lvl 5");
        PrettyPrint.bullet("Increased ghost speed to " + 0.85 + "x Pacman");
      }
      default ->
      {
      }
    }

    loadObjects();
    if (hardReset) loadItems();
    loadEntities();
    spawnPlayers();
    spawnGhosts();
    if (hardReset) gameState.level++;
    gameScreen.setLevel(gameState.level);
    gameScreen.gameReloading = true;
    PrettyPrint.bullet("Reloaded level contents");

    IntStream.range(0, updateLayer.size()).forEach(i -> updateLayer.set(i, true));
    render.run();
    PrettyPrint.bullet("Rendered level");

    gameScreen.enableReadyPopup(true, null);
    PrettyPrint.bullet("Enabled ready popup");

    PrettyPrint.endGroup();
  }
  //endregion


  public void controlPlayer (int player, Direction nextDir)
  {
    if (player > 0 && gameState.players.size() >= player)
      gameState.requestedDirections.set(player - 1, nextDir);
  }
}
