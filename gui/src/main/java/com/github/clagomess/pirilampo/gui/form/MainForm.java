package com.github.clagomess.pirilampo.gui.form;

import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import com.github.clagomess.pirilampo.core.enums.HtmlPanelToggleEnum;
import com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum;
import com.github.clagomess.pirilampo.gui.component.ColorChooserComponent;
import com.github.clagomess.pirilampo.gui.component.FileChooserComponent;
import com.github.clagomess.pirilampo.gui.component.RadioButtonGroupComponent;
import com.github.clagomess.pirilampo.gui.util.AppenderUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.Arrays;

import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.HTML;
import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.PDF;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.*;
import static com.github.clagomess.pirilampo.core.enums.HtmlPanelToggleEnum.CLOSED;
import static com.github.clagomess.pirilampo.core.enums.HtmlPanelToggleEnum.OPENED;
import static com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum.LANDSCAPE;
import static com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum.PORTRAIT;

public class MainForm {
    private final ParametersDto defaultDto = new ParametersDto();

    // source
    public final RadioButtonGroupComponent<CompilationTypeEnum> rbCompilationType = new RadioButtonGroupComponent<>(Arrays.asList(
            new RadioButtonGroupComponent.RadioButton<>("Folder", FOLDER, true),
            new RadioButtonGroupComponent.RadioButton<>("Diff", FOLDER_DIFF),
            new RadioButtonGroupComponent.RadioButton<>("Feature", FEATURE)
    ));

    public final FileChooserComponent fcProjectSource = new FileChooserComponent() {{
        rbCompilationType.addOnChange(value -> fcProjectSource.reset());

        setConfig(fc -> {
            if(rbCompilationType.getSelectedValue() == FEATURE){
                fc.setAcceptAllFileFilterUsed(false);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.addChoosableFileFilter(new FileNameExtensionFilter(
                        "Features: *.feature",
                        "feature"
                ));
            }else{
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }
        });
    }};

    public final FileChooserComponent fcProjectMasterSource = new FileChooserComponent("Select Master") {{
        setEnabled(false);
        setConfig(fc -> fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY));

        rbCompilationType.addOnChange(value -> {
            fcProjectMasterSource.reset();
            fcProjectMasterSource.setEnabled(value == FOLDER_DIFF);
        });
    }};

    // project
    public final JTextField txtProjectName = new JTextField(defaultDto.getProjectName());
    public final JTextField txtProjectVersion = new JTextField(defaultDto.getProjectVersion());
    public final FileChooserComponent fcProjectLogo = new FileChooserComponent() {{
        setConfig(fc -> {
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.addChoosableFileFilter(new FileNameExtensionFilter(
                    "Images",
                    "jpg", "jpeg", "png"
            ));
        });
    }};

    public JTabbedPane pTabArtifact = new JTabbedPane(){{
        setBorder(BorderFactory.createTitledBorder("Artifact"));
    }};

    // HTML
    public final RadioButtonGroupComponent<HtmlPanelToggleEnum> rbHtmlPanelToggle = new RadioButtonGroupComponent<>(Arrays.asList(
        new RadioButtonGroupComponent.RadioButton<>("Opened", OPENED, true),
        new RadioButtonGroupComponent.RadioButton<>("Closed", CLOSED)
    ));

    public final JCheckBox chkEmbedImages = new JCheckBox(){{setSelected(true);}};
    public final ColorChooserComponent ccMenuColor = new ColorChooserComponent(
            "Menu Color",
            defaultDto.getMenuColor()
    );
    public final ColorChooserComponent ccMenuTextColor = new ColorChooserComponent(
            "Menu Text Coloer",
            defaultDto.getMenuTextColor()
    );

    // PDF
    public final RadioButtonGroupComponent<LayoutPdfEnum> rbLayoutPdfEnum = new RadioButtonGroupComponent<>(Arrays.asList(
            new RadioButtonGroupComponent.RadioButton<>(PORTRAIT, true),
            new RadioButtonGroupComponent.RadioButton<>(LANDSCAPE)
    ));

    // progress
    public JProgressBar progress = new JProgressBar(){{setStringPainted(true);}};
    public JTextArea console = new JTextArea(){{
        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setFont(new Font(
                "Consolas",
                this.getFont().getStyle(),
                this.getFont().getSize()
        ));

        AppenderUtil.setOnChange(text -> {
            this.setText(text);
            this.setCaretPosition(this.getDocument().getLength());
        });
    }};

    public final JButton btnCompile = new JButton("Compile HTML!") {{
        pTabArtifact.addChangeListener(l -> {
            CompilationArtifactEnum artifact = pTabArtifact.getSelectedIndex() == 0 ? HTML : PDF;
            this.setText(String.format("Compile %s!", artifact));
        });
    }};

    public ParametersDto toDto(){
        ParametersDto dto = new ParametersDto();
        dto.setProjectName(txtProjectName.getText());
        dto.setProjectVersion(txtProjectVersion.getText());
        dto.setProjectLogo(fcProjectLogo.getValue());
        dto.setLayoutPdf(rbLayoutPdfEnum.getSelectedValue());
        dto.setHtmlPanelToggle(rbHtmlPanelToggle.getSelectedValue());
        dto.setMenuColor(ccMenuColor.getValue());
        dto.setMenuTextColor(ccMenuTextColor.getValue());
        dto.setEmbedImages(chkEmbedImages.isSelected());
        dto.setCompilationType(rbCompilationType.getSelectedValue());
        dto.setCompilationArtifact(pTabArtifact.getSelectedIndex() == 0 ? HTML : PDF);
        dto.setProjectSource(fcProjectSource.getValue());
        dto.setProjectMasterSource(fcProjectMasterSource.getValue());

        return dto;
    }
}
