package ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class GameOverScreen extends JPanel
{
  private final int           width    = 700;
  private final int           distance = 20;
  private final int           center   = Gui.frameWidth / 2;
  private       List <JLabel> labels   = new ArrayList <>(0);
  private       int           latestY;
  private       int           yOffset  = 0;
  private       int           players  = 1;


  public GameOverScreen (int playerCount, String gameName, List <Integer> scores, List <LocalTime> times)
  {
    setBounds(Gui.defaultFrameBounds);
    setBackground(Color.black);
    setLayout(null);

    players = 1;

    createLabels(gameName);
    createHeader(playerCount);
    //    , List <Integer> scores, List <LocalTime> times

    if (players > 2)
    {
      addPlayerScreen(0, 1);
      addPlayerScreen(1, 1);
      addPlayerScreen(0, 3);
      addPlayerScreen(1, 3);
    }
    if (players == 2)
    {
      yOffset = Gui.frameHeight / 4;
      addPlayerScreen(0, 1);
      addPlayerScreen(1, 1);
    }
    if (players == 1)
    {
      addPlayerScreen(0, 1);
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
    latestY = nextY;

    add(temp);
    labels.add(temp);

    temp.setBounds(0, nextY, Gui.frameWidth, size + size / 5);
    temp.setFont(new Font(font, Font.PLAIN, size));
    //    temp.setBorder(BorderFactory.createLineBorder(c, 3, true));
    temp.setForeground(c);
    temp.setHorizontalAlignment(horizontAlign);
    temp.setVerticalAlignment(verticalAlign);

  }

  private void createHeader (int playerCount)
  {
    if (playerCount == 1)
      createCenterLabel("Dein Ergebnis: ", "Fira Code", 40, SwingConstants.CENTER, SwingConstants.BOTTOM, Color.cyan, distance);
    else
      createCenterLabel("Ergebnisse: ", "Fira Code", 40, SwingConstants.CENTER, SwingConstants.BOTTOM, Color.cyan, distance);
  }

  private void addPlayerScreen (int x, int y)
  {
    int localwidth = 0;
    int xBuffer    = 100;
    if (players >= 2)
    {
      localwidth = Gui.frameWidth / 2;
      xBuffer = 0;
    }
    else
    {
      localwidth = Gui.frameWidth - 2 * xBuffer;
    }
    GameSummaryScreen gameSummaryScreen = new GameSummaryScreen(localwidth);
    gameSummaryScreen.setLocation(( Gui.frameWidth / 2 ) * x + xBuffer, (int) ( Gui.frameHeight - gameSummaryScreen.getHeight() * ( ( 1.0 + y ) / 2 ) - 10 - yOffset ));
    gameSummaryScreen.setBackground(Color.red);
    gameSummaryScreen.addKeyBoard(Gui.frameWidth / 2);
    Gui.getInstance().content.add(gameSummaryScreen);
  }
}
