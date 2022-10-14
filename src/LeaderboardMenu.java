import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class LeaderboardMenu extends JPanel {


    //Create and calculate constants
    pmButton header, back, enter, ybuffer, xbuffer;
    JLabel label;
    int entryheight = 60;
    int currentActive = -1;
    public static int tabOffset = 0;
    //container of all created buttons in the 9 slots
    //public static List<pmButton[]> buttonListAll = new ArrayList<>(0);
    //container of the 9 display slots
    public static List<pmButton[]> buttonSlots = new ArrayList<>(0);

    //create a Leaderboard record type
    record LeaderboardEntry(String name, int highscore, LocalTime time, LocalDate date) {
    }

    //create a List of Leaderboard entries
    public static List<LeaderboardEntry> entries = new ArrayList<>(0);

    /*
     * Defines the Leaderboard GUI
     */
    public LeaderboardMenu() {

        setBackground(Color.black);
        setLayout(null);

        createUI();

        createEntrySlots();

        toggleSelection(0);

        setEntries(0);

        //example entry
//        addEntry(new LeaderboardEntry("a", 10, LocalTime.now(), LocalDate.now()));




    }

    /*
     * Can be used the create and add a new Leaderboardentry to the database
     * @param neu - a leaderboard entry
     */
    public void addEntry(LeaderboardEntry neu) {
        entries.add(neu);
        //sorts the entries by score
        entries.sort(new Comparator<LeaderboardEntry>() {
            @Override
            public int compare(LeaderboardEntry o1, LeaderboardEntry o2) {
                return Integer.compare(o2.highscore, o1.highscore);
            }
        });

        setEntries(0);
    }

    /*
     * Used to scroll up or down by 1
     * @param change - 1/0/-1  = down/none/up
     */
    public void moveActive(int change) {
        if (change == 1) {
            //keep scroll in slot bounds (0-8)
            if (currentActive + change >= 0 && currentActive + change <= 8) {
                //normal scroll from top until mid
                if (currentActive + change <= 4) {
                    toggleSelection(currentActive);
                    toggleSelection(currentActive + change);
                    return;
                }
                //entry shift from topmid until botmid
                if (tabOffset + change + 8 < entries.size()) {
                    tabOffset += change;
                    setEntries(tabOffset);
                } else {
                    //normal scroll from bot mid to bot
                    toggleSelection(currentActive);
                    toggleSelection(currentActive + change);
                }
            }
        }
        if (change == -1) {
            //keep scroll in slot bound (0-8)
            if (currentActive + change >= 0 && currentActive + change <= 8) {
                //normal scroll from bot until botmid
                if (currentActive + change >= 4) {
                    toggleSelection(currentActive);
                    toggleSelection(currentActive + change);
                    return;
                }
                //entry shift from botmid until topmid
                if (tabOffset + change >= 0) {
                    tabOffset += change;
                    setEntries(tabOffset);
                } else {
                    toggleSelection(currentActive);
                    toggleSelection(currentActive + change);
                }
            }
        }

    }

    /*
     * Creates the various Buttons on the GUI in the Leaderboard
     */
    private void createUI() {
        header = new pmButton("Leaderboard :");
        header.setBounds(20, 20, Gui.frame_width - 40, 60);
        header.setTheme("Leaderboard-GUI");
        header.update();
        add(header);

        label = new JLabel("Rank:      Name:                           Score:               Time:             Date:");
        label.setBounds(20, 85, Gui.frame_width - 40, 50);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 32));
        label.setForeground(Color.yellow);
        add(label);

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

        back = new pmButton("<| back");
        back.setBounds(20, Gui.frame_height - 80, 120, 50);
        back.setTheme("Leaderboard-GUI");
        back.update();
        add(back);

        enter = new pmButton("go |>");
        enter.setBounds(Gui.frame_width - 20 - 120, Gui.frame_height - 80, 120, 50);
        enter.setTheme("Leaderboard-GUI");
        enter.update();
        add(enter);
    }

    /*
     * Is used to create 9 Slots to display entries
     */
    private void createEntrySlots() {
        int slots = 9;
        int xpos0 = 10;
        int ypos0 = 170;


        for (int i = 0; i < slots; i++) {
            pmButton[] buttonSlot = new pmButton[5];
            int ypos = ypos0 + i * entryheight + 25 * i;

            //rank
            buttonSlot[0] = createButton((i) + ".:", xpos0, ypos, 110, entryheight, "Leaderboard", SwingConstants.RIGHT);
            //name
            buttonSlot[1] = createButton("e" + i, xpos0 + 130, ypos, 280, entryheight, "Leaderboard", SwingConstants.CENTER);
            //highscore
            buttonSlot[2] = createButton("eScore" + i, xpos0 + 430, ypos, 280, entryheight, "Leaderboard", SwingConstants.CENTER);
            //time
            buttonSlot[3] = createButton("eTime" + i, xpos0 + 730, ypos, 170, entryheight, "Leaderboard", SwingConstants.RIGHT);
            //date
            buttonSlot[4] = createButton("eDate" + i, xpos0 + 910, ypos, 230, entryheight, "Leaderboard", SwingConstants.CENTER);

            buttonSlots.add(buttonSlot);

            updateSlot(i);
        }
    }

    /*
     * Create the buttons to display a line of an entry
     */
    private pmButton createButton(String name, int x, int y, int w, int h, String theme, int align) {
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
    private void toggleSelection(int slot) {
        pmButton[] buttonSlot = buttonSlots.get(slot);
        if (slot == currentActive) {
            for (int f = 0; f < 5; f++) {
                buttonSlot[f].isSelected = false;
            }
        } else {
            for (int f = 0; f < 5; f++) {
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
    private void updateSlot(int slot) {
        pmButton[] buttonSlot = buttonSlots.get(slot);
        for (int f = 0; f < 5; f++) {
            buttonSlot[f].update();
            buttonSlot[f].setBorder(BorderFactory.createLineBorder(Color.black, 3, true));
        }
    }

    /*
     * Will enter the needed entries in the display slots
     * @param offset - by how many changes the entries are shifted from rank 1 being in the 0.slot
     */
    private void setEntries(int offset) {

        if (entries.size() <= 9) {
            Random random = new Random();
            for (int i = 0; i < 9; i++) {
                addEntry(new LeaderboardEntry("None", 0, LocalTime.of(0,0,0), LocalDate.of(1111,11,11)));
            }
        }

        for (int s = 0; s <= 8; s++) {

            pmButton[] buttonSlot = buttonSlots.get(s);
            LeaderboardEntry slotEntry = entries.get(s + offset);

            for (int e = 0; e < 5; e++) {
                buttonSlot[0].setText(entries.indexOf(slotEntry) + 1 + ".: ");
                buttonSlot[1].setText(slotEntry.name);
                buttonSlot[2].setText(String.valueOf(slotEntry.highscore));
                DateTimeFormatter newtime = DateTimeFormatter.ofPattern("HH:mm:ss");
                buttonSlot[3].setText(slotEntry.time.format(newtime));
                DateTimeFormatter newdate = DateTimeFormatter.ofPattern("dd:MM:yy");
                buttonSlot[4].setText(slotEntry.date.format(newdate));
            }
        }


    }

}




