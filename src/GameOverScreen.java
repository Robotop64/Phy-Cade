import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalTime;

public class GameOverScreen extends JPanel
{
  private final int width = 600;
  private final int dist  = 60;

  public GameOverScreen (String gameName, int score, LocalTime time)
  {
    setBackground(Color.black);
    setLayout(null);
    createLabels(gameName, score, time);
  }

  private void createLabels (String gameName, int score, LocalTime time)
  {
    JLabel title = new JLabel("~ " + gameName + " ~");
    title.setBounds(Gui.frame_width / 2 - width / 2, dist / 6, width, 100);
    //    title.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3, true));
    title.setFont(new Font("Comic Sans MS", Font.PLAIN, 60));
    title.setHorizontalAlignment(SwingConstants.CENTER);
    title.setForeground(Color.cyan);
    add(title);

    JLabel over = new JLabel("GAME OVER");
    over.setBounds(Gui.frame_width / 2 - width / 2, dist * 2, width, 100);
    //    over.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3, true));
    over.setFont(new Font("Comic Sans MS", Font.PLAIN, 100));
    over.setHorizontalAlignment(SwingConstants.CENTER);
    over.setForeground(Color.red);
    add(over);

    JLabel message = new JLabel("Dein Ergebnis");
    message.setBounds(Gui.frame_width / 2 - width / 2, dist * 6, width, 100);
    //    over.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3, true));
    message.setFont(new Font("Comic Sans MS", Font.PLAIN, 40));
    message.setHorizontalAlignment(SwingConstants.CENTER);
    message.setForeground(Color.red);
    add(message);


  }
}
