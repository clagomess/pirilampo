package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@Slf4j
public class CompilerTest {
    private static final class InheritorCompiler extends Compiler {}
    private final InheritorCompiler compiler = new InheritorCompiler();

    @Test
    public void listFolder() throws Exception {
        val okPath = Thread.currentThread()
                .getContextClassLoader()
                .getResource("feature").getFile();

        val result = compiler.listFolder(new File(okPath));
        Assertions.assertThat(result.size()).isGreaterThanOrEqualTo(0);
    }

    @Test
    public void listFolder_NotDir() {
        assertThrowsExactly(Exception.class, () -> {
            compiler.listFolder(new File(""));
        });
    }

    @ParameterizedTest
    @CsvSource({
            "feature,,feature/xxx.Feature,_xxx,_xxx.html,_xxx.feature,xxx",
            "feature,master,master/xxx.Feature,master_xxx,master_xxx.html,master_xxx.feature,xxx",
    })
    public void getFeatureMetadata(
            String source,
            String sourceMaster,
            String feature,
            String expectedId,
            String expectedIdHtml,
            String expectedIdFeature,
            String expectedIdName
    ){
        File sourceFile = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource(source).getFile());

        File sourceMasterFile = StringUtils.isNotBlank(sourceMaster) ?
                new File(Thread.currentThread()
                        .getContextClassLoader()
                        .getResource(sourceMaster).getFile()) : null;

        File featureFile = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource(feature).getFile());

        ParametroDto parametro = new ParametroDto();
        parametro.setTxtSrcFonte(sourceFile);
        parametro.setTxtSrcFonteMaster(sourceMasterFile);

        val result = compiler.getFeatureMetadata(parametro, featureFile);

        assertEquals(expectedId, result.getId());
        assertEquals(expectedIdHtml, result.getIdHtml());
        assertEquals(expectedIdFeature, result.getIdFeature());
        assertEquals(expectedIdName, result.getName());
    }

    @ParameterizedTest
    @CsvSource({
        "feature/html_embed.html,.html",
        "feature/xxx.png,.png",
    })
    public void writeResourceToOut(String resource, String suffix) throws IOException {
        File tmpFile = File.createTempFile("writeResourceToOut-", suffix);

        File resourceFile = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource(resource).getFile());

        try (
                FileOutputStream fos = new FileOutputStream(tmpFile);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                PrintWriter out = new PrintWriter(bw);
        ){
            compiler.writeResourceToOut(resource, out);

            FileUtils.contentEquals(tmpFile, resourceFile);
        }
    }

    @Test
    public void writeFileToOut()  throws IOException {
        File resourceFile = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource("feature/html_embed.html").getFile());

        try(
                StringWriter sw = new StringWriter();
                PrintWriter out = new PrintWriter(sw);
        ){
            compiler.writeFileToOut(resourceFile, out);
            assertEquals("<strong>html_embed_txt</strong>", sw.toString());
        }
    }
}
