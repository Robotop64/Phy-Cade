package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.ClassicPacmanMap;
import kn.uni.games.classic.pacman.game.LoggerObject;
import kn.uni.games.classic.pacman.game.PlacedObject;
import kn.uni.games.classic.pacman.game.Rendered;
import kn.uni.games.classic.pacman.game.Ticking;
import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
import kn.uni.games.classic.pacman.game.hud.HUD;
import kn.uni.ui.InputListener.Input;
import kn.uni.ui.InputListener.Key;
import kn.uni.ui.InputListener.Player;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassicPacmanGameScreen extends UIScreen
{
  public ClassicPacmanGameState gameState;
  double           tickDuration;
  Map <Key, Input> joystick = new HashMap <>();
  Player           player;

  public ClassicPacmanGameScreen (JPanel parent, Player player) throws IOException
  {
    super(parent);
    setBackground(Color.black.darker().darker().darker().darker());
    gameState = new ClassicPacmanGameState();
    tickDuration = 1_000_000_000.0 / gameState.tps;
    this.player = player;

    bindPlayer(player, input ->
    {
      if (input.key().equals(Key.B))
      {
        //        if (!gameState.running)
        //        {
        //
        //        }

        gameState.running = false;
        Gui.getInstance().content.add(MainMenu.getInstance());
        MainMenu.getInstance().setBounds(Gui.defaultFrameBounds);
        MainMenu.getInstance().activate();
        return;
      }

      if (input.toDirection() == null)
      {
        joystick.remove(input.key());
        joystick.forEach((key, in) -> gameState.playerDirection = in.toDirection());
      }
      else
      {
        joystick.put(input.key(), input);
        gameState.playerDirection = input.toDirection();
      }

      //      System.out.println(gameState.playerDirection);
    });
    //create DebugDisplay
    gameState.gameObjects.add(new DebugDisplay(gameState, new Vector2d().cartesian(10, 10), player));
    //create Logger
    gameState.gameObjects.add(new LoggerObject());
    //create Map
    {
      ClassicPacmanMap map = new ClassicPacmanMap(gameState, new Vector2d().cartesian(gameState.mapOffset, gameState.mapOffset), 1000, 1000);
      gameState.gameObjects.add(map);
      gameState.map = map;
      gameState.size = new Vector2d().cartesian(map.width, map.height);
      map.setItems(new ArrayList <>());
    }
    //create HUD
    gameState.gameObjects.add(new HUD(gameState, new Vector2d().cartesian(gameState.mapOffset, gameState.mapOffset), new Vector2d().cartesian(gameState.map.width, gameState.map.height)));


    startGame();
  }

  /**
   * Starts the game and keeps it running
   */
  public void startGame ()
  {
    new Thread(() ->
    {
      gameState.running = true;
      gameState.currentTick = 0;
      gameState.lastTickTime = System.nanoTime();
      gameState.startTime = LocalTime.now();
      while (gameState.running)
      {
        long t = System.nanoTime();
        if (t - gameState.lastTickTime < tickDuration) continue;
        gameState.currentTick++;
        gameState.lastTickTime = t;

        //remove marked objects
        gameState.gameObjects.stream()
                             .filter(gameObject -> gameObject instanceof PlacedObject)
                             .filter(gameObject -> ( (PlacedObject) gameObject ).expired)
                             .forEach(gameObject -> gameState.gameObjects.remove(gameObject));

        //tick gameObjects
        gameState.gameObjects.stream()
                             .filter(gameObject -> gameObject instanceof Ticking)
                             .forEach(gameObject -> ( (Ticking) gameObject ).tick(gameState));

        List <String> debugData = DebugDisplay.getDebugList(player, gameState);
        debugData.set(1, "GameState-running:" + gameState.running);
        debugData.set(3, "ObjectCount:" + gameState.gameObjects.size());
        debugData.set(4, "LevelData:" +
            " [Lvl:" + gameState.level +
            "][HP:" + gameState.lives +
            "][Score:" + gameState.score +
            "][IL:" + gameState.map.getPlacedItems().size() + "]");
        debugData.set(5,
            "[FrS:" + gameState.fruitSpawned +
                "][Gs:" + gameState.startTime +
                "][Gd:" + gameState.gameDuration + "]");

        if (gameState.currentTick % 2 == 0)
        {
          Gui.getInstance().frame.repaint();
        }
      }
      kill();
    }).start();
  }

  @Override
  protected void paintComponent (Graphics g)
  {
    super.paintComponent(g);
    Graphics2D gg = (Graphics2D) g;
    gameState.gameObjects.stream()
                         .filter(gameObject -> gameObject instanceof Rendered)
                         .map(gameObject -> (Rendered) gameObject)
                         .sorted(Comparator.comparingInt(Rendered::paintLayer))
                         .forEach(gameObject -> ( gameObject ).paintComponent(gg, gameState));
  }


}
