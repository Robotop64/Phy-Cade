package kn.uni.menus;

public class ProjectorState
{
  int     tps;
  int     tickDuration;
  boolean running;
  long    currentTick;
  long    lastTickTime;

  public ProjectorState ()
  {
    tps = 60;
    tickDuration = 1_000_000_000 / tps;
    running = true;
    currentTick = 0;
    lastTickTime = System.nanoTime();
  }
}
