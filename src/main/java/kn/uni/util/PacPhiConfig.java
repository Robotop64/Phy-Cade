package kn.uni.util;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.nio.file.Files.createDirectories;

public class PacPhiConfig
{
  //make singleton
  private static PacPhiConfig                   instance;
  public         SettingTree                    settings;
  public         HashMap <Setting <?>, Context> descriptions;
  public static List<SettingTree> nonEditable = new ArrayList<>();;
  public static List<SettingTree> debugOnly= new ArrayList<>();;

  public PacPhiConfig ()
  {
    createDefaultSettings();
    //createDescriptions();
  }

  public static PacPhiConfig getInstance ()
  {
    if (instance == null) instance = new PacPhiConfig();
    return instance;
  }

  public void createDefaultSettings ()
  {
    //trunk
    settings = new SettingTree(new HashMap <>(), new Setting <>("Settings", "Group", null, null, null), 0);

    //General branch
    settings.addSubGroup("General");
    settings.get("General").addSetting("Version", "Value", null, null, null, false);
    settings.get("General").get("Version").makeNonEditable();
    settings.get("General").addSetting("Branch", "Value", "UNSTABLE", new String[]{ "STABLE", "UNSTABLE" }, "STABLE",true);

    //Debugging branch
    settings.addSubGroup("Debugging");
    settings.get("Debugging").addSetting("Enabled", "Boolean", false, null, null,true);
    settings.get("Debugging").addSetting("Immortal", "Boolean", false, new Boolean[]{ true, false }, false,true);
    settings.get("Debugging").addSetting("StartLives", "Counter", 5, null, 5,true);

    //Gameplay branch
    settings.addSubGroup("Gameplay");


    //Graphics branch
    settings.addSubGroup("Graphics");
    settings.get("Graphics").addSubGroup("General");
    settings.get("Graphics").get("General").addSetting("Effects", "Boolean", true, new Boolean[]{ true, false }, true, true);
    settings.get("Graphics").addSubGroup("Advanced");
    settings.get("Graphics").addSubGroup("Style");
    settings.get("Graphics").get("Style").addSetting("MenuSkin", "Value", "Classic", new String[]{ "Classic" }, "Classic", true);
    settings.get("Graphics").get("Style").addSetting("SeasonalSkins", "Boolean", true, new Boolean[]{ true, false }, true, true);
    settings.get("Graphics").get("Style").addSetting("PlayerSkin",  "Value", "Classic", new String[]{ "Classic" }, "Classic", true);
    settings.get("Graphics").get("Style").addSetting("GhostSkin",  "Value", "Classic", new String[]{ "Classic" }, "Classic", true);
    settings.get("Graphics").get("Style").addSetting("ItemSkin",  "Value", "Classic", new String[]{ "Classic" }, "Classic", true);
    settings.get("Graphics").get("Style").addSetting("MapSkin",  "Value", "Classic", new String[]{ "Classic" }, "Classic", true);
    settings.get("Graphics").get("Style").addSetting("WallSkin",  "Value", "Classic", new String[]{ "Classic" }, "Classic", true);

    //Audio branch
    settings.addSubGroup("Audio");
    settings.get("Audio").addSetting("Enabled", "Boolean", true, new Boolean[]{ true, false }, true, true);
    settings.get("Audio").addSetting("MasterVolume", "Range", 100, new Integer[]{ 0, 100 }, 100, true);
    settings.get("Audio").addSetting("Music", "Boolean", true, new Boolean[]{ true, false }, true, true);
    settings.get("Audio").addSetting("MusicVolume", "Range", 100, new Integer[]{ 0, 100 }, 100, true);
    settings.get("Audio").addSetting("SFX", "Range", 100, new Integer[]{ 0, 100 }, 100, true);
    settings.get("Audio").addSetting("SFXVolume", "Range", 100, new Integer[]{ 0, 100 }, 100, true);
  }

