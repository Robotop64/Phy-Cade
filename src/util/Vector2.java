package util;

public class Vector2
{

  public static final Vector2 WHOLE = new Vector2(0);

  final long x;
  final long y;
  final long scale;

  public Vector2 (long scale)
  {
    this.scale = scale;
    x = 0;
    y = 0;
  }

  public Vector2 ()
  {
    this.scale = 0;
    x = 0;
    y = 0;
  }

  public Vector2 (long x, long y, long scale)
  {
    this.x = x;
    this.y = y;
    this.scale = scale;
  }

  public Vector2 cartesian (long x, long y)
  {
    return new Vector2(x << scale, y << scale, scale);
  }

  public Vector2 polar (long ρ, long φ)
  {
    return new Vector2((((long)(ρ * Math.cos(φ))) << scale), ((long)(ρ * Math.sin(φ))) << scale, scale);
  }

  public Vector2 add (Vector2 v)
  {
    return new Vector2(x + v.x, y + v.y, scale);
  }

  public Vector2 mutliply (long s)
  {
    return new Vector2(x * s, y * s, scale);
  }

  public Vector2 subtract (Vector2 v)
  {
    return add(v.mutliply(-1));
  }

  public String toString ()
  {
    return "(%d, %d)".formatted(x >> scale, y >> scale);
  }

  public String exactRepresentation ()
  {
    long divisor = (long)Math.pow(2, scale);
    return "(%d/%d, %d/%d)".formatted(x, divisor, y, divisor);
  }

  public long getX ()
  {
    return x >> scale;
  }

  public long getY ()
  {
    return y >> scale;
  }
}
