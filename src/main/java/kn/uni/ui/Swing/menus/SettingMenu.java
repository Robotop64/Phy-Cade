package kn.uni.ui.Swing.menus;

import kn.uni.Gui;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.components.PacTree;
import kn.uni.ui.Swing.components.RoundedPanel;
import kn.uni.ui.Swing.components.SettingEditor;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;

import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
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

    tree.tree.addTreeSelectionListener(new TreeSelectionListener()
    {
      @Override
      public void valueChanged (TreeSelectionEvent e)
      {
        if (e.getPath().getLastPathComponent().toString().equals("Settings"))
          return;

        String path = e.getPath().toString()
                       .replace("[", "")
                       .replace("]", "")
                       .replace(",", "/")
                       .replace(" ", "");

        path = path.substring("Settings/".length());


        System.out.println(path);
      }
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
    SettingEditor settingEditor = new SettingEditor(
        new Vector2d().cartesian(buffer, separator.getLocation().y + separator.getHeight() + (double) buffer / 2),
        new Dimension(editorSize.width - 2 * buffer, 50),
        0.6, 50,
        new Config.Range(50, 0, 100, 5)
    );
    editor.add(settingEditor);
    //endregion


    //    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 50, 25);
    //    slider.setBounds(buffer, (int) (separator.getLocation().y+separator.getHeight()+ (double) buffer /2), editorSize.width-2*buffer, editorSize.height-separator.getLocation().y-separator.getHeight()- 3*buffer/2);
    //    slider.setMinorTickSpacing(2);
    //    slider.setMajorTickSpacing(10);
    //    slider.setPaintTicks(true);
    //    slider.setPaintLabels(true);
    //    editor.add(slider);

    //    PacList editorList = new PacList(
    //        new Vector2d().cartesian(buffer, separator.getLocation().y+separator.getHeight()+ (double) buffer /2),
    //        new Dimension(editorSize.width-2*buffer, editorSize.height-separator.getLocation().y-separator.getHeight()- 3*buffer/2)
    //    );
    //    editorList.vBuffer = 10;
    //    editorList.alignment = PacList.Alignment.VERTICAL;
    ////    editorList.setBackground(Color.WHITE);
    //    editor.add(editorList);
    //
    //    editorList.addObject(new PacButton("Test"));
    //    editorList.addObject(new PacButton("Test"));
    //    editorList.addObject(new PacButton("Test"));
    //    editorList.addObject(new PacButton("Test"));
    //
    //    editorList.unifyFontSize(20f);
    //
    //    editor.add(editorList);


    //endregion
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
}



