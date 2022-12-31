package kn.uni.menus;

public class ProjectorState
{
  static int tps;
  static int tickDuration;
  boolean running;

  public ProjectorState ()
  {
    tps = 60;
    tickDuration = 1_000_000_000 / tps;
    running = true;
  }
}
