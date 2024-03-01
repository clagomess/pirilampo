package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ParseDocumentTest {
    @Test
    public void foo() throws Exception {
        // @TODO: rewrite
        val parametro = new ParametroDto();
        val file = new File("C:\\Users\\claudio\\DESENV_JAVA\\pirilampo\\core\\src\\test\\resources\\feature\\xxx.Feature");

        parametro.setTxtSrcFonte(file);

        ParseDocument parseDocument = new ParseDocument(
                parametro,
                file
        );

        File tmpFile = File.createTempFile("result-", ".html");
        log.info("Created: {}", tmpFile);

        try (
                FileOutputStream fos = new FileOutputStream(tmpFile, true);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(bw);
        ){
            parseDocument.build(out);
        }
    }
}
