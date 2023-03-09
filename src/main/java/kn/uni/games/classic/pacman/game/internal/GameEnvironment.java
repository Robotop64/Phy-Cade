package kn.uni.games.classic.pacman.game.internal;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.objects.AdvPacManMap;
import kn.uni.games.classic.pacman.game.objects.AdvPacManTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static kn.uni.util.Util.round;

public class GameEnvironment
{
  public JLayeredPane displayStack;
  public AdvGameState gameState;

  //background[0], map[1], objects[2], items[3], entities[4], vfx[5], frame[6]
  public List <JPanel> panels = new ArrayList <>();

  public GameEnvironment (Dimension dim)
  {
    displayStack = new JLayeredPane();
    displayStack.setBounds(0, 0, dim.width, dim.height);
    displayStack.setLayout(null);

    gameState = new AdvGameState();

    //region fill displayStack with panels
    IntStream.range(0, 6).forEach(i ->
    {
      GamePanel panel = new GamePanel(dim, i);
      panel.gameState = gameState;
      panels.add(panel);
      displayStack.add(panel);
      displayStack.setLayer(panel, i);
    });

    //Frame
    JPanel panel = new JPanel();
    panel.setBounds(0, 0, dim.width, dim.height);
    panel.setOpaque(false);
    panel.setBackground(null);
    panel.setBorder(BorderFactory.createLineBorder(Color.cyan.darker().darker(), 2));
    panels.add(panel);
    displayStack.add(panel);
    displayStack.setLayer(panel, 6);
    //endregion
  }

  public JLayeredPane getDisplayStack ()
  {
    return displayStack;
  }

  public AdvGameState getGameState ()
  {
    return gameState;
  }

  public void updateAll ()
  {
    panels.stream().filter(o -> o instanceof GamePanel).map(panel -> (GamePanel) panel).forEach(GamePanel::render);
  }

  public void update (int index)
  {
    ( (GamePanel) panels.get(index) ).render();
  }

  public void start ()
  {
    gameState.running = true;
    gameState.paused = false;
    gameState.currentTick = 0;
    gameState.lastTickTime = System.nanoTime();
    double tickDuration = 1_000_000_000.0 / gameState.tps;

    Thread ticker = new Thread(() ->
    {
      while (gameState.running)
      {
        if (!gameState.paused)
        {
          long t = System.nanoTime();
          if (t - gameState.lastTickTime < tickDuration) continue;
          gameState.currentTick++;
          gameState.lastTickTime = t;

          gameState.layers.forEach(layer ->
              layer.stream()
                   .filter(gameObject -> gameObject instanceof AdvTicking)
                   .forEach(gameObject -> ( (AdvTicking) gameObject ).tick()));

          update(4);

          panels.forEach(Component::repaint);
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

    Thread renderer = new Thread(() ->
    {
      while (gameState.running)
      {
        update(4);

        panels.forEach(Component::repaint);
        try
        {
          Thread.sleep(50);
        }
        catch (InterruptedException e)
        {
          throw new RuntimeException(e);
        }
      }
    });

    ticker.start();
    //    renderer.start();
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
