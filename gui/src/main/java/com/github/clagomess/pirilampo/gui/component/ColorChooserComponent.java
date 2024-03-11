package com.github.clagomess.pirilampo.gui.component;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ColorChooserComponent extends JPanel {
    private final JTextField text = new JTextField();
    private final JButton button = new JButton("Select");

    public ColorChooserComponent(){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow,fill]"));

        button.addActionListener(l -> {
            JColorChooser.showDialog(null, "aa", Color.WHITE);
        });

        add(text, "width 100%");
        add(button);
    }
}
