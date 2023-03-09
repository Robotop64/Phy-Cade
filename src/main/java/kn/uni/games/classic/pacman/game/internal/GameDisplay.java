package kn.uni.games.classic.pacman.game.internal;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class GameDisplay extends JPanel
{
  BufferedImage finalImg;

  public GameDisplay (Dimension dim)
  {
    setBounds(0, 0, dim.width, dim.height);
    setLayout(null);
    setOpaque(false);
    setBackground(null);
    setBorder(BorderFactory.createLineBorder(Color.cyan.darker().darker(), 2));
  }

  public void setFinalImg (BufferedImage finalImg)
  {
    this.finalImg = finalImg;
  }

  @Override
  public void paintComponent (Graphics g)
  {
    super.paintComponent(g);

    g.drawImage(finalImg, 0, 0, null);
  }


}
