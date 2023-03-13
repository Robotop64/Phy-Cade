package kn.uni.games.classic.pacman.game.internal;

import kn.uni.games.classic.pacman.game.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.objects.AdvGameObject;

import java.util.ArrayList;
import java.util.List;

public class AdvTimer extends AdvGameObject implements AdvTicking
{
  public List <TimerTask> tasks = new ArrayList <>();
  public AdvGameState     gameState;

  public AdvTimer (AdvGameState gameState)
  {
    this.gameState = gameState;
  }

  public void addTask (TimerTask task)
  {
    tasks.add(task);
  }

  public void removeTask (TimerTask task)
  {
    tasks.remove(task);
  }

  @Override
  public void tick ()
  {
    tasks.forEach(task ->
    {
      if (task.startTick + task.waitPeriod == gameState.currentTick)
      {
        task.task.run();
        removeTask(task);
      }
    });
  }

  public record TimerTask(long startTick, long waitPeriod, Runnable task) { }
}
