package kn.uni.util;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class PacPhiConfig
{
  //make singleton
  private static PacPhiConfig instance;
  public         SettingTree  settings;

  public PacPhiConfig ()
  { }

  public static PacPhiConfig getInstance ()
  {
    if (instance == null) instance = new PacPhiConfig();
    return instance;
  }

  public void createDefault ()
  {
    //trunk
    settings = new SettingTree(new HashMap <>(), new Setting <>("Settings", "Group", null, null, null));

    //General branch
    SettingTree general = new SettingTree(new HashMap <>(), new Setting <>("General", "Group", null, null, null));
    SettingTree version = new SettingTree(new HashMap <>(), new Setting <>("version", "Value", null, null, null));
    SettingTree branch  = new SettingTree(new HashMap <>(), new Setting <>("branch", "Value", "STABLE", null, null));
    general.add("Version", version);
    general.add("Branch", branch);
    settings.add("General", general);

    //Graphics branch
    SettingTree graphics = new SettingTree(new HashMap <>(), new Setting <>("Graphics", "Group", null, null, null));
    settings.add("Graphics", graphics);

    //Audio branch
    SettingTree audio        = new SettingTree(new HashMap <>(), new Setting <>("Audio", "Group", null, null, null));
    SettingTree masterVolume = new SettingTree(new HashMap <>(), new Setting <>("Volume", Range.class.getSimpleName(), 50, new Range(0, 100, 5), 50));
    SettingTree musicVolume  = new SettingTree(new HashMap <>(), new Setting <>("Volume", Range.class.getSimpleName(), 50, new Range(0, 100, 5), 50));
    SettingTree soundVolume  = new SettingTree(new HashMap <>(), new Setting <>("Volume", Range.class.getSimpleName(), 50, new Range(0, 100, 5), 50));
    audio.add("Master", masterVolume);
    audio.add("Music", musicVolume);
    audio.add("Sound", soundVolume);
    settings.add("Audio", audio);
  }

  public void load ()
  {
    Gson gson = new Gson();

    File f = new File(getPath() + "settings.json");

    if (!f.exists())
    {
      createDefault();
      save();
      System.out.println("Created default settings");
    }
    else
    {
      //load json to config
      try
      {
        settings = gson.fromJson(new FileReader(getPath() + "settings.json"), SettingTree.class);
        System.out.println("Loaded settings");
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();
      }
    }

  }

  public void save ()
  {
    Gson   gson = new Gson();
    String json = gson.toJson(settings, SettingTree.class);
    //save json to file
    try
    {
      FileWriter writer = new FileWriter(getPath() + "settings.json");
      writer.write(json);
      writer.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private String getPath ()
  {
    String os            = System.getProperty("os.name");
    String locationWin   = System.getProperty("user.home") + "\\Appdata\\Roaming\\Pacphi\\";
    String locationLinux = System.getProperty("user.home") + "/local/share/Pacphi/";
    String path          = "";

    if (os.contains("Windows")) path = locationWin;
    else if (os.contains("Linux")) path = locationLinux;
    return path;
  }

  public boolean check (String[] testable)
  {
    Object load = PacPhiConfig.getInstance().settings.get(testable[0]).get(testable[1]).setting().current();

    Gson gson = new Gson();
    try
    {
      SettingTree settings = gson.fromJson(new FileReader(getPath() + "settings.json"), SettingTree.class);
      Object      write    = settings.get(testable[0]).get(testable[1]).setting().current();
      return load.equals(write);
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    return false;
  }

  // record for setting
  public record Setting <T>(String name, T type, T current, T possibleVal, T defaultVal) { }

  // tree consisting of setting groups and settings as leaves
  public record SettingTree(HashMap <String, SettingTree> children, Setting <?> setting)
  {
    @SuppressWarnings("unused")
    public boolean isLeaf () { return children.size() == 0; }

    public SettingTree get (String key)
    {
      return children.get(key);
    }

    public void add (String key, SettingTree value) { children.put(key, value); }

    public void set (String key, Object value)
    {
      Setting <?> old = children.get(key).setting;
      children.replace(
          key,
          new SettingTree(new HashMap <>(), new Setting <>(old.name, old.type, value, old.possibleVal, old.defaultVal)));
    }
  }

  record Range(int min, int max, int stepSize) { }
}
