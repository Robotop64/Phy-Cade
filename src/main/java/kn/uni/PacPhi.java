package kn.uni;

import kn.uni.util.ConfigEditor;

public class PacPhi
{
  public static final String GAME_VERSION = "BETA-1.0.3";
  public static final String GAME_BRANCH  = "UNSTABLE";

  public static void main (String[] args)
  {

    //    System.setProperty("net.java.games.input.librarypath", new File("target/natives/").getAbsolutePath());
    Gui.getInstance().initialize();
    ConfigEditor.setState("General", "version", GAME_VERSION);
    ConfigEditor.setState("General", "branch", GAME_BRANCH);
  }

}
