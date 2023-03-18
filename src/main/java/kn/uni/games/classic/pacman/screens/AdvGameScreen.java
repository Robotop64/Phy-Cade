package kn.uni.games.classic.pacman.screens;

import com.formdev.flatlaf.extras.components.FlatProgressBar;
import kn.uni.Gui;
import kn.uni.PacPhi;
import kn.uni.games.classic.pacman.game.entities.Spawner;
import kn.uni.games.classic.pacman.game.internal.GameEnvironment;
import kn.uni.games.classic.pacman.game.internal.physics.AdvCollider;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvTimer;
import kn.uni.games.classic.pacman.game.map.AdvPacManMap;
import kn.uni.games.classic.pacman.game.objects.Blocker;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kn.uni.games.classic.pacman.screens.AdvGameScreen.Components.GAME_HUD;
import static kn.uni.games.classic.pacman.screens.AdvGameScreen.Components.GAME_WINDOW;
import static kn.uni.games.classic.pacman.screens.AdvGameScreen.Components.LOADING_CONTAINER;
import static kn.uni.games.classic.pacman.screens.AdvGameScreen.Components.READY_LABEL;

public class AdvGameScreen extends UIScreen
{
  //components
  public List <Component> uiComponents  = new ArrayList <>();
  public boolean          gameReloading = false;
  //controls
  Map <InputListener.Key, InputListener.Input> joystick = new HashMap <>();
  //hud data lists
  private PacList         data;
  private PacList         leaderboard;
  private GameEnvironment env;
  private boolean         gameStarted = false;


  public AdvGameScreen (JPanel parent)
  {
    super(parent);
    setBackground(Color.BLACK);
    setLayout(null);


    createFrames();
    createHud();

    createLoadingPopup();

    createReadyPopup();

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


    JPanel border = new JPanel();
    border.setBackground(null);
    border.setOpaque(false);
    border.setBorder(BorderFactory.createLineBorder(Color.CYAN.darker().darker(), 2));
    border.setBounds(0, 0, uiComponents.get(GAME_WINDOW.ordinal()).getWidth(), uiComponents.get(GAME_WINDOW.ordinal()).getHeight());

    ( (JLayeredPane) uiComponents.get(GAME_WINDOW.ordinal()) ).add(border);
    ( (JLayeredPane) uiComponents.get(GAME_WINDOW.ordinal()) ).setLayer(border, Integer.MAX_VALUE);
  }

  private void createHud ()
  {
    Dimension hudBounds = uiComponents.get(GAME_HUD.ordinal()).getSize();

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

    PacLabel level = new PacLabel(StringUtils.leftPad(String.valueOf(1), 6));
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

    PacLabel lives = new PacLabel(StringUtils.leftPad(String.valueOf(5), 4));
    lives.setHeader("Lives: x");
    lives.setHorizontalAlignment(PacLabel.LEFT);
    data.addObject(lives);

    data.fitComponents();
    data.unifyFontSize(35f);

    ( (JPanel) uiComponents.get(GAME_HUD.ordinal()) ).add(data);
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
    Dimension gameSize = uiComponents.get(GAME_WINDOW.ordinal()).getSize();
    Vector2d  infoPos  = new Vector2d().cartesian(buffer, gameSize.height - 100);

    //create container
    JPanel loadInfo = new JPanel();
    loadInfo.setLayout(null);
    loadInfo.setBounds((int) infoPos.x, (int) infoPos.y, gameSize.width - 2 * buffer, 100 - buffer);
    //add to list
    uiComponents.add(loadInfo);
    //add to Layered Frame
    ( (JLayeredPane) uiComponents.get(GAME_WINDOW.ordinal()) ).add(loadInfo);
    ( (JLayeredPane) uiComponents.get(GAME_WINDOW.ordinal()) ).setLayer(loadInfo, Integer.MAX_VALUE);

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
    Dimension gameSize = uiComponents.get(GAME_WINDOW.ordinal()).getSize();

    Dimension readySize = new Dimension(300, 150);
    Vector2d  readyPos  = new Vector2d().cartesian(gameSize.width / 2. - readySize.width / 2., gameSize.height / 2. - readySize.height / 2. - 30);

    JPanel readyBackground = new JPanel();
    readyBackground.setBounds(2, 2, gameSize.width - 2, gameSize.height - 2);
    readyBackground.setBackground(new Color(0, 0, 0, 225));
    readyBackground.setLayout(null);
    readyBackground.setBorder(BorderFactory.createLineBorder(Color.CYAN.darker().darker(), 1));
    readyBackground.setOpaque(true);
    readyBackground.setVisible(false);

    PacLabel readyPrompt = new PacLabel(new Vector2d().cartesian((int) readyPos.x, (int) readyPos.y), new Dimension(readySize.width, readySize.height), "<html> <body> <p align=\"center\"> Ready ?<br/>Press START! </p> </body> </html>");
    readyPrompt.setFont(readyPrompt.getFont().deriveFont(35f));
    readyPrompt.setHorizontalAlignment(SwingConstants.CENTER);
    readyPrompt.setForeground(Color.CYAN);
    readyPrompt.setBackground(Color.BLACK);
    readyPrompt.setOpaque(true);
    readyPrompt.setVisible(false);
    uiComponents.add(readyPrompt);

    readyBackground.add(readyPrompt);
    ( (JLayeredPane) uiComponents.get(GAME_WINDOW.ordinal()) ).add(readyBackground);
    ( (JLayeredPane) uiComponents.get(GAME_WINDOW.ordinal()) ).setLayer(readyBackground, Integer.MAX_VALUE - 20);
  }
  //endregion

