package kn.uni.games.classic.pacman.persistence;


import kn.uni.PacPhi;
import kn.uni.games.classic.pacman.screens.LeaderboardMenu;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("SpellCheckingInspection")
public class PacmanDatabaseProvider
{
  private static final String url      = "jdbc:mysql://pacphidatabase/leaderboard";
  private static final String username = "pacphi";
  private static final String password = "pacmacbummbumm";

  private static List <LeaderboardMenu.LeaderboardEntry> getQuery (String command)
  {

    System.out.println("Connecting database...");

    try (Connection connection = DriverManager.getConnection(PacPhi.database.url, PacPhi.database.username, PacPhi.database.password))
    {

      System.out.println("Database connected!");
      Statement                               statement = connection.createStatement();
      ResultSet                               resultSet = statement.executeQuery(command);
      List <LeaderboardMenu.LeaderboardEntry> listOut   = new ArrayList <>();

      while (resultSet.next())
      {
        System.out.println();
        LeaderboardMenu.LeaderboardEntry out = new LeaderboardMenu.LeaderboardEntry(
            resultSet.getInt("id"),
            resultSet.getString("playerName"),
            resultSet.getInt("level"),
            resultSet.getLong("score"),
            resultSet.getTime("duration").toLocalTime(),
            resultSet.getDate("datum").toLocalDate(),
            resultSet.getString("version"),
            resultSet.getString("comment"));
        listOut.add(out);
      }
      return listOut;
    }
    catch (SQLException e)
    {
      throw new IllegalStateException("Cannot connect the database!");
    }
  }

  private static void setQuery (String command, LeaderboardMenu.LeaderboardEntry in)
  {

    System.out.println("Connecting database...");

    try (Connection connection = DriverManager.getConnection(PacPhi.database.url, PacPhi.database.username, PacPhi.database.password))
    {

      System.out.println("Database connected!");

      PreparedStatement preparedStmt = connection.prepareStatement(command);
      preparedStmt.setInt(1, 0);
      preparedStmt.setString(2, in.name());
      preparedStmt.setInt(3, in.level());
      preparedStmt.setLong(4, in.highScore());
      preparedStmt.setTime(5, new Time(in.time().getHour(), in.time().getMinute(), in.time().getSecond()));
      preparedStmt.setDate(6, new Date(in.date().getYear() - 1900, in.date().getMonthValue() - 1, in.date().getDayOfMonth()));
      preparedStmt.setString(7, in.version());
      preparedStmt.setString(8, in.notes());

      preparedStmt.execute();
      System.out.println("Entry successfully added!");

    }
    catch (Exception e)
    {
      System.err.println("Got an exception!");
      // printStackTrace method
      // prints line numbers + call stack
      //      e.printStackTrace();
      // Prints what exception has been thrown
      //      System.out.println(e);
    }
  }


  public static List <LeaderboardMenu.LeaderboardEntry> dynLeaderboard (String game, long score, int end)
  {
    return new ArrayList <>(getQuery("select * from " + game + " where score >" + score + " Order By  score asc LIMIT " + 0 + "," + end + ";"));
  }


  public static List <LeaderboardMenu.LeaderboardEntry> getEntries (String game)
  {
    return getQuery("select * from " + game + " Order By  score desc , duration asc");
  }

  public static void setEntries (String game, LeaderboardMenu.LeaderboardEntry in)
  {
    setQuery("insert into " + game + " (matrikelnummer,playername,score,duration,datum) values ( ?, ?, ?, ?, ?);", in);
  }
}

