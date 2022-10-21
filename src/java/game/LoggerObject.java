package game;

import java.util.LinkedList;
import java.util.List;

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
    List <Long> l = times.stream().limit(61).toList();
    times.clear();
    times.addAll(l);
    double d = times.getFirst() - times.getLast();
    if (gameState.logging || gameState.currentTick % 180 == 0)
      System.out.printf("Tick %d at Time %.6f. Last 60 ticks took %f seconds%n", gameState.currentTick, gameState.lastTickTime / 1_000_000_000.0, d / 1_000_000_000.0);

    if (gameState.currentTick > 300) gameState.logging = false;
  }
}
