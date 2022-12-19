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
  private static PacPhiConfig instance;
  public         SettingTree  settings;
  public HashMap<Setting<?>, Context> descriptions;

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

  public void createDefaultSettings()
  {
    //trunk
    settings = new SettingTree(new HashMap <>(), new Setting <>("Settings", "Group", null, null, null));

    //General branch
    SettingTree general = new SettingTree(new HashMap <>(), new Setting <>("General", "Group", null, null, null));
    SettingTree version = new SettingTree(new HashMap <>(), new Setting <>("version", "Value", null, null, null));
    SettingTree branch  = new SettingTree(new HashMap <>(), new Setting <>("branch", "Value", "UNSTABLE", new String[]{"STABLE", "UNSTABLE"}, "STABLE"));
    SettingTree debug   = new SettingTree(new HashMap <>(), new Setting <>("debug", "Value", false, new boolean[]{true, false}, false));
    general.add("Version", version);
    general.add("Branch", branch);
    general.add("Debug", debug);
    settings.add("General", general);

    //Graphics branch
    SettingTree graphics = new SettingTree(new HashMap <>(), new Setting <>("Graphics", "Group", null, null, null));
      SettingTree effects = new SettingTree(new HashMap <>(), new Setting <>("Effects", "Value", true, new Boolean[]{true, false}, true));
    SettingTree advGraphics = new SettingTree(new HashMap <>(), new Setting <>("Advanced", "SubGroup", null, null, null));
    SettingTree style = new SettingTree(new HashMap <>(), new Setting <>("Style", "SubGroup", null, null, null));
      SettingTree pacSkin = new SettingTree(new HashMap <>(), new Setting <>("pacSkin", "Value", "Classic", new String[]{"Classic"}, "Classic"));
      SettingTree ghostSkin = new SettingTree(new HashMap <>(), new Setting <>("ghostSkin", "Value", "Classic", new String[]{"Classic", "Profs"}, "Classic"));
      SettingTree mapSkin = new SettingTree(new HashMap <>(), new Setting <>("mapSkin", "Value", "Classic", new String[]{"Classic"}, "Classic"));
      SettingTree wallSkin = new SettingTree(new HashMap <>(), new Setting <>("wallSkin", "Value", "Classic", new String[]{"Classic"}, "Classic"));
      SettingTree itemSkin = new SettingTree(new HashMap <>(), new Setting <>("itemSkin", "Value", "Classic", new String[]{"Classic"}, "Classic"));
    graphics.add("Effects", effects);
    graphics.add("Advanced", advGraphics);
    graphics.add("Style", style);
      style.add("PacSkin", pacSkin);
      style.add("GhostSkin", ghostSkin);
      style.add("MapSkin", mapSkin);
      style.add("WallSkin", wallSkin);
      style.add("ItemSkin", itemSkin);
    settings.add("Graphics", graphics);

    //Audio branch
    SettingTree audio        = new SettingTree(new HashMap <>(), new Setting <>("Audio", "Group", null, null, null));
      SettingTree muted        = new SettingTree(new HashMap <>(), new Setting <>("muted", "Value", false, new Boolean[]{true, false}, false));
    SettingTree masterVolume = new SettingTree(new HashMap <>(), new Setting <>("Volume", Range.class.getSimpleName(), 50, new Range(0, 100, 5), 50));
    SettingTree musicVolume  = new SettingTree(new HashMap <>(), new Setting <>("Volume", Range.class.getSimpleName(), 50, new Range(0, 100, 5), 50));
    SettingTree soundVolume  = new SettingTree(new HashMap <>(), new Setting <>("Volume", Range.class.getSimpleName(), 50, new Range(0, 100, 5), 50));
    audio.add("Muted", muted);
    audio.add("Master", masterVolume);
    audio.add("Music", musicVolume);
    audio.add("Sound", soundVolume);
    settings.add("Audio", audio);
  }

  public void createDescriptions(){
    descriptions= new HashMap<>();
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

  public record Context(String displayName, String info) { }

  record Range(int min, int max, int stepSize) { }
}
