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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class DebugDisplay extends PlacedObject implements Rendered
{
  private final int[]                                       subTypeDist          = { 1, 7, 14, 19, 6 };
  private final Map <Integer, List <String>>                ghostData            = new HashMap <>();
  public        boolean                                     enabled              = false;
  public        InputListener.Player                        player;
  public        Map <DebugType, Map <DebugSubType, String>> diagnostics2         = new HashMap <>();
  public        List <String>                               ghostNames           = new ArrayList <>(4);
  public        List <String>                               ghostPositions       = new ArrayList <>(4);
  public        List <String>                               ghostDirections      = new ArrayList <>(4);
  public        List <String>                               ghostSpeeds          = new ArrayList <>(4);
  public        List <String>                               ghostAI              = new ArrayList <>(4);
  public        List <String>                               ghostModes           = new ArrayList <>(4);
  public        List <String>                               ghostsVulnerable     = new ArrayList <>(4);
  public        List <String>                               ghostsTargetDistance = new ArrayList <>(4);


  public DebugDisplay (Vector2d pos, InputListener.Player player)
  {
    enabled = true;
    this.pos = pos;
    this.player = player;

    //initialise Maps
    IntStream.range(0, DebugType.values().length).forEach(i -> diagnostics2.put(Arrays.stream(DebugType.values()).toList().get(i), new HashMap <>()));


    //assign prefixes to other maps
    String[] otherPrefixes = { "[Run:", "[TPS:", "[DBC:", "[DBT:", "[ObC:", "[InP:",
                               "[Lvl:", "[HP :", "[Sc :", "[ItL:", "[FSp:", "[ST :", "[Dur:",
                               "[Pos:", "[Dir:", "[Spe:", "[Ali:", "[Vul:" };
    int[] range = { 0, 6, 13, 19 };

    IntStream.range(0, 3).forEach(k -> IntStream.range(range[k], range[k + 1] - 1).forEach(i ->
        diagnostics2.get(Arrays.stream(DebugType.values()).toList().get(k))
                    .put(Arrays.stream(DebugSubType.values()).toList().get(i), otherPrefixes[i])));


    String[] ghostPrefixes = { "[Pos:    ", "[Dir:    ", "[Speed:  ", "[AI:     ", "[Mode:   ", "[Vuln:   ", "[Target: " };
    //assign prefixes to ghost map
    IntStream.range(19, 25).forEach(i -> diagnostics2.get(Arrays.stream(DebugType.values()).toList().get(3)).put(Arrays.stream(DebugSubType.values()).toList().get(i), ghostPrefixes[i - 19]));
    //create iterable of data lists
    List <List <String>> lists = new ArrayList <>(Arrays.asList(ghostNames, ghostPositions, ghostDirections, ghostSpeeds, ghostAI, ghostModes, ghostsVulnerable, ghostsTargetDistance));
    //initialise ghost data lists
    lists.forEach(i -> IntStream.range(0, 4).forEach(j -> i.add(" NaN")));
    //initialise ghost data
    IntStream.range(18, 26).forEach(i -> ghostData.put(i, lists.get(i - 18)));

  }

  public static DebugDisplay getDebugDisplay (ClassicPacmanGameState gameState)
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
          g.drawString(type.name() + ":", 10, 20 * ( type.ordinal() + subTypeDist[type.ordinal()] ));
          //for each subtype
          map.forEach((subType, s) ->
          {
            g.drawString(s, 20, 20 * ( subType.ordinal() + type.ordinal() + 2 ));
          });
        }
        else
        {
          g.drawString(type.name() + "s:   " + ghostNames, 10, 20 * ( type.ordinal() + subTypeDist[type.ordinal()] ));
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
