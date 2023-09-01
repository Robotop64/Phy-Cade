package kn.uni.ui.Swing.components;

import kn.uni.Gui;
import kn.uni.ui.Swing.interfaces.Action;
import kn.uni.ui.Swing.interfaces.Colored;
import kn.uni.ui.Swing.menus.SettingMenu;
import kn.uni.util.fileRelated.Config.Config;

import javax.swing.Box;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.List;

import static javax.swing.SwingConstants.HORIZONTAL;

public class SettingEditor extends JPanel implements Colored
{
  double             columnRatio;
  double             bufferRatio;
  GridBagConstraints gbc;

  Config.Leaf    leaf;
  Config.Setting setting;

  /**
   * Creates a component used to edit a single setting
   *
   * @param columnRatio - the ratio of the label column (-bufferRatio/2) to the whole component
   * @param bufferRatio - the ratio buffer between the label and the editor
   * @param leaf        - the leaf containing the setting
   */
  public SettingEditor (double columnRatio, double bufferRatio, Config.Leaf leaf)
  {
    super();
    setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    setBackground(null);

    this.columnRatio = columnRatio;
    this.bufferRatio = bufferRatio;
    this.leaf = leaf;
    this.setting = leaf.getType();
    addComponents();
  }

  private void addComponents ()
  {
    //region label
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = columnRatio - bufferRatio / 2; // First column width ratio
    gbc.weighty = 1;

    PacButton label = new PacButton(Config.getInstance().optionNames.get(setting.id));
    label.setMinimumSize(new Dimension((int) ( getWidth() * columnRatio ), 50));
    label.setPreferredSize(new Dimension((int) ( getWidth() * columnRatio ), 50));
    label.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
    label.setBuffer(3);
    label.setAlignmentX(LEFT_ALIGNMENT);
    label.setHorizontalAlignment(PacLabel.LEFT);
    label.setOpaque(false);

    //TODO: add description popup on click
    //label.addAction(()->{});
    add(label, gbc);
    //endregion

    //buffer
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = bufferRatio; // Gap width ratio
    add(Box.createHorizontalStrut(1), gbc);

    addEditor();
  }

  private void addEditor ()
  {
    Component editComp = null;

    switch (Config.Setting.SubClass.valueOf(setting.asSubClass().getSimpleName()))
    {
      case Range ->
      {
        Config.Range range = setting.toRange();
        PacSlider    value = new PacSlider(HORIZONTAL, (int) range.min, (int) range.max, (int) range.current, (int) range.stepSize);

        editComp = value;

        value.addAction(() ->
        {
          range.current = value.getValue();
        });

        value.addChangeListener(e -> value.run());
      }

      case Switch ->
      {
        Config.Switch bool  = setting.toSwitch();
        PacToggle     value = new PacToggle();
        value.setSelected(bool.current);

        editComp = value;

        value.addAction(() ->
        {
          bool.current = value.isSelected();
        });

        value.addActionListener(e -> value.run());
      }

      case Digit ->
      {
        Config.Digit digit = setting.toDigit();
        PacSpinner   value = new PacSpinner();
        value.setValue(digit.current);

        editComp = value;

        value.addAction(() ->
        {
          digit.current = (int) value.getValue();
        });

        value.addChangeListener(e -> value.run());
      }

      case Value ->
      {
        Config.Value         value  = setting.toValue();
        PacComboBox <String> editor = new PacComboBox <>(value.possible);
        editor.setSelectedItem(value.current);

        editComp = editor;

        editor.setAction(() ->
        {
          value.current = (String) editor.getSelectedItem();
        });

        editor.addActionListener(e -> editor.run());
      }

      case Table ->
      {
        Config.Table table = setting.toTable();
      }

      case Matrix ->
      {

      }
    }

    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weightx = ( 1 - columnRatio - bufferRatio / 2 );
    gbc.weighty = 1;
    assert editComp != null;

    List <String> tags = Arrays.asList(leaf.tags);
    if (!tags.contains("editable"))
      editComp.setEnabled(false);
    if (!tags.contains("visible"))
      editComp.setVisible(false);
    if (tags.contains("debug") && !Config.getInstance().getSetting("Debugging/Enabled").toSwitch().current)
      editComp.setEnabled(false);

    if (editComp instanceof Action action)
    {
      //save on change
      action.addAction(Config::save);

      //reset debug settings on disable of debug mode
      if (setting.equals(Config.getInstance().getSetting("Debugging/Enabled")))
      {
        action.addAction(() ->
        {
          if (!setting.toSwitch().current)
            Config.getInstance().optionLeaves.stream()
                                             .filter(l -> l.hasTag("debug"))
                                             .map(Config.Leaf::getType)
                                             .forEach(s ->
                                             {
                                               switch (Config.Setting.SubClass.valueOf(s.asSubClass().getSimpleName()))
                                               {
                                                 case Switch -> s.toSwitch().current = s.toSwitch().defaultVal;
                                                 case Digit -> s.toDigit().current = s.toDigit().defaultVal;
                                                 case Value -> s.toValue().current = s.toValue().defaultVal;
                                                 case Range -> s.toRange().current = s.toRange().defaultVal;
                                               }
                                             });
        });
      }

      action.addAction(() ->
      {
        SettingMenu.getInstance(Gui.getInstance().content).fillEditor();
        SettingMenu.getInstance(Gui.getInstance().content).scroll.revalidate();
      });

    }

    editComp.setMinimumSize(new Dimension((int) ( getWidth() * ( 1 - columnRatio - bufferRatio / 2 ) ), 50));
    editComp.setPreferredSize(new Dimension((int) ( getWidth() * ( 1 - columnRatio - bufferRatio / 2 ) ), 50));
    editComp.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));

    add(editComp, gbc);
  }
}
