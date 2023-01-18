package kn.uni;

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

    //    System.setProperty("net.java.games.input.librarypath", new File("target/natives/").getAbsolutePath());

    //load Config and set branch specific settings
    PacPhiConfig.getInstance().load();
    PacPhiConfig.getInstance().settings.get("General").get("-").set("Version", GAME_VERSION);
    PacPhiConfig.getInstance().settings.get("General").get("-").set("Branch", GAME_BRANCH);
    PacPhiConfig.getInstance().settings.get("Debugging").get("-").set("Enabled", false);
    PacPhiConfig.getInstance().save();

    permissions = (Permission) JsonEditor.load(new Permission(), "Permission");
    assert permissions != null;
    System.out.println(permissions.current);


    DatabaseAccess dba = (DatabaseAccess) JsonEditor.load(new DatabaseAccess(), "DatabaseAccess");
    assert dba != null;
    database = dba.getMatchingPermissionDatabase(permissions.current);

    System.out.println(database.url);

    if (database != null)
      System.out.println("Database access found");
    else System.out.println("No database for current permissions found");

    Gui.getInstance().initialize();
  }

}

