package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.Common;
import br.com.pirilampo.core.dto.ParametroDto;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureToHTMLCompilerTest extends Common {
    @Test
    public void build() throws Exception {
        File targetFile = new File("target/FeatureToHTMLCompilerTest");
        if(!targetFile.isDirectory()) assertTrue(targetFile.mkdir());

        ParametroDto parametro = new ParametroDto();
        parametro.setTxtSrcFonte(featureFile);
        parametro.setTxtOutputTarget(targetFile);

        new FeatureToHTMLCompiler(parametro).build();
        File htmlFile = new File(targetFile, "xxx.html");
        assertTrue(htmlFile.isFile());

        String html = FileUtils.readFileToString(htmlFile);

        assertNotEquals(html, "");
        assertFalse(html.contains("xxx.png"));
        assertTrue(html.contains("https://pt.wikipedia.org/static/images/project-logos/ptwiki.png"));
        assertTrue(html.contains("width=\"50\""));
        assertFalse(html.contains("&lt;strike&gt;"));
        assertTrue(html.contains("<strike>"));
        assertFalse(html.contains("&lt;br&gt;"));
        assertTrue(html.contains("<br/>"));

        assertTrue(FileUtils.contentEquals(
                new File(getClass().getResource("FeatureToHTMLCompilerTest/expected-build.html").getFile()),
                htmlFile
        ));
    }

    // @TODO: impl unit for test remove buffer on error
}
