package kn.uni.menus;


import java.awt.Graphics2D;

public interface Displayed
{
  /**
   * how to render this object
   *
   * @param g graphics context
   */
  void paintComponent (Graphics2D g);

  /**
   * Layer the object is painted on
   *
   * @return a value between 0 and Integer.MAX_VALUE
   */
  int paintLayer ();
}
