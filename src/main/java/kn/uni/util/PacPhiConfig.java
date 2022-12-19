package kn.uni.util;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import static java.nio.file.Files.createDirectories;

public class PacPhiConfig
{
  //make singleton
  private static PacPhiConfig                   instance;
  public         SettingTree                    settings;
  public         HashMap <Setting <?>, Context> descriptions;

  public PacPhiConfig ()
  {
    createDefaultSettings();
    createDescriptions();
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
    SettingTree general = new SettingTree(new HashMap <>(), new Setting <>("General", "Group", null, null, null), 0);
    SettingTree version = new SettingTree(new HashMap <>(), new Setting <>("Version", "Value", null, null, null), 1);
    SettingTree branch  = new SettingTree(new HashMap <>(), new Setting <>("Branch", "Value", "UNSTABLE", new String[]{ "STABLE", "UNSTABLE" }, "STABLE"), 2);
    SettingTree debug   = new SettingTree(new HashMap <>(), new Setting <>("Debug", "Value", false, new boolean[]{ true, false }, false), 3);
    general.add("Version", version);
    general.add("Branch", branch);
    general.add("Debug", debug);
    settings.add("General", general);

    //Graphics branch
    SettingTree graphics     = new SettingTree(new HashMap <>(), new Setting <>("Graphics", "Group", null, null, null), 1);
    SettingTree effects      = new SettingTree(new HashMap <>(), new Setting <>("Effects", "Value", true, new Boolean[]{ true, false }, true), 0);
    SettingTree advGraphics  = new SettingTree(new HashMap <>(), new Setting <>("Advanced", "SubGroup", null, null, null), 1);
    SettingTree style        = new SettingTree(new HashMap <>(), new Setting <>("Style", "SubGroup", null, null, null), 2);
    SettingTree menuSkin     = new SettingTree(new HashMap <>(), new Setting <>("MenuSkin", "Value", "Classic", new String[]{ "Classic" }, "Classic"), 0);
    SettingTree seasonalSkin = new SettingTree(new HashMap <>(), new Setting <>("SeasonalSkin", "Value", false, new Boolean[]{ true, false }, false), 1);
    SettingTree pacSkin      = new SettingTree(new HashMap <>(), new Setting <>("PacSkin", "Value", "Classic", new String[]{ "Classic" }, "Classic"), 2);
    SettingTree ghostSkin    = new SettingTree(new HashMap <>(), new Setting <>("GhostSkin", "Value", "Classic", new String[]{ "Classic", "Profs" }, "Classic"), 3);
    SettingTree mapSkin      = new SettingTree(new HashMap <>(), new Setting <>("MapSkin", "Value", "Classic", new String[]{ "Classic", "Forest", "Dungeon", "Cave" }, "Classic"), 4);
    SettingTree wallSkin     = new SettingTree(new HashMap <>(), new Setting <>("WallSkin", "Value", "Classic", new String[]{ "Classic", "Forest", "Dungeon", "Cave" }, "Classic"), 5);
    SettingTree itemSkin     = new SettingTree(new HashMap <>(), new Setting <>("ItemSkin", "Value", "Classic", new String[]{ "Classic" }, "Classic"), 6);
    graphics.add("Effects", effects);
    graphics.add("Advanced", advGraphics);
    graphics.add("Style", style);
    style.add("MenuSkin", menuSkin);
    style.add("SeasonalSkin", seasonalSkin);
    style.add("PacSkin", pacSkin);
    style.add("GhostSkin", ghostSkin);
    style.add("MapSkin", mapSkin);
    style.add("WallSkin", wallSkin);
    style.add("ItemSkin", itemSkin);
    settings.add("Graphics", graphics);

    //Audio branch
    SettingTree audio        = new SettingTree(new HashMap <>(), new Setting <>("Audio", "Group", null, null, null), 2);
    SettingTree muted        = new SettingTree(new HashMap <>(), new Setting <>("muted", "Value", false, new Boolean[]{ true, false }, false), 1);
    SettingTree masterVolume = new SettingTree(new HashMap <>(), new Setting <>("Volume", Range.class.getSimpleName(), 50, new Range(0, 100, 5), 50), 2);
    SettingTree musicVolume  = new SettingTree(new HashMap <>(), new Setting <>("Volume", Range.class.getSimpleName(), 50, new Range(0, 100, 5), 50), 3);
    SettingTree soundVolume  = new SettingTree(new HashMap <>(), new Setting <>("Volume", Range.class.getSimpleName(), 50, new Range(0, 100, 5), 50), 4);
    audio.add("Muted", muted);
    audio.add("Master", masterVolume);
    audio.add("Music", musicVolume);
    audio.add("Sound", soundVolume);
    settings.add("Audio", audio);

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
    descriptions.put(settings.get("Graphics").get("Effects").setting, new Context("Effekte", "Aktiviert oder deaktiviert die Effekte"));
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
  }

  public record Context(String displayName, String info) { }

  record Range(int min, int max, int stepSize) { }
}
