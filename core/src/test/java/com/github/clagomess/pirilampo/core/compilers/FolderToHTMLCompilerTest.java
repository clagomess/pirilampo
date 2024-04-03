package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.exception.FeatureException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class FolderToHTMLCompilerTest extends Common {
    private final File targetFile = new File("target/FolderToHTMLCompilerTest");

    @BeforeEach
    public void setup(){
        if(!targetFile.isDirectory()){
            assertTrue(targetFile.mkdir());
        }else{
            Arrays.stream(targetFile.listFiles()).forEach(File::delete);
        }
    }

    @Test
    public void build() throws Exception {
        ParametersDto parameters = new ParametersDto();
        parameters.setMenuColor("#666");
        parameters.setProjectLogo("logo_xxx.png");
        parameters.setProjectSource(featureFolder);
        parameters.setProjectTarget(targetFile);

        new FolderToHTMLCompiler(parameters).build();

        File htmlFile = new File(targetFile,"html/index.html");
        assertTrue(htmlFile.isFile());

        Assertions.assertThat(FileUtils.readFileToString(htmlFile))
                .doesNotContain((new File(String.valueOf(parameters.getProjectLogo()))).getName())
                .doesNotContain("logo_xxx.png")
                .contains(parameters.getMenuColor())
                .contains("#/html/html_embed.html")
                .contains("html_embed_txt")
                ;

        Assertions.assertThat(htmlFile)
                .hasSameTextualContentAs(decompressResource(
                        targetFile,
                        "FolderToHTMLCompilerTest/expected-build.html.gz"
                ));
    }

    @Test
    public void build_master() throws Exception {
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(featureFolder);
        parameters.setProjectMasterSource(featureMasterFolder);
        parameters.setProjectTarget(targetFile);
        new FolderToHTMLCompiler(parameters).build();

        File htmlFile = new File(targetFile,"html/index.html");
        assertTrue(htmlFile.isFile());

        Assertions.assertThat(FileUtils.readFileToString(htmlFile, StandardCharsets.UTF_8))
                        .contains("YYY_MASTER_YYY");

        Assertions.assertThat(htmlFile)
                .hasSameTextualContentAs(decompressResource(
                        targetFile,
                        "FolderToHTMLCompilerTest/expected-build-master.html.gz"
                ));
    }

    @Test
    public void buildMenu() throws IOException {
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(featureFolder);
        parameters.setProjectMasterSource(featureMasterFolder);
        parameters.setProjectTarget(targetFile);

        val compiler = new FolderToHTMLCompiler(parameters);

        StringWriter sw = new StringWriter();
        try (PrintWriter out = new PrintWriter(sw)){
            compiler.buildMenu(out);
            log.info("{}", sw);
        }

        Assertions.assertThat(sw.toString())
                .contains("let menuIdx = 0;")
                .contains("createMenuItem");
    }

    @Test
    public void checkIsSavedProperties() throws Exception {
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(featureFolder);
        parameters.setProjectTarget(targetFile);

        new FolderToHTMLCompiler(parameters).build();

        assertTrue(new File(
                parameters.getProjectSource(),
                PropertiesCompiler.FILENAME
        ).isFile());
    }

    @Test
    public void checkDeletedBuffersOnError() {
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(featureErrorFolder);
        parameters.setProjectTarget(targetFile);

        assertThrowsExactly(FeatureException.class, () -> new FolderToHTMLCompiler(parameters).build());

        assertFalse(new File(targetFile,"html/index.html").exists());
    }
}
