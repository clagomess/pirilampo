package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FolderToPDFCompilerTest {
    @Test //(timeout = 8000) @TODO: check
    public void build() throws Exception {
        File sourceFile = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource("feature").getFile());

        File targetFile = new File("target/FolderToPDFCompilerTest");
        if(!targetFile.isDirectory()) assertTrue(targetFile.mkdir());

        ParametroDto parametro = new ParametroDto();
        parametro.setTxtNome("_AA_");
        parametro.setTxtVersao("_BB_");
        parametro.setTxtSrcFonte(sourceFile);
        parametro.setTxtOutputTarget(targetFile);

        new FolderToPDFCompiler(parametro).build();
        File pdf = new File(targetFile, "html/index.pdf");
        assertTrue(pdf.isFile());

        PDDocument pdfDocument = PDDocument.load(pdf);
        String pdfAsStr = new PDFTextStripper().getText(pdfDocument);

        assertTrue(pdfAsStr.contains(parametro.getTxtNome()));
        assertTrue(pdfAsStr.contains(parametro.getTxtVersao()));

        pdfDocument.close();
    }

    // @TODO: validate content html
    // @TODO: impl unit for test remove buffer on error
}
