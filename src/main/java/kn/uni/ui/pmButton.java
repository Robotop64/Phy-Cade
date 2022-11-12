package kn.uni.ui;

import kn.uni.util.Fira;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;
import java.util.Set;

public class pmButton extends JLabel
{

	public boolean isSelected;

	private Set <Runnable> actions = new HashSet <>();

	public pmButton (String text)
	{
		super(text);
		setBorder(BorderFactory.createLineBorder(Color.yellow, 3, true));
		setForeground(Color.yellow);
		setBackground(Color.black);
		setHorizontalAlignment(CENTER);
		//    setFont(new Font("Fira Code", Font.PLAIN, 32));
		setFontSize(32);

	}

	public void setFontSize (int size)
	{
		Font old = this.getFont();
		setFont(new Font(old.getName(), old.getStyle(), size));
	}

	public void update ()
	{
		Color color = isSelected ? Color.yellow : Color.cyan.darker();
		setBorder(BorderFactory.createLineBorder(color, 3, true));
		setForeground(color);
	}

	public void press ()
	{
		actions.forEach(Runnable::run);
	}

	public void removeAction (Runnable a)
	{
		actions.remove(a);
	}

	public void clearActions ()
	{
		actions.clear();
	}

	public void addAction (Runnable a)
	{
		actions.add(a);
	}

	public void setTheme (String theme)
	{
		switch (theme)
		{
			case "Normal" ->
			{
				setForeground(Color.yellow);
				setBackground(Color.black);
				//        this.setFont(new Font("Comic Sans MS", Font.PLAIN, 32));
				setFont(Fira.getInstance().getLigatures(32));
			}
			case "Leaderboard" ->
			{
				setForeground(Color.cyan);
				setBackground(Color.black);
				setFont(Fira.getInstance().getLigatures(32));
				setBorder(BorderFactory.createLineBorder(Color.black, 3, true));
			}
			case "Leaderboard-GUI" ->
			{
				setForeground(Color.cyan);
				setBackground(Color.black);
				setFont(Fira.getInstance().getLigatures(32));
				setBorder(BorderFactory.createLineBorder(Color.cyan, 3, true));
			}
			default ->
			{
			}
		}
	}

}


