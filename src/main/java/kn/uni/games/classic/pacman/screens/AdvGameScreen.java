package kn.uni.games.classic.pacman.screens;

import com.formdev.flatlaf.extras.components.FlatProgressBar;
import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.internal.GameEnvironment;
import kn.uni.games.classic.pacman.game.objects.AdvPacManMap;
import kn.uni.ui.InputListener;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.components.PacList;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;
import org.apache.commons.lang3.StringUtils;

import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvGameScreen extends UIScreen
{
  //components
  public List <Component> uiComponents = new ArrayList <>();
  //controls
  Map <InputListener.Key, InputListener.Input> joystick = new HashMap <>();
  //hud data lists
  private PacList data;
  private PacList leaderboard;

  private GameEnvironment env;


  public AdvGameScreen (JPanel parent)
  {
    super(parent);
    setBackground(Color.BLACK);
    setLayout(null);


    createFrames();
    createHud();

    createLoadingPopup();

    createReadyPopup();

    enableReadyPrompt(false);


    loadGame();

    enableControls();
  }

  //region create UI
  private void createFrames ()
  {
    int edgeBuffer = 15;

    Vector2d  gamePos  = new Vector2d().cartesian(edgeBuffer, edgeBuffer);
    Dimension gameSize = new Dimension(Gui.frameHeight - 2 * 15, Gui.frameHeight - 2 * 15);

    Vector2d  hudPos  = new Vector2d().cartesian(edgeBuffer + gameSize.height + edgeBuffer, edgeBuffer);
    Dimension hudSize = new Dimension(Gui.frameWidth - 3 * edgeBuffer - gameSize.width, Gui.frameHeight - 2 * edgeBuffer);


    JLayeredPane gameWindow = new JLayeredPane();
    gameWindow.setBounds((int) gamePos.x, (int) gamePos.y, gameSize.width, gameSize.height);
    gameWindow.setBorder(BorderFactory.createLineBorder(Color.cyan.darker().darker(), 2));
    add(gameWindow);
    uiComponents.add(gameWindow);

    JPanel hud = new JPanel();
    hud.setBounds((int) hudPos.x, (int) hudPos.y, hudSize.width, hudSize.height);
    hud.setBorder(BorderFactory.createLineBorder(Color.cyan.darker().darker(), 2));
    hud.setBackground(null);
    hud.setLayout(null);
    add(hud);
    uiComponents.add(hud);
  }

  private void createHud ()
  {
    Dimension hudBounds = uiComponents.get(1).getSize();

    //region GameState values
    data = new PacList(new Vector2d().cartesian(5, 5), new Dimension(hudBounds.width - 10, 400 - 10));
    data.alignment = PacList.Alignment.VERTICAL;
    data.vBuffer = 0;
    data.edgeBuffer = 10;
    data.setAutoFit(false);

    PacLabel title = new PacLabel("~ Pac-Man ~");
    title.setHorizontalAlignment(PacLabel.CENTER);
    data.addObject(title);

    data.addBuffer(0, 5);

    PacLabel level = new PacLabel("%7d".formatted(1));
    level.setHeader("Level:");
    level.setHorizontalAlignment(PacLabel.LEFT);
    data.addObject(level);

    data.addBuffer(0, 5);

    PacLabel score1 = new PacLabel("Score:");
    score1.setHorizontalAlignment(PacLabel.LEFT);
    data.addObject(score1);

    data.addBuffer(0, -15);

    PacLabel score2 = new PacLabel("❰" + String.format("%09d", 0) + "❱");
    score2.setBackground(null);
    score2.setOpaque(false);
    score2.setHorizontalAlignment(PacLabel.CENTER);
    data.addObject(score2);

    data.addBuffer(0, 5);

    PacLabel time1 = new PacLabel("Time:");
    time1.setHorizontalAlignment(PacLabel.LEFT);
    data.addObject(time1);

    data.addBuffer(0, -15);

    PacLabel time2 = new PacLabel("00:00:00.000");
    time2.setBackground(null);
    time2.setOpaque(false);
    time2.setHorizontalAlignment(PacLabel.CENTER);
    data.addObject(time2);

    data.addBuffer(0, 5);

    PacLabel lives = new PacLabel("5");
    lives.setHeader("Lives: x");
    lives.setHorizontalAlignment(PacLabel.LEFT);
    data.addObject(lives);

    data.fitComponents();
    data.unifyFontSize(35f);

    ( (JPanel) uiComponents.get(1) ).add(data);
    //endregion

    //region Dynamic Leaderboard
    leaderboard = new PacList(new Vector2d().cartesian(10, 680), new Dimension(hudBounds.width - 20, hudBounds.height - 680 - 10));
    leaderboard.alignment = PacList.Alignment.VERTICAL;
    leaderboard.edgeBuffer = 10;
    leaderboard.vBuffer = 0;
    leaderboard.setAutoFit(false);


    PacLabel title2 = new PacLabel("~ Leaderboard ~");
    title2.setFont(title2.getFont().deriveFont(25f));
    title2.setHorizontalAlignment(PacLabel.CENTER);
    leaderboard.addObject(title2);

    JPanel spacer0 = new JPanel();
    spacer0.setBackground(Color.CYAN.darker().darker());
    spacer0.setBounds(0, 0, 1, 3);
    leaderboard.addSeparator(spacer0);

    PacLabel nextScore = new PacLabel("❰" + String.format("%09d", 69420) + "❱");
    nextScore.setFont(nextScore.getFont().deriveFont(30f));
    nextScore.setHorizontalAlignment(PacLabel.CENTER);
    leaderboard.addObject(nextScore);

    PacLabel nextPlayer = new PacLabel("nextPlayer");
    nextPlayer.setFont(nextPlayer.getFont().deriveFont(20f));
    nextPlayer.setHorizontalAlignment(PacLabel.CENTER);
    leaderboard.addObject(nextPlayer);

    JPanel spacer1 = new JPanel();
    spacer1.setBackground(Color.CYAN.darker().darker());
    spacer1.setBounds(0, 0, 1, 3);
    leaderboard.addSeparator(spacer1);

    PacLabel yourScore = new PacLabel("❰" + String.format("%09d", 100) + "❱");
    yourScore.setFont(yourScore.getFont().deriveFont(30f));
    yourScore.setHorizontalAlignment(PacLabel.CENTER);
    leaderboard.addObject(yourScore);

    PacLabel yourPlayer = new PacLabel("You");
    yourPlayer.setFont(yourPlayer.getFont().deriveFont(20f));
    yourPlayer.setHorizontalAlignment(PacLabel.CENTER);
    leaderboard.addObject(yourPlayer);

    JPanel spacer2 = new JPanel();
    spacer2.setBackground(Color.CYAN.darker().darker());
    spacer2.setBounds(0, 0, 1, 3);
    leaderboard.addSeparator(spacer2);

    PacLabel prevScore = new PacLabel("❰" + String.format("%09d", 64) + "❱");
    prevScore.setFont(prevScore.getFont().deriveFont(30f));
    prevScore.setHorizontalAlignment(PacLabel.CENTER);
    leaderboard.addObject(prevScore);

    PacLabel prevPlayer = new PacLabel("prevPlayer");
    prevPlayer.setFont(prevPlayer.getFont().deriveFont(20f));
    prevPlayer.setHorizontalAlignment(PacLabel.CENTER);
    leaderboard.addObject(prevPlayer);

    JPanel spacer3 = new JPanel();
    spacer3.setBackground(Color.CYAN.darker().darker());
    spacer3.setBounds(0, 0, 1, 3);
    leaderboard.addSeparator(spacer3);

    leaderboard.fitComponents();

    ( (JPanel) uiComponents.get(1) ).add(leaderboard);
    //endregion
  }

  private void createLoadingPopup ()
  {
    int       buffer   = 20;
    Dimension gameSize = uiComponents.get(0).getSize();
    Vector2d  infoPos  = new Vector2d().cartesian(buffer, gameSize.height - 100);

    //create container
    JPanel loadInfo = new JPanel();
    loadInfo.setLayout(null);
    loadInfo.setBounds((int) infoPos.x, (int) infoPos.y, gameSize.width - 2 * buffer, 100 - buffer);
    //add to list
    uiComponents.add(loadInfo);
    //add to Layered Frame
    ( (JLayeredPane) uiComponents.get(0) ).add(loadInfo, Integer.MAX_VALUE);

    //create List for formatting
    PacList loading = new PacList(new Vector2d().cartesian(0, 0), new Dimension(loadInfo.getWidth(), loadInfo.getHeight()));
    loading.showBorder(true);
    loading.alignment = PacList.Alignment.VERTICAL;
    loading.edgeBuffer = 10;
    loading.vBuffer = 10;
    loading.setAutoFit(true);
    loading.setBackGround(Color.BLACK);
    loadInfo.add(loading);

    //region add components
    PacLabel text = new PacLabel("Progress : ");
    text.setFont(text.getFont().deriveFont(20f));
    text.setHorizontalAlignment(PacLabel.CENTER);
    loading.addObject(text);

    FlatProgressBar loadingBar = new FlatProgressBar();
    loadingBar.setIndeterminate(false);
    loadingBar.setBackground(Color.CYAN.darker().darker().darker().darker().darker());
    loadingBar.setForeground(Color.CYAN.darker().darker());
    loadingBar.setUI(new BasicProgressBarUI()
    {
      protected Color getSelectionBackground () { return Color.CYAN; }

      protected Color getSelectionForeground () { return Color.CYAN; }
    });
    loadingBar.setValue(0);
    loadingBar.setStringPainted(true);
    loadingBar.setString("0%");
    loading.addObject(loadingBar);
    //endregion

    loading.fitComponents();

    loading.unifyFontSize(18f);
  }

  private void createReadyPopup ()
  {
    Dimension gameSize = uiComponents.get(0).getSize();

    Dimension readySize = new Dimension(400, 400);
    Vector2d  readyPos  = new Vector2d().cartesian(gameSize.width / 2. - readySize.width / 2., gameSize.height / 2. - readySize.height / 2.);

    PacLabel readyPrompt = new PacLabel(new Vector2d().cartesian((int) readyPos.x, (int) readyPos.y), new Dimension(readySize.width, readySize.height), "<html> <body> <p align=\"center\"> Ready ?<br/>Press a Button! </p> </body> </html>");
    readyPrompt.setFont(readyPrompt.getFont().deriveFont(35f));
    readyPrompt.setHorizontalAlignment(SwingConstants.CENTER);
    readyPrompt.setForeground(Color.CYAN);
    readyPrompt.setBackground(Color.BLACK);
    readyPrompt.setOpaque(true);
    uiComponents.add(readyPrompt);
    ( (JLayeredPane) uiComponents.get(0) ).add(readyPrompt, Integer.MAX_VALUE - 10);
  }
  //endregion

  //region update methods
  private void setLevel (int level)
  {
    ( (PacLabel) data.getItem(2) ).setText(StringUtils.leftPad(String.valueOf(level), 6));
  }

  private void setScore (int score)
  {
    ( (PacLabel) data.getItem(6) ).setText("❰" + String.format("%09d", score) + "❱");
  }

  private void setTime (long millis)
  {
    ( (PacLabel) data.getItem(10) ).setText("%02d:%02d:%02d.%03d".formatted(millis / 3600000, ( millis / 60000 ) % 60, ( millis / 1000 ) % 60, millis % 1000));
  }

  private void setLives (int lives)
  {
    ( (PacLabel) data.getItem(14) ).setText("%7d".formatted(lives));
  }

  private void updateLeaderboard (LeaderboardMenu.LeaderboardEntry[] entries)
  {
    PacList leaderboard = (PacList) uiComponents.get(1);

    for (int i = 0; i < entries.length; i++)
    {
      PacLabel score = (PacLabel) leaderboard.getItem(2 * i + 1);
      PacLabel name  = (PacLabel) leaderboard.getItem(2 * i + 2);

      score.setText("❰" + String.format("%09d", entries[i].highScore()) + "❱");
      name.setText(entries[i].name());
    }
    //    ( (PacLabel) leaderboard.getItem(2) ).setText("❰" + String.format("%09d", score) + "❱");
  }

  private void setLoadingProgress (boolean done, int progress, String text)
  {
    JPanel          panel      = (JPanel) uiComponents.get(2);
    Component[]     components = panel.getComponents();
    PacList         list       = (PacList) components[0];
    PacLabel        label      = (PacLabel) list.getItem(0);
    FlatProgressBar bar        = (FlatProgressBar) list.getItem(1);


    if (done)
    {
      panel.setVisible(false);
      bar.setValue(100);
      bar.setString("Done");
      return;
    }

    label.setText(text);
    bar.setValue(progress);
    bar.setString(progress + "%");
  }

  private void enableReadyPrompt (boolean enable)
  {
    PacLabel readyPrompt = (PacLabel) uiComponents.get(3);
    readyPrompt.setVisible(enable);
  }
  //endregion

  //region game methods
  private void loadGame ()
  {
    Thread t = new Thread(
        () ->
        {
          setLoadingProgress(false, 10, "Creating game environment...");
          env = new GameEnvironment(uiComponents.get(0).getSize());


          setLoadingProgress(false, 20, "Loading map...");
          AdvPacManMap map = new AdvPacManMap(env.getGameState());
          map.calculateAbsolutes(uiComponents.get(0).getSize());

          AdvPacManEntity player1 = new AdvPacManEntity(env.gameState, new Vector2d().cartesian(1, 1).multiply(map.tileSize));
          env.gameState.players.add(player1);
          map.addToPool(player1);

          env.getGameState().layers.get(1).add(map);


          setLoadingProgress(false, 30, "Loading objects...");
          env.getGameState().layers.get(2).addAll(map.generateObjects());


          setLoadingProgress(false, 40, "Loading items...");
          env.getGameState().layers.get(3).addAll(map.generateItems());


          setLoadingProgress(false, 50, "Loading entities...");
          env.getGameState().layers.get(4).addAll(map.generateEntities());


          setLoadingProgress(false, 95, "Finished initializing!");
          ( (JLayeredPane) uiComponents.get(0) ).add(env.getDisplay(), 0);


          setLoadingProgress(true, 100, "Starting game!");
          env.start();
        }
    );

    t.start();
  }

  private void enableControls ()
  {
    bindPlayer(InputListener.Player.playerOne, input ->
    {
      if (input.toDirection() == null)
      {
        joystick.remove(input.key());
        joystick.forEach((key, in) -> env.controlPlayer(1, in.toDirection()));
      }
      else
      {
        joystick.put(input.key(), input);
        env.controlPlayer(1, input.toDirection());
      }
    });
  }
  //endregion
}
