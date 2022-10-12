import javax.swing.JPanel;
import java.awt.Color;

public class MainMenu extends JPanel {

    pmButton spButton, mpButton, settingsButton, lbButton;

    int w = 500;
    int x = (Gui.frame_width - w) / 2;

    int h = 120;
    int n_offsets = 32;
    int n_buttons = 4;
    int buffer = (Gui.frame_height - (n_buttons * h)) / n_offsets;
    int y = (n_offsets - n_buttons) / 2 * buffer;

    public MainMenu(){

        setBackground(Color.black);

        setLayout(null);
        spButton = new pmButton("EIN SPIELER");
        spButton.setBounds(x, y, w, h);
        add(spButton);

        mpButton = new pmButton("ZWEI SPIELER");
        mpButton.setBounds(x, y + (h + buffer), w, h);
        add(mpButton);

        lbButton = new pmButton("BESTENLISTE");
        lbButton.setBounds(x, y + 2 *(h + buffer), w, h);
        add(lbButton);

        settingsButton = new pmButton("EINSTELLUNGEN");
        settingsButton.setBounds(x, y + 3 * (h + buffer), w, h);
        add(settingsButton);

        spButton.isSelected = true;
        spButton.update();

    }

}
