package kn.uni.menus;

import kn.uni.Gui;
import kn.uni.PacPhi;
import kn.uni.menus.objects.UILabel;
import kn.uni.menus.objects.UITable;
import kn.uni.ui.InputListener;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.PacPhiConfig;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends Menu
{
  UITable selected;

  public MainMenu (JPanel parent)
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

      selected.moveSelection(input.toDirection());

    });
  }

  private void createUIComponents ()
  {
    //titel
    UILabel titel = new UILabel(new Vector2d().cartesian(390, 100), new Dimension(500, 120), "~ PAC - MAN ~", 0);
    titel.setFontSize(54);
    titel.borderColor = null;
    titel.textColor = Color.yellow;
    elements.add(titel);
    //

    //buttons
    UITable main = new UITable(
        new Vector2d().cartesian(390, 217),
        new Dimension(500, 120 * 5 + 4 * 9), 0,
        new Dimension(1, 5));
    main.vSpacing = 9;
    main.borderWidth = 1;

    main.cellToButton(new int[]{ 0, 0 }, "EIN SPIELER");
    main.getCellContent(new int[]{ 0, 0 }).asLabel().setFontSize(32);

    main.cellToButton(new int[]{ 0, 1 }, "ZWEI SPIELER");
    main.getCellContent(new int[]{ 0, 1 }).asLabel().setFontSize(32);

    main.cellToButton(new int[]{ 0, 2 }, "BESTENLISTE");
    main.getCellContent(new int[]{ 0, 2 }).asLabel().setFontSize(32);

    main.cellToButton(new int[]{ 0, 3 }, "EINSTELLUNGEN");
    main.getCellContent(new int[]{ 0, 3 }).asLabel().setFontSize(32);

    main.cellToButton(new int[]{ 0, 4 }, "TON - AN");
    main.getCellContent(new int[]{ 0, 4 }).asLabel().setFontSize(32);
    main.getCellContent(new int[]{ 0, 4 }).asButton().addAction(() ->
    {
      UILabel label = main.getCellContent(new int[]{ 0, 4 }).asLabel();
      label.setText(label.text.getText().equals("TON - AN") ? "TON - AUS" : "TON - AN");
    });

    main.selectCell(new int[]{ 0, 0 });
    selected = main;
    elements.add(main);
    //

    //infos
    UILabel i = new UILabel(
        new Vector2d().cartesian(10, Gui.frameHeight - ( 20 * 5 + 4 * 5 + 10 )),
        new Dimension(300, 20 * 5 + 4 * 3),
        "a", 0);
    i.setFontSize(16);
    i.setAlignment(UILabel.Alignment.BOTTOM_LEFT);
    i.textLineSpacing = 8;
    i.borderColor = null;

    List <String> content = new ArrayList <>();
    content.add("Ver.:" + PacPhi.GAME_VERSION);
    content.add("Latest Feature: " + PacPhi.GAME_UPDATE);
    content.add("Branch: " + PacPhi.GAME_BRANCH);
    String currentVersion = (String) PacPhiConfig.getInstance().settings.get("General").get("-").get("Branch").setting().current();
    content.add(PacPhi.GAME_BRANCH == currentVersion ? "" : "Restart to: " + currentVersion);
    boolean debug = (boolean) PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current();
    content.add(debug ? "Debug Mode" : "");

    content.removeIf(String::isEmpty);

    StringBuilder text = new StringBuilder();
    for (String s : content)
    {
      text.append(s).append("\n");
    }
    i.setText(String.valueOf(text));
    elements.add(i);
    //

    //QR-Code


  }
}
