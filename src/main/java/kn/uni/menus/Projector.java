package kn.uni.menus;

import kn.uni.ui.InputListener;
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

    bindPlayer(InputListener.Player.playerOne, input ->
    {

    });

  }

  private void startScreen ()
  {

  }

  @Override
  protected void paintComponent (Graphics g)
  {
    super.paintComponent(g);
    Graphics2D gg = (Graphics2D) g;
    selectedMenu.elements.stream()
                         .filter(UIObject -> UIObject instanceof Displayed)
                         .map(UIObject -> (Displayed) UIObject)
                         .sorted(Comparator.comparingInt(Displayed::paintLayer))
                         .forEach(UIObject -> ( UIObject ).paintComponent(gg));
  }
}




