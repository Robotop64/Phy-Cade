package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.components.PacList;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;
import org.apache.commons.lang3.StringUtils;

import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class AdvGameScreen extends UIScreen
{
  public List <Component> uiComponents = new ArrayList <>();

  private PacList data;
  private PacList leaderboard;


  public AdvGameScreen (JPanel parent)
  {
    super(parent);
    setBackground(Color.BLACK);
    setLayout(null);

    createFrames();
    createHud();

    setScore(69420);
    setLevel(5);

    Thread t = new Thread(
        () ->
        {
          for (int i = 0; i < 1000000; i++)
          {
            setTime(i * 100L);
            setScore(i * 100);
            setLevel(i);
            try
            {
              Thread.sleep(1);
            }
            catch (InterruptedException e)
            {
              e.printStackTrace();
            }
          }
        }
    );
    t.start();

    setTime(100005);
  }

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

  private void updateLeaderboard (int score)
  {
    ( (PacLabel) leaderboard.getItem(2) ).setText("❰" + String.format("%09d", score) + "❱");
  }
}
