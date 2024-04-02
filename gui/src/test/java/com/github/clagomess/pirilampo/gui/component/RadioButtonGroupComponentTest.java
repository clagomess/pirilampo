package com.github.clagomess.pirilampo.gui.component;

import com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static com.github.clagomess.pirilampo.core.enums.CompilationTypeEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class RadioButtonGroupComponentTest {
    @ParameterizedTest
    @CsvSource(value = {
            ",FOLDER_DIFF",
            "FEATURE,FEATURE",
            "FOLDER,FOLDER",
            "FOLDER_DIFF,FOLDER_DIFF",
    })
    public void setSelected(CompilationTypeEnum selected, CompilationTypeEnum expected){
        val rb = new RadioButtonGroupComponent<>(Arrays.asList(
                new RadioButtonGroupComponent.RadioButton<>(FOLDER),
                new RadioButtonGroupComponent.RadioButton<>(FOLDER_DIFF, true),
                new RadioButtonGroupComponent.RadioButton<>(FEATURE)
        ));

        rb.setSelected(selected);

        assertEquals(expected, rb.getSelectedValue());
    }
}
