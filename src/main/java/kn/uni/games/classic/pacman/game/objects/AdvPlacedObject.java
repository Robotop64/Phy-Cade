package kn.uni.games.classic.pacman.game.objects;


import kn.uni.util.Vector2d;

public class AdvPlacedObject extends AdvGameObject
{
  public Vector2d absPos;
  public Vector2d mapPos;
  public boolean  expired;

  public AdvPlacedObject ()
  {
    absPos = new Vector2d();
    mapPos = new Vector2d();
    expired = false;
  }

  public void markExpired ()
  {
    expired = true;
  }

}
