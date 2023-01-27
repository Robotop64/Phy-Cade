package kn.uni.menus;

import kn.uni.Gui;
import kn.uni.menus.objects.UILabel;
import kn.uni.menus.objects.UITable;
import kn.uni.ui.InputListener;
import kn.uni.util.Fira;
import kn.uni.util.Vector2d;

import javax.swing.*;
import java.awt.*;

public class SettingsMenu extends Menu
{
    UITable selected;

    public SettingsMenu (JPanel parent)
    {
        super(parent);

        createUIComponents();

        bindPlayer(InputListener.Player.playerOne, input ->
        {

        });
    }

    private void createUIComponents()
    {
        //Header
        UILabel header = new UILabel(
                new Vector2d().cartesian(15,15),
                new Dimension(Gui.frameWidth - 30 * 2, 50),
                "EINSTELLUNGEN:",
                0);
        header.setAlignment(UILabel.Alignment.MIDDLE_CENTER);
        header.setFont(Fira.getInstance().getUnderlined(54));
        header.borderColor = null;
        header.textColor = Color.CYAN.darker().darker();
        elements.add(header);
        //

        //Main Sections
        UITable mainSections = new UITable(new Vector2d().cartesian(15, 80), new Dimension(Gui.frameWidth - 30 * 2, 50 * 4 + 3 * 9), 0, new Dimension(4, 1));
        mainSections.showCellBounds = true;
        elements.add(mainSections);
    }
}
