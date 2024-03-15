package com.github.clagomess.pirilampo.core.compilers;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.dto.ParametersDto;
import com.github.clagomess.pirilampo.core.enums.CompilationArtifactEnum;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class PropertiesCompilerTest extends Common {
    private final PropertiesCompiler propertiesCompiler = new PropertiesCompiler();
    private final File source = new File("target/PropertiesCompilerTest");

    @BeforeEach
    public void setup(){
        if(!source.isDirectory()){
            assertTrue(source.mkdir());
        }else{
            Arrays.stream(source.listFiles()).forEach(File::delete);
        }
    }

    @Test
    public void getSourceDir_source_file(){
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(featureFile);
        parameters.setCompilationType(CompilationTypeEnum.FEATURE);
        parameters.setCompilationArtifact(CompilationArtifactEnum.HTML);

        assertTrue(propertiesCompiler.getSourceDir(parameters).isDirectory());
    }

    @Test
    public void getSourceDir_source_dir(){
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(featureFolder);
        parameters.setCompilationType(CompilationTypeEnum.FOLDER);
        parameters.setCompilationArtifact(CompilationArtifactEnum.HTML);

        assertTrue(propertiesCompiler.getSourceDir(parameters).isDirectory());
    }

    @Test
    public void getSourceDir_null(){
        ParametersDto parameters = new ParametersDto();
        assertNull(propertiesCompiler.getSourceDir(parameters));
    }

    @Test
    public void loadData(){
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(new File(getClass()
                .getResource("PropertiesCompilerTest")
                .getFile())
        );

        propertiesCompiler.loadData(parameters);
        assertEquals("foo-bar", parameters.getProjectName());
        assertTrue(parameters.isEmbedImages());
    }

    @Test
    public void loadData_null(){
        ParametersDto parameters = new ParametersDto();
        propertiesCompiler.loadData(parameters);
        assertNull(parameters.getProjectSource());
    }

    @Test
    public void setData() throws IOException {
        ParametersDto parameters = new ParametersDto();
        parameters.setProjectSource(source);

        propertiesCompiler.setData(parameters);

        File result = new File(source, PropertiesCompiler.FILENAME);
        assertTrue(result.isFile());

        Assertions.assertThat(FileUtils.readFileToString(result, StandardCharsets.UTF_8))
                .contains("projectName")
                .contains("projectVersion")
                .contains("projectLogo")
                .contains("layoutPdf")
                .contains("htmlPanelToggle")
                .contains("menuColor")
                .contains("menuTextColor")
                .contains("embedImages")
                .contains("compilationType")
                .contains("compilationArtifact")
                .doesNotContain("projectSource")
                .doesNotContain("projectMasterSource")
                .doesNotContain("projectTarget");
    }
}
