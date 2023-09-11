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
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Comparator;

public class LeaderboardMenu extends UIScreen implements Controllable
{

  public static LeaderboardMenu instance;
  Config.Tree currentSettingGroup;
  public JPanel      editors;
  public JScrollPane scroll;

  public LeaderboardMenu (JPanel parent)
  {
    super(parent);
    setLayout(null);
    setBackground(Color.BLACK);
    setVisible(true);

    addComponents();
  }

  public static LeaderboardMenu getInstance (JPanel parent)
  {
    if (instance == null)
    {
      instance = new LeaderboardMenu(parent);
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
        ">>- Leaderboard -<<");
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


    Vector2d  gameInfoPos  = new Vector2d().cartesian(buffer, title.getLocation().y + title.getHeight() + buffer);
    Dimension gameInfoSize = new Dimension(350, (int) ( Gui.frameHeight - gameInfoPos.y - infoSize.height - 2 * buffer ));

    //region game info panel
    RoundedPanel gameInfo = new RoundedPanel();
    gameInfo.setLocation((int) gameInfoPos.x, (int) gameInfoPos.y);
    gameInfo.setSize(gameInfoSize);
    gameInfo.setArc(widgetArc);
    gameInfo.setBorderWidth(lineWeight);
    gameInfo.useColorSet(Style.normal);
    gameInfo.setLayout(null);
    add(gameInfo);

    //region game info content
    JPanel gameInfoContent = new JPanel();
    int cBuffer = buffer*2/3;
    gameInfoContent.setLocation(cBuffer, cBuffer);
    gameInfoContent.setSize(gameInfoSize.width - 2 * cBuffer, gameInfoSize.height - 2 * cBuffer);
    gameInfoContent.setLayout(new BoxLayout(gameInfoContent, BoxLayout.Y_AXIS));
    gameInfoContent.setBackground(null);
    gameInfoContent.setForeground(null);
    gameInfo.add(gameInfoContent);

    JComboBox <String> gameSelector = new JComboBox<>(Config.getInstance().getSetting("General/Games").toValue().possible);
    gameSelector.setMaximumSize(new Dimension(Short.MAX_VALUE, lineHeight));

    gameInfoContent.add(gameSelector);


    gameInfoContent.revalidate();
    //endregion

    //endregion


    Vector2d  editorPos  = new Vector2d().cartesian(gameInfo.getLocation().x + gameInfo.getWidth() + buffer, gameInfo.getLocation().y);
    Dimension editorSize = new Dimension((int) ( Gui.frameWidth - editorPos.x - buffer ), gameInfoSize.height);
    //region editor
    RoundedPanel editor = new RoundedPanel();
    editor.setLocation((int) editorPos.x, (int) editorPos.y);
    editor.setSize(editorSize);
    editor.setArc(widgetArc);
    editor.setBorderWidth(lineWeight);
    editor.useColorSet(Style.normal);
    editor.setLayout(null);
    add(editor);
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



