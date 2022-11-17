package kn.uni.games.classic.pacman.game;


import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
import kn.uni.ui.InputListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LoggerObject extends GameObject implements Ticking
{
  LinkedList <Long> times = new LinkedList <>();

  public LoggerObject ()
  {
    times.add(0L);
  }

  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    times.push(gameState.lastTickTime);
    List <Long> l = times.stream().limit(gameState.tps + 1).toList();
    times.clear();
    times.addAll(l);
    double d = times.getFirst() - times.getLast();
    if (gameState.logging || gameState.currentTick % ( 2L * gameState.tps ) == 0)
    {
      Map <DebugDisplay.DebugType, Map<DebugDisplay.DebugSubType, String>>debugData = DebugDisplay.getDebugList(InputListener.Player.playerOne, gameState);
      debugData.get(DebugDisplay.DebugType.General).put(DebugDisplay.DebugSubType.TPS, "TPS:[Target:" + gameState.tps + "]" +"[Last: took " + d / 1_000_000_000.0 + "s]");
    }
    if (gameState.currentTick > 300) gameState.logging = false;
  }

}
