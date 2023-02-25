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
}
