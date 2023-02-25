package kn.uni.games.classic.pacman.screens;

import kn.uni.ui.UIScreen;

import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class AdvGameScreen extends UIScreen
{
  public final Dimension gameSize = new Dimension(1000, 1000);

  public List <Component> uiComponents = new ArrayList <>();


  public AdvGameScreen (JPanel parent)
  {
    super(parent);

    createUI();
  }

  private void createUI ()
  {
    JLayeredPane gameWindow = new JLayeredPane();
    gameWindow.setBounds(15, 15, gameSize.width, gameSize.height);
    gameWindow.setBorder(BorderFactory.createLineBorder(Color.cyan.darker().darker(), 5));
    add(gameWindow);

  }
}
