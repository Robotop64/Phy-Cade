package kn.uni.games.classic.pacman.game.hud;


import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PlacedObject;
import kn.uni.games.classic.pacman.game.Rendered;
import kn.uni.games.classic.pacman.persistence.PacmanDatabaseProvider;
import kn.uni.games.classic.pacman.screens.LeaderboardMenu;
import kn.uni.games.classic.pacman.screens.LeaderboardMenu.LeaderboardEntry;
import kn.uni.util.Fira;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class DynamicLeaderboard extends PlacedObject implements Rendered
{
	private final LeaderboardEntry[]            dynBoard        = new LeaderboardEntry[3];
	private final LinkedList <LeaderboardEntry> currentSupply   = new LinkedList <>();
	public        Vector2d                      size;
	private       boolean                       useDatabase     = true;
	private       boolean                       newHighscore    = false;
	private       boolean                       fetching        = false;
	private       int                           entriesPerFetch = 1000;

	public DynamicLeaderboard (Vector2d pos, Vector2d size)
	{
		this.pos = pos;
		this.size = size;

		// in case no entries available
		dynBoard[0] = new LeaderboardMenu.LeaderboardEntry(0," ", 0,0, LocalTime.now(), LocalDate.now(),"","");
		dynBoard[1] = new LeaderboardMenu.LeaderboardEntry(0,"You", 0,0, LocalTime.now(), LocalDate.now(),"","");
		dynBoard[2] = new LeaderboardMenu.LeaderboardEntry(0," ", 0,0, LocalTime.now(), LocalDate.now(),"","");

		//try fetching entries
		try
		{
			fetchEntries(0, entriesPerFetch);
		}
		catch (Exception e)
		{
			useDatabase = false;
			e.printStackTrace();
			System.out.println("Can't access database, continue without dynamic leaderboard!");
		}


	}

	@Override
	public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
	{
		String header = "Dynamic Leaderboard:";

		dynBoard[1] = new LeaderboardMenu.LeaderboardEntry(0,"You",0, gameState.score, LocalTime.now(), LocalDate.now(),"","");

		setDynBoard(gameState);


		String nextScore   = "❰" + "%9d".formatted(dynBoard[2].highScore()) + "❱";
		String nextName    = "%1$-16s".formatted(dynBoard[2].name());
		String youScore    = "❰" + "%9d".formatted(dynBoard[1].highScore()) + "❱";
		String youName     = "%1$-16s".formatted("You");
		String behindScore = "❰" + "%9d".formatted(dynBoard[0].highScore()) + "❱";
		String behindName  = "%1$-16s".formatted(dynBoard[0].name());

		if (newHighscore)
		{
			nextScore = "!Neuer Highscore!";
			nextName = "%1$-16s".formatted(" ");
		}

		String[] list = { nextScore, nextName, youScore, youName, behindScore, behindName };

		pos.use(g::translate);
		g.setFont(Fira.getInstance().getLigaturesUnderlined(getFontSize(header, gameState)));
		g.drawString(header, 3, (int)(18.4 * header.length() / 160. * getFontSize(header, gameState)));

		for (int i = 0; i < list.length; i++)
		{
			drawString(g, list[i], gameState, i);
		}

		pos.multiply(-1).use(g::translate);

	}

	@Override
	public int paintLayer ()
	{
		return Integer.MAX_VALUE - 50;
	}

	private int getFontSize (String text, ClassicPacmanGameState gameState)
	{
		return (int)(((size.x / text.length() * 32 / 20) / 100 * gameState.uiSize));
	}

	private void drawString (Graphics2D g, String text, ClassicPacmanGameState gameState, int row)
	{
		//used to shift the rows
		int rowOffset;

		if (row % 2 == 0)
		{
			rowOffset = row * (40);
		}
		else
		{
			rowOffset = row * 40 - 10;
		}
		rowOffset += 50;

		//used to underline the names as separation
		if (row % 2 == 0)
		{
			g.setFont(Fira.getInstance().getLigatures(getFontSize(text, gameState)));
		}
		else
		{
			g.setFont(Fira.getInstance().getLigaturesUnderlined(getFontSize(text, gameState)));
		}

		g.setStroke(new BasicStroke(1));
		g.drawString(text, 3, (int)(18.4 * text.length() / 160. * getFontSize(text, gameState) + rowOffset));
	}

	private void fetchEntries (long score, int end)
	{
		if (!fetching && !newHighscore)
		{
			fetching = true;
			Thread thread = new Thread(() ->
			{

				currentSupply.addAll(PacmanDatabaseProvider.dynLeaderboard("pacman", score, end));

				if (currentSupply.size() == 0)
				{
					dynBoard[0] = dynBoard[2];
					newHighscore = true;
				}
				fetching = false;
				System.out.println("Done fetching new Entries!");
			});
			thread.start();

			System.out.println("Started fetching new Entries!");
		}

	}

	private void setDynBoard (ClassicPacmanGameState gameState)
	{
		if (useDatabase)
		{
			if (currentSupply.size() == 0)
			{
				fetchEntries(gameState.score, entriesPerFetch);
			}
			else
			{
				while (dynBoard[2].highScore() < gameState.score && currentSupply.size() != 0)
				{
					dynBoard[0] = dynBoard[2];
					dynBoard[2] = currentSupply.pop();
				}
			}
		}
	}

}
