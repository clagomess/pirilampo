package com.github.clagomess.pirilampo.gui.form;

import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import com.github.clagomess.pirilampo.core.enums.HtmlPanelToggleEnum;
import com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum;
import com.github.clagomess.pirilampo.gui.component.ColorChooserComponent;
import com.github.clagomess.pirilampo.gui.component.FileChooserComponent;
import com.github.clagomess.pirilampo.gui.component.RadioButtonGroupComponent;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Arrays;

public class MainForm {
    private final ParametersDto defaultDto = new ParametersDto();

    // source
    public final RadioButtonGroupComponent<CompilationTypeEnum> rbCompilationType = new RadioButtonGroupComponent<>(Arrays.asList(
            new RadioButtonGroupComponent.RadioButton<>("Folder", CompilationTypeEnum.FOLDER, true),
            new RadioButtonGroupComponent.RadioButton<>("Diff", CompilationTypeEnum.FOLDER_DIFF),
            new RadioButtonGroupComponent.RadioButton<>("Feature", CompilationTypeEnum.FEATURE)
    ));

    public final FileChooserComponent fcProjectSource = new FileChooserComponent() {{
        rbCompilationType.addOnChange(value -> fcProjectSource.reset());

        setConfig(fc -> {
            if(rbCompilationType.getSelectedValue() == CompilationTypeEnum.FEATURE){
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
            fcProjectMasterSource.setEnabled(value == CompilationTypeEnum.FOLDER_DIFF);
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

    // HTML
    public final RadioButtonGroupComponent<HtmlPanelToggleEnum> rbHtmlPanelToggle = new RadioButtonGroupComponent<>(Arrays.asList(
        new RadioButtonGroupComponent.RadioButton<>("Opened", HtmlPanelToggleEnum.OPENED, true),
        new RadioButtonGroupComponent.RadioButton<>("Closed", HtmlPanelToggleEnum.CLOSED)
    ));

    public final JCheckBox chkEmbedImages = new JCheckBox();
    public final ColorChooserComponent ccMenuColor = new ColorChooserComponent();
    public final ColorChooserComponent ccMenuTextColor = new ColorChooserComponent();

    // PDF
    public final RadioButtonGroupComponent<LayoutPdfEnum> rbLayoutPdfEnum = new RadioButtonGroupComponent<>(Arrays.asList(
            new RadioButtonGroupComponent.RadioButton<>(LayoutPdfEnum.PORTRAIT, true),
            new RadioButtonGroupComponent.RadioButton<>(LayoutPdfEnum.LANDSCAPE)
    ));
}
