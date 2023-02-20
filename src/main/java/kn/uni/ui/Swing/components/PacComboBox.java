package kn.uni.ui.Swing.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.components.FlatComboBox;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.Swing.renderer.ColoredListCellRenderer;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;

import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.Dimension;

public class PacComboBox extends FlatComboBox <String>
{
  public boolean isSelected;

  public boolean isFocused;

  public Runnable onSelectionChange;

  public PacComboBox (Vector2d position, Dimension size)
  {
    super();
    this.setBounds((int) position.x, (int) position.y, size.width, size.height);
    useColorSet(Style.normal);

    addListeners();
  }

  public void useColorSet (Style.ColorSet colorSet)
  {
    this.setOutline(colorSet.border());
    this.setBackground(colorSet.background());
    this.setForeground(colorSet.foreground());

    Object          child = this.getAccessibleContext().getAccessibleChild(0);
    BasicComboPopup popup = (BasicComboPopup) child;
    popup.setBorder(new LineBorder(colorSet.foreground()));

    this.setRenderer(new ColoredListCellRenderer(colorSet.background(), colorSet.foreground().darker().darker().darker(), colorSet.foreground()));
    putClientProperty(FlatClientProperties.STYLE, "buttonBackground:" + Util.colorToHex(colorSet.background()) + ";" + "buttonArrowColor:" + Util.colorToHex(colorSet.foreground()));
  }

  public void setSelected (boolean selected)
  {
    isSelected = selected;
    Style.ColorSet set = isSelected ? Style.selected : Style.normal;
    useColorSet(set);
  }

  public void selectOption (int index)
  {
    this.setSelectedIndex(index);

  }

  public void setFocused (boolean focused)
  {
    isFocused = focused;
    Style.ColorSet set = isFocused ? Style.focused : Style.normal;
    useColorSet(set);
    if (isFocused)
      this.grabFocus();
  }

  private void addListeners ()
  {
    //    PacComboBox self = this;
    //    addFocusListener(new FocusAdapter()
    //    {
    //      @Override
    //      public void focusGained (FocusEvent e)
    //      {
    //        useColorSet(Style.focused);
    //        self.showPopup();
    //      }
    //
    //      @Override
    //      public void focusLost (FocusEvent e)
    //      {
    //        useColorSet(Style.normal);
    //      }
    //    });

    addActionListener(e ->
    {
      if (onSelectionChange != null) onSelectionChange.run();
    });
  }
}
