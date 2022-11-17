package kn.uni.games.classic.pacman.game.hud;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PlacedObject;
import kn.uni.games.classic.pacman.game.Rendered;
import kn.uni.ui.InputListener;
import kn.uni.util.Fira;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DebugDisplay extends PlacedObject implements Rendered
{
  public boolean              enabled = false;
  public InputListener.Player player;

  public List <String> diagnostics = new ArrayList <>();


  public DebugDisplay (ClassicPacmanGameState gameState, Vector2d pos, InputListener.Player player)
  {
    enabled = true;
    this.pos = pos;
    this.player = player;
    diagnostics.add("DebugDisplay:");
    diagnostics.add("GameState-running:");
    diagnostics.add("TPS:");
    diagnostics.add("ObjectCount:");
    diagnostics.add("LevelData:");
    diagnostics.add("LevelData2:");
    diagnostics.add("PlayerData:");
  }

  public static List <String> getDebugList (InputListener.Player player, ClassicPacmanGameState gameState)
  {
    return Objects.requireNonNull(gameState.gameObjects.stream()
                                                       .filter(o -> o instanceof DebugDisplay)
                                                       .map(o -> (DebugDisplay) o)
                                                       .filter(o -> o.player == player)
                                                       .findFirst()
                                                       .orElse(null)).diagnostics;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    pos.use(g::translate);
    g.setColor(new Color(100, 100, 100, 200));
    g.setFont(Fira.getInstance().getLigatures(20f));
    g.fillRect(-10, -10, Gui.frameWidth / 2, 600);

    if (enabled)
    {
      g.setColor(Color.red);
      for (int i = 0; i < diagnostics.size(); i++)
      {
        g.drawString(diagnostics.get(i), 0, getRowPos(i));
      }
    }
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE;
  }

  private int getRowPos (int index)
  {
    return index * 20;
  }
}
