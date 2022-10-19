import java.io.File;

public class PacPhi
{

  public static void main (String[] args)
  {

    System.setProperty("net.java.games.input.librarypath", new File("./lib/").getAbsolutePath());
    Gui.getInstance().initialize();

  }

}
