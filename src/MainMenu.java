import javax.swing.JPanel;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class MainMenu extends JPanel {

    pmButton spButton, mpButton, settingsButton, lbButton, soundButton;
    pmButton title_label;

    int w = 500;
    int x = (Gui.frame_width - w) / 2;

    int h = 120;
    int n_offsets = 32;
    int n_buttons = 5;
    int buffer = (Gui.frame_height - (n_buttons * h)) / n_offsets;
    int y = (n_offsets - n_buttons) / 2 * buffer + 100;

    int selected_index = 0;
    int listener_id;

    List<pmButton> buttons;

    public MainMenu(){

        setBackground(Color.black);
        setLayout(null);

        title_label = new pmButton("~ PAC - MAN ~");
        title_label.setBounds(x, 100, w, h);
        title_label.setFontSize(54);
        title_label.setBorder(null);
        add(title_label);

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

        soundButton = new pmButton("TON - AN");
        soundButton.setBounds(x, y + 4 * (h + buffer), w, h);
        add(soundButton);

        //Kai schmidt-brauns

        buttons = Arrays.asList(spButton, mpButton, lbButton, settingsButton, soundButton);

        select(0);

        listener_id = InputListener.getInstance().subscribe(input -> {
            if (input.player().equals(InputListener.Player.player_two)) return;
            if (!Arrays.asList(InputListener.Key.vertical, InputListener.Key.horizontal)
                    .contains(input.key())) return;
            int delta = switch (input.state()){
                case up -> -1;
                case down -> 1;
                case none -> 0;
            };
            select(Util.bounded(selected_index + delta, 0, n_buttons-1));
        });

        buttons.forEach(b -> b.addAction(() -> {
            InputListener.getInstance().unsubscribe(listener_id);
            setVisible(false);
        }));

        InputListener.getInstance().subscribe(input -> {
            if (input.equals(new InputListener.Input(InputListener.Key.A, InputListener.State.down, InputListener.Player.player_one)))
                buttons.get(selected_index).press();
        });
    }

    public void select(int index){
        selected_index = index;
        buttons.forEach(b -> b.isSelected = false);
        buttons.get(index).isSelected = true;
        buttons.forEach(pmButton::update);
    }

}