  //region update methods
  public void setLevel (int level)
  {
    ( (PacLabel) data.getItem(2) ).setText(StringUtils.leftPad(String.valueOf(level), 6));
  }

  public void setScore (int score)
  {
    ( (PacLabel) data.getItem(6) ).setText("❰" + String.format("%09d", score) + "❱");
  }

  public void setTime (long millis)
  {
    ( (PacLabel) data.getItem(10) ).setText("%02d:%02d:%02d.%03d".formatted(millis / 3600000, ( millis / 60000 ) % 60, ( millis / 1000 ) % 60, millis % 1000));
  }

  public void setLives (int lives)
  {
    ( (PacLabel) data.getItem(12) ).setText(StringUtils.leftPad(String.valueOf(lives), 4));
  }

  public void updateLeaderboard (LeaderboardMenu.LeaderboardEntry[] entries)
  {
    PacList leaderboard = (PacList) uiComponents.get(GAME_HUD.ordinal());

    for (int i = 0; i < entries.length; i++)
    {
      PacLabel score = (PacLabel) leaderboard.getItem(2 * i + 1);
      PacLabel name  = (PacLabel) leaderboard.getItem(2 * i + 2);

      score.setText("❰" + String.format("%09d", entries[i].highScore()) + "❱");
      name.setText(entries[i].name());
    }
  }

  public void setLoadingProgress (String state, int progress, String text)
  {
    JPanel          panel      = (JPanel) uiComponents.get(LOADING_CONTAINER.ordinal());
    Component[]     components = panel.getComponents();
    PacList         list       = (PacList) components[0];
    PacLabel        label      = (PacLabel) list.getItem(0);
    FlatProgressBar bar        = (FlatProgressBar) list.getItem(1);


    if (state.equals("done"))
    {
      panel.setVisible(false);
      bar.setValue(100);
      bar.setString("Done");
      return;
    }

    if (state.equals("ready"))
    {
      bar.setVisible(false);
      label.setText(text);
      return;
    }

    label.setText(text);
    bar.setValue(progress);
    bar.setString(progress + "%");
  }

  public void enableReadyPopup (boolean enable)
  {
    PacLabel readyPrompt = (PacLabel) uiComponents.get(READY_LABEL.ordinal());

    Thread countdown = new Thread(() ->
    {
      String[] num = new String[]{ "5", "4", "3", "2", "1", "GO!" };

      for (String s : num)
      {
        readyPrompt.setText("<html> <body> <p align=\"center\"> " + s + " </p> </body> </html>");
        try
        {
          Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
          throw new RuntimeException(e);
        }
      }

      readyPrompt.setVisible(enable);
      readyPrompt.getParent().setVisible(enable);
      env.resumeGame();
      if (!gameReloading)
        env.gameState.gameStartTime = System.nanoTime();
      gameReloading = false;
    });

    if (!enable)
      countdown.start();

    if (enable)
    {
      if (gameReloading)
        readyPrompt.setText("<html> <body> <p align=\"center\"> Ready? </p> </body> </html>");
      readyPrompt.setVisible(true);

      readyPrompt.getParent().setVisible(true);
      //TODO after reloading, the background is not transparent
      //      ( (JPanel) readyPrompt.getParent() ).setOpaque(true);
    }
  }

