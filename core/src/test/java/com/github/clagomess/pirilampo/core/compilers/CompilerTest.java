package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class CompilerTest extends Common {
    private final Compiler compiler = new Compiler(){};

    @Test
    public void listFolder() throws Exception {
        val result = compiler.listFolder(featureFolder);
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
            "../feature,,../feature/xxx.Feature,_xxx,_xxx.html,_xxx.feature,xxx",
            "../feature,../master,../master/xxx.Feature,_xxx,_xxx.html,_xxx.feature,xxx",
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
        File sourceFile = new File(Objects.requireNonNull(getClass()
                .getResource(source)).getFile());

        File sourceMasterFile = StringUtils.isNotBlank(sourceMaster) ?
                new File(Objects.requireNonNull(getClass()
                        .getResource(sourceMaster)).getFile()) : null;

        File featureFile = new File(Objects.requireNonNull(getClass()
                .getResource(feature)).getFile());

        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(sourceFile);
        parameters.setProjectMasterSource(sourceMasterFile);

        val result = compiler.getFeatureMetadata(parameters, featureFile);

        assertEquals(expectedId, result.getId());
        assertEquals(expectedIdHtml, result.getIdHtml());
        assertEquals(expectedIdFeature, result.getIdFeature());
        assertEquals(expectedIdName, result.getName());
    }

    @ParameterizedTest
    @CsvSource({
        "../feature/html_embed.html,.html",
        "../feature/xxx.png,.png",
    })
    public void writeResourceToOut(String resource, String suffix) throws IOException {
        File tmpFile = File.createTempFile("writeResourceToOut-", suffix);

        File resourceFile = new File(Objects.requireNonNull(getClass()
                .getResource(resource)).getFile());

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
        File resourceFile = new File(Objects.requireNonNull(getClass()
                .getResource("../feature/html_embed.html")).getFile());

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
            "FOLDER,HTML,target/feature,,target/html/index.html",
            "FOLDER,HTML,target/feature,target,target/html/index.html",
            "FEATURE,HTML,target/feature/xxx.Feature,,target/feature/xxx.html",
            "FEATURE,HTML,target/feature/xxx.Feature,target,target/xxx.html",
            "FOLDER,PDF,target/feature,,target/html/index.pdf",
            "FOLDER,PDF,target/feature,target,target/html/index.pdf",
            "FEATURE,PDF,target/feature/xxx.Feature,,target/feature/xxx.pdf",
            "FEATURE,PDF,target/feature/xxx.Feature,target,target/xxx.pdf",
    })
    public void getOutArtifact(
            CompilationTypeEnum tipCompilacao,
            CompilationArtifactEnum artefato,
            String source,
            String target,
            String expected
    ) throws IOException {
        File targetDir = new File("target/CompilerTest/feature");
        if(!targetDir.exists()) assertTrue(targetDir.mkdirs());
        File targetFile = new File(targetDir, "xxx.Feature");
        if(!targetFile.exists()) FileUtils.writeStringToFile(targetFile, "");

        // init test
        ParametersDto parametersDto = new ParametersDto();
        parametersDto.setCompilationType(tipCompilacao);
        parametersDto.setCompilationArtifact(artefato);
        parametersDto.setProjectSource(StringUtils.isNotBlank(source) ? new File(source) : null);
        parametersDto.setProjectTarget(StringUtils.isNotBlank(target) ? new File(target) : null);

        File result = compiler.getOutArtifact(parametersDto);
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
            "/Users/features,/Users/features/AbC.feature,AbC.feature",
    })
    public void getFeaturePathWithoutAbsolute(File base, File feature, String expected){
        assertEquals(expected, compiler.getFeaturePathWithoutAbsolute(base, feature));
    }

    @Test
    public void getAbsolutePathFeatureAsset(){
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(featureFolder);
        parameters.setProjectMasterSource(featureMasterFolder);

        assertNotNull(compiler.getAbsolutePathFeatureAsset(
                parameters,
                featureFile,
                "xxx.png"
        ));
    }
}
