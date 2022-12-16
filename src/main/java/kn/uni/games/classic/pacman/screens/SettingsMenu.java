package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.ui.InputListener;
import kn.uni.ui.UIScreen;

import javax.swing.JPanel;


public class SettingsMenu extends UIScreen
{
  private static SettingsMenu instance;
  private        int          listener_id;


  public SettingsMenu (JPanel parent)
  {
    super(parent);
  }

  public static SettingsMenu getInstance (JPanel parent)
  {
    if (instance == null) instance = new SettingsMenu(parent);
    return instance;
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
