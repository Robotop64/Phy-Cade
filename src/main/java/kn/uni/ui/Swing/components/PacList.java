package kn.uni.ui.Swing.components;

import com.formdev.flatlaf.extras.components.FlatList;
import kn.uni.util.Vector2d;

import javax.swing.*;
import java.awt.*;

public class PacList extends JPanel {

    public int alignment = SwingConstants.HORIZONTAL;
    public FlatList list;
    public JScrollPane listContainer;

    public PacList(Vector2d position, Dimension size) {
        super();
        setBounds((int) position.x, (int) position.y, size.width, size.height);
        setLayout(null);
        setBackground(Color.BLACK);

        list = new FlatList();
        list.setBounds(0, 0, getWidth(), getHeight());

        add(listContainer = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public void setListData(Object[] data) {
        list.setModel(new DefaultListModel() {
            {
                for (Object o : data) {
                    addElement(o);
                }
            }
        });
    }

    public void addListData(Object[] data) {

    }
}
