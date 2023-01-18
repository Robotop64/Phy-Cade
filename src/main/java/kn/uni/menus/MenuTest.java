package kn.uni.menus;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.screens.MainMenu;
import kn.uni.menus.engine.Projector;
import kn.uni.menus.objects.UIButton;
import kn.uni.menus.objects.UIObject;
import kn.uni.menus.objects.UITable;
import kn.uni.ui.InputListener;
import kn.uni.util.Direction;
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

  }

  private void createUIComponents ()
  {
    UITable a = new UITable(new Vector2d().cartesian(300, 100), new Dimension(800, 600), 0, new Dimension(3, 5));
    a.showBorder = true;
    a.showGrid = true;
    a.controllable = true;

    a.cellToButton(new int[]{ 0, 0 }, "Singleplayer");
    a.getCellContent(new int[]{ 0, 0 }).asLabel().setFontSize(30);
    a.getCellContent(new int[]{ 0, 0 }).asLabel().useColorSet(UIButton.selected);
    a.cellToButton(new int[]{ 0, 1 }, "Multiplayer");
    a.getCellContent(new int[]{ 0, 1 }).asLabel().setFontSize(30);
    a.cellToButton(new int[]{ 0, 2 }, "Settings");
    a.getCellContent(new int[]{ 0, 2 }).asLabel().setFontSize(30);
    a.cellToButton(new int[]{ 0, 3 }, "Credits");
    a.getCellContent(new int[]{ 0, 3 }).asLabel().setFontSize(30);
    a.cellToButton(new int[]{ 0, 4 }, "Exit");
    a.getCellContent(new int[]{ 0, 4 }).asLabel().setFontSize(30);

    a.selectCell(new int[]{0,0});
    a.moveSelection(Direction.down);
    elements.add(a);
  }
}
