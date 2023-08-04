package kn.uni.ui.Swing.menus;

import kn.uni.Gui;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
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
    setBackground(Color.black);
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


    UIDefaults defaults = new UIDefaults();

    JTree tree = new JTree(createTree());

    //    FlatTree tree = new FlatTree();
    //    tree.add(new JTree(createTree()));
    //    System.out.println(tree.getRowCount());
    //    IntStream.range(0, 3).forEach(tree::expandRow);
    //    tree.setRootVisible(false);

    Dimension   treeSize      = new Dimension(200, 700);
    JScrollPane treeContainer = new JScrollPane(tree);
    treeContainer.setLocation(10, 110);
    treeContainer.setSize(treeSize);
    treeContainer.setBorder(new LineBorder(Color.BLUE));
    add(treeContainer);
  }

  private TreeNode createTree ()
  {
    //create a node tree using depth first search
    Config.Tree tree = Config.getInstance().root;

    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    root.add(dfs(tree, root));
    return dfs(tree, root);
  }

  private DefaultMutableTreeNode dfs (Config.Tree current, DefaultMutableTreeNode node)
  {
    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(current.name);
    node.add(newNode);

    //search for children sorted by ordinal
    current.getBranches().values().stream()
           .sorted(Comparator.comparingInt(o -> o.ordinal))
           .forEach(branch -> dfs(branch, newNode));

    return newNode;
  }
}



