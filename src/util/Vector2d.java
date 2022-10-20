package util;

import java.util.Objects;

public class Vector2d
{
  public final double x;
  public final double y;

  public Vector2d ()
  {
    x = 0;
    y = 0;
  }

  public Vector2d (double x, double y)
  {
    this.x = x;
    this.y = y;
  }

  public Vector2d cartesian (double x, double y)
  {
    return new Vector2d(x, y);
  }

  public Vector2d polar (double ρ, double φ)
  {
    return new Vector2d(ρ * cos(φ), ρ * sin(φ));
  }

  public Vector2d add (Vector2d v)
  {
    return new Vector2d(x + v.x, y + v.y);
  }

  public Vector2d addScaled (Vector2d v)
  {
    return new Vector2d(x + v.getX(), y + v.getY());
  }

  public Vector2d multiply (double s)
  {
    return new Vector2d(x * s, y * s);
  }

  public Vector2d multiplyX (double s)
  {
    return new Vector2d(x * s, y);
  }

  public Vector2d multiplyY (double s)
  {
    return new Vector2d(x, y * s);
  }

  public Vector2d subtract (Vector2d v)
  {
    return add(v.multiply(-1));
  }

  public Vector2d rotate (double φ)
  {
    return new Vector2d(cos(φ) * x - sin(φ) * y, sin(φ) * x + cos(φ) * y);
  }

  public String toString ()
  {
    return "(%d, %d)".formatted(x, y);
  }

  /**
   * @deprecated
   */
  public double getX ()
  {
    return x;
  }

  /**
   * @deprecated
   */
  public double getY ()
  {
    return y;
  }

  private double sin (double φ)
  {
    return Math.sin(Math.toRadians(φ));
  }

  private double cos (double φ)
  {
    return Math.cos(Math.toRadians(φ));
  }

  @Override
  public boolean equals (Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vector2d vector2D = (Vector2d)o;
    return x == vector2D.x && y == vector2D.y;
  }

  @Override
  public int hashCode ()
  {
    return Objects.hash(x, y);
  }
}
