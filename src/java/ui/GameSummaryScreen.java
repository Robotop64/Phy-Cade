package ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameSummaryScreen extends JPanel
{
  public static final double        ratio  = 5 / 4.0;
  private             List <JLabel> labels = new ArrayList <>();
  private             int           totalPlayers;
  private             int           thisPlayer;
  private             int           thisScore;
  private             LocalTime     thisTime;
  private             int           width;

  public GameSummaryScreen (int width, int totalPlayers, int thisPlayer, int thisScore, LocalTime thisTime)
  {
    setBackground(Color.black);
    setLayout(null);
    setSize(width, (int) ( width / ratio ) - 150);

    this.thisPlayer = thisPlayer;
    this.totalPlayers = totalPlayers;
    this.thisScore = thisScore;
    this.thisTime = thisTime;
    this.width = width;

    createElements(thisPlayer);

  }

  public void addKeyBoard (int width)
  {
    JLabel boardText = new JLabel(" PX: ");
    boardText.setFont(new Font("Fira Code", Font.PLAIN, 24));
    boardText.setForeground(Color.cyan.darker());
    boardText.setBackground(Color.black);
    boardText.setSize(width - 60, 50);
    boardText.setLocation(this.getX() + 30, this.getY() + 20);
    boardText.setBorder(BorderFactory.createLineBorder(Color.cyan.darker(), 3, true));
    Gui.getInstance().content.add(boardText);

    OnScreenKeyboard o = new OnScreenKeyboard(width - 60);
    o.setLocation(this.getX() + 30, this.getY() + ( this.getHeight() - o.getHeight() ) - 10);
    System.out.println(this.getY());
    o.setTarget(System.out::println);
    Gui.getInstance().content.add(o);
  }

  private void createElements (int player)
  {
    int fontSize   = width / 30;
    int rowDist    = fontSize / 2;
    int xBuffer    = 20;
    int textWidth  = (int) ( width / 3.6 );
    int textHeight = textWidth / 3;
    int labelPosX  = this.getX() + textWidth / 3;
    int numberPosX = this.getX() + labelPosX + textWidth + 20;

    Dimension buttonDim = new Dimension(textWidth + textHeight + 50, textHeight);

    JLabel p = createLabel(
        " Player " + player + " :",
        fontSize,
        Font.BOLD,
        this.getX() + xBuffer,
        this.getY() + rowDist,
        new Dimension(textWidth, textHeight),
        SwingConstants.LEFT);
    //underline the playerLabel
    Map attributes = p.getFont().getAttributes();
    attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    p.setFont(p.getFont().deriveFont(attributes));

    createLabel(
        " Punktzahl : ",
        fontSize,
        Font.BOLD,
        labelPosX,
        this.getY() + textHeight + 2 * rowDist,
        new Dimension(textWidth, textHeight),
        SwingConstants.LEFT);

    createLabel(
        String.valueOf(thisScore),
        fontSize + 10,
        Font.PLAIN,
        numberPosX,
        this.getY() + textHeight + 2 * rowDist,
        new Dimension(textWidth * 2, textHeight),
        SwingConstants.CENTER);

    createLabel(
        " Spielzeit : ",
        fontSize,
        Font.BOLD,
        labelPosX,
        this.getY() + textHeight * 2 + 3 * rowDist,
        new Dimension(textWidth, textHeight),
        SwingConstants.LEFT);

    createLabel(
        String.valueOf(thisTime),
        fontSize + 10,
        Font.PLAIN,
        numberPosX,
        this.getY() + textHeight * 2 + 3 * rowDist,
        new Dimension(textWidth * 2, textHeight),
        SwingConstants.CENTER);

    createLabel(
        " Weiteres : ",
        fontSize,
        Font.BOLD,
        labelPosX,
        this.getY() + textHeight * 3 + 4 * rowDist,
        new Dimension(textWidth, textHeight),
        SwingConstants.LEFT);

    createLabel(
        "",
        fontSize + 10,
        Font.PLAIN,
        numberPosX,
        this.getY() + textHeight * 3 + 4 * rowDist,
        new Dimension(textWidth * 2, textHeight),
        SwingConstants.CENTER);

    createActionButton(
        "Speichern",
        () -> addKeyBoard(width),
        fontSize,
        this.getX() + 20,
        this.getY() + this.getHeight() - buttonDim.height - 20,
        buttonDim);

    createActionButton(
        "Hauptmenü",
        null,
        fontSize,
        this.width - buttonDim.width - 20,
        this.getY() + this.getHeight() - buttonDim.height - 20,
        buttonDim);
  }

  private JLabel createLabel (String text, int fontSize, int fontStyle, int x, int y, Dimension dim, int align)
  {
    JLabel temp = new JLabel(text);
    temp.setSize(dim);
    temp.setLocation(x, y);
    temp.setFont(new Font("Fira Code", fontStyle, fontSize));
    //    temp.setBorder(BorderFactory.createLineBorder(Color.cyan.darker(), 3, true));
    temp.setForeground(Color.cyan.darker());
    temp.setHorizontalAlignment(align);
    labels.add(temp);
    add(temp);
    return temp;
  }

  private void createActionButton (String text, Runnable action, int fontSize, int x, int y, Dimension dim)
  {
    pmButton temp = new pmButton(text);
    temp.addAction(action);
    temp.setFontSize(fontSize);
    temp.setSize(dim);
    temp.setLocation(x, y);
    temp.isSelected = false;
    temp.update();
    labels.add(temp);
    add(temp);
  }


}
