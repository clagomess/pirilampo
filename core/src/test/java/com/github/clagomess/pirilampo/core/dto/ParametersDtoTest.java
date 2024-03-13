package com.github.clagomess.pirilampo.core.dto;

import com.github.clagomess.pirilampo.core.Common;
import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import com.github.clagomess.pirilampo.core.exception.ParametersException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@Slf4j
public class ParametersDtoTest extends Common {
    @Test
    public void validate_projectName(){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setProjectName(null);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @Test
    public void validate_projectVersion(){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setProjectVersion(null);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @Test
    public void validate_projectLogo(){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setProjectLogo(featureFolder);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @Test
    public void validate_layoutPdf(){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setLayoutPdf(null);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @Test
    public void validate_htmlPanelToggle(){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setHtmlPanelToggle(null);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"#AT"})
    public void validate_menuColor(String color){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setMenuColor(color);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"#AT"})
    public void validate_menuTextColor(String color){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setMenuTextColor(color);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @Test
    public void validate_compilationType(){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setCompilationType(null);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @Test
    public void validate_compilationArtifact(){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setCompilationArtifact(null);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @ParameterizedTest
    @CsvSource(value = {
       "FOLDER,",
       "FOLDER,../feature/xxx.Feature",
       "FOLDER_DIFF,../feature/xxx.Feature",
       "FEATURE,../feature",
    })
    public void validate_projectSource(CompilationTypeEnum compilationTypeEnum, String source){
        val projectSource = source != null ? new File(Objects.requireNonNull(getClass()
                .getResource(source)).getFile()) : null;

        val dto = new ParametersDto();
        dto.setProjectSource(projectSource);
        dto.setCompilationType(compilationTypeEnum);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "FOLDER,../feature",
            "FOLDER_DIFF,../feature/xxx.Feature",
            "FOLDER_DIFF,",
    })
    public void validate_projectMasterSource(CompilationTypeEnum compilationTypeEnum, String sourceMaster){
        val projectMasterSource = sourceMaster != null ? new File(Objects.requireNonNull(getClass()
                .getResource(sourceMaster)).getFile()) : null;

        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setProjectMasterSource(projectMasterSource);
        dto.setCompilationType(compilationTypeEnum);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @Test
    public void validate_projectMasterSource_equals(){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setProjectMasterSource(new File(featureFolder.getAbsolutePath()));
        dto.setCompilationType(CompilationTypeEnum.FOLDER_DIFF);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }

    @Test
    public void validate_projectTarget(){
        val dto = new ParametersDto();
        dto.setProjectSource(featureFolder);
        dto.setProjectTarget(featureFile);

        assertThrowsExactly(ParametersException.class, dto::validate);
    }
}
