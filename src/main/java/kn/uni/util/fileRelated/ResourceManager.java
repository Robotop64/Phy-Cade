package kn.uni.util.fileRelated;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ResourceManager
{
  public static boolean isPresent (String path)
  {
    return new File(getPath()+path).exists();
  }

  public static List<File> getFiles (String path)
  {
    return List.of(Objects.requireNonNull(new File(getPath() + path).listFiles()));
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
