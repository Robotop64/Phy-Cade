package kn.uni.menus.G2D;

import kn.uni.menus.G2D.objects.UIObject;
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

  public UIObject getElement (int index)
  {
    return elements.stream().toList().get(index);
  }
}
