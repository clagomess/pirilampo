package com.github.clagomess.pirilampo.gui.component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.util.prefs.Preferences;

@Slf4j
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
        button = new JButton(label);

        text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                value = StringUtils.isNotBlank(text.getText()) ? new File(text.getText()) : null;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                value = StringUtils.isNotBlank(text.getText()) ? new File(text.getText()) : null;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                value = StringUtils.isNotBlank(text.getText()) ? new File(text.getText()) : null;
            }
        });

        Preferences prefs = Preferences.userRoot().node(getClass().getName());

        button.addActionListener(l -> {
            JFileChooser fc = new JFileChooser(prefs.get("LAST_USED_FOLDER", ""));
            config.apply(fc);

            int ret = fc.showOpenDialog(null);
            if(ret == JFileChooser.APPROVE_OPTION && fc.getSelectedFile() != null){
                text.setText(fc.getSelectedFile().getAbsolutePath());
                value = fc.getSelectedFile();

                if(fc.getSelectedFile().isDirectory()){
                    prefs.put("LAST_USED_FOLDER", fc.getSelectedFile().getAbsolutePath());
                }else{
                    prefs.put("LAST_USED_FOLDER", fc.getSelectedFile().getParentFile().getAbsolutePath());
                }
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
        this.text.setEditable(enabled);
        this.button.setEnabled(enabled);
    }

    @FunctionalInterface
    public interface FileChooserFI {
        void apply(JFileChooser fc);
    }
}