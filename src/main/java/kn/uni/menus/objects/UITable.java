package kn.uni.menus.objects;

import kn.uni.menus.interfaces.Displayed;
import kn.uni.menus.interfaces.Updating;
import kn.uni.util.PacPhiConfig;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class UITable extends UIObject implements Displayed, Updating
{
  public int                hSpacing;
  public int                vSpacing;
  public Dimension          dim;
  public Color              backgroundColor;
  public Color              borderColor;
  public Color              textColor;
  public int                borderWidth;
  public Dimension          size;
  public Dimension          innerSize;
  public boolean            showGrid;
  public boolean            showBorder;
  public List <List <Cell>> table;

  public UITable (Vector2d pos, Dimension size, int paintLayer, Dimension dim)
  {
    hSpacing = 5;
    vSpacing = 5;
    this.dim = dim;
    table = new ArrayList <>();

    setStyle(normal);

    this.position = pos;
    this.size = size;
    this.innerSize = new Dimension(size.width - 2 * borderWidth, size.height - 2 * borderWidth);
    this.paintLayer = paintLayer;


    addColumn(dim.width);
    addRow(dim.height);

    System.out.println(table);
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    position.use(g::translate);
    g.setStroke(new BasicStroke(borderWidth));

    g.setColor(backgroundColor);
    g.fillRect(0, 0, size.width, size.height);

    g.setColor(borderColor);
    if (showBorder)
      g.drawRect(0, 0, size.width, size.height);

    if (showGrid)
      showBuffers(g);

    table.forEach(row -> row.stream()
                            .map(cell -> cell.content)
                            .filter(Objects::nonNull)
                            .filter(o -> o.visible)
                            .filter(cell -> cell instanceof Displayed)
                            .map(cell -> (Displayed) cell)
                            .forEach(cell -> cell.paintComponent(g)));

    position.invert().use(g::translate);
  }

  @Override
  public int paintLayer ()
  {
    return paintLayer;
  }

  @Override
  public void update ()
  {
    table.forEach(row -> row.stream()
                            .map(cell -> cell.content)
                            .filter(Objects::nonNull)
                            .filter(o -> o.visible)
                            .filter(cell -> cell instanceof Updating)
                            .map(cell -> (Updating) cell)
                            .forEach(Updating::update));

    if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
    {
      showBorder = true;
      showGrid = true;
    }
  }

  public void setStyle (ColorSet colorSet)
  {
    textColor = colorSet.textColor();
    backgroundColor = colorSet.backgroundColor();
    borderColor = colorSet.borderColor();
  }

  public void addColumn (int columns)
  {
    IntStream.range(0, columns).forEach(i -> table.add(new ArrayList <>()));
  }

  public void addRow (int rows)
  {
    int cellWidth  = ( size.width - ( dim.width - 1 ) * hSpacing ) / dim.width;
    int cellHeight = ( size.height - ( dim.height - 1 ) * vSpacing ) / dim.height;
    table.forEach(row ->
        IntStream.range(0, rows)
                 .forEach(i ->
                 {
                   Cell cell = new Cell(new int[]{ table.indexOf(row), i }, null);
                   cell.size = new Dimension(cellWidth, cellHeight);
                   row.add(cell);
                 }));
  }

  public void updateCellContent (int[] pos, UIObject content)
  {
    table.get(pos[0]).get(pos[1]).content = content;
  }

  public UIObject getCellContent (int[] pos)
  {
    return table.get(pos[0]).get(pos[1]).content;
  }


  public void makeCellLable (int[] pos, String text)
  {
    setCell(pos, new UILabel(getCellPosition(pos), getCellSize(pos), text, paintLayer));
  }

  public void cellToLabel (int[] pos, String text)
  {
    updateCellContent(pos, new UILabel(getCellPosition(pos), getCellSize(pos), text, paintLayer));
  }

  private void setCell (int[] pos, kn.uni.menus.objects.UIObject obj)
  {
    Cell cell = new Cell(pos, obj);
    cell.size = obj.size;
    table.get(pos[0]).set(pos[1], cell);
  }


  public Vector2d getCellPosition (int[] pos)
  {
    int cellWidth  = ( size.width - ( dim.width - 1 ) * hSpacing ) / dim.width;
    int cellHeight = ( size.height - ( dim.height - 1 ) * vSpacing ) / dim.height;
    return new Vector2d().cartesian(pos[0] * ( cellWidth + hSpacing ), pos[1] * ( cellHeight + vSpacing ));
  }

  public Dimension getCellSize (int[] pos)
  {
    int cellWidth  = ( size.width - ( dim.width - 1 ) * hSpacing ) / dim.width;
    int cellHeight = ( size.height - ( dim.height - 1 ) * vSpacing ) / dim.height;
    return new Dimension(cellWidth, cellHeight);
  }

  //  public void setColumnWidth (int column, int width)
  //  {
  //    table.get(column).forEach(cell -> cell.size = new Dimension(width, cell.size.height));
  //  }
  //
  //  public void setRowHeight (int row, int height)
  //  {
  //    table.forEach(column -> column.get(row).size = new Dimension(column.get(row).size.width, height));
  //  }

  private int getColumnWidth (int column)
  {
    return table.get(column).stream()
                .mapToInt(cell -> cell.size.width)
                .max()
                .orElse(0);
  }

  private int getRowHeight (int row)
  {
    return table.stream()
                .mapToInt(column -> column.get(row).size.height)
                .max()
                .orElse(0);
  }

  private void showBuffers (Graphics2D g)
  {
    for (int i = 1; i < dim.width; i++)
    {
      int width = getColumnWidth(i - 1);
      g.drawLine(i * width + ( i - 1 ) * hSpacing, 0, i * width + ( i - 1 ) * hSpacing, size.height);
      g.drawLine(i * width + i * hSpacing, 0, i * width + i * hSpacing, size.height);
    }
    for (int i = 1; i < dim.height; i++)
    {
      int height = getRowHeight(i - 1);
      g.drawLine(0, i * height + ( i - 1 ) * vSpacing, size.width, i * height + ( i - 1 ) * vSpacing);
      g.drawLine(0, i * height + i * vSpacing, size.width, i * height + i * vSpacing);
    }
  }
}

class Cell
{
  public int[]     pos;
  public UIObject  content;
  public Dimension size;

  public Cell (int[] pos, UIObject content)
  {
    this.pos = pos;
    this.content = content;
  }
}

