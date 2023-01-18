package kn.uni.menus;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.screens.MainMenu;
import kn.uni.menus.engine.Projector;
import kn.uni.menus.objects.UIImage;
import kn.uni.menus.objects.UITable;
import kn.uni.ui.InputListener;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import java.awt.Dimension;

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
    UIImage a = new UIImage(new Vector2d().cartesian(0, 0), new Dimension(100, 100), 0, new Dimension(50, 50), "src/main/resources/images/ghosts/ghost1.png");
    elements.add(a);
  }


}
