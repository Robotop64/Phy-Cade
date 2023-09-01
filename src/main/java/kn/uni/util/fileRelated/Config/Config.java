package kn.uni.util.fileRelated.Config;

import kn.uni.PacPhi;
import kn.uni.util.PrettyPrint;
import kn.uni.util.fileRelated.JsonEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Config
{
  private static Config instance;
  public         Tree   root;
  private       int               lastId      = 0;
  public ArrayList<String> optionNames = new ArrayList<>();
  public ArrayList<Leaf> optionLeaves = new ArrayList<>();

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
    local.root = (Tree) JsonEditor.load(new Tree("root", 0), "config" + PacPhi.GAME_BRANCH);

    if (Config.compareSetting(Config.getInstance(), local, "General/Version"))
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
    JsonEditor.save(Config.getInstance().root, "config" + PacPhi.GAME_BRANCH);
  }

  public static void setCurrent (String root, Object value)
  {

    String[] path = root.split("/");
    Tree     tree = Config.getInstance().root;
    for (int i = 0; i < path.length - 1; i++)
    {
      tree = tree.getBranch(path[i]);
    }
    Setting setting = tree.getLeaf(path[path.length - 1]).getType();

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
    else if (setting instanceof Table t)
    {
      t.current = (String[][]) value;
    }
    else if (setting instanceof Matrix m)
    {
      m.current = (double[][]) value;
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
    Setting setting = tree.getLeaf(path[path.length - 1]).getType();

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
    else if (setting instanceof Table)
    {
      return setting.toTable().current;
    }
    else if (setting instanceof Matrix)
    {
      return setting.toMatrix().current;
    }
    return null;
  }

  public void createDef ()
  {
    root = new Tree("root", 0);

    //@formatter:off
    //general
    newSetting("General/Version",             new Value(PacPhi.GAME_VERSION,  new String[]{ "???" }), 0, new String[]{"visible"});
    newSetting("General/Branch",              new Value( "STABLE", new String[]{ "STABLE", "UNSTABLE", "ENTROPIC" }), 1, new String[]{"visible", "editable"});
    newSetting("General/AccessLevel",         new Value( "User", new String[]{ "User", "Developer" }), 2, new String[]{"visible"});

    //debugging
    newSetting("Debugging/Enabled",           new Switch( false), 0, new String[]{"visible", "editable"});
    newSetting("Debugging/Immortal",          new Switch( false), 1, new String[]{"visible", "editable", "debug"});
    newSetting("Debugging/ColorFloor",        new Switch( false), 2, new String[]{"visible", "editable", "debug"});

    //gameplay
    newSetting("Gameplay/PacMan/StartLives",    new Digit( 5, null), 0, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/StartSpeed",    new Digit( 6, null), 1, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/PlayerHP",      new Digit( 1, null), 2, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/GhostHP",       new Digit( 1, null), 3, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/PointsToLife",  new Digit( 10000, null), 4, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/PortalDelay",   new Digit( 0.01, null), 5, new String[]{"visible", "editable", "debug"});
    newSetting("Gameplay/PacMan/PortalCooldown",new Digit( 1, null), 6, new String[]{"visible", "editable", "debug"});

    //pacman speed scaling while ghost are normal
    newSetting("Gameplay/PacMan/PacSpeedScalingNormal",
        new Matrix(new String[]{ "Level", "Speed" },
        new double[][]{              { 1, 0.80 },
                                                { 2, 0.90 },
                                                { 5, 1.00 },
                                                { 21, 0.90 }}), 7, new String[]{"visible", "editable", "debug"});

    //pacman speed scaling while ghost are frightened
    newSetting("Gameplay/PacMan/PacSpeedScalingFrightened",
        new Matrix(new String[]{ "Level", "Speed" },
            new double[][]{   { 1, 0.90 },
                                         { 2, 0.95 },
                                         { 5, 1.00 },
                                         { 21, 0.90 }}), 8, new String[]{"visible", "editable", "debug"});

    //ghost speed scaling while normal
    newSetting("Gameplay/PacMan/GhostSpeedScalingNormal",
        new Matrix(new String[]{ "Level", "Speed" },
        new double[][]{              { 1, 0.75 },
                                                { 2, 0.85 },
                                                { 5, 0.95 },
                                                { 21, 0.95 }}), 9, new String[]{"visible", "editable", "debug"});

    //ghost speed scaling while frightened
    newSetting("Gameplay/PacMan/GhostSpeedScalingFrightened",
        new Matrix(new String[]{ "Level", "Speed" },
            new double[][]{   { 1, 0.50 },
                                         { 2, 0.55 },
                                         { 5, 0.60 },
                                         { 21, 0.60 }}), 10, new String[]{"visible", "editable", "debug"});

    newSetting("Gameplay/PacMan/GhostScatterPlan",
        new Table(
        new Object[]{"Mode↓/Level→",1,2,5},
        new Object[][]{             {"Scatter",7,7,5},
                                               {"Chase",20,20,20},
                                               {"Scatter",7,7,5},
                                               {"Chase",20,20,20},
                                               {"Scatter",5,5,5},
                                               {"Chase",20,1033,1037},
                                               {"Scatter",5,0,0},
                                               {"Chase",-1,-1,-1}}), 11, new String[]{"visible", "editable", "debug"});

    newSetting("Gameplay/PacMan/GhostFrightenDuration",   new Matrix(
        new String[]{ "Level", "Duration" },
        new double[][]{ { 1, 10 },{ 5, 7.5 },{ 10, 5 },{ 15, 2.5 },{20, 0 },}), 12, new String[]{"visible", "editable", "debug"});

    //graphics
    newSetting("Graphics/General/Effects",      new Switch( true), 0, new String[]{"visible", "editable"});

    newSetting("Graphics/Advanced/Antialiasing",new Switch( true), 1, new String[]{"visible", "editable"});

    //music
    newSetting("Audio/Enabled",         new Switch( true), 0, new String[]{"visible", "editable"});
    newSetting("Audio/MasterVolume",    new Range(100, 0, 100, 5), 1, new String[]{"visible", "editable"});
    newSetting("Audio/Music",           new Switch( true), 2, new String[]{"visible", "editable"});
    newSetting("Audio/MusicVolume",     new Range(100, 0, 100, 5), 3, new String[]{"visible", "editable"});
    newSetting("Audio/SFX",             new Switch( true), 4, new String[]{"visible", "editable"});
    newSetting("Audio/SFXVolume",       new Range(100, 0, 100, 5), 5, new String[]{"visible", "editable"});
    //@formatter:on
  }

  public Setting getSetting (String root)
  {
    String[] path = root.split("/");
    Tree     tree = this.root;
    for (int i = 0; i < path.length - 1; i++)
    {
      tree = tree.getBranch(path[i]);
    }
    return tree.getLeaf(path[path.length - 1]).getType();
  }

  private void newSetting (String root, Setting setting, int ordinal, String[] tags)
  {
    String[] path = root.split("/");
    Tree     tree = this.root;

    for (int i = 0; i < path.length - 1; i++)
    {
      Tree current = tree;
      tree = tree.getBranch(path[i]);

      if (tree == null)
      {
        int lastOrdinal = current.getBranches().values().stream().mapToInt(Tree::getOrdinal).max().orElse(0);
        current.addBranch(path[i], lastOrdinal + 1);
        tree = current.getBranch(path[i]);
      }
    }
    setting.id = lastId++;
    Leaf leaf = new Leaf(ordinal, setting, tags);
    tree.addLeaf(path[path.length - 1], leaf);
    optionNames.add(path[path.length - 1]);
    optionLeaves.add(leaf);
  }

  private static boolean compareSetting (Config a, Config b, String root)
  {
    Setting settA = a.getSetting(root);

    if (b.root.branches.isEmpty())
      return false;

    Setting settB = b.getSetting(root);

    return settA.hasSameType(settB);
  }

  //region tree classes
  public static class Tree
  {
    TreeMap <String, Tree> branches;
    Map <String, Leaf>     leaves;
    public String name;
    public int    ordinal;

    public Tree (String name, int ordinal)
    {
      branches = new TreeMap <>();
      leaves = new HashMap <>();
      this.name = name;
      this.ordinal = ordinal;
    }

    public Tree getBranch (String name)
    {
      return branches.get(name);
    }

    public Tree getBranchedTree(String path)
    {
      String[] split = path.split("/");
      Tree tree = this;
      for (int i = 0; i < split.length - 1; i++)
      {
        tree = tree.getBranch(split[i]);
      }
      return tree.getBranch(split[split.length - 1]);
    }

    public Map <String, Tree> getBranches ()
    {
      return branches;
    }

    public Leaf getLeaf (String name)
    {
      return leaves.get(name);
    }

    public Leaf getBranchedLeaf(String path)
    {
      String[] split = path.split("/");
      Tree tree = this;
      for (int i = 0; i < split.length - 1; i++)
      {
        tree = tree.getBranch(split[i]);
      }
      return tree.getLeaf(split[split.length - 1]);
    }

    public Map <String, Leaf> getLeaves ()
    {
      return leaves;
    }

    public void addBranch (String name, int ordinal)
    {
      branches.put(name, new Tree(name, ordinal));
    }

    public void addLeaf (String name, Leaf leaf)
    {
      leaves.put(name, leaf);
    }

    public int getOrdinal ()
    {
      return ordinal;
    }
  }

  public static class Leaf
  {
    Range  range;
    Switch toggle;
    Value  value;
    Digit  digit;
    Table  table;
    Matrix matrix;

    public Setting.SubClass subType;
    public String[]         tags;
    public int              ordinal;

    public Leaf (int ordinal, Setting type, String[] tags)
    {
      subType = Setting.SubClass.valueOf(type.asSubClass().getSimpleName());

      switch (subType)
      {
        case Range -> range = type.toRange();
        case Switch -> toggle = type.toSwitch();
        case Value -> value = type.toValue();
        case Digit -> digit = type.toDigit();
        case Table -> table = type.toTable();
        case Matrix -> matrix = type.toMatrix();
        default ->
        {
          throw new RuntimeException("unknown setting type");
        }
      }

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

    public Setting getType ()
    {
      return switch (subType)
      {
        case Range -> range;
        case Switch -> toggle;
        case Value -> value;
        case Digit -> digit;
        case Table -> table;
        case Matrix -> matrix;
        default -> null;
      };
    }
  }
  //endregion

  //region setting types classes
  public static class Setting
  {
    public int id;
    public enum SubClass
    {
      Range, Switch, Value, Digit, Table, Matrix, Group
    }

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

    public Table toTable ()
    {
      return (Table) this;
    }

    public Matrix toMatrix ()
    {
      return (Matrix) this;
    }

    public Group toGroup ()
    {
      return (Group) this;
    }

    public boolean hasSameType (Setting other)
    {
      Set <Class <?>> classTypes = new HashSet <>();
      classTypes.add(this.getClass());
      classTypes.add(other.getClass());
      return classTypes.size() == 1;
    }

    public Class <? extends Setting> asSubClass ()
    {
      return this.getClass().asSubclass(Setting.class);
    }
  }

  public static class Range extends Setting
  {
    public double current;
    public double defaultVal;
    public double min;
    public double max;
    public double stepSize;

    public Range (double defaultVal, double min, double max, double stepSize)
    {
      this.current = defaultVal;
      this.defaultVal = defaultVal;
      this.min = min;
      this.max = max;
      this.stepSize = stepSize;
    }
  }

  public static class Switch extends Setting
  {
    public boolean current;
    public boolean defaultVal;

    public Switch (boolean defaultVal)
    {
      this.current = defaultVal;
      this.defaultVal = defaultVal;
    }
  }

  public static class Value extends Setting
  {
    public String current;
    public String   defaultVal;
    public String[] possible;

    public Value (String defaultVal, String[] possible)
    {
      this.current = defaultVal;
      this.defaultVal = defaultVal;
      this.possible = possible;
    }
  }

  public static class Digit extends Setting
  {
    public double current;
    public double   defaultVal;
    public double[] possible;

    public Digit (double defaultVal, double[] possible)
    {
      this.current = defaultVal;
      this.defaultVal = defaultVal;
      this.possible = possible;
    }
  }

  public static class Table extends Setting
  {
    Object[]   headers;
    Object[][] current;
    Object[][] defaultVal;

    public Table (Object[] headers, Object[][] defaultVal)
    {
      this.headers = headers;
      this.current = defaultVal;
      this.defaultVal = defaultVal;
    }
  }

  public static class Matrix extends Setting
  {
    String[]   headers;
    double[][] current;
    double[][] defaultVal;

    public Matrix (String[] headers, double[][] defaultVal)
    {
      this.headers = headers;
      this.current = defaultVal;
      this.defaultVal = defaultVal;
    }
  }

  public static class Group extends Setting
  {
    int ordinal;

    public Group (int ordinal)
    {
      this.ordinal = ordinal;
    }
  }
  //endregion
}
