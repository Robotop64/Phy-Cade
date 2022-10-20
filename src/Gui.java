import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Rectangle;

public class Gui
{

  public static final int       frameWidth         = 1280;
  public static final int       frameHeight        = 1024;
  public static final Rectangle defaultFrameBounds = new Rectangle(0, 0, frameWidth, frameHeight);
  private static      Gui       instance;

  JFrame frame;

  private Gui () {}

  public void initialize ()
  {
    frame = new JFrame("Pac-Φ");
    frame.setSize(frameWidth, frameHeight);
    frame.setLocationRelativeTo(null);
    //    frame.setLocation(-frameWidth, 0);

    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setUndecorated(true);
    frame.getContentPane().setBackground(Color.black);
    frame.getContentPane().setLayout(null);

    InputListener inputListener = InputListener.getInstance();
    inputListener.start();

    inputListener.subscribe(System.out::println);

    frame.getContentPane().add(MainMenu.getInstance());
    MainMenu.getInstance().setBounds(defaultFrameBounds);


    pmButton debug = new pmButton("");
    debug.setForeground(Color.red);
    debug.setBorder(null);
    debug.setFontSize(14);
    frame.getContentPane().add(debug);
    debug.setBounds(1, frameHeight - 55, 400, 40);
    inputListener.subscribe(input -> debug.setText(input.toString()));


    frame.getContentPane().setComponentZOrder(debug, 0);
    frame.getContentPane().setComponentZOrder(MainMenu.getInstance(), 1);

    frame.setVisible(true);
  }

  public static Gui getInstance ()
  {
    if (instance == null)
    {
      instance = new Gui();
    }

    return instance;
  }
}
