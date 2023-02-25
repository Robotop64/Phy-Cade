package kn.uni.menus.G2D.objects;

import kn.uni.menus.G2D.interfaces.Displayed;
import kn.uni.menus.G2D.interfaces.Updating;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.PacPhiConfig;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class UITable extends UIObject implements Displayed, Updating
{
  public  int                hSpacing;
  public  int                vSpacing;
  public  Dimension          dim;
  public  Color              backgroundColor;
  public  Color              borderColor;
  public  Color              textColor;
  public  int                borderWidth;
  public  Dimension          size;
  public  Dimension          innerSize;
  public  boolean            showGrid;
  public  boolean            showBorder;
  public  boolean            showBackground;
  public  boolean            showCellBounds;
  public  List <List <Cell>> table;
  public  Cell               selected = null;
  private Dimension          cellSize;
  private Vector2d           offset;

  public UITable (Vector2d pos, Dimension size, int paintLayer, Dimension dim)
  {
    hSpacing = 5;
    vSpacing = 5;
    this.dim = dim;
    table = new ArrayList <>();
    offset = new Vector2d().cartesian(0, 0);

    setStyle(unselected);

    this.position = pos;
    this.size = size;
    this.innerSize = new Dimension(size.width - 2 * borderWidth, size.height - 2 * borderWidth);
    this.paintLayer = paintLayer;
    int cellWidth  = ( size.width - ( dim.width - 1 ) * hSpacing ) / dim.width;
    int cellHeight = ( size.height - ( dim.height - 1 ) * vSpacing ) / dim.height;

    cellSize = new Dimension(cellWidth, cellHeight);

    addColumn(dim.width);
    this.dim = new Dimension(dim.width / 2, dim.height);
    alignCells();
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    position.use(g::translate);
    g.setClip(new Rectangle(size));

    if (showBackground)
    {
      g.setColor(backgroundColor);
      g.fillRect(0, 0, size.width, size.height);
    }

    if (showGrid)
    {
      g.setStroke(new BasicStroke(borderWidth));
      g.setColor(borderColor);
      showBuffers(g);
    }

    offset.use(g::translate);
    table.forEach(row -> row.stream()
                            .map(cell -> cell.content)
                            .filter(Objects::nonNull)
                            .filter(o -> o.visible)
                            .filter(cell -> cell instanceof Displayed)
                            .filter(cell ->
                            {
                              Rectangle bounds     = new Rectangle((int) 0, (int) 0, size.width, size.height);
                              Rectangle cellBounds = new Rectangle((int) cell.position.add(offset).x, (int) cell.position.add(offset).y, cell.size.width, cell.size.height);
                              return bounds.intersects(cellBounds);
                            })
                            .map(cell -> (Displayed) cell)
                            .forEach(cell -> cell.paintComponent(g)));

    if (showCellBounds)
    {
      g.setColor(Color.RED);
      table.forEach(row -> row.forEach(cell -> g.draw(new Rectangle((int) getCellPosition(cell.pos).x, (int) getCellPosition(cell.pos).y, cell.size.width, cell.size.height))));
    }
    offset.invert().use(g::translate);

    g.setClip(null);

    if (showBorder)
    {
      g.setStroke(new BasicStroke(borderWidth));
      g.setColor(borderColor);
      g.drawRect(0, 0, size.width, size.height);
    }
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
      showCellBounds = true;
    }
  }

  public void setStyle (ColorSet colorSet)
  {
    textColor = colorSet.textColor();
    backgroundColor = colorSet.backgroundColor();
    borderColor = colorSet.borderColor();
  }

  //Table Stuff
  public void addColumn (int columns)
  {
    this.dim.width += columns;
    IntStream.range(0, columns).forEach(i ->
    {
      table.add(new ArrayList <>());
      IntStream.range(0, dim.height).forEach(j ->
      {
        Cell cell = new Cell(new int[]{ table.size() - 1, j }, null);
        cell.size = new Dimension(cellSize.width, cellSize.height);
        cell.absPos = new Vector2d().cartesian(cell.pos[0] * ( cellSize.width + hSpacing ), cell.pos[1] * ( cellSize.height + vSpacing ));
        table.get(table.size() - 1).add(cell);
      });
    });
  }

  public void addRow (int rows)
  {
    this.dim.height += rows;

    IntStream.range(0, rows).forEach(i ->
    {
      IntStream.range(0, table.size()).forEach(j ->
      {
        Cell cell = new Cell(new int[]{ j, table.get(j).size() }, null);
        cell.size = new Dimension(cellSize.width, cellSize.height);
        cell.absPos = new Vector2d().cartesian(cell.pos[0] * ( cellSize.width + hSpacing ), cell.pos[1] * ( cellSize.height + vSpacing ));
        table.get(j).add(cell);
      });
    });
  }

  public void alignCells ()
  {
    table.forEach(col ->
        col.forEach(cell ->
        {
          int colIndex  = table.indexOf(col);
          int cellIndex = col.indexOf(cell);

          if (colIndex == 0 && cellIndex == 0)
            cell.absPos = new Vector2d().cartesian(0, 0);
          else if (colIndex != 0 && cellIndex == 0)
            cell.absPos = new Vector2d().cartesian(table.get(colIndex - 1).get(0).absPos.x + table.get(colIndex - 1).get(0).size.width + hSpacing, 0);
          else
            cell.absPos = new Vector2d().cartesian(col.get(cellIndex - 1).absPos.x, col.get(cellIndex - 1).absPos.y + col.get(cellIndex - 1).size.height + vSpacing);

          if (cell.content != null)
            cell.content.position = cell.absPos;
        }));
  }

  public void setSpacing (int hSpacing, int vSpacing)
  {
    this.hSpacing = hSpacing;
    this.vSpacing = vSpacing;
    alignCells();
  }

  public void setColumnWidth (int column, int width)
  {
    table.get(column).forEach(cell -> cell.size = new Dimension(width, cell.size.height));
    alignCells();
  }

  public void setRowHeight (int row, int height)
  {
    table.forEach(column -> column.get(row).size = new Dimension(column.get(row).size.width, height));
    alignCells();
  }

  public void convertRowToButtons (int row, String[] texts)
  {
    IntStream.range(0, table.size()).forEach(i ->
    {
      Cell cell = table.get(i).get(row);
      cell.content = new UIButton(cell.absPos, cell.size, texts[i], paintLayer);
      cell.content.asButton().useColorSet(UIButton.unselected);
    });
  }

  public void convertColumnToButtons (int column, String[] texts)
  {
    IntStream.range(0, table.get(column).size()).forEach(i ->
    {
      Cell cell = table.get(column).get(i);
      cell.content = new UIButton(cell.absPos, cell.size, texts[i], paintLayer);
      cell.content.asButton().useColorSet(UIButton.unselected);
    });
  }

  public void convertRowToLabels (int row, String[] texts)
  {
    IntStream.range(0, table.size()).forEach(i ->
    {
      Cell cell = table.get(i).get(row);
      cell.content = new UILabel(cell.absPos, cell.size, texts[i], paintLayer);
      cell.content.asLabel().useColorSet(UIObject.unselected);
    });
  }

  public void convertColumnToLabels (int column, String[] texts)
  {
    IntStream.range(0, table.get(column).size()).forEach(i ->
    {
      Cell cell = table.get(column).get(i);
      cell.content = new UILabel(cell.absPos, cell.size, texts[i], paintLayer);
      cell.content.asLabel().useColorSet(UIObject.unselected);
    });
  }

  public int getCurrentCellWidth ()
  {
    return cellSize.width;
  }

  public int getCurrentCellHeight ()
  {
    return cellSize.height;
  }

  public void fillColumn (int column, int size)
  {
    Cell first = table.get(column).get(0);
    table.get(column).clear();
    IntStream.range(0, size).forEach(i ->
    {
      Cell cell = new Cell(new int[]{ column, i }, null);
      cell.size = new Dimension(first.size.width, first.size.height);
      cell.absPos = new Vector2d().cartesian(cell.pos[0] * ( cellSize.width + hSpacing ), cell.pos[1] * ( cellSize.height + vSpacing ));
      table.get(column).add(cell);
    });

  }

  public void loadLength (int length)
  {
    table.forEach(col ->
    {
      fillColumn(table.indexOf(col), length);
    });

    this.dim.height = length;
  }

  public List <UIObject> getRow (int row)
  {
    List <UIObject> rowList = new ArrayList <>();
    table.forEach(col ->
    {
      rowList.add(col.get(row).content);
    });
    return rowList;
  }

  public List <UIObject> getColumn (int column)
  {
    List <UIObject> colList = new ArrayList <>();
    table.get(column).forEach(cell ->
    {
      colList.add(cell.content);
    });
    return colList;
  }
  //

  //Selection
  public void selectCell (int[] pos)
  {
    Cell next = table.get(pos[0]).get(pos[1]);
    Cell old  = selected;
    if (next.content == null || !next.content.selectable) return;

    if (old != null)
    {
      old.selected = false;
      old.content.asButton().isSelected = false;
    }

    next.selected = true;
    next.content.asButton().isSelected = true;

    selected = next;
  }

  public void deselectCell ()
  {
    if (selected == null) return;
    selected.selected = false;
    selected.content.asButton().isSelected = false;
    selected = null;
  }

  public void moveSelection (Direction dir)
  {
    int[] pos = selected.pos;
    if (pos == null)
    {
      return;
    }

    if (dir == null) return;

    switch (dir)
    {
      case up ->
      {
        if (pos[1] > 0)
          selectCell(new int[]{ pos[0], pos[1] - 1 });
      }
      case down ->
      {
        if (pos[1] < dim.height - 1)
          selectCell(new int[]{ pos[0], pos[1] + 1 });
      }
      case left ->
      {
        if (pos[0] > 0)
          selectCell(new int[]{ pos[0] - 1, pos[1] });
      }
      case right ->
      {
        if (pos[0] < dim.width - 1)
          selectCell(new int[]{ pos[0] + 1, pos[1] });
      }
    }
  }

  public void pressSelected ()
  {
    if (selected == null) return;
    selected.content.asButton().press();
  }
  //

  //Cell Stuff
  public void setCellSize (Dimension size)
  {
    cellSize = size;
    table.forEach(row -> row.forEach(cell ->
    {
      cell.size = size;
      if (cell.content != null)
      {
        cell.content.asLabel().setSize(size);
        cell.content.position = getCellPosition(cell.pos);
      }
    }));
  }

  public Dimension getCellSize (int[] pos)
  {
    return table.get(pos[0]).get(pos[1]).size;
  }

  public void setDefCellSize ()
  {
    int cellWidth  = ( size.width - ( dim.width - 1 ) * hSpacing ) / dim.width;
    int cellHeight = ( size.height - ( dim.height - 1 ) * vSpacing ) / dim.height;
    setCellSize(new Dimension(cellWidth, cellHeight));
    alignCells();
  }

  public void cellToLabel (int[] pos, String text)
  {
    setCellContent(pos, new UILabel(getCellPosition(pos), getCellSize(pos), text, paintLayer));
  }

  public void cellToButton (int[] pos, String text)
  {
    setCellContent(pos, new UIButton(getCellPosition(pos), getCellSize(pos), text, paintLayer));
  }

  public Vector2d getCellPosition (int[] pos)
  {
    return table.get(pos[0]).get(pos[1]).absPos;
  }

  public boolean cellIsEdgeOfVisibleArea (int[] pos)
  {
    Vector2d cellPos      = getCellPosition(pos).add(position);
    Vector2d transCellPos = cellPos.add(offset);

    Area visibleArea = new Area(new Rectangle((int) position.x, (int) position.y, size.width, size.height));
    return Arrays.stream(Direction.valuesCardinal()).anyMatch(dir ->
    {
      Vector2d nextCellPos = new Vector2d().cartesian(0, 0);

      if (dir.equals(Direction.right))
        nextCellPos = transCellPos.add(dir.toVector().multiply(cellSize.width + hSpacing));

      if (dir.equals(Direction.left))
        nextCellPos = transCellPos.add(dir.toVector().multiply(cellSize.width + hSpacing));

      if (dir.equals(Direction.up))
        nextCellPos = transCellPos.add(dir.toVector().multiply(cellSize.height + vSpacing));

      if (dir.equals(Direction.down))
        nextCellPos = transCellPos.add(dir.toVector().multiply(cellSize.height + vSpacing));

      return !visibleArea.contains(nextCellPos.x, nextCellPos.y);
    });
  }

  public boolean cellIsInVisibleArea (int[] pos)
  {
    Vector2d cellPos      = getCellPosition(pos).add(position);
    Vector2d transCellPos = cellPos.add(offset);

    Area visibleArea = new Area(new Rectangle((int) position.x, (int) position.y, size.width, size.height));
    return visibleArea.contains(transCellPos.x, transCellPos.y);
  }

  public void setFontSize (int size)
  {
    table.forEach(row -> row.forEach(cell ->
    {
      if (cell.content != null)
      {
        cell.content.asLabel().setFontSize(size);
      }
    }));
  }
  //

  //CellContent
  public UIObject getCellContent (int[] pos)
  {
    return table.get(pos[0]).get(pos[1]).content;
  }

  public void setCellContent (int[] pos, kn.uni.menus.G2D.objects.UIObject obj)
  {
    Cell cell = new Cell(pos, obj);
    cell.size = obj.size;
    table.get(pos[0]).set(pos[1], cell);
  }

  public void moveContent (Direction dir, int distance)
  {
    if (dir == null) return;
    if (distance < 1) return;

    if (dir.toVector().isHorizontal())
      offset = offset.add(dir.toVector().multiply(distance * ( cellSize.width + hSpacing )));
    else
      offset = offset.add(dir.toVector().multiply(distance * ( cellSize.height + vSpacing )));
  }

  public void fitCellContent ()
  {
    table.forEach(col -> col.forEach(cell ->
    {
      cell.content.asLabel().setSize(cellSize);
    }));
  }
  //

  //Painting
  private void showBuffers (Graphics2D g)
  {
    int width  = getCurrentCellWidth();
    int height = getCurrentCellHeight();
    int x      = size.width / ( width + hSpacing ) + 1;
    int y      = size.height / ( height + vSpacing ) + 1;
    for (int i = 1; i < x; i++)
    {
      g.drawLine(( i * width + ( i - 1 ) * hSpacing ), 0, ( i * width + ( i - 1 ) * hSpacing ), size.height);
      g.drawLine(( i * width + i * hSpacing ), 0, ( i * width + i * hSpacing ), size.height);
    }
    for (int i = 1; i < y; i++)
    {
      g.drawLine(0, ( i * height + ( i - 1 ) * vSpacing ), size.width, ( i * height + ( i - 1 ) * vSpacing ));
      g.drawLine(0, ( i * height + i * vSpacing ), size.width, ( i * height + i * vSpacing ));
    }
  }
  //
}

//TODO make this a separate class
class Cell
{
  public int[]     pos;
  public Vector2d  absPos;
  public UIObject  content;
  public Dimension size;
  public boolean   selected = false;

  public Cell (int[] pos, UIObject content)
  {
    this.pos = pos;
    this.content = content;
  }
}
