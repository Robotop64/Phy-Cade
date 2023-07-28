package kn.uni;

import kn.uni.ui.InputListener;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.menus.PacMainMenu;
import kn.uni.util.PrettyPrint;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Objects;

public class Gui
{

  public static final int       frameWidth         = 1280;
  public static final int       frameHeight        = 1024;
  public static final Rectangle defaultFrameBounds = new Rectangle(0, 0, frameWidth, frameHeight);
  private static      Gui       instance;

  public JFrame frame;
  public JPanel content;
  PacLabel debug;

  private Gui () { }

  public static Gui getInstance ()
  {
    if (instance == null)
    {
      instance = new Gui();
    }

    return instance;
  }

  public void initialize ()
  {
    PrettyPrint.startGroup(PrettyPrint.Type.Message, "Init GUI");
    frame = new JFrame("Pac-Φ");
    frame.setSize(frameWidth, frameHeight);
    frame.setLocation(0, 0);
    frame.setLocationRelativeTo(null);
    //    frame.setLocation(-frameWidth, 0);
    PrettyPrint.bullet("Frame created:" + frameWidth + "x" + frameHeight + " @" + "(" + frame.getLocation().x + "," + frame.getLocation().y + ")");

    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setUndecorated(true);
    frame.getContentPane().setBackground(Color.black);
    frame.getContentPane().setLayout(null);

    InputListener inputListener = InputListener.getInstance();
    inputListener.start();
    PrettyPrint.bullet("InputListener started");
    PrettyPrint.empty();


    content = new JPanel();
    frame.getContentPane().add(content);
    content.setBounds(defaultFrameBounds);
    content.setLayout(null);
    content.setBackground(Color.BLACK);

    //create debug panel
    debug = new PacLabel(new Vector2d().cartesian(5, frameHeight - 45), new Dimension(350, 40), "");
    debug.useColorSet(new Style.ColorSet(Color.RED, null, null));
    debug.setFontSize(14);

    if (Objects.equals(Config.getCurrent("Debugging/-/Enabled"), true))
      debug.setVisible(true);
    else
      debug.setVisible(false);

    PrettyPrint.bullet("Hooked Debug Panel to InputListener");
    inputListener.subscribe(input -> debug.setText(input.toString()));
    frame.getContentPane().add(debug);

    //adjust content order
    frame.getContentPane().setComponentZOrder(debug, 0);
    frame.getContentPane().setComponentZOrder(content, 1);
    PrettyPrint.endGroup();

    PrettyPrint.announce("Finished Initialization!");
    PrettyPrint.empty();
    PrettyPrint.announce("Starting Game");

    PacMainMenu mainMenu = PacMainMenu.getInstance(content);
    mainMenu.setBounds(defaultFrameBounds);
    content.add(mainMenu);

    //    frame.getGraphicsConfiguration().getDevice().setFullScreenWindow(frame);
    //    frame.setAlwaysOnTop(true);

    frame.setVisible(true);
    frame.createBufferStrategy(3);
  }
}
