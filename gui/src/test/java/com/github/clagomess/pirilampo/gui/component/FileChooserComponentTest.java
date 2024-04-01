package com.github.clagomess.pirilampo.gui.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
public class FileChooserComponentTest {

    // @TODO: needs mock SwingUtilities.invokeLater, to non thread run

    @Test
    public void reset(){
        AtomicReference<Integer> countTriggerChange = new AtomicReference<>(0);

        FileChooserComponent fc = new FileChooserComponent();
        fc.addOnChange(file -> countTriggerChange.getAndSet(countTriggerChange.get() + 1));
        fc.reset();

        assertNull(fc.getValue());
        assertNull(StringUtils.stripToNull(fc.text.getText()));
        assertEquals(1, countTriggerChange.get());
    }

    @ParameterizedTest
    @CsvSource(value = {
            ",false,0",
            " ,true,1",
            "foo/bar,true,1",
    }, ignoreLeadingAndTrailingWhitespace = false)
    public void setValue(File file, Boolean expected, Integer expectedChanges){
        log.info("{} - {}", file, expected);

        AtomicReference<Integer> countTriggerChange = new AtomicReference<>(0);

        FileChooserComponent fc = new FileChooserComponent();
        fc.addOnChange(f -> countTriggerChange.getAndSet(countTriggerChange.get() + 1));
        fc.setValue(file);

        assertEquals(expected, fc.getValue() != null);
        assertEquals(expected, StringUtils.stripToNull(fc.text.getText()) != null);
        assertEquals(expectedChanges, countTriggerChange.get());
    }
}
