//package kn.uni.util.fileRelated;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.TreeMap;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//
//
//public class PacPhiConfig
//{
//  //make singleton
//  private static PacPhiConfig              instance;
//  public         List <SettingTree>        nonEditable = new ArrayList <>();
//  public         List <SettingTree>        debugOnly   = new ArrayList <>();
//  public         SettingTree               settings;
//  public         HashMap <String, Context> descriptions;
//
//  public PacPhiConfig ()
//  {
//  }
//
//  public static PacPhiConfig getInstance ()
//  {
//    if (instance == null) instance = new PacPhiConfig();
//    return instance;
//  }
//
//  public static Object getContent (String group, String subGroup, String setting)
//  {
//    return PacPhiConfig.getInstance().settings.get(group).get(subGroup).get(setting).setting().content;
//  }
//
//  public static void setCurrent (String group, String subGroup, String setting, Object value)
//  {
//    PacPhiConfig.getInstance().settings.get(group).get(subGroup).set(setting, value);
//  }
//
//  public static void load ()
//  {
//    //create defaults
//    PacPhiConfig.getInstance().init();
//    //load settings
//    PacPhiConfig.getInstance().settings = (SettingTree) JsonEditor.load(PacPhiConfig.getInstance().settings, "settings");
//  }
//
//  public static void save ()
//  {
//
//    JsonEditor.save(PacPhiConfig.getInstance().settings, "settings");
//  }
//
//  public Setting getSetting (String group, String subGroup, String setting)
//  {
//    return PacPhiConfig.getInstance().settings.get(group).get(subGroup).get(setting).setting();
//  }
//
//  public void init ()
//  {
//    createDefaultSettings();
//    createDescriptions();
//  }
//
//  public void createDefaultSettings ()
//  {
//    //trunk
//    settings = new SettingTree(new TreeMap <>(), new Setting("Settings", new Group(0)), 0);
//
//    //General branch
//    settings.addSubGroup("General");
//
//    settings.get("General").addSubGroup("-");
//    settings.get("General").get("-").addSetting("Version", new Value(null, null, null), true, false, false);
//    settings.get("General").get("-").addSetting("Branch", new Value("ENTROPIC", "STABLE", new String[]{ "STABLE", "UNSTABLE" }), true, true, false);
//    settings.get("General").get("-").addSetting("AccessLevel", new Value("User", "User", new String[]{ "User", "Developer" }), false, false, false);
//
//
//    //Debugging branch
//    settings.addSubGroup("Debugging");
//
//    settings.get("Debugging").addSubGroup("-");
//    settings.get("Debugging").get("-").addSetting("Enabled", new Switch(false, false), true, true, false);
//
//    settings.get("Debugging").addSubGroup("Graphics");
//
//    settings.get("Debugging").addSubGroup("Gameplay");
//    settings.get("Debugging").get("Gameplay").addSetting("Immortal", new Switch(false, false), true, true, true);
//
//
//    //Gameplay branch
//    settings.addSubGroup("Gameplay");
//
//    settings.get("Gameplay").addSubGroup("-");
//
//    settings.get("Gameplay").addSubGroup("PacMan");
//    settings.get("Gameplay").get("PacMan").addSetting("StartLives", new Digit(5, 5, null), true, true, true);
//    settings.get("Gameplay").get("PacMan").addSetting("StartSpeed", new Digit(6, 6, null), true, true, true);
//    settings.get("Gameplay").get("PacMan").addSetting("PlayerHP", new Digit(1, 1, null), true, true, true);
//    settings.get("Gameplay").get("PacMan").addSetting("GhostHP", new Digit(1, 1, null), true, true, true);
//    settings.get("Gameplay").get("PacMan").addSetting("PointsToLife", new Digit(10000, 10000, null), true, true, true);
//    settings.get("Gameplay").get("PacMan").addSetting("PortalDelay", new Digit(0.01, 0.01, null), true, true, true);
//    settings.get("Gameplay").get("PacMan").addSetting("PortalCooldown", new Digit(1, 1, null), true, true, true);
//
//
//    //Graphics branch
//    settings.addSubGroup("Graphics");
//    settings.get("Graphics").addSubGroup("General");
//    settings.get("Graphics").get("General").addSetting("Effects", new Switch(true, true), true, true, false);
//
//    settings.get("Graphics").addSubGroup("Advanced");
//    settings.get("Graphics").get("Advanced").addSetting("Antialiasing", new Switch(false, false), true, true, false);
//
//    settings.get("Graphics").addSubGroup("Style");
//    settings.get("Graphics").get("Style").addSetting("MenuSkin", new Value("Classic", "Classic", new String[]{ "Classic" }), true, true, false);
//    settings.get("Graphics").get("Style").addSetting("SeasonalSkins", new Switch(true, true), true, true, false);
//    settings.get("Graphics").get("Style").addSetting("PlayerSkin", new Value("Classic", "Classic", new String[]{ "Classic" }), true, true, false);
//    settings.get("Graphics").get("Style").addSetting("GhostSkin", new Value("Classic", "Classic", new String[]{ "Classic" }), true, true, false);
//    settings.get("Graphics").get("Style").addSetting("ItemSkin", new Value("Classic", "Classic", new String[]{ "Classic" }), true, true, false);
//    settings.get("Graphics").get("Style").addSetting("MapSkin", new Value("Classic", "Classic", new String[]{ "Classic" }), true, true, false);
//    settings.get("Graphics").get("Style").addSetting("WallSkin", new Value("Classic", "Classic", new String[]{ "Classic" }), true, true, false);
//
//    //Audio branch
//    settings.addSubGroup("Audio");
//    settings.get("Audio").addSubGroup("General");
//    settings.get("Audio").get("General").addSetting("Enabled", new Switch(true, true), true, true, false);
//    settings.get("Audio").get("General").addSetting("MasterVolume", new Range(100, 0, 100, 5), true, true, false);
//    settings.get("Audio").get("General").addSetting("Music", new Switch(true, true), true, true, false);
//    settings.get("Audio").get("General").addSetting("MusicVolume", new Range(100, 0, 100, 5), true, true, false);
//    settings.get("Audio").get("General").addSetting("SFX", new Switch(true, true), true, true, false);
//    settings.get("Audio").get("General").addSetting("SFXVolume", new Range(100, 0, 100, 5), true, true, false);
//  }
//
//  public void createDescriptions ()
//  {
//    descriptions = new HashMap <>();
//
//    descriptions.put("-", new Context("Allgemein", " "));
//    descriptions.put("Advanced", new Context("Erweiterte Einstellungen", "Hier können erweiterte Einstellungen vorgenommen werden"));
//
//    descriptions.put("General", new Context("Allgemein", " "));
//    descriptions.put("Version", new Context("Spiel Version", "Die aktuelle Version des Spiels"));
//    descriptions.put("Branch", new Context("Branch", "The branch of the game"));
//    descriptions.put("Debug", new Context("Debug Modus",
//        """
//            Diese Einstellung ist nur für Entwicklungszwecke.
//            Erweitert die Anzeige um zusätzliche Informationen.
//            Es stehen zusätzliche Einsteluungen zur Verfügung.
//            Ein beitragen von Spielständen ist nicht möglich.
//            Anmelden ist nicht möglich.
//            """));
//    descriptions.put("AccessLevel", new Context("Zugriffslevel", "Gibt den Modus des Spielautomaten an"));
//
//    descriptions.put("Debugging", new Context("Debugging", " "));
//    descriptions.put("Enabled", new Context("Aktiv", "Aktiviert oder deaktiviert dieser Einstellungsgruppe"));
//    descriptions.put("Immortal", new Context("Unverwundbarkeit", "Aktiviert oder deaktiviert die Unverwundbarkeit"));
//    descriptions.put("StartLives", new Context("Startleben", "Bestimmt die Anzahl der Leben am Anfang eines Spiels"));
//    descriptions.put("StartSpeed", new Context("Startgeschwindigkeit", "Bestimmt die Geschwindigkeit am Anfang eines Spiels"));
//
//    descriptions.put("Gameplay", new Context("Gameplay", " "));
//    descriptions.put("GameSpecific", new Context("Spiele", " "));
//    descriptions.put("PacMan", new Context("PacMan", " "));
//
//    descriptions.put("Graphics", new Context("Grafik", " "));
//    descriptions.put("Effects", new Context("Effekte", "Aktiviert oder deaktiviert die Effekte"));
//    descriptions.put("Style", new Context("Stil", "Hier können die Stile der Grafikelemente vorgenommen werden"));
//    descriptions.put("MenuSkin", new Context("Menü", "Bestimmt das Aussehen des Menüs"));
//    descriptions.put("SeasonalSkins", new Context("Saisonal", "Aktiviert oder deaktiviert die saisonalen Skins"));
//    descriptions.put("PlayerSkin", new Context("Spieler", "Bestimmt das Aussehen des Spielers"));
//    descriptions.put("GhostSkin", new Context("Geister", "Bestimmt das Aussehen der Geister"));
//    descriptions.put("ItemSkin", new Context("Gegenstände", "Bestimmt das Aussehen der Items"));
//    descriptions.put("MapSkin", new Context("Karte", "Bestimmt das Aussehen der Karte"));
//    descriptions.put("WallSkin", new Context("Wände", "Bestimmt das Aussehen der Wände"));
//
//    descriptions.put("Audio", new Context("Audio", " "));
//    descriptions.put("MasterVolume", new Context("Gesamtlautstärke", "Steuert die Lautstärke des gesamten Spiels"));
//    descriptions.put("Music", new Context("Musik", "Aktiviert oder deaktiviert die Musik"));
//    descriptions.put("MusicVolume", new Context("Musiklautstärke", "Steuert die Lautstärke der Musik"));
//    descriptions.put("SFX", new Context("Soundeffekte", "Aktiviert oder deaktiviert die Soundeffekte"));
//    descriptions.put("SFXVolume", new Context("Soundlautstärke", "Steuert die Lautstärke der Soundeffekte"));
//
//  }
//
//  // record for setting
//  public record Setting <T>(String name, T content) { }
//
//  // tree consisting of setting groups and settings as leaves
//  public record SettingTree(TreeMap <String, SettingTree> children, Setting <?> setting, int order)
//  {
//    @SuppressWarnings("unused")
//    public boolean isLeaf () { return children.size() == 0; }
//
//    public boolean hasSubGroups ()
//    {
//
//      AtomicBoolean hasSubGroups = new AtomicBoolean(false);
//      children.forEach((s, settingTree) ->
//      {
//        if (!( settingTree.setting.content instanceof Group )) hasSubGroups.set(true);
//      });
//
//      return hasSubGroups.get();
//    }
//
//    public boolean isGroup ()
//    {
//      return setting.content instanceof Group;
//    }
//
//    public SettingTree get (String key)
//    {
//      return children.get(key);
//    }
//
//    public void add (String key, SettingTree value) { children.put(key, value); }
//
//    public void set (String key, Object value)
//    {
//      Setting <?> old = children.get(key).setting;
//
//      if (old.content instanceof Group)
//        return;
//
//      //      System.out.println(children.get(key).setting().content());
//
//      Object newContent = null;
//
//      if (old.content instanceof Range o)
//      {
//        newContent = new Range((Double) value, o.min, o.max, o.stepSize);
//      }
//
//      else if (old.content instanceof Switch o)
//      {
//        newContent = new Switch((Boolean) value, o.defaultVal);
//      }
//
//      else if (old.content instanceof Value o)
//      {
//        newContent = new Value((String) value, o.defaultVal, o.possible);
//      }
//
//      else if (old.content instanceof Digit o)
//      {
//        newContent = new Digit((Double) value, o.defaultVal, o.possible);
//      }
//
//
//      children.replace(
//          key,
//          new SettingTree(new TreeMap <>(), new Setting <>(old.name, newContent), children.get(key).order));
//    }
//
//    private void addSubGroup (String name)
//    {
//      children.put(name, new SettingTree(new TreeMap <>(), new Setting <>(name, new Group(1)), children.size()));
//    }
//
//    private void addSetting (String name, Object content, boolean visible, boolean editable, boolean debug)
//    {
//      AtomicInteger order = new AtomicInteger();
//
//      if (children.size() == 0) order.set(0);
//      else children.values().stream()
//                   .filter(settingTree -> settingTree.order != -1)
//                   .max(Comparator.comparingInt(settingTree -> settingTree.order))
//                   .ifPresent(settingTree -> order.set(settingTree.order + 1));
//
//      if (!visible) order.set(-1);
//      if (!editable) PacPhiConfig.getInstance().nonEditable.add(this);
//      if (debug) PacPhiConfig.getInstance().debugOnly.add(this);
//
//      children.put(name, new SettingTree(new TreeMap <>(), new Setting <>(name, content), order.get()));
//    }
//
//    private void makeDebug ()
//    {
//      PacPhiConfig.getInstance().debugOnly.add(this);
//    }
//
//    private void makeNonEditable ()
//    {
//      PacPhiConfig.getInstance().nonEditable.add(this);
//    }
//
//    public List <String> getKeyList ()
//    {
//      List <String> keyList = new ArrayList <>(children.keySet());
//      keyList.sort(Comparator.comparingInt(s -> children.get(s).order));
//      return keyList;
//    }
//  }
//
//  public record Context(String displayName, String info) { }
//
//  //region setting types
//  public record Range(double current, double min, double max, double stepSize) { }
//
//  public record Switch(boolean current, boolean defaultVal) { }
//
//  public record Value(String current, String defaultVal, String[] possible) { }
//
//  public record Digit(double current, double defaultVal, double[] possible) { }
//
//  public record Group(int ordinal) { }
//  //endregion
//}
//
////region setting types
////  class SettingType
////  {
////    public Range toRange ()
////    {
////      return (Range) this;
////    }
////
////    public Switch toSwitch ()
////    {
////      return (Switch) this;
////    }
////
////    public Value toValue ()
////    {
////      return (Value) this;
////    }
////
////    public Digit toDigit ()
////    {
////      return (Digit) this;
////    }
////
////    public Group toGroup ()
////    {
////      return (Group) this;
////    }
////  }
////
////  public class Range extends SettingType
////  {
////    double current;
////    double min;
////    double max;
////    double stepSize;
////
////    public Range (double current, double min, double max, double stepSize)
////    {
////      this.current = current;
////      this.min = min;
////      this.max = max;
////      this.stepSize = stepSize;
////    }
////  }
////
////  public class Switch extends SettingType
////  {
////    boolean current;
////    boolean defaultVal;
////
////    public Switch (boolean current, boolean defaultVal)
////    {
////      this.current = current;
////      this.defaultVal = defaultVal;
////    }
////  }
////
////  public class Value extends SettingType
////  {
////    String   current;
////    String   defaultVal;
////    String[] possible;
////
////    public Value (String current, String defaultVal, String[] possible)
////    {
////      this.current = current;
////      this.defaultVal = defaultVal;
////      this.possible = possible;
////    }
////  }
////
////  public class Digit extends SettingType
////  {
////    double   current;
////    double   defaultVal;
////    double[] possible;
////
////    public Digit (double current, double defaultVal, double[] possible)
////    {
////      this.current = current;
////      this.defaultVal = defaultVal;
////      this.possible = possible;
////    }
////  }
////
////  public class Group extends SettingType
////  {
////    int ordinal;
////
////    public Group (int ordinal)
////    {
////      this.ordinal = ordinal;
////    }
////  }
////endregion
//
//
////example for reading the configs
////if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))