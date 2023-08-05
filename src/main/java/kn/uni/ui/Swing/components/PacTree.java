package kn.uni.ui.Swing.components;

import kn.uni.ui.Swing.Style;
import kn.uni.util.Vector2d;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

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
  }

  private void style ()
  {
    tree.setLocation(this.arc/2, this.arc/2);
    tree.setSize(new Dimension(this.getWidth() - this.arc, this.getHeight() - this.arc));
    tree.setBackground(null);
    tree.setOpaque(false);

    this.setBackground(Style.normal.background());
    this.setForeground(Style.normal.foreground());

    tree.setCellRenderer(
        new DefaultTreeCellRenderer(){
          public Color getTextNonSelectionColor() {
            return Style.normal.foreground();
          }
          public Color getBackgroundNonSelectionColor() {
            return Style.normal.background();
          }

          public Color getTextSelectionColor() {
            return Style.focused.foreground();
          }
          public Color getBackgroundSelectionColor() {
            return Style.focused.background();
          }

          public Color getBorderSelectionColor() {
            return Style.focused.foreground();
          }
        }
    );
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
}
