package com.github.clagomess.pirilampo.gui.ui;

import com.github.clagomess.pirilampo.core.compilers.*;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.gui.form.MainForm;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.HTML;
import static com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum.PDF;
import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.*;

@Slf4j
public class MainUI extends JFrame {
    private final MainForm form = new MainForm();

    public MainUI() {
        setTitle("Pirilampo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 400));
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

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void compile(){
        new Thread(() -> {
            try {
                form.btnCompile.setEnabled(false);

                ParametersDto parameters = form.toDto();
                parameters.validate();

                ArtifactCompiler compiler = null;

                if (parameters.getCompilationType() == FEATURE &&
                        parameters.getCompilationArtifact() == HTML
                ) {
                    compiler = new FeatureToHTMLCompiler(parameters);
                }

                if (parameters.getCompilationType() == FEATURE &&
                        parameters.getCompilationArtifact() == PDF
                ) {
                    compiler = new FeatureToPDFCompiler(parameters);
                }

                if (Arrays.asList(FOLDER, FOLDER_DIFF).contains(parameters.getCompilationType()) &&
                        parameters.getCompilationArtifact() == HTML
                ) {
                    compiler = new FolderToHTMLCompiler(parameters);
                }

                if (parameters.getCompilationType() == FOLDER &&
                        parameters.getCompilationArtifact() == PDF
                ) {
                    compiler = new FolderToPDFCompiler(parameters);
                }

                if (compiler == null) throw new RuntimeException("Failed to start compuler");
                compiler.setProgress((value -> form.progress.setValue((int) Math.round(value * 100f))));
                compiler.build();
                // @TODO: impl. msg ok
            }catch (Throwable e){
                log.error(log.getName(), e); //@TODO: to UI
            } finally {
                form.btnCompile.setEnabled(true);
            }
        }).start();
    }
}
