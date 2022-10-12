import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;

public class pmButton extends JLabel {

    public boolean isSelected;

    public pmButton(String text){
        super(text);
        setBorder(BorderFactory.createLineBorder(Color.yellow, 3, true));
        setForeground(Color.yellow);
        setBackground(Color.black);
        setHorizontalAlignment(CENTER);
        setFont(new Font("Comic Sans MS", Font.PLAIN, 32));

    }

    public void update(){
        Color color = isSelected ? Color.cyan : Color.yellow;
        setBorder(BorderFactory.createLineBorder(color, 3, true));
        setForeground(color);
    }

}


