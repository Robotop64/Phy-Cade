package ui;

import game.ClassicPacmanGameState;
import game.LoggerObject;
import game.PacmanObject;
import game.Rendered;
import game.Ticking;
import game.map.ClassicPacmanMap;
import ui.InputListener.Input;
import ui.InputListener.Key;
import ui.InputListener.Player;
import util.Vector2d;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ClassicPacmanGameScreen extends UIScreen
{
  ClassicPacmanGameState gameState;
  int                    tps          = 60;
  double                 tickDuration = 1_000_000_000.0 / tps;
  Map <Key, Input>       joystick     = new HashMap <>();

  public ClassicPacmanGameScreen (JPanel parent, Player player)
  {
    super(parent);
    setBackground(Color.gray.darker().darker().darker().darker());
    gameState = new ClassicPacmanGameState();

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

    gameState.gameObjects.add(new LoggerObject());
    ClassicPacmanMap map = new ClassicPacmanMap(new Vector2d().cartesian(20, 20), 900, 900);
    gameState.gameObjects.add(map);
    gameState.map = map;
    gameState.size = new Vector2d().cartesian(map.width, map.height);
    gameState.gameObjects.add(new PacmanObject((int)(Math.round(map.tileSize * 2. / 3.))));

    startGame();
  }

  public void startGame ()
  {
    new Thread(() ->
    {
      gameState.running = true;
      gameState.currentTick = 0;
      gameState.lastTickTime = System.nanoTime();

      while (gameState.running)
      {
        long t = System.nanoTime();
        if (t - gameState.lastTickTime < tickDuration) continue;
        gameState.currentTick++;
        gameState.lastTickTime = t;
        gameState.gameObjects.stream()
                             .filter(gameObject -> gameObject instanceof Ticking)
                             .forEach(gameObject -> ((Ticking)gameObject).tick(gameState));
        Gui.getInstance().frame.repaint();
      }
    }).start();
  }

  @Override
  protected void paintComponent (Graphics g)
  {
    super.paintComponent(g);
    Graphics2D gg = (Graphics2D)g;
    gameState.gameObjects.stream()
                         .filter(gameObject -> gameObject instanceof Rendered)
                         .map(gameObject -> (Rendered)gameObject)
                         .sorted(Comparator.comparingInt(Rendered::paintLayer))
                         .forEach(gameObject -> (gameObject).paintComponent(gg, gameState));
  }

}
