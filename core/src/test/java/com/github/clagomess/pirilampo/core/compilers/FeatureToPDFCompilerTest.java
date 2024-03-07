package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.File;
import java.io.IOException;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeatureToPDFCompilerTest extends Common {
    @Test
    @Timeout(8)
    public void build() throws Exception {
        File targetFile = new File("target/FeatureToPDFCompilerTest");
        if(!targetFile.isDirectory()) assertTrue(targetFile.mkdir());

        ParametersDto parameters = new ParametersDto();
        parameters.setProjectName("_AA_");
        parameters.setProjectVersion("_BB_");
        parameters.setProjectSource(featureFile);
        parameters.setProjectTarget(targetFile);
        parameters.setCompilationType(CompilationTypeEnum.FEATURE);
        parameters.setCompilationArtifact(CompilationArtifactEnum.PDF);

        new FeatureToPDFCompiler(parameters).build();
        File pdfFile = new File(targetFile, "xxx.pdf");
        assertTrue(pdfFile.isFile());

        try(PDDocument pdfDocument = Loader.loadPDF(pdfFile)){
            String pdfAsStr = new PDFTextStripper().getText(pdfDocument);

            assertTrue(pdfAsStr.contains(parameters.getProjectName()));
            assertTrue(pdfAsStr.contains(parameters.getProjectVersion()));

            boolean possuiImagens = StreamSupport.stream(
                pdfDocument.getPage(0)
                        .getResources()
                        .getXObjectNames()
                        .spliterator()
            ,false).anyMatch(cosName -> {
                try {
                    PDXObject xobject = pdfDocument.getPage(0).getResources().getXObject(cosName);
                    return (xobject instanceof PDImageXObject);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            assertTrue(possuiImagens);
        }

        // @TODO: also, validate html
    }

    // @TODO: impl unit for test remove buffer on error
}
