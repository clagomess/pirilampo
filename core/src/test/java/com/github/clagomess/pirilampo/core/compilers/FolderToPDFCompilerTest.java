package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FolderToPDFCompilerTest extends Common {
    @Test
    @Timeout(8)
    public void build() throws Exception {
        File targetFile = new File("target/FolderToPDFCompilerTest");
        if(!targetFile.isDirectory()) assertTrue(targetFile.mkdir());

        ParametersDto parameters = new ParametersDto();
        parameters.setProjectName("_AA_");
        parameters.setProjectVersion("_BB_");
        parameters.setProjectSource(featureFolder);
        parameters.setProjectTarget(targetFile);
        parameters.setCompilationType(CompilationTypeEnum.FOLDER);
        parameters.setCompilationArtifact(CompilationArtifactEnum.PDF);

        new FolderToPDFCompiler(parameters).build();
        File pdf = new File(targetFile, "html/index.pdf");
        assertTrue(pdf.isFile());

        PDDocument pdfDocument = Loader.loadPDF(pdf);
        String pdfAsStr = new PDFTextStripper().getText(pdfDocument);

        assertTrue(pdfAsStr.contains(parameters.getProjectName()));
        assertTrue(pdfAsStr.contains(parameters.getProjectVersion()));

        pdfDocument.close();
    }

    // @TODO: validate content html
    // @TODO: impl unit for test remove buffer on error
}
