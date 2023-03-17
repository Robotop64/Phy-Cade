package kn.uni.games.classic.pacman.game.internal.objects;


import kn.uni.util.Vector2d;

public class AdvPlacedObject extends AdvGameObject
{
  public static int      iconSize;
  public        Vector2d absPos;
  public        Vector2d mapPos;

  public AdvPlacedObject ()
  {
    absPos = new Vector2d();
    mapPos = new Vector2d();
  }
}
