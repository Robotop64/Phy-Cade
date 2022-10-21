package persistence;

import ui.LeaderboardMenu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProvider
{
  private static List <LeaderboardMenu.LeaderboardEntry> query (String command)
  {
    String url      = "jdbc:mysql://localhost:3306/leaderboard";
    String username = "root";
    String password = "sQuidWart";

    System.out.println("Connecting database...");

    try (Connection connection = DriverManager.getConnection(url, username, password))
    {

      System.out.println("Database connected!");
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(command);

      List <LeaderboardMenu.LeaderboardEntry> listOut = new ArrayList <>();

      while (resultSet.next())
      {
        LeaderboardMenu.LeaderboardEntry out = new LeaderboardMenu.LeaderboardEntry(
            resultSet.getString("playername"),
            Integer.parseInt(resultSet.getString("score")),
            LocalTime.of(00, 00, 00),
            LocalDate.of(01, 01, 01));
        listOut.add(out);
      }
      return listOut;

    }
    catch (SQLException e)
    {
      throw new IllegalStateException("Cannot connect the database!", e);
    }
  }

  public static List <LeaderboardMenu.LeaderboardEntry> getEntries (String game, int start, int end)
  {
    return query("select * from " + game);
  }

}
