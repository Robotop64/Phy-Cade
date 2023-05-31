package kn.uni.util.fileRelated.Config;

import kn.uni.PacPhi;
import kn.uni.util.PrettyPrint;
import kn.uni.util.fileRelated.JsonEditor;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Config
{
  private static Config instance;
  private        Tree   root;

  private Config ()
  {
  }

  public static Config getInstance ()
  {
    if (instance == null)
    {
      instance = new Config();
    }
    return instance;
  }

  public static void init ()
  {
    Config.getInstance().createDef();
    PrettyPrint.bullet("Initiated default Config");
  }

  public static void load ()
  {
    Config local = new Config();
    local.root = (Tree) JsonEditor.load(new Tree(), "config"+ PacPhi.GAME_BRANCH);

    if (Config.compareSetting(Config.getInstance(), local,"General/-/Version"))
    {
      Config.getInstance().root = local.root;
      PrettyPrint.bullet("Local Config version is up to date");
      return;
    }

    PrettyPrint.bullet("Local Config version is expired/incompatible or unavailable");
    PrettyPrint.bullet("Using default Config");
  }

  public static void save ()
  {
    JsonEditor.save(Config.getInstance().root, "config"+ PacPhi.GAME_BRANCH);
  }

  public static void setCurrent (String root, Object value)
  {

    String[] path = root.split("/");
    Tree     tree = Config.getInstance().root;
    for (int i = 0; i < path.length - 1; i++)
    {
      tree = tree.getBranch(path[i]);
    }
    SettingType setting = tree.getLeaf(path[path.length - 1]).getType();

    if (setting instanceof Value v)
    {
      v.current = (String) value;
    }
    else if (setting instanceof Switch s)
    {
      s.current = (boolean) value;
    }
    else if (setting instanceof Digit d)
    {
      d.current = (double) value;
    }
    else if (setting instanceof Range r)
    {
      r.current = (double) value;
    }
  }

  public static Object getCurrent (String root)
  {
    String[] path = root.split("/");
    Tree     tree = Config.getInstance().root;
    for (int i = 0; i < path.length - 1; i++)
    {
      tree = tree.getBranch(path[i]);
    }
    SettingType setting = tree.getLeaf(path[path.length - 1]).getType();

    if (setting instanceof Value)
    {
      return setting.toValue().current;
    }
    else if (setting instanceof Switch)
    {
      return setting.toSwitch().current;
    }
    else if (setting instanceof Digit)
    {
      return setting.toDigit().current;
    }
    else if (setting instanceof Range)
    {
      return setting.toRange().current;
    }
    return null;
  }

  public void createDef ()
  {
    root = new Tree();

    //@formatter:off
    //general
    newSetting("General/-/Version",             new Value(PacPhi.GAME_VERSION, null, null), 0, new String[]{"visible"});
    newSetting("General/-/Branch",              new Value(PacPhi.GAME_BRANCH, "STABLE", new String[]{ "STABLE", "UNSTABLE", "ENTROPIC" }), 1, new String[]{"visible", "editable"});
    newSetting("General/-/AccessLevel",         new Value("User", "User", new String[]{ "User", "Developer" }), 2, new String[]{"visible"});

    //debugging
    newSetting("Debugging/-/Enabled",           new Switch(false, false), 0, new String[]{"visible", "editable"});
    newSetting("Debugging/-/Immortal",          new Switch(false, false), 1, new String[]{"visible", "editable", "debug"});

    //gameplay
    newSetting("Gameplay/PacMan/StartLives",    new Digit(5, 5, null), 0, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/StartSpeed",    new Digit(6, 6, null), 1, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/PlayerHP",      new Digit(1, 1, null), 2, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/GhostHP",       new Digit(1, 1, null), 3, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/PointsToLife",  new Digit(10000, 10000, null), 4, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/PortalDelay",   new Digit(0.01, 0.01, null), 5, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/PortalCooldown",new Digit(1, 1, null), 6, new String[]{"visible", "editable", "debug"});

    //graphics
    newSetting("Graphics/General/Effects",      new Switch(true, true), 0, new String[]{"visible", "editable"});

    newSetting("Graphics/Advanced/Antialiasing",new Switch(true, true), 1, new String[]{"visible", "editable"});

    //music
    newSetting("Audio/General/Enabled",         new Switch(true, true), 0, new String[]{"visible", "editable"});
    newSetting("Audio/General/MasterVolume",    new Range(100, 0, 100, 5), 1, new String[]{"visible", "editable"});
    newSetting("Audio/General/Music",           new Switch(true, true), 2, new String[]{"visible", "editable"});
    newSetting("Audio/General/MusicVolume",     new Range(100, 0, 100, 5), 3, new String[]{"visible", "editable"});
    newSetting("Audio/General/SFX",             new Switch(true, true), 4, new String[]{"visible", "editable"});
    newSetting("Audio/General/SFXVolume",       new Range(100, 0, 100, 5), 5, new String[]{"visible", "editable"});
    //@formatter:on
  }

  public SettingType getSetting (String root)
  {
    String[] path = root.split("/");
    Tree     tree = this.root;
    for (int i = 0; i < path.length - 1; i++)
    {
      tree = tree.getBranch(path[i]);
    }
    return tree.getLeaf(path[path.length - 1]).getType();
  }

  private void newSetting (String root, SettingType setting, int ordinal, String[] tags)
  {
    String[] path = root.split("/");
    Tree     tree = this.root;

    for (int i = 0; i < path.length - 1; i++)
    {
      Tree current = tree;
      tree = tree.getBranch(path[i]);

      if (tree == null)
      {
        current.addBranch(path[i]);
        tree = current.getBranch(path[i]);
      }
    }
    tree.addLeaf(path[path.length - 1], new Leaf(ordinal, setting, tags));
  }

  private static boolean compareSetting (Config a, Config b, String root)
  {
    SettingType settA = a.getSetting(root);

    if (b.root.branches.isEmpty())
      return false;

    SettingType settB = b.getSetting(root);

    return settA.equals(settB);
  }

  //region tree classes
  public static class Tree
  {
    TreeMap <String, Tree> branches;
    Map <String, Leaf>     leaves;

    public Tree ()
    {
      branches = new TreeMap <>();
      leaves = new HashMap <>();
    }

    public Tree getBranch (String name)
    {
      return branches.get(name);
    }

    public Map <String, Tree> getBranches ()
    {
      return branches;
    }

    public Leaf getLeaf (String name)
    {
      return leaves.get(name);
    }

    public Map <String, Leaf> getLeaves ()
    {
      return leaves;
    }

    public void addBranch (String name)
    {
      branches.put(name, new Tree());
    }

    public void addLeaf (String name, Leaf leaf)
    {
      leaves.put(name, leaf);
    }

  }

  public class Leaf
  {
    Range  range;
    Switch toggle;
    Value  value;
    Digit  digit;

    String[] tags;
    int ordinal;

    public Leaf (int ordinal, SettingType type, String[] tags)
    {
      if (type instanceof Range)
        range = (Range) type;
      else if (type instanceof Switch)
        toggle = (Switch) type;
      else if (type instanceof Value)
        value = (Value) type;
      else if (type instanceof Digit)
        digit = (Digit) type;

      this.tags = tags;
      this.ordinal = ordinal;
    }

    public boolean hasTag (String tag)
    {
      for (String t : tags)
      {
        if (t.equals(tag))
          return true;
      }
      return false;
    }

    public SettingType getType ()
    {
      if (range != null)
        return range;
      else if (toggle != null)
        return toggle;
      else if (value != null)
        return value;
      else if (digit != null)
        return digit;
      return null;
    }
  }

  //endregion

  //region setting types classes
  public class SettingType
  {
    public Range toRange ()
    {
      return (Range) this;
    }

    public Switch toSwitch ()
    {
      return (Switch) this;
    }

    public Value toValue ()
    {
      return (Value) this;
    }

    public Digit toDigit ()
    {
      return (Digit) this;
    }

    public Group toGroup ()
    {
      return (Group) this;
    }

    public boolean equals(SettingType other)
    {
      if (this instanceof Range)
      {
        Range a = (Range) this;
        Range b = other.toRange();
        return a.current == b.current;
      }
      else if (this instanceof Switch)
      {
        Switch a = (Switch) this;
        Switch b = other.toSwitch();
        return a.current == b.current;
      }
      else if (this instanceof Value)
      {
        Value a = (Value) this;
        Value b = other.toValue();
        return a.current.equals(b.current);
      }
      else if (this instanceof Digit)
      {
        Digit a = (Digit) this;
        Digit b = other.toDigit();
        return a.current == b.current;
      }
      else if (this instanceof Group)
      {
        return other instanceof Group;
      }
      return false;
    }
  }

  public class Range extends SettingType
  {
    double current;
    double min;
    double max;
    double stepSize;

    public Range (double current, double min, double max, double stepSize)
    {
      this.current = current;
      this.min = min;
      this.max = max;
      this.stepSize = stepSize;
    }
  }

  public class Switch extends SettingType
  {
    boolean current;
    boolean defaultVal;

    public Switch (boolean current, boolean defaultVal)
    {
      this.current = current;
      this.defaultVal = defaultVal;
    }
  }

  public class Value extends SettingType
  {
    String   current;
    String   defaultVal;
    String[] possible;

    public Value (String current, String defaultVal, String[] possible)
    {
      this.current = current;
      this.defaultVal = defaultVal;
      this.possible = possible;
    }
  }

  public class Digit extends SettingType
  {
    double   current;
    double   defaultVal;
    double[] possible;

    public Digit (double current, double defaultVal, double[] possible)
    {
      this.current = current;
      this.defaultVal = defaultVal;
      this.possible = possible;
    }
  }

  public class Group extends SettingType
  {
    int ordinal;

    public Group (int ordinal)
    {
      this.ordinal = ordinal;
    }
  }
  //endregion
}
