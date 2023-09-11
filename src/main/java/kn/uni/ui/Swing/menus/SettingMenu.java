package kn.uni.ui.Swing.menus;

import kn.uni.Gui;
import kn.uni.ui.InputListener;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.components.PacTree;
import kn.uni.ui.Swing.components.RoundedPanel;
import kn.uni.ui.Swing.components.SettingEditor;
import kn.uni.ui.Swing.interfaces.Controllable;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Comparator;

public class SettingMenu extends UIScreen implements Controllable
{

  public static SettingMenu instance;
  Config.Tree currentSettingGroup;
  public JPanel      editors;
  public JScrollPane scroll;

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
    int buffer     = 20;
    int lineHeight = 60;
    int lineWeight = 3;
    int widgetArc  = 30;

    //region title
    Dimension titleSize = new Dimension(500, lineHeight);
    PacLabel title = new PacLabel(
        new Vector2d().cartesian(Gui.frameWidth / 2. - titleSize.width / 2., buffer),
        titleSize,
        ">>- Einstellungen -<<");
    title.setFont(title.getFont().deriveFont(40f));
    title.setBorder(null);
    title.useColorSet(Style.normal);
    title.setHorizontalAlignment(PacLabel.CENTER);
    add(title);
    //endregion

    Dimension infoSize = new Dimension(Gui.frameWidth - 2 * buffer, (int) ( 2 * lineHeight * 0.75 + buffer ));
    Vector2d  infoPos  = new Vector2d().cartesian(buffer, Gui.frameHeight - infoSize.height - buffer);
    //region info panel
    RoundedPanel info = new RoundedPanel();
    info.setLocation((int) infoPos.x, (int) infoPos.y);
    info.setSize(infoSize);
    info.setArc(widgetArc);
    info.setBorderWidth(lineWeight);
    info.useColorSet(Style.normal);
    add(info);
    //endregion


    Vector2d  selectionPos  = new Vector2d().cartesian(buffer, title.getLocation().y + title.getHeight() + buffer);
    Dimension selectionSize = new Dimension(200, (int) ( Gui.frameHeight - selectionPos.y - infoSize.height - 2 * buffer ));
    //region selection tree
    PacTree tree = new PacTree(createTree(),
        selectionPos,
        selectionSize
    );
    tree.arc = widgetArc;
    tree.borderWidth = lineWeight;
    tree.setFont(tree.getFont().deriveFont(25f));
    tree.tree.setToggleClickCount(1);

    for (int i = 0; i < tree.tree.getRowCount(); i++) {
      tree.tree.expandRow(i);
    }

    tree.tree.addTreeWillExpandListener(new TreeWillExpandListener() {
      @Override
      public void treeWillExpand (TreeExpansionEvent event) throws ExpandVetoException
      {

      }

      @Override
      public void treeWillCollapse (TreeExpansionEvent event) throws ExpandVetoException
      {
        throw new ExpandVetoException(event);
      }
    });

    tree.tree.addTreeSelectionListener(e ->
    {
      if (e.getPath().getLastPathComponent().toString().equals("Settings"))
        return;

      String path = e.getPath().toString()
                     .replace("[", "")
                     .replace("]", "")
                     .replace(",", "/")
                     .replace(" ", "");

      path = path.substring("Settings/".length());
      currentSettingGroup = Config.getInstance().root.getBranchedTree(path);
      fillEditor();
    });

    add(tree);
    //endregion


    Vector2d  editorPos  = new Vector2d().cartesian(tree.getLocation().x + tree.getWidth() + buffer, tree.getLocation().y);
    Dimension editorSize = new Dimension((int) ( Gui.frameWidth - editorPos.x - buffer ), selectionSize.height);
    //region editor
    RoundedPanel editor = new RoundedPanel();
    editor.setLocation((int) editorPos.x, (int) editorPos.y);
    editor.setSize(editorSize);
    editor.setArc(widgetArc);
    editor.setBorderWidth(lineWeight);
    editor.useColorSet(Style.normal);
    editor.setLayout(null);
    add(editor);

    //region editor title
    PacLabel editorTitle = new PacLabel(
        new Vector2d().cartesian(buffer, buffer),
        new Dimension(editorSize.width - 2 * buffer, 25),
        "Editor:");
    editorTitle.setFont(editorTitle.getFont().deriveFont(25f));
    editorTitle.useColorSet(Style.normal);
    editorTitle.setBorder(null);
    editorTitle.setHorizontalAlignment(PacLabel.LEFT);
    editor.add(editorTitle);
    //endregion

    //region editor separator
    RoundedPanel separator = new RoundedPanel();
    separator.setLocation(buffer, (int) ( editorTitle.getLocation().y + editorTitle.getHeight() + buffer / 2 ));
    separator.setSize(editorSize.width - 2 * buffer, 4);
    separator.setArc(2);
    separator.setBorderWidth(5);
    separator.useColorSet(Style.normal);
    separator.repaint();
    editor.add(separator);
    //endregion

    //region editor content
    editors = new JPanel();
    editors.setLayout(new BoxLayout(editors, BoxLayout.Y_AXIS));
    editors.setBackground(null);
    editors.setForeground(null);
    editors.setOpaque(false);

    scroll = new JScrollPane(editors,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setLocation(buffer, (int) ( separator.getLocation().y + separator.getHeight() + buffer / 2 ));
    scroll.setSize(editorSize.width - 2 * buffer, editorSize.height - scroll.getLocation().y - buffer);
    scroll.setBackground(null);
    scroll.setForeground(null);
    scroll.getViewport().setBackground(null);
    scroll.setOpaque(false);
    scroll.setBorder(null);
    editor.add(scroll);
    //endregion

    //endregion
  }

  public void fillEditor ()
  {
    editors.removeAll();
    editors.repaint();
    if (!currentSettingGroup.getLeaves().isEmpty())
    {
      currentSettingGroup.getLeaves().values().stream()
                         .sorted(Comparator.comparingInt(o -> o.ordinal))
                         .forEach( leaf ->
                         {
                           SettingEditor sEditor = new SettingEditor(
                               0.7,
                               0.05,
                               leaf
                           );
                           sEditor.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
                           editors.add(sEditor);
                           editors.add(Box.createRigidArea(new Dimension(0, 10)));
                         });
    }
    editors.revalidate();
  }

  //region tree
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

    //    IntStream.range(0, node.getChildCount())
    //        .mapToObj(node::getChildAt)
    //        .map(PacTree.Node.class::cast)
    //        .forEachOrdered(child ->
    //        {
    //          child.parent = node;
    //
    //          ArrayList<String> path = new ArrayList<>();
    //          path.add(child.toString());
    //
    //          PacTree.Node parent = (PacTree.Node) child.getParent();
    //          while (parent != null)
    //          {
    //            path.add(parent.toString());
    //            parent = (PacTree.Node) parent.getParent();
    //          }
    //          Collections.reverse(path);
    //          child.path = path.toArray(new String[0]);
    //          System.out.println(Arrays.toString(child.path));
    //        });


    return node;
  }
  //endregion

  public void enableControls ()
  {
    bindPlayer(InputListener.Player.playerOne, input ->
    {
     if (input.key() == InputListener.Key.B)
      {
        Gui.getInstance().setContent(PacMainMenu.getInstance(Gui.getInstance().content));
      }
    });
  }
}



