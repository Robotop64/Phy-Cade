package kn.uni.ui.Swing.components;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class PacTable extends JTable
{

  public PacTable ()
  {
    super();
  }

  public  PacTable(String[] columnNames, Object[][] data) {
    super(data, columnNames);
  }

  public static AbstractTableModel customModel(String[] columnNames, Object[][] data) {
    return new AbstractTableModel() {
      @Override
      public int getRowCount() {
        return data.length;
      }

      @Override
      public int getColumnCount() {
        return columnNames.length;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
      }

      @Override
      public String getColumnName(int column) {
        return columnNames[column];
      }
    };
  }

}
