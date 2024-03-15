package com.github.clagomess.pirilampo.gui.component;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
public class ColorChooserComponentTest {
    @ParameterizedTest
    @CsvSource(value = {
            ",#010203",
            " ,#010203",
            "#FFFFFF,#FFFFFF",
    }, ignoreLeadingAndTrailingWhitespace = false)
    public void setValue(String color, String expected){
        val cc = new ColorChooserComponent("TEST", "#010203");
        cc.setValue(color);

        assertEquals(expected, cc.getValue());
        assertEquals(expected, cc.text.getText());
        assertEquals(expected, cc.colorToHexString(cc.swatch.getBackground()));
    }

    @Test
    public void setValue_Wrong(){
        val cc = new ColorChooserComponent("TEST", "#010203");
        cc.setValue("#FFFFFT");

        assertEquals("#FFFFFT", cc.getValue());
        assertEquals("#FFFFFT", cc.text.getText());
        assertEquals("#EEEEEE", cc.colorToHexString(cc.swatch.getBackground()));
    }


    @Test
    public void construct(){
        val cc = new ColorChooserComponent("TEST", "#010203");

        assertEquals("#010203", cc.getValue());
        assertEquals("#010203", cc.text.getText());
        assertEquals("#010203", cc.colorToHexString(cc.swatch.getBackground()));
    }

    @Test
    public void colorToHexString(){
        val cc = new ColorChooserComponent("TEST", "#010203");
        assertEquals("#FFFFFF", cc.colorToHexString(Color.WHITE));
    }

    @Test
    public void setText(){
        val cc = new ColorChooserComponent("TEST", "#010203");
        cc.text.setText("#FFFFFF");

        assertEquals("#FFFFFF", cc.getValue());
        assertEquals("#FFFFFF", StringUtils.stripToNull(cc.text.getText()));
        assertEquals("#FFFFFF", cc.colorToHexString(cc.swatch.getBackground()));
    }

    @Test
    public void setText_Wrong(){
        val cc = new ColorChooserComponent("TEST", "#010203");
        cc.text.setText("#FFFFFT");

        assertEquals("#FFFFFT", cc.getValue());
        assertEquals("#FFFFFT", StringUtils.stripToNull(cc.text.getText()));
        assertEquals("#EEEEEE", cc.colorToHexString(cc.swatch.getBackground()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void setText_NullAndEmpty(String color){
        val cc = new ColorChooserComponent("TEST", "#010203");
        cc.text.setText(color);

        assertNull(StringUtils.stripToNull(cc.getValue()));
        assertNull(StringUtils.stripToNull(cc.text.getText()));
        assertEquals("#EEEEEE", cc.colorToHexString(cc.swatch.getBackground()));
    }
}
