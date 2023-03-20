package kn.uni;

import com.formdev.flatlaf.FlatDarculaLaf;
import kn.uni.util.fileRelated.Database;
import kn.uni.util.fileRelated.DatabaseAccess;
import kn.uni.util.fileRelated.JsonEditor;
import kn.uni.util.fileRelated.PacPhiConfig;
import kn.uni.util.fileRelated.Permission;

public class PacPhi
{
  public static final String     GAME_VERSION = "BETA-1.0.5";
  public static final String     GAME_BRANCH  = "ENTROPIC";
  public static final String     GAME_UPDATE  = " ";
  public static       Permission permissions;
  public static       Database   database;

  public static double THREAD_DELAY = -1;
  public static Thread benchmarkThread;

  public static void main (String[] args)
  {
    //    System.setProperty("sun.java2d.opengl", "true");

    getSettings();

    getPermission();

    getDatabase();

    benchmark();

    FlatDarculaLaf.setup();

    Gui.getInstance().initialize();
  }

  public static void getSettings ()
  {
    PacPhiConfig.load();
    PacPhiConfig.setCurrent("General", "-", "Version", GAME_VERSION);
    PacPhiConfig.setCurrent("General", "-", "Branch", GAME_BRANCH);
    PacPhiConfig.setCurrent("Debugging", "-", "Enabled", false);
    PacPhiConfig.setCurrent("Graphics", "Advanced", "Antialiasing", true);
    PacPhiConfig.save();
  }

  public static void getPermission ()
  {
    permissions = (Permission) JsonEditor.load(new Permission(), "Permission");
    assert permissions != null;
    System.out.println("Permission-Level: " + permissions.current);
  }

  public static void getDatabase ()
  {
    DatabaseAccess dba = (DatabaseAccess) JsonEditor.load(new DatabaseAccess(), "DatabaseAccess");
    assert dba != null;
    database = dba.getMatchingPermissionDatabase(permissions.current);

    if (database != null)
    {
      System.out.println("Database access found");
      System.out.println("Database: " + dba.getDatabaseName(database));
    }
    else System.out.println("No database for current permissions found");
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

        System.out.println("Thread-Benchmark: " + "Time: " + String.format("%.4f", time) + "ms, rescheduleDelay " + String.format("%.4f", delay) + "ms");
        THREAD_DELAY = delay;
      });
      benchmarkThread.start();
    }
  }
}

