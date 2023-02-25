package kn.uni.games.classic.pacman.game.internal;


import kn.uni.games.classic.pacman.game.graphics.Ticking;
import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
import kn.uni.games.classic.pacman.game.objects.AdvGameObject;

import java.util.LinkedList;
import java.util.List;

public class LoggerObject extends AdvGameObject implements Ticking
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
      DebugDisplay.setData(gameState, DebugDisplay.DebugType.General, DebugDisplay.DebugSubType.TPS, "TPS:[Target:" + gameState.tps + "]" + "[Last: took " + d / 1_000_000_000.0 + "s]");
    }
    if (gameState.currentTick > 300) gameState.logging = false;
  }

}
