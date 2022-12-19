package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.ui.InputListener;
import kn.uni.ui.UIScreen;
import kn.uni.ui.pmButton;
import kn.uni.util.PacPhiConfig;

import javax.swing.JPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


public class SettingsMenu extends UIScreen
{
  private static SettingsMenu instance;
  private        int          listener_id;


  public SettingsMenu (JPanel parent)
  {
    super(parent);
    setBackground(Color.black);
    setLayout(null);

    createLayout();
  }

  public static SettingsMenu getInstance (JPanel parent)
  {
    if (instance == null) instance = new SettingsMenu(parent);
    return instance;
  }

  private void createLayout ()
  {
    List <pmButton> sectionButtons = new ArrayList <>();
    int             borderBuffer   = 15;

    pmButton header = new pmButton("Settings:");
    header.setBounds(borderBuffer, borderBuffer, Gui.frameWidth - borderBuffer * 2, 50);
    header.isSelected = false;
    header.update();
    add(header);

    int mainSections = PacPhiConfig.getInstance().settings.children().size();
    int allowedWith  = Gui.frameWidth - borderBuffer * 20;
    int sectionWidth = ( allowedWith - borderBuffer * ( mainSections - 1 ) ) / mainSections;

    PacPhiConfig.getInstance().settings.children().forEach((sectionName, section) ->
    {
      PacPhiConfig.Setting setting = section.setting();

      pmButton sectionButton = new pmButton(setting.name());
      sectionButton.setBounds(borderBuffer * 10 + ( sectionWidth + borderBuffer ) * section.order(), borderBuffer * 2 + 50, sectionWidth, 50);
      sectionButton.isSelected = section.order() == 0;
      sectionButton.update();
      sectionButtons.add(sectionButton);
      add(sectionButton);
    });
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
