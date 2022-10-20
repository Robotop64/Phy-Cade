package util;


import java.util.Objects;

/**
 * @deprecated
 */
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
    return new Vector2((((long)(ρ * cos(φ))) << scale), ((long)(ρ * sin(φ))) << scale, scale);
  }

  public Vector2 add (Vector2 v)
  {
    return new Vector2(x + v.x, y + v.y, scale);
  }

  public Vector2 addScaled (Vector2 v)
  {
    return new Vector2(x + (v.getX() << scale), y + (v.getY() << scale), scale);
  }

  public Vector2 multiply (long s)
  {
    return new Vector2(x * s, y * s, scale);
  }

  public Vector2 multiplyX (long s)
  {
    return new Vector2(x * s, y, scale);
  }

  public Vector2 multiplyY (long s)
  {
    return new Vector2(x, y * s, scale);
  }

  public Vector2 subtract (Vector2 v)
  {
    return add(v.multiply(-1));
  }

  public Vector2 rotate (long φ)
  {
    return new Vector2((long)(cos(φ) * x - sin(φ) * y), (long)(sin(φ) * x + cos(φ) * y), scale);
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

  private double sin (long φ)
  {
    return Math.sin(Math.toRadians(φ));
  }

  private double cos (long φ)
  {
    return Math.cos(Math.toRadians(φ));
  }

  @Override
  public boolean equals (Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vector2 vector2 = (Vector2)o;
    return x == vector2.x && y == vector2.y;
  }

  @Override
  public int hashCode ()
  {
    return Objects.hash(x, y);
  }
}


