package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.persistence.PacmanDatabaseProvider;
import kn.uni.ui.InputListener;
import kn.uni.ui.pmButton;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardMenu extends JPanel
{
  private static LeaderboardMenu instance;

  //container of the 9 display slots
  private final List <pmButton[]> buttonSlots = new ArrayList <>();
  private final int      entryHeight   = 60;
  public List <LeaderboardEntry> entries = new ArrayList <>();
  public Map <GameTitle, Leaderboard> games = new HashMap <>();
  private       int      listener_id;
  //  public List <Leaderboard>      games = new ArrayList <>();
  private       int      tableOffset   = 0;
  private       pmButton header;
  private       pmButton ybuffer;
  private       pmButton xbuffer;
  private       JLabel   label;
  private       JLabel   left;
  private       JLabel   right;
  private       int      currentActive = -1;
  private       int      activeGame    = 0;
  private GameTitle newActiveGame = GameTitle.pacman;
  /**
   * constructor for the leaderboard menu
   */
  private LeaderboardMenu ()
  {
    // Initialisation
    setBackground(Color.black);
    setLayout(null);
    createUI();
    createEntrySlots();

    loadDatabase();
    //set default slot to active
    toggleSelection(0);
    //loads the first entries into the slots
    setEntries(0);
    //set default game to active
    setGame(GameTitle.pacman);

    // end of initialisation
  }

  /**
   * turns this menu into a singleton
   *
   * @return the instance of this menu
   */
  public static LeaderboardMenu getInstance ()
  {
    if (instance == null)
    {
      instance = new LeaderboardMenu();
    }
    return instance;
  }

  /**
   * Used to load the database entries into the loaded leaderboards
   */
  private void loadDatabase ()
  {
    games.put(GameTitle.pacman, new Leaderboard("pacman", PacmanDatabaseProvider.getEntries("pacman")));
    games.put(GameTitle.supermario, new Leaderboard("supermario", PacmanDatabaseProvider.getEntries("supermario")));
  }

  /**
   * Used to scroll up or down by 1
   *
   * @param change - 1/0/-1  = down/none/up
   */
  public void moveActive (int change)
  {
    if (change == 1)
    {
      if (entries.size() - 1 > currentActive)
      {
        //keep scroll in slot bounds (0-8)
        if (currentActive + change >= 0 && currentActive + change <= 8)
        {
          //normal scroll from top until mid
          if (currentActive + change <= 4)
          {
            toggleSelection(currentActive);
            toggleSelection(currentActive + change);
            return;
          }
          //entry shift from topmid until botmid
          if (tableOffset + change + 8 < entries.size())
          {
            tableOffset += change;
            setEntries(tableOffset);
          }
          else
          {
            //normal scroll from bot mid to bot
            toggleSelection(currentActive);
            toggleSelection(currentActive + change);
          }
        }
      }
    }
    if (change == -1)
    {
      //keep scroll in slot bound (0-8)
      if (currentActive + change >= 0 && currentActive + change <= 8)
      {
        //normal scroll from bot until botmid
        if (currentActive + change >= 4)
        {
          toggleSelection(currentActive);
          toggleSelection(currentActive + change);
          return;
        }
        //entry shift from botmid until topmid
        if (tableOffset + change >= 0)
        {
          tableOffset += change;
          setEntries(tableOffset);
        }
        else
        {
          toggleSelection(currentActive);
          toggleSelection(currentActive + change);
        }
      }
    }

  }

  /**
   * Used to move the leaderboard, only useful if more than 2 games available
   *
   * @param change - either -1 or 1
   */
  private void moveGame (int change)
  {

    if (activeGame >= 0 && GameTitle.values().length > 1)
    {
      if (change == 0) return;

      List <GameTitle> titleList = Arrays.stream(GameTitle.values()).toList();
      int              index     = titleList.indexOf(newActiveGame);
      if (change == -1) change = titleList.size() - 1;
      GameTitle next = titleList.get(( index + change ) % titleList.size());
      newActiveGame = next;

      if (currentActive != 0)
      {
        toggleSelection(currentActive);
        tableOffset = 0;
        toggleSelection(0);
      }

      setGame(newActiveGame);

    }
  }

  /**
   * Used to set the game who's Leaderboard should be displayed
   */
  private void setGame (GameTitle g)
  {
    if (games.size() >= 1)
    {
      entries = getGameLeaderboard(g);
      header.setText("Bestenliste : ~ " + g.name() + " ~");
    }
    setEntries(0);

  }

  /**
   * Used to get the leaderboard of a certain game
   */
  private List <LeaderboardEntry> getGameLeaderboard (GameTitle g)
  {
    List <LeaderboardEntry> e = new ArrayList <>();

    if (games.get(g) == null) return e;

    e = games.get(g).entries;

    return e;

  }

  /**
   * Creates the various Buttons on the GUI in the Leaderboard
   */
  private void createUI ()
  {
    header = new pmButton("Bestenliste : ~selected game~");
    header.setBounds(20, 20, Gui.frameWidth - 40, 60);
    header.setTheme("Leaderboard-GUI");
    header.update();
    add(header);

    label = new JLabel(" Rang:             Name:               Punktestand:            Zeit:           Datum:");
    label.setBounds(20, 85, Gui.frameWidth - 40, 50);
    label.setHorizontalAlignment(SwingConstants.LEFT);
    label.setFont(new Font("Comic Sans MS", Font.PLAIN, 32));
    label.setForeground(Color.yellow);
    add(label);

    left = new JLabel(" < |");
    left.setBounds(30, 17, 50, 60);
    left.setHorizontalAlignment(SwingConstants.LEFT);
    left.setFont(new Font("Comic Sans MS", Font.PLAIN, 32));
    left.setForeground(Color.cyan);
    add(left);

    right = new JLabel("| > ");
    right.setBounds(Gui.frameWidth - 80, 17, 50, 60);
    right.setHorizontalAlignment(SwingConstants.RIGHT);
    right.setFont(new Font("Comic Sans MS", Font.PLAIN, 32));
    right.setForeground(Color.cyan);
    add(right);

    ybuffer = new pmButton("");
    ybuffer.setBounds(20, 140, Gui.frameWidth - 40, 10);
    ybuffer.setTheme("Leaderboard-GUI");
    ybuffer.setHorizontalAlignment(SwingConstants.LEFT);
    ybuffer.update();
    add(ybuffer);

    xbuffer = new pmButton("");
    xbuffer.setBounds(125, 90, 10, Gui.frameHeight - 180);
    xbuffer.setTheme("Leaderboard-GUI");
    xbuffer.setHorizontalAlignment(SwingConstants.LEFT);
    xbuffer.update();
    add(xbuffer);

    for (int i = 1; i < 10; i++)
    {
      pmButton temp = new pmButton("");
      temp.setBounds(20, 170 + i * ( entryHeight + 25 ) - 10, Gui.frameWidth - 40, 3);
      temp.setTheme("Leaderboard-GUI");
      temp.setHorizontalAlignment(SwingConstants.LEFT);
      temp.setBorder(BorderFactory.createLineBorder(new Color(11, 136, 156), 3, true));
      add(temp);
    }

  }

  /**
   * Is used to create 9 Slots to display entries
   */
  private void createEntrySlots ()
  {
    int slots = 9;
    int xpos0 = 10;
    int ypos0 = 170;


    for (int i = 0; i < slots; i++)
    {
      pmButton[] buttonSlot = new pmButton[5];
      int        ypos       = ypos0 + i * entryHeight + 25 * i;

      //rank
      buttonSlot[0] = createButton("0", xpos0, ypos, 110, entryHeight, "Leaderboard", SwingConstants.RIGHT);
      //name
      buttonSlot[1] = createButton("", xpos0 + 130, ypos, 280, entryHeight, "Leaderboard", SwingConstants.CENTER);
      //highscore
      buttonSlot[2] = createButton("", xpos0 + 430, ypos, 280, entryHeight, "Leaderboard", SwingConstants.CENTER);
      //time
      buttonSlot[3] = createButton("", xpos0 + 730, ypos, 170, entryHeight, "Leaderboard", SwingConstants.RIGHT);
      //date
      buttonSlot[4] = createButton("", xpos0 + 910, ypos, 230, entryHeight, "Leaderboard", SwingConstants.CENTER);

      buttonSlots.add(buttonSlot);

      updateSlot(i);
    }
  }

  /**
   * Create the buttons to display a line of an entry
   */
  private pmButton createButton (String name, int x, int y, int w, int h, String theme, int align)
  {
    pmButton temp = new pmButton(name);
    temp.setBounds(x, y, w, h);
    temp.setTheme(theme);
    temp.setHorizontalAlignment(align);
    add(temp);
    return temp;
  }

  /**
   * Used to select and deselect a certain slot
   *
   * @param slot - which of the 9 slots (0-8) should be interacted with
   */
  private void toggleSelection (int slot)
  {
    pmButton[] buttonSlot = buttonSlots.get(slot);
    if (slot == currentActive)
    {
      for (int f = 0; f < 5; f++)
      {
        buttonSlot[f].isSelected = false;
      }
    }
    else
    {
      for (int f = 0; f < 5; f++)
      {
        buttonSlot[f].isSelected = true;
      }
      currentActive = slot;
    }
    updateSlot(slot);
  }

  /**
   * Used to update a slot after interacting with it
   *
   * @param slot - which of the 9 slots (0-8) should be interacted with
   */
  private void updateSlot (int slot)
  {
    pmButton[] buttonSlot = buttonSlots.get(slot);
    for (int f = 0; f < 5; f++)
    {
      buttonSlot[f].update();
      buttonSlot[f].setBorder(BorderFactory.createLineBorder(Color.black, 3, true));
    }
  }

  /**
   * Will enter the needed entries in the display slots
   *
   * @param offset - by how many changes the entries are shifted from rank 1 being in the 0.slot
   */
  private void setEntries (int offset)
  {
    if (entries.size() <= 9)
    {
      for (int s = 0; s < 9; s++)
      {

        pmButton[] buttonSlot = buttonSlots.get(s);

        for (int e = 0; e < 5; e++)
        {
          buttonSlot[0].setText("");
          buttonSlot[1].setText("");
          buttonSlot[2].setText("");
          DateTimeFormatter newtime = DateTimeFormatter.ofPattern("HH:mm:ss");
          buttonSlot[3].setText("");
          DateTimeFormatter newdate = DateTimeFormatter.ofPattern("dd.MM.yy");
          buttonSlot[4].setText("");
        }
      }
    }


    for (int s = 0; s < Math.min(9, entries.size()); s++)
    {

      pmButton[]       buttonSlot = buttonSlots.get(s);
      LeaderboardEntry slotEntry  = entries.get(s + offset);

      for (int e = 0; e < 5; e++)
      {
        buttonSlot[0].setText(entries.indexOf(slotEntry) + 1 + ".  ");
        buttonSlot[1].setText(slotEntry.name);
        buttonSlot[2].setText(String.valueOf(slotEntry.highScore));
        DateTimeFormatter newtime = DateTimeFormatter.ofPattern("HH:mm:ss");
        buttonSlot[3].setText(slotEntry.time.format(newtime));
        DateTimeFormatter newdate = DateTimeFormatter.ofPattern("dd.MM.yy");
        buttonSlot[4].setText(slotEntry.date.format(newdate));
      }
    }
  }

  /**
   * Used to activate the input Listener
   */
  public void activate ()
  {
    listener_id = InputListener.getInstance().subscribe(input ->
    {
      if (input.equals(new InputListener.Input(InputListener.Key.B, InputListener.State.down, InputListener.Player.playerOne)))
      {
        InputListener.getInstance().unsubscribe(listener_id);
        setVisible(false);
        getParent().remove(this);
        Gui.getInstance().content.add(MainMenu.getInstance());
        MainMenu.getInstance().setBounds(Gui.defaultFrameBounds);
        MainMenu.getInstance().activate();
      }

      if (input.player().equals(InputListener.Player.playerTwo)) return;
      if (!Arrays.asList(InputListener.Key.vertical, InputListener.Key.horizontal)
                 .contains(input.key())) return;
      int delta = switch (input.state())
          {
            case up -> -1;
            case down -> 1;
            case none -> 0;
          };

      if (input.key().name().equals("horizontal")) moveGame(delta);
      if (input.key().name().equals("vertical")) moveActive(delta);
    });
    setVisible(true);
  }

  private enum GameTitle
  { pacman, supermario, doom }

  public record LeaderboardEntry(int accNum, String name, int level, long highScore, LocalTime time, LocalDate date, String version, String notes) { }

  public record Leaderboard(String name, List <LeaderboardEntry> entries) { }
}




