package kn.uni.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Fira
{
	//path to fira code font file resource
	private static final String path = "fonts/Fira Code Regular Nerd Font Complete.ttf";

	private static Fira instance;

	private final Font regular;
	private final Font underlined;
	private final Font ligatures;
	private final Font ligaturesUnderlined;

	//private constructor
	private Fira ()
	{
		Font baseFont;
		try
		{
			baseFont = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getClassLoader().getResource(path)).openStream());
		}
		catch (FontFormatException | IOException e)
		{
			throw new RuntimeException(e);
		}

		if (baseFont == null)
		{
			throw new RuntimeException("Font could not be loaded");
		}

		regular = baseFont.deriveFont(20f);
		// add ligatures / underline to font
		Map attributes = baseFont.getAttributes();
		attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
		ligatures = baseFont.deriveFont(attributes).deriveFont(20f);
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		ligaturesUnderlined = baseFont.deriveFont(attributes);
		attributes.remove(TextAttribute.LIGATURES);
		underlined = baseFont.deriveFont(attributes);
	}

	//singleton pattern
	public static Fira getInstance ()
	{
		if (instance == null)
		{
			instance = new Fira();
		}
		return instance;
	}

	//get regular font
	public Font getRegular (float size)
	{
		return regular.deriveFont(size);
	}

	//get font with ligatures
	public Font getLigatures (float size)
	{
		return ligatures.deriveFont(size);
	}

	//get regular underlined
	public Font getUnderlined (float size)
	{
		return underlined.deriveFont(size);
	}

	//get ligatures underlined
	public Font getLigaturesUnderlined (float size)
	{
		return ligaturesUnderlined.deriveFont(size);
	}


}
