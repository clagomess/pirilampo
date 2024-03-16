package com.github.clagomess.pirilampo.core.parsers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ImageParserTest extends Common {
    private final ImageParser imageParser = new ImageParser();

    @ParameterizedTest
    @CsvSource(value = {
            "true$smallest.png$data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABAQAAAAA3bvkkAAAACklEQVR4AWNgAAAAAgABc3UBGAAAAABJRU5ErkJggg==",
            "false$smallest.png$../feature/smallest.png",
            "true$xx_a.png$xx_a.png",
            "false$xx_a.png$xx_a.png",
            "true$https://picsum.photos/800/200$https://picsum.photos/800/200",
            "false$https://picsum.photos/800/200$https://picsum.photos/800/200",
    }, delimiter = '$')
    public void parse(Boolean embedded, String filename, String expected){
        val parameters = new ParametersDto();
        parameters.setEmbedImages(embedded);
        parameters.setProjectSource(featureFolder);

        StringWriter sw = new StringWriter();

        try (PrintWriter out = new PrintWriter(sw)){
            imageParser.parse(out, parameters, featureFile, filename);

            assertEquals(expected, sw.toString());
        }
    }
}
