package kn.uni.ui.Swing.components;

import kn.uni.Gui;
import kn.uni.ui.InputListener;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.UIScreen;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class PacConfirm extends UIScreen
{
  public  String[] text;
  private PacList  buttons;
  public  Runnable confirmAction;
  public  Runnable cancelAction;

  public PacConfirm (JPanel parent, Vector2d position, Dimension size, String[] text)
  {
    super(parent);
    this.setBounds((int) position.x, (int) position.y, size.width, size.height);
    useColorSet(Style.normal);
    this.setLayout(null);
    this.text = text;
  }

  public PacConfirm (JPanel parent, String[] text)
  {
    super(parent);
    useColorSet(Style.normal);
    this.setLayout(null);
    this.text = text;
  }

  public void initComponents ()
  {
    RoundedPanel content = new RoundedPanel();
    int          buffer  = 200;
    content.setBounds(buffer, buffer, Gui.frameWidth - 2 * buffer, Gui.frameHeight - 2 * buffer);
    content.setLayout(new BorderLayout());
    content.setArc(30);
    content.setBorderWidth(3);
    content.useColorSet(Style.normal);
    add(content);

    int innerBuffer = 40;
    content.add(Box.createVerticalStrut(innerBuffer), BorderLayout.NORTH);
    content.add(Box.createVerticalStrut(innerBuffer), BorderLayout.SOUTH);
    content.add(Box.createHorizontalStrut(innerBuffer), BorderLayout.WEST);
    content.add(Box.createHorizontalStrut(innerBuffer), BorderLayout.EAST);


    JPanel innerContent = new JPanel();
    innerContent.setLayout(new BoxLayout(innerContent, BoxLayout.Y_AXIS));
    innerContent.setBackground(Color.BLACK);
    content.add(innerContent, BorderLayout.CENTER);


    //region text
    String[] lines = this.text[0].split("\n");
    PacLabel text  = new PacLabel("<html><center>" + lines[0] + "<br>" + "<br>" + lines[1] + "</center></html>");
    text.setFontSize(45);
    text.setBorder(new LineBorder(Style.normal.border(), 3));
    text.setHorizontalAlignment(SwingConstants.CENTER);
    text.setVerticalAlignment(SwingConstants.CENTER);
    text.setMinimumSize(new Dimension(innerContent.getWidth(), innerContent.getHeight()-25-100));
    text.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    innerContent.add(text);
    //endregion

    innerContent.add(Box.createVerticalStrut(25));

    //region buttons
    int    buttonHeight = 100;
    JPanel buttonPanel  = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.setBackground(Color.BLACK);
    buttonPanel.setMinimumSize(new Dimension(innerContent.getWidth(), buttonHeight));
    buttonPanel.setPreferredSize(new Dimension(innerContent.getWidth(), buttonHeight));
    buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, buttonHeight));
    innerContent.add(buttonPanel);

    PacButton confirm = new PacButton(this.text[1]);
    confirm.setMinimumSize(new Dimension(innerContent.getWidth(), buttonHeight));
    confirm.setMaximumSize(new Dimension(Short.MAX_VALUE, buttonHeight));
    confirm.addAction(confirmAction);
    buttonPanel.add(confirm);

    buttonPanel.add(Box.createHorizontalStrut(40));

    PacButton cancel = new PacButton(this.text[2]);
    cancel.setMinimumSize(new Dimension(innerContent.getWidth(), buttonHeight));
    cancel.setMaximumSize(new Dimension(Short.MAX_VALUE, buttonHeight));
    cancel.addAction(cancelAction);
    buttonPanel.add(cancel);
    //endregion
  }

  public void useColorSet (Style.ColorSet colorSet)
  {
    this.setBorder(new LineBorder(colorSet.border(), 3));
    this.setBackground(colorSet.background());
    this.setForeground(colorSet.foreground());
  }

  public void enableControls ()
  {
        bindPlayer(InputListener.Player.playerOne, input ->
        {
          if (input.key() == InputListener.Key.A && input.state() == InputListener.State.down)
          {
            InputListener.getInstance().clearInput();
            buttons.fireSelectedAction();
          }

          Direction dir = input.toDirection();
          if (dir == null) return;
          switch (dir)
          {
            case left -> buttons.selectNext(-1);
            case right -> buttons.selectNext(1);
            default ->
            {
            }
          }
        });
  }

}
