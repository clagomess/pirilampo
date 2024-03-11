package com.github.clagomess.pirilampo.gui.component;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;

public class ColorChooserComponent extends JPanel {
    private final JPanel color = new JPanel();
    private final JTextField text = new JTextField();
    private final JButton button = new JButton("Select");

    @Getter
    private String value;

    public ColorChooserComponent(String label, String defaultColor){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow,fill]"));

        value = StringUtils.isNotBlank(text.getText()) ? text.getText() : defaultColor;
        text.setText(value);
        color.setBackground(Color.decode(value));

        button.addActionListener(l -> {
            JColorChooser pane = new JColorChooser(Color.decode(value));

            AbstractColorChooserPanel[] defaultPanels = pane.getChooserPanels();
            pane.removeChooserPanel(defaultPanels[4]);
            pane.removeChooserPanel(defaultPanels[2]);
            pane.removeChooserPanel(defaultPanels[1]);

            JColorChooser.createDialog(null, label, true, pane, a -> {
                value = "#" + String.format("%06X", 0xFFFFFF & pane.getColor().getRGB());
                text.setText(value);
                color.setBackground(pane.getColor());
            },null).setVisible(true);
        });

        add(color);
        add(text, "width 100%");
        add(button);
    }
}
