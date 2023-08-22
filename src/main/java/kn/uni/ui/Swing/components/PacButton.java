package kn.uni.ui.Swing.components;

import com.formdev.flatlaf.extras.components.FlatButton;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.interfaces.Colored;
import kn.uni.util.Vector2d;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class PacButton extends FlatButton implements Colored
{

  public  boolean        isSelected;
  public  boolean        isFocused;
  public int buffer = 0;
  @SuppressWarnings("FieldMayBeFinal")
  private Set <Runnable> actions = new HashSet <>();

  public PacButton (Vector2d position, Dimension size, String text)
  {
    super();
    this.setBounds((int) position.x, (int) position.y, size.width, size.height);
    this.setText(text);
    this.setFocusable(false);
    useColorSet(Style.normal);

    addListeners();
    putClientProperty( "JButton.buttonType", "roundRect" );
  }

  public PacButton (String text)
  {
    this.setText(text);
    this.setFocusable(false);
    useColorSet(Style.normal);

    addListeners();
    putClientProperty( "JButton.buttonType", "roundRect" );
  }

  public void useColorSet (Style.ColorSet colorSet)
  {
    Colored.super.useColorSet(colorSet);
    this.setOutline(colorSet.border());
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
  }

  public void setBuffer (int buffer)
  {
    this.buffer = buffer;
    this.setText(getText());
  }

  public void setText (String text)
  {
    super.setText(" ".repeat(Math.max(0, buffer))+ text+" ".repeat(Math.max(0, buffer)));
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
    addActionListener(e -> press());

    addMouseListener(new MouseAdapter()
                     {
                       @Override
                       public void mouseEntered (MouseEvent e)
                       {
                         if (getParent().getParent().getParent().getParent() instanceof PacList list)
                         {
                           list.selectItem(list.getIndexOf(PacButton.this));
                         }
                         setFocused(true);
                       }

                       @Override
                       public void mouseExited (MouseEvent e)
                       {
                         setFocused(false);
                       }
                     }
    );

  }

}