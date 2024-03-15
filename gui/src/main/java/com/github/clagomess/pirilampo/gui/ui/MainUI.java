package com.github.clagomess.pirilampo.gui.ui;

import com.github.clagomess.pirilampo.core.compilers.*;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.exception.FeatureException;
import com.github.clagomess.pirilampo.gui.component.IconComponent;
import com.github.clagomess.pirilampo.gui.form.MainForm;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.HTML;
import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.PDF;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.*;

@Slf4j
public class MainUI extends JFrame {
    private final MainForm form = new MainForm();
    private final PropertiesCompiler propertiesCompiler = new PropertiesCompiler();

    public MainUI() {
        setTitle("Pirilampo");
        setIconImage(IconComponent.FAVICON.getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(550, 400));
        setResizable(false);
        setLayout(new MigLayout("insets 10 10 10 10", "[grow,fill]", ""));

        JPanel pSource = new JPanel(new MigLayout("", "[grow,fill]"));
        pSource.setBorder(BorderFactory.createTitledBorder("Source"));
        pSource.add(form.rbCompilationType, "wrap");
        pSource.add(form.fcProjectSource, "wrap");
        pSource.add(form.fcProjectMasterSource, "wrap");
        add(pSource, "wrap");

        JPanel pProject = new JPanel(new MigLayout("", "[grow,fill]"));
        pProject.setBorder(BorderFactory.createTitledBorder("Project"));
        pProject.add(new JLabel("Name"));
        pProject.add(new JLabel("Version"), "wrap");
        pProject.add(form.txtProjectName);
        pProject.add(form.txtProjectVersion, "wrap");
        pProject.add(new JLabel("Logo"), "wrap");
        pProject.add(form.fcProjectLogo, "span 2, wrap");
        add(pProject, "wrap");

        JPanel pHtml = new JPanel(new MigLayout("", "[grow,fill]"));
        pHtml.add(new JLabel("Panel Toggle"));
        pHtml.add(new JLabel("Embed Images?"), "wrap");
        pHtml.add(form.rbHtmlPanelToggle);
        pHtml.add(form.chkEmbedImages, "wrap");
        pHtml.add(new JLabel("Menu Color"));
        pHtml.add(new JLabel("Menu Text Color"), "wrap");
        pHtml.add(form.ccMenuColor);
        pHtml.add(form.ccMenuTextColor, "wrap");

        JPanel pPdf = new JPanel(new MigLayout("", "[grow,fill]"));
        pPdf.add(new JLabel("PDF Layout"), "wrap");
        pPdf.add(form.rbLayoutPdfEnum, "wrap");

        form.pTabArtifact.addTab("HTML", pHtml);
        form.pTabArtifact.addTab("PDF", pPdf);
        add(form.pTabArtifact, "wrap");

        JPanel pProgress = new JPanel(new MigLayout("", "[grow,fill]"));
        pProgress.setBorder(BorderFactory.createTitledBorder("Progress"));
        pProgress.add(form.progress, "wrap");
        pProgress.add(new JScrollPane(
                form.console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        ), "height 50, wrap");
        add(pProgress, "wrap");

        form.btnCompile.addActionListener(l -> compile());
        add(form.btnCompile);
        getRootPane().setDefaultButton(form.btnCompile);

        // add events
        form.fcProjectSource.setOnChange(this::projectSourceOnChange);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void projectSourceOnChange(File file){
        ParametersDto parameters = form.toDto();
        propertiesCompiler.loadData(parameters);

        form.txtProjectName.setText(parameters.getProjectName());
        form.txtProjectVersion.setText(parameters.getProjectVersion());
        form.fcProjectLogo.setValue(parameters.getProjectLogo());
        form.rbLayoutPdfEnum.setSelected(parameters.getLayoutPdf());
        form.rbHtmlPanelToggle.setSelected(parameters.getHtmlPanelToggle());
        form.ccMenuColor.setValue(parameters.getMenuColor());
        form.ccMenuTextColor.setValue(parameters.getMenuTextColor());
        form.chkEmbedImages.setSelected(parameters.isEmbedImages());
        form.rbCompilationType.setSelected(parameters.getCompilationType());
        form.pTabArtifact.setSelectedIndex(parameters.getCompilationArtifact() == HTML ? 0 : 1);
    }

    private ArtifactCompiler getArtifactCompiler(ParametersDto parameters){
        if (parameters.getCompilationType() == FEATURE &&
                parameters.getCompilationArtifact() == HTML
        ) {
            return new FeatureToHTMLCompiler(parameters);
        }

        if (parameters.getCompilationType() == FEATURE &&
                parameters.getCompilationArtifact() == PDF
        ) {
            return new FeatureToPDFCompiler(parameters);
        }

        if (Arrays.asList(FOLDER, FOLDER_DIFF).contains(parameters.getCompilationType()) &&
                parameters.getCompilationArtifact() == HTML
        ) {
            return new FolderToHTMLCompiler(parameters);
        }

        if (parameters.getCompilationType() == FOLDER &&
                parameters.getCompilationArtifact() == PDF
        ) {
            return new FolderToPDFCompiler(parameters);
        }

        throw new RuntimeException("Failed to start compuler");
    }

    public void compile(){
        new Thread(() -> {
            try {
                form.btnCompile.setEnabled(false);

                ParametersDto parameters = form.toDto();
                parameters.validate();

                ArtifactCompiler compiler = getArtifactCompiler(parameters);
                compiler.setProgress((value -> form.progress.setValue((int) Math.round(value * 100f))));
                compiler.build();

                JOptionPane.showMessageDialog(this, "Compilation done");
            }catch (FeatureException e){
                log.error(log.getName(), e);
                JOptionPane.showMessageDialog(
                        this,
                        String.format("%s:\n\n%s", e.getFeature().getAbsolutePath(), e.getMessage()),
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE
                );
            }catch (Throwable e){
                log.error(log.getName(), e);
                JOptionPane.showMessageDialog(
                        this,
                        e.getMessage(),
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE
                );
            } finally {
                form.btnCompile.setEnabled(true);
            }
        }).start();
    }
}
