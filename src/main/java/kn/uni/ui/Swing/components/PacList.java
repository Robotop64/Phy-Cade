package kn.uni.ui.Swing.components;

import kn.uni.ui.Swing.Style;
import kn.uni.util.Vector2d;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class PacList extends JPanel
{

  public Alignment alignment  = Alignment.VERTICAL;
  public int       hBuffer    = 5;
  public int       vBuffer    = 5;
  public int       edgeBuffer = 5;
  public int       borderSize = 1;
  public boolean   showBorder = false;
  public boolean   autoFit    = true;
  public boolean selectionRollAround = true;

  public  ArrayList <Component> collection;
  public  JScrollPane           scrollPane;
  public  JPanel                viewPane;
  public  Style.ColorSet        currentColorSet;
  public  Component             selected;
  private ArrayList <Component> buffers;

  public PacList (Vector2d position, Dimension size)
  {
    super();
    setBounds((int) position.x, (int) position.y, size.width, size.height);
    setLayout(null);
    setBackground(null);

    collection = new ArrayList <>();
    buffers = new ArrayList <>();
    viewPane = new JPanel();
    viewPane.setLayout(null);
    viewPane.setBounds(0, 0, getWidth(), getHeight());

    scrollPane = new JScrollPane(viewPane, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBounds(0, 0, getWidth(), getHeight());

    scrollPane.setBorder(null);
    add(scrollPane);

    useColorSet(Style.normal);
  }

  //region add/remove/clear
  public void addObject (Component object)
  {
    collection.add(object);
    viewPane.add(object);

    if (autoFit)
    {
      collection.forEach((component) ->
      {
        Vector2d position = getComponentPosition(collection.indexOf(component));
        component.setBounds(
            (int) position.x,
            (int) position.y,
            (int) getComponentSize().width,
            (int) getComponentSize().height);
      });
    }
    else
    {
      collection.forEach((component) ->
      {
        Vector2d position = getComponentPosition(collection.indexOf(object));
        object.setLocation((int) position.x, (int) position.y);
      });

      Vector2d position = getComponentPosition(collection.indexOf(object));

      if (alignment == Alignment.VERTICAL)
        viewPane.setPreferredSize(new Dimension(viewPane.getWidth(), (int) ( position.y + object.getHeight() )));
      else
        viewPane.setPreferredSize(new Dimension((int) ( position.x + object.getWidth() ), viewPane.getHeight()));
    }


    setAlignment(alignment);
  }

  public void removeObject (Component object)
  {
    collection.remove(object);
    scrollPane.remove(object);
  }

  public void clear ()
  {
    collection.clear();
    scrollPane.removeAll();
  }

  public void addBuffer (int hBuffer, int vBuffer)
  {
    JPanel buffer = new JPanel();
    buffer.setBackground(Color.RED);
    buffer.setBorder(null);
    buffer.setBounds(0, 0, hBuffer, vBuffer);
    addObject(buffer);
    buffers.add(buffer);
  }

  public void addSeparator (JPanel separator)
  {
    addObject(separator);
    buffers.add(separator);

    separator.setSize(getWidth() - 2 * edgeBuffer, separator.getHeight());
  }

  //endregion

  //region optics
  public void showBorder (boolean show)
  {
    showBorder = show;
    viewPane.setBorder(showBorder ? BorderFactory.createLineBorder(currentColorSet.border(), borderSize) : null);
  }

  public void setAutoFit (boolean autoFit)
  {
    this.autoFit = autoFit;
    //    scrollPane.setHorizontalScrollBarPolicy(autoFit ? JScrollPane.HORIZONTAL_SCROLLBAR_NEVER : JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    //    scrollPane.setVerticalScrollBarPolicy(autoFit ? JScrollPane.VERTICAL_SCROLLBAR_NEVER : JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
  }

  public void useColorSet (Style.ColorSet colorSet)
  {
    currentColorSet = colorSet;
    viewPane.setBackground(currentColorSet.background());
    viewPane.setBorder(showBorder ? BorderFactory.createLineBorder(currentColorSet.border(), borderSize) : null);
  }

  public void unifyFontSize (float size)
  {
    collection.forEach((component) ->
    {
      component.setFont(component.getFont().deriveFont(size));
    });
  }

  public void fitComponents ()
  {
    List <Component> components = new ArrayList <>(collection);
    components.removeAll(buffers);

    components.forEach((object) ->
    {
      object.setBounds(0, 0, getComponentSize().width, getComponentSize().height);
    });

    updateLocations();

  }

  public void setBackGround (Color color)
  {
    viewPane.setBackground(color);
  }

  //endregion

  //region getters/setters

  public List <Component> getAllItems ()
  {
    return collection;
  }

  public void setItem (int index, Component component)
  {
    collection.set(index, component);
  }

  public Component getItem (int index)
  {
    return collection.get(index);
  }

  public int getIndexOf (Component component)
  {
    return collection.indexOf(component);
  }

  //endregion

  //region selection
  public void selectItem (int index)
  {
    if (selected != null)
    {
      ( (PacButton) selected ).setFocused(false);
    }

    if (selectionRollAround)
    {
      if (index > collection.size() - 1)
        index = 0;
      else if (index < 0)
        index = collection.size() - 1;
    }
    else
    {
      if (index > collection.size() - 1)
        index = collection.size() - 1;
      else if (index < 0)
        index = 0;
    }

    selected = collection.get(index);

    ( (PacButton) selected ).setFocused(true);
  }

  public void selectNext (int step)
  {
    selectItem(collection.indexOf(selected) + step);
  }

  public void fireSelectedAction ()
  {
    if(selected != null && selected instanceof PacButton)
      ( (PacButton) selected ).press();
  }
  //endregion


  //region privates
  private void setAlignment (Alignment alignment)
  {
    this.alignment = alignment;

    if (alignment == Alignment.HORIZONTAL)
    {
      viewPane.setPreferredSize(new Dimension(getComponentSize().width * collection.size() + ( collection.size() - 1 ) * hBuffer, getHeight()));
    }
    else if (alignment == Alignment.VERTICAL)
    {
      viewPane.setPreferredSize(new Dimension(getWidth(), getComponentSize().height * collection.size() + ( collection.size() - 1 ) * vBuffer));
    }
  }

  private Dimension getComponentSize ()
  {
    int width  = 0;
    int height = 0;

    int itemSize = collection.size() - buffers.size();

    if (alignment == Alignment.HORIZONTAL)
    {
      width = ( getWidth() - 2 * edgeBuffer - ( itemSize - 1 ) * hBuffer ) / itemSize;
      height = getHeight() - 2 * edgeBuffer;
    }
    else if (alignment == Alignment.VERTICAL)
    {
      width = getWidth() - 2 * edgeBuffer;
      height = ( getHeight() - 2 * edgeBuffer - ( itemSize - 1 ) * vBuffer ) / itemSize;
    }

    return new Dimension(width, height);
  }

  private Vector2d getComponentPosition (int index)
  {
    AtomicReference <Vector2d> start = new AtomicReference <>(new Vector2d().cartesian(edgeBuffer, edgeBuffer));

    IntStream.range(0, index).forEach((i) ->
    {
      if (alignment == Alignment.HORIZONTAL)
      {
        start.set(start.get().add(new Vector2d().cartesian(collection.get(i).getBounds().width, 0)));
      }
      else if (alignment == Alignment.VERTICAL)
      {
        start.set(start.get().add(new Vector2d().cartesian(0, collection.get(i).getBounds().height)));
      }
    });

    if (alignment == Alignment.HORIZONTAL)
    {
      start.set(start.get().add(new Vector2d().cartesian(index * hBuffer, 0)));
    }
    else if (alignment == Alignment.VERTICAL)
    {
      start.set(start.get().add(new Vector2d().cartesian(0, index * vBuffer)));
    }
    return start.get();
  }

  public void updateLocations ()
  {
    collection.forEach((object) ->
    {
      Vector2d position = getComponentPosition(collection.indexOf(object));
      object.setLocation((int) position.x, (int) position.y);
    });
  }
  //endregion

  public enum Alignment
  { HORIZONTAL, VERTICAL }
}
