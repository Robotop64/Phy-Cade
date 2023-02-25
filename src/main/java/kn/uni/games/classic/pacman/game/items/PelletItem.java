package kn.uni.games.classic.pacman.game.items;

import kn.uni.util.Vector2d;

public class PelletItem extends Item
{
  //position on the canvas
  public Vector2d absPos;
  //position on the map
  public Vector2d mapPos;


  public PelletItem (Vector2d mapPos)
  {
    this.mapPos = mapPos;
    this.type = ItemType.PELLET;
  }
}
