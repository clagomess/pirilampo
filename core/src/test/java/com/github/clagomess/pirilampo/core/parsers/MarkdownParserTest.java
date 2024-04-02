package com.github.clagomess.pirilampo.core.parsers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class MarkdownParserTest {
    @Test
    public void build(){
        assertEquals(
                "a: <a href=\"xx_a.html\">xx_a</a>",
                MarkdownParser.getInstance().build("a: [xx_a](xx_a.html)")
        );
    }
}
