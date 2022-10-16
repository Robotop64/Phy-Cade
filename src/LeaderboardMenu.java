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
import java.util.Comparator;
import java.util.List;

public class LeaderboardMenu extends JPanel
{


  //container of the 9 display slots
  private static final List <pmButton[]> buttonSlots = new ArrayList <>(0);


  //create a Leaderboard record type
  private record LeaderboardEntry(String name, int highscore, LocalTime time, LocalDate date) { }

  //create a List of LeaderboardEntries
  public List <LeaderboardEntry> entries = new ArrayList <>(0);


  public record Leaderboard(String name, List <LeaderboardEntry> entries) { }

  public List <Leaderboard> games = new ArrayList <>(0);


  //constants
  private        int      listener_id;
  private static int      tabOffset = 0;
  private        pmButton header, ybuffer, xbuffer, temp;
  private JLabel label, left, right;
  private final int      entryheight   = 60;
  private       int      currentActive = -1;
  private       int      activeGame    = -1;
  private       String[] gamesTitles   = { "PacMan" };


  /*
   * Defines the Leaderboard GUI
   */
  public LeaderboardMenu ()
  {

    // Initialisation
    setBackground(Color.black);
    setLayout(null);
    createUI();
    createEntrySlots();
    createLeaderboards();
    toggleSelection(0);
    setEntries(0);
    setGame("PacMan");

    activate();
    // end of initialisation

    //examples
    addEntry("PacMan", new LeaderboardEntry("a", 9000, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("b", 8000, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("c", 7000, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("c", 6000, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("c", 5000, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("c", 4000, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("c", 3000, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("c", 2000, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("c", 1000, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("c", 500, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("c", 20000, LocalTime.of(15, 20, 45), LocalDate.now()));
    addEntry("PacMan", new LeaderboardEntry("c", 30000, LocalTime.of(15, 20, 45), LocalDate.now()));


  }

  /*
   * Can be used the create and add a new Leaderboardentry to the database
   * @param Board - which game the entry should be added to (Test,PacMan)
   * @param neu - a leaderboard entry with (Name, Score, Time, Date)
   */
  public void addEntry (String Board, LeaderboardEntry neu)
  {
    getGameLeaderboard(Board).add(neu);
    //sorts the entries by score
    getGameLeaderboard(Board).sort(new Comparator <LeaderboardEntry>()
    {
      @Override
      public int compare (LeaderboardEntry o1, LeaderboardEntry o2)
      {
        return Integer.compare(o2.highscore, o1.highscore);
      }
    });

    setEntries(0);
  }

  /*
   * Used to scroll up or down by 1
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
          if (tabOffset + change + 8 < entries.size())
          {
            tabOffset += change;
            setEntries(tabOffset);
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
        if (tabOffset + change >= 0)
        {
          tabOffset += change;
          setEntries(tabOffset);
        }
        else
        {
          toggleSelection(currentActive);
          toggleSelection(currentActive + change);
        }
      }
    }

  }

  /*
   * Used to move the leaderboard, only useful if more than 2 games available
   * @param change - either -1 or 1
   */
  public void moveGame (int change)
  {
    activeGame += change;
    if (activeGame >= 0 && gamesTitles.length > 1)
    {
      setGame(gamesTitles[activeGame]);
    }
  }

  /*
   * Used to set the game who's Leaderboard should be displayed
   */
  public void setGame (String name)
  {
    activeGame = 0;
    if (games.size() >= 1)
    {
      entries = getGameLeaderboard(name);
      header.setText("Bestenliste : ~ " + name + " ~");
    }
    setEntries(0);
  }

  /*
   * Used to Initialise the existing game Leaderboards
   */
  private void createLeaderboards ()
  {
    List <LeaderboardEntry> pacMan = new ArrayList <>(0);
    games.add(new Leaderboard("PacMan", pacMan));
  }

  /*
   * Used to get the leaderboard of a certain game
   */
  private List <LeaderboardEntry> getGameLeaderboard (String name)
  {
    List <LeaderboardEntry> e = null;
    for (int i = 0; i < games.size(); i++)
    {
      if (games.get(i).name.equals(name))
      {
        e = games.get(i).entries;
      }
    }
    return e;

  }

  /*
   * Creates the various Buttons on the GUI in the Leaderboard
   */
  private void createUI ()
  {
    header = new pmButton("Bestenliste : ~selected game~");
    header.setBounds(20, 20, Gui.frame_width - 40, 60);
    header.setTheme("Leaderboard-GUI");
    header.update();
    add(header);

    label = new JLabel(" Rang:             Name:               Punktestand:            Zeit:           Datum:");
    label.setBounds(20, 85, Gui.frame_width - 40, 50);
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
    right.setBounds(Gui.frame_width - 80, 17, 50, 60);
    right.setHorizontalAlignment(SwingConstants.RIGHT);
    right.setFont(new Font("Comic Sans MS", Font.PLAIN, 32));
    right.setForeground(Color.cyan);
    add(right);

    ybuffer = new pmButton("");
    ybuffer.setBounds(20, 140, Gui.frame_width - 40, 10);
    ybuffer.setTheme("Leaderboard-GUI");
    ybuffer.setHorizontalAlignment(SwingConstants.LEFT);
    ybuffer.update();
    add(ybuffer);

    xbuffer = new pmButton("");
    xbuffer.setBounds(125, 90, 10, Gui.frame_height - 180);
    xbuffer.setTheme("Leaderboard-GUI");
    xbuffer.setHorizontalAlignment(SwingConstants.LEFT);
    xbuffer.update();
    add(xbuffer);

    for (int i = 1; i < 10; i++)
    {
      pmButton temp = new pmButton("");
      temp.setBounds(20, 170 + i * ( entryheight + 25 ) - 10, Gui.frame_width - 40, 3);
      temp.setTheme("Leaderboard-GUI");
      temp.setHorizontalAlignment(SwingConstants.LEFT);
      temp.setBorder(BorderFactory.createLineBorder(new Color(11, 136, 156), 3, true));
      add(temp);
    }

  }

  /*
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
      int        ypos       = ypos0 + i * entryheight + 25 * i;

      //rank
      buttonSlot[0] = createButton("", xpos0, ypos, 110, entryheight, "Leaderboard", SwingConstants.RIGHT);
      //name
      buttonSlot[1] = createButton("", xpos0 + 130, ypos, 280, entryheight, "Leaderboard", SwingConstants.CENTER);
      //highscore
      buttonSlot[2] = createButton("", xpos0 + 430, ypos, 280, entryheight, "Leaderboard", SwingConstants.CENTER);
      //time
      buttonSlot[3] = createButton("", xpos0 + 730, ypos, 170, entryheight, "Leaderboard", SwingConstants.RIGHT);
      //date
      buttonSlot[4] = createButton("", xpos0 + 910, ypos, 230, entryheight, "Leaderboard", SwingConstants.CENTER);

      buttonSlots.add(buttonSlot);

      updateSlot(i);
    }
  }

  /*
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

  /*
   * Used to select and deselect a certain slot
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

  /*
   * Used to update a slot after interacting with it
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

  /*
   * Will enter the needed entries in the display slots
   * @param offset - by how many changes the entries are shifted from rank 1 being in the 0.slot
   */
  private void setEntries (int offset)
  {

    for (int s = 0; s < Math.min(9, entries.size()); s++)
    {

      pmButton[]       buttonSlot = buttonSlots.get(s);
      LeaderboardEntry slotEntry  = entries.get(s + offset);

      for (int e = 0; e < 5; e++)
      {
        buttonSlot[0].setText(entries.indexOf(slotEntry) + 1 + ".  ");
        buttonSlot[1].setText(slotEntry.name);
        buttonSlot[2].setText(String.valueOf(slotEntry.highscore));
        DateTimeFormatter newtime = DateTimeFormatter.ofPattern("HH:mm:ss");
        buttonSlot[3].setText(slotEntry.time.format(newtime));
        DateTimeFormatter newdate = DateTimeFormatter.ofPattern("dd:MM:yy");
        buttonSlot[4].setText(slotEntry.date.format(newdate));
      }
    }
  }

  private void activate ()
  {
    listener_id = InputListener.getInstance().subscribe(input ->
    {
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
}




