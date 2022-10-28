package kn.uni.ui;

import kn.uni.ui.InputListener.Input;
import kn.uni.ui.InputListener.Player;

import javax.swing.JPanel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UIScreen extends JPanel
{
  private Map <Player, Consumer <Input>> handlers   = new HashMap <>();
  private int                            listenerId = -1;

  public UIScreen (JPanel parent)
  {
    parent.add(this);
  }

  public void bindPlayer (Player p, Consumer <Input> handler)
  {
    if (listenerId < 0)
    {
      listenerId = InputListener.getInstance()
                                .subscribe(input -> handlers.keySet()
                                                            .stream()
                                                            .filter(input.player()::equals)
                                                            .map(handlers::get)
                                                            .forEach(c -> c.accept(input)));
    }
    handlers.put(p, handler);
  }

  public void kill ()
  {
    InputListener.getInstance().unsubscribe(listenerId);
    listenerId = -1;
    setVisible(false);
    getParent().remove(this);
  }

}
