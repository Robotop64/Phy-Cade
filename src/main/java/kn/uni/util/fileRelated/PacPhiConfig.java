package kn.uni.util.fileRelated;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PacPhiConfig
{
  //make singleton
  private static PacPhiConfig              instance;
  public         List <SettingTree>        nonEditable = new ArrayList <>();
  public         List <SettingTree>        debugOnly   = new ArrayList <>();
  public         SettingTree               settings;
  public         HashMap <String, Context> descriptions;

  public PacPhiConfig ()
  {
  }

  public static PacPhiConfig getInstance ()
  {
    if (instance == null) instance = new PacPhiConfig();
    return instance;
  }

  public static boolean checkSetting (String group, String subGroup, String setting, Object value)
  {
    return PacPhiConfig.getInstance().settings.get(group).get(subGroup).get(setting).setting().current().equals(value);
  }

  public static Object getCurrent (String group, String subGroup, String setting)
  {
    return PacPhiConfig.getInstance().settings.get(group).get(subGroup).get(setting).setting().current();
  }

  public static Setting getSetting (String group, String subGroup, String setting)
  {
    return PacPhiConfig.getInstance().settings.get(group).get(subGroup).get(setting).setting();
  }

  public static void setCurrent (String group, String subGroup, String setting, Object value)
  {
    PacPhiConfig.getInstance().settings.get(group).get(subGroup).set(setting, value);
  }

  public static void load ()
  {
    //create defaults
    PacPhiConfig.getInstance().init();
    //load settings
    PacPhiConfig.getInstance().settings = (SettingTree) JsonEditor.load(PacPhiConfig.getInstance().settings, "settings");
  }

  public static void save ()
  {
    JsonEditor.save(PacPhiConfig.getInstance().settings, "settings");
  }

  public void init ()
  {
    createDefaultSettings();
    createDescriptions();
  }

  public void createDefaultSettings ()
  {
    //trunk
    settings = new SettingTree(new TreeMap <>(), new Setting <>("Settings", "Group", null, null, null), 0);

    //General branch
    settings.addSubGroup("General");
    settings.get("General").addSubGroup("-");
    settings.get("General").get("-").addSetting("Version", "Value", null, null, null, false);
    settings.get("General").get("-").get("Version").makeNonEditable();
    settings.get("General").get("-").addSetting("Branch", "Value", "UNSTABLE", new String[]{ "STABLE", "UNSTABLE" }, "STABLE", true);
    settings.get("General").get("-").addSetting("AccessLevel", "Value", "User", new String[]{ "User", "Developer" }, "User", false);
    settings.get("General").get("-").get("AccessLevel").makeNonEditable();

    //Debugging branch
    settings.addSubGroup("Debugging");
    settings.get("Debugging").addSubGroup("-");
    settings.get("Debugging").get("-").addSetting("Enabled", "Boolean", false, null, null, true);
    settings.get("Debugging").addSubGroup("Graphics");
    settings.get("Debugging").addSubGroup("Gameplay");
    settings.get("Debugging").get("Gameplay").addSetting("Immortal", "Boolean", false, new Boolean[]{ true, false }, false, true);
    settings.get("Debugging").get("Gameplay").get("Immortal").makeDebug();

    //Gameplay branch
    settings.addSubGroup("Gameplay");
    settings.get("Gameplay").addSubGroup("-");
    settings.get("Gameplay").addSubGroup("PacMan");
    settings.get("Gameplay").get("PacMan").addSetting("StartLives", "Value", 5, null, 5, false);
    settings.get("Gameplay").get("PacMan").get("StartLives").makeDebug();
    settings.get("Gameplay").get("PacMan").addSetting("StartSpeed", "Value", 6, null, 6, false);
    settings.get("Gameplay").get("PacMan").get("StartSpeed").makeDebug();
    settings.get("Gameplay").get("PacMan").addSetting("PlayerHP", "Value", 1, null, 1, false);
    settings.get("Gameplay").get("PacMan").get("PlayerHP").makeDebug();
    settings.get("Gameplay").get("PacMan").addSetting("GhostHP", "Value", 1, null, 1, false);
    settings.get("Gameplay").get("PacMan").get("GhostHP").makeDebug();
    settings.get("Gameplay").get("PacMan").addSetting("PointsToLife", "Value", 10000, null, 10000, false);
    settings.get("Gameplay").get("PacMan").get("PointsToLife").makeDebug();


    //Graphics branch
    settings.addSubGroup("Graphics");
    settings.get("Graphics").addSubGroup("General");
    settings.get("Graphics").get("General").addSetting("Effects", "Boolean", true, new Boolean[]{ true, false }, true, true);
    settings.get("Graphics").addSubGroup("Advanced");
    settings.get("Graphics").get("Advanced").addSetting("Antialiasing", "Boolean", false, new Boolean[]{ true, false }, false, true);
    settings.get("Graphics").addSubGroup("Style");
    settings.get("Graphics").get("Style").addSetting("MenuSkin", "Value", "Classic", new String[]{ "Classic" }, "Classic", true);
    settings.get("Graphics").get("Style").addSetting("SeasonalSkins", "Boolean", true, new Boolean[]{ true, false }, true, true);
    settings.get("Graphics").get("Style").addSetting("PlayerSkin", "Value", "Classic", new String[]{ "Classic" }, "Classic", true);
    settings.get("Graphics").get("Style").addSetting("GhostSkin", "Value", "Classic", new String[]{ "Classic" }, "Classic", true);
    settings.get("Graphics").get("Style").addSetting("ItemSkin", "Value", "Classic", new String[]{ "Classic" }, "Classic", true);
    settings.get("Graphics").get("Style").addSetting("MapSkin", "Value", "Classic", new String[]{ "Classic" }, "Classic", true);
    settings.get("Graphics").get("Style").addSetting("WallSkin", "Value", "Classic", new String[]{ "Classic" }, "Classic", true);

    //Audio branch
    settings.addSubGroup("Audio");
    settings.get("Audio").addSubGroup("General");
    settings.get("Audio").get("General").addSetting("Enabled", "Boolean", true, new Boolean[]{ true, false }, true, true);
    settings.get("Audio").get("General").addSetting("MasterVolume", "Range", 100, new Integer[]{ 0, 100 }, 100, true);
    settings.get("Audio").get("General").addSetting("Music", "Boolean", true, new Boolean[]{ true, false }, true, true);
    settings.get("Audio").get("General").addSetting("MusicVolume", "Range", 100, new Integer[]{ 0, 100 }, 100, true);
    settings.get("Audio").get("General").addSetting("SFX", "Boolean", true, new Boolean[]{ true, false }, true, true);
    settings.get("Audio").get("General").addSetting("SFXVolume", "Range", 100, new Integer[]{ 0, 100 }, 100, true);
  }

  public void createDescriptions ()
  {
    descriptions = new HashMap <>();

    descriptions.put("-", new Context("Allgemein", " "));
    descriptions.put("Advanced", new Context("Erweiterte Einstellungen", "Hier können erweiterte Einstellungen vorgenommen werden"));

    descriptions.put("General", new Context("Allgemein", " "));
    descriptions.put("Version", new Context("Spiel Version", "Die aktuelle Version des Spiels"));
    descriptions.put("Branch", new Context("Branch", "The branch of the game"));
    descriptions.put("Debug", new Context("Debug Modus",
        """
            Diese Einstellung ist nur für Entwicklungszwecke.
            Erweitert die Anzeige um zusätzliche Informationen.
            Es stehen zusätzliche Einsteluungen zur Verfügung.
            Ein beitragen von Spielständen ist nicht möglich.
            Anmelden ist nicht möglich.
            """));
    descriptions.put("AccessLevel", new Context("Zugriffslevel", "Gibt den Modus des Spielautomaten an"));

    descriptions.put("Debugging", new Context("Debugging", " "));
    descriptions.put("Enabled", new Context("Aktiv", "Aktiviert oder deaktiviert dieser Einstellungsgruppe"));
    descriptions.put("Immortal", new Context("Unverwundbarkeit", "Aktiviert oder deaktiviert die Unverwundbarkeit"));
    descriptions.put("StartLives", new Context("Startleben", "Bestimmt die Anzahl der Leben am Anfang eines Spiels"));
    descriptions.put("StartSpeed", new Context("Startgeschwindigkeit", "Bestimmt die Geschwindigkeit am Anfang eines Spiels"));

    descriptions.put("Gameplay", new Context("Gameplay", " "));
    descriptions.put("GameSpecific", new Context("Spiele", " "));
    descriptions.put("PacMan", new Context("PacMan", " "));

    descriptions.put("Graphics", new Context("Grafik", " "));
    descriptions.put("Effects", new Context("Effekte", "Aktiviert oder deaktiviert die Effekte"));
    descriptions.put("Style", new Context("Stil", "Hier können die Stile der Grafikelemente vorgenommen werden"));
    descriptions.put("MenuSkin", new Context("Menü", "Bestimmt das Aussehen des Menüs"));
    descriptions.put("SeasonalSkins", new Context("Saisonal", "Aktiviert oder deaktiviert die saisonalen Skins"));
    descriptions.put("PlayerSkin", new Context("Spieler", "Bestimmt das Aussehen des Spielers"));
    descriptions.put("GhostSkin", new Context("Geister", "Bestimmt das Aussehen der Geister"));
    descriptions.put("ItemSkin", new Context("Gegenstände", "Bestimmt das Aussehen der Items"));
    descriptions.put("MapSkin", new Context("Karte", "Bestimmt das Aussehen der Karte"));
    descriptions.put("WallSkin", new Context("Wände", "Bestimmt das Aussehen der Wände"));

    descriptions.put("Audio", new Context("Audio", " "));
    descriptions.put("MasterVolume", new Context("Gesamtlautstärke", "Steuert die Lautstärke des gesamten Spiels"));
    descriptions.put("Music", new Context("Musik", "Aktiviert oder deaktiviert die Musik"));
    descriptions.put("MusicVolume", new Context("Musiklautstärke", "Steuert die Lautstärke der Musik"));
    descriptions.put("SFX", new Context("Soundeffekte", "Aktiviert oder deaktiviert die Soundeffekte"));
    descriptions.put("SFXVolume", new Context("Soundlautstärke", "Steuert die Lautstärke der Soundeffekte"));

  }

  // record for setting
  public record Setting <T>(String name, T type, T current, T possibleVal, T defaultVal) { }

  // tree consisting of setting groups and settings as leaves
  public record SettingTree(TreeMap <String, SettingTree> children, Setting <?> setting, int order)
  {
    @SuppressWarnings("unused")
    public boolean isLeaf () { return children.size() == 0; }

    public boolean hasSubGroups ()
    {

      AtomicBoolean hasSubGroups = new AtomicBoolean(false);
      children.forEach((s, settingTree) ->
      {
        if (settingTree.setting.type == "SubGroup") hasSubGroups.set(true);
      });

      return hasSubGroups.get();
    }

    public boolean isGroup ()
    {
      return setting.type == "SubGroup" || setting.type == "Group";
    }

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
          new SettingTree(new TreeMap <>(), new Setting <>(old.name, old.type, value, old.possibleVal, old.defaultVal), children.get(key).order));
    }

    public void addSubGroup (String name)
    {
      children.put(name, new SettingTree(new TreeMap <>(), new Setting <>(name, "SubGroup", null, null, null), children.size()));
    }

    public void addSetting (String name, Object type, Object current, Object possibleVal, Object defaultVal, boolean editable)
    {
      AtomicInteger order = new AtomicInteger();

      if (children.size() == 0) order.set(0);
      else children.values().stream()
                   .filter(settingTree -> settingTree.order != -1)
                   .max(Comparator.comparingInt(settingTree -> settingTree.order))
                   .ifPresent(settingTree -> order.set(settingTree.order + 1));

      if (!editable) order.set(-1);

      children.put(name, new SettingTree(new TreeMap <>(), new Setting <>(name, type, current, possibleVal, defaultVal), order.get()));
    }

    public void makeDebug ()
    {
      PacPhiConfig.getInstance().debugOnly.add(this);
    }

    public void makeNonEditable ()
    {
      PacPhiConfig.getInstance().nonEditable.add(this);
    }

    public List <String> getKeyList ()
    {
      List <String> keyList = new ArrayList <>(children.keySet());
      keyList.sort(Comparator.comparingInt(s -> children.get(s).order));
      return keyList;
    }
  }

  public record Context(String displayName, String info) { }

  record Range(int min, int max, int stepSize) { }
}

//example for reading the configs
//if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))