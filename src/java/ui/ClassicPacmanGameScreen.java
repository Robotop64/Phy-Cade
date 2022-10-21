package ui;

import game.BoxObject;
import game.GameState;
import game.LoggerObject;
import game.Rendered;
import game.Ticking;
import ui.InputListener.Key;
import ui.InputListener.Player;
import util.Direction;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class ClassicPacmanGameScreen extends UIScreen
{
  GameState gameState;
  Direction lastDirection;
  int       tps          = 60;
  double    tickDuration = 1_000_000_000.0 / tps;

  public ClassicPacmanGameScreen (JPanel parent, Player player)
  {
    super(parent);
    setBackground(Color.black);
    gameState = new GameState();

    bindPlayer(player, input ->
    {
      if (input.key().equals(Key.B))
      {
        gameState.running = false;
        return;
      }

      Direction d = input.toDirection();
      if (d == null)
      {
        gameState.playerDirection = lastDirection;
        lastDirection = null;
      }
      else
      {
        lastDirection = gameState.playerDirection;
        gameState.playerDirection = d;
      }
    });

    gameState.gameObjects.add(new LoggerObject());
    gameState.gameObjects.add(new BoxObject());

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
        gameState.gameObjects.stream().filter(gameObject -> gameObject instanceof Ticking).parallel().forEach(gameObject -> ((Ticking)gameObject).tick(gameState));
        Gui.getInstance().frame.repaint();
      }
    }).start();
  }

  @Override
  protected void paintComponent (Graphics g)
  {
    super.paintComponent(g);
    Graphics2D gg = (Graphics2D)g;
    gameState.gameObjects.stream().filter(gameObject -> gameObject instanceof Rendered).parallel().forEach(gameObject -> ((Rendered)gameObject).paintComponent(gg, gameState));
  }

}
