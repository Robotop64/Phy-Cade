package kn.uni.util.fileRelated;

import com.google.gson.Gson;
import kn.uni.util.PrettyPrint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class JsonEditor
{
  public static boolean silent = false;

  public static Object load (Object obj, String name)
  {
    Gson gson = new Gson();

    File f = new File(getPath() + name + ".json");

    if (!f.exists())
    {
      save(obj, name);

      if (!silent) PrettyPrint.bullet("Created " + name);

      return obj;
    }
    else
    {
      //load json to config
      try
      {
        obj = gson.fromJson(new FileReader(getPath() + name + ".json"), obj.getClass());

        if (!silent) PrettyPrint.bullet("Loaded " + name);

        return obj;
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();
        if (!silent) PrettyPrint.bullet("Failed to load " + name);
      }
    }
    return null;
  }

  public static void save (Object obj, String name)
  {

    //create dir if not exists
    String dirPath   = getPath();
    File   directory = new File(dirPath);
    if (!directory.exists())
    {
      directory.mkdirs();
    }

    //save obj to json
    Gson gson = new Gson();

    try (FileWriter b = new FileWriter(getPath() + name + ".json"))
    {
      gson.toJson(obj, b);
      b.flush();

      if (!silent)  PrettyPrint.bullet("Saved " + name);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (!silent) PrettyPrint.bullet("Failed to save " + name);
    }
  }

  public static String getPath ()
  {
    String os            = System.getProperty("os.name");
    String locationWin   = System.getProperty("user.home") + "\\Appdata\\Roaming\\Pacphi\\";
    String locationLinux = System.getProperty("user.home") + "/local/share/Pacphi/";
    String path          = "";

    if (os.contains("Windows")) path = locationWin;
    else if (os.contains("Linux")) path = locationLinux;
    return path;
  }
}
