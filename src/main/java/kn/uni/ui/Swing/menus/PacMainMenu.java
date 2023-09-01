package kn.uni.ui.Swing.menus;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.screens.AdvGameScreen;
import kn.uni.ui.InputListener;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.components.PacButton;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.components.PacList;
import kn.uni.ui.Swing.interfaces.Controllable;
import kn.uni.ui.UIScreen;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

import static kn.uni.Gui.defaultFrameBounds;

public class PacMainMenu extends UIScreen implements Controllable
{
  PacList list;

  public static PacMainMenu instance;

  public PacMainMenu (JPanel parent)
  {
    super(parent);
    setLayout(null);
    setBounds(defaultFrameBounds);
    setBackground(Color.black);
    setVisible(true);

    addComponents();

    enableControls();
  }

  public static PacMainMenu getInstance(JPanel parent)
  {
    if (instance == null)
    {
      instance = new PacMainMenu(parent);
    }
    return instance;
  }

  public void addComponents ()
  {
    //region title
    PacLabel title = new PacLabel(
        new Vector2d().cartesian(390, 100),
        new Dimension(500, 120),
        "~ PAC - MAN ~");
    title.setFont(title.getFont().deriveFont(54f));
    title.setBorder(null);
    title.useColorSet(Style.focused);
    title.setHorizontalAlignment(PacLabel.CENTER);
    add(title);
    //endregion


    //region buttons
    Vector2d center = new Vector2d().cartesian(Gui.frameWidth/2., Gui.frameHeight/2.);
    Dimension listSize = new Dimension(400, 400);
    int buttonBuffer = 40;
    list = new PacList(
        center.subtract(new Vector2d().cartesian(listSize.width/2.,listSize.height/2.))
              .add(new Vector2d().cartesian(0, 30)),
        listSize);
    list.vBuffer = buttonBuffer;
    list.alignment = PacList.Alignment.VERTICAL;


    PacButton sp = new PacButton("EIN SPIELER");
    sp.addAction(() -> {
      AdvGameScreen.clearInstance();
      AdvGameScreen advGameScreen = AdvGameScreen.getInstance(Gui.getInstance().content);
      Gui.getInstance().setContent(advGameScreen);
    });
    list.addObject(sp);

    PacButton mp = new PacButton("ZWEI SPIELER");
    list.addObject(mp);

    PacButton best = new PacButton("BESTENLISTE");
    list.addObject(best);

    PacButton options = new PacButton("EINSTELLUNGEN");
    options.addAction(() -> {
      Gui.getInstance().setContent(SettingMenu.getInstance(Gui.getInstance().content));
    });
    list.addObject(options);

    list.unifyFontSize(40f);

    add(list);
    list.selectItem(0);
    //endregion
  }

  public void enableControls()
  {
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
}
