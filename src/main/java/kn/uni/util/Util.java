package kn.uni.util;

public class Util
{
	public static final double precision = 1e9;

	public static int bounded (int x, int min, int max) {return Math.max(Math.min(x, max), min);}

	public static Vector2d stringToSize (String text, int fontSize)
	{
		int    stringLength      = text.length();
		double widthPerFontSize  = 20 / 32.;
		double heightPerFontSize = 40 / 32.;

		return new Vector2d().cartesian(fontSize * widthPerFontSize * stringLength, fontSize * heightPerFontSize);
	}

	/**
	 * Computes regular sine
	 *
	 * @param φ Angle in <b>DEGREES</b>
	 * @return sin(φ)
	 */
	public static double sin (double φ)
	{
		return Math.sin(Math.toRadians(φ));
	}

	/**
	 * Computes regular cosine
	 *
	 * @param φ Angle in <b>DEGREES</b>
	 * @return cos(φ)
	 */
	public static double cos (double φ)
	{
		return Math.cos(Math.toRadians(φ));
	}

	/**
	 * Rounds to given precision
	 *
	 * @param in
	 * @return
	 */
	public static double round (double in)
	{
		return Math.round(in * precision) / precision;
	}

}
