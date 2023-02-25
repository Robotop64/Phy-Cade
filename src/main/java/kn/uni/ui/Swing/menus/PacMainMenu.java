package kn.uni.ui.Swing.menus;

import javax.swing.JPanel;
import java.awt.Color;

import static kn.uni.Gui.defaultFrameBounds;

public class PacMainMenu extends JPanel
{

  public PacMainMenu (JPanel parent)
  {
    super();
    setLayout(null);
    setBounds(defaultFrameBounds);
    setBackground(Color.black);
    setVisible(true);

    addComponents();

  }

  public void addComponents ()
  {
    //title
    //    PacLabel title = new PacLabel(
    //        new Vector2d().cartesian(390, 100),
    //        new Dimension(500, 120),
    //        "~ PAC - MAN ~");
    //    title.setFont(title.getFont().deriveFont(54f));
    //    title.setBorder(null);
    //    title.useColorSet(Style.focused);
    //    title.setHorizontalAlignment(PacLabel.CENTER);
    //    add(title);
    //

    //buttons
    //    PacList list = new PacList(
    //        new Vector2d().cartesian(390, 217),
    //        new Dimension(500, 120 * 5 + 4 * 9));
    //    list.vBuffer = 9;
    //    list.alignment = PacList.Alignment.VERTICAL;
    //
    //
    //    PacButton sp = new PacButton("EIN SPIELER");
    //    list.addObject(sp);
    //
    //    PacButton mp = new PacButton("ZWEI SPIELER");
    //    list.addObject(mp);
    //
    //    PacButton best = new PacButton("BESTENLISTE");
    //    list.addObject(best);
    //
    //    PacButton options = new PacButton("EINSTELLUNGEN");
    //    list.addObject(options);
    //
    //    PacButton sound = new PacButton("TON - AN");
    //    sound.addAction(() -> sound.setText(sound.getText().equals("TON - AN") ? "TON - AUS" : "TON - AN"));
    //    list.addObject(sound);
    //
    //    list.unifyFontSize(54f);

    //    add(list);
  }
}
