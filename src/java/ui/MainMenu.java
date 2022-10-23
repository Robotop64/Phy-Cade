package ui;

import ui.InputListener.Input;
import ui.InputListener.Player;
import util.Util;

import javax.swing.JPanel;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class MainMenu extends JPanel
{

  private static MainMenu instance;

  pmButton spButton, mpButton, settingsButton, lbButton, soundButton;
  pmButton title_label;

  int w = 500;
  int x = (Gui.frameWidth - w) / 2;

  int h         = 120;
  int n_offsets = 32;
  int n_buttons = 5;
  int buffer    = (Gui.frameHeight - (n_buttons * h)) / n_offsets;
  int y         = (n_offsets - n_buttons) / 2 * buffer + 100;

  int selected_index = 0;
  int listenerId;

  List <pmButton> buttons;

  private MainMenu ()
  {

    setBackground(Color.black);
    setLayout(null);

    title_label = new pmButton("~ PAC - MAN ~");
    title_label.setBounds(x, 100, w, h);
    title_label.setFontSize(54);
    title_label.setBorder(null);
    add(title_label);

    spButton = new pmButton("EIN SPIELER");
    spButton.setBounds(x, y, w, h);
    add(spButton);

    mpButton = new pmButton("ZWEI SPIELER");
    mpButton.setBounds(x, y + (h + buffer), w, h);
    add(mpButton);

    lbButton = new pmButton("BESTENLISTE");
    lbButton.setBounds(x, y + 2 * (h + buffer), w, h);
    add(lbButton);

    settingsButton = new pmButton("EINSTELLUNGEN");
    settingsButton.setBounds(x, y + 3 * (h + buffer), w, h);
    add(settingsButton);

    soundButton = new pmButton("TON - AN");
    soundButton.setBounds(x, y + 4 * (h + buffer), w, h);
    add(soundButton);

    buttons = Arrays.asList(spButton, mpButton, lbButton, settingsButton, soundButton);

    select(0);

    activate();

    buttons.forEach(b -> b.addAction(() ->
    {
      InputListener.getInstance().unsubscribe(listenerId);
      setVisible(false);
      getParent().remove(this);
    }));

    spButton.addAction(() ->
    {
//      List <Integer> scores = new ArrayList <>();
//      scores.add(50000);
//      scores.add(60000);
//      scores.add(70000);
//      scores.add(80000);
//      List <LocalTime> times = new ArrayList <>();
//      times.add(LocalTime.of(00, 10, 26));
//      times.add(LocalTime.of(00, 20, 26));
//      times.add(LocalTime.of(00, 30, 26));
//      times.add(LocalTime.of(00, 40, 26));
//
//      GameOverScreen gameOverScreen = new GameOverScreen(4, "pacman", scores, times);
//      Gui.getInstance().content.add(gameOverScreen);
//
//      Gui.getInstance().frame.invalidate();
//      Gui.getInstance().frame.repaint();

      ClassicPacmanGameScreen gameScreen = new ClassicPacmanGameScreen(Gui.getInstance().content, Player.playerOne);
      gameScreen.setBounds(Gui.defaultFrameBounds);
      gameScreen.setVisible(true);
    });

    mpButton.addAction(() ->
    {

    });

    lbButton.addAction(() ->
    {
      Gui.getInstance().content.add(LeaderboardMenu.getInstance());
      LeaderboardMenu.getInstance().setBounds(Gui.defaultFrameBounds);
      LeaderboardMenu.getInstance().activate();
    });

    soundButton.clearActions();
    soundButton.addAction(() ->
    {
      soundButton.setText(soundButton.getText().contains("AUS") ? "TON - AN" : "TON - AUS");
    });

  }

  public static MainMenu getInstance ()
  {
    if (instance == null) instance = new MainMenu();
    return instance;
  }

  public void activate ()
  {
    System.out.println("Main Menu activated");
    listenerId = InputListener.getInstance().subscribe(input ->
    {
      if (input.equals(new Input(InputListener.Key.A, InputListener.State.down, Player.playerOne)))
      {
        buttons.get(selected_index).press();
        return;
      }
      if (input.equals(new Input(InputListener.Key.B, InputListener.State.down, Player.playerOne)))
      {
        System.exit(0);
      }

      if (input.player().equals(Player.playerTwo)) return;
      if (!Arrays.asList(InputListener.Key.vertical, InputListener.Key.horizontal)
                 .contains(input.key())) return;
      int delta = switch (input.state())
        {
          case up -> -1;
          case down -> 1;
          case none -> 0;
        };
      select(Util.bounded(selected_index + delta, 0, n_buttons - 1));
    });
    setVisible(true);
    Gui.getInstance().frame.repaint();
  }

  public void select (int index)
  {
    selected_index = index;
    buttons.forEach(b -> b.isSelected = false);
    buttons.get(index).isSelected = true;
    buttons.forEach(pmButton::update);
  }

}
