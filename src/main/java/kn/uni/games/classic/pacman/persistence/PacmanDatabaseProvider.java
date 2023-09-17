package kn.uni.games.classic.pacman.persistence;


import kn.uni.games.classic.pacman.screens.LeaderboardMenu;
import kn.uni.util.fileRelated.JsonEditor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@SuppressWarnings("SpellCheckingInspection")
public class PacmanDatabaseProvider
{
  public static boolean silent = true;

  record Query(String command, Boolean expectResult) { }

  private static List <List <String>> makeConnection (String database, List <Query> statements)
  {
    if (!silent) System.out.println("Connecting database...");
    Connection connection = null;
    try
    {
      //connect to database
      String path = JsonEditor.getPath() + "databases/";
      connection = DriverManager.getConnection("jdbc:sqlite:" + path + database);
      if (!silent) System.out.println("Database connected!");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);

      //execute statements
      List <ResultSet> results = new ArrayList <>();
      for (Query s : statements)
      {
        if (s.expectResult)
          results.add(statement.executeQuery(s.command));
        else
          statement.executeUpdate(s.command);
      }

      //stringify results
      //List containing a list of rows for each command
      List <List <String>> out = new ArrayList <>();
      //for each resultset (1 command -> 1 resultset)
      results
          .forEach(
              //for each row (1 resultset -> n rows)
              result -> {
                //create a temporary list containing the rows
                List<String> temp = new ArrayList<>();

                try
                {
                  //while rows are available
                  while (result.next())
                  {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i <= result.getMetaData().getColumnCount(); i++)
                    {
                      sb.append(result.getString(i));
                      if (i < result.getMetaData().getColumnCount())
                        sb.append(", ");
                    }
                    //add the row to the temporary list
                    temp.add(sb.toString());
                  }
                }

                catch (SQLException ignored){}
                out.add(temp);
              }
          );

      return out;
    }
    catch (SQLException e)
    {
      // if the error message is "out of memory",
      // it probably means no database file is found
      if (!silent) System.err.println(e.getMessage());
      if (!silent) System.out.println("Cannot connect the database!");
    }
    finally
    {
      try
      {
        if (connection != null)
        {
          connection.close();
          if (!silent) System.out.println("Database connection closed!");
        }
      }
      catch (SQLException e)
      {
        // connection close failed.
        if (!silent) System.out.println("Cannot close connection!");
      }
    }
    return null;
  }

  public static boolean testConnection (String database)
  {
    boolean    status     = false;
    Connection connection = null;
    try
    {
      //connect to database
      String path = JsonEditor.getPath() + "databases/";
      connection = DriverManager.getConnection("jdbc:sqlite:" + path + database);
      status = true;
    }
    catch (SQLException ignored)
    {
    }
    finally
    {
      try
      {
        if (connection != null)
        {
          connection.close();
        }
      }
      catch (SQLException ignored)
      {
      }
    }

    return status;
  }

  public static List <String> testTables (String database)
  {
    List <String>        out    = new ArrayList <>();
    List <List <String>> result = makeConnection(database, List.of(new Query("select name from sqlite_master where type = 'table';", true)));
    if (result != null)
      result.forEach(out::addAll);
    return out;
  }

  public static void addEntries (String database, String table, List <LeaderboardMenu.LeaderboardEntry> newEntries)
  {
    List <Query> statements = new ArrayList <>();

    for (LeaderboardMenu.LeaderboardEntry e : newEntries)
    {
      statements.add(new Query("insert into " + table + " (entryId,playerId,playername,level,score,time,date,version,comment) values ( " +
          String.format("NULL,%d,\"%s\",%d,%f,\"%s\",\"%s\",\"%s\",\"%s\"", e.accNum(), e.name(), e.level(), (float) e.highScore(), e.time(), e.date(), e.version(), e.notes()) + ");", false));
    }
    makeConnection(database, statements);
  }

  public static List<LeaderboardMenu.LeaderboardEntry> getEntries (String database, String table, String filter, String sort)
  {
    List <String> entryList =
        Objects.requireNonNull(makeConnection(database, List.of(new Query("select * from \"" + table+"\" " + filter+ sort, true)))).get(0);
    List <LeaderboardMenu.LeaderboardEntry> out = new ArrayList <>();
    entryList.forEach(e ->
        out.add(LeaderboardMenu.LeaderboardEntry.fromString(e))
    );
    return out;
  }

  public static List<LeaderboardMenu.LeaderboardEntry> getDynEntries(String database, String table, long score, int end)
  {
    List <String> entryList = Objects.requireNonNull(makeConnection(database,
        List.of(new Query("select * from " + table + " where score >" + score + " Order By  score asc LIMIT " + 0 + "," + end + ";", true)))).get(0);
    List <LeaderboardMenu.LeaderboardEntry> out = new ArrayList <>();
    entryList.forEach(e ->
        out.add(LeaderboardMenu.LeaderboardEntry.fromString(e))
    );
    return out;
  }

  public static void createTable (String database, String table, List <String> columns)
  {
    List <Query> statements = new ArrayList <>();
    statements.add(new Query("CREATE TABLE " + table + " (" + columns.toString().replaceAll("[\\[\\]]", "") + ");", false));
    makeConnection(database, statements);
  }
}