  public void createDescriptions ()
  {
    descriptions = new HashMap <>();
    descriptions.put(settings.get("General").get("Version").setting, new Context("Spiel Version", "Die aktuelle Version des Spiels"));
    descriptions.put(settings.get("General").get("Branch").setting, new Context("Branch", "The branch of the game"));
    descriptions.put(settings.get("General").get("Debug").setting, new Context("Debug Modus",
        """
            Diese Einstellung ist nur für Entwicklungszwecke.
            Erweitert die Anzeige um zusätzliche Informationen.
            Es stehen zusätzliche Einsteluungen zur Verfügung.
            Ein beitragen von Spielständen ist nicht möglich.
            Anmelden ist nicht möglich.
            """));
    descriptions.put(settings.get("Graphics").setting, new Context("Grafik", "Hier können die allgemeinen Grafikeinstellungen vorgenommen werden"));
    descriptions.put(settings.get("Graphics").get("General").get("Effects").setting, new Context("Effekte", "Aktiviert oder deaktiviert die Effekte"));
    descriptions.put(settings.get("Graphics").get("Advanced").setting, new Context("Erweiterte Einstellungen", "Hier können die erweiterten Grafikeinstellungen vorgenommen werden"));
    descriptions.put(settings.get("Graphics").get("Style").setting, new Context("Stil", "Hier können die Stile der Grafikelemente vorgenommen werden"));
    descriptions.put(settings.get("Graphics").get("Style").get("PacSkin").setting, new Context("Spieler", "Bestimmt das Aussehen des Spielers"));
    descriptions.put(settings.get("Graphics").get("Style").get("GhostSkin").setting, new Context("Geister", "Bestimmt das Aussehen der Geister"));
    descriptions.put(settings.get("Graphics").get("Style").get("MapSkin").setting, new Context("Karte", "Bestimmt das Aussehen der Karte"));
    descriptions.put(settings.get("Graphics").get("Style").get("WallSkin").setting, new Context("Wände", "Bestimmt das Aussehen der Wände"));
    descriptions.put(settings.get("Graphics").get("Style").get("ItemSkin").setting, new Context("Gegenstände", "Bestimmt das Aussehen der Items"));
    descriptions.put(settings.get("Audio").setting, new Context("Audio", "Hier können die allgemeinen Audioeinstellungen vorgenommen werden"));
    descriptions.put(settings.get("Audio").get("Master").setting, new Context("Gesamtlautstärke", "Steuert die Lautstärke des gesamten Spiels"));
    descriptions.put(settings.get("Audio").get("Music").setting, new Context("Musiklautstärke", "Steuert die Lautstärke der Musik"));
    descriptions.put(settings.get("Audio").get("Sound").setting, new Context("Soundlautstärke", "Steuert die Lautstärke der Soundeffekte"));

  }

  public void load ()
  {
    Gson gson = new Gson();

    File f = new File(getPath() + "settings.json");

    if (!f.exists())
    {
      createDefaultSettings();
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
      File f = new File(getPath() + "settings.json");
      System.out.println(getPath() + "settings.json");
      createDirectories(f.toPath().getParent());
      f.createNewFile();
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

  // record for setting
  public record Setting <T>(String name, T type, T current, T possibleVal, T defaultVal) { }

  // tree consisting of setting groups and settings as leaves
  public record SettingTree(HashMap <String, SettingTree> children, Setting <?> setting, int order)
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
          new SettingTree(new HashMap <>(), new Setting <>(old.name, old.type, value, old.possibleVal, old.defaultVal), children.get(key).order));
    }

    public void addSubGroup(String name)
    {
      children.put(name, new SettingTree(new HashMap <>(), new Setting<>(name, "SubGroup", null,null,null), children.size()));
    }

    public void addSetting(String name, Object type, Object current, Object possibleVal, Object defaultVal, boolean editable)
    {
      int order = 0;
      if (editable) order = children.size();
      else order = -1;
      children.put(name, new SettingTree(new HashMap <>(), new Setting<>(name, type, current, possibleVal, defaultVal), order));
    }

    public void makeDebug(){
      debugOnly.add(this);
    }

    public void makeNonEditable(){
      nonEditable.add(this);
    }
  }

  public record Context(String displayName, String info) { }

  record Range(int min, int max, int stepSize) { }
}