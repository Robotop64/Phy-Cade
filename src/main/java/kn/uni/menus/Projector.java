package kn.uni.menus;

import kn.uni.Gui;
import kn.uni.ui.UIScreen;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Comparator;

public class Projector extends UIScreen
{
  public ProjectorState state;
  public Menu           selectedMenu;

  public Projector (JPanel parent)
  {
    super(parent);
    setBackground(Color.black.darker().darker().darker().darker());

    state = new ProjectorState();

    startScreen();
  }

  private void startScreen ()
  {
    new Thread(() ->
    {
      state.running = true;
      state.currentTick = 0;
      state.lastTickTime = System.nanoTime();
      while (state.running)
      {
        long t = System.nanoTime();
        if (t - state.lastTickTime < state.tickDuration) continue;
        state.currentTick++;
        state.lastTickTime = t;

        //update UIObject
        selectedMenu.elements.stream()
                             .filter(UIObject -> UIObject instanceof Updating)
                             .forEach(UIObject -> ( (Updating) UIObject ).update());

        //        if (state.currentTick % 2 == 0)
        Gui.getInstance().frame.repaint();
      }
      kill();
    }).start();
  }

  @Override
  protected void paintComponent (Graphics g)
  {
    super.paintComponent(g);
    Graphics2D gg = (Graphics2D) g;
    if (selectedMenu != null && selectedMenu.elements.size() > 0) selectedMenu.elements.stream()
                                                                                       .filter(UIObject -> UIObject instanceof Displayed)
                                                                                       .map(UIObject -> (Displayed) UIObject)
                                                                                       .sorted(Comparator.comparingInt(Displayed::paintLayer))
                                                                                       .forEach(UIObject -> ( UIObject ).paintComponent(gg));
  }

  public void setSelectedMenu (Menu menu)
  {
    selectedMenu = menu;
  }
}




