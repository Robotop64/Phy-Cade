package util;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Vector2d
{
  public final double x;
  public final double y;

  public Vector2d ()
  {
    x = 0;
    y = 0;
  }

  private Vector2d (double x, double y)
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

  public Vector2d multiply (double s)
  {
    return new Vector2d(x * s, y * s);
  }

  public Vector2d divide (double d)
  {
    return multiply(1.0 / d);
  }

  public Vector2d subtract (Vector2d v)
  {
    return add(v.multiply(-1));
  }

  public Vector2d rotate (double φ)
  {
    return new Vector2d(cos(φ) * x - sin(φ) * y, sin(φ) * x + cos(φ) * y);
  }

  public double lenght ()
  {
    return Math.sqrt(x * x + y * y);
  }

  public Vector2d unitVector ()
  {
    return divide(lenght());
  }

  public double scalar (Vector2d other)
  {
    return x * other.x + y * other.y;
  }

  public Vector2d projectOn (Vector2d target)
  {
    return target.unitVector().multiply(unitVector().scalar(target.unitVector()) * lenght());
  }

  public Vector2d orthogonalTo (Vector2d target)
  {
    return subtract(projectOn(target));
  }

  /**
   * creates a stream of all Points in [0, x] × [0, y] ∩ ℕ
   *
   * @return {@link Stream} of {@link Vector2d}
   */
  public Stream <Vector2d> stream ()
  {
    return IntStream.range(0, (int)x)
                    .mapToObj(i -> IntStream.range(0, (int)y)
                                            .mapToObj(j -> new Vector2d().cartesian(i, j)))
                    .flatMap(Function.identity());
  }

  public String toString ()
  {
    return "(%f, %f)".formatted(x, y);
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
