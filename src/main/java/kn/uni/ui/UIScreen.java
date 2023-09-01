package kn.uni.ui;

import kn.uni.Gui;
import kn.uni.ui.InputListener.Input;
import kn.uni.ui.InputListener.Player;
import kn.uni.ui.Swing.interfaces.Controllable;

import javax.swing.JPanel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UIScreen extends JPanel implements Controllable
{
  private Map <Player, Consumer <Input>> handlers   = new HashMap <>();
  public int                            listenerId = -1;
  public JPanel parent;

  public UIScreen (JPanel parent)
  {
    parent.add(this);
    this.parent = parent;
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
    getParent().remove(this);
    Gui.getInstance().frame.repaint();
  }

  @Override
  public void enableControls (){}

  public void disableControls ()
  {
    InputListener.getInstance().unsubscribe(listenerId);
    listenerId = -1;
  }

}
