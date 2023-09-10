package kn.uni.ui.Swing.components;

import com.formdev.flatlaf.extras.components.FlatTable;
import com.formdev.flatlaf.extras.components.FlatTableHeader;
import kn.uni.ui.Swing.Style;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PacTable extends JPanel
{
  public boolean displayGrid   = true;
  public boolean displayHeader = true;
  public boolean displayBorder = true;

  Object[][]      data;
  String[]        columnNames;
  public FlatTableHeader header;
  public FlatTable body;
  Style.ColorSet  currentColorSet;
  JScrollPane bodyContainer;

  public PacTable ()
  {
    super();
//    setBounds((int) position.x, (int) position.y, size.width, size.height);
//
//    setLayout(null);
//    setBackground(Style.normal.background());
//
//    body = new FlatTable();
//    body.setBounds(0, 0, getWidth(), getHeight());
//    body.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//    header = new FlatTableHeader();
//    header.setBounds(0, 0, getWidth(), 30);
//    header.setColumnModel(body.getColumnModel());
//
//    add(bodyContainer = new JScrollPane(body, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
//
//    useColorSet(Style.normal);
//
//    useHeader(displayHeader);
  }

//  public void useColorSet (Style.ColorSet colorSet)
//  {
//    currentColorSet = colorSet;
//
//    getTableHeader().setForeground(colorSet.foreground());
//    getTableHeader().setBackground(colorSet.background());
//    body.setBackground(colorSet.background());
//    body.setForeground(colorSet.foreground());
//
//    showBorder(displayBorder);
//
//    showGrid(displayGrid);
//  }
//
//  public void setContent (Object[][] data, String[] columnNames)
//  {
//    this.data = data;
//    this.columnNames = columnNames;
//    body.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
//  }
//
//  public void setCellContent (int row, int column, Object value)
//  {
//    data[row][column] = value;
//    body.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
//  }
//
//  public Object getCellContent (int row, int column)
//  {
//    return data[row][column];
//  }
//
//  public void setHeader (String[] columnNames)
//  {
//    this.columnNames = columnNames;
//    body.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
//  }
//
//  public void setColumnHeader (int column, String name)
//  {
//    columnNames[column] = name;
//    body.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
//  }
//
//  public void useHeader (boolean use)
//  {
//    displayHeader = use;
//    remove(bodyContainer);
//    body.setTableHeader(use ? header : null);
//    bodyContainer = new JScrollPane(body, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//    bodyContainer.setBounds(0, 0, getWidth(), getHeight());
//    bodyContainer.setBorder(BorderFactory.createLineBorder(Style.normal.background()));
//    add(bodyContainer);
//  }
//
//  public void setColumnWidth (int column, int width)
//  {
//    body.getColumnModel().getColumn(column).setPreferredWidth(width);
//  }
//
//  public void fitColumns ()
//  {
//    body.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//  }
//
//  public FlatTableHeader getTableHeader ()
//  {
//    return header;
//  }
//
//  public void showGrid (boolean show)
//  {
//    body.setShowGrid(show);
//    displayGrid = show;
//
//    Color gridColor = displayGrid ? currentColorSet.border() : currentColorSet.background();
//
//    body.setGridColor(gridColor);
//    getTableHeader().putClientProperty(FlatClientProperties.STYLE,
//        "separatorColor:" + String.format("#%02x%02x%02x", gridColor.getRed(), gridColor.getGreen(), gridColor.getBlue()) + ";"
//            + "bottomSeparatorColor:" + String.format("#%02x%02x%02x", gridColor.getRed(), gridColor.getGreen(), gridColor.getBlue()));
//  }
//
//  public void showBorder (boolean show)
//  {
//    displayBorder = show;
//    Border border = displayBorder ? BorderFactory.createLineBorder(currentColorSet.border()) : null;
//
//    body.setBorder(border);
//    getTableHeader().setBorder(border);
//    bodyContainer.setBorder(BorderFactory.createLineBorder(currentColorSet.border()));
//  }
}
