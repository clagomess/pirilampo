package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureToHTMLCompilerTest {
    @Test
    public void build() throws Exception {
        File sourceFile = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource("feature/xxx.Feature").getFile());

        File targeFile = new File("target/FeatureToHTMLCompilerTest");
        if(!targeFile.isDirectory()) assertTrue(targeFile.mkdir());

        ParametroDto parametro = new ParametroDto();
        parametro.setTxtSrcFonte(sourceFile);
        parametro.setTxtOutputTarget(targeFile);

        new FeatureToHTMLCompiler(parametro).build();

        assertTrue(sourceFile.isFile());

        File htmlFile = new File(targeFile, "xxx.html");
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
}
