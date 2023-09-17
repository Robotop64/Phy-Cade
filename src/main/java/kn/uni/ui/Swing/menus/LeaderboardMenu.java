package kn.uni.ui.Swing.menus;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.persistence.PacmanDatabaseProvider;
import kn.uni.ui.InputListener;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.components.PacComboBox;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.components.RoundedPanel;
import kn.uni.ui.Swing.interfaces.Controllable;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LeaderboardMenu extends UIScreen implements Controllable
{

  public static LeaderboardMenu instance;
  public String[] dbFields;
  public String[] tableFields;
  private final int entryCount = 15;

  public LeaderboardMenu (JPanel parent)
  {
    super(parent);
    setLayout(null);
    setBackground(Color.BLACK);
    setVisible(true);

    dbFields = new String[4];

    addComponents();

    refreshTable(0);
  }

  public static LeaderboardMenu getInstance (JPanel parent)
  {
    if (instance == null)
    {
      instance = new LeaderboardMenu(parent);
    }
    return instance;
  }

  private int buffer     = 20;
  private int lineHeight = 60;
  private int lineWeight = 3;
  private int widgetArc  = 30;
  private int cBuffer    = buffer * 2 / 3;
  private int vIBuffer   = 10;

  ComponentSpec title;
  ComponentSpec info;
  ComponentSpec gameInfo;
  ComponentSpec table;

  public void addComponents ()
  {
    title = makeTitle();

    info = makeInfo();

    gameInfo = makeGameInfo();

    table = makeTable();

//    IntStream.range(0, entryCount).forEach(i -> {
//      JPanel entry = new JPanel();
//      entry.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
////      entry.setLayout(new BorderLayout());
//      entry.setBackground(Color.RED);
//      entry.setForeground(null);
//      eContent.add(entry);
//
//      if (i < entryCount - 1)
//        eContent.add(Box.createRigidArea(new Dimension(0, vIBuffer)));
//    });
  }

  record ComponentSpec (Vector2d pos, Dimension size, Component comp) { }

  private ComponentSpec makeTitle()
  {
    Dimension titleSize = new Dimension(500, lineHeight);
    Vector2d  titlePos  = new Vector2d().cartesian(Gui.frameWidth / 2. - titleSize.width / 2., buffer);

    PacLabel title = new PacLabel(
        titlePos,
        titleSize,
        ">>- Leaderboard -<<");
    title.setFont(title.getFont().deriveFont(40f));
    title.setBorder(null);
    title.useColorSet(Style.normal);
    title.setHorizontalAlignment(PacLabel.CENTER);
    add(title);

    return new ComponentSpec(titlePos, titleSize, title);
  }

  private ComponentSpec makeInfo()
  {
    Dimension infoSize = new Dimension(Gui.frameWidth - 2 * buffer, (int) ( 2 * lineHeight * 0.75 + buffer ));
    Vector2d  infoPos  = new Vector2d().cartesian(buffer, Gui.frameHeight - infoSize.height - buffer);

    RoundedPanel info = new RoundedPanel();
    info.setLocation((int) infoPos.x, (int) infoPos.y);
    info.setSize(infoSize);
    info.setArc(widgetArc);
    info.setBorderWidth(lineWeight);
    info.useColorSet(Style.normal);
    add(info);

    return new ComponentSpec(infoPos, infoSize, info);
  }

  private ComponentSpec makeGameInfo()
  {
    Vector2d  gameInfoPos  = new Vector2d().cartesian(buffer, title.pos().y + title.size().getHeight() + buffer);
    Dimension gameInfoSize = new Dimension(350, (int) ( Gui.frameHeight - gameInfoPos.y - info.size().height - 2 * buffer ));

    //region parent
    RoundedPanel gameInfo = new RoundedPanel();
    gameInfo.setLocation((int) gameInfoPos.x, (int) gameInfoPos.y);
    gameInfo.setSize(gameInfoSize);
    gameInfo.setArc(widgetArc);
    gameInfo.setBorderWidth(lineWeight);
    gameInfo.useColorSet(Style.normal);
    gameInfo.setLayout(null);
    add(gameInfo);
    //endregion

    //region children
    JPanel gameInfoContent = new JPanel();
    gameInfoContent.setLocation(cBuffer, cBuffer);
    gameInfoContent.setSize(gameInfoSize.width - 2 * cBuffer, gameInfoSize.height - 2 * cBuffer);
    gameInfoContent.setLayout(new BoxLayout(gameInfoContent, BoxLayout.Y_AXIS));
    gameInfoContent.setBackground(null);
    gameInfoContent.setForeground(null);
    gameInfo.add(gameInfoContent);

    //region game selector title
    JPanel gST = new JPanel();
    gST.setMaximumSize(new Dimension(Short.MAX_VALUE, lineHeight));
    gST.setLayout(new BorderLayout());
    PacLabel gsC = new PacLabel("Game:");
    gsC.setFontSize(20);
    gST.add(gsC, BorderLayout.CENTER);
    gameInfoContent.add(gST);
    //endregion

    //region game selector
    JPanel gS = new JPanel();
    gS.setMaximumSize(new Dimension(Short.MAX_VALUE, lineHeight));
    gS.setLayout(new BorderLayout());
    Config.Setting       gameSetting  = Config.getInstance().getSetting("General/Games");
    PacComboBox <String> gameSelector = new PacComboBox <>(gameSetting.toValue().possible);

    JLabel renderer = (JLabel) gameSelector.getRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);
    gameSelector.setFont(renderer.getFont().deriveFont(20f));

    gameSelector.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        dbFields[0] = Objects.requireNonNull(gameSelector.getSelectedItem()).toString();
        refreshTable(0);
      }
    });

    gameSelector.setSelectedItem(gameSetting.toValue().current);
    dbFields[0] = gameSetting.toValue().current;

    gS.add(gameSelector, BorderLayout.CENTER);
    gameInfoContent.add(gS);
    //endregion

    gameInfoContent.add(Box.createRigidArea(new Dimension(0, vIBuffer)));

    //region mode selector title
    JPanel mST = new JPanel();
    mST.setMaximumSize(new Dimension(Short.MAX_VALUE, lineHeight));
    mST.setLayout(new BorderLayout());
    PacLabel msC = new PacLabel("Mode:");
    msC.setFontSize(20);
    mST.add(msC, BorderLayout.CENTER);
    gameInfoContent.add(mST);
    //endregion

    //region mode selector
    JPanel mS = new JPanel();
    mS.setMaximumSize(new Dimension(Short.MAX_VALUE, lineHeight));
    mS.setLayout(new BorderLayout());
    Config.Setting       modeSetting  = Config.getInstance().getSetting("Gameplay/" + gameSetting.toValue().current + "/GameMode");
    PacComboBox <String> modeSelector = new PacComboBox <>(modeSetting.toValue().possible);

    renderer = (JLabel) modeSelector.getRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);
    modeSelector.setFont(renderer.getFont().deriveFont(20f));

    modeSelector.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        dbFields[1] = Objects.requireNonNull(modeSelector.getSelectedItem()).toString();
        refreshTable(0);
      }
    });

    modeSelector.setSelectedItem(modeSetting.toValue().current);
    dbFields[1] = modeSetting.toValue().current;

    mS.add(modeSelector, BorderLayout.CENTER);
    gameInfoContent.add(mS);
    //endregion

    gameInfoContent.add(Box.createRigidArea(new Dimension(0, vIBuffer)));

    //region sort title
    JPanel sT = new JPanel();
    sT.setMaximumSize(new Dimension(Short.MAX_VALUE, lineHeight));
    sT.setLayout(new BorderLayout());
    PacLabel ssC = new PacLabel("SortBy:");
    ssC.setFontSize(20);
    sT.add(ssC, BorderLayout.CENTER);
    gameInfoContent.add(sT);
    //endregion

    //region sort selector
    JPanel sS = new JPanel();
    sS.setMaximumSize(new Dimension(Short.MAX_VALUE, lineHeight));
    sS.setLayout(new BoxLayout(sS, BoxLayout.X_AXIS));
    sS.setBackground(null);

    //region sort by
    Config.Setting       sortSetting  = Config.getInstance().getSetting("General/Leaderboard/SortBy");
    List <String>        sorts        = new ArrayList <>(List.of(sortSetting.toValue().possible));
    if (Config.getInstance().hasSetting("Gameplay/" + gameSetting.toValue().current + "/Leaderboard/SortBy"))
    {
      Config.Setting       gameSortSetting = Config.getInstance().getSetting("Gameplay/" + gameSetting.toValue().current + "/Leaderboard/SortBy");
      sorts.addAll(List.of(gameSortSetting.toValue().possible));
    }

    PacComboBox <String> sortSelector = new PacComboBox <>(sorts.toArray(new String[0]));
    sortSelector.setEditable(true);

    renderer = (JLabel) sortSelector.getRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);
    JTextField tF = (JTextField) sortSelector.getEditor().getEditorComponent();
    tF.setHorizontalAlignment(JTextField.CENTER);
    sortSelector.setFont(tF.getFont().deriveFont(20f));

    sortSelector.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        dbFields[2] = Objects.requireNonNull(sortSelector.getSelectedItem()).toString();
        refreshTable(0);
      }
    });

    sortSelector.setSelectedItem(sortSetting.toValue().current);
    dbFields[2] = sortSetting.toValue().current;

    sS.add(sortSelector);
    //endregion

    sS.add(Box.createRigidArea(new Dimension(vIBuffer, 0)));

    //region sort direction
    PacComboBox<String> sortDir = new PacComboBox<>(new String[]{"Desc", "Asc"});
    sortDir.setEditable(true);

    renderer = (JLabel) sortDir.getRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);
    tF = (JTextField) sortDir.getEditor().getEditorComponent();
    tF.setHorizontalAlignment(JTextField.CENTER);
    sortDir.setFont(tF.getFont().deriveFont(20f));

    sortDir.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        dbFields[3] = Objects.requireNonNull(sortDir.getSelectedItem()).toString();
        refreshTable(0);
      }
    });

    sortDir.setSelectedItem("Desc");
    dbFields[3] = "Desc";

    sS.add(sortDir);

    //endregion

    gameInfoContent.add(sS);

    //endregion

    gameInfoContent.revalidate();
    return new ComponentSpec(gameInfoPos, gameInfoSize, gameInfo);
  }

  private ComponentSpec makeTable()
  {
    Vector2d  tablePos  = new Vector2d().cartesian(gameInfo.pos().x + gameInfo.size().getWidth() + buffer, gameInfo.pos().y);
    Dimension tableSize = new Dimension((int) ( Gui.frameWidth - tablePos.x - buffer ), gameInfo.size().height);

    RoundedPanel table = new RoundedPanel();
    table.setLocation((int) tablePos.x, (int) tablePos.y);
    table.setSize(tableSize);
    table.setArc(widgetArc);
    table.setBorderWidth(lineWeight);
    table.useColorSet(Style.normal);
    table.setLayout(null);
    add(table);

    //region table header
    JPanel tHeader = new JPanel();
    tHeader.setLocation(cBuffer, cBuffer);
    tHeader.setSize(tableSize.width - 2 * cBuffer, lineHeight);
    tHeader.setLayout(new BoxLayout(tHeader, BoxLayout.X_AXIS));
    tHeader.setBackground(Color.BLUE);
    tHeader.setForeground(null);
    table.add(tHeader);
    //endregion

    //region table content
    JPanel tContent = new JPanel();
    tContent.setLocation(cBuffer, tHeader.getLocation().y+tHeader.getHeight() + cBuffer);
    tContent.setSize(tableSize.width - 2 * cBuffer, tableSize.height - tHeader.getHeight() - 3 * cBuffer);
    tContent.setLayout(new BoxLayout(tContent, BoxLayout.Y_AXIS));
    tContent.setBackground(Color.RED);
    tContent.setForeground(null);
    table.add(tContent);
    //endregion

    return new ComponentSpec(tablePos, tableSize, table);
  }

  public void refreshTable (int page)
  {
    int startRow = page * entryCount;
    String filter = "";
    String sort = " ORDER BY " + dbFields[2] + " " + dbFields[3] + " LIMIT "+ startRow + "," + entryCount;

    List<kn.uni.games.classic.pacman.screens.LeaderboardMenu.LeaderboardEntry> entries = PacmanDatabaseProvider.getEntries(dbFields[0] +".db", dbFields[1], filter, sort);
  }

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



