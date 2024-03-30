package com.github.clagomess.pirilampo.gui.component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

@Slf4j
public class FileChooserComponent extends JPanel {
    private final List<OnChangeFI> onChangeList = new LinkedList<>();
    final JTextField text = new JTextField();
    private final JButton button;

    @Setter
    private FileChooserFI config = (fc) -> {};

    @Getter
    private File value = null;

    public void setValue(File vl){
        text.setText(vl != null ? vl.getAbsolutePath() : null);
        value = vl;
    }

    public FileChooserComponent(){
        this("Select");
    }

    public FileChooserComponent(String label){
        setLayout(new MigLayout("insets 0 0 0 0", "[grow,fill]"));
        button = new JButton(label);

        text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            public void update(){
                value = StringUtils.isNotBlank(text.getText()) ? new File(text.getText()) : null;
                onChangeList.forEach(ch -> ch.change(value));
            }
        });

        Preferences prefs = Preferences.userRoot().node(getClass().getName());

        button.addActionListener(l -> {
            JFileChooser fc = new JFileChooser(prefs.get("LAST_USED_FOLDER", ""));
            config.apply(fc);

            int ret = fc.showOpenDialog(null);
            if(ret == JFileChooser.APPROVE_OPTION && fc.getSelectedFile() != null){
                setValue(fc.getSelectedFile());

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

    public void addOnChange(OnChangeFI value){
        onChangeList.add(value);
    }

    public void reset(){
        this.value = null;
        onChangeList.forEach(ch -> ch.change(null));
        this.text.setText(null);
    }

    public void setEnabled(boolean enabled){
        reset();
        this.text.setEditable(enabled);
        this.button.setEnabled(enabled);
    }

    @FunctionalInterface
    public interface FileChooserFI {
        void apply(JFileChooser fc);
    }

    @FunctionalInterface
    public interface OnChangeFI {
        void change(File file);
    }
}
