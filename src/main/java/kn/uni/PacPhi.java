package kn.uni;

import kn.uni.util.PacPhiConfig;

public class PacPhi
{
  public static final String GAME_VERSION = "BETA-1.0.3";
  public static final String GAME_BRANCH  = "UNSTABLE";
  public static final String GAME_UPDATE  = "Settings!!!";

  public static void main (String[] args)
  {

    //    System.setProperty("net.java.games.input.librarypath", new File("target/natives/").getAbsolutePath());
    PacPhiConfig.getInstance().init();
    PacPhiConfig.getInstance().load();
    PacPhiConfig.getInstance().settings.get("General").get("-").set("Version", GAME_VERSION);
    PacPhiConfig.getInstance().settings.get("General").get("-").set("Branch", GAME_BRANCH);
    PacPhiConfig.getInstance().settings.get("Debugging").get("-").set("Enabled", true);
    PacPhiConfig.getInstance().save();

    Gui.getInstance().initialize();

  }

}
