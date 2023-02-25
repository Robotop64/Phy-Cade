package kn.uni.menus.G2D;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.screens.MainMenu;
import kn.uni.menus.G2D.engine.Projector;
import kn.uni.menus.G2D.objects.UITable;
import kn.uni.ui.InputListener;

import javax.swing.JPanel;

public class MenuTest extends Menu
{
  UITable selected;

  public MenuTest (JPanel parent)
  {
    super(parent);

    createUIComponents();

    bindPlayer(InputListener.Player.playerOne, input ->
    {
      if (input.key().equals(InputListener.Key.B))
      {
        Projector.getInstance(parent).state.running = false;
        Gui.getInstance().content.add(MainMenu.getInstance());
        MainMenu.getInstance().setBounds(Gui.defaultFrameBounds);
        MainMenu.getInstance().activate();
        kill();
      }

      //      if (input.equals(new InputListener.Input(InputListener.Key.A, InputListener.State.down, InputListener.Player.playerOne)))
      //      {
      //        selected.pressSelected();
      //        return;
      //      }
      //
      //      selected.moveSelection(input.toDirection());


    });

  }

  private void createUIComponents ()
  {
  }


}
