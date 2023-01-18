package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.PacPhi;
import kn.uni.menus.MenuTest;
import kn.uni.menus.engine.Projector;
import kn.uni.ui.InputListener;
import kn.uni.ui.InputListener.Input;
import kn.uni.ui.InputListener.Player;
import kn.uni.ui.pmButton;
import kn.uni.util.Fira;
import kn.uni.util.Util;
import kn.uni.util.fileRelated.PacPhiConfig;
import kn.uni.util.fileRelated.Permission;
import kn.uni.util.fileRelated.TextureEditor;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainMenu extends JPanel
{

  private static MainMenu instance;

  pmButton spButton, mpButton, settingsButton, lbButton, soundButton, testButton;
  pmButton        title_label;
  List <pmButton> buttons;

  int w = 500;
  int x = ( Gui.frameWidth - w ) / 2;

  int h         = 120;
  int n_offsets = 32;
  int n_buttons = 6;
  int buffer    = ( Gui.frameHeight - ( n_buttons * h ) ) / n_offsets;
  int y         = ( n_offsets - n_buttons ) / 2 * buffer + 100;

  int selected_index = 0;
  int listenerId;


  private MainMenu ()
  {

    setBackground(Color.black);
    setLayout(null);

    createButtons();

    buttons = Arrays.asList(spButton, mpButton, lbButton, settingsButton, soundButton, testButton);

    select(0);

    activate();

    addActions();

    addGraphics();

    if (PacPhi.permissions.current.equals(Permission.Level.User))
    {
      testButton.clearActions();
      settingsButton.clearActions();
      mpButton.clearActions();
    }
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
      //      if (input.equals(new Input(InputListener.Key.B, InputListener.State.down, Player.playerOne)))
      //      {
      //        System.exit(0);
      //      }

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
    Gui.getInstance().frame.invalidate();
    Gui.getInstance().frame.repaint();
  }

  public void select (int index)
  {
    selected_index = index;
    buttons.forEach(b -> b.isSelected = false);
    buttons.get(index).isSelected = true;
    buttons.forEach(pmButton::update);
  }

  private void createButtons ()
  {
    title_label = new pmButton("~ PAC - MAN ~");
    title_label.setBounds(x, 100, w, h);
    title_label.setFontSize(54);
    title_label.setBorder(null);
    add(title_label);

    spButton = new pmButton("EIN SPIELER");
    spButton.setBounds(x, y, w, h);
    add(spButton);

    mpButton = new pmButton("ZWEI SPIELER");
    mpButton.setBounds(x, y + ( h + buffer ), w, h);
    add(mpButton);

    lbButton = new pmButton("BESTENLISTE");
    lbButton.setBounds(x, y + 2 * ( h + buffer ), w, h);
    add(lbButton);

    settingsButton = new pmButton("EINSTELLUNGEN");
    settingsButton.setBounds(x, y + 3 * ( h + buffer ), w, h);
    add(settingsButton);

    soundButton = new pmButton("TON - AN");
    soundButton.setBounds(x, y + 4 * ( h + buffer ), w, h);
    add(soundButton);

    testButton = new pmButton("TEST");
    testButton.setBounds(x, y + 5 * ( h + buffer ), w, h);
    add(testButton);
  }

  private void addActions ()
  {
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

      ClassicPacmanGameScreen gameScreen;
      try
      {
        gameScreen = new ClassicPacmanGameScreen(Gui.getInstance().content, Player.playerOne);
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }
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

    settingsButton.addAction(() ->
    {
      Gui.getInstance().content.add(SettingsMenu.getInstance(this));
      SettingsMenu.getInstance(this).setBounds(Gui.defaultFrameBounds);
      SettingsMenu.getInstance(this).activate();
    });

    soundButton.clearActions();
    soundButton.addAction(() -> soundButton.setText(soundButton.getText().contains("AUS") ? "TON - AN" : "TON - AUS"));

    testButton.addAction(() ->
    {
      Projector projector = Projector.getInstance(Gui.getInstance().content);
      projector.setBounds(Gui.defaultFrameBounds);
      projector.setSelectedMenu(new MenuTest(projector));
      projector.startScreen();
    });
  }

  private void addGraphics ()
  {
    //QR Code
    JPanel qrPanel = new JPanel();
    qrPanel.setBounds(Gui.frameWidth - 170, Gui.frameHeight - 250, 160, 240);
    qrPanel.setBackground(Color.black);

    JLabel qrLabel = new JLabel("<html> <pre>   Vorschläge  <br/>       &       <br/>    Probleme </pre></html>", SwingConstants.CENTER);
    qrLabel.setForeground(Color.cyan.darker());
    qrLabel.setFont(Fira.getInstance().getLigatures(15));
    qrLabel.setBounds(0, 0, 160, 20);
    qrPanel.add(qrLabel);

    BufferedImage qrImage = TextureEditor.getInstance().loadResource("pacman/QR_Code_GitLab.png");
    JLabel        qrCode  = new JLabel(new ImageIcon(qrImage));
    qrCode.setBounds(0, 0, 150, 150);
    qrCode.setBackground(Color.white);
    qrPanel.add(qrCode);

    add(qrPanel);

    //GameInfo
    List <String> content = new ArrayList <>();
    content.add("Ver.:" + PacPhi.GAME_VERSION);
    content.add("Latest Feature: " + PacPhi.GAME_UPDATE);
    content.add("Branch: " + PacPhi.GAME_BRANCH);
    String currentVersion = (String) PacPhiConfig.getInstance().settings.get("General").get("-").get("Branch").setting().current();
    content.add(PacPhi.GAME_BRANCH == currentVersion ? "" : "Restart to: " + currentVersion);
    boolean debug = (boolean) PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current();
    content.add(debug ? "Debug Mode" : "");

    content.removeIf(String::isEmpty);

    int    height   = 20 * content.size();
    JPanel gameInfo = new JPanel();
    gameInfo.setLayout(null);
    gameInfo.setBounds(10, Gui.frameHeight - ( height + 10 ), 300, height);
    gameInfo.setBackground(Color.black);

    content.forEach(s ->
    {
      JLabel label = new JLabel(s, SwingConstants.LEFT);
      label.setForeground(Color.cyan.darker());
      label.setFont(Fira.getInstance().getLigatures(15));
      label.setBounds(0, 20 * content.indexOf(s), 300, 20);
      gameInfo.add(label);
    });

    add(gameInfo);
  }
}
