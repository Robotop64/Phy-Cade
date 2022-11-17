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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugDisplay extends PlacedObject implements Rendered
{
  public boolean              enabled = false;
  public InputListener.Player player;

  public enum DebugType
  { General, Level, Player, Ghost }
  public enum DebugSubType
  {running, TPS, dataBase, dataBaseDuration, objectCount, input,
    Lvl,Lives,Score,ItemsLeft,FruitsSpawned,GameStart,GameDuration,
    PlayerPosition, PlayerDirection, PlayerSpeed, PlayerState, PlayerVulnerable,
    GhostPosition, GhostDirection, GhostSpeed, GhostState, GhostVulnerable}
  private int[] subTypeLength = {1,7,14,19,6};

  public List <String> diagnostics = new ArrayList <>();
  public Map <DebugType, Map<DebugSubType, String>> diagnostics2 = new HashMap<>();


  public DebugDisplay (ClassicPacmanGameState gameState, Vector2d pos, InputListener.Player player)
  {
    //enabled = true;
    this.pos = pos;
    this.player = player;

    diagnostics2.put(DebugType.General, new HashMap<>());

    diagnostics2.get(DebugType.General).put(DebugSubType.running, "Running: NaN");
    diagnostics2.get(DebugType.General).put(DebugSubType.TPS, "TPS: NaN");
    diagnostics2.get(DebugType.General).put(DebugSubType.dataBase, "DataBase: NaN");
    diagnostics2.get(DebugType.General).put(DebugSubType.dataBaseDuration, "DataBase Duration: NaN");
    diagnostics2.get(DebugType.General).put(DebugSubType.objectCount, "Object Count: NaN");
    diagnostics2.get(DebugType.General).put(DebugSubType.input, "Input: NaN");


    diagnostics2.put(DebugType.Level, new HashMap<>());

    diagnostics2.get(DebugType.Level).put(DebugSubType.Lvl, "[Lvl: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.Lives, "[HP: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.Score, "[Score: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.ItemsLeft, "[Items: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.FruitsSpawned, "[FruitSp: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.GameStart, "[Start: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.GameDuration, "[Dur: NaN]");


    diagnostics2.put(DebugType.Player, new HashMap<>());

    diagnostics2.get(DebugType.Player).put(DebugSubType.PlayerPosition, "[Pos: NaN]");
    diagnostics2.get(DebugType.Player).put(DebugSubType.PlayerDirection, "[Dir: NaN]");
    diagnostics2.get(DebugType.Player).put(DebugSubType.PlayerSpeed, "[Speed: NaN]");
    diagnostics2.get(DebugType.Player).put(DebugSubType.PlayerState, "[Alive: NaN]");
    diagnostics2.get(DebugType.Player).put(DebugSubType.PlayerVulnerable, "[Vul: NaN]");


    diagnostics2.put(DebugType.Ghost, new HashMap<>());

    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostPosition, "[Pos: NaN]");
    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostDirection, "[Dir: NaN]");
    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostSpeed, "[Speed: NaN]");
    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostState, "[State: NaN]");
    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostVulnerable, "[Vul: NaN]");
  }

  public static Map <DebugType, Map<DebugSubType, String>> getDebugList (InputListener.Player player, ClassicPacmanGameState gameState)
  {
    return Objects.requireNonNull(gameState.gameObjects.stream()
                                                       .filter(o -> o instanceof DebugDisplay)
                                                       .map(o -> (DebugDisplay) o)
                                                       .filter(o -> o.player == player)
                                                       .findFirst()
                                                       .orElse(null)).diagnostics2;
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    if (enabled)
    {
      pos.use(g::translate);
      g.setColor(new Color(40, 40, 40, 220));
      g.setFont(Fira.getInstance().getLigatures(15f));
      g.fillRect(-20, -20, Gui.frameWidth / 2, 600);
      g.setColor(Color.red);
      diagnostics2.forEach((type, map) -> {
        g.drawString(type.name(), 10, 20*(type.ordinal()+subTypeLength[type.ordinal()]));
        map.forEach((subType, s) -> {
          g.drawString(s, 20, 20*(subType.ordinal()+type.ordinal()+2));
        });
      });
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
