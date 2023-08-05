package kn.uni.ui.Swing.menus;

import kn.uni.Gui;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.components.PacTree;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Comparator;

public class SettingMenu extends UIScreen
{

  public static SettingMenu instance;

  public SettingMenu (JPanel parent)
  {
    super(parent);
    setLayout(null);
    setBackground(Color.BLACK);
    setVisible(true);

    addComponents();
  }

  public static SettingMenu getInstance (JPanel parent)
  {
    if (instance == null)
    {
      instance = new SettingMenu(parent);
    }
    return instance;
  }

  public void addComponents ()
  {
    Dimension titleSize = new Dimension(500, 60);
    PacLabel title = new PacLabel(
        new Vector2d().cartesian(Gui.frameWidth / 2. - titleSize.width / 2., 10),
        titleSize,
        ">>- Einstellungen -<<");
    title.setFont(title.getFont().deriveFont(40f));
    title.setBorder(null);
    title.useColorSet(Style.normal);
    title.setHorizontalAlignment(PacLabel.CENTER);
    add(title);

    PacTree tree = new PacTree(createTree(),
    new Vector2d().cartesian(10, 110),
    new Dimension(200, 700)
    );
    tree.setFont(tree.getFont().deriveFont(25f));

    add(tree);
  }

  private PacTree.Node createTree ()
  {
    //create a node tree using depth first search
    Config.Tree  tree = Config.getInstance().root;
    PacTree.Node root = new PacTree.Node("Settings");
    dfs(tree, root);

    return root;
  }

  private PacTree.Node dfs (Config.Tree current, PacTree.Node node)
  {
    //search for children sorted by ordinal
    current.getBranches().values().stream()
           .sorted(Comparator.comparingInt(o -> o.ordinal))
           .forEach(branch ->
           {
             node.add(dfs(branch, new PacTree.Node(branch.name)));
           });

    return node;
  }
}



