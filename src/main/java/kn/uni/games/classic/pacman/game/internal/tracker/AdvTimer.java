package kn.uni.games.classic.pacman.game.internal.tracker;

import kn.uni.games.classic.pacman.game.internal.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.util.PrettyPrint;

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

  public static AdvTimer getInstance (AdvGameState gameState)
  {
    return (AdvTimer) gameState.objects.find(AdvGameState.Layer.INTERNALS, AdvTimer.class).get(0);
  }

  public void addTask (TimerTask task, String hint)
  {
    PrettyPrint.startGroup(PrettyPrint.Type.Event, "Timer");
    PrettyPrint.bullet("Added task \"" + hint + "\" to timer");
    PrettyPrint.bullet("Push on tick " + task.startTick + " and pop in " + task.waitPeriod + " ticks");
    PrettyPrint.endGroup();

    tasks.add(task);
  }

  public void removeTask(String id)
  {
    tasks.removeIf(task -> task.id.equals(id));
  }

  @Override
  public void tick ()
  {
    tasks.stream()
        .filter(task -> task.isCompleted(gameState))
        .forEach(task -> task.action.run());

    for (int i = 0; i < tasks.size(); i++)
    {
      if (tasks.get(i).isCompleted(gameState))
      {
        PrettyPrint.startGroup(PrettyPrint.Type.Event, "Timer");
        PrettyPrint.bullet("Removed task \"" + tasks.get(i).id + "\"");
        PrettyPrint.endGroup();
        tasks.remove(i);
        i--;
      }
    }
  }

  public record TimerTask(long startTick, long waitPeriod, Runnable action, String id)
  {
    public boolean isCompleted (AdvGameState gameState)
    {
      return startTick + waitPeriod < gameState.currentTick;
    }
  }
}
