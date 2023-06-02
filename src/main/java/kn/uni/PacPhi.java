package kn.uni;

import com.formdev.flatlaf.FlatDarculaLaf;
import kn.uni.util.PrettyPrint;
import kn.uni.util.fileRelated.Config.Config;
import kn.uni.util.fileRelated.Database;
import kn.uni.util.fileRelated.DatabaseAccess;
import kn.uni.util.fileRelated.JsonEditor;
import kn.uni.util.fileRelated.Permission;

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

    getPermission();

    getDatabase();

    benchmark();

    FlatDarculaLaf.setup();

    Gui.getInstance().initialize();
  }

  public static void getSettings ()
  {
    PrettyPrint.startGroup(PrettyPrint.Type.Message, "Init Settings");

    Config.init();

    Config.load();

    Config.setCurrent("Debugging/-/Enabled", false);
    Config.setCurrent("Graphics/Advanced/Antialiasing", true);
    PrettyPrint.bullet("changed startup settings");

    Config.save();

    PrettyPrint.endGroup();
  }

  public static void getPermission ()
  {
    PrettyPrint.startGroup(PrettyPrint.Type.Message, "Init Permission");

    permissions = (Permission) JsonEditor.load(new Permission(), "Permission");
    assert permissions != null;

    PrettyPrint.bullet("Level: " + permissions.current);
    PrettyPrint.endGroup();
  }

  public static void getDatabase ()
  {
    PrettyPrint.startGroup(PrettyPrint.Type.Message, "Init Database");

    DatabaseAccess dba = (DatabaseAccess) JsonEditor.load(new DatabaseAccess(), "DatabaseAccess");
    assert dba != null;
    database = dba.getMatchingPermissionDatabase(permissions.current);

    if (database != null)
    {
      PrettyPrint.bullet("found matching Database");
      PrettyPrint.bullet("Database: " + dba.getDatabaseName(database));
    }
    else
      PrettyPrint.bullet("no matching Database found");

    PrettyPrint.endGroup();
  }

  /**
   * Benchmarking the delay of the Thread.sleep() method
   */
  public static void benchmark ()
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
}

