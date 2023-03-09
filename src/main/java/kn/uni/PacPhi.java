package kn.uni;

import com.formdev.flatlaf.FlatDarculaLaf;
import kn.uni.util.fileRelated.Database;
import kn.uni.util.fileRelated.DatabaseAccess;
import kn.uni.util.fileRelated.JsonEditor;
import kn.uni.util.fileRelated.PacPhiConfig;
import kn.uni.util.fileRelated.Permission;

public class PacPhi
{
  public static final String     GAME_VERSION = "BETA-1.0.3";
  public static final String     GAME_BRANCH  = "UNSTABLE";
  public static final String     GAME_UPDATE  = " ";
  public static       Permission permissions;
  public static       Database   database;

  public static void main (String[] args)
  {

    getSettings();

    getPermission();

    getDatabase();

    FlatDarculaLaf.setup();

    Gui.getInstance().initialize();
  }

  public static void getSettings ()
  {
    PacPhiConfig.getInstance().load();
    PacPhiConfig.getInstance().settings.get("General").get("-").set("Version", GAME_VERSION);
    PacPhiConfig.getInstance().settings.get("General").get("-").set("Branch", GAME_BRANCH);
    PacPhiConfig.getInstance().settings.get("Debugging").get("-").set("Enabled", true);
    PacPhiConfig.getInstance().save();
    PacPhiConfig.getInstance().createDescriptions();
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
}

