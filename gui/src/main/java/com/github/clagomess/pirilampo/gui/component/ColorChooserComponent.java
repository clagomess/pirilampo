package com.github.clagomess.pirilampo.gui.component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

@Slf4j
public class ColorChooserComponent extends JPanel {
    private final String defaultColor;
    final JPanel swatch = new JPanel();
    final JTextField text = new JTextField();
    private final JButton button = new JButton("Select");

    @Getter
    private String value;

    public void setValue(String vl){
        value = StringUtils.isNotBlank(vl) ? vl : defaultColor;
        text.setText(value);
    }

    public ColorChooserComponent(String label, String defaultColor){
        this.defaultColor = defaultColor;

        setLayout(new MigLayout("insets 0 0 0 0", "[grow,fill]"));

        text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update(text.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update(text.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update(text.getText());
            }

            public void update(String color){
                value = color;

                try {
                    swatch.setBackground(Color.decode(color));
                }catch (Throwable e){
                    log.warn(log.getName(), e);
                    swatch.setBackground(null);
                }
            }
        });

        setValue(defaultColor);

        button.addActionListener(l -> {
            JColorChooser pane = new JColorChooser(Color.decode(value));

            AbstractColorChooserPanel[] defaultPanels = pane.getChooserPanels();
            pane.removeChooserPanel(defaultPanels[4]);
            pane.removeChooserPanel(defaultPanels[2]);
            pane.removeChooserPanel(defaultPanels[1]);

            JColorChooser.createDialog(null, label, true, pane, a -> {
                setValue(colorToHexString(pane.getColor()));
            },null).setVisible(true);
        });

        add(swatch);
        add(text, "width 100%");
        add(button);
    }

    protected String colorToHexString(Color color){
        if(color == null) return null;
        return "#" + String.format("%06X", 0xFFFFFF & color.getRGB());
    }
}
