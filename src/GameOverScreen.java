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
  private final int           width   = 700;
  private final int           dist    = 20;
  private final int           centerL = Gui.frameWidth / 2 - width / 2;
  private       List <JLabel> labels  = new ArrayList <>(0);

  public GameOverScreen (int playerCount, String gameName, int score, LocalTime time)
  {
    setBounds(0, 0, Gui.frameWidth, Gui.frameHeight);
    setBackground(Color.black);
    setLayout(null);


    System.out.println(labels.size());
    createLabels(gameName, score, time);
    createPlayerLabel(playerCount, gameName, score, time);

  }

  private void createLabels (String gameName, int score, LocalTime time)
  {
    createCenterLabel("~ " + gameName + " ~", "Fira Code", 60, SwingConstants.CENTER, SwingConstants.BOTTOM, Color.orange, dist);
    createCenterLabel("GAME OVER", "Fira Code", 100, SwingConstants.CENTER, SwingConstants.BOTTOM, Color.red, dist);
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
    //    temp.setBorder(BorderFactory.createLineBorder(c, 3, true));
    temp.setForeground(c);
    temp.setHorizontalAlignment(horizontAlign);
    temp.setVerticalAlignment(verticalAlign);

  }

  private void createPlayerLabel (int playerCount, String gameName, int score, LocalTime time)
  {
    if (playerCount == 1)
      createCenterLabel("Dein Ergebnis: ", "Fira Code", 40, SwingConstants.CENTER, SwingConstants.BOTTOM, Color.cyan, dist * 3);
    else
      createCenterLabel("Ergebnisse: ", "Fira Code", 40, SwingConstants.CENTER, SwingConstants.BOTTOM, Color.cyan, dist * 3);

    //    if (singleplayer) else


  }
}
