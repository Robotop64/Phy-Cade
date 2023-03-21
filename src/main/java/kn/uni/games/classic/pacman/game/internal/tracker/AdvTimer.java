package kn.uni.games.classic.pacman.game.internal.tracker;

import kn.uni.games.classic.pacman.game.internal.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;

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

  @Override
  public void tick ()
  {
    tasks.forEach(task ->
    {
      if (task.startTick + task.waitPeriod == gameState.currentTick)
      {
        task.task.run();
      }
    });

    for (int i = 0; i < tasks.size(); i++)
    {
      if (tasks.get(i).isCompleted(gameState))
      {
        tasks.remove(i);
        i--;
      }
    }
  }

  public record TimerTask(long startTick, long waitPeriod, Runnable task)
  {
    public boolean isCompleted (AdvGameState gameState)
    {
      return startTick + waitPeriod < gameState.currentTick;
    }
  }
}
