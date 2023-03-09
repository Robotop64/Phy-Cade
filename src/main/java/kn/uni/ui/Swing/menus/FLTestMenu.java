package kn.uni.ui.Swing.menus;

import kn.uni.ui.Swing.components.PacLabel;
import kn.uni.ui.Swing.components.PacList;
import kn.uni.ui.UIScreen;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

import static kn.uni.Gui.defaultFrameBounds;

public class FLTestMenu extends UIScreen
{

  public FLTestMenu (JPanel parent)
  {
    super(parent);
    setLayout(null);
    setBounds(defaultFrameBounds);
    setBackground(Color.black);
    setVisible(true);

    addComponents();

  }

  public void addComponents ()
  {
    //    PacList list = new PacList(new Vector2d().cartesian(20, 20), new Dimension(200, 200));
    //    list.showBorder(true);
    //    list.alignment = PacList.Alignment.VERTICAL;
    //    list.vBuffer = 0;
    //    list.edgeBuffer = 10;
    //    list.setAutoFit(false);
    //    list.setBackGround(Color.red);
    //
    //
    //    PacLabel label = new PacLabel("TEST");
    //    label.setBounds(0, 0, 100, 10);
    //    list.addObject(label);
    //
    //
    //    //    PacButton button = new PacButton("FIRST");
    //    //    button.setBounds(0, 0, 100, 10);
    //    //    list.addObject(button);
    //    //
    //    //    list.addBuffer(0, 10);
    //    //
    //    //
    //    //    PacButton button2 = new PacButton("SECOND");
    //    //    button2.setBounds(0, 0, 100, 50);
    //    //    list.addObject(button2);
    //    //
    //    //    list.addBuffer(0, 5);
    //    //    JPanel panel = new JPanel();
    //    //    panel.setBounds(0, 0, 100, 3);
    //    //    panel.setBackground(Color.red);
    //    //    panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
    //    //    list.addSeparator(panel);
    //    //    list.addBuffer(0, 5);
    //    //
    //    //    PacButton button3 = new PacButton("THIRD");
    //    //    button3.setBounds(0, 0, 100, 100);
    //    //    list.addObject(button3);
    //    //
    //    //    list.addBuffer(0, 10);
    //
    //    list.fitComponents();
    //
    //    list.unifyFontSize(20f);
    //
    //    add(list);

    PacList loading = new PacList(new Vector2d().cartesian(20, 20), new Dimension(200, 200));
    loading.showBorder(true);
    loading.alignment = PacList.Alignment.VERTICAL;
    loading.vBuffer = 0;
    loading.edgeBuffer = 10;
    loading.setAutoFit(false);
    loading.setBackGround(Color.red);
    add(loading);

    PacLabel text = new PacLabel("Progress : ");
    text.setFont(text.getFont().deriveFont(20f));
    text.setBounds(0, 0, 100, 10);
    text.setHorizontalAlignment(PacLabel.LEFT);
    loading.addObject(text);
    //
    //    FlatProgressBar loadingBar = new FlatProgressBar();
    //    loadingBar.setValue(50);
    //    loadingBar.setStringPainted(true);
    //    loadingBar.setString("0%");
    //    loading.add(loadingBar);
    //
    loading.fitComponents();

    loading.unifyFontSize(20f);


  }
}
