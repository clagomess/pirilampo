package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import com.github.clagomess.pirilampo.core.exception.FeatureException;
import lombok.val;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class FolderToPDFCompilerTest extends Common {
    private final File targetFile = new File("target/FolderToPDFCompilerTest");

    @BeforeEach
    public void setup(){
        if(!targetFile.isDirectory()){
            assertTrue(targetFile.mkdir());
        }else{
            Arrays.stream(targetFile.listFiles()).forEach(File::delete);
        }
    }

    @Test
    @Timeout(8)
    public void build() throws Exception {
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

    @Test
    public void checkDeletedBuffersOnError() {
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(featureErrorFolder);
        parameters.setProjectTarget(targetFile);
        parameters.setCompilationType(CompilationTypeEnum.FOLDER);
        parameters.setCompilationArtifact(CompilationArtifactEnum.PDF);

        val compiler = new FolderToPDFCompiler(parameters);
        assertThrowsExactly(FeatureException.class, compiler::build);
        assertFalse(new File(targetFile, "html/index.pdf").exists());
        Assertions.assertThat(compiler.getTempFiles()).noneMatch(File::exists);
    }
}
