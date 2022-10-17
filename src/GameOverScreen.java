import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalTime;

public class GameOverScreen extends JPanel
{
  private JLabel title, over, text, score, time;
  private int overWidth  = 600;
  private int titleWidth = 600;

  public GameOverScreen (String gameName, int score, LocalTime time)
  {
    setBackground(Color.black);
    setLayout(null);
    createLabels(gameName, score, time);
  }

  private void createLabels (String gameName, int score, LocalTime time)
  {
    title = new JLabel(gameName);
    title.setBounds(Gui.frame_width / 2 - titleWidth / 2, 10, titleWidth, 100);
    //    title.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3, true));
    title.setFont(new Font("Comic Sans MS", Font.PLAIN, 60));
    title.setHorizontalAlignment(SwingConstants.CENTER);
    add(title);

    over = new JLabel("GAME OVER");
    over.setBounds(Gui.frame_width / 2 - overWidth / 2, 120, overWidth, 100);
    //    over.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3, true));
    over.setFont(new Font("Comic Sans MS", Font.PLAIN, 100));
    over.setHorizontalAlignment(SwingConstants.CENTER);
    add(over);


  }
}
