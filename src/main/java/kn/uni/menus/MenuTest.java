package kn.uni.menus;

import kn.uni.menus.objects.UIButton;
import kn.uni.menus.objects.UITable;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import java.awt.Dimension;

public class MenuTest extends Menu
{

  public MenuTest (JPanel parent)
  {
    super(parent);

    createUIComponents();
  }

  private void createUIComponents ()
  {
    UITable a = new UITable(new Vector2d().cartesian(300, 100), new Dimension(300, 600), 0, new Dimension(3, 5));
    a.showBorder = true;
    a.showGrid = true;

    a.cellToLabel(new int[]{ 0, 0 }, "Singleplayer");
    a.getCellContent(new int[]{ 0, 0 }).asLabel().setFontSize(30);
    a.getCellContent(new int[]{ 0, 0 }).asLabel().useColorSet(UIButton.selected);
    a.cellToLabel(new int[]{ 0, 1 }, "Multiplayer");
    a.getCellContent(new int[]{ 0, 1 }).asLabel().setFontSize(30);
    a.cellToLabel(new int[]{ 0, 2 }, "Settings");
    a.getCellContent(new int[]{ 0, 2 }).asLabel().setFontSize(30);
    a.cellToLabel(new int[]{ 0, 3 }, "Credits");
    a.getCellContent(new int[]{ 0, 3 }).asLabel().setFontSize(30);
    a.cellToLabel(new int[]{ 0, 4 }, "Exit");
    a.getCellContent(new int[]{ 0, 4 }).asLabel().setFontSize(30);

    elements.add(a);
  }
}
