import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Gui
{

  public static final int       frameWidth         = 1280;
  public static final int       frameHeight        = 1024;
  public static final Rectangle defaultFrameBounds = new Rectangle(0, 0, frameWidth, frameHeight);
  private static      Gui       instance;

  JFrame frame;

  private Gui ()
  {

  }

  public void initialize ()
  {
    frame = new JFrame("Pac-Φ");
    frame.setSize(frameWidth, frameHeight);
    frame.setLocationRelativeTo(null);
    //    frame.setLocation(-frame_width, 0);

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

    //    pos();

    frame.setVisible(true);
  }

  private void pos ()
  {
    frame.setUndecorated(false);
    frame.addComponentListener(new ComponentAdapter()
    {
      @Override
      public void componentMoved (ComponentEvent e)
      {
        super.componentMoved(e);
        System.out.println(e);
      }
    });
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
