package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.ui.InputListener;
import kn.uni.ui.UIScreen;
import kn.uni.ui.pmButton;
import kn.uni.util.Util;

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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GameSummaryPanel extends UIScreen
{
  private             int                  listener_id;
  public static final double               ratio     = 5 / 4.0;
  private             List <JLabel>        labels    = new ArrayList <>();
  private             int                  totalPlayers;
  private             int                  thisScore;
  private             LocalTime            thisTime;
  private             int                  level;
  private             int                  width;
  private             int                  activeKey = 0;
  private             List <pmButton>      buttons   = new ArrayList <>();
  private             GameOverScreen       container;
  private             InputListener.Player player;

  public GameSummaryPanel (GameOverScreen container, int width, int totalPlayers, InputListener.Player player, int thisScore, LocalTime thisTime, int thisLevel)
  {
    super(Gui.getInstance().content);
    setBackground(Color.black);
    setLayout(null);
    setSize(width, (int) ( width / ratio ) - 150);

    this.container = container;
    this.player = player;
    this.totalPlayers = totalPlayers;
    this.thisScore = thisScore;
    this.thisTime = thisTime;
    this.level = thisLevel;
    this.width = width;

    //TODO bind
    //   bindPlayer();

    createElements(player);

    activate();
  }

  public void addKeyBoard (int width)
  {
    //disables the inputListener of the SummaryPanel
    muteSummary();

    //setup name display
    {
      JLabel boardText = new JLabel(" PX: ");
      boardText.setFont(new Font("Fira Code", Font.PLAIN, 24));
      boardText.setForeground(Color.cyan.darker());
      boardText.setBackground(Color.black);
      boardText.setSize(width - 60, 50);
      boardText.setLocation(this.getX() + 30, this.getY() + 20);
      boardText.setBorder(BorderFactory.createLineBorder(Color.cyan.darker(), 3, true));
      add(boardText);
    }

    //create Keyboard
    {
      String           name = "";
      OnScreenKeyboard o    = new OnScreenKeyboard(this, width - 60);
      o.setLocation(this.getX() + 30, this.getY() + ( this.getHeight() - o.getHeight() ) - 10);

      //      o.setTarget();
      Gui.getInstance().content.add(o);
    }
  }

  //TODO level label
  private void createElements (InputListener.Player player)
  {
    int fontSize   = width / 30;
    int rowDist    = fontSize / 2;
    int xBuffer    = 20;
    int textWidth  = (int) ( width / 3.6 );
    int textHeight = textWidth / 3;
    int labelPosX  = this.getX() + textWidth / 3;
    int numberPosX = this.getX() + labelPosX + textWidth + 20;

    Dimension buttonDim = new Dimension(textWidth + textHeight + 50, textHeight);

    int playerNum = 0;
    if (player.name().equals("playerOne")) playerNum = 1;
    if (player.name().equals("playerTwo")) playerNum = 2;
    if (player.name().equals("playerThree")) playerNum = 3;
    if (player.name().equals("playerFour")) playerNum = 4;

    JLabel p = createLabel(
        " Player " + playerNum + " :",
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
        " Level : ",
        fontSize,
        Font.BOLD,
        labelPosX,
        this.getY() + textHeight + 2 * rowDist,
        new Dimension(textWidth, textHeight),
        SwingConstants.LEFT);

    createLabel(
        String.valueOf(level),
        fontSize + 10,
        Font.PLAIN,
        numberPosX,
        this.getY() + textHeight + 2 * rowDist,
        new Dimension(textWidth * 2, textHeight),
        SwingConstants.CENTER);

    createLabel(
        " Punktzahl : ",
        fontSize,
        Font.BOLD,
        labelPosX,
        this.getY() + textHeight * 2 + 3 * rowDist,
        new Dimension(textWidth, textHeight),
        SwingConstants.LEFT);

    createLabel(
        String.valueOf(thisScore),
        fontSize + 10,
        Font.PLAIN,
        numberPosX,
        this.getY() + textHeight * 2 + 3 * rowDist,
        new Dimension(textWidth * 2, textHeight),
        SwingConstants.CENTER);

    createLabel(
        " Spielzeit : ",
        fontSize,
        Font.BOLD,
        labelPosX,
        this.getY() + textHeight * 3 + 4 * rowDist,
        new Dimension(textWidth, textHeight),
        SwingConstants.LEFT);

    createLabel(
        String.valueOf(thisTime),
        fontSize + 10,
        Font.PLAIN,
        numberPosX,
        this.getY() + textHeight * 3 + 4 * rowDist,
        new Dimension(textWidth * 2, textHeight),
        SwingConstants.CENTER);

    createLabel(
        " Weiteres : ",
        fontSize,
        Font.BOLD,
        labelPosX,
        this.getY() + textHeight * 4 + 5 * rowDist,
        new Dimension(textWidth, textHeight),
        SwingConstants.LEFT);

    createLabel(
        "",
        fontSize + 10,
        Font.PLAIN,
        numberPosX,
        this.getY() + textHeight * 4 + 5 * rowDist,
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
        this::killSummary,
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
    buttons.add(temp);
    add(temp);
  }

  private void setActiveKey (int index)
  {
    buttons.get(activeKey).isSelected = false;
    buttons.get(activeKey).update();
    activeKey = index;
    buttons.get(index).isSelected = true;
    buttons.get(index).update();
  }

  private void muteSummary ()
  {
    InputListener.getInstance().unsubscribe(listener_id);
  }

  private void killSummary ()
  {
    //TODO kill() doesnt remove the listener
    //    kill();
    //removes the GameSummaryPanel
    InputListener.getInstance().unsubscribe(listener_id);
    setVisible(false);
    getParent().remove(this);
    //used to remove the GameOverScreen, if all players dismissed the SummaryPanel
    container.children.remove(this);
    container.removeSummary();
  }

  /**
   * Used to activate the input Listener
   */
  public void activate ()
  {
    listener_id = InputListener.getInstance().subscribe(input ->
    {
      //filter inputs to match player assigned to this SummaryPanel
      if (!input.player().equals(player)) return;
      //press a button
      if (input.equals(new InputListener.Input(InputListener.Key.A, InputListener.State.down, InputListener.Player.playerOne)))
      {
        buttons.get(activeKey).press();
      }
      //only allow joystick beyond this
      if (!Arrays.asList(InputListener.Key.vertical, InputListener.Key.horizontal)
                 .contains(input.key())) return;
      //joystick inputs
      int delta = switch (input.state())
          {
            case up -> -1;
            case down -> 1;
            case none -> 0;
          };
      if (input.key().name().equals("horizontal"))
      {
        setActiveKey(Util.bounded(activeKey + delta, 0, 1));
      }

    });
    setVisible(true);
  }
}
