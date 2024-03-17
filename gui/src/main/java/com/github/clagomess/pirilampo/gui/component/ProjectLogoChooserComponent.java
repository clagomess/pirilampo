package com.github.clagomess.pirilampo.gui.component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;

@Slf4j
public class ProjectLogoChooserComponent extends JPanel {
    final JTextField text = new JTextField();
    private final JButton button = new JButton("Choose");

    @Setter
    private ProjectSourceFI projectSource = () -> null;

    @Getter
    private String value = null;

    public void setValue(String value){
        this.value = StringUtils.stripToNull(value);
        text.setText(this.value);
    }

    public ProjectLogoChooserComponent() {
        setLayout(new MigLayout("insets 0 0 0 0", "[grow,fill]"));

        button.addActionListener(l -> choose());

        add(text, "width 100%");
        add(button);
    }

    protected void choose(){
        File source = projectSource.get();
        if(source == null || !source.isDirectory()){
            JOptionPane.showMessageDialog(
                    null,
                    "<Project Source> is required",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        log.info("impl"); //@TODO: impl: "jpg", "jpeg", "png"
    }

    public void setEnabled(boolean enabled){
        setValue(null);
        this.text.setEditable(enabled);
        this.button.setEnabled(enabled);
    }

    @FunctionalInterface
    public interface ProjectSourceFI {
        File get();
    }
}
