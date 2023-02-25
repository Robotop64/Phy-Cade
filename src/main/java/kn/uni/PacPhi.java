package kn.uni;

import kn.uni.games.classic.pacman.screens.AdvGameScreen;
import kn.uni.util.fileRelated.Database;
import kn.uni.util.fileRelated.DatabaseAccess;
import kn.uni.util.fileRelated.JsonEditor;
import kn.uni.util.fileRelated.PacPhiConfig;
import kn.uni.util.fileRelated.Permission;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

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

    //    FlatDarculaLaf.setup();
    //
    //    Gui.getInstance().initialize();


    //    AdvPacManMap map = new AdvPacManMap();
    //    map.calculateAbsolutes(new Dimension(400, 400));


    class MyFrame extends JFrame
    {
      public MyFrame ()
      {
        super();
        setSize(Gui.defaultFrameBounds.getSize());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        JPanel panel = new JPanel();
        panel.setSize(Gui.defaultFrameBounds.getSize());
        add(panel);

        panel.add(new AdvGameScreen(panel));
      }

      //      public void paint (Graphics g)
      //      {
      //        map.paintComponent(g);
      //      }
    }

    new MyFrame();

  }

  public static void getSettings ()
  {
    PacPhiConfig.getInstance().load();
    PacPhiConfig.getInstance().settings.get("General").get("-").set("Version", GAME_VERSION);
    PacPhiConfig.getInstance().settings.get("General").get("-").set("Branch", GAME_BRANCH);
    PacPhiConfig.getInstance().settings.get("Debugging").get("-").set("Enabled", false);
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

