package kn.uni;

import com.formdev.flatlaf.FlatDarculaLaf;
import kn.uni.games.classic.pacman.persistence.PacmanDatabaseProvider;
import kn.uni.ui.Swing.components.PacTree;
import kn.uni.util.PrettyPrint;
import kn.uni.util.fileRelated.Config.Config;
import kn.uni.util.fileRelated.Database;
import kn.uni.util.fileRelated.JsonEditor;
import kn.uni.util.fileRelated.Permission;
import kn.uni.util.fileRelated.ResourceManager;

import javax.swing.UIManager;
import java.util.ArrayList;
import java.util.List;

public class PacPhi
{
  public static final String     GAME_VERSION = "1.0.6";
  public static final String     GAME_BRANCH  = "ENTROPIC";
  public static final String     GAME_UPDATE  = " ";
  public static       Permission permissions;
  public static       Database   database;

  public static double THREAD_DELAY = -1;
  public static Thread benchmarkThread;

  public static void main (String[] args)
  {
    PrettyPrint.announce("Starting PacPhi");
    PrettyPrint.empty();

    getSettings();

    getDatabase();

    benchmark();

    silence();

    FlatDarculaLaf.registerCustomDefaultsSource("Swing");
    FlatDarculaLaf.setup();
    editStyle();

    Gui.getInstance().initialize();
  }

  /**
   * Loads the current settings from the settings file
   */
  private static void getSettings ()
  {
    PrettyPrint.startGroup(PrettyPrint.Type.Message, "Init Settings");

    Config.init();

//    Config.load();

    Config.setCurrent("Debugging/Enabled", false);
    Config.setCurrent("Graphics/Advanced/Antialiasing", true);
    PrettyPrint.bullet("changed startup settings");

    Config.save();

    PrettyPrint.endGroup();
  }

  /**
   * Loads the current database from the database file
   */
  private static void getDatabase ()
  {
    PrettyPrint.startGroup(PrettyPrint.Type.Message, "Init Database");

    PrettyPrint.bullet("Testing available Databases");

    List <String> databases = new ArrayList <>();
    ResourceManager.getFiles("databases").forEach(f ->
    {
      PrettyPrint.subBullet(2, "found: " + f.getName());
      databases.add(f.getName());
    });
    PrettyPrint.empty();
    if (!databases.isEmpty())
      PrettyPrint.bullet("Testing databases");
    databases.forEach(d ->
    {
      PrettyPrint.subBullet(2, "Status(" + d + "):");
      PrettyPrint.subBullet(3, "Connection: " + PacmanDatabaseProvider.testConnection(d));
      PrettyPrint.subBullet(3, "Tables: " + PacmanDatabaseProvider.testTables(d));
    });

    if (databases.isEmpty())
    {
      PrettyPrint.bullet("No databases found");

      PrettyPrint.bullet("Creating new database");
      PacmanDatabaseProvider.createTable("Pacman.db", "classic", List.of("entryId INTEGER PRIMARY KEY", "playerId INTEGER", "playerName TEXT", "level INTEGER", "score REAL", "time TEXT", "date TEXT", "version TEXT", "comment TEXT"));
      getDatabase();
    }
    PrettyPrint.endGroup();
  }

  /**
   * Benchmarking the delay of the Thread.sleep() method
   */
  private static void benchmark ()
  {
    int  iterations = 300;
    long pause      = 8;
    int  repeats    = 1;

    for (int x = 0; x < repeats; x++)
    {
      benchmarkThread = new Thread(() ->
      {
        long start = System.nanoTime();


        for (int i = 0; i < iterations; i++)
        {
          try
          {
            Thread.sleep(pause);
          }
          catch (InterruptedException e)
          {
            throw new RuntimeException(e);
          }
        }

        long end = System.nanoTime();

        double time  = ( end - start ) / 1_000_000.0;
        double delay = ( time - iterations * pause ) / iterations;

        THREAD_DELAY = delay;

        PrettyPrint.startGroup(PrettyPrint.Type.Message, "Thread Benchmark");
        PrettyPrint.bullet("Time: " + String.format("%.4f", time) + "ms");
        PrettyPrint.bullet("rescheduleDelay: " + String.format("%.4f", delay) + "ms");
        PrettyPrint.endGroup();
      });
      benchmarkThread.start();
    }
  }

  private static void silence ()
  {
    JsonEditor.silent = true;
  }

  private static void editStyle(){
    UIManager.put("Tree.collapsedIcon", new PacTree.TreeIcon(0));
    UIManager.put("Tree.expandedIcon", new PacTree.TreeIcon(0));
  }
}

