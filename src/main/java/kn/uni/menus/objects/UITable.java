package kn.uni.menus.objects;

import kn.uni.menus.interfaces.Displayed;
import kn.uni.menus.interfaces.Updating;
import kn.uni.util.Vector2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class UITable extends UIObject implements Displayed, Updating {
    public int hSpacing;
    public int vSpacing;
    public Dimension dim;
    private List<List<kn.uni.menus.objects.UIObject>> cells;
    public Color backgroundColor;
    public Color borderColor;
    public int borderWidth;
    public Dimension size;
    public boolean showGrid;

    public UITable(Vector2d pos, Dimension size, int paintLayer, Dimension dim) {
        hSpacing = 5;
        vSpacing = 5;
        this.dim = dim;
        cells = new ArrayList<>(dim.height);
        addColumn(1);


        this.position = pos;
        this.size = size;
        this.paintLayer = paintLayer;


    }

    @Override
    public void paintComponent(Graphics2D g) {
        position.use(g::translate);
        g.setStroke(new BasicStroke(borderWidth));
        g.setColor(backgroundColor);
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(borderColor);
        g.drawRect(0, 0, size.width, size.height);
        position.invert().use(g::translate);

        cells.forEach(row -> row.stream()
                .filter(o -> o.visible)
                .filter(cell -> cell instanceof Displayed)
                .map(cell -> (Displayed) cell)
                .forEach(cell -> cell.paintComponent(g)));
    }

    @Override
    public int paintLayer() {
        return paintLayer;
    }

    @Override
    public void update() {
        cells.forEach(row -> row.stream()
                        .filter(o -> o.visible)
                        .filter(cell -> cell instanceof Updating)
                        .map(cell -> (Updating) cell)
                        .forEach(Updating::update));

    }

    public void addRow(int rows)
    {
        IntStream.range(0, rows).forEach(i -> cells.add(new ArrayList<>(dim.width)));

    }

    public void addColumn(int columns)
    {
        IntStream.range(0, columns).forEach(i -> cells.get(i).add(null));
    }
}
