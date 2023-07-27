package kn.uni.ui.Swing.menus;

import kn.uni.games.classic.pacman.screens.AdvGameScreen;
import kn.uni.ui.InputListener;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.components.PacButton;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.components.PacList;
import kn.uni.ui.UIScreen;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

import static kn.uni.Gui.defaultFrameBounds;

public class PacMainMenu extends UIScreen
{
  PacList list;

  public PacMainMenu (JPanel parent)
  {
    super(parent);
    setLayout(null);
    setBounds(defaultFrameBounds);
    setBackground(Color.black);
    setVisible(true);

    addComponents();

    bindPlayer(InputListener.Player.playerOne, input -> {
      if (input.key() == InputListener.Key.A && input.state() == InputListener.State.down)
      {
        InputListener.getInstance().clearInput();
        list.fireSelectedAction();
      }

      Direction dir = input.toDirection();
      if (dir == null) return;
      switch (dir)
      {
        case up -> list.selectNext(-1);
        case down -> list.selectNext(1);
        default -> {}
      }
    });

  }

  public void addComponents ()
  {
    //title
    PacLabel title = new PacLabel(
        new Vector2d().cartesian(390, 100),
        new Dimension(500, 120),
        "~ PAC - MAN ~");
    title.setFont(title.getFont().deriveFont(54f));
    title.setBorder(null);
    title.useColorSet(Style.focused);
    title.setHorizontalAlignment(PacLabel.CENTER);
    add(title);


    //buttons
    list = new PacList(
        new Vector2d().cartesian(390, 217),
        new Dimension(500, 120 * 5 + 4 * 9));
    list.vBuffer = 9;
    list.alignment = PacList.Alignment.VERTICAL;


    PacButton sp = new PacButton("EIN SPIELER");
    sp.addAction(() -> {
      this.kill();
      AdvGameScreen advGameScreen = new AdvGameScreen(this.parent);
      advGameScreen.setBounds(defaultFrameBounds);
      parent.add(advGameScreen);
    });
    list.addObject(sp);

    PacButton mp = new PacButton("ZWEI SPIELER");
    list.addObject(mp);

    PacButton best = new PacButton("BESTENLISTE");
    list.addObject(best);

    PacButton options = new PacButton("EINSTELLUNGEN");
    list.addObject(options);

    PacButton sound = new PacButton("TON - AN");
    sound.addAction(() -> sound.setText(sound.getText().equals("TON - AN") ? "TON - AUS" : "TON - AN"));
    list.addObject(sound);

    list.unifyFontSize(54f);

    add(list);
    list.selectItem(0);
  }
}
