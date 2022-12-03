package kn.uni.games.classic.pacman.game;


import kn.uni.util.Vector2d;

public class PlacedObject extends GameObject
{
  public Vector2d pos;
  public boolean  expired;
  public boolean  movable;

  public PlacedObject ()
  {
    pos = new Vector2d();
    expired = false;
    movable = false;
  }

  public void markExpired ()
  {
    expired = true;
  }

}
