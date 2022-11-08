package kn.uni.games.classic.pacman.game;


import kn.uni.util.Vector2d;

public class PlacedObject extends GameObject
{

  public Vector2d pos;

  public boolean expired;

  public PlacedObject ()
  {
    pos = new Vector2d();
    expired = false;
  }

}
