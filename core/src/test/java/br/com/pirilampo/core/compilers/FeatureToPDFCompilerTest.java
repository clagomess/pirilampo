package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FeatureToPDFCompilerTest {
    @Test //(timeout = 8000) @TODO: check
    public void build() throws Exception {
        File sourceFile = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource("feature/xxx.Feature").getFile());

        File targetFile = new File("target/FeatureToPDFCompilerTest");
        if(!targetFile.isDirectory()) assertTrue(targetFile.mkdir());

        ParametroDto parametro = new ParametroDto();
        parametro.setTxtNome("_AA_");
        parametro.setTxtVersao("_BB_");
        parametro.setTxtSrcFonte(sourceFile);
        parametro.setTxtOutputTarget(targetFile);

        new FeatureToPDFCompiler(parametro).build();
        File pdfFile = new File(targetFile, "xxx.pdf");
        assertTrue(pdfFile.isFile());

        try(PDDocument pdfDocument = PDDocument.load(pdfFile)){
            String pdfAsStr = new PDFTextStripper().getText(pdfDocument);

            assertTrue(pdfAsStr.contains(parametro.getTxtNome()));
            assertTrue(pdfAsStr.contains(parametro.getTxtVersao()));

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
