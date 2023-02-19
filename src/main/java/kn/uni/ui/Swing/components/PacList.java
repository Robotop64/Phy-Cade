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

public class PacList extends JPanel
{

  public Alignment alignment  = Alignment.VERTICAL;
  public int       hBuffer    = 5;
  public int       vBuffer    = 5;
  public int       edgeBuffer = 5;
  public int       borderSize = 1;
  public boolean   showBorder = false;

  public ArrayList <Component> collection;
  public JScrollPane           scrollPane;
  public JPanel                viewPane;

  public Style.ColorSet currentColorSet;

  public PacList (Vector2d position, Dimension size)
  {
    super();
    setBounds((int) position.x, (int) position.y, size.width, size.height);
    setLayout(null);
    setBackground(Color.BLACK);

    collection = new ArrayList <>();
    viewPane = new JPanel();
    viewPane.setLayout(null);
    viewPane.setBounds(0, 0, getWidth(), getHeight());

    scrollPane = new JScrollPane(viewPane, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBounds(0, 0, getWidth(), getHeight());

    scrollPane.setBorder(null);
    add(scrollPane);

    useColorSet(Style.normal);
  }

  public void addObject (Component object)
  {
    collection.add(object);
    viewPane.add(object);

    collection.forEach((component) ->
    {
      component.setBounds(
          (int) getComponentPosition(collection.indexOf(component)).x,
          (int) getComponentPosition(collection.indexOf(component)).y,
          (int) getComponentSize().width,
          (int) getComponentSize().height);
    });

    setAlignment(alignment);

    System.out.println(getComponentSize());
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

  public void setAlignment (Alignment alignment)
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

  public void showBorder (boolean show)
  {
    showBorder = show;
    viewPane.setBorder(showBorder ? BorderFactory.createLineBorder(currentColorSet.border(), borderSize) : null);
  }

  public void useColorSet (Style.ColorSet colorSet)
  {
    currentColorSet = colorSet;
    viewPane.setBackground(currentColorSet.background());
    viewPane.setBorder(showBorder ? BorderFactory.createLineBorder(currentColorSet.border(), borderSize) : null);
  }


  private Dimension getComponentSize ()
  {
    int width  = 0;
    int height = 0;

    if (alignment == Alignment.HORIZONTAL)
    {
      width = ( getWidth() - 2 * edgeBuffer - ( collection.size() - 1 ) * hBuffer ) / collection.size();
      height = getHeight();
    }
    else if (alignment == Alignment.VERTICAL)
    {
      width = getWidth() - 2 * edgeBuffer;
      height = ( getHeight() - 2 * edgeBuffer - ( collection.size() - 1 ) * vBuffer ) / collection.size();
    }

    return new Dimension(width, height);
  }

  private Vector2d getComponentPosition (int index)
  {
    int x = edgeBuffer;
    int y = edgeBuffer;

    if (alignment == Alignment.HORIZONTAL)
    {
      x = edgeBuffer + index * ( getComponentSize().width ) + ( index ) * hBuffer;
    }
    else if (alignment == Alignment.VERTICAL)
    {
      y = edgeBuffer + index * ( getComponentSize().height ) + ( index ) * vBuffer;
    }

    return new Vector2d().cartesian(x, y);
  }

  public enum Alignment
  { HORIZONTAL, VERTICAL }
}
