package kn.uni.menus;

import kn.uni.Gui;
import kn.uni.menus.engine.Projector;
import kn.uni.menus.objects.UILabel;
import kn.uni.menus.objects.UITable;
import kn.uni.ui.InputListener;
import kn.uni.util.Fira;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.PacPhiConfig;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsMenu extends Menu
{
  UITable selected;
  int     mainSectionIndex = 0;
  int     subSectionIndex  = 0;
  int     fontSize         = 24;

  public SettingsMenu (JPanel parent)
  {
    super(parent);

    createUIComponents();

    bindPlayer(InputListener.Player.playerOne, input ->
    {
      if (input.equals(new InputListener.Input(InputListener.Key.A, InputListener.State.down, InputListener.Player.playerOne)))
      {
        selected.pressSelected();
        return;
      }

      if (input.equals(new InputListener.Input(InputListener.Key.B, InputListener.State.down, InputListener.Player.playerOne)))
      {
        if (selected.equals(getElement(1).asTable()))
        {
          kill();
          PacPhiConfig.getInstance().save();
          Projector.getInstance(Gui.getInstance().content).setSelectedMenu(new MainMenu(parent));
        }
        else if (selected.equals(getElement(2).asTable()))
        {
          selected.deselectCell();
          selected = getElement(1).asTable();
        }
        else if (selected.equals(getElement(3).asTable()))
        {
          selected.deselectCell();
          selected = getElement(2).asTable();
        }
        return;
      }

      selected.moveSelection(input.toDirection());

      if (input.toDirection() != null)
      {
        if (selected.equals(getElement(1).asTable()))
        {
          mainSectionIndex = Util.bounded((int) ( mainSectionIndex + input.toDirection().toVector().x ), 0, PacPhiConfig.getInstance().settings.children().size() - 1);
          showCurrentSubSection();
        }
        else if (selected.equals(getElement(2).asTable()))
        {
          List <String> mainKeys = PacPhiConfig.getInstance().settings.getKeyList();
          List <String> subKeys  = PacPhiConfig.getInstance().settings.children().get(mainKeys.get(mainSectionIndex)).getKeyList();

          subSectionIndex = Util.bounded((int) ( subSectionIndex + input.toDirection().toVector().y ), 0, subKeys.size() - 1);
          showCurrentSettings();
        }

        System.out.println("mainSectionIndex = " + mainSectionIndex);
        System.out.println("subSectionIndex = " + subSectionIndex);

      }

    });
    assignActions();
    showCurrentSubSection();
    showCurrentSettings();
  }

  private void createUIComponents ()
  {
    //Header
    UILabel header = new UILabel(
        new Vector2d().cartesian(15, 15),
        new Dimension(Gui.frameWidth - 30 * 2, 50),
        "EINSTELLUNGEN:",
        0);
    header.setAlignment(UILabel.Alignment.MIDDLE_CENTER);
    header.setFont(Fira.getInstance().getUnderlined(54));
    header.borderColor = null;
    header.textColor = Color.CYAN.darker().darker();
    elements.add(header);
    //


    //Main Sections
    int mainSectionCount = PacPhiConfig.getInstance().settings.children().size();

    UITable mainSections = new UITable(new Vector2d().cartesian(50, 80), new Dimension(Gui.frameWidth - 2 * 50, 50), 0, new Dimension(mainSectionCount, 1));
    mainSections.showBorder = true;
    mainSections.showCellBounds = true;
    mainSections.setSpacing(15, 0);
    mainSections.setDefCellSize();

    //ButtonLabels
    List <String> mainKeys   = PacPhiConfig.getInstance().settings.getKeyList();
    List <String> mainLabels = new ArrayList <>();
    mainKeys.forEach(key -> mainLabels.add(PacPhiConfig.getInstance().descriptions.get(key).displayName()));
    mainSections.convertRowToButtons(0, mainLabels.toArray(new String[0]));
    //
    mainSections.selectCell(new int[]{ 0, 0 });
    mainSections.setFontSize(fontSize);
    selected = mainSections;
    elements.add(mainSections);
    //


    //Sub Sections
    UITable subSections = new UITable(new Vector2d().cartesian(50, 200), new Dimension(175, Gui.frameHeight - 15 - 200 - 100), 0, new Dimension(1, 1));
    subSections.showBorder = true;
    subSections.showCellBounds = true;
    subSections.setSpacing(0, 15);
    subSections.setDefCellSize();
    subSections.setCellSize(new Dimension(subSections.getCurrentCellWidth(), mainSections.getCurrentCellHeight()));
    subSections.alignCells();

    subSections.setFontSize(fontSize);
    elements.add(subSections);
    //


    //Settings
    UITable settings = new UITable(new Vector2d().cartesian(250, 200), new Dimension(Gui.frameWidth - 200 - 100, Gui.frameHeight - 15 - 200 - 100), 0, new Dimension(2, 1));
    settings.showBorder = true;
    settings.showCellBounds = true;
    settings.setSpacing(5, 15);
    settings.setDefCellSize();
    settings.setCellSize(new Dimension(settings.getCurrentCellWidth(), mainSections.getCurrentCellHeight()));
    settings.setColumnWidth(0, (int) ( ( settings.size.width - 5 ) * 2 / 3. ));
    settings.setColumnWidth(1, (int) ( ( settings.size.width - 5 ) / 3. ));
    settings.alignCells();

    settings.selectCell(new int[]{ 0, 0 });
    settings.setFontSize(fontSize);
    elements.add(settings);
    //
  }

  private void showCurrentSubSection ()
  {
    List <String> mainKeys  = PacPhiConfig.getInstance().settings.getKeyList();
    List <String> subKeys   = PacPhiConfig.getInstance().settings.children().get(mainKeys.get(mainSectionIndex)).getKeyList();
    List <String> subLabels = new ArrayList <>();
    subKeys.forEach(key -> subLabels.add(PacPhiConfig.getInstance().descriptions.get(key).displayName()));
    if (subKeys.size() > 0)
    {
      getElement(2).asTable().loadLength(subKeys.size());
      getElement(2).asTable().alignCells();
      getElement(2).asTable().convertColumnToButtons(0, subLabels.toArray(new String[0]));
    }
  }

  private void showCurrentSettings ()
  {
    List <String> mainKeys    = PacPhiConfig.getInstance().settings.getKeyList();
    List <String> subKeys     = PacPhiConfig.getInstance().settings.children().get(mainKeys.get(mainSectionIndex)).getKeyList();
    List <String> settingKeys = PacPhiConfig.getInstance().settings.children().get(mainKeys.get(mainSectionIndex)).children().get(subKeys.get(subSectionIndex)).getKeyList();
    Collections.reverse(settingKeys);
    List <String> settingLabels = new ArrayList <>();
    settingKeys.forEach(key -> settingLabels.add(PacPhiConfig.getInstance().descriptions.get(key).displayName()));

    List <String> settingValues = new ArrayList <>();
    settingKeys.forEach(key -> settingValues.add(PacPhiConfig.getInstance().settings.children().get(mainKeys.get(mainSectionIndex)).children().get(subKeys.get(subSectionIndex)).get(key).setting().current().toString()));

    if (settingKeys.size() > 0)
    {
      getElement(3).asTable().loadLength(settingKeys.size());
      getElement(3).asTable().alignCells();
      getElement(3).asTable().convertColumnToLabels(0, settingLabels.toArray(new String[0]));
      getElement(3).asTable().convertColumnToButtons(1, settingValues.toArray(new String[0]));
    }
  }

  private void assignActions ()
  {
    getElement(1).asTable().getRow(0).forEach(button -> button.asButton().addAction(() ->
    {
      getElement(2).asTable().selectCell(new int[]{ 0, 0 });
      selected = getElement(2).asTable();
    }));
  }
}


//  int subSectionCount = PacPhiConfig.getInstance().settings.children().get(mainKeys.get(0)).children().size();
//  int settingCount = PacPhiConfig.getInstance().settings.children().get(mainKeys.get(0)).children().get(subKeys.get(0)).children().size();
//
//ButtonLabels
//    List <String> subKeys   = PacPhiConfig.getInstance().settings.children().get(mainKeys.get(0)).getKeyList();
//    List <String> subLabels = new ArrayList <>();
//    subKeys.forEach(key -> subLabels.add(PacPhiConfig.getInstance().descriptions.get(key).displayName()));
//    subSections.convertColumnToButtons(0, subLabels.toArray(new String[0]));
//
//ButtonLabels
//    List <String> settingKeys   = PacPhiConfig.getInstance().settings.children().get(mainKeys.get(0)).children().get(subKeys.get(0)).getKeyList();
//    List <String> settingLabels = new ArrayList <>();
//    settingKeys.forEach(key -> settingLabels.add(PacPhiConfig.getInstance().descriptions.get(key).displayName()));
//    settings.convertColumnToButtons(0, settingLabels.toArray(new String[0]));
//