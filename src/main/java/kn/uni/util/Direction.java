package kn.uni.util;

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
