package kn.uni.util;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.sqrt;
import static kn.uni.util.Util.cosDeg;
import static kn.uni.util.Util.round;
import static kn.uni.util.Util.sinDeg;

@SuppressWarnings({ "unused", "NonAsciiCharacters" })
public class Vector2d
{
  public final double x;
  public final double y;
  public final double ρ;
  public final double φ;


  public Vector2d ()
  {
    x = 0;
    y = 0;
    ρ = 0;
    φ = 0;
  }

  private Vector2d (double x, double y)
  {
    this.x = x;
    this.y = y;
    ρ = magnitude();
    double deg = Math.toDegrees(Math.atan2(y, x));
    if (deg < 0) deg += 360;
    φ = deg;
  }

  public Vector2d copy () { return new Vector2d(x, y); }

  public double magnitude ()
  {
    return round(sqrt(x * x + y * y));
  }

  public Vector2d cartesian (double x, double y)
  {
    return new Vector2d(x, y);
  }

//  //transform a polar vector to a cartesian vector
//  public Vector2d toCartesian ()
//  {
//    return new Vector2d().polar(x, y);
//  }
//
  //0° is right, 90° is up, 180° is left, 270° is down
  public Vector2d polar (double ρ, double φ)
  {
    return new Vector2d(ρ * cosDeg(φ), ρ * sinDeg(φ));
  }

//  //transform a cartesian vector to a polar vector
//  public Vector2d toPolar ()
//  {
//    double deg = Math.toDegrees(Math.atan2(y, x));
//    if (deg < 0) deg += 360;
//    Vector2d v = new Vector2d(magnitude(), deg);
//    return v;
//  }

  public Vector2d add (Vector2d v) { return new Vector2d(x + v.x, y + v.y); }

  public Vector2d multiply (double s) { return new Vector2d(x * s, y * s); }

  public Vector2d invert () { return this.multiply(-1); }

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
    return new Vector2d(cosDeg(φ) * x - sinDeg(φ) * y, sinDeg(φ) * x + cosDeg(φ) * y);
  }

  public double length ()
  {
    return sqrt(x * x + y * y);
  }

  public Vector2d unitVector ()
  {
    return divide(length());
  }

  public double scalar (Vector2d other)
  {
    return x * other.x + y * other.y;
  }

  public Vector2d projectOn (Vector2d target)
  {
    return target.unitVector().multiply(unitVector().scalar(target.unitVector()) * length());
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
    return IntStream.range(0, (int) x)
                    .mapToObj(i -> IntStream.range(0, (int) y)
                                            .mapToObj(j -> new Vector2d().cartesian(i, j)))
                    .flatMap(Function.identity());
  }

  public Stream <Vector2d> pm (Vector2d v)
  {
    return Stream.of(add(v), subtract(v));
  }

  public Vector2d rounded ()
  {
    return new Vector2d(round(x), round(y));
  }

  public String toString ()
  {
    return "(%f, %f)".formatted(x, y);
  }

  public boolean isHorizontal ()
  {
    return x != 0 && y == 0;
  }

  public boolean isVertical ()
  {
    return x == 0 && y != 0;
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

  public void use (BiConsumer <Integer, Integer> consumer)
  {
    consumer.accept((int) rounded().x, (int) rounded().y);
  }

  public Vector2d floor ()
  {
    return new Vector2d(Math.floor(x), Math.floor(y));
  }

  public Vector2d ceil ()
  {
    return new Vector2d(Math.ceil(x), Math.ceil(y));
  }

  @Override
  public boolean equals (Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vector2d vector2D = (Vector2d) o;
    return rounded().x == vector2D.rounded().x && rounded().y == vector2D.rounded().y;
  }

  public Direction toDirectionCardinal ()
  {
    if (x == 0 && y == 0) return null;
    if (x == 0 && y == -1) return Direction.up;
    if (x == 0 && y == 1) return Direction.down;
    if (x == 1 && y == 0) return Direction.right;
    if (x == -1 && y == 0) return Direction.left;
    throw new IllegalStateException("Vector2d is not a Direction");
  }

  public Direction approxDirection ()
  {
    Vector2d polar = this.unitVector();
    double   φ     = polar.φ;
    φ = φ % 360;

    Direction dir = Direction.up;

    if (φ <= 45 || φ >= 315) dir = Direction.right;
    if (φ >= 45 && φ < 135) dir = Direction.up;
    if (φ >= 135 && φ < 225) dir = Direction.left;
    if (φ >= 225 && φ < 315) dir = Direction.down;

    //swap up and down due to the coordinate system of monitors having a y-axis pointing down
    if (dir == Direction.up) dir = Direction.down;
    else if (dir == Direction.down) dir = Direction.up;

    return dir;
  }

  @Override
  public int hashCode ()
  {
    return Objects.hash(rounded().x, rounded().y);
  }
}