  public void enableLoadingPopup (boolean enable)
  {
    JPanel panel = (JPanel) uiComponents.get(LOADING_CONTAINER.ordinal());
    panel.setVisible(enable);
    Arrays.stream(panel.getComponents()).forEach(c -> c.setVisible(enable));
    uiComponents.get(GAME_WINDOW.ordinal()).repaint();
  }
  //endregion

  //region game methods
  private void loadGame ()
  {
    Thread t = new Thread(
        () ->
        {
          setLoadingProgress("loading", 10, "Creating game environment...");
          env = new GameEnvironment(uiComponents.get(GAME_WINDOW.ordinal()).getSize());
          env.gameScreen = this;

          ( (JLayeredPane) uiComponents.get(GAME_WINDOW.ordinal()) ).add(env.getDisplay());
          ( (JLayeredPane) uiComponents.get(GAME_WINDOW.ordinal()) ).setLayer(env.getDisplay(), 0);


          setLoadingProgress("loading", 15, "Loading internals...");
          env.gameState.spawn(AdvGameState.Layer.INTERNALS, new AdvTimer(env.gameState));
          env.gameState.spawn(AdvGameState.Layer.PHYSICS, new AdvCollider(env.gameState));


          setLoadingProgress("loading", 20, "Loading map...");
          //TODO : load map from file
          AdvPacManMap map = new AdvPacManMap(env.getGameState());
          //
          map.calculateAbsolutes(uiComponents.get(GAME_WINDOW.ordinal()).getSize());
          env.display.setBounds(
              uiComponents.get(GAME_WINDOW.ordinal()).getSize().width / 2 - map.size.width / 2,
              uiComponents.get(GAME_WINDOW.ordinal()).getSize().height / 2 - map.size.height / 2,
              map.size.width,
              map.size.height);
          map.render();

          map.addToPool(
              new Spawner(
                  "PlayerSpawn", env.getGameState(),
                  new Vector2d().cartesian(14, 23.5),
                  Spawner.SpawnerType.PLAYER,
                  new Vector2d().cartesian(14, 23.5))
          );
          map.addToPool(
              new Spawner(
                  "FruitSpawn", env.getGameState(),
                  new Vector2d().cartesian(14, 23.5),
                  Spawner.SpawnerType.FRUIT,
                  new Vector2d().cartesian(14, 23.5))
          );

          map.addToPool(
              new Blocker(new Vector2d().cartesian(13.5, 12.5), new Dimension((int) ( AdvGameConst.tileSize * 2 ), 3))
          );
          map.addToPool(
              new Blocker(new Vector2d().cartesian(14.5, 12.5), new Dimension((int) ( AdvGameConst.tileSize * 2 ), 3))
          );

          map.scaleSpawnablesPos();

          env.getGameState().layers.get(AdvGameState.Layer.MAP.ordinal()).add(map);


          setLoadingProgress("loading", 30, "Loading objects...");
          env.loadObjects();


          setLoadingProgress("loading", 40, "Loading items...");
          env.loadItems();


          setLoadingProgress("loading", 50, "Loading entities...");
          env.loadEntities();

          env.spawnPlayers();

          setLoadingProgress("loading", 80, "Waiting for Benchmark...");

          while (PacPhi.benchmarkThread.isAlive())
          {
            try
            {
              Thread.sleep(1L);
            }
            catch (InterruptedException e)
            {
              throw new RuntimeException(e);
            }
          }


          setLoadingProgress("loading", 95, "Finished initializing!");


          setLoadingProgress("ready", 100, "Ready to start the Game!");
          enableReadyPopup(true);
          env.pauseGame();
          env.startGame();
        }
    );

    t.start();
  }
  //endregion

  //region input methods
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

      if (input.key() == InputListener.Key.A && ( !gameStarted || gameReloading ))
      {
        gameStarted = true;
        enableLoadingPopup(false);
        enableReadyPopup(false);
      }
    });
  }
  //endregion

  public enum Components
  {
    GAME_WINDOW,
    GAME_HUD,
    LOADING_CONTAINER,
    READY_LABEL
  }
}
