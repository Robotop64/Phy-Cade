package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.ui.InputListener;
import kn.uni.ui.InputListener.Player;
import kn.uni.ui.UIScreen;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class GameOverScreen extends UIScreen
{
  private final int             distance = 20;
  private final List <JLabel>   labels   = new ArrayList <>(0);
  public        List <UIScreen> children;
  public        String          gameName;
  private       int             yOffset  = 0;


  public GameOverScreen (int players, String gameName, List <Integer> scores, List <LocalTime> times, List <Integer> levels)
  {
    super(Gui.getInstance().content);
    setBounds(Gui.defaultFrameBounds);
    setBackground(Color.black);
    setLayout(null);

    children = new ArrayList <>();
    this.gameName = gameName;

    createLabels(gameName);
    createHeader(players);

    if (players > 2)
    {
      addPlayerScreen(0, 3, players, Player.playerOne, scores.get(0), times.get(0), levels.get(0));
      addPlayerScreen(1, 3, players, Player.playerTwo, scores.get(1), times.get(1), levels.get(1));
      //      addPlayerScreen(0, 1, players, 3, scores.get(2), times.get(2));
      //      addPlayerScreen(1, 1, players, 4, scores.get(3), times.get(3));
    }
    if (players == 2)
    {
      yOffset = Gui.frameHeight / 4;
      addPlayerScreen(0, 1, players, InputListener.Player.playerOne, scores.get(0), times.get(0), levels.get(0));
      addPlayerScreen(1, 1, players, InputListener.Player.playerTwo, scores.get(1), times.get(1), levels.get(1));
    }
    if (players == 1)
    {
      addPlayerScreen(0, 1, players, InputListener.Player.playerOne, scores.get(0), times.get(0), levels.get(0));
    }

  }

  private void createLabels (String gameName)
  {
    createCenterLabel("~ " + gameName + " ~", "Fira Code", 60, SwingConstants.CENTER, SwingConstants.BOTTOM, Color.orange, distance);
    createCenterLabel("GAME OVER", "Fira Code", 100, SwingConstants.CENTER, SwingConstants.BOTTOM, Color.red, distance);


  }

  private void createCenterLabel (String text, String font, int size, int horizontAlign, int verticalAlign, Color c, int distance)
  {
    JLabel temp  = new JLabel(text);
    int    nextY = 10;
    if (labels.size() != 0)
    {
      nextY = (int) ( labels.get(labels.size() - 1).getY() + labels.get(labels.size() - 1).getSize().getHeight() ) + distance;
    }

    add(temp);
    labels.add(temp);

    temp.setBounds(0, nextY, Gui.frameWidth, size + size / 5);
    temp.setFont(new Font(font, Font.PLAIN, size));
    temp.setForeground(c);
    temp.setHorizontalAlignment(horizontAlign);
    temp.setVerticalAlignment(verticalAlign);
  }

  private void createHeader (int playerCount)
  {
    if (playerCount == 1) createCenterLabel("Dein Ergebnis: ", "Fira Code", 40, SwingConstants.CENTER, SwingConstants.BOTTOM, Color.cyan, distance);
    else createCenterLabel("Eure Ergebnisse: ", "Fira Code", 40, SwingConstants.CENTER, SwingConstants.BOTTOM, Color.cyan, distance);
  }

  private void addPlayerScreen (int x, int y, int totalPlayers, InputListener.Player player, int thisScore, LocalTime thisTime, int thisLevel)
  {
    int localWidth;
    int xBuffer = 100;

    if (totalPlayers >= 2)
    {
      localWidth = Gui.frameWidth / 2 - 10;
      xBuffer = 0;
    }
    else
    {
      localWidth = Gui.frameWidth - 2 * xBuffer - 10;
    }
    //create GameSummaryPanel
    GameSummaryPanel gameSummaryPanel = new GameSummaryPanel(this, localWidth, totalPlayers, player, thisScore, thisTime, thisLevel);
    gameSummaryPanel.setLocation(( Gui.frameWidth / 2 ) * x + xBuffer + 5, (int) ( Gui.frameHeight - gameSummaryPanel.getHeight() * ( ( 1.0 + y ) / 2 ) - 10 - yOffset + 5 - y * 5 ));
    gameSummaryPanel.setBackground(Color.black);
    gameSummaryPanel.setBorder(BorderFactory.createLineBorder(Color.cyan.darker(), 3, true));
    //    gameSummaryScreen.addKeyBoard(localWidth);
    Gui.getInstance().content.add(gameSummaryPanel);
    children.add(gameSummaryPanel);
  }

  public void removeSummary ()
  {
    if (children.size() == 0)
    {
      setVisible(false);
      getParent().remove(this);
      Gui.getInstance().content.add(MainMenu.getInstance());
      MainMenu.getInstance().setBounds(Gui.defaultFrameBounds);
      MainMenu.getInstance().activate();
    }
  }

}
