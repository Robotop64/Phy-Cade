import net.java.games.input.Controller;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Gui {

    public static final int frame_width = 1280;
    public static final int frame_height = 1024;
    private static Gui instance;

    JFrame frame;

    private Gui(){

    };

    public void initialize(){
        frame = new JFrame("Pac-Φ");
        frame.setSize(frame_width, frame_height);
        frame.setLocationRelativeTo(null);
        frame.setLocation(2560, 0);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setUndecorated(true);

        frame.getContentPane().add(new MainMenu());

        frame.setVisible(true);

        InputListener inputListener = InputListener.getInstance();
        inputListener.start();

        inputListener.subscribe(System.out::println);
    }

    public static Gui getInstance() {
        if (instance == null) {
            instance = new Gui();
        }

        return instance;
    }
}
