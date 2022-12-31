package kn.uni.menus;

import java.util.concurrent.ConcurrentLinkedDeque;

public class Menu
{
  public ConcurrentLinkedDeque <UIObject> elements;

  public Menu ()
  {
    elements = new ConcurrentLinkedDeque <>();
  }
}
