package kn.uni.ui.Swing.components;

import kn.uni.ui.InputListener;
import kn.uni.ui.Swing.Style;
import kn.uni.ui.UIScreen;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.awt.Dimension;

public class PacConfirm extends UIScreen
{
  public String[] text;
  private PacList buttons;
  public Runnable confirmAction;
  public Runnable cancelAction;

  public PacConfirm (JPanel parent, Vector2d position, Dimension size, String[] text)
  {
    super(parent);
    this.setBounds((int) position.x, (int) position.y, size.width, size.height);
    useColorSet(Style.normal);
    this.setLayout(null);
    this.text = text;
  }

  public void initComponents ()
  {
    int innerBuffer = 75;
    Dimension innerBounds = new Dimension(this.getWidth()-2*innerBuffer, this.getHeight()-2*innerBuffer);

    int buttonHeight = 100;
    int textHeight = innerBounds.height - buttonHeight-25;

    PacLabel text = new PacLabel(new Vector2d().cartesian(innerBuffer, innerBuffer), new Dimension(innerBounds.width, textHeight), "");
    String[] lines = this.text[0].split("\n");
    text.setText("<html><center>" + lines[0] + "<br>" + "<br>"+ lines[1] + "</center></html>");
    text.setFontSize(45);
    text.setHorizontalAlignment(SwingConstants.CENTER);
    text.setVerticalAlignment(SwingConstants.CENTER);
    add(text);

    buttons = new PacList(new Vector2d().cartesian(innerBuffer, innerBuffer+textHeight+25), new Dimension(innerBounds.width, buttonHeight));
    buttons.hBuffer = 50;
    buttons.alignment = PacList.Alignment.HORIZONTAL;
    buttons.selectionRollAround = false;

    PacButton confirm = new PacButton(new Vector2d(), new Dimension(buttons.getWidth()/2-buttons.hBuffer/2, buttons.getHeight()), this.text[1]);
    confirm.addAction(confirmAction);
    buttons.addObject(confirm);

    PacButton cancel = new PacButton(new Vector2d(), new Dimension(buttons.getWidth()/2-buttons.hBuffer/2, buttons.getHeight()), this.text[2]);
    cancel.addAction(cancelAction);
    buttons.addObject(cancel);

    buttons.unifyFontSize(30f);

    buttons.selectItem(1);

    add(buttons);

    bindPlayer(InputListener.Player.playerOne,input -> {
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
        default -> {}
      }
    });
  }

  public void useColorSet (Style.ColorSet colorSet)
  {
    this.setBorder(new LineBorder(colorSet.border(), 3));
    this.setBackground(colorSet.background());
    this.setForeground(colorSet.foreground());
  }
}
