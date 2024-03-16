package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class GherkinDocumentParserTest extends Common {
    @Test
    public void build() throws Exception {
        val parameters = new ParametersDto();
        parameters.setProjectSource(featureFile);

        File tmpFile = File.createTempFile("result-", ".html");
        log.info("Created: {}", tmpFile);

        try (
                FileOutputStream fos = new FileOutputStream(tmpFile, true);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(bw);
        ){
            new GherkinDocumentParser(parameters, featureFile).build(out);
        }
    }
}
