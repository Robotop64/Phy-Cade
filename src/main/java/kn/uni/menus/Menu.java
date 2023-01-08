package kn.uni.menus;

import kn.uni.menus.objects.UIObject;
import kn.uni.ui.UIScreen;

import javax.swing.JPanel;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Menu extends UIScreen
{
  public ConcurrentLinkedDeque <UIObject> elements;

  public Menu (JPanel parent)
  {
    super(parent);

    elements = new ConcurrentLinkedDeque <>();
  }

}
