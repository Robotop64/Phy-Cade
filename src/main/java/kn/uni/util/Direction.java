package kn.uni.util;

import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;

public enum Direction
{
  up,
  down,
  left,
  right,
  topLeft,
  topRight,
  bottomLeft,
  bottomRight;

  public static Direction[] valuesCardinal ()
  {
    return new Direction[]{ up, down, left, right };
  }

  public static Direction[] valuesDiagonal ()
  {
    return new Direction[]{ topLeft, topRight, bottomLeft, bottomRight };
  }

  public static Direction[] valuesAll ()
  {
    return new Direction[]{ up, down, left, right, topLeft, topRight, bottomLeft, bottomRight };
  }

  /**
   * Returns the direction from a to b
   *
   * @param a origin
   * @param b target
   * @return direction from a to b
   */
  public static Direction getDirection (Vector2d a, Vector2d b)
  {
    if (a == null || b == null)
      throw new IllegalArgumentException("a and b must not be null");

    if (a.equals(b))
      throw new IllegalArgumentException("a and b must not be equal");

    //decide if the direction is horizontal or vertical,
    if (Math.abs(a.x - b.x) > Math.abs(a.y - b.y))
      //if the origin(a) is to the left of the target(b) => right, else left
      if (a.x < b.x)
        return Direction.right;
      else
        return Direction.left;

    else

      //if the origin(a) is above the target(b) => down, else up
      if (a.y > b.y)
        return Direction.down;
      else
        return Direction.up;
  }

  public static Direction getDirection (AdvPlacedObject a, AdvPlacedObject b)
  {
    if (a.mapPos.x == b.mapPos.floor().x)
      if (a.mapPos.y < b.mapPos.floor().y)
        return Direction.down;
      else
        return Direction.up;

    if (a.mapPos.y == b.mapPos.floor().y)
      if (a.mapPos.x < b.mapPos.floor().x)
        return Direction.right;
      else
        return Direction.left;

    return null;
  }

  public Direction[] getCardinalsOfDiagonal ()
  {
    switch (this)
    {
      case topLeft ->
      {
        return new Direction[]{ up, left };
      }
      case topRight ->
      {
        return new Direction[]{ up, right };
      }
      case bottomLeft ->
      {
        return new Direction[]{ down, left };
      }
      case bottomRight ->
      {
        return new Direction[]{ down, right };
      }
    }
    return null;
  }

  public Vector2d toVector ()
  {
    return switch (this)
    {
      case up -> new Vector2d().cartesian(0, -1);
      case down -> new Vector2d().cartesian(0, 1);
      case left -> new Vector2d().cartesian(-1, 0);
      case right -> new Vector2d().cartesian(1, 0);
      case topLeft -> new Vector2d().cartesian(-1, -1);
      case topRight -> new Vector2d().cartesian(1, -1);
      case bottomLeft -> new Vector2d().cartesian(-1, 1);
      case bottomRight -> new Vector2d().cartesian(1, 1);
    };
  }

  public Direction opposite ()
  {
    return switch (this)
    {
      case up -> down;
      case down -> up;
      case left -> right;
      case right -> left;
      case topLeft -> bottomRight;
      case topRight -> bottomLeft;
      case bottomLeft -> topRight;
      case bottomRight -> topLeft;
    };
  }

  public int toAngle ()
  {
    return switch (this)
    {
      case right -> 0;
      case bottomRight -> 45;
      case down -> 90;
      case bottomLeft -> 135;
      case left -> 180;
      case topLeft -> 225;
      case up -> 270;
      case topRight -> 315;
    };
  }
}
