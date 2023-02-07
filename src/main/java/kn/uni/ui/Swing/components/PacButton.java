package kn.uni.ui.Swing.components;

import com.formdev.flatlaf.extras.components.FlatButton;
import kn.uni.util.Fira;
import kn.uni.util.Vector2d;

import java.awt.*;
import java.awt.event.ActionListener;

public class PacButton extends FlatButton {

    Font standardFont = Fira.getInstance().getRegular(20f);

    public PacButton(Vector2d position, Dimension size, String text) {
        super();
        this.setBounds((int) position.x, (int) position.y, size.width, size.height);
        this.setText(text);
        this.setOutline(Color.CYAN.darker());
        this.setBackground(Color.BLACK);
        this.setForeground(Color.CYAN.darker());
        this.setFont(standardFont);
    }


    }