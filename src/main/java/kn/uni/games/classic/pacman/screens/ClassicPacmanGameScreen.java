package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.ClassicPacmanMap;
import kn.uni.games.classic.pacman.game.hud.HUD;
import kn.uni.games.classic.pacman.game.LoggerObject;
import kn.uni.games.classic.pacman.game.PacmanObject;
import kn.uni.games.classic.pacman.game.Rendered;
import kn.uni.games.classic.pacman.game.Ticking;
import kn.uni.ui.InputListener.Input;
import kn.uni.ui.InputListener.Key;
import kn.uni.ui.InputListener.Player;
import kn.uni.ui.UIScreen;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ClassicPacmanGameScreen extends UIScreen
{
  public ClassicPacmanGameState gameState;
  double           tickDuration;
  Map <Key, Input> joystick = new HashMap <>();

  public ClassicPacmanGameScreen (JPanel parent, Player player) throws IOException
  {
    super(parent);
    setBackground(Color.black.darker().darker().darker().darker());
    gameState = new ClassicPacmanGameState();
    tickDuration = 1_000_000_000.0 / gameState.tps;

    bindPlayer(player, input ->
    {
      if (input.key().equals(Key.B))
      {
        //        if (!gameState.running)
        //        {
        //
        //        }

        gameState.running = false;
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

      System.out.println(gameState.playerDirection);
    });


    LoggerObject.createLogger(gameState);
    ClassicPacmanMap.createMap(gameState);
    PacmanObject.spawnPacman(gameState);
    HUD.createHUD(gameState);

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
        gameState.gameObjects.stream()
                             .filter(gameObject -> gameObject instanceof Ticking)
                             .forEach(gameObject -> ( (Ticking) gameObject ).tick(gameState));
        Gui.getInstance().frame.repaint();

      }
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
