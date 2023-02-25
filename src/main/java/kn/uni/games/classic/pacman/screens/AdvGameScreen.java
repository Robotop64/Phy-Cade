package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.components.PacList;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;

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


  public AdvGameScreen (JPanel parent)
  {
    super(parent);
    setBackground(Color.BLACK);
    setLayout(null);

    createFrames();
    createHud();
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
    PacList list = new PacList(new Vector2d().cartesian(5, 5), new Dimension(hudBounds.width - 10, 400 - 10));
    list.alignment = PacList.Alignment.VERTICAL;
    list.vBuffer = 0;
    list.edgeBuffer = 10;
    list.setAutoFit(false);


    PacLabel title = new PacLabel("~ Pac-Man ~");
    title.setHorizontalAlignment(PacLabel.CENTER);
    list.addObject(title);


    list.addBuffer(0, 5);


    PacLabel level = new PacLabel("%7d".formatted(256));
    level.setHeader("Level: ");
    level.setHorizontalAlignment(PacLabel.LEFT);
    list.addObject(level);


    list.addBuffer(0, 5);


    PacLabel score1 = new PacLabel("Score:");
    score1.setHorizontalAlignment(PacLabel.LEFT);
    list.addObject(score1);


    list.addBuffer(0, -15);


    int      sco    = 100;
    PacLabel score2 = new PacLabel("❰" + String.format("%09d", sco) + "❱");
    score2.setHorizontalAlignment(PacLabel.CENTER);
    list.addObject(score2);


    list.addBuffer(0, 5);


    PacLabel time1 = new PacLabel("Time:");
    time1.setHorizontalAlignment(PacLabel.LEFT);
    list.addObject(time1);


    list.addBuffer(0, -15);


    PacLabel time2 = new PacLabel("00:00:20.046");
    time2.setHorizontalAlignment(PacLabel.CENTER);
    list.addObject(time2);


    list.addBuffer(0, 5);


    PacLabel lives = new PacLabel("5");
    lives.setHeader("Lives: x");
    lives.setHorizontalAlignment(PacLabel.LEFT);
    list.addObject(lives);

    list.fitComponents();
    list.unifyFontSize(35f);

    ( (JPanel) uiComponents.get(1) ).add(list);
    //endregion

    //region Dynamic Leaderboard
    PacList list2 = new PacList(new Vector2d().cartesian(10, 680), new Dimension(hudBounds.width - 20, hudBounds.height - 680 - 10));
    list2.alignment = PacList.Alignment.VERTICAL;
    list2.edgeBuffer = 10;
    list2.vBuffer = 0;
    list2.setAutoFit(false);


    PacLabel title2 = new PacLabel("~ Leaderboard ~");
    title2.setFont(title2.getFont().deriveFont(25f));
    title2.setHorizontalAlignment(PacLabel.CENTER);
    list2.addObject(title2);

    JPanel spacer0 = new JPanel();
    spacer0.setBackground(Color.CYAN.darker().darker());
    spacer0.setBounds(0, 0, 1, 3);
    list2.addSeparator(spacer0);

    PacLabel nextScore = new PacLabel("❰" + String.format("%09d", 69420) + "❱");
    nextScore.setFont(nextScore.getFont().deriveFont(30f));
    nextScore.setHorizontalAlignment(PacLabel.CENTER);
    list2.addObject(nextScore);

    PacLabel nextPlayer = new PacLabel("nextPlayer");
    nextPlayer.setFont(nextPlayer.getFont().deriveFont(20f));
    nextPlayer.setHorizontalAlignment(PacLabel.CENTER);
    list2.addObject(nextPlayer);

    JPanel spacer1 = new JPanel();
    spacer1.setBackground(Color.CYAN.darker().darker());
    spacer1.setBounds(0, 0, 1, 3);
    list2.addSeparator(spacer1);

    PacLabel yourScore = new PacLabel("❰" + String.format("%09d", 100) + "❱");
    yourScore.setFont(yourScore.getFont().deriveFont(30f));
    yourScore.setHorizontalAlignment(PacLabel.CENTER);
    list2.addObject(yourScore);

    PacLabel yourPlayer = new PacLabel("You");
    yourPlayer.setFont(yourPlayer.getFont().deriveFont(20f));
    yourPlayer.setHorizontalAlignment(PacLabel.CENTER);
    list2.addObject(yourPlayer);

    JPanel spacer2 = new JPanel();
    spacer2.setBackground(Color.CYAN.darker().darker());
    spacer2.setBounds(0, 0, 1, 3);
    list2.addSeparator(spacer2);

    PacLabel prevScore = new PacLabel("❰" + String.format("%09d", 64) + "❱");
    prevScore.setFont(prevScore.getFont().deriveFont(30f));
    prevScore.setHorizontalAlignment(PacLabel.CENTER);
    list2.addObject(prevScore);

    PacLabel prevPlayer = new PacLabel("prevPlayer");
    prevPlayer.setFont(prevPlayer.getFont().deriveFont(20f));
    prevPlayer.setHorizontalAlignment(PacLabel.CENTER);
    list2.addObject(prevPlayer);

    JPanel spacer3 = new JPanel();
    spacer3.setBackground(Color.CYAN.darker().darker());
    spacer3.setBounds(0, 0, 1, 3);
    list2.addSeparator(spacer3);

    list2.fitComponents();

    ( (JPanel) uiComponents.get(1) ).add(list2);


    //endregion
  }
}
