package kn.uni.games.classic.pacman.game.hud;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PlacedObject;
import kn.uni.games.classic.pacman.game.Rendered;
import kn.uni.games.classic.pacman.game.ghosts.Ghost;
import kn.uni.ui.InputListener;
import kn.uni.util.Fira;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DebugDisplay extends PlacedObject implements Rendered
{
  public  boolean                                     enabled       = false;
  public  InputListener.Player                        player;
  public  Map <DebugType, Map <DebugSubType, String>> diagnostics2  = new HashMap <>();
  public  List <String>                               ghostNames;
  public  List <String>                               ghostPositions;
  public  List <String>                               ghostDirections;
  public  List <String>                               ghostSpeeds;
  public  List <String>                               ghostAI;
  public  List <String>                               ghostModes;
  public  List <String>                               ghostsVulnerable;
  public  List <String>                               ghostsTargetDistance;
  private int[]                                       subTypeLength = { 1, 7, 14, 19, 6 };
  private Map <Integer, List <String>>                ghostData     = new HashMap <>();

  public DebugDisplay (Vector2d pos, InputListener.Player player)
  {
    //    enabled = true;
    this.pos = pos;
    this.player = player;

    diagnostics2.put(DebugType.General, new HashMap <>());

    diagnostics2.get(DebugType.General).put(DebugSubType.running, "Running: NaN");
    diagnostics2.get(DebugType.General).put(DebugSubType.TPS, "TPS: NaN");
    diagnostics2.get(DebugType.General).put(DebugSubType.dataBase, "DataBase: NaN");
    diagnostics2.get(DebugType.General).put(DebugSubType.dataBaseDuration, "DataBase Duration: NaN");
    diagnostics2.get(DebugType.General).put(DebugSubType.objectCount, "Object Count: NaN");
    diagnostics2.get(DebugType.General).put(DebugSubType.input, "Input: NaN");


    diagnostics2.put(DebugType.Level, new HashMap <>());

    diagnostics2.get(DebugType.Level).put(DebugSubType.Lvl, "[Lvl: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.Lives, "[HP: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.Score, "[Score: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.ItemsLeft, "[Items: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.FruitsSpawned, "[FruitSp: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.GameStart, "[Start: NaN]");
    diagnostics2.get(DebugType.Level).put(DebugSubType.GameDuration, "[Dur: NaN]");


    diagnostics2.put(DebugType.Player, new HashMap <>());

    diagnostics2.get(DebugType.Player).put(DebugSubType.PlayerPosition, "[Pos: NaN]");
    diagnostics2.get(DebugType.Player).put(DebugSubType.PlayerDirection, "[Dir: NaN]");
    diagnostics2.get(DebugType.Player).put(DebugSubType.PlayerSpeed, "[Speed: NaN]");
    diagnostics2.get(DebugType.Player).put(DebugSubType.PlayerState, "[Alive: NaN]");
    diagnostics2.get(DebugType.Player).put(DebugSubType.PlayerVulnerable, "[Vul: NaN]");


    diagnostics2.put(DebugType.Ghost, new HashMap <>());

    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostPosition, "[Pos:");
    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostDirection, "[Dir:");
    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostSpeed, "[Speed:");
    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostAI, "[AI:");
    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostState, "[State:");
    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostVulnerable, "[Vul:");
    diagnostics2.get(DebugType.Ghost).put(DebugSubType.GhostTargetDist, "[Dist:");

    ghostNames = new ArrayList <>(4);
    ghostNames.add("NaN");
    ghostNames.add("NaN");
    ghostNames.add("NaN");
    ghostNames.add("NaN");
    ghostPositions = new ArrayList <>(4);
    ghostPositions.add("NaN");
    ghostPositions.add("NaN");
    ghostPositions.add("NaN");
    ghostPositions.add("NaN");
    ghostDirections = new ArrayList <>(4);
    ghostDirections.add("NaN");
    ghostDirections.add("NaN");
    ghostDirections.add("NaN");
    ghostDirections.add("NaN");
    ghostSpeeds = new ArrayList <>(4);
    ghostSpeeds.add("NaN");
    ghostSpeeds.add("NaN");
    ghostSpeeds.add("NaN");
    ghostSpeeds.add("NaN");
    ghostAI = new ArrayList <>(4);
    ghostAI.add("NaN");
    ghostAI.add("NaN");
    ghostAI.add("NaN");
    ghostAI.add("NaN");
    ghostModes = new ArrayList <>(4);
    ghostModes.add("NaN");
    ghostModes.add("NaN");
    ghostModes.add("NaN");
    ghostModes.add("NaN");
    ghostsVulnerable = new ArrayList <>(4);
    ghostsVulnerable.add("NaN");
    ghostsVulnerable.add("NaN");
    ghostsVulnerable.add("NaN");
    ghostsVulnerable.add("NaN");
    ghostsTargetDistance = new ArrayList <>(4);
    ghostsTargetDistance.add("NaN");
    ghostsTargetDistance.add("NaN");
    ghostsTargetDistance.add("NaN");
    ghostsTargetDistance.add("NaN");

    ghostData.put(DebugSubType.GhostName.ordinal(), ghostNames);
    ghostData.put(DebugSubType.GhostPosition.ordinal(), ghostPositions);
    ghostData.put(DebugSubType.GhostDirection.ordinal(), ghostDirections);
    ghostData.put(DebugSubType.GhostSpeed.ordinal(), ghostSpeeds);
    ghostData.put(DebugSubType.GhostAI.ordinal(), ghostAI);
    ghostData.put(DebugSubType.GhostState.ordinal(), ghostModes);
    ghostData.put(DebugSubType.GhostVulnerable.ordinal(), ghostsVulnerable);
    ghostData.put(DebugSubType.GhostTargetDist.ordinal(), ghostsTargetDistance);

  }

  public static DebugDisplay getDebug (ClassicPacmanGameState gameState)
  {
    return Objects.requireNonNull(gameState.gameObjects.stream()
                                                       .filter(o -> o instanceof DebugDisplay)
                                                       .map(o -> (DebugDisplay) o)
                                                       .filter(o -> o.player == gameState.player)
                                                       .findFirst()
                                                       .orElse(null));
  }

  public static void setData (ClassicPacmanGameState gameState, DebugType type, DebugSubType subType, String data)
  {
    Objects.requireNonNull(gameState.gameObjects.stream()
                                                .filter(o -> o instanceof DebugDisplay)
                                                .map(o -> (DebugDisplay) o)
                                                .filter(o -> o.player == gameState.player)
                                                .findFirst()
                                                .orElse(null)).diagnostics2.get(type).put(subType, data);
  }

  public static void setGhostData (ClassicPacmanGameState gameState, DebugSubType SubType, Ghost ghost, String data)
  {
    Objects.requireNonNull(gameState.gameObjects.stream()
                                                .filter(o -> o instanceof DebugDisplay)
                                                .map(o -> (DebugDisplay) o)
                                                .filter(o -> o.player == gameState.player)
                                                .findFirst()
                                                .orElse(null)).ghostData.get(SubType.ordinal()).set(ghost.name.ordinal(), data);


  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    if (enabled)
    {
      pos.use(g::translate);
      //panel background
      g.setColor(new Color(40, 40, 40, 220));
      g.setFont(Fira.getInstance().getLigatures(15f));
      g.fillRect(-20, -20, Gui.frameWidth / 2, 600);
      g.setColor(Color.red);
      //for each type
      diagnostics2.forEach((type, map) ->
      {
        //exclude ghost for extra rendering
        if (type != DebugType.Ghost)
        {
          g.drawString(type.name() + ":", 10, 20 * ( type.ordinal() + subTypeLength[type.ordinal()] ));
          //for each subtype
          map.forEach((subType, s) ->
          {
            g.drawString(s, 20, 20 * ( subType.ordinal() + type.ordinal() + 2 ));
          });
        }
        else
        {
          g.drawString(type.name() + "s:" + ghostNames, 10, 20 * ( type.ordinal() + subTypeLength[type.ordinal()] ));
          map.forEach((subType, s) ->
          {
            g.drawString(s + ghostData.get(subType.ordinal()), 20, 20 * ( subType.ordinal() + type.ordinal() + 1 ));
          });
        }
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

  public enum DebugType
  { General, Level, Player, Ghost }

  public enum DebugSubType
  {
    running, TPS, dataBase, dataBaseDuration, objectCount, input,
    Lvl, Lives, Score, ItemsLeft, FruitsSpawned, GameStart, GameDuration,
    PlayerPosition, PlayerDirection, PlayerSpeed, PlayerState, PlayerVulnerable,
    GhostName, GhostPosition, GhostDirection, GhostSpeed, GhostAI, GhostState, GhostVulnerable, GhostTargetDist
  }
}
