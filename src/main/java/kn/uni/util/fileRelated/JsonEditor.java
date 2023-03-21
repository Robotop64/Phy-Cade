package kn.uni.util.fileRelated;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class JsonEditor
{
  public static Object load (Object obj, String name)
  {
    Gson gson = new Gson();

    File f = new File(getPath() + name + ".json");

    if (!f.exists())
    {
      save(obj, name);
      System.out.println("Created " + name);
      return obj;
    }
    else
    {
      //load json to config
      try
      {
        obj = gson.fromJson(new FileReader(getPath() + name + ".json"), obj.getClass());
        System.out.println("Loaded " + name);
        return obj;
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static void save (Object obj, String name)
  {
    Gson gson = new Gson();

    gson.toJson(obj, obj.getClass());

    try (FileWriter b = new FileWriter(getPath() + name + ".json"))
    {
      gson.toJson(obj, b);
      b.flush();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }


    //    //save json to file
    //    try
    //    {
    //      File f = new File(getPath() + name + ".json");
    //      createDirectories(f.toPath().getParent());
    //      //noinspection ResultOfMethodCallIgnored
    //      f.createNewFile();
    //      FileWriter writer = new FileWriter(getPath() + name + ".json");
    //      writer.write(json);
    //      writer.flush();
    //      writer.close();
    //      System.out.println("Saved " + name);
    //    }
    //    catch (Exception e)
    //    {
    //      e.printStackTrace();
    //    }
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
