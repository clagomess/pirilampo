package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class PdfParserTest {
    @Test
    public void build() throws Exception {
        File target = new File("target/ParsePdfTest");
        if(!target.isDirectory()) assertTrue(target.mkdir());

        File sourceFile = new File(getClass().getResource("PdfParserTest/build.html").getFile());
        File targetFile = new File(target, "result.pdf");

        try (
                FileOutputStream fos = new FileOutputStream(targetFile);
                InputStream html = Files.newInputStream(sourceFile.toPath());
                InputStream css = getClass().getResourceAsStream("PdfParserTest/foo.css")
        ){
            PdfParser pdfParser = new PdfParser(new ParametersDto(), css);
            pdfParser.initDocument(fos);
            pdfParser.addFeatureHTML(sourceFile, html);
            pdfParser.closeDocument();

            assertTrue(targetFile.isFile());
        }
    }
}
