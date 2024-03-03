package br.com.pirilampo.core.compilers;

import br.com.pirilampo.core.dto.ParametroDto;
import br.com.pirilampo.core.enums.ArtefatoEnum;
import br.com.pirilampo.core.enums.CompilacaoEnum;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

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

    @ParameterizedTest
    @CsvSource({
            "PASTA,HTML,target/feature,,target/html/index.html",
            "PASTA,HTML,target/feature,target,target/html/index.html",
            "FEATURE,HTML,target/feature/xxx.Feature,,target/feature/xxx.html",
            "FEATURE,HTML,target/feature/xxx.Feature,target,target/xxx.html",
            "PASTA,PDF,target/feature,,target/html/index.pdf",
            "PASTA,PDF,target/feature,target,target/html/index.pdf",
            "FEATURE,PDF,target/feature/xxx.Feature,,target/feature/xxx.pdf",
            "FEATURE,PDF,target/feature/xxx.Feature,target,target/xxx.pdf",
    })
    public void getOutArtifact(
            CompilacaoEnum tipCompilacao,
            ArtefatoEnum artefato,
            String source,
            String target,
            String expected
    ) throws IOException {
        File targetDir = new File("target/feature");
        if(!targetDir.exists()) assertTrue(targetDir.mkdir());
        File targetFile = new File(targetDir, "xxx.Feature");
        if(!targetFile.exists()) FileUtils.writeStringToFile(targetFile, "");

        // init test
        ParametroDto parametroDto = new ParametroDto();
        parametroDto.setTipCompilacao(tipCompilacao);
        parametroDto.setArtefato(artefato);
        parametroDto.setTxtSrcFonte(StringUtils.isNotBlank(source) ? new File(source) : null);
        parametroDto.setTxtOutputTarget(StringUtils.isNotBlank(target) ? new File(target) : null);

        File result = compiler.getOutArtifact(parametroDto);
        log.info("{}", result.getAbsolutePath());
        assertEquals(new File(expected).getAbsolutePath(), result.getAbsolutePath());
    }

    @ParameterizedTest
    @CsvSource({
            "target/foo/AbC.feature,.feature",
            "target/foo/AbC.FEATURE,.FEATURE",
            "target/foo/AbC.Feature,.Feature",
            "target/foo/AbC.txt,",
            "target/foo/AbC.feature.jpg,",
    })
    public void getFeatureExtension(String source, String expected){
        assertEquals(expected, compiler.getFeatureExtension(new File(source)));
    }

    @ParameterizedTest
    @CsvSource({
            "target/foo,target/foo/AbC.feature,AbC.feature",
            "target/foo,target/foo/bar/AbC.feature,bar/AbC.feature",
            "target,target/foo/bar/AbC.feature,foo/bar/AbC.feature",
            "C:\\Users\\features,C:\\Users\\features\\AbC.feature,AbC.feature",
    })
    public void getFeaturePathWithoutAbsolute(File base, File feature, String expected){
        assertEquals(expected, compiler.getFeaturePathWithoutAbsolute(base, feature));
    }

    @Test
    public void getAbsolutePathFeatureAsset(){
        File sourceFile = new File(Thread.currentThread()
                .getContextClassLoader()
                .getResource("feature").getFile());

        File sourceMasterFile = new File(Thread.currentThread()
                        .getContextClassLoader()
                        .getResource("master").getFile());

        File featureFile = new File(sourceFile, "xxx.Feature");

        ParametroDto parametro = new ParametroDto();
        parametro.setTxtSrcFonte(sourceFile);
        parametro.setTxtSrcFonteMaster(sourceMasterFile);

        assertNotNull(compiler.getAbsolutePathFeatureAsset(
                parametro,
                featureFile,
                "xxx.png"
        ));
    }
}
