package kn.uni.ui.Swing.components;

import kn.uni.ui.Swing.Style;
import kn.uni.util.Vector2d;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

public class PacTree extends RoundedPanel
{
  public JTree tree;

  public PacTree (Node root, Vector2d position, Dimension size)
  {
    setBounds((int) position.x, (int) position.y, size.width, size.height);
    setLayout(null);
    this.arc = 30;
    this.borderWidth = 3;

    tree = new JTree(root);
    style();
    add(tree);
    revalidate();
  }

  private void style ()
  {
    setBackground(null);
    setForeground(Style.normal.border());
    tree.setLocation(this.arc /3, this.arc/3);
    tree.setSize(new Dimension(this.getWidth() - this.arc*2/3, this.getHeight() - this.arc*2/3));
  }

  public void setFont (Font font)
  {
    if (tree == null) return;
    this.tree.setFont(font);
  }

  public Font getFont ()
  {
    if (tree == null) return null;
    return this.tree.getFont();
  }

  public static class Node extends DefaultMutableTreeNode
  {
    public Node (String name)
    {
      super(name);
    }
  }

  public static class TreeIcon implements Icon
  {

    private static int SIZE = 0;

    public TreeIcon(int size) {
      SIZE = size;
    }


    public int getIconWidth() {
      return SIZE;
    }

    public int getIconHeight() {
      return SIZE;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.setColor(Style.normal.foreground());
      g.fillOval(x, y, SIZE - 1, SIZE - 1);
    }
  }
}
