package ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;

public class GameSummaryScreen extends JPanel
{
  public static final double ratio = 5 / 4.0;

  public GameSummaryScreen (int width)
  {
    setBackground(Color.black);
    setLayout(null);
    setSize(width, (int) ( width / ratio ) - 150);


  }

  public void addKeyBoard (int width)
  {
    JLabel boardText = new JLabel(" PX: ");
    boardText.setFont(new Font("Fira Code", Font.PLAIN, 24));
    boardText.setForeground(Color.cyan.darker());
    boardText.setSize(width - 60, 50);
    boardText.setLocation(this.getX() + 30, this.getY() + 20);
    boardText.setBorder(BorderFactory.createLineBorder(Color.cyan.darker(), 3, true));
    Gui.getInstance().content.add(boardText);

    OnScreenKeyboard o = new OnScreenKeyboard(width - 60);
    o.setLocation(this.getX() + 30, this.getY() + ( this.getHeight() - o.getHeight() ));
    System.out.println(this.getY());
    o.setTarget(System.out::println);
    Gui.getInstance().content.add(o);
  }

}
