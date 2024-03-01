package br.com.pirilampo.core.compilers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FolderToPDFCompilerTest {
    @Test //(timeout = 8000) @TODO: check
    public void build(){
/*
        try {
            parametro.setTxtSrcFonte(new File(resourcePath + File.separator + "feature"));
            parametro.setTxtOutputTarget(new File(criarPasta().getAbsolutePath()));
            new FolderToHTMLCompiler(parametro).build();
            String pdf = parametro.getTxtOutputTarget() + File.separator + "index.pdf";
            assertTrue((new File(pdf)).isFile());

            PDDocument pdfDocument = PDDocument.load(new File(pdf));
            String pdfAsStr = new PDFTextStripper().getText(pdfDocument);

            assertTrue(pdfAsStr.contains(projectName));
            assertTrue(pdfAsStr.contains(projectVersion));

            pdfDocument.close();
        }catch (Exception e){
            log.error(log.getName(), e);
            fail();
        } */
    }
}
