package kn.uni.util;

public enum Direction
{
  up,
  down,
  left,
  right;

  public Vector2d toVector ()
  {
    return switch (this)
      {
        case up -> new Vector2d().cartesian(0, -1);
        case down -> new Vector2d().cartesian(0, 1);
        case left -> new Vector2d().cartesian(-1, 0);
        case right -> new Vector2d().cartesian(1, 0);
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
      };
  }
}
