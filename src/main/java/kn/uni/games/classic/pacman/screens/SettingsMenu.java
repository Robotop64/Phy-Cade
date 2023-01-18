package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.ui.InputListener;
import kn.uni.ui.UIScreen;
import kn.uni.ui.pmButton;
import kn.uni.util.fileRelated.PacPhiConfig;

import javax.swing.JPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class SettingsMenu extends UIScreen
{
  private static SettingsMenu             instance;
  private        List <pmButton>          sectionButtons  = new ArrayList <>();
  private        List <pmButton>          subGroupButtons = new ArrayList <>();
  private        List <pmButton>          settingButtons  = new ArrayList <>();
  private        List <pmButton>          settingContext  = new ArrayList <>();
  private        int                      listener_id;
  private        PacPhiConfig.SettingTree activeSection;
  private        PacPhiConfig.SettingTree activeSubGroup;
  private        PacPhiConfig.SettingTree activeSetting;


  public SettingsMenu (JPanel parent)
  {
    super(parent);
    setBackground(Color.black);
    setLayout(null);

    displaySections();

    selectSection(4);

    selectSubGroup(0);

    //    selectSetting(0);

  }

  public static SettingsMenu getInstance (JPanel parent)
  {
    if (instance == null) instance = new SettingsMenu(parent);
    return instance;
  }

  private void displaySections ()
  {
    int borderBuffer = 15;

    pmButton header = new pmButton("Settings:");
    header.setBounds(borderBuffer, borderBuffer, Gui.frameWidth - borderBuffer * 2, 50);
    header.isSelected = false;
    header.update();
    add(header);

    int mainSections     = PacPhiConfig.getInstance().settings.children().size();
    int mainSectionWidth = Gui.frameWidth - borderBuffer * 5 * 2;
    int sectionWidth     = ( mainSectionWidth - borderBuffer * ( mainSections - 1 ) ) / mainSections;

    IntStream.range(0, mainSections).forEach(i ->
        PacPhiConfig.getInstance().settings.children().values().stream()
                                           .filter(section -> section.order() == i)
                                           .findFirst()
                                           .ifPresent(section ->
                                           {
                                             PacPhiConfig.Setting <?> setting = section.setting();

                                             pmButton sectionButton = new pmButton(setting.name());
                                             sectionButton.setBounds(borderBuffer * 5 + ( sectionWidth + borderBuffer ) * section.order(), borderBuffer + 48, sectionWidth, 50);
                                             if (section.order() == 0)
                                             {
                                               sectionButton.isSelected = true;
                                               activeSection = section;
                                             }
                                             sectionButton.update();
                                             sectionButtons.add(sectionButton);
                                             add(sectionButton);
                                           }));
  }

  private void selectSection (int order)
  {
    //deselect active Buttons
    sectionButtons.forEach(button -> button.isSelected = false);
    //select new Button
    sectionButtons.get(order).isSelected = true;
    //update all Buttons
    sectionButtons.forEach(pmButton::update);

    //update active Section
    activeSection = PacPhiConfig.getInstance().settings.children().values().stream()
                                                       .filter(section -> section.order() == order)
                                                       .findFirst()
                                                       .orElse(null);
    //clear old SubGroups and Settings
    subGroupButtons.forEach(this::remove);
    subGroupButtons.clear();
    settingButtons.forEach(this::remove);
    settingButtons.clear();

    displayActiveSubsections();
  }

  private void displayActiveSubsections ()
  {
    int borderBuffer = 15;
    int subsections  = activeSection.children().size();
    if (subsections == 0) return;

    IntStream.range(0, subsections).forEach(i ->
        activeSection.children().values().stream()
                     .filter(section -> section.order() == i)
                     .findFirst()
                     .ifPresent(section ->
                         {
                           //a setting will not be added:
                           //if the the setting is a debug option and debug is disabled
                           //if the setting is a debug option and the setting is not editable
                           //if the setting is not a subgroup
                           if (( (
                               PacPhiConfig.getInstance().debugOnly.contains(section)
                                   &&
                                   !( (boolean) PacPhiConfig.getInstance().settings.get("Debugging").get("Enabled").setting().current() )
                           ) || PacPhiConfig.getInstance().nonEditable.contains(section) ) || section.setting().type() != "SubGroup") return;

                           PacPhiConfig.Setting <?> setting = section.setting();

                           pmButton sectionButton = new pmButton(setting.name());
                           sectionButton.setBounds(borderBuffer * 2, ( borderBuffer * 3 + 100 ) + ( 50 + borderBuffer ) * section.order(), 200, 50);
                           if (section.order() == 0)
                           {
                             sectionButton.isSelected = true;
                             activeSubGroup = section;
                           }
                           sectionButton.update();
                           subGroupButtons.add(sectionButton);
                           add(sectionButton);
                         }
                     )
    );
  }

  private void selectSubGroup (int order)
  {
    subGroupButtons.forEach(button -> button.isSelected = false);
    subGroupButtons.get(order).isSelected = true;
    subGroupButtons.forEach(pmButton::update);

    activeSubGroup = activeSection.children().values().stream()
                                  .filter(section -> section.order() == order)
                                  .findFirst()
                                  .orElse(null);
    settingButtons.clear();

    displayActiveSettings();
  }

  private void displayActiveSettings ()
  {
    int borderBuffer = 15;
    int settings     = activeSubGroup.children().size();
    if (settings == 0) return;

    IntStream.range(0, settings).forEach(i ->
        activeSubGroup.children().values().stream()
                      .filter(section -> section.order() == i)
                      .findFirst()
                      .ifPresent(section ->
                          {
                            //a setting will not be added:
                            //if the the setting is a debug option and debug is disabled
                            //if the setting is a debug option and the setting is not editable
                            if ((
                                PacPhiConfig.getInstance().debugOnly.contains(section)
                                    &&
                                    !( (boolean) PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current() )
                            ) || PacPhiConfig.getInstance().nonEditable.contains(section)) return;

                            PacPhiConfig.Setting <?> setting = section.setting();

                            pmButton sectionButton = new pmButton(setting.name());
                            sectionButton.setBounds(300, ( borderBuffer * 3 + 100 ) + ( 70 + borderBuffer ) * section.order(), 350, 70);
                            if (section.order() == 0)
                            {
                              sectionButton.isSelected = true;
                              activeSetting = section;
                            }
                            sectionButton.update();
                            settingButtons.add(sectionButton);
                            add(sectionButton);

                            pmButton settingField = new pmButton(setting.current().toString());
                            settingField.setBounds(700, ( borderBuffer * 3 + 100 ) + ( 70 + borderBuffer ) * section.order(), 350, 70);
                            if (section.order() == 0)
                            {
                              settingField.isSelected = true;
                            }
                            settingField.update();
                            add(settingField);
                          }


                      )
    );
  }

  private void selectSetting (int order)
  {
    if (settingButtons.size() != 0)
    {
      settingButtons.forEach(button -> button.isSelected = false);
      settingButtons.get(order).isSelected = true;
      settingButtons.forEach(pmButton::update);

      settingContext.forEach(button -> button.isSelected = false);
      settingContext.get(order).isSelected = true;
      settingContext.forEach(pmButton::update);

      activeSetting = activeSubGroup.children().values().stream()
                                    .filter(section -> section.order() == order)
                                    .findFirst()
                                    .orElse(null);
    }


  }

  /**
   * Used to activate the input Listener
   */
  public void activate ()
  {
    listener_id = InputListener.getInstance().subscribe(input ->
    {
      if (input.equals(new InputListener.Input(InputListener.Key.B, InputListener.State.down, InputListener.Player.playerOne)))
      {
        InputListener.getInstance().unsubscribe(listener_id);
        setVisible(false);
        getParent().remove(this);
        Gui.getInstance().content.add(MainMenu.getInstance());
        MainMenu.getInstance().setBounds(Gui.defaultFrameBounds);
        MainMenu.getInstance().activate();
      }

      //      if (input.player().equals(InputListener.Player.playerTwo)) return;
      //      if (!Arrays.asList(InputListener.Key.vertical, InputListener.Key.horizontal)
      //                 .contains(input.key())) return;
      //      int delta = switch (input.state())
      //          {
      //            case up -> -1;
      //            case down -> 1;
      //            case none -> 0;
      //          };

      //      if (input.key().name().equals("horizontal")) ;
      //      if (input.key().name().equals("vertical")) ;
    });
    setVisible(true);
  }
}
