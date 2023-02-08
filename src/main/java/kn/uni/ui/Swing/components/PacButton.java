package kn.uni.ui.Swing.components;

import com.formdev.flatlaf.extras.components.FlatButton;
import kn.uni.ui.Swing.Style;
import kn.uni.util.Vector2d;

import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashSet;
import java.util.Set;

public class PacButton extends FlatButton
{

  public  boolean        isSelected;
  public  boolean        isFocused;
  @SuppressWarnings("FieldMayBeFinal")
  private Set <Runnable> actions = new HashSet <>();

  public PacButton (Vector2d position, Dimension size, String text)
  {
    super();
    this.setBounds((int) position.x, (int) position.y, size.width, size.height);
    this.setText(text);
    useColorSet(Style.normal);

    addListeners();
  }

  public void useColorSet (Style.ColorSet colorSet)
  {
    this.setOutline(colorSet.border());
    this.setBackground(colorSet.background());
    this.setForeground(colorSet.foreground());
  }

  public void setSelected (boolean selected)
  {
    isSelected = selected;
    Style.ColorSet set = isSelected ? Style.selected : Style.normal;
    useColorSet(set);
  }

  public void setFocused (boolean focused)
  {
    isFocused = focused;
    Style.ColorSet set = isFocused ? Style.focused : Style.normal;
    useColorSet(set);
    if (isFocused)
      this.grabFocus();
  }

  public void press ()
  {
    actions.forEach(Runnable::run);
  }

  @SuppressWarnings("unused")
  public void removeAction (Runnable a)
  {
    actions.remove(a);
  }

  public void clearActions ()
  {
    actions.clear();
  }

  public void addAction (Runnable a)
  {
    actions.add(a);
  }

  private void addListeners ()
  {
    PacButton self = this;
    addFocusListener(new FocusAdapter()
    {
      @Override
      public void focusGained (FocusEvent e)
      {
        setFocused(true);
      }

      @Override
      public void focusLost (FocusEvent e)
      {
        setFocused(false);
      }
    });

    addActionListener(e -> press());
  }

}