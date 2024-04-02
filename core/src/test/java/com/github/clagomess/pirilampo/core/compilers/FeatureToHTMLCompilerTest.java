package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import com.github.clagomess.pirilampo.core.exception.FeatureException;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureToHTMLCompilerTest extends Common {
    private final File targetFile = new File("target/FeatureToHTMLCompilerTest");

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
        parameters.setProjectSource(featureFile);
        parameters.setProjectTarget(targetFile);
        parameters.setCompilationType(CompilationTypeEnum.FEATURE);
        parameters.setCompilationArtifact(CompilationArtifactEnum.HTML);

        new FeatureToHTMLCompiler(parameters).build();
        File htmlFile = new File(targetFile, "xxx.html");
        assertTrue(htmlFile.isFile());

        Assertions.assertThat(FileUtils.readFileToString(htmlFile, StandardCharsets.UTF_8))
                .doesNotContain("xxx.png")
                .doesNotContain("&lt;strike&gt;")
                .doesNotContain("&lt;br&gt;")
                .contains("https://pt.wikipedia.org/static/images/project-logos/ptwiki.png")
                .contains("width=\"50\"")
                .contains("<strike>")
                .contains("<br/>")
                ;

        Assertions.assertThat(htmlFile)
                .hasSameTextualContentAs(new File(getClass()
                        .getResource("FeatureToHTMLCompilerTest/expected-build.html")
                        .getFile()
                ));
    }

    @Test
    public void checkIsSavedProperties() throws Exception {
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(featureFile);
        parameters.setProjectTarget(targetFile);
        parameters.setCompilationType(CompilationTypeEnum.FEATURE);
        parameters.setCompilationArtifact(CompilationArtifactEnum.HTML);

        new FeatureToHTMLCompiler(parameters).build();

        assertTrue(new File(
                parameters.getProjectSource().getParentFile(),
                PropertiesCompiler.FILENAME
        ).isFile());
    }

    @Test
    public void checkDeletedBuffersOnError() {
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(featureErrorFile);
        parameters.setProjectTarget(targetFile);
        parameters.setCompilationType(CompilationTypeEnum.FEATURE);
        parameters.setCompilationArtifact(CompilationArtifactEnum.HTML);

        assertThrowsExactly(FeatureException.class, () -> new FeatureToHTMLCompiler(parameters).build());
        assertFalse(new File(targetFile, "yyy.html").exists());
    }
}
