package kn.uni.util;

import org.ini4j.Wini;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class ConfigEditor
{
  public static Wini loadConfig ()
  {
    String os            = System.getProperty("os.name");
    String locationWin   = System.getProperty("user.home") + "\\Appdata\\Roaming\\Pacphi\\";
    String locationLinux = System.getProperty("user.home") + "/local/share/Pacphi/";
    String path          = "";

    if (os.contains("Windows")) path = locationWin;
    else if (os.contains("Linux")) path = locationLinux;

    File f = new File(path + "settings.ini");

    if (!f.exists())
    {
      try
      {
        //load default config
        String content = new Scanner(new File("src/main/resources/pacman/defaultSettings.ini")).useDelimiter("\\Z").next();
        //create new config file
        FileWriter writer = new FileWriter(f);
        writer.write(content);
        writer.close();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    Wini config = null;
    try
    {
      config = new Wini(f);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return config;
  }

  public static void setState (String section, String option, String value)
  {
    Wini config = loadConfig();
    try
    {
      config.put(section, option, value);
      config.store();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private String getState (String section, String option)
  {
    Wini config = loadConfig();
    return config.get(section, option, String.class);
  }

}
