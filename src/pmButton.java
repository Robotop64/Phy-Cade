import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;
import java.util.Set;

public class pmButton extends JLabel {

    public boolean isSelected;

    private Set<Runnable> actions = new HashSet<>();

    public pmButton(String text){
        super(text);
        setBorder(BorderFactory.createLineBorder(Color.yellow, 3, true));
        setForeground(Color.yellow);
        setBackground(Color.black);
        setHorizontalAlignment(CENTER);
        setFont(new Font("Comic Sans MS", Font.PLAIN, 32));

    }

    public void setFontSize(int size){
        setFont(new Font("Comic Sans MS", Font.PLAIN, size));
    }

    public void update(){
        Color color = isSelected ? Color.yellow : Color.cyan.darker();
        setBorder(BorderFactory.createLineBorder(color, 3, true));
        setForeground(color);
    }

    public void press(){
        actions.forEach(Runnable::run);
    }

    public void removeAction(Runnable a){
        actions.remove(a);
    }

    public void addAction(Runnable a){
        actions.add(a);
    }

    public void setTheme(String theme){
        switch (theme) {
            case "Normal" -> {
                this.setForeground(Color.yellow);
                this.setBackground(Color.black);
                this.setFont(new Font("Comic Sans MS", Font.PLAIN, 32));
            }
            case "Leaderboard" -> {
                this.setForeground(Color.cyan);
                this.setBackground(Color.black);
                this.setFont(new Font("Comic Sans MS", Font.PLAIN, 32));
                setBorder(BorderFactory.createLineBorder(Color.black, 3, true));
            }
            case "Leaderboard-GUI" -> {
                this.setForeground(Color.cyan);
                this.setBackground(Color.black);
                this.setFont(new Font("Comic Sans MS", Font.PLAIN, 32));
                this.setBorder(BorderFactory.createLineBorder(Color.cyan, 3, true));
            }
            default -> {
            }
        }
    }

}


