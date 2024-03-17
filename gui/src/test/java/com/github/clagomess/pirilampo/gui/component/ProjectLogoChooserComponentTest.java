package com.github.clagomess.pirilampo.gui.component;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ProjectLogoChooserComponentTest {
    @ParameterizedTest
    @CsvSource(value = {
            ",false",
            " ,false",
            "foo/bar,true",
    }, ignoreLeadingAndTrailingWhitespace = false)
    public void setValue(String file, Boolean expected){
        log.info("{} - {}", file, expected);

        val plc = new ProjectLogoChooserComponent();
        plc.setValue(file);

        assertEquals(expected, plc.getValue() != null);
        assertEquals(expected, StringUtils.stripToNull(plc.text.getText()) != null);
    }
}
