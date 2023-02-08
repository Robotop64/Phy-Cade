package kn.uni.ui.Swing.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.components.FlatTable;
import com.formdev.flatlaf.extras.components.FlatTableHeader;
import kn.uni.ui.Swing.Style;
import kn.uni.util.Vector2d;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Dimension;

public class PacTable extends FlatTable
{
  public boolean displayGrid   = true;
  public boolean displayHeader = true;
  public boolean displayBorder = true;

  Object[][]      data;
  String[]        columnNames;
  FlatTableHeader header;
  Style.ColorSet  currentColorSet;

  public PacTable (Vector2d position, Dimension size)
  {
    super();
    setBounds((int) position.x, (int) position.y, size.width, size.height);

    header = new FlatTableHeader();
    header.setColumnModel(getColumnModel());

    useColorSet(Style.normal);
  }

  //  public PacTable (Vector2d position, Dimension size, Object[][] data, String[] columnNames)
  //  {
  //    super();
  //    setContent(data, columnNames);
  //    setBounds((int) position.x, (int) position.y, size.width, size.height);
  //
  //    this.data = data;
  //    this.columnNames = columnNames;
  //
  //    header = new FlatTableHeader();
  //    header.setColumnModel(getColumnModel());
  //    System.out.println("header = " + header);
  //
  //    useColorSet(Style.normal);
  //  }

  public void useColorSet (Style.ColorSet colorSet)
  {
    currentColorSet = colorSet;

    getTableHeader().setForeground(colorSet.foreground());
    getTableHeader().setBackground(colorSet.background());
    this.setBackground(colorSet.background());
    this.setForeground(colorSet.foreground());

    showBorder(displayBorder);

    showGrid(displayGrid);
  }

  public void setContent (Object[][] data, String[] columnNames)
  {
    this.data = data;
    this.columnNames = columnNames;
    setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
  }

  public void setCellContent (int row, int column, Object value)
  {
    data[row][column] = value;
    setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
  }

  public Object getCellContent (int row, int column)
  {
    return data[row][column];
  }

  public void setHeader (String[] columnNames)
  {
    this.columnNames = columnNames;
    setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
  }

  public void setColumnHeader (int column, String name)
  {
    columnNames[column] = name;
    setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
  }

  public void useHeader (boolean use)
  {
    setTableHeader(use ? new javax.swing.table.JTableHeader() : null);

    if (use)
    {
      getTableHeader().setBounds(getX(), getY(), getWidth(), 30);
      getTableHeader().setColumnModel(getColumnModel());
      setBounds(getX(), getY() + 30, getWidth(), getHeight() - 30);
    }
  }

  public void setHeaderHeight (int height)
  {
    getTableHeader().setBounds(getX(), getY(), getWidth(), height);
    setBounds(getX(), getY() + height, getWidth(), getHeight() - height);
  }

  public FlatTableHeader getTableHeader ()
  {
    return header;
  }

  public void showGrid (boolean show)
  {
    setShowGrid(show);
    displayGrid = show;

    Color gridColor = displayGrid ? currentColorSet.border() : currentColorSet.background();

    setGridColor(gridColor);
    getTableHeader().putClientProperty(FlatClientProperties.STYLE,
        "separatorColor:" + String.format("#%02x%02x%02x", gridColor.getRed(), gridColor.getGreen(), gridColor.getBlue()) + ";"
            + "bottomSeparatorColor:" + String.format("#%02x%02x%02x", gridColor.getRed(), gridColor.getGreen(), gridColor.getBlue()));
  }

  public void showBorder (boolean show)
  {
    displayBorder = show;
    Border border = displayBorder ? BorderFactory.createLineBorder(currentColorSet.border()) : null;

    setBorder(border);
    getTableHeader().setBorder(border);
  }
}
