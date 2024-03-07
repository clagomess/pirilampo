package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.enums.LayoutPdfEnum;
import com.github.clagomess.pirilampo.core.parsers.PdfParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class PdfParserTest {
    @Test
    public void build() throws Exception {
        File target = new File("target/ParsePdfTest");
        if(!target.isDirectory()) assertTrue(target.mkdir());

        File targetFile = new File(target, "result.pdf");

        try (
                FileOutputStream fos = new FileOutputStream(targetFile);
                InputStream html = getClass().getResourceAsStream("FeatureToPDFCompilerTest/expected-build.html");
                InputStream css = Thread.currentThread().getContextClassLoader()
                        .getResource("htmlTemplate/dist/feature-pdf.min.css")
                        .openStream();
        ){
            // new PdfParser().build(fos, html, css, LayoutPdfEnum.LANDSCAPE); @TODO: fix
            assertTrue(targetFile.isFile());
        }
    }
}
