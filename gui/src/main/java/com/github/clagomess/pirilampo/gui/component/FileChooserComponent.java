package com.github.clagomess.pirilampo.gui.component;

import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.File;

public class FileChooserComponent extends JPanel {
    private final JTextField text = new JTextField();
    private final JButton button;

    @Setter
    private FileChooserFI config = (fc) -> {};

    @Getter
    private File value = null;

    public FileChooserComponent(){
        this("Select");
    }

    public FileChooserComponent(String label){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow,fill]"));
        text.setEditable(false);
        button = new JButton(label);

        button.addActionListener(l -> {
            JFileChooser fc = new JFileChooser();
            config.apply(fc);

            int ret = fc.showOpenDialog(null);
            if(ret == JFileChooser.APPROVE_OPTION && fc.getSelectedFile() != null){
                text.setText(fc.getSelectedFile().getAbsolutePath());
                value = fc.getSelectedFile();
            }
        });


        add(text, "width 100%");
        add(button);
    }

    public void reset(){
        this.value = null;
        this.text.setText(null);
    }

    public void setEnabled(boolean enabled){
        this.button.setEnabled(enabled);
    }

    @FunctionalInterface
    public interface FileChooserFI {
        void apply(JFileChooser fc);
    }
}
