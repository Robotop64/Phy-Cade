package kn.uni.games.classic.pacman.game.internal;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.stream.IntStream;

public class GameDisplay extends JPanel
{
  List <GameLayer> layers;

  public GameDisplay (Dimension dim)
  {
    setBounds(0, 0, dim.width, dim.height);
    setLayout(null);
    setOpaque(false);
    setBackground(null);
    setBorder(BorderFactory.createLineBorder(Color.cyan.darker().darker(), 2));
  }

  public void setLayers (List <GameLayer> layers)
  {
    this.layers = layers;
  }

  @Override
  public void paintComponent (Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    IntStream.range(0, 6).forEach(i ->
    {
      this.layers.get(i).paintComponent(g2);
    });
  }


}
