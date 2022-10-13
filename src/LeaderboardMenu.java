import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardMenu extends JPanel {

    pmButton entry, header, back, enter;

    int entrywidth = Gui.frame_width-80;
    int entryheight = 60;
    int x = (Gui.frame_width - entrywidth) / 2;

    Color guiColor = Color.blue;

    public static List<LeaderboardEntry> entries = new ArrayList<>(1);

    public LeaderboardMenu(){

        setBackground(Color.black);
        setLayout(null);

        header = new pmButton("Leaderboard :");
        header.setBounds(20,20,Gui.frame_width-40, 60);
        header.setForeground(guiColor);
        header.setBorder(BorderFactory.createLineBorder(guiColor, 3, true));
        add(header);

        back = new pmButton("<| back");
        back.setBounds(20,Gui.frame_height-80,120, 50);
        back.setForeground(guiColor);
        back.setBorder(BorderFactory.createLineBorder(guiColor, 3, true));
        add(back);

        enter = new pmButton("go |>");
        enter.setBounds(Gui.frame_width-20-120,Gui.frame_height-80,120, 50);
        enter.setForeground(guiColor);
        enter.setBorder(BorderFactory.createLineBorder(guiColor, 3, true));
        add(enter);


        LeaderboardEntry a = new LeaderboardEntry("a", 9000, new SimpleDateFormat("2022-10-13"));
        entries.add(a);
        LeaderboardEntry b = new LeaderboardEntry("b", 7000, new SimpleDateFormat("2022-10-13"));
        entries.add(b);
        LeaderboardEntry c = new LeaderboardEntry("c", 4000, new SimpleDateFormat("2022-10-13"));
        entries.add(c);

        entries.sort(new Comparator<LeaderboardEntry>() {
            @Override
            public int compare(LeaderboardEntry o1, LeaderboardEntry o2) {
                return Integer.compare(o2.highscore, o1.highscore);
            }
        });

        for(int i=0;i<entries.size();i++){
            LeaderboardEntry e =entries.get(i);
            String text = (i+1)+".: "+ e.name + " | " + e.highscore + " | " + e.date;
            pmButton temp = new pmButton(text);
            temp.setBounds(40,80+i*entryheight, entrywidth, entryheight);
            add(temp);

        }


    }

    record LeaderboardEntry(String name, int highscore, SimpleDateFormat date){
    }




}
