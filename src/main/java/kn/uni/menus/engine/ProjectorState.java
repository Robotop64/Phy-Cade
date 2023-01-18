package kn.uni.menus.engine;

public class ProjectorState
{
  public int     tps;
  public int     tickDuration;
  public boolean running;
  public long    currentTick;
  public long    lastTickTime;

  public ProjectorState ()
  {
    tps = 60;
    tickDuration = 1_000_000_000 / tps;
    running = true;
    currentTick = 0;
    lastTickTime = System.nanoTime();
  }
}
